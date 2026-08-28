package net.gbs.gbswarehouse.domain.model

data class SparePartsLine(
    val materialCode: String,
    val materialDescription: String,
    val plantCode: String,
    val warehouseCode: String,
    val purchaseOrder: String,
    val purchaseOrderItem: String,
    val uom: String,
    val receivedQuantity: Float,
    val valuationType: String?=null,
    val isBatchManaged: Boolean,
    val expiryDate: String?=null,
)
