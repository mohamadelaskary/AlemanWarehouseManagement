package net.gbs.gbswarehouse.domain.model

data class FinishedGoodsFeedLine(
    val materialCode: String,
    val materialDescription: String,
    val plantCode: String,
    val warehouseCode: String,
    val orderNumber: String,
    val itemNumber: String,
    val uom: String,
    val receivedQuantity: Double,
    val workerGroupCode: String? = null,
    val workerGroupName: String? = null
)
