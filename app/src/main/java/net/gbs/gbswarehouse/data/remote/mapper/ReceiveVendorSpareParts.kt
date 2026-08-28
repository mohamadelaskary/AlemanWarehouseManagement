package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.request.LineDto
import net.gbs.gbswarehouse.data.remote.dto.request.ReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.data.remote.mapper.toLineDtos
import net.gbs.gbswarehouse.domain.model.ReceiveVendorSparePartsRequest
import net.gbs.gbswarehouse.domain.model.SparePartsLine

fun LineDto.toSparePartsLine(): SparePartsLine{
    return SparePartsLine(
        materialCode = materialCode,
        materialDescription = materialDescription,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        valuationType = valuationType,
        expiryDate = expirationDate,
        isBatchManaged = isBatchManaged,
    )
}

fun List<LineDto>.toSparePartsLines(): List<SparePartsLine>{
    return map { it.toSparePartsLine() }
}

fun SparePartsLine.toLineDto(): LineDto{
    return LineDto(
        materialCode = materialCode,
        plantCode = plantCode,
        warehouseCode = warehouseCode,
        purchaseOrder = purchaseOrder,
        purchaseOrderItem = purchaseOrderItem,
        uom = uom,
        receivedQuantity = receivedQuantity,
        valuationType = valuationType,
        expirationDate = expiryDate,
        isBatchManaged = isBatchManaged,
        materialDescription = materialDescription,
    )
}

fun List<SparePartsLine>.toLineDtos(): List<LineDto>{
    return map { it.toLineDto() }
}

fun ReceiveVendorSparePartsRequest.toReceiveVendorSparePartsRequestDto(): ReceiveVendorSparePartsRequestDto {
    return ReceiveVendorSparePartsRequestDto(
        userId = userId,
        companyCodes = companyCodes,
        poNumber = poNumber,
        lines = lines.toLineDtos(),
    )
}

fun ReceiveVendorSparePartsRequestDto.toReceiveVendorSparePartsRequest(): ReceiveVendorSparePartsRequest {
    return ReceiveVendorSparePartsRequest(
        userId = userId,
        companyCodes = companyCodes,
        poNumber = poNumber,
        lines = lines.toSparePartsLines(),
    )
}