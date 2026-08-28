package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class ScrapIssueLine(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("materialDescription") val materialDescription: String?=null,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("costCenterCode") val costCenterCode: String,
    @SerializedName("itemText") val itemText: String?
)
