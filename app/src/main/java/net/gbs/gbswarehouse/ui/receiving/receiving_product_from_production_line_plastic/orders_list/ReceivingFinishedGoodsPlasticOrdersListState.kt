package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list

import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrder

data class ReceivingFinishedGoodsPlasticOrdersListState (
    val orderList: List<FinishedGoodsPlasticProductionOrder> = emptyList(),
    val orderFilteredList: List<FinishedGoodsPlasticProductionOrder> = emptyList(),
    val searchText: String = "",
    val loadingStatus: LoadingFinishedGoodsPlasticOrdersListStatus = LoadingFinishedGoodsPlasticOrdersListStatus.LOADING,
    val isItemsShowed: Boolean = false,
    val showItemsClickedCardIndex: Int = -1
)

sealed class LoadingFinishedGoodsPlasticOrdersListStatus {
    object IDLE : LoadingFinishedGoodsPlasticOrdersListStatus()
    data object LOADING : LoadingFinishedGoodsPlasticOrdersListStatus()
    data class SUCCESS(val ordersList: List<FinishedGoodsPlasticProductionOrder>) : LoadingFinishedGoodsPlasticOrdersListStatus()
    data class ERROR(val statusMessage: String) : LoadingFinishedGoodsPlasticOrdersListStatus()
}
