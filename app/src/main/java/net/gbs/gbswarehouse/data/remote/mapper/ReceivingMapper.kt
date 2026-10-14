package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.data.remote.mapper.toValuationTypes
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.QualityDecision

typealias DomainGoodsReceiptLineStatusResult = net.gbs.gbswarehouse.domain.model.GoodsReceiptLineStatusResult
typealias DomainGoodsReceiptStatusResult = net.gbs.gbswarehouse.domain.model.GoodsReceiptStatusResult

fun SparePartsPurchaseOrderItemDto.toDomain() = SparePartsPurchaseOrderItem(
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    isBatchManaged = isBatchManaged,
    valuationTypes = valuationTypes?.map { it.toValuationType() } ?: emptyList()
)

fun SparePartsPurchaseOrderDto.toDomain() = SparePartsPurchaseOrder(
    poNumber = poNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    companyCode = companyCode,
    purchaseOrderItems = purchaseOrderItems.map { it.toDomain() }
)

fun RawMaterialsPurchaseOrderItemDto.toDomain() = RawMaterialsPurchaseOrderItem(
    poNumber = poNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    companyCode = companyCode,
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    carNo = carNo,
    isFactoryCar = isFactoryCar,
    factoryCarDescription = factoryCarDescription
)

fun RawMaterialsPurchaseOrderDto.toDomain() = RawMaterialsPurchaseOrder(
    purchaseOrderNumber = purchaseOrderNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    carNo = carNo,
    isFactoryCar = isFactoryCar,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    purchaseOrderItems = lines.map { it.toDomain() }
)

fun PackagingMaterialsPurchaseOrderItemDto.toDomain() = PackagingMaterialsPurchaseOrderItem(
    poNumber = poNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    companyCode = companyCode,
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    carNo = carNo,
    isFactoryCar = isFactoryCar,
    factoryCarDescription = factoryCarDescription
)

fun PackagingMaterialsPurchaseOrderDto.toDomain() = PackagingMaterialsPurchaseOrder(
    purchaseOrderNumber = purchaseOrderNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    carNo = carNo,
    isFactoryCar = isFactoryCar,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    purchaseOrderItems = lines.map { it.toDomain() }
)

fun PoultryMaterialsPurchaseOrderItemDto.toDomain() = PoultryMaterialsPurchaseOrderItem(
    poNumber = poNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    companyCode = companyCode,
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    carNo = carNo,
    isFactoryCar = isFactoryCar ?: true,
    factoryCarDescription = factoryCarDescription
)

fun PoultryMaterialsPurchaseOrderDto.toDomain() = PoultryMaterialsPurchaseOrder(
    purchaseOrderNumber = purchaseOrderNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    carNo = carNo,
    isFactoryCar = isFactoryCar,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    purchaseOrderItems = lines.map { it.toDomain() }
)

fun TransitOrderMaterialPurchaseOrderItemDto.toDomain() = TransitOrderMaterialPurchaseOrderItem(
    poNumber = poNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    companyCode = companyCode,
    poItemNumber = poItemNumber,
    materialCode = materialCode,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    receivedQuantity = receivedQuantity,
    remainingQuantity = remainingQuantity,
    uom = uom,
    plantCode = plantCode,
    plantDescription = plantDescription,
    carNo = carNo,
    isFactoryCar = isFactoryCar ?: true,
    factoryCarDescription = factoryCarDescription
)

fun PoultryMaterialsLineDto.toModel(): PoultryMaterialsLine {
    return PoultryMaterialsLine(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        carNo = carNo,
        unloadingPoint = unloadingPoint,
        naolon = naolon,
        contNo = contNo,
        loadingVendorCode = loadingVendorCode,
        wrokerGroupCode = wrokerGroupCode,
        naolonDescription = naolonDescription,
        packTypeCode = packTypeCode,
        isBatchManaged = isBatchManaged,
        expirationDate = expirationDate
    )
}

@JvmName("toPoultryMaterialsLineModelList")
fun List<PoultryMaterialsLineDto>.toModel(): List<PoultryMaterialsLine> {
    return this.map { it.toModel() }
}

