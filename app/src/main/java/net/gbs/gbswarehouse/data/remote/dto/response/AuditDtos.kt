package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class InventoryOrderDto(
    @SerializedName("inventoryOrderId") val inventoryOrderId: Int,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("description") val description: String?,
    @SerializedName("warehouseCode") val warehouseCode: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("assignedItemCount") val assignedItemCount: Int?,
    @SerializedName("scannedItemCount") val scannedItemCount: Int?
)

data class InventoryAssignmentDto(
    @SerializedName("assignmentId") val assignmentId: Int,
    @SerializedName("inventoryOrderId") val inventoryOrderId: Int,
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("itemCode") val itemCode: String?,
    @SerializedName("itemName") val itemName: String?,
    @SerializedName("warehouseCode") val warehouseCode: String?,
    @SerializedName("locator") val locator: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("logisticsAreaId") val logisticsAreaId: Int?,
    @SerializedName("logisticsAreaName") val logisticsAreaName: String?,
    @SerializedName("batch") val batch: String?,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("stockRecordKey") val stockRecordKey: String?,
    @SerializedName("assignedDate") val assignedDate: String?,
    @SerializedName("scanId") val scanId: Int?,
    @SerializedName("scannedAt") val scannedAt: String?,
    @SerializedName("countedQty") val countedQty: Double?
)

data class InventoryAssignmentPageDto(
    @SerializedName("items") val items: List<InventoryAssignmentDto>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

data class ScanInventoryItemBodyDto(
    @SerializedName("inventoryOrderId") val inventoryOrderId: Int,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("itemCode") val itemCode: String,
    @SerializedName("batch") val batch: String?,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("countedQty") val countedQty: Double
)

data class ScanInventoryItemResultDto(
    @SerializedName("scanId") val scanId: Int,
    @SerializedName("assignmentId") val assignmentId: Int,
    @SerializedName("scannedAt") val scannedAt: String?,
    @SerializedName("alreadyScanned") val alreadyScanned: Boolean,
    @SerializedName("storageBinCode") val storageBinCode: String?,
    @SerializedName("itemCode") val itemCode: String?,
    @SerializedName("countedQty") val countedQty: Double?
)
