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
    @SerializedName("itemText") val itemText: String?,
    /** Batch / valuation type of the stock row this line was issued from, so edit can restore it. */
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("valuationType") val valuationType: String? = null,
    /** Company is required to add a line, so keep it too rather than making the user re-pick it. */
    @SerializedName("companyCode") val companyCode: String? = null
)
