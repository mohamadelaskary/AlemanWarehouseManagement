package net.gbs.gbswarehouse.domain.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.PurchaseOrderRequestDto
import net.gbs.gbswarehouse.data.remote.dto.request.ReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.data.remote.dto.response.MobileResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.PlantWarehousesResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.PurchaseOrderResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.WorkerGroupsResponseDto

interface ReceivingRepository {

    suspend fun getPlantWarehouses(
        plantCode: String
    ):
            NetworkResult<PlantWarehousesResponseDto>

    suspend fun getAllWorkerGroups():
            NetworkResult<WorkerGroupsResponseDto>

    suspend fun getPurchaseOrderData(
        request: PurchaseOrderRequestDto
    ): NetworkResult<PurchaseOrderResponseDto>

    suspend fun receiveVendorSpareParts(
        request: ReceiveVendorSparePartsRequestDto
    ): NetworkResult<MobileResponseDto>

}
