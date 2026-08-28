package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllFinishedGoodsPlasticProductionOrdersUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingFinishedGoodsPlasticListViewModel @Inject constructor(
    private val getAllFinishedGoodsPlasticProductionOrdersUseCase: GetAllFinishedGoodsPlasticProductionOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingFinishedGoodsPlasticOrdersListState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ReceivingFinishedGoodsPlasticOrdersListEvent) {
        when (event) {
            ReceivingFinishedGoodsPlasticOrdersListEvent.InitScreen -> {
                loadOrders()
            }
            is ReceivingFinishedGoodsPlasticOrdersListEvent.OnSearchTextChanged -> {
                _uiState.update { it.copy(searchText = event.text) }
                filterOrders(event.text)
            }
            is ReceivingFinishedGoodsPlasticOrdersListEvent.OnShowItemsButtonClicked -> {
                _uiState.update {
                    if (it.showItemsClickedCardIndex == event.currentIndex) {
                        it.copy(isItemsShowed = !it.isItemsShowed)
                    } else {
                        it.copy(
                            isItemsShowed = true,
                            showItemsClickedCardIndex = event.currentIndex
                        )
                    }
                }
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingStatus = LoadingFinishedGoodsPlasticOrdersListStatus.LOADING) }
            try {
                val result = getAllFinishedGoodsPlasticProductionOrdersUseCase()
                when (result) {
                    is NetworkResult.Success -> {
                        val orders = result.data
                        _uiState.update {
                            it.copy(
                                orderList = orders,
                                orderFilteredList = orders,
                                loadingStatus = LoadingFinishedGoodsPlasticOrdersListStatus.SUCCESS(orders)
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
                            it.copy(loadingStatus = LoadingFinishedGoodsPlasticOrdersListStatus.ERROR(message))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingStatus = LoadingFinishedGoodsPlasticOrdersListStatus.ERROR(e.localizedMessage ?: "Error"))
                }
            }
        }
    }

    private fun filterOrders(query: String) {
        _uiState.update { state ->
            val filteredList = if (query.isBlank()) {
                state.orderList
            } else {
                state.orderList.filter {
                    it.orderNumber.contains(query, ignoreCase = true)||
                            it.productionOrderItems.any { item ->
                                item.warehouseCode?.contains(query, ignoreCase = true) == true
                            }
                }
            }
            state.copy(orderFilteredList = filteredList)
        }
    }
}
