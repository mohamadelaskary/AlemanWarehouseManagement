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
    val workerGroupName: String? = null,
    val storageBinCode:String?=null,
    /** Batch / valuation type of the stock row this line was issued from, so edit can restore it. */
    val batch: String? = null,
    val valuationType: String? = null,
)
