package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsLine
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse

import net.gbs.gbswarehouse.domain.model.Message

data class ReceivingPackagingMaterialsFromVendorState (
    val orderNumber:String="",
    val purchaseOrderGetDataStatus: PackagingPurchaseOrderGetDataStatus = PackagingPurchaseOrderGetDataStatus.IDLE,
    val isWarehousesLoading:Boolean=false,
    val purchaseOrder: PackagingMaterialsPurchaseOrder?=null,
    val materialCodeInput: String = "",
    val materialSearch:String="",
    val filteredMaterials:List<PackagingMaterialsPurchaseOrderItem> = emptyList(),
    val materials:List<PackagingMaterialsPurchaseOrderItem> = emptyList(),
    val selectedMaterial:PackagingMaterialsPurchaseOrderItem?=null,
    val warehouses:List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse?=null,
    val quantity:String = "",
    val carNo: String = "",
    val productionDate: String = "",
    val addedItems:List<PackagingMaterialsLine> = emptyList(),
    val materialCodeError : Message? = null,
    val searchError : Message? = null,
    val quantityError: Message? = null,
    val warehouseError: Message? = null,
    val carNoError: Message? = null,
    val productionDateError: Message? = null,
    val error:Message? = null,
    val savingSuccessStatusMessage:Message? = null,
    val savingErrorStatusMessage:Message? = null,
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
    val warehouseSearchText: String = "",
    val allPurchaseOrders: List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val filteredPurchaseOrders: List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val purchaseOrderSearch: String = "",
    val showChangePoDialog: Boolean = false,
    val isAllPurchaseOrdersLoading: Boolean = false,
    val showMaterialsList: Boolean = false,
    val showMaterialsListOrderIndex:Int = -1,
)

sealed class PackagingPurchaseOrderGetDataStatus {
    data object IDLE : PackagingPurchaseOrderGetDataStatus()
    data object LOADING : PackagingPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: PackagingMaterialsPurchaseOrder) : PackagingPurchaseOrderGetDataStatus()
    data class ERROR(val error: Message) : PackagingPurchaseOrderGetDataStatus()
}
