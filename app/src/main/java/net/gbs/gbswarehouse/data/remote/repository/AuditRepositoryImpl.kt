package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.ScanInventoryItemBodyDto
import net.gbs.gbswarehouse.data.remote.mapper.toDomain
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.domain.repository.AuditRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AuditRepository {

    override suspend fun getMyInventoryOrders(): NetworkResult<List<InventoryOrder>> {
        return safeApiCall { api.getMyInventoryOrders() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getMyAssignments(
        orderId: Int,
        filter: String?,
        page: Int,
        pageSize: Int
    ): NetworkResult<InventoryAssignmentPage> {
        return safeApiCall { api.getMyAssignments(orderId, filter, page, pageSize) }
            .map { response ->
                response.data?.toDomain() ?: InventoryAssignmentPage(
                    items = emptyList(),
                    totalCount = 0,
                    page = page,
                    pageSize = pageSize,
                    hasMore = false
                )
            }
    }

    override suspend fun getBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation> {
        return safeApiCall { api.getBinLocation(storageBinCode) }
            .map { response ->
                response.data?.toDomain() ?: StorageBinLocation(
                    storageBinId = 0,
                    storageBinCode = "",
                    storageBinName = "",
                    sectionId = 0,
                    sectionCode = "",
                    sectionName = "",
                    roadId = 0,
                    roadCode = "",
                    roadName = "",
                    logisticsAreaId = 0,
                    logisticsAreaCode = "",
                    logisticsAreaName = "",
                    warehouseId = 0,
                    warehouseCode = "",
                    warehouseDescription = "",
                    plantCode = ""
                )
            }
    }

    override suspend fun scanInventoryItem(body: ScanInventoryItemBodyDto): NetworkResult<ScanInventoryItemResult> {
        return safeApiCall { api.scanInventoryItem(body) }
            .map { response ->
                response.data?.toDomain() ?: ScanInventoryItemResult(
                    scanId = 0,
                    assignmentId = 0,
                    scannedAt = null,
                    alreadyScanned = false,
                    storageBinCode = null,
                    itemCode = null,
                    countedQty = null
                )
            }
    }
}
