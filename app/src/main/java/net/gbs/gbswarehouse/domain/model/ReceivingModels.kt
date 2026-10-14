package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName
import net.gbs.gbswarehouse.domain.model.ValuationType

data class SparePartsPurchaseOrder(
    val poNumber: String,
    val vendorCode: String?,
    val vendorName: String?,
    val companyCode: String,
    val purchaseOrderItems: List<SparePartsPurchaseOrderItem>
)
data class PoultryMaterialsLine (
    val materialCode: String = "",
    val materialDescription: String = "",
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
data class SparePartsPurchaseOrderItem(
    val poItemNumber: String,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val plantCode: String?,
    val isBatchManaged: Boolean?,
    val valuationTypes: List<ValuationType>
)

data class RawMaterialsPurchaseOrder(
    val purchaseOrderNumber: String,
    val vendorCode: String?,
    val vendorName: String?,
    val carNo: String?,
    val isFactoryCar: Boolean?,
    val factoryCarDescription: String?,
    val itemCount: Int?,
    val purchaseOrderItems: List<RawMaterialsPurchaseOrderItem>
) {
    val poNumber: String get() = purchaseOrderNumber
}

data class RawMaterialsPurchaseOrderItem(
    val poNumber: String?,
    val vendorCode: String?,
    val vendorName: String?,
    val companyCode: String?,
    val poItemNumber: String,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val plantCode: String?,
    val carNo: String?,
    val isFactoryCar: Boolean?,
    val factoryCarDescription: String?
)

data class PackagingMaterialsPurchaseOrder(
    val purchaseOrderNumber: String,
    val vendorCode: String?,
    val vendorName: String?,
    val carNo: String?,
    val isFactoryCar: Boolean?,
    val factoryCarDescription: String?,
    val itemCount: Int?,
    val purchaseOrderItems: List<PackagingMaterialsPurchaseOrderItem>
) {
    val poNumber: String get() = carNo ?: purchaseOrderNumber
}

data class PackagingMaterialsPurchaseOrderItem(
    val poNumber: String?,
    val vendorCode: String?,
    val vendorName: String?,
    val companyCode: String?,
    val poItemNumber: String,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val plantCode: String?,
    val carNo: String?,
    val isFactoryCar: Boolean?,
    val factoryCarDescription: String?
)

data class PoultryMaterialsPurchaseOrder(
    val purchaseOrderNumber: String,
    val vendorCode: String?,
    val vendorName: String?,
    val carNo: String?,
    val isFactoryCar: Boolean?,
    val factoryCarDescription: String?,
    val itemCount: Int?,
    val purchaseOrderItems: List<PoultryMaterialsPurchaseOrderItem>
) {
    val poNumber: String get() = carNo ?: purchaseOrderNumber
}

data class PoultryMaterialsPurchaseOrderItem(
    val poNumber: String?,
    val vendorCode: String?,
    val vendorName: String?,
    val companyCode: String?,
    val poItemNumber: String,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val plantCode: String?,
    val carNo: String?,
    val isFactoryCar: Boolean = true,
    val factoryCarDescription: String? = null
)

data class TransitOrderMaterialPurchaseOrder(
    val purchaseOrderNumber: String = "",
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val warehouseCode: String? = null,
    val warehouseDescription: String? = null,
    val companyCode: String = "",
    val carNo: String?,
    val isFactoryCar: Boolean? = null,
    val factoryCarDescription: String? = null,
    val itemCount: Int? = null,
    val purchaseOrderItems: List<TransitOrderMaterialPurchaseOrderItem> = emptyList(),
    val issuingPlantCode: String? = null,
    val issuingPlantDescription: String? = null,
) {
    val poNumber: String get() = carNo ?: purchaseOrderNumber
}

data class TransitOrderMaterialPurchaseOrderItem(
    val poNumber: String? = null,
    val vendorCode: String? = null,
    val vendorName: String? = null,
    val companyCode: String? = null,
    val poItemNumber: String = "",
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val orderedQuantity: Double? = null,
    val receivedQuantity: Double? = null,
    val remainingQuantity: Double? = null,
    val uom: String? = null,
    val plantCode: String? = null,
    val plantDescription: String? = null,
    val carNo: String?,
    val isFactoryCar : Boolean = true,
    val factoryCarDescription: String? = null
)

data class TransitOrderMaterialLine(
    val materialCode: String = "",
    val materialDescription: String = "",
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

data class FinishedGoodsPlasticProductionOrder(
    val orderNumber: String,
    val productionOrderItems: List<FinishedGoodsPlasticProductionOrderItem>
)

data class FinishedGoodsPlasticProductionOrderItem(
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
    @SerializedName("basicStartDate"      ) var basicStartDate      : String? = null,
    var palletWeight: Double? = null
)

data class FinishedGoodsFeedProductionOrder(
    val orderNumber: String,
    val productionOrderItems: List<FinishedGoodsFeedProductionOrderItem>
)

data class FinishedGoodsFeedProductionOrderItem(
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

data class GoodsReceiptStatusResult(
    val goodsReceiptId: Long,
    val receiptNo: String,
    val receiptStatus: String,
    val readyForPutAway: Boolean,
    val needsAction: Boolean,
    val hasOrphanedLines: Boolean,
    val lines: List<GoodsReceiptLineStatusResult>
)

data class GoodsReceiptLineStatusResult(
    val goodsReceiptLineId: Long,
    val material: String,
    val receivedQuantity: Double,
    val sapOutboxMessageId: Long?,
    val sapStatus: String?,
    val sapMaterialDocument: String?,
    val sapMaterialDocumentYear: String?,
    val attemptCount: Int,
    val maxAttempts: Int,
    val lastErrorMessage: String?
)

data class ReceiveRawMaterialsResult(
    val receiptStatus: GoodsReceiptStatusResult?,
    val purchaseOrder: RawMaterialsPurchaseOrder?
)

data class ReceivePackagingMaterialsResult(
    val purchaseOrder: PackagingMaterialsPurchaseOrder?
)

data class ReceiveVendorPoultryMaterialsResult(
    val receiptStatus: GoodsReceiptStatusResult,
    val purchaseOrder: PoultryMaterialsPurchaseOrder?
)

data class ReceiveTransitOrderMaterialResult(
    val purchaseOrder: TransitOrderMaterialPurchaseOrder?
)

data class PostSparePartsReturnResult(
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class SparePartsReturnReservationLine(
    @SerializedName("reservationNumber"     ) var reservationNumber     : String?                   = null,
    @SerializedName("reservationItem"       ) var reservationItem       : String?                   = null,
    @SerializedName("materialCode"          ) var materialCode          : String?                   = null,
    @SerializedName("materialDescription"   ) var materialDescription   : String?                   = null,
    @SerializedName("uom"                   ) var uom                   : String?                   = null,
    @SerializedName("plantCode"             ) var plantCode             : String?                   = null,
    @SerializedName("storageLocationCode"   ) var storageLocationCode   : String?                   = null,
    @SerializedName("movementType"          ) var movementType          : String?                   = null,
    @SerializedName("batch"                 ) var batch                 : String?                   = null,
    @SerializedName("quantity"              ) var quantity              : Double?                      = null,
    @SerializedName("receivedQuantity"      ) var receivedQuantity      : Double?                      = null,
    @SerializedName("remainingQuantity"     ) var remainingQuantity     : Double?                      = null,
    @SerializedName("costCenterCode"        ) var costCenterCode        : String?                   = null,
    @SerializedName("costCenterDescription" ) var costCenterDescription : String?                   = null,
    @SerializedName("isBatchManaged"        ) var isBatchManaged        : Boolean?                  = null,
    @SerializedName("valuationTypes"        ) var valuationTypes        : List<ValuationType> = arrayListOf(),
    var orderDescription: String
)

data class ReceiveFinishedGoodsPlasticResult(
    val productionOrder: FinishedGoodsPlasticProductionOrder?
)

data class ReceiveFinishedGoodsFeedResult(
    val productionOrder: FinishedGoodsFeedProductionOrder?
)

data class LoadingVendor(
    val vendorCode: String,
    val vendorName: String
)

data class PutAwayOrder(
    val orderNumber: String,
    val receiptType: String,
    val itemCount: Int,
    val earliestReceivedAt: String,
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val remainingQuantity: Double? = null,
    val carNo: String? = null,
    val lines: List<PutAwayOrderLine> = emptyList()
)

data class PutAwayOrderLine(
    val goodsReceiptLineId: Int,
    val goodsReceiptId: Int,
    val receiptNo: String,
    val receiptType: String,
    val materialCode: String,
    val materialDescription: String? = null,
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
    val receivedAt: String,
    val carNo: String? = null
)

data class StorageBinLocation(
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

data class ReceivingOpenOrderCounts(
    val spareParts: Int = 0,
    val packagingMaterials: Int = 0,
    val rawMaterials: Int = 0,
    val poultryMaterials: Int = 0,
    val finishedGoodsPlastic: Int = 0,
    val finishedGoodsFeed: Int = 0,
    val transitOrderMaterial: Int = 0,
    val sparePartsReturn: Int = 0,
    val total: Int = 0
)

data class PutAwayOpenOrderCounts(
    val spareParts: Int = 0,
    val packagingMaterials: Int = 0,
    val rawMaterials: Int = 0,
    val poultryMaterials: Int = 0,
    val finishedGoodsPlastic: Int = 0,
    val finishedGoodsFeed: Int = 0,
    val transitOrderMaterial: Int = 0,
    val sparePartsReturn: Int = 0,
    val directTransfer: Int = 0,
    val total: Int = 0
)

data class Plant(
    val code: String,
    val description: String
) {
    fun spinnerLabel(): String = "$description ($code)"
}

data class BlindReceiveMaterial(
    val materialCode: String,
    val materialDescription: String,
    val uom: String,
    val uomDescription: String
) {
    fun spinnerLabel(): String = "$materialCode - $materialDescription"
}

data class Uom(
    val code: String,
    val description: String
) {
    fun spinnerLabel(): String = "$description ($code)"
}

data class CreateBlindReceiveResult(
    val blindReceiptId: Int,
    val receiptNo: String,
    val status: String?
)

