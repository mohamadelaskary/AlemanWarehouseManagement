package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import kotlinx.serialization.Serializable
import net.gbs.gbswarehouse.domain.model.ValuationType
import java.math.BigDecimal



@Serializable
data class MobileResponse<T>(
    override val responseStatus: ResponseStatus,
    val data: T? = null
) : BaseResponse

// ---------- Request DTOs ----------

@Serializable
data class GetPurchaseOrderReqDto(
    val poNumber: String = ""
)

@Serializable
data class ReceiveRawMaterialsDto(
    val poNumber: String = "",
    val lines: List<RawMaterialsLineDto> = emptyList()
)

@Serializable
data class RawMaterialsLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val purchaseOrder: String = "",
    val purchaseOrderItem: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val carNo: String? = null,
    val unloadingPoint: String? = null,
    val naolon: Double? = null,
    val contNo: String? = null,
    val loadingVendorCode: String? = null,
    val wrokerGroupCode: String? = null,
    val naolonDescription: String? = null,
    val packTypeCode: String? = null
)

@Serializable
data class ReceivePackagingMaterialsDto(
    val poNumber: String = "",
    val lines: List<PackagingMaterialsLineDto> = emptyList()
)

@Serializable
data class PackagingMaterialsLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val purchaseOrder: String = "",
    val purchaseOrderItem: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val carNo: String? = null
)

@Serializable
data class ReceivePackagingMaterialsFromDiffPOsDto(
    val lines: List<PackagingMaterialsLineDto> = emptyList()
)

@Serializable
data class ReceiveVendorSparePartsDto(
    val poNumber: String = "",
    val lines: List<SparePartsLineDto> = emptyList()
)

@Serializable
data class SparePartsLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val purchaseOrder: String = "",
    val purchaseOrderItem: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val isBatchManaged: Boolean = false,
    val valuationType: String? = null,
    val expirationDate: String? = null // ISO string; adjust if you want Date
)

@Serializable
data class ReceiveVendorPoultryMaterialsDto(
    val poNumber: String = "",
    val lines: List<PoultryMaterialsLineDto> = emptyList()
)

@Serializable
data class PoultryMaterialsLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val purchaseOrder: String = "",
    val purchaseOrderItem: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val carNo: String? = null,
    val unloadingPoint: String? = null,
    val naolon: Double? = null,
    val contNo: String? = null,
    val loadingVendorCode: String? = null,
    val wrokerGroupCode: String? = null,
    val naolonDescription: String? = null,
    val packTypeCode: String? = null,
    val isBatchManaged: Boolean = false,
    val expirationDate: String? = null
)

@Serializable
data class ReceiveTransitOrderMaterialDto(
    val poNumber: String = "",
    val lines: List<TransitOrderMaterialLineDto> = emptyList()
)

@Serializable
data class TransitOrderMaterialLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val purchaseOrder: String = "",
    val purchaseOrderItem: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val carNo: String? = null,
    val unloadingPoint: String? = null,
    val naolon: Double? = null,
    val contNo: String? = null,
    val loadingVendorCode: String? = null,
    val wrokerGroupCode: String? = null,
    val naolonDescription: String? = null,
    val packTypeCode: String? = null,
    val carType: String? = null
)

@Serializable
data class PostSparePartsReturnDto(
    val lines: List<SparePartsReturnLineDto> = emptyList()
)

@Serializable
data class SparePartsReturnLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val reservationNumber: String = "",
    val reservationItem: String = "",
    val returnReason: String?=null,
    val valuationType: String? = null,
    val itemText: String? = null,
    val goodsRecipient: String? = null
)

@Serializable
data class ReceiveFinishedGoodsPlasticDto(
    val orderNumber: String = "",
    val lines: List<FinishedGoodsPlasticLineDto> = emptyList()
)

@Serializable
data class FinishedGoodsPlasticLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val orderNumber: String = "",
    val itemNumber: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val unloadingPoint: String? = null,
    val naolon: Double? = null,
    val wrokerGroupCode: String? = null
)

@Serializable
data class ReceiveFinishedGoodsFeedDto(
    val orderNumber: String = "",
    val lines: List<FinishedGoodsFeedLineDto> = emptyList()
)

@Serializable
data class FinishedGoodsFeedLineDto(
    val materialCode: String = "",
    val plantCode: String = "",
    val warehouseCode: String = "",
    val orderNumber: String = "",
    val itemNumber: String = "",
    val uom: String = "",
    val receivedQuantity: Double = 0.0,
    val wrokerGroupCode: String? = null
)

// ---------- Response DTOs (results) ----------

