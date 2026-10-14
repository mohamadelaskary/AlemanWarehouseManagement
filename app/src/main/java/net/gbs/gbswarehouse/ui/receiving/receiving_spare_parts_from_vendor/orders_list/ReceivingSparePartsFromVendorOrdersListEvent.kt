package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list

sealed interface ReceivingSparePartsFromVendorOrdersListEvent{
    data object InitScreen : ReceivingSparePartsFromVendorOrdersListEvent
    data class OnSearchTextChanged(val text: String) : ReceivingSparePartsFromVendorOrdersListEvent
    data class OnGroupSelected(val group: OrderGroup) : ReceivingSparePartsFromVendorOrdersListEvent
    data class OnShowMaterialsButtonClicked(val currentIndex: Int) : ReceivingSparePartsFromVendorOrdersListEvent
}
