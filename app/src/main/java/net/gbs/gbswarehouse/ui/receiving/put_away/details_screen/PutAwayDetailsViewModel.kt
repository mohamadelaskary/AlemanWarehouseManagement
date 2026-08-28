package net.gbs.gbswarehouse.ui.receiving.put_away.details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ConfirmPutAwayRequestDto
import net.gbs.gbswarehouse.domain.model.PutAwayOrderLine
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

@HiltViewModel
class PutAwayDetailsViewModel @Inject constructor(
    private val repository: ReceivingRepository
) : ViewModel() {

    private val _orderLines = MutableStateFlow<List<PutAwayOrderLine>>(emptyList())
    val orderLines: StateFlow<List<PutAwayOrderLine>> = _orderLines.asStateFlow()

    private val _selectedBin = MutableStateFlow<StorageBinLocation?>(null)
    val selectedBin: StateFlow<StorageBinLocation?> = _selectedBin.asStateFlow()

    private val _selectedLine = MutableStateFlow<PutAwayOrderLine?>(null)
    val selectedLine: StateFlow<PutAwayOrderLine?> = _selectedLine.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun getOrderLines(receiptType: String, orderNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getPutAwayOrderLines(receiptType, orderNumber)) {
                is NetworkResult.Success -> _orderLines.value = result.data
                is NetworkResult.BusinessError -> _errorMessage.value = result.message
                is NetworkResult.HttpError -> _errorMessage.value = result.message
                is NetworkResult.NetworkError -> _errorMessage.value = result.message
                is NetworkResult.UnknownError -> _errorMessage.value = result.message
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }

    fun validateBin(binCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getStorageBinLocation(binCode)) {
                is NetworkResult.Success -> _selectedBin.value = result.data
                is NetworkResult.BusinessError -> _errorMessage.value = result.message
                is NetworkResult.HttpError -> _errorMessage.value = result.message
                is NetworkResult.NetworkError -> _errorMessage.value = result.message
                is NetworkResult.UnknownError -> _errorMessage.value = result.message
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }

    fun selectLine(line: PutAwayOrderLine?) {
        _selectedLine.value = line
    }

    fun clearBin() {
        _selectedBin.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
    fun clearSuccess() {
        _successMessage.value = null
    }

    fun confirmPutAway(quantity: Double) {
        val line = _selectedLine.value ?: return
        val bin = _selectedBin.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            val request = ConfirmPutAwayRequestDto(
                goodsReceiptLineId = line.goodsReceiptLineId,
                storageBinCode = bin.storageBinCode,
                quantity = quantity
            )
            when (val result = repository.confirmPutAway(request)) {
                is NetworkResult.Success -> {
                    _successMessage.value = result.data
                    _selectedLine.value = null
                    _selectedBin.value = null
                    // Refresh data after success to get updated quantities
                    // refreshOrderData(line.receiptType, line.purchaseOrder)
                }
                is NetworkResult.BusinessError -> _errorMessage.value = result.message
                is NetworkResult.HttpError -> _errorMessage.value = result.message
                is NetworkResult.NetworkError -> _errorMessage.value = result.message
                is NetworkResult.UnknownError -> _errorMessage.value = result.message
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }
}