@Serializable
data class GoodsReceiptLineStatusResult(
    val goodsReceiptLineId: Long,
    val material: String,
    val receivedQuantity: Double,
    val sapOutboxMessageId: Long? = null,
    val sapStatus: String? = null,
    val sapMaterialDocument: String? = null,
    val sapMaterialDocumentYear: String? = null,
    val attemptCount: Int,
    val maxAttempts: Int,
    val lastErrorMessage: String? = null
)

@Serializable
data class GoodsReceiptStatusResult(
    val goodsReceiptId: Long,
    val receiptNo: String,
    val receiptStatus: String, // enum/string representation
    val readyForPutAway: Boolean,
    val needsAction: Boolean,
    val hasOrphanedLines: Boolean,
    val lines: List<GoodsReceiptLineStatusResult> = emptyList()
)

@Serializable
data class ReceiveRawMaterialsResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: RawMaterialsPurchaseOrderDto? = null
)

@Serializable
data class RawMaterialsPurchaseOrderDto(
    val poNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val companyCode: String = "",
    val purchaseOrderItems: List<RawMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class RawMaterialsPurchaseOrderItemDto(
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val carNo: String? = null
)

// Packaging result
@Serializable
data class ReceivePackagingMaterialsResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: PackagingMaterialsPurchaseOrderDto? = null
)

@Serializable
data class PackagingMaterialsPurchaseOrderDto(
    val poNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val companyCode: String = "",
    val purchaseOrderItems: List<PackagingMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class PackagingMaterialsPurchaseOrderItemDto(
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val carNo: String? = null
)

// Spare parts result
@Serializable
data class ReceiveVendorSparePartsResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: SparePartsPurchaseOrderDto? = null
)

@Serializable
data class SparePartsPurchaseOrderDto(
    val poNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val companyCode: String = "",
    val purchaseOrderItems: List<SparePartsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class SparePartsPurchaseOrderItemDto(
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val isBatchManaged: Boolean? = null,
    val valuationTypes: List<ValuationTypeDto> = emptyList()
)

// Poultry result
@Serializable
data class ReceiveVendorPoultryMaterialsResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: PoultryMaterialsPurchaseOrderDto? = null
)

@Serializable
data class PoultryMaterialsPurchaseOrderDto(
    val poNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val companyCode: String = "",
    val purchaseOrderItems: List<PoultryMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class PoultryMaterialsPurchaseOrderItemDto(
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val carNo: String? = null
)

// Transit order result
@Serializable
data class ReceiveTransitOrderMaterialResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: TransitOrderMaterialPurchaseOrderDto? = null
)

@Serializable
data class TransitOrderMaterialPurchaseOrderDto(
    val poNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val warehouseCode: String? = null,
    val warehouseDescription: String? = null,
    val companyCode: String = "",
    val purchaseOrderItems: List<TransitOrderMaterialPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class TransitOrderMaterialPurchaseOrderItemDto(
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val carNo: String? = null
)

// Spare parts return result
@Serializable
data class PostSparePartsReturnResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val reservationLines: List<SparePartsReturnReservationLineDto> = emptyList()
)

// Finished goods results
@Serializable
data class ReceiveFinishedGoodsPlasticResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val productionOrder: FinishedGoodsPlasticProductionOrderDto? = null
)

@Serializable
data class FinishedGoodsPlasticProductionOrderDto(
    val orderNumber: String = "",
    val productionOrderItems: List<FinishedGoodsPlasticProductionOrderItemDto> = emptyList()
)

@Serializable
data class FinishedGoodsPlasticProductionOrderItemDto(
    @SerializedName("itemNumber"          ) var itemNumber          : String? = null,
    @SerializedName("materialCode"        ) var materialCode        : String? = null,
    @SerializedName("materialDescription" ) var materialDescription : String? = null,
    @SerializedName("orderedQuantity"     ) var orderedQuantity     : Int?    = null,
    @SerializedName("receivedQuantity"    ) var receivedQuantity    : Int?    = null,
    @SerializedName("remainingQuantity"   ) var remainingQuantity   : Int?    = null,
    @SerializedName("uom"                 ) var uom                 : String? = null,
    @SerializedName("plantCode"           ) var plantCode           : String? = null,
    @SerializedName("warehouseCode"       ) var warehouseCode       : String? = null,
    @SerializedName("orderType"           ) var orderType           : String? = null,
    @SerializedName("basicStartDate"      ) var basicStartDate      : String? = null
)

@Serializable
data class ReceiveFinishedGoodsFeedResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val productionOrder: FinishedGoodsFeedProductionOrderDto? = null
)

@Serializable
data class FinishedGoodsFeedProductionOrderDto(
    val orderNumber: String = "",
    val productionOrderItems: List<FinishedGoodsFeedProductionOrderItemDto> = emptyList()
)

