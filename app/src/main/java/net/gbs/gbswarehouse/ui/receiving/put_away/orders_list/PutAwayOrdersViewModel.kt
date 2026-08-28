package net.gbs.gbswarehouse.ui.receiving.put_away.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.PutAwayOrder
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

@HiltViewModel
class PutAwayOrdersViewModel @Inject constructor(
    private val repository: ReceivingRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<PutAwayOrder>>(emptyList())
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<PutAwayOrder>>(emptyList())
    val orders: StateFlow<List<PutAwayOrder>> = _filteredOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        filterOrders()
    }

    private fun filterOrders() {
        val query = _searchText.value.trim()
        _filteredOrders.value = if (query.isEmpty()) {
            _orders.value
        } else {
            _orders.value.filter { it.orderNumber.contains(query, ignoreCase = true) }
        }
    }

    fun getOrders(receiptType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.getPutAwayOrders(receiptType)) {
                is NetworkResult.Success -> {
                    _orders.value = result.data
                    filterOrders()
                }
                is NetworkResult.BusinessError -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.HttpError -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.NetworkError -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.UnknownError -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    _isLoading.value = true
                }
            }
            _isLoading.value = false
        }
    }
}
