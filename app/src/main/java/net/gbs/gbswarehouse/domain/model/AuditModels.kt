package net.gbs.gbswarehouse.domain.model

data class InventoryOrder(
    val inventoryOrderId: Int,
    val orderNumber: String,
    val description: String?,
    val warehouseCode: String?,
    val plantCode: String?,
    val status: String?,
    val assignedItemCount: Int?,
    val scannedItemCount: Int?
)

data class InventoryAssignment(
    val assignmentId: Int,
    val inventoryOrderId: Int,
    val userId: String?,
    val userName: String?,
    val itemCode: String?,
    val itemName: String?,
    val warehouseCode: String?,
    val locator: String?,
    val plantCode: String?,
    val logisticsAreaId: Int?,
    val logisticsAreaName: String?,
    val batch: String?,
    val valuationType: String?,
    val stockRecordKey: String?,
    val assignedDate: String?,
    val scanId: Int?,
    val scannedAt: String?,
    val countedQty: Double?,
    val valuationTypes: List<ValuationType> = emptyList(),
    val batches: List<String> = emptyList()
)

data class InventoryAssignmentPage(
    val items: List<InventoryAssignment>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
)

data class ScanInventoryItemResult(
    val scanId: Int,
    val assignmentId: Int,
    val scannedAt: String?,
    val alreadyScanned: Boolean,
    val storageBinCode: String?,
    val itemCode: String?,
    val countedQty: Double?
)
