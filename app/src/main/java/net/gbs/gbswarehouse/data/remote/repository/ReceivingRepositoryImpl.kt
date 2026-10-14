package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.local.LoginManager
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.request.*
import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.data.remote.mapper.*
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import net.gbs.gbswarehouse.QualityDecision
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceivingRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val loginManager: LoginManager,
) : ReceivingRepository {

    override suspend fun getPlantWarehouses(plantCode: String): NetworkResult<List<Warehouse>> {
        return safeApiCall { api.getPlantWarehouses(plantCode) }.map { it.data?.toWarehouses() ?: emptyList() }
    }

    override suspend fun getAllWorkerGroups(): NetworkResult<List<WorkersGroup>> {
        return safeApiCall { api.getAllWorkerGroups() }.map { it.data?.toWorkersGroups() ?: emptyList() }
    }

    override suspend fun getAllLoadingVendors(): NetworkResult<List<LoadingVendor>> {
        return safeApiCall { api.getAllLoadingVendors() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getAllCarNumbers(): NetworkResult<List<CarNumber>> {
        return safeApiCall { api.getAllCarNumbers() }.map { it.data?.toModel() ?: emptyList() }
    }

    override suspend fun getAllCarTypes(): NetworkResult<List<CarType>> {
        return safeApiCall { api.getAllCarTypes() }.map { it.data?.toModel() ?: emptyList() }
    }

    override suspend fun getNaolonPerTonValue(
        plantCode: String,
        naolonDescription: String,
        carType: String
    ): NetworkResult<NaolonPerTon> {
        return safeApiCall { api.getNaolonPerTonValue(plantCode, naolonDescription, carType) }.map { it.data!! }
    }

    override suspend fun getAllNaolonDescriptions(): NetworkResult<List<NaolonDescription>> {
        return safeApiCall { api.getAllNaolonDescriptions() }.map { it.data?.toModel() ?: emptyList() }
    }

    override suspend fun getAllPackageTypes(): NetworkResult<List<PackageType>> {
        return safeApiCall { api.getAllPackageTypes() }.map { it.data?.toModel() ?: emptyList() }
    }

    // Spare parts
    override suspend fun getReceivingSparePartsFromVendorOrdersList(): NetworkResult<List<SparePartsPurchaseOrder>> {
        return safeApiCall { api.getAllSparePartsPurchaseOrders() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getReceivingSparePartsFromVendorOrderData(request: GetSparePartsPurchaseOrderDataRequest): NetworkResult<PurchaseOrder> {
        return safeApiCall { api.getSparePartsPurchaseOrderData(request) }.map { it.data!!.toPurchaseOrder() }
    }

    override suspend fun receiveVendorSpareParts(request: ReceiveVendorSparePartsRequestDto): NetworkResult<PurchaseOrder> {
        request.userId = loginManager.getUserId()
        request.companyCodes = loginManager.getUserCompanies()
        return safeApiCall { api.receiveVendorSpareParts(request) }.map { it.data!!.toPurchaseOrder() }
    }

    // Raw materials
    override suspend fun getAllRawMaterialsPurchaseOrdersByCarNumber(): NetworkResult<List<RawMaterialsPurchaseOrder>> {
        return safeApiCall { api.getAllRawMaterialsPurchaseOrdersByCarNumber() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getRawMaterialsPurchaseOrderData(carNo: String): NetworkResult<RawMaterialsPurchaseOrder> {
        return safeApiCall { api.getRawMaterialsPurchaseOrderData(GetRawMaterialsPurchaseOrderDataRequestDto(carNo)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receiveRawMaterials(request: ReceiveRawMaterialsDto): NetworkResult<ReceiveRawMaterialsResult> {
        return safeApiCall { api.receiveRawMaterials(request) }.map { it.data!!.toDomain() }
    }

    // Packaging materials
    override suspend fun getAllPackagingMaterialsPurchaseOrders(): NetworkResult<List<PackagingMaterialsPurchaseOrder>> {
        return safeApiCall { api.getAllPackagingMaterialsPurchaseOrdersByCarNumber() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getPackagingMaterialsPurchaseOrderData(carNo: String): NetworkResult<PackagingMaterialsPurchaseOrder> {
        return safeApiCall { api.getPackagingMaterialsPurchaseOrderData(GetPackagingMaterialsPurchaseOrderDataRequestDto(carNo)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receivePackagingMaterials(request: ReceivePackagingMaterialsDto): NetworkResult<ReceivePackagingMaterialsResult> {
        return safeApiCall { api.receivePackagingMaterials(request) }.map { it.data!!.toDomain() }
    }

    override suspend fun receivePackagingMaterialsFromDiffPOs(request: ReceivePackagingMaterialsFromDiffPOsDto): NetworkResult<ReceivePackagingMaterialsResult> {
        return safeApiCall { api.receivePackagingMaterialsFromDiffPOs(request) }.map { it.data!!.toDomain() }
    }

    // Poultry
    override suspend fun getAllPoultryMaterialsPurchaseOrdersByCarNumber(): NetworkResult<List<PoultryMaterialsPurchaseOrder>> {
        return safeApiCall { api.getAllPoultryMaterialsPurchaseOrdersByCarNumber() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getPoultryMaterialsPurchaseOrderData(carNo: String): NetworkResult<PoultryMaterialsPurchaseOrder> {
        return safeApiCall { api.getPoultryMaterialsPurchaseOrderData(GetPoultryMaterialsPurchaseOrderDataRequestDto(carNo)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receivePoultryMaterials(request: ReceiveVendorPoultryMaterialsDto): NetworkResult<ReceiveVendorPoultryMaterialsResult> {
        return safeApiCall { api.receivePoultryMaterials(request) }.map { it.data!!.toDomain() }
    }

    // Transit order
    override suspend fun getAllTransitOrderMaterialPurchaseOrdersByCarNumber(): NetworkResult<List<TransitOrderMaterialPurchaseOrder>> {
        return safeApiCall { api.getAllTransitOrderMaterialPurchaseOrdersByCarNumber() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getTransitOrderMaterialPurchaseOrderData(carNo: String): NetworkResult<TransitOrderMaterialPurchaseOrder> {
        return safeApiCall { api.getTransitOrderMaterialPurchaseOrderDataByCarNumber(GetTransitOrderMaterialPurchaseOrderDataRequestDto(carNo)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receiveTransitOrderMaterial(request: ReceiveTransitOrderMaterialDto): NetworkResult<ReceiveTransitOrderMaterialResult> {
        return safeApiCall { api.receiveTransitOrderMaterial(request) }.map {
            ReceiveTransitOrderMaterialResult(
                purchaseOrder = it.data?.toDomain()
            )
        }
    }

    // Spare parts return
    override suspend fun getSparePartsReturnMovementTypeCounts(): NetworkResult<List<MovementType>> {
        return safeApiCall { api.getSparePartsReturnMovementTypeCounts() }.map { it.data?.toModel() ?: emptyList() }
    }

    override suspend fun getSparePartsReturnReservationsByMovementType(movementType: String): NetworkResult<List<SparePartsReturnReservationOrder>> {
        return safeApiCall { api.getSparePartsReturnReservationsByMovementType(movementType) }.map { it.data?.toModel() ?: emptyList() }
    }

    override suspend fun getSparePartsReturnReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>> {
        return safeApiCall { api.getSparePartsReturnReservationLines(reservationNumber) }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun postSparePartsReturn(request: PostSparePartsReturnDto): NetworkResult<PostSparePartsReturnResult> {
        return safeApiCall { api.postSparePartsReturn(request) }.map { response ->
            (response.data ?: emptyList()).toDomain()
        }
    }

    // Finished goods
    override suspend fun getAllFinishedGoodsPlasticProductionOrders(): NetworkResult<List<FinishedGoodsPlasticProductionOrder>> {
        return safeApiCall { api.getAllFinishedGoodsPlasticProductionOrders() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getFinishedGoodsPlasticProductionOrderData(orderNumber: String): NetworkResult<FinishedGoodsPlasticProductionOrder> {
        return safeApiCall { api.getFinishedGoodsPlasticProductionOrderData(GetProductionOrderReqDto(orderNumber)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receiveFinishedGoodsPlastic(request: ReceiveFinishedGoodsPlasticDto): NetworkResult<ReceiveFinishedGoodsPlasticResult> {
        return safeApiCall { api.receiveFinishedGoodsPlastic(request) }.map { it.data!!.toDomain() }
    }

    override suspend fun getAllFinishedGoodsFeedProductionOrders(): NetworkResult<List<FinishedGoodsFeedProductionOrder>> {
        return safeApiCall { api.getAllFinishedGoodsFeedProductionOrders() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getFinishedGoodsFeedProductionOrderData(orderNumber: String): NetworkResult<FinishedGoodsFeedProductionOrder> {
        return safeApiCall { api.getFinishedGoodsFeedProductionOrderData(GetProductionOrderReqDto(orderNumber)) }.map { it.data!!.toDomain() }
    }

    override suspend fun receiveFinishedGoodsFeed(request: ReceiveFinishedGoodsFeedDto): NetworkResult<ReceiveFinishedGoodsFeedResult> {
        return safeApiCall { api.receiveFinishedGoodsFeed(request) }.map { it.data!!.toDomain() }
    }

    override suspend fun getPutAwayOrders(receiptType: String): NetworkResult<List<PutAwayOrder>> {
        return safeApiCall { api.getPutAwayOrders(receiptType) }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getPutAwayOrderLines(receiptType: String, orderNumber: String): NetworkResult<List<PutAwayOrderLine>> {
        return safeApiCall { api.getPutAwayOrderLines(receiptType, orderNumber) }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getStorageBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation> {
        return safeApiCall { api.getStorageBinLocation(storageBinCode) }.map { it.data!!.toDomain() }
    }

    override suspend fun confirmPutAway(request: ConfirmPutAwayRequestDto): NetworkResult<String> {
        return safeApiCall { api.confirmPutAway(request) }.map { it.responseStatus.statusMessage }
    }

    override suspend fun getReceivingOpenOrderCounts(): NetworkResult<ReceivingOpenOrderCounts> {
        return safeApiCall { api.getReceivingOpenOrderCounts() }.map { response ->
            response.data?.toDomain() ?: ReceivingOpenOrderCounts()
        }
    }

    override suspend fun getPutAwayOpenOrderCounts(): NetworkResult<PutAwayOpenOrderCounts> {
        return safeApiCall { api.getPutAwayOpenOrderCounts() }.map { response ->
            response.data?.toDomain() ?: PutAwayOpenOrderCounts()
        }
    }

    override suspend fun searchBlindReceiveMaterials(filter: String, pageSize: Int): NetworkResult<List<BlindReceiveMaterial>> {
        return safeApiCall { api.searchBlindReceiveMaterials(filter, pageSize) }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getBlindReceiveUoms(): NetworkResult<List<Uom>> {
        return safeApiCall { api.getBlindReceiveUoms() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getBlindReceiveQualityDecisions(): NetworkResult<List<QualityDecision>> {
        return safeApiCall { api.getBlindReceiveQualityDecisions() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun getUserPlants(): NetworkResult<List<Plant>> {
        return safeApiCall { api.getUserPlants() }.map { response -> response.data?.map { it.toDomain() } ?: emptyList() }
    }

    override suspend fun createBlindReceive(request: CreateBlindReceiveRequestDto): NetworkResult<CreateBlindReceiveResult> {
        return safeApiCall { api.createBlindReceive(request) }.map { response -> response.data!!.toDomain() }
    }
}

