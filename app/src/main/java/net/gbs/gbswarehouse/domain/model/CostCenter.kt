package net.gbs.gbswarehouse.domain.model

data class CostCenter (
    val costCenterId: Int,
    val costCenterCode: String,
    val costCenterDescription: String,
    val companyCode: String,
    val companyDescription: String
)