package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedOrder
import net.gbs.gbswarehouse.domain.usecase.GetFinishedGoodsFeedOrdersUseCase
import javax.inject.Inject

@HiltViewModel
class IssueOrdersListViewModel @Inject constructor(
    private val getFinishedGoodsFeedOrdersUseCase: GetFinishedGoodsFeedOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(IssueOrdersListState())
    val state = _state.asStateFlow()

    init {
        onEvent(IssueOrdersListEvent.Refresh)
    }

    fun onEvent(event: IssueOrdersListEvent) {
        when (event) {
            is IssueOrdersListEvent.Refresh -> getOrders()
            is IssueOrdersListEvent.OnSearchTextChanged -> {
                _state.update { it.copy(orderSearch = event.text) }
                filterOrders()
            }
            is IssueOrdersListEvent.OnToggleOrderItems -> {
                _state.update {
                    val current = it.expandedCarNumbers
                    val newSet = if (current.contains(event.carNo)) {
                        current - event.carNo
                    } else {
                        current + event.carNo
                    }
                    it.copy(expandedCarNumbers = newSet)
                }
            }
        }
    }

    private fun getOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getFinishedGoodsFeedOrdersUseCase()) {
                is NetworkResult.Success -> {
                    val orders = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = orders,
                            filteredOrders = orders
                        )
                    }
                    filterOrders()
                }
                is NetworkResult.BusinessError -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.HttpError -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.NetworkError -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.UnknownError -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun filterOrders() {
        val query = _state.value.orderSearch
        val filtered = if (query.isEmpty()) {
            _state.value.orders
        } else {
            _state.value.orders.filter { matchesSearch(it, query) }
        }
        _state.update { it.copy(filteredOrders = filtered) }
    }

    private fun matchesSearch(order: FinishedGoodsFeedOrder, query: String): Boolean {
        return order.carNo?.contains(query, ignoreCase = true) == true ||
                order.factoryCarDescription?.contains(query, ignoreCase = true) == true ||
                order.customerName?.contains(query, ignoreCase = true) == true ||
                order.customerCode?.contains(query, ignoreCase = true) == true ||
                order.lines.any {
                    it.salesOrder?.contains(query, ignoreCase = true) == true ||
                            it.materialCode?.contains(query, ignoreCase = true) == true ||
                            it.materialDescription?.contains(query, ignoreCase = true) == true
                }
    }
}
