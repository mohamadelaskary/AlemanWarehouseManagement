package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.data.remote.mapper.toDomain
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssuingRepositoryImpl @Inject constructor(
    private val api: ApiService
) : IssuingRepository {

    override suspend fun getPlantWarehouses(plantCode: String): NetworkResult<List<SAPWarehouse>> {
        return safeApiCall { api.getPlantWarehousesIssuing(plantCode) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getMaterialStock(
        plantCode: String,
        materialCode: String,
        storageLocationCode: String?
    ): NetworkResult<List<SAPMaterialBatchValuationStock>> {
        return safeApiCall { api.getMaterialStock(plantCode, materialCode, storageLocationCode) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getScrapEligibleStock(): NetworkResult<List<SAPMaterialBatchValuationStock>> {
        return safeApiCall { api.getScrapEligibleStock() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getStorageBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation> {
        return safeApiCall { api.getStorageBinLocationIssuing(storageBinCode) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun getTransferToProductionReservations(): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getTransferToProductionReservations() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getTransferToProductionReservationLines(reservationNumber: String): NetworkResult<List<TransferToProductionReservationLine>> {
        return safeApiCall { api.getTransferToProductionReservationLines(reservationNumber) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun issueTransferToProduction(request: PostTransferToProductionDto): NetworkResult<PostTransferToProductionResult> {
        return safeApiCall { api.issueTransferToProduction(request) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun getSparePartsToMaintenanceReservations(): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getSparePartsToMaintenanceReservations() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getSparePartsToMaintenanceReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>> {
        return safeApiCall { api.getSparePartsToMaintenanceReservationLines(reservationNumber) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun issueSparePartsToMaintenance(request: PostSparePartsIssueToMaintenanceDto): NetworkResult<PostSparePartsIssueToMaintenanceResult> {
        return safeApiCall { api.issueSparePartsToMaintenance(request) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun getIssueToInternalOrderReservations(): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getIssueToInternalOrderReservations() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getIssueToInternalOrderReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>> {
        return safeApiCall { api.getIssueToInternalOrderReservationLines(reservationNumber) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun issueToInternalOrder(request: PostIssueToInternalOrderDto): NetworkResult<PostIssueToInternalOrderResult> {
        return safeApiCall { api.issueToInternalOrder(request) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun directTransfer(request: PostDirectTransferDto): NetworkResult<PostDirectTransferResult> {
        return safeApiCall { api.directTransfer(request) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun getDirectIssueOnCostCenterReservations(): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getDirectIssueOnCostCenterReservations() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getDirectIssueOnCostCenterReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>> {
        return safeApiCall { api.getDirectIssueOnCostCenterReservationLines(reservationNumber) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun directIssueOnCostCenter(request: PostDirectIssueOnCostCenterDto): NetworkResult<PostDirectIssueOnCostCenterResult> {
        return safeApiCall { api.directIssueOnCostCenter(request) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun getCompanies(): NetworkResult<List<Company>> {
        return safeApiCall { api.getCompanies() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getCostCenters(companyCode: String): NetworkResult<List<CostCenter>> {
        return safeApiCall { api.getCostCenters(companyCode) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun scrapIssue(request: PostScrapIssueDto): NetworkResult<PostScrapIssueResult> {
        return safeApiCall { api.scrapIssue(request) }
            .map { it.data!!.toDomain() }
    }
}
