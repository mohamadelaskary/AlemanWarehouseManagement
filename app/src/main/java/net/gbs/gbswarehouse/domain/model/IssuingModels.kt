package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class SAPWarehouse(
    val warehouseId: Long,
    val warehouseCode: String,
    val warehouseDescription: String?,
    val plantCode: String
)

data class SAPMaterialBatchValuationStock(
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

data class TransferToProductionReservationLine(
    val reservationNumber: String,
    val reservationItem: String,
    val materialCode: String?,
    val materialDescription: String?,
    val uom: String?,
    val plantCode: String?,
    val storageLocationCode: String?,
    val movementType: String,
    val batch: String?,
    val quantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val costCenterCode: String?,
    val costCenterDescription: String?,
    val receivingPlantCode: String?,
    val receivingStorageLocationCode: String?,
    val isBatchManaged: Boolean?,
    val valuationTypes: List<ValuationType>
)

data class PostTransferToProductionResult(
    val reservationLines: List<TransferToProductionReservationLine>
)

data class PostSparePartsIssueToMaintenanceResult(
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class PostIssueToInternalOrderResult(
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class PostDirectTransferResult(
    val receiptStatus: GoodsReceiptStatusResult
)

data class PostDirectIssueOnCostCenterResult(
    val reservationLines: List<DirectIssueReservationLine>
)

data class PostScrapIssueResult(
    val receiptStatus: GoodsReceiptStatusResult
)

data class MaterialOnHandStock(
    val plantCode: String?,
    val warehouseCode: String?,
    val storageBinCode: String?,
    val materialCode: String?,
    val materialDescription: String? = null,
    val batch: String?,
    val batchDate: String?,
    val valuationType: String?,
    val isBatchManaged: Boolean?,
    val availableQty: Double?,
    val uom: String? = null
)

data class TransferOrder(
    val transferOrderNumber: String?,
    val issuingPlantDescription: String?,
    val carNo: String?,
    val isFactoryCar: Boolean = true,
    val factoryCarDescription: String?,
    val itemCount: Int?,
    val lines: List<TransferOrderItem> = emptyList()
) {
    val poNumber: String?
        get() = carNo ?: transferOrderNumber
}

data class TransferOrderItem(
    val poNumber: String?,
    val companyCode: String?,
    val poItemNumber: String?,
    val materialCode: String?,
    val materialDescription: String?,
    val orderedQuantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val issuingPlantCode: String?,
    val issuingPlantDescription: String?,
    val receivingPlantCode: String?,
    val receivingPlantDescription: String?,
    val carNo: String?,
    val factoryCarDescription: String?
)

data class PostTransferOrderResult(
    val transferOrder: TransferOrder?
)

data class DirectTransferPutAwayDocument(
    val goodsReceiptId: Int,
    val receiptNo: String,
    val plantCodes: String?,
    val warehouseCodes: String?,
    val receivingPlantCodes: String?,
    val receivingWarehouseCodes: String?,
    val itemCount: Int,
    val createdBy: String?,
    val transferredAt: String?,
    val materialCode: String? = null,
    val materialDescription: String? = null,
    val remainingQuantity: Double? = null,
    val carNo: String? = null
)

data class DirectTransferPutAwayLine(
    val goodsReceiptLineId: Int,
    val goodsReceiptId: Int,
    val receiptNo: String,
    val materialCode: String,
    val materialDescription: String? = null,
    val plantCode: String?,
    val warehouseCode: String?,
    val receivingPlantCode: String?,
    val receivingWarehouseCode: String?,
    val batch: String?,
    val valuationType: String?,
    val uom: String?,
    val transferredQuantity: Double,
    val putAwayQuantity: Double,
    val remainingQuantity: Double,
    val notes: String?,
    val transferredAt: String?,
    val carNo: String? = null
)

data class IssuingOpenOrderCounts(
    val transferToProduction: Int = 0,
    val sparePartsToMaintenance: Int = 0,
    val internalOrder: Int = 0,
    val directIssueOnCostCenter: Int = 0,
    val finishedGoodsFeed: Int = 0,
    val transferOrder: Int = 0,
    val total: Int = 0
)

data class MaterialBinLocation(
    val storageBinName: String?,
    val onHandQty: Double?,
    val reservedQty: Double?,
    val plantCode: String?,
    val warehouseCode: String?,
    val storageBinCode: String?,
    val materialCode: String?,
    val materialDescription: String?,
    val uom: String?,
    val batch: String?,
    val batchDate: String?,
    val valuationType: String?,
    val isBatchManaged: Boolean?,
    val availableQty: Double?
)

