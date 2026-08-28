package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list

sealed class ReceivingFinishedGoodsFeedOrdersListEvent {
    object InitScreen : ReceivingFinishedGoodsFeedOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : ReceivingFinishedGoodsFeedOrdersListEvent()
//    data class OnShowItemsButtonClicked(val currentIndex: Int) : ReceivingFinishedGoodsFeedOrdersListEvent()
}