fun PoultryMaterialsLine.toDto(): PoultryMaterialsLineDto {
    return PoultryMaterialsLineDto(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        carNo = carNo,
        unloadingPoint = unloadingPoint,
        naolon = naolon,
        contNo = contNo,
        loadingVendorCode = loadingVendorCode,
        wrokerGroupCode = wrokerGroupCode,
        naolonDescription = naolonDescription,
        packTypeCode = packTypeCode,
        isBatchManaged = isBatchManaged,
        expirationDate = expirationDate
    )
}

@JvmName("toPoultryMaterialsLineDtoList")
fun List<PoultryMaterialsLine>.toDto(): List<PoultryMaterialsLineDto> {
    return this.map { it.toDto() }
}

fun TransitOrderMaterialLineDto.toModel(): TransitOrderMaterialLine {
    return TransitOrderMaterialLine(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        carNo = carNo,
        unloadingPoint = unloadingPoint,
        naolon = naolon,
        contNo = contNo,
        loadingVendorCode = loadingVendorCode,
        wrokerGroupCode = wrokerGroupCode,
        naolonDescription = naolonDescription,
        packTypeCode = packTypeCode,
        carType = carType
    )
}

@JvmName("toTransitOrderMaterialLineModelList")
fun List<TransitOrderMaterialLineDto>.toModel(): List<TransitOrderMaterialLine> {
    return this.map { it.toModel() }
}

fun TransitOrderMaterialLine.toDto(): TransitOrderMaterialLineDto {
    return TransitOrderMaterialLineDto(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        carNo = carNo,
        unloadingPoint = unloadingPoint,
        naolon = naolon,
        contNo = contNo,
        loadingVendorCode = loadingVendorCode,
        wrokerGroupCode = wrokerGroupCode,
        naolonDescription = naolonDescription,
        packTypeCode = packTypeCode,
        carType = carType
    )
}

@JvmName("toTransitOrderMaterialLineDtoList")
fun List<TransitOrderMaterialLine>.toDto(): List<TransitOrderMaterialLineDto> {
    return this.map { it.toDto() }
}

fun TransitOrderMaterialPurchaseOrderDto.toDomain() = TransitOrderMaterialPurchaseOrder(
    purchaseOrderNumber = purchaseOrderNumber,
    vendorCode = vendorCode,
    vendorName = vendorName,
    warehouseCode = warehouseCode,
    warehouseDescription = warehouseDescription,
    companyCode = companyCode,
    carNo = carNo,
    isFactoryCar = isFactoryCar ?: true,
    factoryCarDescription = factoryCarDescription,
    itemCount = itemCount,
    issuingPlantCode = issuingPlantCode,
    issuingPlantDescription = issuingPlantDescription,
    purchaseOrderItems = lines.map { it.toDomain() }
)

fun FinishedGoodsPlasticProductionOrderItemDto.toDomain() = FinishedGoodsPlasticProductionOrderItem(
    itemNumber = itemNumber,
    materialCode = materialCode,
    remainingQuantity = remainingQuantity,
    receivedQuantity = receivedQuantity,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    orderType = orderType,
    basicStartDate = basicStartDate,
    uom = uom,
)

fun FinishedGoodsPlasticProductionOrderDto.toDomain() = FinishedGoodsPlasticProductionOrder(
    orderNumber = orderNumber,
    productionOrderItems = productionOrderItems.map { it.toDomain() }
)

fun FinishedGoodsFeedProductionOrderItemDto.toDomain() = FinishedGoodsFeedProductionOrderItem(
    itemNumber = itemNumber,
    materialCode = materialCode,
    remainingQuantity = remainingQuantity,
    receivedQuantity = receivedQuantity,
    materialDescription = materialDescription,
    orderedQuantity = orderedQuantity,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    orderType = orderType,
    basicStartDate = basicStartDate,
    uom = uom
)

fun FinishedGoodsFeedProductionOrderDto.toDomain() = FinishedGoodsFeedProductionOrder(
    orderNumber = orderNumber,
    productionOrderItems = productionOrderItems.map { it.toDomain() }
)

fun net.gbs.gbswarehouse.data.remote.dto.response.GoodsReceiptLineStatusResult.toDomain() = DomainGoodsReceiptLineStatusResult(
    goodsReceiptLineId = goodsReceiptLineId,
    material = material,
    receivedQuantity = receivedQuantity,
    sapOutboxMessageId = sapOutboxMessageId,
    sapStatus = sapStatus,
    sapMaterialDocument = sapMaterialDocument,
    sapMaterialDocumentYear = sapMaterialDocumentYear,
    attemptCount = attemptCount,
    maxAttempts = maxAttempts,
    lastErrorMessage = lastErrorMessage
)

