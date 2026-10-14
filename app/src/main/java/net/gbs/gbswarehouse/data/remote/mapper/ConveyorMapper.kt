package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.domain.model.*

fun ConveyorStatusDto.toDomain(): Conveyor {
    val numericId = conveyorId.filter { it.isDigit() }.toIntOrNull() ?: 1
    val mappedStatus = when (state.lowercase()) {
        "idle" -> ConveyorStatus.IDLE
        "running" -> ConveyorStatus.RUNNING
        "paused" -> ConveyorStatus.PAUSED
        "stopping" -> ConveyorStatus.RUNNING
        "completed" -> ConveyorStatus.COMPLETED
        "fault", "error", "offline" -> ConveyorStatus.ERROR
        "manual" -> ConveyorStatus.MANUAL
        else -> ConveyorStatus.IDLE
    }

    val operation = if (!runId.isNullOrEmpty()) {
        LoadingOperation(
            operationId = runId,
            orderId = 0,
            orderNumber = salesOrder ?: "",
            vehicleNumber = "",
            itemId = 0,
            itemName = materialCode ?: salesOrderItem ?: name,
            conveyorId = numericId,
            conveyorName = name,
            requiredQuantity = targetQty,
            loadedQuantity = loadedQty,
            status = mappedStatus,
            failureReason = if (mappedStatus == ConveyorStatus.ERROR) {
                faultText ?: faultName
            } else null,
            salesOrderItem = salesOrderItem,
            materialCode = materialCode
        )
    } else null

    return Conveyor(
        id = numericId,
        conveyorId = conveyorId,
        name = name,
        status = mappedStatus,
        currentOperation = operation,
        online = online,
        faultCode = faultCode,
        faultName = faultName,
        faultText = faultText,
        sensorBlocked = sensorBlocked,
        remoteEnabled = remoteEnabled,
        motorRunning = motorRunning,
        runId = runId,
        salesOrder = salesOrder,
        salesOrderItem = salesOrderItem,
        materialCode = materialCode,
        loadedQty = loadedQty,
        targetQty = targetQty
    )
}

fun ConveyorSettingsDto.toDomain(): ConveyorSettings = ConveyorSettings(
    beltSpeedMmPerSec = beltSpeedMmPerSec,
    stopDelaySec = stopDelaySec
)

fun ConveyorSettings.toDto(): ConveyorSettingsDto = ConveyorSettingsDto(
    beltSpeedMmPerSec = beltSpeedMmPerSec,
    stopDelaySec = stopDelaySec
)

fun BagTypeDto.toDomain(): BagType = BagType(
    id = id,
    materialCode = materialCode,
    name = name,
    lengthMm = lengthMm,
    widthMm = widthMm,
    lengthToleranceUpMm = lengthToleranceUpMm,
    lengthToleranceDownMm = lengthToleranceDownMm,
    weightKg = weightKg
)
