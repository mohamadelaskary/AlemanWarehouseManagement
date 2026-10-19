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

fun MaterialOnHandStockDto.toDomain() = MaterialOnHandStock(
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    storageBinCode = storageBinCode,
    materialCode = materialCode,
    materialDescription = materialDescription,
    batch = batch,
    batchDate = batchDate,
    valuationType = valuationType,
    isBatchManaged = isBatchManaged,
    availableQty = availableQty,
    uom = uom ?: unitOfMeasure
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
    itemCount = itemCount,
    lines = lines.map { it.toDomain() }
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
    valuationTypes = valuationTypes?.map { it.toValuationType() } ?: emptyList()
)

@JvmName("transferToProductionLinesToResult")
fun List<TransferToProductionReservationLineDto>.toTransferToProductionResult() = PostTransferToProductionResult(
    reservationLines = map { it.toDomain() }
)

fun List<SparePartsReturnReservationLineDto>.toSparePartsIssueToMaintenanceResult() = PostSparePartsIssueToMaintenanceResult(
    reservationLines = map { it.toDomain() }
)

fun List<SparePartsReturnReservationLineDto>.toIssueToInternalOrderResult() = PostIssueToInternalOrderResult(
    reservationLines = map { it.toDomain() }
)

fun PostDirectTransferResultDto.toDomain() = PostDirectTransferResult(
    receiptStatus = receiptStatus.toDomain()
)

fun List<DirectIssueReservationLineDto>.toDirectIssueOnCostCenterResult() = PostDirectIssueOnCostCenterResult(
    reservationLines = toModel()
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
        batch = batch,
        valuationType = valuationType,
        companyCode = companyCode,

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
        batch = batch,
        valuationType = valuationType,
        companyCode = companyCode,
    )
@JvmName("scrapIssueLineListToDto")
fun List<ScrapIssueLine>.toDto(): List<ScrapIssueLineDto> =
    map { it.toDto() }

@JvmName("scrapIssueLineDtoListToModel")
fun List<ScrapIssueLineDto>.toModel(): List<ScrapIssueLine> =
    map { it.toModel() }

fun DirectIssueReservationLineDto.toModel(): DirectIssueReservationLine =
    DirectIssueReservationLine(
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
    )

fun DirectIssueReservationLine.toDto(): DirectIssueReservationLineDto =
    DirectIssueReservationLineDto(
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
    )

fun FinishedGoodsFeedOrderDto.toDomain() = FinishedGoodsFeedOrder(
    salesOrderNumber = salesOrderNumber,
    customerCode = customerCode,
    customerName = customerName,
    orderDescription = orderDescription,
    carNo = carNo,
    isFactoryCar = isFactoryCar ?: true,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    lines = lines.map { it.toDomain() }
)

fun FinishedGoodsFeedItemDto.toDomain() = FinishedGoodsFeedItem(
    customerCode = customerCode,
    customerName = customerName,
    orderType = orderType,
    orderDescription = orderDescription,
    shippingPointCode = shippingPointCode,
    salesOrder = salesOrder,
    itemNo = itemNo,
    materialCode = materialCode,
    materialDescription = materialDescription,
    quantity = quantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    carNo = carNo,
    factoryCarDescription = factoryCarDescription
)

fun IssueFinishedGoodsFeedResultDto.toDomain() = IssueFinishedGoodsFeedResult(
    goodsReceiptId = goodsReceiptId,
    receiptNo = receiptNo,
    receiptStatus = receiptStatus,
    deliveryNo = deliveryNo,
    readyForPutAway = readyForPutAway,
    needsAction = needsAction,
    hasOrphanedLines = hasOrphanedLines,
    lines = lines.map { it.toDomain() }
)

