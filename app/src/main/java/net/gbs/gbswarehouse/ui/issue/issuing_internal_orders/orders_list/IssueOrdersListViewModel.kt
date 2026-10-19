package net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.domain.usecase.issuing.GetIssueToInternalOrderReservationsUseCase
import javax.inject.Inject

@HiltViewModel
class IssueOrdersListViewModel @Inject constructor(
    private val getIssueToInternalOrderReservationsUseCase: GetIssueToInternalOrderReservationsUseCase
): ViewModel() {
    var orders by mutableStateOf<List<SparePartsReturnReservationOrder>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = getIssueToInternalOrderReservationsUseCase()) {
                is NetworkResult.Success -> {
                    orders = result.data ?: emptyList()
                }
                is NetworkResult.BusinessError -> {
                    errorMessage = result.message
                }
                is NetworkResult.HttpError -> {
                    errorMessage = result.message
                }
                is NetworkResult.NetworkError -> {
                    errorMessage = result.message
                }
                is NetworkResult.UnknownError -> {
                    errorMessage = result.message
                }
                else -> {
                    errorMessage = "Unknown error"
                }
            }
            isLoading = false
        }
    }

    var orderSearch by mutableStateOf("")
    var expandedOrderNumbers by mutableStateOf<Set<String>>(emptySet())

    fun toggleOrderExpanded(reservationNumber: String) {
        expandedOrderNumbers = if (expandedOrderNumbers.contains(reservationNumber)) {
            expandedOrderNumbers - reservationNumber
        } else {
            expandedOrderNumbers + reservationNumber
        }
    }

    val filteredOrders: List<SparePartsReturnReservationOrder> get() {
        return if (orderSearch.isBlank()) orders
        else orders.filter {
            it.orderNumber?.contains(orderSearch) == true ||
            it.reservationNumber?.contains(orderSearch) == true || 
            it.orderDescription?.contains(orderSearch, ignoreCase = true) == true 
        }
    }
}
