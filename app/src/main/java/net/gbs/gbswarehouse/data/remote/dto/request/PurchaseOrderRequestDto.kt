package net.gbs.gbswarehouse.data.remote.dto.request

data class PurchaseOrderRequestDto (
    val pageName: String,
    var companyCodes: List<String> = listOf(),
    val poNumber: String,
)
