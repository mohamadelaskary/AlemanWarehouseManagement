package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPackagingMaterialsPurchaseOrdersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPoultryMaterialsPurchaseOrdersUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingPoultryFromVendorListViewModel @Inject constructor(
    private val getAllPoultryMaterialsPurchaseOrdersUseCase: GetAllPoultryMaterialsPurchaseOrdersUseCase
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
                val result = getAllPoultryMaterialsPurchaseOrdersUseCase()
                when (result) {
                    is NetworkResult.Success -> {
                        val orders = result.data
                        _uiState.update {
                            it.copy(
                                poList = orders,
                                poFilteredList = orders,
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
                state.poList.filter {
                    it.poNumber.contains(query, ignoreCase = true) ||
                            (it.vendorName?.contains(query, ignoreCase = true) == true)
                }
            }
            state.copy(poFilteredList = filteredList)
        }
    }
}
