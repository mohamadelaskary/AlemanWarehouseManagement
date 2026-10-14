package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.orders_list

import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedOrder

data class IssueOrdersListState(
    val orders: List<FinishedGoodsFeedOrder> = emptyList(),
    val filteredOrders: List<FinishedGoodsFeedOrder> = emptyList(),
    val orderSearch: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedCarNumbers: Set<String> = emptySet()
)
