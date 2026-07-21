package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class ReceivingSparePartsFromVendorState (
    val orderNumber:String="",

    val purchaseOrderSearchStatus: PurchaseOrderSearchStatus = PurchaseOrderSearchStatus.IDLE,

    val isWarehousesLoading:Boolean=false,

    val purchaseOrder: PurchaseOrder?=null,
    val materialSearch:String="",
    val filteredMaterials:List<SparePartsMaterial> = emptyList(),
    val materials:List<SparePartsMaterial> = emptyList(),

    val selectedMaterial:SparePartsMaterial?=null,

    val warehouses:List<Warehouse> = emptyList(),
    val selectedExpiryDate : String = "",
    val selectedWarehouse: Warehouse?=null,
    val selectedValuationType: ValuationType? = null,
    val quantity:String = "",

    val addedItems:List<SparePartsLine> = emptyList(),
    val materialCodeError : String = "",
    val searchError : String = "",
    val quantityError: String = "",
    val warehouseError: String = "",
    val valuationTypeError: String = "",
    val error:String="",
    val savingSuccessStatusMessage:String = "",
    val savingErrorStatusMessage:String = "",
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
)

enum class PurchaseOrderSearchStatus {
    IDLE, LOADING, SUCCESS, ERROR
}