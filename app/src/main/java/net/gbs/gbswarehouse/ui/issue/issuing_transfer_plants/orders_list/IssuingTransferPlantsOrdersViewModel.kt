package net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.TransferOrder
import net.gbs.gbswarehouse.domain.usecase.issuing.GetTransferOrdersUseCase
import javax.inject.Inject

@HiltViewModel
class IssuingTransferPlantsOrdersViewModel @Inject constructor(
    private val getTransferOrdersUseCase: GetTransferOrdersUseCase
): ViewModel() {
    var orders by mutableStateOf<List<TransferOrder>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var orderSearch by mutableStateOf("")
    var expandedCarNumbers by mutableStateOf<Set<String>>(emptySet())

    fun toggleOrderExpanded(carNo: String) {
        expandedCarNumbers = if (expandedCarNumbers.contains(carNo)) {
            expandedCarNumbers - carNo
        } else {
            expandedCarNumbers + carNo
        }
    }

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = getTransferOrdersUseCase()) {
                is NetworkResult.Success -> {
                    orders = result.data ?: emptyList()
                }
                is NetworkResult.BusinessError -> errorMessage = result.message
                is NetworkResult.HttpError -> errorMessage = result.message
                is NetworkResult.NetworkError -> errorMessage = result.message
                is NetworkResult.UnknownError -> errorMessage = result.message
                else -> errorMessage = "Unknown error"
            }
            isLoading = false
        }
    }

    val filteredOrders: List<TransferOrder> get() {
        return if (orderSearch.isBlank()) orders
        else orders.filter { order ->
            order.carNo?.contains(orderSearch, ignoreCase = true) == true ||
            order.factoryCarDescription?.contains(orderSearch, ignoreCase = true) == true ||
            order.issuingPlantDescription?.contains(orderSearch, ignoreCase = true) == true ||
            order.lines.any { line ->
                line.poNumber?.contains(orderSearch, ignoreCase = true) == true ||
                line.companyCode?.contains(orderSearch, ignoreCase = true) == true ||
                line.receivingPlantDescription?.contains(orderSearch, ignoreCase = true) == true ||
                line.materialCode?.contains(orderSearch, ignoreCase = true) == true ||
                line.materialDescription?.contains(orderSearch, ignoreCase = true) == true
            }
        }
    }
}
