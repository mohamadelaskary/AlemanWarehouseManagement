package net.gbs.gbswarehouse.data.remote.dto.request

data class PurchaseOrderRequestDto (
    val pageName: String,
    val companyCodes: List<String>,
    val poNumber: String,
)
