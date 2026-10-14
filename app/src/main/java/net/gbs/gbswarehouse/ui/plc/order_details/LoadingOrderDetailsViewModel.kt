package net.gbs.gbswarehouse.ui.plc.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.usecase.loading.LoadingUseCases
import javax.inject.Inject

@HiltViewModel
class LoadingOrderDetailsViewModel @Inject constructor(
    private val useCases: LoadingUseCases
) : ViewModel() {

    private val _order = MutableStateFlow<LoadingOrder?>(null)
    val order: StateFlow<LoadingOrder?> = _order.asStateFlow()

    private val _conveyors = MutableStateFlow<List<Conveyor>>(emptyList())
    val conveyors: StateFlow<List<Conveyor>> = _conveyors.asStateFlow()

    private val _selectedItem = MutableStateFlow<OrderItem?>(null)
    val selectedItem: StateFlow<OrderItem?> = _selectedItem.asStateFlow()

    private val _startQty = MutableStateFlow("")
    val startQty: StateFlow<String> = _startQty.asStateFlow()

    fun updateStartQty(qty: String) {
        _startQty.value = qty
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isStarting = MutableStateFlow(false)
    val isStarting: StateFlow<Boolean> = _isStarting.asStateFlow()

    private val _errorMessage = MutableStateFlow<Message?>(null)
    val errorMessage: StateFlow<Message?> = _errorMessage.asStateFlow()

    val availableConveyors: List<Conveyor>
        get() = _conveyors.value.filter { it.isAvailable }

    init {
        viewModelScope.launch {
            useCases.observeConveyors().collect { conveyors ->
                _conveyors.value = conveyors
            }
        }
        viewModelScope.launch {
            // Keep the shown quantities in sync with the simulated PLC progress.
            // Uses the cached order list, so this never hits the network per bag.
            useCases.observeLoadingOperations().drop(1).collect {
                val orderId = _order.value?.id ?: return@collect
                val refreshed = useCases.getCachedLoadingOrders()
                    ?.firstOrNull { it.id == orderId }
                if (refreshed != null) _order.value = refreshed
            }
        }
    }

    fun loadOrder(orderId: Int) {
        if (_order.value?.id == orderId) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = useCases.getLoadingOrderDetails(orderId)) {
                is NetworkResult.Success -> {
                    _order.value = result.data
                    _isLoading.value = false
                }
                is NetworkResult.Loading -> _isLoading.value = true
                else -> {
                    _isLoading.value = false
                    _errorMessage.value = result.errorMessageOrDefault()
                }
            }
        }
    }

    fun selectItem(item: OrderItem) {
        if (item.remainingQuantity <= 0) {
            _errorMessage.value = Message.Resource(R.string.item_fully_loaded)
            return
        }
        _selectedItem.value = item
        _startQty.value = item.remainingQuantity.toString()
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
        _startQty.value = ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun startLoading(conveyor: Conveyor, onStarted: (LoadingOperation) -> Unit) {
        val currentOrder = _order.value ?: return
        val item = _selectedItem.value ?: return
        val qtyInt = _startQty.value.toIntOrNull()
        if (qtyInt == null || qtyInt <= 0) {
            _errorMessage.value = Message.Resource(R.string.please_enter_a_valid_quantity)
            return
        }
        if (qtyInt > item.remainingQuantity) {
            _errorMessage.value = Message.Resource(R.string.quantity_cannot_exceed_remaining_quantity, item.remainingQuantity.toString())
            return
        }
        viewModelScope.launch {
            _isStarting.value = true
            _errorMessage.value = null
            when (val result = useCases.startLoading(currentOrder, item, conveyor, qtyInt)) {
                is NetworkResult.Success -> {
                    _isStarting.value = false
                    _selectedItem.value = null
                    _startQty.value = ""
                    onStarted(result.data)
                }
                is NetworkResult.Loading -> _isStarting.value = true
                else -> {
                    _isStarting.value = false
                    _errorMessage.value = result.errorMessageOrDefault()
                }
            }
        }
    }

    private fun NetworkResult<*>.errorMessageOrDefault(): Message = when (this) {
        is NetworkResult.BusinessError -> Message.real(message)
        is NetworkResult.HttpError -> Message.real(message)
        is NetworkResult.NetworkError -> Message.real(message)
        is NetworkResult.UnknownError -> Message.real(message)
        else -> Message.Resource(R.string.unknown_error)
    }
}