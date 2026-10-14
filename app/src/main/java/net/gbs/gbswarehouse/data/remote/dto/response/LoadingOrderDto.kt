package net.gbs.gbswarehouse.data.remote.dto.response

/**
 * Response of `api/Conveyors/GetLoadingOrders`.
 *
 * There is no single-order endpoint, so the list response is the only source of
 * order data; the clicked order is reused from the object already fetched.
 */
data class LoadingOrderDto(
    val goodsReceiptId: Int = 0,
    val receiptNo: String = "",
    val status: String? = null,
    val shippingPointCode: String? = null,
    val carNo: String = "",
    val workerGroupCode: String? = null,
    val createdAt: String? = null,
    val orderType: String? = null,
    val lines: List<LoadingOrderLineDto> = emptyList()
)

data class LoadingOrderLineDto(
    val goodsReceiptLineId: Int = 0,
    val salesOrder: String? = null,
    val salesOrderItem: String? = null,
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val itemText: String? = null,
    val quantity: Double = 0.0,
    val uom: String? = null,
    val plantCode: String? = null,
    val loadedBags: Int = 0,
    val bagLengthMm: Int = 0,
    val bagWeightKg: Double = 0.0,
    val suggestedBags: Int = 0,
    val remainingSuggestedBags: Int = 0
)
