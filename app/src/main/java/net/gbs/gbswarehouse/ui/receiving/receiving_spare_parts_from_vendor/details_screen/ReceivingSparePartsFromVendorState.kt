package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class ReceivingSparePartsFromVendorState (
    val orderNumber:String="",

    val purchaseOrderGetDataStatus: PurchaseOrderGetDataStatus = PurchaseOrderGetDataStatus.IDLE,

    val isWarehousesLoading:Boolean=false,

    val purchaseOrder: PurchaseOrder?=null,
    val materialCodeInput: String = "",
    val materialSearch:String="",
    val filteredMaterials:List<SparePartsMaterial> = emptyList(),
    val materials:List<SparePartsMaterial> = emptyList(),

    val selectedMaterial:SparePartsMaterial?=null,

    val warehouses:List<Warehouse> = emptyList(),
    val selectedExpiryDate : String? = null,
    val selectedWarehouse: Warehouse?=null,
    val selectedValuationType: ValuationType? = null,
    val quantity:String = "",

    val addedItems:List<SparePartsLine> = emptyList(),
    val materialCodeError : Message? = null,
    val searchError : Message? = null,
    val quantityError: Message? = null,
    val warehouseError: Message? = null,
    val valuationTypeError: Message? = null,
    val error:Message? = null,
    val savingSuccessStatusMessage:Message? = null,
    val savingErrorStatusMessage:Message? = null,
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
    val warehouseSearchText: String = "",
    val valuationSearchText: String = "",
    val transportationVendor: TransportationType? = null,
    )

sealed class PurchaseOrderGetDataStatus {
    data object IDLE : PurchaseOrderGetDataStatus()
    data object LOADING : PurchaseOrderGetDataStatus()
    data class SUCCESS(val po: PurchaseOrder) : PurchaseOrderGetDataStatus()
    data class ERROR(val error: Message) : PurchaseOrderGetDataStatus()
}