package net.gbs.gbswarehouse.data.fake

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.ConveyorStatusDto
import net.gbs.gbswarehouse.data.remote.dto.response.StartLoadingRequestDto
import net.gbs.gbswarehouse.data.remote.mapper.toDomain
import net.gbs.gbswarehouse.data.remote.stream.ConveyorStreamClient
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.ConveyorStatus
import net.gbs.gbswarehouse.domain.model.LoadingEvent
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.model.StreamStatus
import net.gbs.gbswarehouse.domain.repository.PlcDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Server-backed PLC data source.
 *
 * Live conveyor statuses are pushed through `api/Conveyors/Stream`
 * (SignalR / SSE, auto-detected by [ConveyorStreamClient]) while
 * `api/Conveyors/GetConveyors` provides the initial snapshot and a
 * fallback poll whenever the stream is down.
 */
@Singleton
class PlcDataSourceImpl @Inject constructor(
    private val api: ApiService,
    private val streamClient: ConveyorStreamClient
) : PlcDataSource {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _conveyors = MutableStateFlow<List<Conveyor>>(emptyList())
    override val conveyors: StateFlow<List<Conveyor>> = _conveyors.asStateFlow()

    private val _operations = MutableStateFlow<List<LoadingOperation>>(emptyList())
    override val operations: StateFlow<List<LoadingOperation>> = _operations.asStateFlow()

    private val _events = MutableSharedFlow<LoadingEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<LoadingEvent> = _events.asSharedFlow()

    private val _streamStatus = MutableStateFlow(StreamStatus.OFFLINE)
    override val streamStatus: StateFlow<StreamStatus> = _streamStatus.asStateFlow()

    init {
        refreshConveyors()
        observeStream()
        observeStreamStatus()
        startFallbackPolling()
    }

    private fun observeStreamStatus() {
        scope.launch {
            streamClient.status.collect { status ->
                _streamStatus.value = when (status) {
                    ConveyorStreamClient.Status.LIVE -> StreamStatus.LIVE
                    ConveyorStreamClient.Status.CONNECTING -> StreamStatus.CONNECTING
                    ConveyorStreamClient.Status.DISCONNECTED -> StreamStatus.OFFLINE
                }
            }
        }
    }

    private fun observeStream() {
        scope.launch {
            streamClient.conveyorUpdates().collect { update ->
                applyStreamUpdate(update)
            }
        }
    }

    /** Keeps data fresh while the push stream is reconnecting. */
    private fun startFallbackPolling() {
        scope.launch {
            while (true) {
                delay(FALLBACK_POLL_INTERVAL_MS)
                if (_streamStatus.value != StreamStatus.LIVE) {
                    getConveyors()
                }
            }
        }
    }

    private fun refreshConveyors() {
        scope.launch {
            getConveyors()
        }
    }

    /** Upserts one streamed conveyor and derives operations/events from the change. */
    private fun applyStreamUpdate(update: ConveyorStatusDto) {
        val incoming = update.toDomain()
        val current = _conveyors.value
        val existing = current.firstOrNull {
            it.conveyorId.equals(incoming.conveyorId, ignoreCase = true)
        }
        val updated = if (existing == null) {
            current + incoming
        } else {
            current.map {
                if (it.conveyorId.equals(incoming.conveyorId, ignoreCase = true)) incoming else it
            }
        }
        _conveyors.value = updated
        _operations.value = updated.mapNotNull { it.currentOperation }

        val previous = existing?.currentOperation
        val operation = incoming.currentOperation
        if (operation != null) {
            if (previous != null && operation.loadedQuantity > previous.loadedQuantity) {
                _events.tryEmit(LoadingEvent.BagCounted(operation.operationId, operation.loadedQuantity))
            }
            if (operation.status == ConveyorStatus.COMPLETED && previous?.status != ConveyorStatus.COMPLETED) {
                _events.tryEmit(LoadingEvent.Completed(operation))
            }
            if (operation.status == ConveyorStatus.ERROR && previous?.status != ConveyorStatus.ERROR) {
                _events.tryEmit(LoadingEvent.Failed(operation))
            }
        }
        _events.tryEmit(LoadingEvent.ConveyorStatusChanged(incoming))
    }

    override suspend fun getConveyors(): NetworkResult<List<Conveyor>> {
        val result = safeApiCall { api.getConveyors() }
        return when (result) {
            is NetworkResult.Success -> {
                val list = result.data.data?.map { it.toDomain() } ?: emptyList()
                _conveyors.value = list
                val ops = list.mapNotNull { conv -> conv.currentOperation }
                _operations.value = ops
                NetworkResult.Success(list)
            }
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    private fun findConveyorIdForOperation(operationId: String): String {
        val op = _operations.value.firstOrNull { it.operationId == operationId }
        if (op != null) {
            val conv = _conveyors.value.firstOrNull { it.id == op.conveyorId }
            if (conv != null) return conv.conveyorId
            return "C${op.conveyorId}"
        }
        return "C1"
    }

    override suspend fun startLoading(
        order: LoadingOrder,
        item: OrderItem,
        conveyor: Conveyor,
        qty: Int
    ): NetworkResult<LoadingOperation> {
        val req = StartLoadingRequestDto(
            goodsReceiptLineId = item.id,
            qty = qty
        )
        val result = safeApiCall { api.startConveyor(conveyor.conveyorId, req) }
        refreshConveyors()
        return when (result) {
            is NetworkResult.Success -> {
                val run = result.data.data
                val op = LoadingOperation(
                    operationId = run?.runId ?: "OP-${conveyor.id}",
                    orderId = order.id,
                    orderNumber = order.orderNumber,
                    vehicleNumber = order.vehicleNumber,
                    itemId = item.id,
                    itemName = item.name,
                    conveyorId = conveyor.id,
                    conveyorName = conveyor.name,
                    requiredQuantity = run?.targetQty ?: item.requiredQuantity,
                    loadedQuantity = run?.loadedQty ?: item.loadedQuantity,
                    status = ConveyorStatus.RUNNING
                )
                NetworkResult.Success(op)
            }
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override suspend fun pauseLoading(operationId: String): NetworkResult<LoadingOperation> {
        val conveyorId = findConveyorIdForOperation(operationId)
        val result = safeApiCall { api.pauseConveyor(conveyorId) }
        refreshConveyors()
        val op = getOperation(operationId) ?: LoadingOperation(
            operationId = operationId, orderId = 0, orderNumber = "", vehicleNumber = "",
            itemId = 0, itemName = "", conveyorId = 1, conveyorName = "",
            requiredQuantity = 0, loadedQuantity = 0, status = ConveyorStatus.PAUSED
        )
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(op.copy(status = ConveyorStatus.PAUSED))
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override suspend fun resumeLoading(operationId: String): NetworkResult<LoadingOperation> {
        val conveyorId = findConveyorIdForOperation(operationId)
        val result = safeApiCall { api.resumeConveyor(conveyorId) }
        refreshConveyors()
        val op = getOperation(operationId) ?: LoadingOperation(
            operationId = operationId, orderId = 0, orderNumber = "", vehicleNumber = "",
            itemId = 0, itemName = "", conveyorId = 1, conveyorName = "",
            requiredQuantity = 0, loadedQuantity = 0, status = ConveyorStatus.RUNNING
        )
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(op.copy(status = ConveyorStatus.RUNNING))
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override suspend fun manualStart(conveyorId: Int): NetworkResult<Conveyor> {
        val cId = "C$conveyorId"
        val result = safeApiCall { api.manualStartConveyor(cId) }
        refreshConveyors()
        val conv = _conveyors.value.firstOrNull { it.id == conveyorId } ?: Conveyor(conveyorId = cId, id = conveyorId, name = "Conveyor $conveyorId", status = ConveyorStatus.MANUAL)
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(conv.copy(status = ConveyorStatus.MANUAL))
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override suspend fun manualStop(conveyorId: Int): NetworkResult<Conveyor> {
        val cId = "C$conveyorId"
        val result = safeApiCall { api.manualStopConveyor(cId) }
        refreshConveyors()
        val conv = _conveyors.value.firstOrNull { it.id == conveyorId } ?: Conveyor(conveyorId = cId, id = conveyorId, name = "Conveyor $conveyorId", status = ConveyorStatus.IDLE)
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(conv.copy(status = ConveyorStatus.IDLE))
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override suspend fun addOnePack(operationId: String): NetworkResult<LoadingOperation> {
        refreshConveyors()
        val op = getOperation(operationId) ?: LoadingOperation(
            operationId = operationId, orderId = 0, orderNumber = "", vehicleNumber = "",
            itemId = 0, itemName = "", conveyorId = 1, conveyorName = "",
            requiredQuantity = 10, loadedQuantity = 1, status = ConveyorStatus.RUNNING
        )
        return NetworkResult.Success(op)
    }

    override suspend fun reportFailure(operationId: String, reason: String): NetworkResult<LoadingOperation> {
        val conveyorId = findConveyorIdForOperation(operationId)
        val result = safeApiCall { api.stopConveyor(conveyorId) }
        refreshConveyors()
        val op = getOperation(operationId) ?: LoadingOperation(
            operationId = operationId, orderId = 0, orderNumber = "", vehicleNumber = "",
            itemId = 0, itemName = "", conveyorId = 1, conveyorName = "",
            requiredQuantity = 0, loadedQuantity = 0, status = ConveyorStatus.ERROR, failureReason = reason
        )
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(op.copy(status = ConveyorStatus.ERROR, failureReason = reason))
            is NetworkResult.BusinessError -> NetworkResult.BusinessError(result.message)
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message)
            is NetworkResult.NetworkError -> NetworkResult.NetworkError(result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.UnknownError -> NetworkResult.UnknownError(result.message)
        }
    }

    override fun getOperation(operationId: String): LoadingOperation? =
        _operations.value.firstOrNull { it.operationId == operationId }

    override fun getActiveOperation(): LoadingOperation? =
        _operations.value.firstOrNull { !it.isFinished }

    companion object {
        private const val FALLBACK_POLL_INTERVAL_MS = 5_000L
    }
}
