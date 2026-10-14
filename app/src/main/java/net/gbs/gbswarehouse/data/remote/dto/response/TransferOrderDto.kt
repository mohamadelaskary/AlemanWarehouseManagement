package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TransferOrderDto(
    @SerializedName("transferOrderNumber") val transferOrderNumber: String? = null,
    @SerializedName("issuingPlantDescription") val issuingPlantDescription: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
    @SerializedName("itemCount") val itemCount: Int? = null,
    @SerializedName("lines") val lines: List<TransferOrderItemDto> = emptyList()
)

@Serializable
data class TransferOrderItemDto(
    @SerializedName("poNumber") val poNumber: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null,
    @SerializedName("poItemNumber") val poItemNumber: String? = null,
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("orderedQuantity") val orderedQuantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("issuingPlantCode") val issuingPlantCode: String? = null,
    @SerializedName("issuingPlantDescription") val issuingPlantDescription: String? = null,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String? = null,
    @SerializedName("receivingPlantDescription") val receivingPlantDescription: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null
)