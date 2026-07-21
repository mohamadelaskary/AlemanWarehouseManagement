package net.gbs.gbswarehouse.data.remote.dto.response

data class PurchaseOrderResponseDto (
    override val responseStatus: ResponseStatus,
    val data: PurchaseOrderDto?,
) : BaseResponse


data class PurchaseOrderDto(
    val poNumber: String?,
    val vendorCode: String?,
    val vendorName: String?,
    val companyCode: String?,
    val purchaseOrderItems: List<SparePartsMaterialDto>?,
)

data class SparePartsMaterialDto(
    val poItemNumber: String?,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Float?,
    val receivedQuantity: Float?,
    val remainingQuantity: Float?,
    val uom: String?,
    val plantCode: String?,
    val valuationTypes: List<ValuationTypeDto>?,
)

data class ValuationTypeDto(
    val type: String?,
)