fun net.gbs.gbswarehouse.data.remote.dto.response.GoodsReceiptStatusResult.toDomain() = DomainGoodsReceiptStatusResult(
    goodsReceiptId = goodsReceiptId,
    receiptNo = receiptNo,
    receiptStatus = receiptStatus,
    readyForPutAway = readyForPutAway,
    needsAction = needsAction,
    hasOrphanedLines = hasOrphanedLines,
    lines = lines.map { it.toDomain() }
)

fun ReceiveRawMaterialsResultDto.toDomain() = ReceiveRawMaterialsResult(
    receiptStatus = receiptStatus?.toDomain(),
    purchaseOrder = purchaseOrder?.toDomain()
)

fun ReceivePackagingMaterialsResultDto.toDomain() = ReceivePackagingMaterialsResult(
    purchaseOrder = purchaseOrder?.toDomain()
)

fun ReceiveVendorPoultryMaterialsResultDto.toDomain() = ReceiveVendorPoultryMaterialsResult(
    receiptStatus = receiptStatus.toDomain(),
    purchaseOrder = purchaseOrder?.toDomain()
)



fun SparePartsReturnReservationLineDto.toDomain() = SparePartsReturnReservationLine(
    reservationNumber = reservationNumber,
    reservationItem = reservationItem,
    materialCode = materialCode,
    materialDescription = materialDescription ?: "",
    uom = uom,
    plantCode = plantCode ?: "",
    storageLocationCode = storageLocationCode,
    movementType = movementType,
    batch = batch,
    receivedQuantity = receivedQuantity,
    quantity = quantity ?: 0.0,
    remainingQuantity = remainingQuantity ?: 0.0,
    costCenterCode = costCenterCode,
    costCenterDescription = costCenterDescription,
    valuationTypes = valuationTypes?.toValuationTypes() ?: emptyList(),
    isBatchManaged = isBatchManaged ?: false,
    orderDescription = orderDescription ?: ""
)

fun List<SparePartsReturnReservationLineDto>.toDomain() = PostSparePartsReturnResult(
    reservationLines = map { it.toDomain() }
)

fun ReceiveFinishedGoodsPlasticResultDto.toDomain() = ReceiveFinishedGoodsPlasticResult(
    productionOrder = productionOrder?.toDomain()
)

fun ReceiveFinishedGoodsFeedResultDto.toDomain() = ReceiveFinishedGoodsFeedResult(
    productionOrder = productionOrder?.toDomain()
)

fun LoadingVendorDto.toDomain() = LoadingVendor(
    vendorCode = loadingVendorCode ?: "",
    vendorName = loadingVendorName ?: ""
)

fun PackagingMaterialsLine.toDto() = PackagingMaterialsLineDto(
    materialCode = materialCode,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    purchaseOrder = purchaseOrder,
    purchaseOrderItem = purchaseOrderItem,
    uom = uom,
    receivedQuantity = receivedQuantity,
    carNo = carNo,
    productionDate = productionDate
)

fun FinishedGoodsFeedLine.toDto() = FinishedGoodsFeedLineDto(
    materialCode = materialCode,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    orderNumber = orderNumber,
    itemNumber = itemNumber,
    uom = uom,
    receivedQuantity = receivedQuantity,
    wrokerGroupCode = workerGroupCode
)

fun FinishedGoodsPlasticLine.toDto() = FinishedGoodsPlasticLineDto(
    materialCode = materialCode,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    orderNumber = orderNumber,
    itemNumber = itemNumber,
    uom = uom,
    receivedQuantity = receivedQuantity,
    unloadingPoint = unloadingPoint,
    naolon = naolon,
    wrokerGroupCode = workerGroupCode,
    palletWeight = palletWeight
)

fun SparePartsPurchaseOrderItem.toPurchaseOrder() : SparePartsMaterial {
    return SparePartsMaterial(
        poItemNumber = poItemNumber,
        materialCode = materialCode ?: "",
        materialDescription = materialDescription ?: "",
        orderedQuantity = orderedQuantity?.toFloat() ?: 0f,
        receivedQuantity = receivedQuantity?.toFloat() ?: 0f,
        remainingQuantity = remainingQuantity?.toFloat() ?: 0f,
        uom = uom ?: "",
        plantCode = plantCode ?: "",
        isBatchManaged = isBatchManaged ?: false,
        valuationTypes = valuationTypes
    )
}

