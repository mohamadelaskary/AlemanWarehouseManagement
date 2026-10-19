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
    
    suspend fun getMaterialOnHandStock(
        storageBinCode: String,
        materialCode: String?
    ): NetworkResult<List<MaterialOnHandStock>>
    
    suspend fun getScrapEligibleStock(): NetworkResult<List<SAPMaterialBatchValuationStock>>
    
    suspend fun getMaterialBinLocations(
        plantCode: String,
        warehouseCode: String,
        materialCode: String,
        valuationType: String
    ): NetworkResult<List<MaterialBinLocation>>
    
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
    suspend fun getDirectIssueOnCostCenterReservationLines(reservationNumber: String): NetworkResult<List<DirectIssueReservationLine>>
    suspend fun directIssueOnCostCenter(request: PostDirectIssueOnCostCenterDto): NetworkResult<PostDirectIssueOnCostCenterResult>

    // Scrap issue
    suspend fun scrapIssue(request: PostScrapIssueDto): NetworkResult<PostScrapIssueResult>

    // Finished goods feed
    suspend fun getFinishedGoodsFeedOrdersByCarNumber(): NetworkResult<List<FinishedGoodsFeedOrder>>
    suspend fun getFinishedGoodsFeedOrderLinesByCarNumber(carNo: String): NetworkResult<FinishedGoodsFeedOrder>
    suspend fun issueFinishedGoodsFeed(request: IssueFinishedGoodsFeedBodyDto): NetworkResult<List<FinishedGoodsFeedItem>>

    // Transfer orders
    suspend fun getTransferOrdersByCarNumber(): NetworkResult<List<TransferOrder>>
    suspend fun getTransferOrderDataByCarNumber(carNo: String): NetworkResult<TransferOrder>
    suspend fun issueTransferOrder(request: IssueTransferOrderDto): NetworkResult<PostTransferOrderResult>

    // Direct transfer put away
    suspend fun getDirectTransferPutAwayDocuments(): NetworkResult<List<DirectTransferPutAwayDocument>>
    suspend fun getDirectTransferPutAwayLines(receiptNo: String): NetworkResult<List<DirectTransferPutAwayLine>>
    suspend fun confirmDirectTransferPutAway(request: ConfirmDirectTransferPutAwayRequestDto): NetworkResult<List<DirectTransferPutAwayLine>>

    // Open order counts
    suspend fun getIssuingOpenOrderCounts(): NetworkResult<IssuingOpenOrderCounts>
}
