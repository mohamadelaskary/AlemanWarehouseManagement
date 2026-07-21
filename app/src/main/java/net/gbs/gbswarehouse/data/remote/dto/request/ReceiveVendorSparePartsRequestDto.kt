package net.gbs.gbswarehouse.data.remote.dto.request

data class ReceiveVendorSparePartsRequestDto (
    val userId: String,
    val companyCodes: List<String>,
    val poNumber: String,
    val lines: List<LineDto>,
)

data class LineDto(
    val materialCode: String,
    val materialDescription: String,
    val plantCode: String,
    val warehouseCode: String,
    val purchaseOrder: String,
    val purchaseOrderItem: String,
    val uom: String,
    val receivedQuantity: Float,
    val valuationType: String,
    val expiryDate: String,
)