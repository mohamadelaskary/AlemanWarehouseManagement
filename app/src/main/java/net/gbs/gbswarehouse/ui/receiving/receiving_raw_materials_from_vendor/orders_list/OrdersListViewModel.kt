package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllRawMaterialsPurchaseOrdersByCarNumberUseCase
import javax.inject.Inject

@HiltViewModel
class OrdersListViewModel @Inject constructor(
    private val getAllRawMaterialsPurchaseOrdersByCarNumberUseCase: GetAllRawMaterialsPurchaseOrdersByCarNumberUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ReceivingRawMaterialsFromVendorOrdersListState())
    val state: StateFlow<ReceivingRawMaterialsFromVendorOrdersListState> = _state.asStateFlow()

    init {
        getOrders()
    }

    private fun getOrders() {
        viewModelScope.launch {
            _state.update { it.copy(loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.LOADING) }
            when (val result = getAllRawMaterialsPurchaseOrdersByCarNumberUseCase()) {
                is NetworkResult.Success -> {
                    val orders = result.data
                    _state.update {
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
                            loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.SUCCESS(orders)
                        )
                    }
                }
                is NetworkResult.HttpError -> {
                    _state.update { it.copy(loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.ERROR(result.message)) }
                }
                is NetworkResult.NetworkError -> {
                    _state.update { it.copy(loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.ERROR(result.message)) }
                }
                is NetworkResult.BusinessError -> {
                    _state.update { it.copy(loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.ERROR(result.message)) }
                }
                is NetworkResult.UnknownError -> {
                    _state.update { it.copy(loadingStatus = LoadingRawMaterialsPurchaseOrdersListStatus.ERROR(result.message)) }
                }
                else -> {}
            }
        }
    }

    fun onEvent(event: ReceivingRawMaterialsFromVendorOrdersListEvent) {
        when (event) {
            is ReceivingRawMaterialsFromVendorOrdersListEvent.OnSearchTextChanged -> {
                _state.update { state ->
                    val filtered = if (event.searchText.isEmpty()) {
                        state.poList
                    } else {
                        state.poList.filter {
                            it.carNo?.contains(event.searchText, ignoreCase = true) == true ||
                                    it.factoryCarDescription?.contains(event.searchText, ignoreCase = true) == true ||
                                    (it.vendorName?.contains(event.searchText, ignoreCase = true) == true) ||
                                    (it.vendorCode?.contains(event.searchText, ignoreCase = true) == true) ||
                                    it.purchaseOrderItems.any { item ->
                                        item.materialCode?.contains(event.searchText, ignoreCase = true) == true ||
                                                item.materialDescription?.contains(event.searchText, ignoreCase = true) == true ||
                                                item.poNumber?.contains(event.searchText, ignoreCase = true) == true
                                    }
                        }
                    }
                    state.copy(searchText = event.searchText, poFilteredList = filtered)
                }
            }
            is ReceivingRawMaterialsFromVendorOrdersListEvent.OnShowMaterialsClicked -> {
                _state.update {
                    if (it.showMaterialsClickedCardIndex == event.index) {
                        it.copy(isMaterialsShowed = !it.isMaterialsShowed)
                    } else {
                        it.copy(showMaterialsClickedCardIndex = event.index, isMaterialsShowed = true)
                    }
                }
            }
            ReceivingRawMaterialsFromVendorOrdersListEvent.Refresh -> {
                getOrders()
            }
        }
    }
}
