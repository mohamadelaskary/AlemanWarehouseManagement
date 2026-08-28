package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list

sealed class ReceivingRawMaterialsFromVendorOrdersListEvent {
    data class OnSearchTextChanged(val searchText:String) : ReceivingRawMaterialsFromVendorOrdersListEvent()
    data class OnShowMaterialsClicked(val index:Int) : ReceivingRawMaterialsFromVendorOrdersListEvent()
    data object Refresh : ReceivingRawMaterialsFromVendorOrdersListEvent()
}
