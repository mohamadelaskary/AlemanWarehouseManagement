package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.request.*
import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.data.remote.mapper.toDirectIssueOnCostCenterResult
import net.gbs.gbswarehouse.data.remote.mapper.toDomain
import net.gbs.gbswarehouse.data.remote.mapper.toIssueToInternalOrderResult
import net.gbs.gbswarehouse.data.remote.mapper.toModel
import net.gbs.gbswarehouse.data.remote.mapper.toSparePartsIssueToMaintenanceResult
import net.gbs.gbswarehouse.data.remote.mapper.toTransferToProductionResult
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.domain.model.GoodsReceiptStatusResult
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

    override suspend fun getMaterialOnHandStock(
        storageBinCode: String,
        materialCode: String?
    ): NetworkResult<List<MaterialOnHandStock>> {
        return safeApiCall { api.getMaterialOnHandStock(storageBinCode, materialCode) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getScrapEligibleStock(): NetworkResult<List<SAPMaterialBatchValuationStock>> {
        return safeApiCall { api.getScrapEligibleStock() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getStorageBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation> {
        return safeApiCall { api.getStorageBinLocationIssuing(storageBinCode) }
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
            .map { response -> (response.data ?: emptyList()).toTransferToProductionResult() }
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
            .map { response -> (response.data ?: emptyList()).toSparePartsIssueToMaintenanceResult() }
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
            .map { response -> (response.data ?: emptyList()).toIssueToInternalOrderResult() }
    }

    override suspend fun directTransfer(request: PostDirectTransferDto): NetworkResult<PostDirectTransferResult> {
        return safeApiCall { api.directTransfer(request) }
            .map { response ->
                response.data?.toDomain() ?: PostDirectTransferResult(
                    receiptStatus = GoodsReceiptStatusResult(
                        goodsReceiptId = 0,
                        receiptNo = "",
                        receiptStatus = "",
                        readyForPutAway = false,
                        needsAction = false,
                        hasOrphanedLines = false,
                        lines = emptyList<net.gbs.gbswarehouse.domain.model.GoodsReceiptLineStatusResult>()
                    )
                )
            }
    }

    override suspend fun getDirectIssueOnCostCenterReservations(): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getDirectIssueOnCostCenterReservations() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getDirectIssueOnCostCenterReservationLines(reservationNumber: String): NetworkResult<List<DirectIssueReservationLine>> {
        return safeApiCall { api.getDirectIssueOnCostCenterReservationLines(reservationNumber) }
            .map { response -> response.data?.map { it.toModel() } ?: emptyList() }
    }

    override suspend fun directIssueOnCostCenter(request: PostDirectIssueOnCostCenterDto): NetworkResult<PostDirectIssueOnCostCenterResult> {
        return safeApiCall { api.directIssueOnCostCenter(request) }
            .map { response -> (response.data ?: emptyList()).toDirectIssueOnCostCenterResult() }
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
            .map { response ->
                response.data?.toDomain() ?: PostScrapIssueResult(
                    receiptStatus = net.gbs.gbswarehouse.domain.model.GoodsReceiptStatusResult(
                        goodsReceiptId = 0,
                        receiptNo = "",
                        receiptStatus = "",
                        readyForPutAway = false,
                        needsAction = false,
                        hasOrphanedLines = false,
                        lines = emptyList<net.gbs.gbswarehouse.domain.model.GoodsReceiptLineStatusResult>()
                    )
                )
            }
    }

    override suspend fun getFinishedGoodsFeedOrdersByCarNumber(): NetworkResult<List<FinishedGoodsFeedOrder>> {
        return safeApiCall { api.getFinishedGoodsFeedOrdersByCarNumber() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getFinishedGoodsFeedOrderLinesByCarNumber(carNo: String): NetworkResult<FinishedGoodsFeedOrder> {
        return safeApiCall { api.getFinishedGoodsFeedOrderLinesByCarNumber(GetFinishedGoodsFeedOrderLinesByCarNumberRequestDto(carNo)) }
            .map { response -> response.data!!.toDomain() }
    }

    override suspend fun issueFinishedGoodsFeed(request: IssueFinishedGoodsFeedBodyDto): NetworkResult<List<FinishedGoodsFeedItem>> {
        return safeApiCall { api.issueFinishedGoodsFeed(request) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getTransferOrdersByCarNumber(): NetworkResult<List<TransferOrder>> {
        return safeApiCall { api.getTransferOrdersByCarNumber() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getTransferOrderDataByCarNumber(carNo: String): NetworkResult<TransferOrder> {
        return safeApiCall { api.getTransferOrderDataByCarNumber(GetTransferOrderDataByCarNumberRequestDto(carNo)) }
            .map { it.data!!.toDomain() }
    }

    override suspend fun issueTransferOrder(request: IssueTransferOrderDto): NetworkResult<PostTransferOrderResult> {
        return safeApiCall { api.issueTransferOrder(request) }
            .map { response ->
                PostTransferOrderResult(
                    transferOrder = response.data?.toDomain()
                )
            }
    }

    override suspend fun getDirectTransferPutAwayDocuments(): NetworkResult<List<DirectTransferPutAwayDocument>> {
        return safeApiCall { api.getDirectTransferPutAwayDocuments() }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getDirectTransferPutAwayLines(receiptNo: String): NetworkResult<List<DirectTransferPutAwayLine>> {
        return safeApiCall { api.getDirectTransferPutAwayLines(receiptNo) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun confirmDirectTransferPutAway(request: ConfirmDirectTransferPutAwayRequestDto): NetworkResult<List<DirectTransferPutAwayLine>> {
        return safeApiCall { api.confirmDirectTransferPutAway(request) }
            .map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getIssuingOpenOrderCounts(): NetworkResult<IssuingOpenOrderCounts> {
        return safeApiCall { api.getIssuingOpenOrderCounts() }
            .map { response -> response.data?.toDomain() ?: IssuingOpenOrderCounts() }
    }
}
