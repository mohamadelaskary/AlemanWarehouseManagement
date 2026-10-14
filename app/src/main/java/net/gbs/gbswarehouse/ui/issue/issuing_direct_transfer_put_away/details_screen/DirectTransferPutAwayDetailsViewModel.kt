package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer_put_away.details_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ConfirmDirectTransferPutAwayRequestDto
import net.gbs.gbswarehouse.domain.model.DirectTransferPutAwayLine
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.usecase.issuing.ConfirmDirectTransferPutAwayUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectTransferPutAwayLinesUseCase
import javax.inject.Inject

@HiltViewModel
class DirectTransferPutAwayDetailsViewModel @Inject constructor(
    private val getDirectTransferPutAwayLines: GetDirectTransferPutAwayLinesUseCase,
    private val confirmDirectTransferPutAway: ConfirmDirectTransferPutAwayUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _lines = MutableStateFlow<List<DirectTransferPutAwayLine>>(emptyList())
    val lines: StateFlow<List<DirectTransferPutAwayLine>> = _lines.asStateFlow()

    private val _selectedLine = MutableStateFlow<DirectTransferPutAwayLine?>(null)
    val selectedLine: StateFlow<DirectTransferPutAwayLine?> = _selectedLine.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<Message?>(null)
    val errorMessage: StateFlow<Message?> = _errorMessage.asStateFlow()

    private val _validationMessage = MutableStateFlow<Message?>(null)
    val validationMessage: StateFlow<Message?> = _validationMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<Message?>(null)
    val successMessage: StateFlow<Message?> = _successMessage.asStateFlow()

    fun getLines(receiptNo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = getDirectTransferPutAwayLines(receiptNo)) {
                is NetworkResult.Success -> _lines.value = result.data
                is NetworkResult.BusinessError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.HttpError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.NetworkError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.UnknownError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }

    fun selectLine(line: DirectTransferPutAwayLine?) {
        _selectedLine.value = line
        _validationMessage.value = null
    }

    fun onBarcodeScanned(barcode: String) {
        val code = barcode.trim()
        if (code.isEmpty()) return
        val line = _lines.value.find {
            it.materialCode.equals(code, ignoreCase = true) ||
                it.batch?.equals(code, ignoreCase = true) == true
        }
        if (line != null) {
            selectLine(line)
        } else {
            _errorMessage.value = Message.Resource(R.string.material_not_found)
        }
    }

    fun confirmPutAway(storageBinCode: String, quantity: Double) {
        val line = _selectedLine.value
        val binCode = storageBinCode.trim()

        if (line == null) {
            _validationMessage.value = Message.Resource(R.string.please_select_line_first)
            return
        }
        if (binCode.isEmpty()) {
            _validationMessage.value = Message.Resource(R.string.please_enter_storage_bin)
            return
        }
        if (quantity <= 0.0) {
            _validationMessage.value = Message.Resource(R.string.please_enter_a_valid_quantity)
            return
        }
        if (quantity > line.remainingQuantity) {
            _validationMessage.value  = Message.Resource(
                R.string.quantity_cannot_exceed_remaining_quantity,
            )
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val request = ConfirmDirectTransferPutAwayRequestDto(
                goodsReceiptLineId = line.goodsReceiptLineId,
                storageBinCode = binCode,
                quantity = quantity
            )
            when (val result = confirmDirectTransferPutAway(request)) {
                is NetworkResult.Success -> {
                    val updatedLines = result.data ?: emptyList()
                    _lines.value = updatedLines
                    val currentSelectedId = line.goodsReceiptLineId
                    val matchingUpdated = updatedLines.find { it.goodsReceiptLineId == currentSelectedId }
                    _selectedLine.value = if (matchingUpdated != null && matchingUpdated.remainingQuantity > 0.0) matchingUpdated else null
                    _successMessage.value = Message.Real(result.message!!)
                }
                is NetworkResult.BusinessError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.HttpError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.NetworkError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.UnknownError -> _errorMessage.value = Message.Real(result.message)
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearValidation() {
        _validationMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toLong().toString() else qty.toString()
    }
}
