package net.gbs.gbswarehouse.data.remote.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class IssueTransferOrderDto(
    @SerializedName("poNumber") val poNumber: String = "",
    @SerializedName("destination") val destination: String = "",
    @SerializedName("lines") val lines: List<IssueTransferOrderLineDto> = emptyList()
)

@Serializable
data class IssueTransferOrderLineDto(
    @SerializedName("materialCode") val materialCode: String = "",
    @SerializedName("plantCode") val plantCode: String = "",
    @SerializedName("warehouseCode") val warehouseCode: String = "",
    @SerializedName("receivingPlantCode") val receivingPlantCode: String = "",
    @SerializedName("purchaseOrder") val purchaseOrder: String = "",
    @SerializedName("purchaseOrderItem") val purchaseOrderItem: String = "",
    @SerializedName("uom") val uom: String = "",
    @SerializedName("quantity") val quantity: Double = 0.0,
    @SerializedName("storageBinCode") val storageBinCode: String = "",
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("valuationType") val valuationType: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("unloadingPoint") val unloadingPoint: String? = null,
    @SerializedName("workerGroupCode") val workerGroupCode: String? = null
)
