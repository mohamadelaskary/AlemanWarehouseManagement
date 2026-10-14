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
    val packTypeCode: String? = null,
    val carType: String? = null
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
    val carNo: String? = null,
    val productionDate: String? = null
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
    val wrokerGroupCode: String? = null,
    val palletWeight: Double? = null
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
    val wrokerGroupCode: String? = null,
    val storageBinCode: String?=null,
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
data class GetRawMaterialsPurchaseOrderDataRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

@Serializable
data class ReceiveRawMaterialsResultDto(
    val receiptStatus: GoodsReceiptStatusResult? = null,
    val purchaseOrder: RawMaterialsPurchaseOrderDto? = null
)

@Serializable
data class RawMaterialsPurchaseOrderDto(
    @SerializedName("purchaseOrderNumber") val purchaseOrderNumber: String = "",
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
    @SerializedName("itemCount") val itemCount: Int? = null,
    @SerializedName("lines") val lines: List<RawMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class RawMaterialsPurchaseOrderItemDto(
    @SerializedName("poNumber") val poNumber: String? = null,
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null,
    @SerializedName("poItemNumber") val poItemNumber: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("orderedQuantity") val orderedQuantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
)

@Serializable
data class GetPackagingMaterialsPurchaseOrderDataRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

@Serializable
data class GetPoultryMaterialsPurchaseOrderDataRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

// Packaging result
@Serializable
data class ReceivePackagingMaterialsResultDto(
    val purchaseOrder: PackagingMaterialsPurchaseOrderDto? = null
)

@Serializable
data class PackagingMaterialsPurchaseOrderDto(
    @SerializedName("purchaseOrderNumber") val purchaseOrderNumber: String = "",
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
    @SerializedName("itemCount") val itemCount: Int? = null,
    @SerializedName("lines") val lines: List<PackagingMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class PackagingMaterialsPurchaseOrderItemDto(
    @SerializedName("poNumber") val poNumber: String? = null,
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null,
    @SerializedName("poItemNumber") val poItemNumber: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("orderedQuantity") val orderedQuantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null
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
    val valuationTypes: List<ValuationTypeDto>? = emptyList()
)

// Poultry result
@Serializable
data class ReceiveVendorPoultryMaterialsResultDto(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: PoultryMaterialsPurchaseOrderDto? = null
)

@Serializable
data class PoultryMaterialsPurchaseOrderDto(
    @SerializedName("purchaseOrderNumber") val purchaseOrderNumber: String = "",
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
    @SerializedName("itemCount") val itemCount: Int? = null,
    @SerializedName("lines") val lines: List<PoultryMaterialsPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class PoultryMaterialsPurchaseOrderItemDto(
    @SerializedName("poNumber") val poNumber: String? = null,
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null,
    @SerializedName("poItemNumber") val poItemNumber: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("orderedQuantity") val orderedQuantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
)

// Transit order result
@Serializable
data class GetTransitOrderMaterialPurchaseOrderDataRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

// Finished goods feed result
@Serializable
data class GetFinishedGoodsFeedOrderLinesByCarNumberRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

// Transfer order by car number
@Serializable
data class GetTransferOrderDataByCarNumberRequestDto(
    @SerializedName("carNo") val carNo: String = ""
)

@Serializable
data class TransitOrderMaterialPurchaseOrderDto(
    @SerializedName("purchaseOrderNumber") val purchaseOrderNumber: String = "",
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("warehouseCode") val warehouseCode: String? = null,
    @SerializedName("warehouseDescription") val warehouseDescription: String? = null,
    @SerializedName("companyCode") val companyCode: String = "",
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null,
    @SerializedName("itemCount") val itemCount: Int? = null,
    @SerializedName("IssuingPlantDescription") val issuingPlantDescription: String? = null,
    @SerializedName("IssuingPlantCode") val issuingPlantCode: String? = null,
    @SerializedName("lines") val lines: List<TransitOrderMaterialPurchaseOrderItemDto> = emptyList()
)

@Serializable
data class TransitOrderMaterialPurchaseOrderItemDto(
    @SerializedName("poNumber") val poNumber: String? = null,
    @SerializedName("vendorCode") val vendorCode: String? = null,
    @SerializedName("vendorName") val vendorName: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null,
    @SerializedName("poItemNumber") val poItemNumber: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("orderedQuantity") val orderedQuantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("plantDescription") val plantDescription: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("isFactoryCar") val isFactoryCar: Boolean? = null,
    @SerializedName("factoryCarDescription") val factoryCarDescription: String? = null
)



// Finished goods results
@Serializable
data class ReceiveFinishedGoodsPlasticResultDto(
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
    @SerializedName("orderedQuantity"     ) var orderedQuantity     : Double?    = null,
    @SerializedName("receivedQuantity"    ) var receivedQuantity    : Double?    = null,
    @SerializedName("remainingQuantity"   ) var remainingQuantity   : Double?    = null,
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
    val companyCode: String = "",
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
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("receiptType") val receiptType: String,
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("earliestReceivedAt") val earliestReceivedAt: String,
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("materialName") val materialName: String? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("carNumber") val carNumber: String? = null,
    @SerializedName("lines") val lines: List<PutAwayOrderLineDto> = emptyList()
)

@Serializable
data class PutAwayOrderLineDto(
    @SerializedName("goodsReceiptLineId") val goodsReceiptLineId: Int,
    @SerializedName("goodsReceiptId") val goodsReceiptId: Int,
    @SerializedName("receiptNo") val receiptNo: String,
    @SerializedName("receiptType") val receiptType: String,
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("materialName") val materialName: String? = null,
    @SerializedName("plantCode") val plantCode: String,
    @SerializedName("warehouseCode") val warehouseCode: String,
    @SerializedName("purchaseOrder") val purchaseOrder: String,
    @SerializedName("purchaseOrderItem") val purchaseOrderItem: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("receivedQuantity") val receivedQuantity: Double,
    @SerializedName("putAwayQuantity") val putAwayQuantity: Double,
    @SerializedName("remainingQuantity") val remainingQuantity: Double,
    @SerializedName("batch") val batch: String?,
    @SerializedName("expirationDate") val expirationDate: String?,
    @SerializedName("receivedAt") val receivedAt: String,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("carNumber") val carNumber: String? = null
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
    @SerializedName("quantity"                      ) var quantity                      : Double?    = null,
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
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("lines") val lines: List<SparePartsReturnReservationLineDto> = emptyList()
)

@Serializable
data class SparePartsReturnReservationLineDto(
    @SerializedName("reservationNumber") val reservationNumber: String = "",
    @SerializedName("reservationItem") val reservationItem: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("storageLocationCode") val storageLocationCode: String? = null,
    @SerializedName("movementType") val movementType: String? = null,
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("quantity") val quantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("costCenterCode") val costCenterCode: String? = null,
    @SerializedName("costCenterDescription") val costCenterDescription: String? = null,
    @SerializedName("isBatchManaged") val isBatchManaged: Boolean? = null,
    @SerializedName("valuationTypes") val valuationTypes: List<ValuationTypeDto>? = null,
    var orderDescription: String
)

// --- Transfer to Production reservation line (response) ---
// Used by GetTransferToProductionReservationLines (full payload) and by
// IssueTransferToProduction (refreshed lines only, no cost-center/valuation payload)
// so every field is optional to stay tolerant of both shapes.
@Serializable
data class TransferToProductionReservationLineDto(
    @SerializedName("reservationNumber") val reservationNumber: String = "",
    @SerializedName("reservationItem") val reservationItem: String = "",
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("storageLocationCode") val storageLocationCode: String? = null,
    @SerializedName("movementType") val movementType: String = "",
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("quantity") val quantity: Double? = null,
    @SerializedName("receivedQuantity") val receivedQuantity: Double? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("costCenterCode") val costCenterCode: String? = null,
    @SerializedName("costCenterDescription") val costCenterDescription: String? = null,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String? = null,
    @SerializedName("receivingStorageLocationCode") val receivingStorageLocationCode: String? = null,
    @SerializedName("isBatchManaged") val isBatchManaged: Boolean? = null,
    @SerializedName("valuationTypes") val valuationTypes: List<ValuationTypeDto>? = null
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
    @SerializedName("receivingBatch") val receivingBatch: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("workerGroupCode") val workersGroupCode: String,
    @SerializedName("agingGroupCode") val agingGroupCode: String
)

// Transfer to Production - result
// The endpoint answers with the refreshed reservation lines directly inside the
// response envelope, there is no inner result object:
// { "responseStatus": { ... }, "data": [ { "reservationNumber": "341734", ... } ] }
// so the result DTO is the list itself, see MobileResponse<List<TransferToProductionReservationLineDto>>

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
    @SerializedName("receivingBatch") val receivingBatch: String?,
    @SerializedName("workerGroupCode") val workerGroupCode: String?,
    @SerializedName("agingGroupCode") val agingGroupCode: String?,
    @SerializedName("notes") val notes: String?,
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
    @SerializedName("batch") val batch: String?,
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
    @SerializedName("itemText") val itemText: String?,
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("valuationType") val valuationType: String? = null,
    @SerializedName("companyCode") val companyCode: String? = null
)

@Serializable
data class PostScrapIssueResultDto(
    @SerializedName("receiptStatus") val receiptStatus: GoodsReceiptStatusResult
)

@Serializable
data class MaterialOnHandStockDto(
    @SerializedName("plantCode"      ) val plantCode      : String?  = null,
    @SerializedName("warehouseCode"  ) val warehouseCode  : String?  = null,
    @SerializedName("storageBinCode" ) val storageBinCode : String?  = null,
    @SerializedName("materialCode"   ) val materialCode   : String?  = null,
    @SerializedName("materialDescription"   ) val materialDescription   : String?  = null,
    @SerializedName("batch"          ) val batch          : String?  = null,
    @SerializedName("batchDate"      ) val batchDate          : String?  = null,
    @SerializedName("valuationType"  ) val valuationType  : String?  = null,
    @SerializedName("isBatchManaged" ) val isBatchManaged : Boolean? = null,
    @SerializedName("availableQty"   ) val availableQty   : Double?  = null,
    @SerializedName("unitOfMeasure"  ) val unitOfMeasure  : String?  = null,
    @SerializedName("uom"            ) val uom            : String?  = null
)

@Serializable
data class DirectTransferPutAwayDocumentDto(
    @SerializedName("goodsReceiptId") val goodsReceiptId: Int = 0,
    @SerializedName("receiptNo") val receiptNo: String = "",
    @SerializedName("plantCodes") val plantCodes: String? = null,
    @SerializedName("warehouseCodes") val warehouseCodes: String? = null,
    @SerializedName("receivingPlantCodes") val receivingPlantCodes: String? = null,
    @SerializedName("receivingWarehouseCodes") val receivingWarehouseCodes: String? = null,
    @SerializedName("itemCount") val itemCount: Int = 0,
    @SerializedName("createdBy") val createdBy: String? = null,
    @SerializedName("transferredAt") val transferredAt: String? = null,
    @SerializedName("materialCode") val materialCode: String? = null,
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("materialName") val materialName: String? = null,
    @SerializedName("remainingQuantity") val remainingQuantity: Double? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("carNumber") val carNumber: String? = null
)

@Serializable
data class DirectTransferPutAwayLineDto(
    @SerializedName("goodsReceiptLineId") val goodsReceiptLineId: Int = 0,
    @SerializedName("goodsReceiptId") val goodsReceiptId: Int = 0,
    @SerializedName("receiptNo") val receiptNo: String = "",
    @SerializedName("materialCode") val materialCode: String = "",
    @SerializedName("materialDescription") val materialDescription: String? = null,
    @SerializedName("materialName") val materialName: String? = null,
    @SerializedName("plantCode") val plantCode: String? = null,
    @SerializedName("warehouseCode") val warehouseCode: String? = null,
    @SerializedName("receivingPlantCode") val receivingPlantCode: String? = null,
    @SerializedName("receivingWarehouseCode") val receivingWarehouseCode: String? = null,
    @SerializedName("batch") val batch: String? = null,
    @SerializedName("valuationType") val valuationType: String? = null,
    @SerializedName("uom") val uom: String? = null,
    @SerializedName("transferredQuantity") val transferredQuantity: Double = 0.0,
    @SerializedName("putAwayQuantity") val putAwayQuantity: Double = 0.0,
    @SerializedName("remainingQuantity") val remainingQuantity: Double = 0.0,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("transferredAt") val transferredAt: String? = null,
    @SerializedName("carNo") val carNo: String? = null,
    @SerializedName("carNumber") val carNumber: String? = null
)

data class ConfirmDirectTransferPutAwayRequestDto(
    val goodsReceiptLineId: Int,
    val storageBinCode: String,
    val quantity: Double
)

@Serializable
data class ReceivingOpenOrderCountsDto(
    @SerializedName("spareParts") val spareParts: Int = 0,
    @SerializedName("packagingMaterials") val packagingMaterials: Int = 0,
    @SerializedName("rawMaterials") val rawMaterials: Int = 0,
    @SerializedName("poultryMaterials") val poultryMaterials: Int = 0,
    @SerializedName("finishedGoodsPlastic") val finishedGoodsPlastic: Int = 0,
    @SerializedName("finishedGoodsFeed") val finishedGoodsFeed: Int = 0,
    @SerializedName("transitOrderMaterial") val transitOrderMaterial: Int = 0,
    @SerializedName("sparePartsReturn") val sparePartsReturn: Int = 0,
    @SerializedName("total") val total: Int = 0
)

@Serializable
data class PutAwayOpenOrderCountsDto(
    @SerializedName("spareParts") val spareParts: Int = 0,
    @SerializedName("packagingMaterials") val packagingMaterials: Int = 0,
    @SerializedName("rawMaterials") val rawMaterials: Int = 0,
    @SerializedName("poultryMaterials") val poultryMaterials: Int = 0,
    @SerializedName("finishedGoodsPlastic") val finishedGoodsPlastic: Int = 0,
    @SerializedName("finishedGoodsFeed") val finishedGoodsFeed: Int = 0,
    @SerializedName("transitOrderMaterial") val transitOrderMaterial: Int = 0,
    @SerializedName("sparePartsReturn") val sparePartsReturn: Int = 0,
    @SerializedName("directTransfer") val directTransfer: Int = 0,
    @SerializedName("total") val total: Int = 0
)

@Serializable
data class IssuingOpenOrderCountsDto(
    @SerializedName("transferToProduction") val transferToProduction: Int = 0,
    @SerializedName("sparePartsToMaintenance") val sparePartsToMaintenance: Int = 0,
    @SerializedName("internalOrder") val internalOrder: Int = 0,
    @SerializedName("directIssueOnCostCenter") val directIssueOnCostCenter: Int = 0,
    @SerializedName("finishedGoodsFeed") val finishedGoodsFeed: Int = 0,
    @SerializedName("transferOrder") val transferOrder: Int = 0,
    @SerializedName("total") val total: Int = 0
)

@Serializable
data class BlindReceiveMaterialDto(
    @SerializedName("materialCode") val materialCode: String,
    @SerializedName("materialDescription") val materialDescription: String,
    @SerializedName("uom") val uom: String,
    @SerializedName("uomDescription") val uomDescription: String
)

@Serializable
data class UomDto(
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String
)

@Serializable
data class QualityDecisionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

@Serializable
data class PlantDto(
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String
)

@Serializable
data class CreateBlindReceiveResultDto(
    @SerializedName("blindReceiptId") val blindReceiptId: Int,
    @SerializedName("receiptNo") val receiptNo: String,
    @SerializedName("status") val status: String?
)


