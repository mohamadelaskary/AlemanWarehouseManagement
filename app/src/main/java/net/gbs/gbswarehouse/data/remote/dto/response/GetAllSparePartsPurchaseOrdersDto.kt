package net.gbs.gbswarehouse.data.remote.dto.response

data class GetAllSparePartsPurchaseOrdersDto (
    override val responseStatus: ResponseStatus,
    val data: List<PurchaseOrderDto>?,
) : BaseResponse
