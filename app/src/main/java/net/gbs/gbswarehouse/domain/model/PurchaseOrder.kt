package net.gbs.gbswarehouse.domain.model

data class PurchaseOrder(
    val poNumber: String,
    val vendorCode: String,
    val vendorName: String,
    val companyCode: String,
    val purchaseOrderItems: List<SparePartsMaterial>,
)
