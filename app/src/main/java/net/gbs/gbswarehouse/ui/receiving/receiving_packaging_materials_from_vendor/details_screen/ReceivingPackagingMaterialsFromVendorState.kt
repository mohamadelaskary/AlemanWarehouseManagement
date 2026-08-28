package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsLine
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse

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
    val addedItems:List<PackagingMaterialsLine> = emptyList(),
    val materialCodeError : String = "",
    val searchError : String = "",
    val quantityError: String = "",
    val warehouseError: String = "",
    val carNoError: String = "",
    val error:String="",
    val savingSuccessStatusMessage:String = "",
    val savingErrorStatusMessage:String = "",
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
    val warehouseSearchText: String = "",
    val allPurchaseOrders: List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val filteredPurchaseOrders: List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val purchaseOrderSearch: String = "",
    val showChangePoDialog: Boolean = false,
    val isAllPurchaseOrdersLoading: Boolean = false,
)

sealed class PackagingPurchaseOrderGetDataStatus {
    data object IDLE : PackagingPurchaseOrderGetDataStatus()
    data object LOADING : PackagingPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: PackagingMaterialsPurchaseOrder) : PackagingPurchaseOrderGetDataStatus()
    data class ERROR(val error: String) : PackagingPurchaseOrderGetDataStatus()
}
