package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list

sealed class ReceivingFinishedGoodsPlasticOrdersListEvent {
    object InitScreen : ReceivingFinishedGoodsPlasticOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : ReceivingFinishedGoodsPlasticOrdersListEvent()
    data class OnShowItemsButtonClicked(val currentIndex: Int) : ReceivingFinishedGoodsPlasticOrdersListEvent()
}
