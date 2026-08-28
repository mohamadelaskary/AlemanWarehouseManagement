package net.gbs.gbswarehouse.domain.model

data class SparePartsMaterial(
    val poItemNumber: String,
    val materialCode: String,
    val materialDescription: String,
    val orderedQuantity: Float,
    val receivedQuantity: Float,
    val remainingQuantity: Float,
    val uom: String,
    val plantCode: String,
    val valuationTypes: List<ValuationType>,
    val isBatchManaged: Boolean,
)
