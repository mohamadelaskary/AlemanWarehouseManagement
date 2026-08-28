package net.gbs.gbswarehouse.domain.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.*
import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.domain.model.*

interface IssuingRepository {
    suspend fun getPlantWarehouses(plantCode: String): NetworkResult<List<SAPWarehouse>>
    suspend fun getCompanies(): NetworkResult<List<Company>>
    suspend fun getCostCenters(companyCode: String): NetworkResult<List<CostCenter>>
    
    suspend fun getMaterialStock(
        plantCode: String,
        materialCode: String,
        storageLocationCode: String? = null
    ): NetworkResult<List<SAPMaterialBatchValuationStock>>
    
    suspend fun getScrapEligibleStock(): NetworkResult<List<SAPMaterialBatchValuationStock>>
    
    suspend fun getStorageBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation>
    
    // Transfer to Production
    suspend fun getTransferToProductionReservations(): NetworkResult<List<SparePartsReturnReservationOrder>>
    suspend fun getTransferToProductionReservationLines(reservationNumber: String): NetworkResult<List<TransferToProductionReservationLine>>
    suspend fun issueTransferToProduction(request: PostTransferToProductionDto): NetworkResult<PostTransferToProductionResult>
    
    // Spare parts to maintenance
    suspend fun getSparePartsToMaintenanceReservations(): NetworkResult<List<SparePartsReturnReservationOrder>>
    suspend fun getSparePartsToMaintenanceReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>>
    suspend fun issueSparePartsToMaintenance(request: PostSparePartsIssueToMaintenanceDto): NetworkResult<PostSparePartsIssueToMaintenanceResult>
    
    // Issue to internal order
    suspend fun getIssueToInternalOrderReservations(): NetworkResult<List<SparePartsReturnReservationOrder>>
    suspend fun getIssueToInternalOrderReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>>
    suspend fun issueToInternalOrder(request: PostIssueToInternalOrderDto): NetworkResult<PostIssueToInternalOrderResult>
    
    // Direct transfer
    suspend fun directTransfer(request: PostDirectTransferDto): NetworkResult<PostDirectTransferResult>
    
    // Direct issue on cost center
    suspend fun getDirectIssueOnCostCenterReservations(): NetworkResult<List<SparePartsReturnReservationOrder>>
    suspend fun getDirectIssueOnCostCenterReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>>
    suspend fun directIssueOnCostCenter(request: PostDirectIssueOnCostCenterDto): NetworkResult<PostDirectIssueOnCostCenterResult>

    // Scrap issue
    suspend fun scrapIssue(request: PostScrapIssueDto): NetworkResult<PostScrapIssueResult>
}
