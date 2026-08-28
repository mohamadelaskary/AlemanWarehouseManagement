package net.gbs.gbswarehouse.data.remote.dto.response

data class SparePartsPurchaseOrderDataDto (
    override val responseStatus: ResponseStatus,
    val data: PurchaseOrderDto?,
) : BaseResponse