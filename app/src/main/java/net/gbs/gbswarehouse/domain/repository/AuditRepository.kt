package net.gbs.gbswarehouse.domain.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ScanInventoryItemBodyDto
import net.gbs.gbswarehouse.domain.model.InventoryAssignmentPage
import net.gbs.gbswarehouse.domain.model.InventoryOrder
import net.gbs.gbswarehouse.domain.model.ScanInventoryItemResult
import net.gbs.gbswarehouse.domain.model.StorageBinLocation

interface AuditRepository {
    suspend fun getMyInventoryOrders(): NetworkResult<List<InventoryOrder>>
    suspend fun getMyAssignments(orderId: Int, filter: String? = null, page: Int = 1, pageSize: Int = 50): NetworkResult<InventoryAssignmentPage>
    suspend fun getBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation>
    suspend fun scanInventoryItem(body: ScanInventoryItemBodyDto): NetworkResult<ScanInventoryItemResult>
}