@Serializable
data class FinishedGoodsFeedProductionOrderItemDto(
    @SerializedName("itemNumber"          ) var itemNumber          : String? = null,
    @SerializedName("materialCode"        ) var materialCode        : String? = null,
    @SerializedName("materialDescription" ) var materialDescription : String? = null,
    @SerializedName("orderedQuantity"     ) var orderedQuantity     : Double?    = null,
    @SerializedName("receivedQuantity"    ) var receivedQuantity    : Double?    = null,
    @SerializedName("remainingQuantity"   ) var remainingQuantity   : Double?    = null,
    @SerializedName("uom"                 ) var uom                 : String? = null,
    @SerializedName("plantCode"           ) var plantCode           : String? = null,
    @SerializedName("warehouseCode"       ) var warehouseCode       : String? = null,
    @SerializedName("orderType"           ) var orderType           : String? = null,
    @SerializedName("basicStartDate"      ) var basicStartDate      : String? = null
)



// Small missing DTO used above (production order request)
@Serializable
data class GetProductionOrderReqDto(
    val orderNumber: String = ""
)

@Serializable
data class PutAwayOrderDto(
    val orderNumber: String,
    val receiptType: String,
    val itemCount: Int,
    val earliestReceivedAt: String
)

@Serializable
data class PutAwayOrderLineDto(
    val goodsReceiptLineId: Int,
    val goodsReceiptId: Int,
    val receiptNo: String,
    val receiptType: String,
    val materialCode: String,
    val plantCode: String,
    val warehouseCode: String,
    val purchaseOrder: String,
    val purchaseOrderItem: String,
    val uom: String,
    val receivedQuantity: Double,
    val putAwayQuantity: Double,
    val remainingQuantity: Double,
    val batch: String?,
    val expirationDate: String?,
    val receivedAt: String
)

@Serializable
data class StorageBinLocationDto(
    val storageBinId: Int,
    val storageBinCode: String,
    val storageBinName: String,
    val sectionId: Int,
    val sectionCode: String,
    val sectionName: String,
    val roadId: Int,
    val roadCode: String,
    val roadName: String,
    val logisticsAreaId: Int,
    val logisticsAreaCode: String,
    val logisticsAreaName: String,
    val warehouseId: Int,
    val warehouseCode: String,
    val warehouseDescription: String,
    val plantCode: String
)

@Serializable
data class ConfirmPutAwayRequestDto(
    val goodsReceiptLineId: Int,
    val storageBinCode: String,
    val quantity: Double
)
@Serializable
data class SAPWarehouseDto(
    @SerializedName("warehouseId") val warehouseId: Long,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("warehouseDescription") val warehouseDescription: String?,
    @SerializedName("plantCode") val plantCode: String
)

@Serializable
data class SAPMaterialBatchValuationStockDto(
    @SerializedName("materialBatchValuationStockId" ) var materialBatchValuationStockId : Int?    = null,
    @SerializedName("materialCode"                  ) var materialCode                  : String? = null,
    @SerializedName("plantCode"                     ) var plantCode                     : String? = null,
    @SerializedName("storageLocationCode"           ) var storageLocationCode           : String? = null,
    @SerializedName("batch"                         ) var batch                         : String? = null,
    @SerializedName("quantity"                      ) var quantity                      : Int?    = null,
    @SerializedName("description"                   ) var description                   : String? = null,
    @SerializedName("unitOfMeasure"                 ) var unitOfMeasure                 : String? = null,
    @SerializedName("batchManagement"               ) var batchManagement               : String? = null,
    @SerializedName("batchDate"                     ) var batchDate                     : String? = null

)



// --- Spare Parts Return reservation browsing DTOs (used across flows) ---
@Serializable
data class SparePartsReturnReservationDto(
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("orderNumber") val orderNumber: String?,
    @SerializedName("orderDescription") val orderDescription: String?,
    @SerializedName("companyCode") val companyCode: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("costCenterCode") val costCenterCode: String?,
    @SerializedName("costCenterDescription") val costCenterDescription: String?,
    @SerializedName("requirementDate") val requirementDate: String?, // ISO datetime
    @SerializedName("movementType") val movementType: String,
    @SerializedName("itemCount") val itemCount: Int
)

@Serializable
data class SparePartsReturnReservationLineDto(
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("materialCode") val materialCode: String?,
    @SerializedName("materialDescription") val materialDescription: String?,
    @SerializedName("uom") val uom: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("storageLocationCode") val storageLocationCode: String?,
    @SerializedName("movementType") val movementType: String,
    @SerializedName("batch") val batch: String?,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("receivedQuantity") val receivedQuantity: Double?,
    @SerializedName("remainingQuantity") val remainingQuantity: Double?,
    @SerializedName("costCenterCode") val costCenterCode: String?,
    @SerializedName("costCenterDescription") val costCenterDescription: String?,
    @SerializedName("isBatchManaged") val isBatchManaged: Boolean?,
    @SerializedName("valuationTypes") val valuationTypes: List<ValuationTypeDto>
)

