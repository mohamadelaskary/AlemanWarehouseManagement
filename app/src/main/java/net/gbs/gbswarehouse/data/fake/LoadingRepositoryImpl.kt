package net.gbs.gbswarehouse.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.LoadingRunDto
import net.gbs.gbswarehouse.data.remote.mapper.toDomain
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.BagType
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.ConveyorSettings
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.repository.LoadingRepository
import net.gbs.gbswarehouse.domain.repository.PlcDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orders come from `api/Conveyors/GetLoadingOrders`.
 * Conveyor and operation state is fetched directly from backend API endpoints (`api/Conveyors/GetConveyors`).
 */
@Singleton
class LoadingRepositoryImpl @Inject constructor(
    private val plcDataSource: PlcDataSource,
    private val api: ApiService
) : LoadingRepository {

    override val conveyors = plcDataSource.conveyors
    override val operations = plcDataSource.operations
    override val events = plcDataSource.events
    override val streamStatus = plcDataSource.streamStatus

    private val orderCache = MutableStateFlow<List<LoadingOrder>>(emptyList())

    override suspend fun getOrders(): NetworkResult<List<LoadingOrder>> {
        val result = safeApiCall { api.getLoadingOrders() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
        if (result is NetworkResult.Success) {
            orderCache.value = result.data
        }
        return result
    }

    override fun getCachedOrders(): List<LoadingOrder>? {
        val cached = orderCache.value
        return if (cached.isEmpty()) null else cached.map(::withLiveProgress)
    }

    override suspend fun getOrderDetails(orderId: Int): NetworkResult<LoadingOrder> {
        var cached = orderCache.value.firstOrNull { it.id == orderId }
        if (cached == null) {
            val refreshed = getOrders()
            if (refreshed is NetworkResult.Success) {
                cached = refreshed.data.firstOrNull { it.id == orderId }
            }
        }
        return cached?.let { NetworkResult.Success(withLiveProgress(it)) }
            ?: NetworkResult.BusinessError("Loading order not found")
    }

    private fun withLiveProgress(order: LoadingOrder): LoadingOrder = order.copy(
        items = order.items.map { item ->
            val operations = plcDataSource.operations.value
            val live = operations
                .filter { op ->
                    (op.orderId > 0 && op.orderId == order.id) ||
                        (op.orderNumber.isNotBlank() && op.orderNumber == order.orderNumber)
                }
                .filter { op ->
                    (op.itemId > 0 && op.itemId == item.id) ||
                        (op.salesOrderItem != null && op.salesOrderItem == item.salesOrderItem) ||
                        (op.materialCode != null && op.materialCode == item.materialCode)
                }
                .maxByOrNull { it.requiredQuantity }
                ?: operations.firstOrNull {
                    it.orderId == order.id && it.itemId == item.id
                }
            if (live == null) {
                item
            } else {
                item.copy(
                    requiredQuantity = live.requiredQuantity,
                    loadedQuantity = live.loadedQuantity.coerceIn(0, live.requiredQuantity)
                )
            }
        }
    )

    override suspend fun startLoading(
        order: LoadingOrder,
        item: OrderItem,
        conveyor: Conveyor,
        qty: Int
    ): NetworkResult<LoadingOperation> = plcDataSource.startLoading(order, item, conveyor, qty)

    override suspend fun pauseLoading(operationId: String): NetworkResult<LoadingOperation> =
        plcDataSource.pauseLoading(operationId)

    override suspend fun resumeLoading(operationId: String): NetworkResult<LoadingOperation> =
        plcDataSource.resumeLoading(operationId)

    override suspend fun manualStart(conveyorId: Int): NetworkResult<Conveyor> =
        plcDataSource.manualStart(conveyorId)

    override suspend fun manualStop(conveyorId: Int): NetworkResult<Conveyor> =
        plcDataSource.manualStop(conveyorId)

    override suspend fun addOnePack(operationId: String): NetworkResult<LoadingOperation> =
        plcDataSource.addOnePack(operationId)

    override suspend fun reportFailure(
        operationId: String,
        reason: String
    ): NetworkResult<LoadingOperation> = plcDataSource.reportFailure(operationId, reason)

    override suspend fun getConveyors(): NetworkResult<List<Conveyor>> =
        plcDataSource.getConveyors()

    override suspend fun getConveyor(conveyorId: String): NetworkResult<Conveyor> =
        safeApiCall { api.getConveyor(conveyorId) }
            .map { response -> response.data?.toDomain() ?: throw IllegalStateException("Conveyor not found") }

    override suspend fun resetConveyorFault(conveyorId: String): NetworkResult<Unit> =
        safeApiCall { api.resetConveyorFault(conveyorId) }
            .map { }

    override suspend fun getConveyorSettings(conveyorId: String): NetworkResult<ConveyorSettings> =
        safeApiCall { api.getConveyorSettings(conveyorId) }
            .map { response -> response.data?.toDomain() ?: throw IllegalStateException("Settings not found") }

    override suspend fun updateConveyorSettings(
        conveyorId: String,
        settings: ConveyorSettings
    ): NetworkResult<ConveyorSettings> =
        safeApiCall { api.updateConveyorSettings(conveyorId, settings.toDto()) }
            .map { response -> response.data?.toDomain() ?: settings }

    override suspend fun getBagTypes(): NetworkResult<List<BagType>> =
        safeApiCall { api.getBagTypes() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }

    override suspend fun getLoadingRunsReport(): NetworkResult<List<LoadingRunDto>> =
        safeApiCall { api.getLoadingRunsReport() }
            .map { response -> response.data ?: emptyList() }

    override fun getOperation(operationId: String): LoadingOperation? =
        plcDataSource.getOperation(operationId)

    override fun getActiveOperation(): LoadingOperation? =
        plcDataSource.getActiveOperation()
}
