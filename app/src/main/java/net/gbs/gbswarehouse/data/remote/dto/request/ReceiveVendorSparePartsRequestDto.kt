package net.gbs.gbswarehouse.data.remote.dto.request

data class ReceiveVendorSparePartsRequestDto (
    var userId: String? = null,
    var companyCodes: List<String> = listOf(),
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
    val valuationType: String?=null,
    val expirationDate: String?=null,
    val isBatchManaged : Boolean,
)