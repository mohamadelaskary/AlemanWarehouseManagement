package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.request.PurchaseOrderRequestDto
import net.gbs.gbswarehouse.data.remote.dto.response.PurchaseOrderDto
import net.gbs.gbswarehouse.data.remote.dto.response.PurchaseOrderResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsMaterialDto
import net.gbs.gbswarehouse.data.remote.dto.response.ValuationTypeDto
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.PurchaseOrderRequest
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.ValuationType


fun PurchaseOrderRequestDto.toPurchaseOrderRequest(): PurchaseOrderRequest {
    return PurchaseOrderRequest(
        pageName = pageName,
        companyCodes = companyCodes,
        poNumber = poNumber,
    )
}

fun PurchaseOrderRequest.toPurchaseOrderRequestDto(): PurchaseOrderRequestDto {
    return PurchaseOrderRequestDto(
        pageName = pageName,
        companyCodes = companyCodes,
        poNumber = poNumber,
    )
}
fun ValuationTypeDto.toValuationType(): ValuationType {
    return ValuationType(
        type = type ?: "",
    )
}
fun List<ValuationTypeDto>.toValuationTypes(): List<ValuationType> {
    return map { it.toValuationType() }
}
fun SparePartsMaterialDto.toSparePartsMaterial(): SparePartsMaterial {
    return SparePartsMaterial(
        poItemNumber = poItemNumber ?: "",
        materialCode = materialCode ?: "",
        materialDescription = materialDescription ?: "",
        orderedQuantity = orderedQuantity ?: 0f,
        receivedQuantity = receivedQuantity ?: 0f,
        remainingQuantity = remainingQuantity ?: 0f,
        uom = uom ?: "",
        plantCode = plantCode ?: "",
        valuationTypes = valuationTypes?.toValuationTypes() ?: emptyList(),
        isBatchManaged = isBatchManaged,
    )
}
fun List<SparePartsMaterialDto>.toSparePartsMaterials(): List<SparePartsMaterial> {
    return map { it.toSparePartsMaterial() }
}
fun PurchaseOrderDto.toPurchaseOrder(): PurchaseOrder {
    return PurchaseOrder(
        poNumber = poNumber ?: "",
        vendorCode = vendorCode ?: "",
        vendorName = vendorName ?: "",
        companyCode = companyCode ?: "",
        purchaseOrderItems = purchaseOrderItems?.toSparePartsMaterials() ?: emptyList(),
    )
}
