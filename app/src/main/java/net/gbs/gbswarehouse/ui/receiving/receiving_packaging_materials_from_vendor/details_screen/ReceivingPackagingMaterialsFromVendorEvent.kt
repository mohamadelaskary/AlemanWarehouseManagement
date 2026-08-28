package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.domain.model.Warehouse

sealed class ReceivingPackagingMaterialsFromVendorEvent {
    data class OnScreenLoaded(val poNumber: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnMaterialCodeInputEntered(val materialCode: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnMaterialSearchTextEntered(val text: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnCarNoEntered(val carNo: String) : ReceivingPackagingMaterialsFromVendorEvent()
    object OnAddButtonClicked : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnDeleteAddedMaterialButtonClicked(val index: Int) : ReceivingPackagingMaterialsFromVendorEvent()
    object OnSaveButtonClicked : ReceivingPackagingMaterialsFromVendorEvent()
    object OnClearMaterialButtonClicked : ReceivingPackagingMaterialsFromVendorEvent()
    object HideSuccessDialog : ReceivingPackagingMaterialsFromVendorEvent()
    object HideErrorDialog : ReceivingPackagingMaterialsFromVendorEvent()
    
    object OnChangePoButtonClicked : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnPurchaseOrderSearchTextEntered(val text: String) : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnPurchaseOrderSelected(val poNumber: String) : ReceivingPackagingMaterialsFromVendorEvent()
    object OnDismissChangePoDialog : ReceivingPackagingMaterialsFromVendorEvent()
    data class OnWarehouseSearchTextChange(val searchText: String) : ReceivingPackagingMaterialsFromVendorEvent()
}
