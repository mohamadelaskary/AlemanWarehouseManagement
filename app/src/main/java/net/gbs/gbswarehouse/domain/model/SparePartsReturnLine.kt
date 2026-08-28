package net.gbs.gbswarehouse.domain.model

data class SparePartsReturnLine(
    val materialCode: String = "",
    val materialDescription: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val reservationNumber: String = "",
    val reservationItem: String = "",
    val returnReason: String?=null,
    val valuationType: String? = null,
    val itemText: String? = null,
    val recipientName: String? = null
)
