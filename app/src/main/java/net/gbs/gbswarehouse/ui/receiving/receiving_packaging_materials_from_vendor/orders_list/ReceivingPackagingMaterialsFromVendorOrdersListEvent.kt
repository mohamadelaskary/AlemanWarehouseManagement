package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list

sealed class ReceivingPackagingMaterialsFromVendorOrdersListEvent {
    object InitScreen : ReceivingPackagingMaterialsFromVendorOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : ReceivingPackagingMaterialsFromVendorOrdersListEvent()
    data class OnShowMaterialsButtonClicked(val currentIndex: Int) : ReceivingPackagingMaterialsFromVendorOrdersListEvent()
}
