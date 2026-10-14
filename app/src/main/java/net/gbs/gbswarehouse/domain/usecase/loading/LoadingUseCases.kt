package net.gbs.gbswarehouse.domain.usecase.loading

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.LoadingRunDto
import net.gbs.gbswarehouse.domain.model.BagType
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.ConveyorSettings
import net.gbs.gbswarehouse.domain.model.LoadingEvent
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.model.StreamStatus
import net.gbs.gbswarehouse.domain.repository.LoadingRepository
import javax.inject.Inject

class GetLoadingOrdersUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<LoadingOrder>> = repository.getOrders()
}

class GetLoadingOrderDetailsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(orderId: Int): NetworkResult<LoadingOrder> =
        repository.getOrderDetails(orderId)
}

class GetCachedLoadingOrdersUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): List<LoadingOrder>? = repository.getCachedOrders()
}

class ObserveConveyorsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): StateFlow<List<Conveyor>> = repository.conveyors
}

class GetConveyorsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Conveyor>> = repository.getConveyors()
}

class ObserveLoadingOperationsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): StateFlow<List<LoadingOperation>> = repository.operations
}

class ObserveLoadingEventsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): SharedFlow<LoadingEvent> = repository.events
}

class ObserveStreamStatusUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): StateFlow<StreamStatus> = repository.streamStatus
}

class ObserveOperationUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(operationId: String): Flow<LoadingOperation?> =
        repository.operations.map { operations ->
            operations.firstOrNull { it.operationId == operationId }
        }
}

class StartLoadingUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(
        order: LoadingOrder,
        item: OrderItem,
        conveyor: Conveyor,
        qty: Int
    ): NetworkResult<LoadingOperation> = repository.startLoading(order, item, conveyor, qty)
}

class PauseLoadingUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(operationId: String): NetworkResult<LoadingOperation> =
        repository.pauseLoading(operationId)
}

class ResumeLoadingUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(operationId: String): NetworkResult<LoadingOperation> =
        repository.resumeLoading(operationId)
}

class ManualStartConveyorUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(conveyorId: Int): NetworkResult<Conveyor> =
        repository.manualStart(conveyorId)
}

class ManualStopConveyorUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(conveyorId: Int): NetworkResult<Conveyor> =
        repository.manualStop(conveyorId)
}

class AddOnePackUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(operationId: String): NetworkResult<LoadingOperation> =
        repository.addOnePack(operationId)
}

class ReportLoadingFailureUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(
        operationId: String,
        reason: String
    ): NetworkResult<LoadingOperation> = repository.reportFailure(operationId, reason)
}

class GetLoadingOperationUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(operationId: String): LoadingOperation? = repository.getOperation(operationId)
}

class GetActiveLoadingOperationUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    operator fun invoke(): LoadingOperation? = repository.getActiveOperation()
}

class ResetConveyorFaultUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(conveyorId: String): NetworkResult<Unit> = repository.resetConveyorFault(conveyorId)
}

class GetConveyorSettingsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(conveyorId: String): NetworkResult<ConveyorSettings> = repository.getConveyorSettings(conveyorId)
}

class UpdateConveyorSettingsUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(conveyorId: String, settings: ConveyorSettings): NetworkResult<ConveyorSettings> =
        repository.updateConveyorSettings(conveyorId, settings)
}

class GetBagTypesUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<BagType>> = repository.getBagTypes()
}

class GetLoadingRunsReportUseCase @Inject constructor(
    private val repository: LoadingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<LoadingRunDto>> = repository.getLoadingRunsReport()
}

data class LoadingUseCases(
    val getLoadingOrders: GetLoadingOrdersUseCase,
    val getLoadingOrderDetails: GetLoadingOrderDetailsUseCase,
    val getCachedLoadingOrders: GetCachedLoadingOrdersUseCase,
    val observeConveyors: ObserveConveyorsUseCase,
    val getConveyors: GetConveyorsUseCase,
    val observeLoadingOperations: ObserveLoadingOperationsUseCase,
    val observeLoadingEvents: ObserveLoadingEventsUseCase,
    val observeStreamStatus: ObserveStreamStatusUseCase,
    val observeOperation: ObserveOperationUseCase,
    val startLoading: StartLoadingUseCase,
    val pauseLoading: PauseLoadingUseCase,
    val resumeLoading: ResumeLoadingUseCase,
    val manualStartConveyor: ManualStartConveyorUseCase,
    val manualStopConveyor: ManualStopConveyorUseCase,
    val addOnePack: AddOnePackUseCase,
    val reportLoadingFailure: ReportLoadingFailureUseCase,
    val getLoadingOperation: GetLoadingOperationUseCase,
    val getActiveLoadingOperation: GetActiveLoadingOperationUseCase,
    val resetConveyorFault: ResetConveyorFaultUseCase,
    val getConveyorSettings: GetConveyorSettingsUseCase,
    val updateConveyorSettings: UpdateConveyorSettingsUseCase,
    val getBagTypes: GetBagTypesUseCase,
    val getLoadingRunsReport: GetLoadingRunsReportUseCase
)