fun SparePartsPurchaseOrder.toPurchaseOrder() : PurchaseOrder {
    return PurchaseOrder(
        poNumber = poNumber,
        vendorCode = vendorCode ?: "",
        vendorName = vendorName ?: "",
        companyCode = companyCode,
        purchaseOrderItems = purchaseOrderItems.map { it.toPurchaseOrder() }
    )
}

fun GetSparePartsReturnReservationsByMovementTypeResultDto.toModel() : SparePartsReturnReservationOrder {
    return SparePartsReturnReservationOrder(
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
}

fun SparePartsReturnReservationOrder.toDto(): GetSparePartsReturnReservationsByMovementTypeResultDto{
    return GetSparePartsReturnReservationsByMovementTypeResultDto(
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
}

@JvmName("toSparePartsReturnReservationOrderModelList")
fun List<GetSparePartsReturnReservationsByMovementTypeResultDto>.toModel(): List<SparePartsReturnReservationOrder> {
    return this.map { it.toModel() }
}

@JvmName("toSparePartsReturnReservationOrderDtoList")
fun List<SparePartsReturnReservationOrder>.toDto(): List<GetSparePartsReturnReservationsByMovementTypeResultDto> {
    return this.map { it.toDto() }
}

fun MovementTypeDto.toModel(): MovementType {
    return MovementType(
        movementType = movementType,
        returnReason = returnReason,
        count = count,
        value = value
    )
}

fun MovementType.toDto(): MovementTypeDto {
    return MovementTypeDto(
        movementType = movementType,
        returnReason = returnReason,
        count = count
    )
}

@JvmName("toMovementTypeModelList")
fun List<MovementTypeDto>.toModel(): List<MovementType> {
    return this.map { it.toModel() }
}

@JvmName("toMovementTypeDtoList")
fun List<MovementType>.toDto(): List<MovementTypeDto> {
    return this.map { it.toDto() }
}

fun CarNumberDto.toModel(): CarNumber {
    return CarNumber(
        carNumberId = carNumberId,
        carNumber = carNumber,
        carDescription = carDescription
    )
}

fun CarNumber.toDto(): CarNumberDto {
    return CarNumberDto(
        carNumberId = carNumberId,
        carNumber = carNumber,
        carDescription = carDescription
    )
}

@JvmName("toCarNumberModelList")
fun List<CarNumberDto>.toModel(): List<CarNumber> {
    return this.map { it.toModel() }
}

@JvmName("toCarNumberDtoList")
fun List<CarNumber>.toDto(): List<CarNumberDto> {
    return this.map { it.toDto() }
}

fun CarTypeDto.toModel() : CarType {
    return CarType(
        vehicleTypeId = vehicleTypeId,
        vehicleTypeName = vehicleTypeName,
        description = description,
        isActive = isActive,
        createdBy = createdBy,
        createdAt = createdAt
    )
}

fun  CarType.toDto() : CarTypeDto {
    return CarTypeDto(
        vehicleTypeId = vehicleTypeId,
        vehicleTypeName = vehicleTypeName,
        description = description,
        isActive = isActive,
        createdBy = createdBy,
        createdAt = createdAt
    )
}

@JvmName("toCarTypeModelList")
fun List<CarTypeDto>.toModel() : List<CarType> {
    return this.map { it.toModel() }
}

@JvmName("toCarTypeDtoList")
fun List<CarType>.toDto() : List<CarTypeDto> {
    return this.map { it.toDto() }
}

fun NaolonDescriptionDto.toModel() : NaolonDescription {
    return NaolonDescription(
        naolonDescriptionId = naolonDescriptionId,
        naolonDescription = naolonDescription
    )
}

fun NaolonDescription.toDto() : NaolonDescriptionDto {
    return NaolonDescriptionDto(
        naolonDescriptionId = naolonDescriptionId,
        naolonDescription = naolonDescription
    )
}

@JvmName("toNaolonDescriptionModelList")
fun List<NaolonDescriptionDto>.toModel() : List<NaolonDescription> {
    return this.map { it.toModel() }
}

@JvmName("toNaolonDescriptionDtoList")
fun List<NaolonDescription>.toDto() : List<NaolonDescriptionDto> {
    return this.map { it.toDto() }
}

fun PackageTypeDto.toModel() : PackageType {
    return PackageType(
        packTypeId = packTypeId,
        packTypeCode = packTypeCode,
        packTypeDescription = packTypeDescription
    )
}

fun PackageType.toDto() : PackageTypeDto {
    return PackageTypeDto(
        packTypeId = packTypeId,
        packTypeCode = packTypeCode,
        packTypeDescription = packTypeDescription
    )
}

@JvmName("toPackageTypeModelList")
fun List<PackageTypeDto>.toModel() : List<PackageType> {
    return this.map { it.toModel() }
}

@JvmName("toPackageTypeDtoList")
fun List<PackageType>.toDto() : List<PackageTypeDto> {
    return this.map { it.toDto() }
}

fun PutAwayOrderDto.toDomain() = PutAwayOrder(
    orderNumber = orderNumber,
    receiptType = receiptType,
    itemCount = itemCount,
    earliestReceivedAt = earliestReceivedAt,
    materialCode = materialCode,
    materialDescription = materialDescription ?: materialName,
    remainingQuantity = remainingQuantity,
    carNo = carNo ?: carNumber,
    lines = lines.map { it.toDomain() }
)

fun PutAwayOrderLineDto.toDomain() = PutAwayOrderLine(
    goodsReceiptLineId = goodsReceiptLineId,
    goodsReceiptId = goodsReceiptId,
    receiptNo = receiptNo,
    receiptType = receiptType,
    materialCode = materialCode,
    materialDescription = materialDescription ?: materialName,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    purchaseOrder = purchaseOrder,
    purchaseOrderItem = purchaseOrderItem,
    uom = uom,
    receivedQuantity = receivedQuantity,
    putAwayQuantity = putAwayQuantity,
    remainingQuantity = remainingQuantity,
    batch = batch,
    expirationDate = expirationDate,
    receivedAt = receivedAt,
    carNo = carNo ?: carNumber
)

fun StorageBinLocationDto.toDomain() = StorageBinLocation(
    storageBinId = storageBinId,
    storageBinCode = storageBinCode,
    storageBinName = storageBinName,
    sectionId = sectionId,
    sectionCode = sectionCode,
    sectionName = sectionName,
    roadId = roadId,
    roadCode = roadCode,
    roadName = roadName,
    logisticsAreaId = logisticsAreaId,
    logisticsAreaCode = logisticsAreaCode,
    logisticsAreaName = logisticsAreaName,
    warehouseId = warehouseId,
    warehouseCode = warehouseCode,
    warehouseDescription = warehouseDescription,
    plantCode = plantCode
)

fun ReceivingOpenOrderCountsDto.toDomain() = ReceivingOpenOrderCounts(
    spareParts = spareParts,
    packagingMaterials = packagingMaterials,
    rawMaterials = rawMaterials,
    poultryMaterials = poultryMaterials,
    finishedGoodsPlastic = finishedGoodsPlastic,
    finishedGoodsFeed = finishedGoodsFeed,
    transitOrderMaterial = transitOrderMaterial,
    sparePartsReturn = sparePartsReturn,
    total = total
)

fun PutAwayOpenOrderCountsDto.toDomain() = PutAwayOpenOrderCounts(
    spareParts = spareParts,
    packagingMaterials = packagingMaterials,
    rawMaterials = rawMaterials,
    poultryMaterials = poultryMaterials,
    finishedGoodsPlastic = finishedGoodsPlastic,
    finishedGoodsFeed = finishedGoodsFeed,
    transitOrderMaterial = transitOrderMaterial,
    sparePartsReturn = sparePartsReturn,
    directTransfer = directTransfer,
    total = total
)

fun BlindReceiveMaterialDto.toDomain() = BlindReceiveMaterial(
    materialCode = materialCode,
    materialDescription = materialDescription,
    uom = uom,
    uomDescription = uomDescription
)

fun UomDto.toDomain() = Uom(
    code = code,
    description = description
)

fun QualityDecisionDto.toDomain() = QualityDecision(
    id = id.toString(),
    name = name
)

fun PlantDto.toDomain() = Plant(
    code = code,
    description = description
)

fun CreateBlindReceiveResultDto.toDomain() = CreateBlindReceiveResult(
    blindReceiptId = blindReceiptId,
    receiptNo = receiptNo,
    status = status
)

