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
    @POST("api/ReceivingHH/GetAllRawMaterialsPurchaseOrdersByCarNumber")
    suspend fun getAllRawMaterialsPurchaseOrdersByCarNumber(): Response<MobileResponse<List<RawMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetRawMaterialsPurchaseOrderDataByCarNumber")
    suspend fun getRawMaterialsPurchaseOrderData(@Body dto: GetRawMaterialsPurchaseOrderDataRequestDto): Response<MobileResponse<RawMaterialsPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveRawMaterials")
    suspend fun receiveRawMaterials(@Body dto: ReceiveRawMaterialsDto): Response<MobileResponse<ReceiveRawMaterialsResultDto>>

    // Spare parts
    @POST("api/ReceivingHH/GetAllSparePartsPurchaseOrders")
    suspend fun getAllSparePartsPurchaseOrders(): Response<MobileResponse<List<SparePartsPurchaseOrderDto>>>
    
    // Packaging materials
    @POST("api/ReceivingHH/GetAllPackagingMaterialsPurchaseOrders")
    suspend fun getAllPackagingMaterialsPurchaseOrders(): Response<MobileResponse<List<PackagingMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetAllPackagingMaterialsPurchaseOrdersByCarNumber")
    suspend fun getAllPackagingMaterialsPurchaseOrdersByCarNumber(): Response<MobileResponse<List<PackagingMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetPackagingMaterialsPurchaseOrderDataByCarNumber")
    suspend fun getPackagingMaterialsPurchaseOrderData(@Body dto: GetPackagingMaterialsPurchaseOrderDataRequestDto): Response<MobileResponse<PackagingMaterialsPurchaseOrderDataDto>>

    @POST("api/ReceivingHH/ReceivePackagingMaterials")
    suspend fun receivePackagingMaterials(@Body dto: ReceivePackagingMaterialsDto): Response<MobileResponse<ReceivePackagingMaterialsResultDto>>

    @POST("api/ReceivingHH/ReceivePackagingMaterialsFromDiffPOs")
    suspend fun receivePackagingMaterialsFromDiffPOs(@Body dto: ReceivePackagingMaterialsFromDiffPOsDto): Response<MobileResponse<List<PackagingMaterialsDiffPoDto>>>

    // Poultry
    @POST("api/ReceivingHH/GetAllPoultryMaterialsPurchaseOrdersByCarNumber")
    suspend fun getAllPoultryMaterialsPurchaseOrdersByCarNumber(): Response<MobileResponse<List<PoultryMaterialsPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetPoultryMaterialsPurchaseOrderDataByCarNumber")
    suspend fun getPoultryMaterialsPurchaseOrderData(@Body dto: GetPoultryMaterialsPurchaseOrderDataRequestDto): Response<MobileResponse<PoultryMaterialsPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveVendorPoultryMaterials")
    suspend fun receivePoultryMaterials(@Body dto: ReceiveVendorPoultryMaterialsDto): Response<MobileResponse<ReceiveVendorPoultryMaterialsResultDto>>

    // Transit order
    @POST("api/ReceivingHH/GetAllTransitOrderMaterialPurchaseOrdersByCarNumber")
    suspend fun getAllTransitOrderMaterialPurchaseOrdersByCarNumber(): Response<MobileResponse<List<TransitOrderMaterialPurchaseOrderDto>>>

    @POST("api/ReceivingHH/GetTransitOrderMaterialPurchaseOrderDataByCarNumber")
    suspend fun getTransitOrderMaterialPurchaseOrderDataByCarNumber(@Body dto: GetTransitOrderMaterialPurchaseOrderDataRequestDto): Response<MobileResponse<TransitOrderMaterialPurchaseOrderDto>>

    @POST("api/ReceivingHH/ReceiveTransitOrderMaterial")
    suspend fun receiveTransitOrderMaterial(@Body dto: ReceiveTransitOrderMaterialDto): Response<MobileResponse<TransitOrderMaterialPurchaseOrderDto>>

    // Spare parts return
    @POST("api/ReceivingHH/GetSparePartsReturnMovementTypeCounts")
    suspend fun getSparePartsReturnMovementTypeCounts(): Response<MobileResponse<List<MovementTypeDto>>>

    @POST("api/ReceivingHH/GetSparePartsReturnReservationsByMovementType")
    suspend fun getSparePartsReturnReservationsByMovementType(@Query("movementType") movementType: String): Response<MobileResponse<List<GetSparePartsReturnReservationsByMovementTypeResultDto>>>

    @POST("api/ReceivingHH/GetSparePartsReturnReservationLines")
    suspend fun getSparePartsReturnReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/ReceivingHH/PostSparePartsReturn")
    suspend fun postSparePartsReturn(@Body dto: PostSparePartsReturnDto): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

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

    // Open order counts
    @POST("api/ReceivingHH/GetReceivingOpenOrderCounts")
    suspend fun getReceivingOpenOrderCounts(): Response<MobileResponse<ReceivingOpenOrderCountsDto>>

    @POST("api/ReceivingHH/GetPutAwayOpenOrderCounts")
    suspend fun getPutAwayOpenOrderCounts(): Response<MobileResponse<PutAwayOpenOrderCountsDto>>

    // Put away
    @GET("api/ReceivingHH/GetPutAwayOrdersWithLines")
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

    // Blind receive
    @GET("api/ReceivingHH/SearchBlindReceiveMaterials")
    suspend fun searchBlindReceiveMaterials(
        @Query("filter") filter: String,
        @Query("pageSize") pageSize: Int = 30
    ): Response<MobileResponse<List<BlindReceiveMaterialDto>>>

    @GET("api/ReceivingHH/GetBlindReceiveUoms")
    suspend fun getBlindReceiveUoms(): Response<MobileResponse<List<UomDto>>>

    @GET("api/ReceivingHH/GetBlindReceiveQualityDecisions")
    suspend fun getBlindReceiveQualityDecisions(): Response<MobileResponse<List<QualityDecisionDto>>>

    @GET("api/ReceivingHH/GetUserPlants")
    suspend fun getUserPlants(): Response<MobileResponse<List<PlantDto>>>

    @POST("api/ReceivingHH/CreateBlindReceive")
    suspend fun createBlindReceive(
        @Body request: CreateBlindReceiveRequestDto
    ): Response<MobileResponse<CreateBlindReceiveResultDto>>

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

    @GET("api/IssuingHH/GetMaterialOnHandStock")
    suspend fun getMaterialOnHandStock(
        @Query("storageBinCode") storageBinCode: String,
        @Query("materialCode") materialCode: String?
    ): Response<MobileResponse<List<MaterialOnHandStockDto>>>
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
    suspend fun issueTransferToProduction(@Body dto: PostTransferToProductionDto): Response<MobileResponse<List<TransferToProductionReservationLineDto>>>

    // Spare parts to maintenance
    @POST("api/IssuingHH/GetSparePartsToMaintenanceReservations")
    suspend fun getSparePartsToMaintenanceReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetSparePartsToMaintenanceReservationLines")
    suspend fun getSparePartsToMaintenanceReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/IssuingHH/IssueSparePartsToMaintenance")
    suspend fun issueSparePartsToMaintenance(@Body dto: PostSparePartsIssueToMaintenanceDto): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    // Issue to internal order
    @POST("api/IssuingHH/GetInternalOrderReservations")
    suspend fun getIssueToInternalOrderReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetInternalOrderReservationLines")
    suspend fun getIssueToInternalOrderReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    @POST("api/IssuingHH/IssueToInternalOrder")
    suspend fun issueToInternalOrder(@Body dto: PostIssueToInternalOrderDto): Response<MobileResponse<List<SparePartsReturnReservationLineDto>>>

    // Direct transfer
    @POST("api/IssuingHH/DirectTransfer")
    suspend fun directTransfer(@Body dto: PostDirectTransferDto): Response<MobileResponse<PostDirectTransferResultDto>>

    // Direct issue on cost center
    @POST("api/IssuingHH/GetDirectIssueOnCostCenterReservations")
    suspend fun getDirectIssueOnCostCenterReservations(): Response<MobileResponse<List<SparePartsReturnReservationDto>>>

    @POST("api/IssuingHH/GetDirectIssueOnCostCenterReservationLines")
    suspend fun getDirectIssueOnCostCenterReservationLines(@Query("reservationNumber") reservationNumber: String): Response<MobileResponse<List<DirectIssueReservationLineDto>>>

    @POST("api/IssuingHH/DirectIssueOnCostCenter")
    suspend fun directIssueOnCostCenter(@Body dto: PostDirectIssueOnCostCenterDto): Response<MobileResponse<List<DirectIssueReservationLineDto>>>

    @POST("api/IssuingHH/ScrapIssue")
    suspend fun scrapIssue(@Body dto: PostScrapIssueDto): Response<MobileResponse<PostScrapIssueResultDto>>

    @POST("api/IssuingHH/GetFinishedGoodsFeedOrdersByCarNumber")
    suspend fun getFinishedGoodsFeedOrdersByCarNumber(): Response<MobileResponse<List<FinishedGoodsFeedOrderDto>>>

    @POST("api/IssuingHH/GetFinishedGoodsFeedOrderLinesByCarNumber")
    suspend fun getFinishedGoodsFeedOrderLinesByCarNumber(@Body dto: GetFinishedGoodsFeedOrderLinesByCarNumberRequestDto): Response<MobileResponse<FinishedGoodsFeedOrderDto>>

    @POST("api/IssuingHH/IssueFinishedGoodsFeed")
    suspend fun issueFinishedGoodsFeed(@Body dto: IssueFinishedGoodsFeedBodyDto): Response<MobileResponse<List<FinishedGoodsFeedItemDto>>>

    @POST("api/IssuingHH/GetTransferOrdersByCarNumber")
    suspend fun getTransferOrdersByCarNumber(): Response<MobileResponse<List<TransferOrderDto>>>

    @POST("api/IssuingHH/GetTransferOrderDataByCarNumber")
    suspend fun getTransferOrderDataByCarNumber(@Body dto: GetTransferOrderDataByCarNumberRequestDto): Response<MobileResponse<TransferOrderDto>>

    @POST("api/IssuingHH/IssueTransferOrder")
    suspend fun issueTransferOrder(@Body dto: IssueTransferOrderDto): Response<MobileResponse<TransferOrderDto>>

    @POST("api/IssuingHH/GetIssuingOpenOrderCounts")
    suspend fun getIssuingOpenOrderCounts(): Response<MobileResponse<IssuingOpenOrderCountsDto>>

    // Direct transfer put away (stock transfers put away)
    @GET("api/IssuingHH/GetDirectTransferPutAwayDocuments")
    suspend fun getDirectTransferPutAwayDocuments(): Response<MobileResponse<List<DirectTransferPutAwayDocumentDto>>>

    @GET("api/IssuingHH/GetDirectTransferPutAwayLines")
    suspend fun getDirectTransferPutAwayLines(@Query("receiptNo") receiptNo: String): Response<MobileResponse<List<DirectTransferPutAwayLineDto>>>

    @POST("api/IssuingHH/ConfirmDirectTransferPutAway")
    suspend fun confirmDirectTransferPutAway(@Body dto: ConfirmDirectTransferPutAwayRequestDto): Response<MobileResponse<List<DirectTransferPutAwayLineDto>>>

    // PLC loading orders and conveyors API
    @GET("api/Conveyors/GetConveyors")
    suspend fun getConveyors(): Response<MobileResponse<List<ConveyorStatusDto>>>

    @GET("api/Conveyors/GetConveyor/{conveyorId}")
    suspend fun getConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponse<ConveyorStatusDto>>

    @GET("api/Conveyors/GetLoadingOrders")
    suspend fun getLoadingOrders(): Response<MobileResponse<List<LoadingOrderDto>>>

    @POST("api/Conveyors/StartConveyor/{conveyorId}")
    suspend fun startConveyor(
        @Path("conveyorId") conveyorId: String,
        @Body request: StartLoadingRequestDto
    ): Response<MobileResponse<LoadingRunDto>>

    @POST("api/Conveyors/PauseConveyor/{conveyorId}")
    suspend fun pauseConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @POST("api/Conveyors/ResumeConveyor/{conveyorId}")
    suspend fun resumeConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @POST("api/Conveyors/StopConveyor/{conveyorId}")
    suspend fun stopConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @POST("api/Conveyors/ResetConveyorFault/{conveyorId}")
    suspend fun resetConveyorFault(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @POST("api/Conveyors/ManualStartConveyor/{conveyorId}")
    suspend fun manualStartConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @POST("api/Conveyors/ManualStopConveyor/{conveyorId}")
    suspend fun manualStopConveyor(@Path("conveyorId") conveyorId: String): Response<MobileResponseDto>

    @GET("api/Conveyors/GetConveyorSettings/{conveyorId}")
    suspend fun getConveyorSettings(@Path("conveyorId") conveyorId: String): Response<MobileResponse<ConveyorSettingsDto>>

    @PUT("api/Conveyors/UpdateConveyorSettings/{conveyorId}")
    suspend fun updateConveyorSettings(
        @Path("conveyorId") conveyorId: String,
        @Body settings: ConveyorSettingsDto
    ): Response<MobileResponse<ConveyorSettingsDto>>

    @GET("api/Conveyors/GetBagTypes")
    suspend fun getBagTypes(): Response<MobileResponse<List<BagTypeDto>>>

    @POST("api/Conveyors/CreateBagType")
    suspend fun createBagType(@Body dto: BagTypeDto): Response<MobileResponse<BagTypeDto>>

    @PUT("api/Conveyors/UpdateBagType/{id}")
    suspend fun updateBagType(@Path("id") id: Int, @Body dto: BagTypeDto): Response<MobileResponse<BagTypeDto>>

    @DELETE("api/Conveyors/DeleteBagType/{id}")
    suspend fun deleteBagType(@Path("id") id: Int): Response<MobileResponseDto>

    @GET("api/Conveyors/GetLoadingRunsReport")
    suspend fun getLoadingRunsReport(): Response<MobileResponse<List<LoadingRunDto>>>

}
