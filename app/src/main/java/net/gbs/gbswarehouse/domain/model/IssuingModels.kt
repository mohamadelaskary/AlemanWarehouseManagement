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
    @SerializedName("quantity"                      ) var quantity                      : Int?    = null,
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
    val receiptStatus: GoodsReceiptStatusResult,
    val reservationLines: List<TransferToProductionReservationLine>
)

data class PostSparePartsIssueToMaintenanceResult(
    val receiptStatus: GoodsReceiptStatusResult,
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class PostIssueToInternalOrderResult(
    val receiptStatus: GoodsReceiptStatusResult,
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class PostDirectTransferResult(
    val receiptStatus: GoodsReceiptStatusResult
)

data class PostDirectIssueOnCostCenterResult(
    val receiptStatus: GoodsReceiptStatusResult,
    val reservationLines: List<SparePartsReturnReservationLine>
)

data class PostScrapIssueResult(
    val receiptStatus: GoodsReceiptStatusResult
)
