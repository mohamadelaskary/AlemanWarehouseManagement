package net.gbs.gbswarehouse.domain.model

data class PackagingMaterialsLine(
    val materialCode: String,
    val materialDescription: String,
    val plantCode: String,
    val warehouseCode: String,
    val purchaseOrder: String,
    val purchaseOrderItem: String,
    val uom: String,
    val receivedQuantity: Double,
    val carNo: String? = null
)
