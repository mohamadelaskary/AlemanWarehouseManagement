package net.gbs.gbswarehouse.ui.plc.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.usecase.loading.LoadingUseCases
import javax.inject.Inject

enum class OrderTypeFilter {
    ALL,
    FINISHED_GOODS_FEED,
    TRANSFER_ORDER
}

@HiltViewModel
class LoadingOrdersViewModel @Inject constructor(
    private val useCases: LoadingUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialOrderNumber: String = savedStateHandle["orderNumber"] ?: ""

    private val _orders = MutableStateFlow<List<LoadingOrder>>(emptyList())
    val orders: StateFlow<List<LoadingOrder>> = _orders.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<LoadingOrder>>(emptyList())
    val filteredOrders: StateFlow<List<LoadingOrder>> = _filteredOrders.asStateFlow()

    private val _searchText = MutableStateFlow(initialOrderNumber)
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedFilter = MutableStateFlow(OrderTypeFilter.ALL)
    val selectedFilter: StateFlow<OrderTypeFilter> = _selectedFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<Message?>(null)
    val errorMessage: StateFlow<Message?> = _errorMessage.asStateFlow()

    init {
        loadOrders()
        observeProgress()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = useCases.getLoadingOrders()) {
                is NetworkResult.Success -> {
                    _orders.value = result.data
                    filterOrders()
                    _isLoading.value = false
                }
                is NetworkResult.Loading -> _isLoading.value = true
                else -> {
                    _isLoading.value = false
                    _errorMessage.value = result.errorMessageOrDefault()
                }
            }
        }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            useCases.observeLoadingOperations().drop(1).collect {
                val cached = useCases.getCachedLoadingOrders() ?: return@collect
                _orders.value = cached
                filterOrders()
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        filterOrders()
    }

    fun onFilterSelected(filter: OrderTypeFilter) {
        _selectedFilter.value = filter
        filterOrders()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun filterOrders() {
        val query = _searchText.value.trim()
        val filter = _selectedFilter.value
        _filteredOrders.value = _orders.value.filter { order ->
            val matchesSearch = query.isEmpty() ||
                    order.orderNumber.contains(query, ignoreCase = true) ||
                    order.vehicleNumber.contains(query, ignoreCase = true) ||
                    order.items.any { item -> item.salesOrder?.contains(query, ignoreCase = true) == true }

            val matchesType = when (filter) {
                OrderTypeFilter.ALL -> true
                OrderTypeFilter.FINISHED_GOODS_FEED -> order.orderType.equals("IssueFinishedGoodsFeed", ignoreCase = true)
                OrderTypeFilter.TRANSFER_ORDER -> order.orderType.equals("IssueTransferOrder", ignoreCase = true)
            }

            matchesSearch && matchesType
        }
    }

    private fun NetworkResult<*>.errorMessageOrDefault(): Message = when (this) {
        is NetworkResult.BusinessError -> Message.real(message)
        is NetworkResult.HttpError -> Message.real(message)
        is NetworkResult.NetworkError -> Message.real(message)
        is NetworkResult.UnknownError -> Message.real(message)
        else -> Message.Resource(R.string.unknown_error)
    }
}
