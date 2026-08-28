package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list

import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrder

data class ReceivingFinishedGoodsFeedOrdersListState (
    val orderList: List<FinishedGoodsFeedProductionOrder> = emptyList(),
    val orderFilteredList: List<FinishedGoodsFeedProductionOrder> = emptyList(),
    val searchText: String = "",
    val loadingStatus: LoadingFinishedGoodsFeedOrdersListStatus = LoadingFinishedGoodsFeedOrdersListStatus.LOADING,
//    val isItemsShowed: Boolean = false,
//    val showItemsClickedCardIndex: Int = -1
)

sealed class LoadingFinishedGoodsFeedOrdersListStatus {
    object IDLE : LoadingFinishedGoodsFeedOrdersListStatus()
    data object LOADING : LoadingFinishedGoodsFeedOrdersListStatus()
    data class SUCCESS(val ordersList: List<FinishedGoodsFeedProductionOrder>) : LoadingFinishedGoodsFeedOrdersListStatus()
    data class ERROR(val statusMessage: String) : LoadingFinishedGoodsFeedOrdersListStatus()
}
