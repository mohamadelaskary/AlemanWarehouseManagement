package net.gbs.gbswarehouse.domain.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.*
import net.gbs.gbswarehouse.data.remote.dto.response.*
import net.gbs.gbswarehouse.domain.model.*
import net.gbs.gbswarehouse.QualityDecision

interface ReceivingRepository {

    suspend fun getPlantWarehouses(plantCode: String): NetworkResult<List<Warehouse>>
    suspend fun getAllWorkerGroups(): NetworkResult<List<WorkersGroup>>
    suspend fun getAllLoadingVendors(): NetworkResult<List<LoadingVendor>>
    suspend fun getAllCarNumbers(): NetworkResult<List<CarNumber>>
    suspend fun getAllCarTypes(): NetworkResult<List<CarType>>
    suspend fun getNaolonPerTonValue(plantCode: String, naolonDescription: String, carType: String): NetworkResult<NaolonPerTon>
    suspend fun getAllNaolonDescriptions(): NetworkResult<List<NaolonDescription>>
    suspend fun getAllPackageTypes(): NetworkResult<List<PackageType>>

    // Spare parts
    suspend fun getReceivingSparePartsFromVendorOrdersList(): NetworkResult<List<SparePartsPurchaseOrder>>
    suspend fun getReceivingSparePartsFromVendorOrderData(request: GetSparePartsPurchaseOrderDataRequest): NetworkResult<PurchaseOrder>
    suspend fun receiveVendorSpareParts(request: ReceiveVendorSparePartsRequestDto): NetworkResult<PurchaseOrder>

    // Raw materials
    suspend fun getAllRawMaterialsPurchaseOrdersByCarNumber(): NetworkResult<List<RawMaterialsPurchaseOrder>>
    suspend fun getRawMaterialsPurchaseOrderData(carNo: String): NetworkResult<RawMaterialsPurchaseOrder>
    suspend fun receiveRawMaterials(request: ReceiveRawMaterialsDto): NetworkResult<ReceiveRawMaterialsResult>

    // Packaging materials
    suspend fun getAllPackagingMaterialsPurchaseOrders(): NetworkResult<List<PackagingMaterialsPurchaseOrder>>
    suspend fun getPackagingMaterialsPurchaseOrderData(carNo: String): NetworkResult<PackagingMaterialsPurchaseOrder>
    suspend fun receivePackagingMaterials(request: ReceivePackagingMaterialsDto): NetworkResult<ReceivePackagingMaterialsResult>
    suspend fun receivePackagingMaterialsFromDiffPOs(request: ReceivePackagingMaterialsFromDiffPOsDto): NetworkResult<ReceivePackagingMaterialsResult>

    // Poultry
    suspend fun getAllPoultryMaterialsPurchaseOrdersByCarNumber(): NetworkResult<List<PoultryMaterialsPurchaseOrder>>
    suspend fun getPoultryMaterialsPurchaseOrderData(carNo: String): NetworkResult<PoultryMaterialsPurchaseOrder>
    suspend fun receivePoultryMaterials(request: ReceiveVendorPoultryMaterialsDto): NetworkResult<ReceiveVendorPoultryMaterialsResult>

    // Transit order
    suspend fun getAllTransitOrderMaterialPurchaseOrdersByCarNumber(): NetworkResult<List<TransitOrderMaterialPurchaseOrder>>
    suspend fun getTransitOrderMaterialPurchaseOrderData(carNo: String): NetworkResult<TransitOrderMaterialPurchaseOrder>
    suspend fun receiveTransitOrderMaterial(request: ReceiveTransitOrderMaterialDto): NetworkResult<ReceiveTransitOrderMaterialResult>

    // Spare parts return
    suspend fun getSparePartsReturnMovementTypeCounts(): NetworkResult<List<MovementType>>
    suspend fun getSparePartsReturnReservationsByMovementType(movementType: String): NetworkResult<List<SparePartsReturnReservationOrder>>
    suspend fun getSparePartsReturnReservationLines(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>>
    suspend fun postSparePartsReturn(request: PostSparePartsReturnDto): NetworkResult<PostSparePartsReturnResult>

    // Finished goods
    suspend fun getAllFinishedGoodsPlasticProductionOrders(): NetworkResult<List<FinishedGoodsPlasticProductionOrder>>
    suspend fun getFinishedGoodsPlasticProductionOrderData(orderNumber: String): NetworkResult<FinishedGoodsPlasticProductionOrder>
    suspend fun receiveFinishedGoodsPlastic(request: ReceiveFinishedGoodsPlasticDto): NetworkResult<ReceiveFinishedGoodsPlasticResult>

    suspend fun getAllFinishedGoodsFeedProductionOrders(): NetworkResult<List<FinishedGoodsFeedProductionOrder>>
    suspend fun getFinishedGoodsFeedProductionOrderData(orderNumber: String): NetworkResult<FinishedGoodsFeedProductionOrder>
    suspend fun receiveFinishedGoodsFeed(request: ReceiveFinishedGoodsFeedDto): NetworkResult<ReceiveFinishedGoodsFeedResult>

    // Put away
    suspend fun getPutAwayOrders(receiptType: String): NetworkResult<List<PutAwayOrder>>
    suspend fun getPutAwayOrderLines(receiptType: String, orderNumber: String): NetworkResult<List<PutAwayOrderLine>>
    suspend fun getStorageBinLocation(storageBinCode: String): NetworkResult<StorageBinLocation>
    suspend fun confirmPutAway(request: ConfirmPutAwayRequestDto): NetworkResult<String>

    // Open order counts
    suspend fun getReceivingOpenOrderCounts(): NetworkResult<ReceivingOpenOrderCounts>
    suspend fun getPutAwayOpenOrderCounts(): NetworkResult<PutAwayOpenOrderCounts>

    // Blind receive
    suspend fun searchBlindReceiveMaterials(filter: String, pageSize: Int = 30): NetworkResult<List<BlindReceiveMaterial>>
    suspend fun getBlindReceiveUoms(): NetworkResult<List<Uom>>
    suspend fun getBlindReceiveQualityDecisions(): NetworkResult<List<QualityDecision>>
    suspend fun getUserPlants(): NetworkResult<List<Plant>>
    suspend fun createBlindReceive(request: CreateBlindReceiveRequestDto): NetworkResult<CreateBlindReceiveResult>
}

