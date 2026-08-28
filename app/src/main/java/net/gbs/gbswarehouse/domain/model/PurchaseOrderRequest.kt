package net.gbs.gbswarehouse.domain.model

data class PurchaseOrderRequest(
    val pageName: String,
    val companyCodes: List<String> = listOf(),
    val poNumber: String,
)
