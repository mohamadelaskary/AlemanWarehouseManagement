package net.gbs.gbswarehouse.data.remote.api

import net.gbs.gbswarehouse.MovementType
import net.gbs.gbswarehouse.data.remote.dto.request.*
import net.gbs.gbswarehouse.data.remote.dto.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/ReceivingHH/GetSparePartsPurchaseOrderData")
    suspend fun getSparePartsPurchaseOrderData(
        @Body getSparePartsPurchaseOrderDataRequest: GetSparePartsPurchaseOrderDataRequest
    ): Response<SparePartsPurchaseOrderDataDto>
    @POST("api/ReceivingHH/ReceiveVendorSpareParts")
    suspend fun receiveVendorSpareParts(
        @Body request: ReceiveVendorSparePartsRequestDto
    ): Response<MobileResponseDto>

    // Helpers
    @GET("api/ReceivingHH/GetPlantWarehouses")
    suspend fun getPlantWarehouses(@Query("plantCode") plantCode: String): Response<MobileResponse<List<WarehouseDto>>>

    @GET("api/ReceivingHH/GetAllWorkerGroups")
    suspend fun getAllWorkerGroups(): Response<MobileResponse<List<WorkersGroupDto>>>

    @GET("api/ReceivingHH/GetAllLoadingVendors")
    suspend fun getAllLoadingVendors(): Response<MobileResponse<List<LoadingVendorDto>>>

    @GET("api/ReceivingHH/GetAllCarNumbers")
    suspend fun getAllCarNumbers(): Response<MobileResponse<List<CarNumberDto>>>

    @GET("api/ReceivingHH/GetAllCarTypes")
    suspend fun getAllCarTypes(): Response<MobileResponse<List<CarTypeDto>>>
    @GET("api/ReceivingHH/GetNaolonPerTonValue")
    suspend fun getNaolonPerTonValue(
        @Query("plantCode") plantCode: String,
        @Query("naolonDescription") naolonDescription: String,
        @Query("vehicleType") carType: String,
    ): Response<MobileResponse<NaolonPerTon>>

    @GET("api/ReceivingHH/GetAllNaolonDescriptions")
    suspend fun getAllNaolonDescriptions(): Response<MobileResponse<List<NaolonDescriptionDto>>>

    @GET("api/ReceivingHH/GetAllPackageTypes")
    suspend fun getAllPackageTypes(): Response<MobileResponse<List<PackageTypeDto>>>
    // Raw materials
    @POST("api/ReceivingHH/GetAllRawMaterialsPurchaseOrders")
    suspend fun getAllRawMaterialsPurchaseOrders(): Response<MobileResponse<List<RawMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetRawMaterialsPurchaseOrderData")
    suspend fun getRawMaterialsPurchaseOrderData(@Body dto: GetPurchaseOrderReqDto): Response<MobileResponse<RawMaterialsPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveRawMaterials")
    suspend fun receiveRawMaterials(@Body dto: ReceiveRawMaterialsDto): Response<MobileResponse<ReceiveRawMaterialsResultDto>>

    // Spare parts
    @POST("api/ReceivingHH/GetAllSparePartsPurchaseOrders")
    suspend fun getAllSparePartsPurchaseOrders(): Response<MobileResponse<List<SparePartsPurchaseOrderDto>>>
    
    // Packaging materials
    @POST("api/ReceivingHH/GetAllPackagingMaterialsPurchaseOrders")
    suspend fun getAllPackagingMaterialsPurchaseOrders(): Response<MobileResponse<List<PackagingMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetPackagingMaterialsPurchaseOrderData")
    suspend fun getPackagingMaterialsPurchaseOrderData(@Body dto: GetPurchaseOrderReqDto): Response<MobileResponse<PackagingMaterialsPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceivePackagingMaterials")
    suspend fun receivePackagingMaterials(@Body dto: ReceivePackagingMaterialsDto): Response<MobileResponse<ReceivePackagingMaterialsResultDto>>

    @POST("api/ReceivingHH/ReceivePackagingMaterialsFromDiffPOs")
    suspend fun receivePackagingMaterialsFromDiffPOs(@Body dto: ReceivePackagingMaterialsFromDiffPOsDto): Response<MobileResponse<ReceivePackagingMaterialsResultDto>>

    // Poultry
    @POST("api/ReceivingHH/GetAllPoultryMaterialsPurchaseOrders")
    suspend fun getAllPoultryMaterialsPurchaseOrders(): Response<MobileResponse<List<PoultryMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetPoultryMaterialsPurchaseOrderData")
    suspend fun getPoultryMaterialsPurchaseOrderData(@Body dto: GetPurchaseOrderReqDto): Response<MobileResponse<PoultryMaterialsPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveVendorPoultryMaterials")
    suspend fun receivePoultryMaterials(@Body dto: ReceiveVendorPoultryMaterialsDto): Response<MobileResponse<ReceiveVendorPoultryMaterialsResultDto>>

    // Transit order
    @POST("api/ReceivingHH/GetAllTransitOrderMaterialPurchaseOrders")
    suspend fun getAllTransitOrderMaterialPurchaseOrders(): Response<MobileResponse<List<TransitOrderMaterialPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetTransitOrderMaterialPurchaseOrderData")
    suspend fun getTransitOrderMaterialPurchaseOrderData(@Body dto: GetPurchaseOrderReqDto): Response<MobileResponse<TransitOrderMaterialPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveTransitOrderMaterial")
    suspend fun receiveTransitOrderMaterial(@Body dto: ReceiveTransitOrderMaterialDto): Response<MobileResponse<ReceiveTransitOrderMaterialResultDto>>

    // Spare parts return
    @POST("api/ReceivingHH/GetSparePartsReturnMovementTypeCounts")
    suspend fun getSparePartsReturnMovementTypeCounts(): Response<MobileResponse<List<MovementTypeDto>>>

    @POST("api/ReceivingHH/GetSparePartsReturnReservationsByMovementType")
    suspend fun getSparePartsReturnReservationsByMovementType(@Query("movementType") movementType: String): Response<MobileResponse<List<GetSparePartsReturnReservationsByMovementTypeResultDto>>>

    @POST("api/ReceivingHH/GetSparePartsReturnReservationLines")
    suspend fun getSparePartsReturnReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/ReceivingHH/PostSparePartsReturn")
    suspend fun postSparePartsReturn(@Body dto: PostSparePartsReturnDto): Response<MobileResponse<PostSparePartsReturnResultDto>>

    // Finished goods
    @POST("api/ReceivingHH/GetAllFinishedGoodsPlasticProductionOrders")
    suspend fun getAllFinishedGoodsPlasticProductionOrders(): Response<MobileResponse<List<FinishedGoodsPlasticProductionOrderDto>>>

    @POST("api/ReceivingHH/GetFinishedGoodsPlasticProductionOrderData")
    suspend fun getFinishedGoodsPlasticProductionOrderData(@Body dto: GetProductionOrderReqDto): Response<MobileResponse<FinishedGoodsPlasticProductionOrderDto>>

    @POST("api/ReceivingHH/ReceiveFinishedGoodsPlastic")
    suspend fun receiveFinishedGoodsPlastic(@Body dto: ReceiveFinishedGoodsPlasticDto): Response<MobileResponse<ReceiveFinishedGoodsPlasticResultDto>>

    @POST("api/ReceivingHH/GetAllFinishedGoodsFeedProductionOrders")
    suspend fun getAllFinishedGoodsFeedProductionOrders(): Response<MobileResponse<List<FinishedGoodsFeedProductionOrderDto>>>

    @POST("api/ReceivingHH/GetFinishedGoodsFeedProductionOrderData")
    suspend fun getFinishedGoodsFeedProductionOrderData(@Body dto: GetProductionOrderReqDto): Response<MobileResponse<FinishedGoodsFeedProductionOrderDto>>

    @POST("api/ReceivingHH/ReceiveFinishedGoodsFeed")
    suspend fun receiveFinishedGoodsFeed(@Body dto: ReceiveFinishedGoodsFeedDto): Response<MobileResponse<ReceiveFinishedGoodsFeedResultDto>>

    // Put away
    @GET("api/ReceivingHH/GetPutAwayOrders")
    suspend fun getPutAwayOrders(@Query("receiptType") receiptType: String): Response<MobileResponse<List<PutAwayOrderDto>>>

    @GET("api/ReceivingHH/GetPutAwayOrderLines")
    suspend fun getPutAwayOrderLines(
        @Query("receiptType") receiptType: String,
        @Query("orderNumber") orderNumber: String
    ): Response<MobileResponse<List<PutAwayOrderLineDto>>>

    @GET("api/ReceivingHH/GetStorageBinLocation")
    suspend fun getStorageBinLocation(@Query("storageBinCode") storageBinCode: String): Response<MobileResponse<StorageBinLocationDto>>

    @POST("api/ReceivingHH/ConfirmPutAway")
    suspend fun confirmPutAway(@Body request: ConfirmPutAwayRequestDto): Response<MobileResponseDto>
    @GET("api/IssuingHH/GetPlantWarehouses")
    suspend fun getPlantWarehousesIssuing(@Query("plantCode") plantCode: String): Response<MobileResponse<List<SAPWarehouseDto>>>

    @GET("api/IssuingHH/GetMaterialStock")
    suspend fun getMaterialStock(
        @Query("plantCode") plantCode: String,
        @Query("materialCode") materialCode: String,
        @Query("storageLocationCode") storageLocationCode: String? = null
    ): Response<MobileResponse<List<SAPMaterialBatchValuationStockDto>>>

    @GET("api/IssuingHH/GetScrapEligibleStock")
    suspend fun getScrapEligibleStock(): Response<MobileResponse<List<SAPMaterialBatchValuationStockDto>>>

    @GET("api/IssuingHH/GetStorageBinLocation")
    suspend fun getStorageBinLocationIssuing(@Query("storageBinCode") storageBinCode: String): Response<MobileResponse<StorageBinLocationDto>>
    @GET("api/IssuingHH/GetCostCenters")
    suspend fun getCostCenters(@Query("companyCode") companyCode: String): Response<MobileResponse<List<CostCenterDto>>>
    @GET("api/IssuingHH/GetCompanies")
    suspend fun getCompanies(): Response<MobileResponse<List<CompanyDto>>>

    // Transfer to Production
    @POST("api/IssuingHH/GetTransferToProductionReservations")
    suspend fun getTransferToProductionReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetTransferToProductionReservationLines")
    suspend fun getTransferToProductionReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<TransferToProductionReservationLineDto>>>

    @POST("api/IssuingHH/IssueTransferToProduction")
    suspend fun issueTransferToProduction(@Body dto: PostTransferToProductionDto): Response<MobileResponse<PostTransferToProductionResultDto>>

    // Spare parts to maintenance
    @POST("api/IssuingHH/GetSparePartsToMaintenanceReservations")
    suspend fun getSparePartsToMaintenanceReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetSparePartsToMaintenanceReservationLines")
    suspend fun getSparePartsToMaintenanceReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/IssuingHH/IssueSparePartsToMaintenance")
    suspend fun issueSparePartsToMaintenance(@Body dto: PostSparePartsIssueToMaintenanceDto): Response<MobileResponse<PostSparePartsIssueToMaintenanceResultDto>>

    // Issue to internal order
    @POST("api/IssuingHH/GetInternalOrderReservations")
    suspend fun getIssueToInternalOrderReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetInternalOrderReservationLines")
    suspend fun getIssueToInternalOrderReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/IssuingHH/IssueToInternalOrder")
    suspend fun issueToInternalOrder(@Body dto: PostIssueToInternalOrderDto): Response<MobileResponse<PostIssueToInternalOrderResultDto>>

    // Direct transfer
    @POST("api/IssuingHH/DirectTransfer")
    suspend fun directTransfer(@Body dto: PostDirectTransferDto): Response<MobileResponse<PostDirectTransferResultDto>>

    // Direct issue on cost center
    @POST("api/IssuingHH/GetDirectIssueOnCostCenterReservations")
    suspend fun getDirectIssueOnCostCenterReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetDirectIssueOnCostCenterReservationLines")
    suspend fun getDirectIssueOnCostCenterReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/IssuingHH/DirectIssueOnCostCenter")
    suspend fun directIssueOnCostCenter(@Body dto: PostDirectIssueOnCostCenterDto): Response<MobileResponse<PostDirectIssueOnCostCenterResultDto>>

    @POST("api/IssuingHH/ScrapIssue")
    suspend fun scrapIssue(@Body dto: PostScrapIssueDto): Response<MobileResponse<PostScrapIssueResultDto>>

}
