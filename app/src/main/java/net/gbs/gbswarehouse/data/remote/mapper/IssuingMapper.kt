package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.domain.model.*

fun SAPWarehouseDto.toDomain() = SAPWarehouse(
    warehouseId = warehouseId,
    warehouseCode = warehouseCode,
    warehouseDescription = warehouseDescription,
    plantCode = plantCode
)

fun SAPMaterialBatchValuationStockDto.toDomain() = SAPMaterialBatchValuationStock(
    materialBatchValuationStockId = materialBatchValuationStockId,
    materialCode = materialCode,
    plantCode = plantCode,
    storageLocationCode = storageLocationCode,
    batch = batch,
    quantity = quantity,
    description = description,
    unitOfMeasure = unitOfMeasure,
    batchManagement = batchManagement,
    batchDate = batchDate,
)

fun SparePartsReturnReservationDto.toDomain() = SparePartsReturnReservationOrder(
    reservationNumber = reservationNumber,
    orderNumber = orderNumber,
    orderDescription = orderDescription,
    companyCode = companyCode,
    plantCode = plantCode,
    costCenterCode = costCenterCode,
    costCenterDescription = costCenterDescription,
    requirementDate = requirementDate,
    movementType = movementType,
    itemCount = itemCount
)

fun TransferToProductionReservationLineDto.toDomain() = TransferToProductionReservationLine(
    reservationNumber = reservationNumber,
    reservationItem = reservationItem,
    materialCode = materialCode,
    materialDescription = materialDescription,
    uom = uom,
    plantCode = plantCode,
    storageLocationCode = storageLocationCode,
    movementType = movementType,
    batch = batch,
    quantity = quantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    costCenterCode = costCenterCode,
    costCenterDescription = costCenterDescription,
    receivingPlantCode = receivingPlantCode,
    receivingStorageLocationCode = receivingStorageLocationCode,
    isBatchManaged = isBatchManaged,
    valuationTypes = valuationTypes.map { it.toValuationType() }
)

fun PostTransferToProductionResultDto.toDomain() = PostTransferToProductionResult(
    receiptStatus = receiptStatus.toDomain(),
    reservationLines = reservationLines.map { it.toDomain() }
)

fun PostSparePartsIssueToMaintenanceResultDto.toDomain() = PostSparePartsIssueToMaintenanceResult(
    receiptStatus = receiptStatus.toDomain(),
    reservationLines = reservationLines.map { it.toDomain() }
)

fun PostIssueToInternalOrderResultDto.toDomain() = PostIssueToInternalOrderResult(
    receiptStatus = receiptStatus.toDomain(),
    reservationLines = reservationLines.map { it.toDomain() }
)

fun PostDirectTransferResultDto.toDomain() = PostDirectTransferResult(
    receiptStatus = receiptStatus.toDomain()
)

fun PostDirectIssueOnCostCenterResultDto.toDomain() = PostDirectIssueOnCostCenterResult(
    receiptStatus = receiptStatus.toDomain(),
    reservationLines = reservationLines.map { it.toDomain() }
)

fun CompanyDto.toDomain() = Company(
    companyCode = companyCode ?: "",
    companyDescription = companyDescription ?: ""
)

fun CostCenterDto.toDomain() = CostCenter(
    costCenterId = costCenterId ?: 0,
    costCenterCode = costCenterCode ?: "",
    costCenterDescription = costCenterDescription ?: "",
    companyCode = companyCode ?: "",
    companyDescription = companyDescription ?: ""
)

fun PostScrapIssueResultDto.toDomain() = PostScrapIssueResult(
    receiptStatus = receiptStatus.toDomain()
)

fun ScrapIssueLine.toDto(): ScrapIssueLineDto =
    ScrapIssueLineDto(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        uom = uom,
        quantity = quantity,
        storageBinCode = storageBinCode,
        costCenterCode = costCenterCode,
        itemText = itemText,

    )

fun ScrapIssueLineDto.toModel():ScrapIssueLine =
    ScrapIssueLine(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        uom = uom,
        quantity = quantity,
        storageBinCode = storageBinCode,
        costCenterCode = costCenterCode,
        itemText = itemText,
    )
fun List<ScrapIssueLine>.toDto(): List<ScrapIssueLineDto> =
    map { it.toDto() }

fun List<ScrapIssueLineDto>.toModel(): List<ScrapIssueLine> =
    map { it.toModel() }