// --- Transfer to Production reservation line (response) ---
@Serializable
data class TransferToProductionReservationLineDto(
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("materialCode") val materialCode: String?,
    @SerializedName("materialDescription") val materialDescription: String?,
    @SerializedName("uom") val uom: String?,
    @SerializedName("plantCode") val plantCode: String?,
    @SerializedName("storageLocationCode") val storageLocationCode: String?,
    @SerializedName("movementType") val movementType: String,
    @SerializedName("batch") val batch: String?,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("receivedQuantity") val receivedQuantity: Double?,
    @SerializedName("remainingQuantity") val remainingQuantity: Double?,
    @SerializedName("costCenterCode") val costCenterCode: String?,
    @SerializedName("costCenterDescription") val costCenterDescription: String?,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String?,
    @SerializedName("receivingStorageLocationCode") val receivingStorageLocationCode: String?,
    @SerializedName("isBatchManaged") val isBatchManaged: Boolean?,
    @SerializedName("valuationTypes") val valuationTypes: List<ValuationTypeDto>
)

// -------------------- Request DTOs & Result DTOs for issuing flows --------------------

// Transfer to Production - request
@Serializable
data class PostTransferToProductionDto(
    @SerializedName("lines") val lines: List<TransferToProductionLineDto>
)

@Serializable
data class TransferToProductionLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("batch") val batch: String?,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String,
    @SerializedName("receivingWarehouseCode") val receivingWarehouseCode: String,
    @SerializedName("receivingValuationType") val receivingValuationType: String?,
    @SerializedName("receivingBatch") val receivingBatch: String?
)

// Transfer to Production - result
@Serializable
data class PostTransferToProductionResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult,
    @SerializedName("reservationLines") val reservationLines: List<TransferToProductionReservationLineDto>
)

// Spare Parts Issue to Maintenance - request
@Serializable
data class PostSparePartsIssueToMaintenanceDto(
    @SerializedName("lines") val lines: List<SparePartsIssueToMaintenanceLineDto>
)

@Serializable
data class SparePartsIssueToMaintenanceLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("batch") val batch: String?
)

@Serializable
data class PostSparePartsIssueToMaintenanceResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult,
    @SerializedName("reservationLines") val reservationLines: List<SparePartsReturnReservationLineDto>
)

// Issue to Internal Order - request
@Serializable
data class PostIssueToInternalOrderDto(
    @SerializedName("lines") val lines: List<IssueToInternalOrderLineDto>
)

@Serializable
data class IssueToInternalOrderLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("batch") val batch: String?
)

@Serializable
data class PostIssueToInternalOrderResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult,
    @SerializedName("reservationLines") val reservationLines: List<SparePartsReturnReservationLineDto>
)

// Direct Transfer - request
@Serializable
data class PostDirectTransferDto(
    @SerializedName("lines") val lines: List<DirectTransferLineDto>
)

@Serializable
data class DirectTransferLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("batch") val batch: String?,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String,
    @SerializedName("receivingWarehouseCode") val receivingWarehouseCode: String,
    @SerializedName("receivingValuationType") val receivingValuationType: String?,
    @SerializedName("receivingBatch") val receivingBatch: String?
)

@Serializable
data class PostDirectTransferResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult
)

// Direct Issue on Cost Center - request/result
@Serializable
data class PostDirectIssueOnCostCenterDto(
    @SerializedName("lines") val lines: List<DirectIssueOnCostCenterLineDto>
)

@Serializable
data class DirectIssueOnCostCenterLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("reservationNumber") val reservationNumber: String,
    @SerializedName("reservationItem") val reservationItem: String,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("valuationType") val valuationType: String?,
    @SerializedName("batch") val batch: String?
)

@Serializable
data class PostDirectIssueOnCostCenterResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult,
    @SerializedName("reservationLines") val reservationLines: List<SparePartsReturnReservationLineDto>
)

// Scrap Issue - request/result
@Serializable
data class PostScrapIssueDto(
    @SerializedName("documentHeaderText") val documentHeaderText: String?,
    @SerializedName("lines") val lines: List<ScrapIssueLineDto>
)

@Serializable
data class ScrapIssueLineDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("storageBinCode") val storageBinCode: String,
    @SerializedName("costCenterCode") val costCenterCode: String,
    @SerializedName("itemText") val itemText: String?
)

@Serializable
data class PostScrapIssueResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult
)
