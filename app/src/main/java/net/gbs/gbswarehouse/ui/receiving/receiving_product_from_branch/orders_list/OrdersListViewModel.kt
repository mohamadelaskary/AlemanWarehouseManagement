package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrder
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllTransitOrderMaterialPurchaseOrdersByCarNumberUseCase
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen.ReceivingProductFromBranchState
import javax.inject.Inject

@HiltViewModel
class OrdersListViewModel @Inject constructor(
    private val getAllTransitOrderMaterialPurchaseOrdersByCarNumberUseCase: GetAllTransitOrderMaterialPurchaseOrdersByCarNumberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingProductFromBranchState())
    val uiState = _uiState.asStateFlow()
    val showMaterials: Boolean = false
    init {
        loadOrders()
    }

    fun onSearchTextEntered(text: String) {
        _uiState.update { state ->
            val filtered = state.allPurchaseOrders.filter { order ->
                matchesSearch(order, text)
            }
            state.copy(purchaseOrderSearch = text, filteredPurchaseOrders = filtered)
        }
    }

    fun onShowMaterialsClicked(index: Int) {
        _uiState.update { it.copy(showMaterialsClickedCardIndex = index, isMaterialsShowed = true) }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAllPurchaseOrdersLoading = true) }
            val result = getAllTransitOrderMaterialPurchaseOrdersByCarNumberUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update {
                    it.copy(
                        allPurchaseOrders = result.data,
                        filteredPurchaseOrders = result.data,
                        isAllPurchaseOrdersLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isAllPurchaseOrdersLoading = false) }
            }
        }
    }

    private fun matchesSearch(order: TransitOrderMaterialPurchaseOrder, text: String): Boolean =
        order.carNo?.contains(text, ignoreCase = true) == true ||
                order.factoryCarDescription?.contains(text, ignoreCase = true) == true ||
                (order.vendorName?.contains(text, ignoreCase = true) == true) ||
                (order.vendorCode?.contains(text, ignoreCase = true) == true) ||
                order.purchaseOrderItems.any { item ->
                    item.materialCode?.contains(text, ignoreCase = true) == true ||
                            item.materialDescription?.contains(text, ignoreCase = true) == true ||
                            item.poNumber?.contains(text, ignoreCase = true) == true
                }
}
