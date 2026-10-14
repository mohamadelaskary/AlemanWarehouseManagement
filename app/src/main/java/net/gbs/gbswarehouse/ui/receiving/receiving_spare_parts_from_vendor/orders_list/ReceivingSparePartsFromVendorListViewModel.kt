package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.Material
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.mapper.toPurchaseOrder
import net.gbs.gbswarehouse.domain.usecase.GetReceivingSparePartsFromVendorPurchaseOrdersListUseCase
import javax.inject.Inject
@HiltViewModel
class ReceivingSparePartsFromVendorListViewModel
@Inject constructor(
    private val getReceivingSparePartsFromVendorPurchaseOrdersListUseCase: GetReceivingSparePartsFromVendorPurchaseOrdersListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingSparePartsFromVendorOrdersListState())
    val uiState = _uiState.asStateFlow()

    init {
        // Removed to avoid duplicate calls, handled by LaunchedEffect in UI
    }

    fun onEvent(event: ReceivingSparePartsFromVendorOrdersListEvent) {
        when (event) {
            ReceivingSparePartsFromVendorOrdersListEvent.InitScreen -> {
                loadOrders()
            }
            is ReceivingSparePartsFromVendorOrdersListEvent.OnSearchTextChanged -> {
                filterOrders(query = event.text)
            }
            is ReceivingSparePartsFromVendorOrdersListEvent.OnGroupSelected -> {
                filterOrders(group = event.group)
            }
            is ReceivingSparePartsFromVendorOrdersListEvent.OnShowMaterialsButtonClicked -> {
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
            Log.d("SparePartsListVM", "loadOrders: Starting")
            _uiState.update { it.copy(loadingStatus = LoadingPurchaseOrdersListStatus.LOADING) }
            try {
                val result = getReceivingSparePartsFromVendorPurchaseOrdersListUseCase.invoke()
                Log.d("SparePartsListVM", "loadOrders: Result received: $result")
                when (result) {
                    is NetworkResult.Success -> {
                        val orders = result.data.map { it.toPurchaseOrder() }
                        Log.d("SparePartsListVM", "loadOrders: Success, orders size: ${orders.size}")
                        _uiState.update {
                            it.copy(
                                poList = orders,
                                loadingStatus = LoadingPurchaseOrdersListStatus.SUCCESS(orders)
                            )
                        }
                        filterOrders()
                    }
                    else -> {
                        val message = when (result) {
                            is NetworkResult.BusinessError -> result.message
                            is NetworkResult.HttpError -> result.message
                            is NetworkResult.NetworkError -> result.message
                            is NetworkResult.UnknownError -> result.message
                            else -> "Unknown Error"
                        }
                        Log.e("SparePartsListVM", "loadOrders: Error: $message")
                        _uiState.update {
                            it.copy(loadingStatus = LoadingPurchaseOrdersListStatus.ERROR(message))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SparePartsListVM", "loadOrders: Exception", e)
                _uiState.update {
                    it.copy(loadingStatus = LoadingPurchaseOrdersListStatus.ERROR(e.localizedMessage ?: "Mapping Error"))
                }
            }
        }
    }

    private fun filterOrders(query: String = _uiState.value.searchText, group: OrderGroup = _uiState.value.selectedGroup) {
        _uiState.update { state ->
            val groupFiltered = state.poList.filter { po ->
                val endsWith9 = po.poNumber.startsWith("9")
                when (group) {
                    OrderGroup.PURCHASES -> !endsWith9
                    OrderGroup.TRANSFERS -> endsWith9
                }
            }
            val filteredList = if (query.isBlank()) {
                groupFiltered
            } else {
                groupFiltered.filter {
                    it.poNumber.contains(query, ignoreCase = true) ||
                            it.vendorName.contains(query, ignoreCase = true)
                }
            }
            state.copy(
                searchText = query,
                selectedGroup = group,
                poFilteredList = filteredList
            )
        }
    }

    var currentClickedOrderIndex by mutableStateOf(-1)
    var showMaterials by mutableStateOf(false)

    val orderSearch: String get() = _uiState.value.searchText
    fun searchPoNumber(query: String) {
        onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnSearchTextChanged(query))
    }

    val filteredOrders: List<Order> get() {
        return _uiState.value.poFilteredList.map {
            Order(
                id = it.poNumber,
                info = it.vendorName,
                date = "", // API doesn't provide date
                items = it.purchaseOrderItems.size,
                status = "", // API doesn't provide status
                statusColor = GREEN,
                materials = it.purchaseOrderItems.map { item ->
                    Material(item.materialCode, item.materialDescription, item.uom, item.orderedQuantity.toInt(), item.receivedQuantity.toInt())
                }
            )
        }
    }
}
