package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.domain.model.*

fun InventoryOrderDto.toDomain() = InventoryOrder(
    inventoryOrderId = inventoryOrderId,
    orderNumber = orderNumber,
    description = description,
    warehouseCode = warehouseCode,
    plantCode = plantCode,
    status = status,
    assignedItemCount = assignedItemCount,
    scannedItemCount = scannedItemCount
)

fun InventoryAssignmentDto.toDomain() = InventoryAssignment(
    assignmentId = assignmentId,
    inventoryOrderId = inventoryOrderId,
    userId = userId,
    userName = userName,
    itemCode = itemCode,
    itemName = itemName,
    warehouseCode = warehouseCode,
    locator = locator,
    plantCode = plantCode,
    logisticsAreaId = logisticsAreaId,
    logisticsAreaName = logisticsAreaName,
    batch = batch,
    valuationType = valuationType,
    stockRecordKey = stockRecordKey,
    assignedDate = assignedDate,
    scanId = scanId,
    scannedAt = scannedAt,
    countedQty = countedQty
)

fun InventoryAssignmentPageDto.toDomain() = InventoryAssignmentPage(
    items = items.map { it.toDomain() },
    totalCount = totalCount,
    page = page,
    pageSize = pageSize,
    hasMore = hasMore
)

fun ScanInventoryItemResultDto.toDomain() = ScanInventoryItemResult(
    scanId = scanId,
    assignmentId = assignmentId,
    scannedAt = scannedAt,
    alreadyScanned = alreadyScanned,
    storageBinCode = storageBinCode,
    itemCode = itemCode,
    countedQty = countedQty
)