fun IssueFinishedGoodsFeedResultLineDto.toDomain() = IssueFinishedGoodsFeedResultLine(
    goodsReceiptLineId = goodsReceiptLineId,
    material = material,
    materialDescription = materialDescription,
    receivedQuantity = receivedQuantity,
    sapOutboxMessageId = sapOutboxMessageId,
    sapStatus = sapStatus,
    sapMaterialDocument = sapMaterialDocument,
    sapMaterialDocumentYear = sapMaterialDocumentYear,
    attemptCount = attemptCount,
    maxAttempts = maxAttempts,
    lastErrorMessage = lastErrorMessage,
    salesOrder = salesOrder,
    salesOrderItem = salesOrderItem,
    billingDocumentNo = billingDocumentNo,
    billingAmount = billingAmount
)

@JvmName("directIssueReservationLineListToDto")
fun List<DirectIssueReservationLine>.toDto(): List<DirectIssueReservationLineDto> =
    map { it.toDto() }

fun TransferOrderDto.toDomain() = TransferOrder(
    transferOrderNumber = transferOrderNumber,
    issuingPlantDescription = issuingPlantDescription,
    carNo = carNo,
    isFactoryCar = isFactoryCar ?: true,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    lines = lines.map { it.toDomain() }
)

fun TransferOrderItemDto.toDomain() = TransferOrderItem(
    poNumber = poNumber,
    companyCode = companyCode,
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    issuingPlantCode = issuingPlantCode,
    issuingPlantDescription = issuingPlantDescription,
    receivingPlantCode = receivingPlantCode,
    receivingPlantDescription = receivingPlantDescription,
    carNo = carNo,
    factoryCarDescription = factoryCarDescription
)


@JvmName("directIssueReservationLineDtoListToModel")
fun List<DirectIssueReservationLineDto>.toModel(): List<DirectIssueReservationLine> =
    map { it.toModel() }
fun DirectTransferPutAwayDocumentDto.toDomain() = DirectTransferPutAwayDocument(
    goodsReceiptId = goodsReceiptId,
    receiptNo = receiptNo,
    plantCodes = plantCodes,
    warehouseCodes = warehouseCodes,
    receivingPlantCodes = receivingPlantCodes,
    receivingWarehouseCodes = receivingWarehouseCodes,
    itemCount = itemCount,
    createdBy = createdBy,
    transferredAt = transferredAt,
    materialCode = materialCode,
    materialDescription = materialDescription ?: materialName,
    remainingQuantity = remainingQuantity,
    carNo = carNo ?: carNumber
)

fun DirectTransferPutAwayLineDto.toDomain() = DirectTransferPutAwayLine(
    goodsReceiptLineId = goodsReceiptLineId,
    goodsReceiptId = goodsReceiptId,
    receiptNo = receiptNo,
    materialCode = materialCode,
    materialDescription = materialDescription ?: materialName,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    receivingPlantCode = receivingPlantCode,
    receivingWarehouseCode = receivingWarehouseCode,
    batch = batch,
    valuationType = valuationType,
    uom = uom,
    transferredQuantity = transferredQuantity,
    putAwayQuantity = putAwayQuantity,
    remainingQuantity = remainingQuantity,
    notes = notes,
    transferredAt = transferredAt,
    carNo = carNo ?: carNumber
)

fun IssuingOpenOrderCountsDto.toDomain() = IssuingOpenOrderCounts(
    transferToProduction = transferToProduction,
    sparePartsToMaintenance = sparePartsToMaintenance,
    internalOrder = internalOrder,
    directIssueOnCostCenter = directIssueOnCostCenter,
    finishedGoodsFeed = finishedGoodsFeed,
    transferOrder = transferOrder,
    total = total
)

fun MaterialBinLocationDto.toDomain() = MaterialBinLocation(
    storageBinName = storageBinName,
    onHandQty = onHandQty,
    reservedQty = reservedQty,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    storageBinCode = storageBinCode,
    materialCode = materialCode,
    materialDescription = materialDescription,
    uom = uom,
    batch = batch,
    batchDate = batchDate,
    valuationType = valuationType,
    isBatchManaged = isBatchManaged,
    availableQty = availableQty
)

