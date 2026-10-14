package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingPoultryFromVendorListViewModel @Inject constructor(
    private val getAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase: GetAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingPoultryFromVendorOrdersListState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ReceivingPoultryFromVendorOrdersListEvent) {
        when (event) {
            ReceivingPoultryFromVendorOrdersListEvent.InitScreen -> {
                loadOrders()
            }
            is ReceivingPoultryFromVendorOrdersListEvent.OnSearchTextChanged -> {
                _uiState.update { it.copy(searchText = event.text) }
                filterOrders(event.text)
            }
            is ReceivingPoultryFromVendorOrdersListEvent.OnShowMaterialsButtonClicked -> {
                _uiState.update {
                    if (it.showMaterialsClickedCardIndex == event.currentIndex) {
                        it.copy(isMaterialsShowed = !it.isMaterialsShowed)
                    } else {
                        it.copy(
                            isMaterialsShowed = true,
                            showMaterialsClickedCardIndex = event.currentIndex
                        )
                    }
                }
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingStatus = LoadingPoultryPurchaseOrdersListStatus.LOADING) }
            try {
                val result = getAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase()
                when (result) {
                    is NetworkResult.Success -> {
                        val orders = result.data
                        _uiState.update {
                            it.copy(
                                poList = orders,
                                poFilteredList = if (it.searchText.isBlank()) orders else orders.filter { po -> matchesSearch(po, it.searchText) },
                                loadingStatus = LoadingPoultryPurchaseOrdersListStatus.SUCCESS(orders)
                            )
                        }
                    }
                    else -> {
                        val message = when (result) {
                            is NetworkResult.BusinessError -> result.message
                            is NetworkResult.HttpError -> result.message
                            is NetworkResult.NetworkError -> result.message
                            is NetworkResult.UnknownError -> result.message
                            else -> "Unknown Error"
                        }
                        _uiState.update {
                            it.copy(loadingStatus = LoadingPoultryPurchaseOrdersListStatus.ERROR(message))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingStatus = LoadingPoultryPurchaseOrdersListStatus.ERROR(e.localizedMessage ?: "Error"))
                }
            }
        }
    }

    private fun filterOrders(query: String) {
        _uiState.update { state ->
            val filteredList = if (query.isBlank()) {
                state.poList
            } else {
                state.poList.filter { matchesSearch(it, query) }
            }
            state.copy(poFilteredList = filteredList)
        }
    }

    private fun matchesSearch(order: PoultryMaterialsPurchaseOrder, query: String): Boolean =
        order.carNo?.contains(query, ignoreCase = true) == true ||
                order.factoryCarDescription?.contains(query, ignoreCase = true) == true ||
                (order.vendorName?.contains(query, ignoreCase = true) == true) ||
                (order.vendorCode?.contains(query, ignoreCase = true) == true) ||
                order.purchaseOrderItems.any { item ->
                    item.materialCode?.contains(query, ignoreCase = true) == true ||
                            item.materialDescription?.contains(query, ignoreCase = true) == true ||
                            item.poNumber?.contains(query, ignoreCase = true) == true
                }
}
