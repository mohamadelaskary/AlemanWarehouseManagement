package net.gbs.gbswarehouse.data.remote.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateBlindReceiveRequestDto(
    @SerializedName("materialCode") val materialCode: String?,
    @SerializedName("materialDescription") val materialDescription: String,
    @SerializedName("vendorName") val vendorName: String?,
    @SerializedName("workerGroupCode") val workerGroupCode: String?,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("uom") val uom: String,
    @SerializedName("qualityDecisionId") val qualityDecisionId: Int?,
    @SerializedName("purchasingOfficer") val purchasingOfficer: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("warehouseCode") val warehouseCode: String?
)
