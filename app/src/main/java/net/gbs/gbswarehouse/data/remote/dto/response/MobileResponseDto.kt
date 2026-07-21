package net.gbs.gbswarehouse.data.remote.dto.response

data class MobileResponseDto (
    override val responseStatus: ResponseStatus,
    val data: PurchaseOrderDto?
) : BaseResponse
