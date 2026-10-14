package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingPackagingMaterialsFromVendorListViewModel @Inject constructor(
    private val getAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase: GetAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingPackagingMaterialsFromVendorOrdersListState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ReceivingPackagingMaterialsFromVendorOrdersListEvent) {
        when (event) {
            ReceivingPackagingMaterialsFromVendorOrdersListEvent.InitScreen -> {
                loadOrders()
            }
            is ReceivingPackagingMaterialsFromVendorOrdersListEvent.OnSearchTextChanged -> {
                _uiState.update { it.copy(searchText = event.text) }
                filterOrders(event.text)
            }
            is ReceivingPackagingMaterialsFromVendorOrdersListEvent.OnShowMaterialsButtonClicked -> {
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
            _uiState.update { it.copy(loadingStatus = LoadingPackagingPurchaseOrdersListStatus.LOADING) }
            try {
                val result = getAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase()
                when (result) {
                    is NetworkResult.Success -> {
                        val orders = result.data
                        _uiState.update {
                            it.copy(
                                poList = orders,
                                poFilteredList = if (it.searchText.isBlank()) orders else orders.filter { po ->
                                    po.carNo?.contains(it.searchText, ignoreCase = true) == true ||
                                            po.factoryCarDescription?.contains(it.searchText, ignoreCase = true) == true ||
                                            (po.vendorName?.contains(it.searchText, ignoreCase = true) == true) ||
                                            (po.vendorCode?.contains(it.searchText, ignoreCase = true) == true) ||
                                            po.purchaseOrderItems.any { item ->
                                                item.materialCode?.contains(it.searchText, ignoreCase = true) == true ||
                                                        item.materialDescription?.contains(it.searchText, ignoreCase = true) == true ||
                                                        item.poNumber?.contains(it.searchText, ignoreCase = true) == true
                                            }
                                },
                                loadingStatus = LoadingPackagingPurchaseOrdersListStatus.SUCCESS(orders)
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
                            it.copy(loadingStatus = LoadingPackagingPurchaseOrdersListStatus.ERROR(message))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingStatus = LoadingPackagingPurchaseOrdersListStatus.ERROR(e.localizedMessage ?: "Error"))
                }
            }
        }
    }

    private fun filterOrders(query: String) {
        _uiState.update { state ->
            val filteredList = if (query.isBlank()) {
                state.poList
            } else {
                state.poList.filter {
                    it.carNo?.contains(query, ignoreCase = true) == true ||
                            it.factoryCarDescription?.contains(query, ignoreCase = true) == true ||
                            (it.vendorName?.contains(query, ignoreCase = true) == true) ||
                            (it.vendorCode?.contains(query, ignoreCase = true) == true) ||
                            it.purchaseOrderItems.any { item ->
                                item.materialCode?.contains(query, ignoreCase = true) == true ||
                                        item.materialDescription?.contains(query, ignoreCase = true) == true ||
                                        item.poNumber?.contains(query, ignoreCase = true) == true
                            }
                }
            }
            state.copy(poFilteredList = filteredList)
        }
    }
}
