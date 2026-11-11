package net.gbs.gbswarehouse.ui.audit.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.InventoryOrder
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.repository.AuditRepository
import javax.inject.Inject

@HiltViewModel
class AuditOrdersListViewModel @Inject constructor(
    private val auditRepository: AuditRepository
) : ViewModel() {
    var orders by mutableStateOf<List<InventoryOrder>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<Message?>(null)
    var orderSearch by mutableStateOf("")

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = auditRepository.getMyInventoryOrders()) {
                is NetworkResult.Success -> {
                    orders = result.data ?: emptyList()
                }
                is NetworkResult.BusinessError -> errorMessage = Message.Real(result.message)
                is NetworkResult.HttpError -> errorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.NetworkError -> errorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.UnknownError -> errorMessage = Message.res(R.string.error_in_connection)
                else -> errorMessage = Message.res(R.string.error_in_connection)
            }
            isLoading = false
        }
    }

    val filteredOrders: List<InventoryOrder> get() {
        return if (orderSearch.isBlank()) orders
        else orders.filter {
            it.orderNumber.contains(orderSearch, ignoreCase = true) ||
            (it.description?.contains(orderSearch, ignoreCase = true) == true) ||
            (it.warehouseCode?.contains(orderSearch, ignoreCase = true) == true) ||
            (it.plantCode?.contains(orderSearch, ignoreCase = true) == true)
        }
    }
}
