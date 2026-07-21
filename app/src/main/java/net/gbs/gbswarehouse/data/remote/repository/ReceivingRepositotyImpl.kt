package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.request.PurchaseOrderRequestDto
import net.gbs.gbswarehouse.data.remote.dto.request.ReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceivingRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ReceivingRepository {

    override suspend fun getPlantWarehouses(plantCode: String) =
        safeApiCall {
            api.getPlantWarehouses(plantCode = plantCode)
        }

    override suspend fun getAllWorkerGroups() =
        safeApiCall {
            api.getAllWorkerGroups()
        }

    override suspend fun getPurchaseOrderData(
        request: PurchaseOrderRequestDto
    ) =
        safeApiCall {
            api.getPurchaseOrderData(request)
        }

    override suspend fun receiveVendorSpareParts(
        request: ReceiveVendorSparePartsRequestDto
    ) =
        safeApiCall {
            api.receiveVendorSpareParts(request)
        }

}