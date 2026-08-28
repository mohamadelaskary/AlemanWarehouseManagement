package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

sealed class ReceivingPoultryFromVendorOrdersListEvent {
    object InitScreen : ReceivingPoultryFromVendorOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : ReceivingPoultryFromVendorOrdersListEvent()
    data class OnShowMaterialsButtonClicked(val currentIndex: Int) : ReceivingPoultryFromVendorOrdersListEvent()
}
