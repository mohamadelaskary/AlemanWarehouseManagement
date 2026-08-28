package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.usecase.receiving.GetSparePartsReturnMovementTypeCountsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetSparePartsReturnReservationsByMovementTypeUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingSparePartsFromProductionOrdersListViewModel @Inject constructor(
    private val getSparePartsReturnMovementTypeCountsUseCase: GetSparePartsReturnMovementTypeCountsUseCase,
    private val getSparePartsReturnReservationsByMovementTypeUseCase: GetSparePartsReturnReservationsByMovementTypeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingSparePartsFromProductionOrdersListState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ReceivingSparePartsFromProductionOrdersListEvent) {
        when (event) {
            ReceivingSparePartsFromProductionOrdersListEvent.OnScreenLoaded -> {
                loadMovementTypes()
            }
            is ReceivingSparePartsFromProductionOrdersListEvent.OnMovementTypeSelected -> {
                _uiState.update { it.copy(selectedMovementType = event.movementType, error = "") }
                loadOrders(event.movementType.movementType?:"")
            }
            is ReceivingSparePartsFromProductionOrdersListEvent.OnSearchTextChanged -> {
                _uiState.update { state ->
                    val filtered = state.orders.filter {
                        it.reservationNumber?.contains(event.text, ignoreCase = true) == true ||
                                it.orderNumber?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(searchText = event.text, filteredOrders = filtered)
                }
            }
            ReceivingSparePartsFromProductionOrdersListEvent.OnRefreshMovementTypes -> {
                loadMovementTypes()
            }

            is ReceivingSparePartsFromProductionOrdersListEvent.OnMovementTypeSearchTextChanged -> _uiState.update {
                it.copy(movementTypeSearchText = event.text)
            }
        }
    }

    private fun loadMovementTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMovementTypes = true, error = "") }
            when (val result = getSparePartsReturnMovementTypeCountsUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(movementTypes = result.data, isLoadingMovementTypes = false) }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> "BusinessError: ${result.message}"
                        is NetworkResult.HttpError -> "HttpError: ${result.message} (${result.code})"
                        is NetworkResult.NetworkError -> "NetworkError: ${result.message}"
                        is NetworkResult.UnknownError -> "UnknownError: ${result.message}"
                        else -> "Unexpected result type: ${result.javaClass.simpleName}"
                    }
                    _uiState.update { it.copy(error = message, isLoadingMovementTypes = false) }
                }
            }
        }
    }

    private fun loadOrders(movementType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOrders = true, error = "") }
            when (val result = getSparePartsReturnReservationsByMovementTypeUseCase(movementType)) {
                is NetworkResult.Success -> {
                    val ordersList = result.data
                    _uiState.update {
                        it.copy(
                            orders = ordersList,
                            filteredOrders = ordersList,
                            isLoadingOrders = false
                        )
                    }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> "BusinessError: ${result.message}"
                        is NetworkResult.HttpError -> "HttpError: ${result.message} (${result.code})"
                        is NetworkResult.NetworkError -> "NetworkError: ${result.message}"
                        is NetworkResult.UnknownError -> "UnknownError: ${result.message}"
                        else -> "Unexpected result type: ${result.javaClass.simpleName}"
                    }
                    _uiState.update { it.copy(error = message, isLoadingOrders = false) }
                }
            }
        }
    }
}
