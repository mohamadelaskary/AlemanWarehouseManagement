package net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.DirectIssueOnCostCenterLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectIssueOnCostCenterDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectIssueOnCostCenterUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectIssueOnCostCenterReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen.IssueDetailsEvent.*
import javax.inject.Inject

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val getDirectIssueOnCostCenterReservationLinesUseCase: GetDirectIssueOnCostCenterReservationLinesUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialStockUseCase: GetMaterialStockUseCase,
    private val directIssueOnCostCenterUseCase: DirectIssueOnCostCenterUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _state = MutableStateFlow(IssueDetailsState())
    val state: StateFlow<IssueDetailsState> = _state.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        onEvent(OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssueDetailsEvent) {
        when (event) {
            is LoadOrderDetails -> loadOrderDetails(event.reservationNumber)
            is OnBarcodeScanned -> {
                if (_state.value.binInput.isEmpty()) {
                    onEvent(OnScanBin(event.barcode))
                } else {
                    val material = _state.value.reservationLines.find { it.materialCode == event.barcode }
                    if (material != null) {
                        onEvent(OnMaterialSelected(material))
                    } else {
                        _state.update {
                            it.copy(
                                matErrorMessage = "المادة غير موجودة في القائمة",
                                matDlg = false
                            )
                        }
                    }
                }
            }
            is OnBinInputChanged -> _state.update { it.copy(binInput = event.text) }
            is OnScanBin -> scanBin(event.binCode)
            OnClearBin -> _state.update { it.copy(selBinData = null, binInput = "", qty = "") }
            
            is OnMaterialSearchTextChanged -> {
                _state.update { state ->
                    val filtered = if (event.text.isEmpty()) {
                        state.reservationLines
                    } else {
                        state.reservationLines.filter {
                            it.materialCode?.contains(event.text, ignoreCase = true) == true ||
                            it.materialDescription?.contains(event.text, ignoreCase = true) == true
                        }
                    }
                    state.copy(matSearchText = event.text, filteredMaterials = filtered)
                }
            }
            is OnMaterialSelected -> selectReservationLine(event.line)
            OnClearMaterial -> clearMaterial()
            OnToggleMaterialDialog -> _state.update { it.copy(matDlg = !it.matDlg) }
            
            is OnStockSearchTextChanged -> _state.update { it.copy(stockSearch = event.text) }
            is OnStockSelected -> {
                val maxQty = (event.stock.quantity?.toDouble()
                    ?: 0.0).coerceAtMost(_state.value.selReservationLine?.remainingQuantity ?: 0.0)
                _state.update {
                    it.copy(
                        maxQty = maxQty,
                        qty = maxQty.toString(),
                        selectedStock = event.stock,
                        stockSearch = "${event.stock.batch} (${event.stock.quantity})",
                        stockError = ""
                    )
                }
            }
            
            is OnQtyChanged -> {
                val maxQty = _state.value.maxQty
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > maxQty) {
                    context.getString(R.string.qty_exceed_max_qty) + maxQty
                } else null
                _state.update { it.copy(qty = event.text, qtyError = error) }
            }
            OnAddLine -> addLine()
            OnIssueAll -> issueAll()
            OnDismissError -> _state.update { it.copy(qtyError = null) }
            is OnDeleteLine -> {
                removeItem(event.materialCode)
            }

            OnDismissSuccess -> {
                _state.update {
                    it.copy(
                        savingErrorMessage = null,
                        savingSuccessMessage = null,
                    )
                }
            }
        }
    }

    private fun removeItem(materialCode: String) {
        val lineWithMaterialCode = _state.value.addedLines.find { it.materialCode == materialCode }
        if (lineWithMaterialCode != null) {
            _state.update { it.copy(addedLines = it.addedLines - lineWithMaterialCode) }
        }
    }

    private fun loadOrderDetails(reservationNumber: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loadingErrorMessage = null) }
            when (val result = getDirectIssueOnCostCenterReservationLinesUseCase(reservationNumber)) {
                is NetworkResult.Success -> {
                    val lines = result.data ?: emptyList()
                    _state.update { it.copy(reservationLines = lines, filteredMaterials = lines) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(loadingErrorMessage = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(loadingErrorMessage = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(loadingErrorMessage = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(loadingErrorMessage = result.message) }
                else -> {}
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun scanBin(binCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isBinLoading = true) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(selBinData = result.data, binInput = result.data?.storageBinCode ?: binCode, binCodeError = "") }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(binCodeError = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(binCodeError = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(binCodeError = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(binCodeError = result.message) }
                else -> {}
            }
            _state.update { it.copy(isBinLoading = false) }
        }
    }

    private fun selectReservationLine(line: SparePartsReturnReservationLine) {
        if (_state.value.selBinData==null){
            _state.update { it.copy(
                matErrorMessage = context.getString(R.string.please_scan_bin_code_first),
                matDlg = false
            )
            }
            return
        }

        val alreadyAddedLine = _state.value.addedLines.find { it.materialCode == line.materialCode }
        if (alreadyAddedLine != null){
            _state.update {
                it.copy(
                    matErrorMessage = context.getString(R.string.material_already_added),
                    matDlg = false
                )
            }
            return
        }

        if (line.isBatchManaged == true || line.valuationTypes.isNotEmpty()) {
            loadStock(line, _state.value.selBinData?.warehouseCode)
        } else {
            _state.update {
                it.copy(selReservationLine = line,
                    matInput = line.materialCode ?: "",
                    matErrorMessage = null,
                    maxQty = line.remainingQuantity ?: 0.0,
                    qty = line.remainingQuantity.toString(),
                    stockSearch = "",
                    selectedStock = null,
                    matDlg = false,
                )
            }
        }
    }

    private fun loadStock(line: SparePartsReturnReservationLine, storageLocationCode: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true) }
            when (val result = getMaterialStockUseCase(line.plantCode?:"",line.materialCode?:"", storageLocationCode)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(selReservationLine = line,
                            matInput = line.materialCode ?: "",
                            matErrorMessage = null,
                            maxQty = line.remainingQuantity ?: 0.0,
                            qty = _state.value.maxQty.toString(),
                            stockSearch = "",
                            selectedStock = null,
                            matDlg = false,
                            stockList = result.data ?: emptyList(),
                            stockError = ""
                        )
                    }
                }
                else -> {}
            }
            _state.update { it.copy(isStockLoading = false) }
        }
    }

    private fun clearMaterial() {
        _state.update { it.copy(
            selReservationLine = null,
            selectedStock = null,
            matInput = "",
            qty = "",
            stockList = emptyList(),
            stockSearch = "",
            matSearchText = "",
            matErrorMessage = null,
            binInput = "",
            selBinData = null,
            binCodeError = null
        ) }
    }

    private fun addLine() {
        val st = _state.value
        val line = st.selReservationLine ?: return
        val bin = st.selBinData ?: return
        
        var hasError = false
        if ((line.isBatchManaged == true || line.valuationTypes.isNotEmpty()) && st.selectedStock == null) {
            _state.update { it.copy(stockError = context.getString(R.string.select_a_batch)) }
            hasError = true
        }
        
        val qtyValue = st.qty.toDoubleOrNull() ?: 0.0
        if (qtyValue <= 0) {
            _state.update { it.copy(qtyError = context.getString(R.string.please_enter_valid_quantity)) }
            hasError = true
        }

        if (st.selectedStock != null && qtyValue > (st.selectedStock.quantity?.toDouble() ?: 0.0)) {
            _state.update { it.copy(qtyError = context.getString(R.string.qty_exceed_max_qty) + st.selectedStock.quantity) }
            hasError = true
        }

        if (hasError) return

        val newLine = DirectIssueOnCostCenterLineDto(
            materialCode = line.materialCode ?: "",
            plantCode = line.plantCode ?: "",
            warehouseCode = bin.warehouseCode ?: "",
            uom = line.uom ?: "",
            quantity = qtyValue,
            reservationNumber = line.reservationNumber ?: "",
            reservationItem = line.reservationItem ?: "",
            storageBinCode = bin.storageBinCode ?: "",
            valuationType = if (line.isBatchManaged == false && line.valuationTypes.isNotEmpty()) st.selectedStock?.batch else null,
            batch = if (line.isBatchManaged == true) st.selectedStock?.batch else null,
        )
        
        _state.update { it.copy(
            addedLines = it.addedLines + newLine,
        ) }
        clearMaterial()
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = PostDirectIssueOnCostCenterDto(lines = st.addedLines)
            when (val result = directIssueOnCostCenterUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(saved = true, savingSuccessMessage = result.message, addedLines = emptyList()) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(savingErrorMessage = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(savingErrorMessage = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(savingErrorMessage = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(savingErrorMessage = result.message) }
                else -> {}
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
