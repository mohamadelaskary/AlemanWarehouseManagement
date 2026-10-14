package net.gbs.gbswarehouse.domain.model

enum class ConveyorStatus {
    IDLE,
    RUNNING,
    PAUSED,
    ERROR,
    COMPLETED,
    MANUAL
}

/** Connection state of the live conveyor stream (SignalR / SSE). */
enum class StreamStatus {
    OFFLINE,
    CONNECTING,
    LIVE
}

data class OrderItem(
    val id: Int,
    val name: String,
    val requiredQuantity: Int,
    val loadedQuantity: Int,
    val salesOrder: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null,
    val itemText: String? = null,
    val quantity: Double = 0.0,
    val uom: String? = null,
    val plantCode: String? = null,
    val bagLengthMm: Int = 0,
    val bagWeightKg: Double = 0.0,
    val remainingSuggestedBags: Int = 0
) {
    val remainingQuantity: Int get() = (requiredQuantity - loadedQuantity).coerceAtLeast(0)

    /** True when there is nothing left to load, so the item can't be started again. */
    val isFullyLoaded: Boolean get() = remainingQuantity <= 0
}

data class LoadingOrder(
    val id: Int,
    val orderNumber: String,
    val vehicleNumber: String,
    val items: List<OrderItem>,
    val status: String? = null,
    val shippingPointCode: String? = null,
    val workerGroupCode: String? = null,
    val createdAt: String? = null,
    val orderType: String? = null
)

data class Conveyor(
    val id: Int,
    val conveyorId: String = "C$id",
    val name: String,
    val status: ConveyorStatus,
    val currentOperation: LoadingOperation? = null,
    val online: Boolean = true,
    val faultCode: Int = 0,
    val faultName: String? = null,
    val faultText: String? = null,
    val sensorBlocked: Boolean = false,
    val remoteEnabled: Boolean = true,
    val motorRunning: Boolean = false,
    val runId: String? = null,
    val salesOrder: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null,
    val loadedQty: Int = 0,
    val targetQty: Int = 0
) {
    val isAvailable: Boolean
        get() = online && (status == ConveyorStatus.IDLE || status == ConveyorStatus.COMPLETED)

    val hasFault: Boolean
        get() = faultCode != 0 || !faultText.isNullOrBlank() || !faultName.isNullOrBlank()
}

data class LoadingOperation(
    val operationId: String,
    val orderId: Int,
    val orderNumber: String,
    val vehicleNumber: String,
    val itemId: Int,
    val itemName: String,
    val conveyorId: Int,
    val conveyorName: String,
    val requiredQuantity: Int,
    val loadedQuantity: Int,
    val status: ConveyorStatus,
    val failureReason: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null
) {
    val remainingQuantity: Int get() = (requiredQuantity - loadedQuantity).coerceAtLeast(0)
    val progress: Float
        get() = if (requiredQuantity <= 0) 0f
        else (loadedQuantity.toFloat() / requiredQuantity.toFloat()).coerceIn(0f, 1f)

    val isFinished: Boolean
        get() = status == ConveyorStatus.COMPLETED || status == ConveyorStatus.ERROR
}

data class ConveyorSettings(
    val beltSpeedMmPerSec: Int,
    val stopDelaySec: Int
)

data class BagType(
    val id: Int,
    val materialCode: String,
    val name: String?,
    val lengthMm: Int,
    val widthMm: Int?,
    val lengthToleranceUpMm: Int?,
    val lengthToleranceDownMm: Int?,
    val weightKg: Double?
)

sealed interface LoadingEvent {
    data class BagCounted(
        val operationId: String,
        val loadedQuantity: Int
    ) : LoadingEvent

    data class Completed(val operation: LoadingOperation) : LoadingEvent
    data class Failed(val operation: LoadingOperation) : LoadingEvent
    data class ConveyorStatusChanged(val conveyor: Conveyor) : LoadingEvent
}
