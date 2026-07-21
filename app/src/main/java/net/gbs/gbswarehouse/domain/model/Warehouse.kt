package net.gbs.gbswarehouse.domain.model

data class Warehouse (
    val warehouseId: Long,
    val warehouseCode: String,
    val warehouseDescription: String,
    val plantCode: String,
)