package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.orders_list

sealed class IssueOrdersListEvent {
    data object Refresh : IssueOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : IssueOrdersListEvent()
    data class OnToggleOrderItems(val carNo: String) : IssueOrdersListEvent()
}
