package net.gbs.gbswarehouse.ui.plc.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.usecase.loading.LoadingUseCases
import javax.inject.Inject

@HiltViewModel
class LoadingOperationViewModel @Inject constructor(
    private val useCases: LoadingUseCases,
) : ViewModel() {

    private val _operation = MutableStateFlow<LoadingOperation?>(null)
    val operation: StateFlow<LoadingOperation?> = _operation.asStateFlow()

    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _errorMessage = MutableStateFlow<Message?>(null)
    val errorMessage: StateFlow<Message?> = _errorMessage.asStateFlow()

    private var resolvedKey: String? = null
    private var attemptedKey: String? = null
    private var lastAttemptAt = 0L

    fun bind(operationId: String) {
        viewModelScope.launch {
            useCases.observeOperation(operationId).collect { op ->
                _operation.value = op
                if (op != null) {
                    resolveOrderItems(op)
                }
            }
        }
    }

    /**
     * Streamed operations only carry the sales order number (order id is not
     * known yet), so items are matched by order number against the cache and
     * fetched from the API when needed. Resolves at most once per order key.
     */
    private suspend fun resolveOrderItems(op: LoadingOperation) {
        val key = op.orderNumber.ifBlank { op.orderId.toString() }
        if (resolvedKey == key) return
        val now = System.currentTimeMillis()
        if (attemptedKey == key && now - lastAttemptAt < RETRY_INTERVAL_MS) return
        attemptedKey = key
        lastAttemptAt = now

        val cached = useCases.getCachedLoadingOrders()
        val match = cached?.firstOrNull { order ->
            (op.orderId > 0 && order.id == op.orderId) ||
                (op.orderNumber.isNotBlank() && order.orderNumber == op.orderNumber)
        }
        if (match != null) {
            _orderItems.value = match.items
            resolvedKey = key
            return
        }

        if (op.orderId > 0) {
            when (val res = useCases.getLoadingOrderDetails(op.orderId)) {
                is NetworkResult.Success -> {
                    _orderItems.value = res.data.items
                    resolvedKey = key
                }
                else -> Unit
            }
            return
        }

        when (val res = useCases.getLoadingOrders()) {
            is NetworkResult.Success -> {
                val fetched = res.data.firstOrNull { it.orderNumber == op.orderNumber }
                if (fetched != null) {
                    _orderItems.value = fetched.items
                    resolvedKey = key
                }
            }
            else -> Unit
        }
    }

    fun pause() = runCommand { useCases.pauseLoading(it) }

    fun resume() = runCommand { useCases.resumeLoading(it) }

    fun addOnePack() = runCommand { useCases.addOnePack(it) }

    fun manualStart() = runCommand { useCases.manualStartConveyor(_operation.value?.conveyorId ?: -1) }

    fun manualStop() = runCommand { useCases.manualStopConveyor(_operation.value?.conveyorId ?: -1) }

    fun reportFailure(reason: String) =
        runCommand { useCases.reportLoadingFailure(it, reason) }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun runCommand(block: suspend (String) -> NetworkResult<*>) {
        val id = _operation.value?.operationId
        if (id == null) {
            _errorMessage.value = Message.Resource(R.string.plc_no_operation_available)
            return
        }
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = block(id)) {
                is NetworkResult.Success -> {
                    _isBusy.value = false
                    _errorMessage.value = null
                }
                is NetworkResult.Loading -> _isBusy.value = true
                else -> {
                    _isBusy.value = false
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

    companion object {
        private const val RETRY_INTERVAL_MS = 10_000L
    }
}
