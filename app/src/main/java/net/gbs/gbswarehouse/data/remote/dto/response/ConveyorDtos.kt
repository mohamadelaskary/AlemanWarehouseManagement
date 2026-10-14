package net.gbs.gbswarehouse.data.remote.dto.response

data class ConveyorStatusDto(
    val conveyorId: String = "",
    val name: String = "",
    val online: Boolean = false,
    val state: String = "Offline",
    val motorRunning: Boolean = false,
    val loadedQty: Int = 0,
    val targetQty: Int = 0,
    val faultCode: Int = 0,
    val faultName: String? = null,
    val faultText: String? = null,
    val sensorBlocked: Boolean = false,
    val remoteEnabled: Boolean = false,
    val runId: String? = null,
    val salesOrder: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null
)

data class StartLoadingRequestDto(
    val goodsReceiptLineId: Int,
    val qty: Int
)

data class ConveyorSettingsDto(
    val beltSpeedMmPerSec: Int = 0,
    val stopDelaySec: Int = 0
)

data class LoadingRunDto(
    val runId: String = "",
    val conveyorId: String = "",
    val receiptNo: String = "",
    val salesOrder: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null,
    val targetQty: Int = 0,
    val loadedQty: Int = 0,
    val status: String = "",
    val faultCode: Int = 0,
    val faultText: String? = null,
    val startedBy: String? = null,
    val startedByUserName: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null
)

data class BagTypeDto(
    val id: Int = 0,
    val materialCode: String = "",
    val name: String? = null,
    val lengthMm: Int = 0,
    val widthMm: Int? = null,
    val lengthToleranceUpMm: Int? = null,
    val lengthToleranceDownMm: Int? = null,
    val weightKg: Double? = null
)
