package net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.DirectIssueOnCostCenterLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectIssueOnCostCenterDto
import net.gbs.gbswarehouse.domain.model.DirectIssueReservationLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectIssueOnCostCenterUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectIssueOnCostCenterReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialOnHandStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen.IssueDetailsEvent.*
import javax.inject.Inject
import net.gbs.gbswarehouse.StockSelectionRestore
import net.gbs.gbswarehouse.resolveStockSelection

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val getDirectIssueOnCostCenterReservationLinesUseCase: GetDirectIssueOnCostCenterReservationLinesUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    private val directIssueOnCostCenterUseCase: DirectIssueOnCostCenterUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(IssueDetailsState())
    val state: StateFlow<IssueDetailsState> = _state.asStateFlow()

    /**
     * Batch / valuation type + quantity of the line being edited, held until the stock
     * list for its bin has been re-fetched by loadStock.
     */
    private var pendingStockRestore: StockSelectionRestore? = null

    fun onBarcodeScanned(barcode: String) {
        onEvent(OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssueDetailsEvent) {
        when (event) {
            is LoadOrderDetails -> loadOrderDetails(event.reservationNumber)
            is OnBarcodeScanned -> {
                if (_state.value.selReservationLine == null) {
                    val material = _state.value.reservationLines.find { it.materialCode == event.barcode }
                    if (material != null) {
                        onEvent(OnMaterialSelected(material))
                    } else {
                        _state.update {
                            it.copy(
                                matErrorMessage = Message.Resource(R.string.material_not_found_in_list),
                                matDlg = false
                            )
                        }
                    }
                } else if (_state.value.binInput.isEmpty() || _state.value.selBinData == null) {
                    onEvent(OnScanBin(event.barcode))
                } else {
                    val material = _state.value.reservationLines.find { it.materialCode == event.barcode }
                    if (material != null) {
                        onEvent(OnMaterialSelected(material))
                    }
                }
            }
            is OnBinInputChanged -> _state.update { it.copy(binInput = event.text, binCodeError = null) }
            is OnScanBin -> scanBin(event.binCode)
            OnClearBin -> clearBin()
            
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
            is OnOnHandStockSelected -> onOnHandStockSelected(event.stock)
            
            is OnQtyChanged -> {
                val maxQty = _state.value.maxQty
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > maxQty) {
                    Message.Resource(R.string.qty_exceed_max_qty) + formatQty(maxQty)
                } else null
                _state.update { it.copy(qty = event.text, qtyError = error) }
            }
            OnAddLine -> addLine()
            OnIssueAll -> issueAll()
            OnDismissError -> _state.update { it.copy(qtyError = null) }
            is OnDeleteLine -> {
                removeItem(event.materialCode)
            }
            is OnEditLine -> editLine(event.addedLine)

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

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toLong().toString() else qty.toString()
    }

    private fun removeItem(materialCode: String) {
        val lineWithMaterialCode = _state.value.addedLines.find { it.materialCode == materialCode }
        if (lineWithMaterialCode != null) {
            _state.update { it.copy(addedLines = it.addedLines - lineWithMaterialCode) }
        }
    }

    private fun editLine(addedLine: DirectIssueOnCostCenterLineDto) {
        removeItem(addedLine.materialCode)
        val line = _state.value.reservationLines.find { it.reservationItem == addedLine.reservationItem } ?: return
        selectReservationLine(line)
        _state.update { it.copy(binInput = addedLine.storageBinCode) }
        if (addedLine.storageBinCode.isEmpty()) {
            pendingStockRestore = null
            _state.update { it.copy(qty = formatQty(addedLine.quantity)) }
            return
        }
        // Re-scan the bin to rebuild the stock list, then re-apply the saved batch /
        // valuation type and quantity once that list arrives.
        pendingStockRestore = StockSelectionRestore(
            batch = addedLine.batch,
            valuationType = addedLine.valuationType,
            qty = formatQty(addedLine.quantity)
        )
        scanBin(addedLine.storageBinCode)
    }

    private fun loadOrderDetails(reservationNumber: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loadingErrorMessage = null) }
            when (val result = getDirectIssueOnCostCenterReservationLinesUseCase(reservationNumber)) {
                is NetworkResult.Success -> {
                    val lines = result.data ?: emptyList()
                    _state.update { it.copy(reservationLines = lines, filteredMaterials = lines) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(loadingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.HttpError -> _state.update { it.copy(loadingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(loadingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(loadingErrorMessage = Message.Real(result.message)) }
                else -> {}
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun scanBin(binCode: String) {
        val line = _state.value.selReservationLine
        if (line == null) {
            _state.update { it.copy(binCodeError = Message.Resource(R.string.please_scan_bin_code_first)) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isBinLoading = true, binCodeError = null) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    val binData = result.data
                    _state.update {
                        it.copy(
                            selBinData = binData,
                            binInput = binData?.storageBinCode ?: binCode,
                            binCodeError = Message.Real("")
                        )
                    }
                    loadStock(line, binData?.storageBinCode ?: binCode)
                }
                is NetworkResult.BusinessError -> _state.update {
                    it.copy(binCodeError = Message.Real(result.message), binInput = "", selBinData = null)
                }
                is NetworkResult.HttpError -> _state.update {
                    it.copy(binCodeError = Message.Real(result.message), binInput = "", selBinData = null)
                }
                is NetworkResult.NetworkError -> _state.update {
                    it.copy(binCodeError = Message.Real(result.message), binInput = "", selBinData = null)
                }
                is NetworkResult.UnknownError -> _state.update {
                    it.copy(binCodeError = Message.Real(result.message), binInput = "", selBinData = null)
                }
                else -> {}
            }
            _state.update { it.copy(isBinLoading = false) }
        }
    }

    private fun clearBin() {
        _state.update {
            it.copy(
                selBinData = null,
                binInput = "",
                binCodeError = null,
                qty = "",
                selectedOnHandStock = null,
                stockOnHandList = emptyList(),
                availableStock = 0.0,
                maxQty = 0.0
            )
        }
    }

    private fun selectReservationLine(line: DirectIssueReservationLine) {
        val alreadyAddedLine = _state.value.addedLines.find {
                    it.reservationItem == line.reservationItem
        }
        if (alreadyAddedLine != null){
            _state.update {
                it.copy(
                    matErrorMessage = Message.Resource(R.string.material_already_added),
                    matDlg = false
                )
            }
            return
        }

        _state.update {
            it.copy(
                selReservationLine = line,
                matInput = line.materialCode ?: "",
                matErrorMessage = null,
                matDlg = false,
                selBinData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                qty = ""
            )
        }
    }

    private fun loadStock(line: DirectIssueReservationLine, storageBinCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true, stockError = null) }
            when (val result = getMaterialOnHandStockUseCase(storageBinCode, line.materialCode ?: "")) {
                is NetworkResult.Success -> {
                    val stockList = result.data ?: emptyList()
                    if (stockList.isEmpty()) {
                        _state.update {
                            it.copy(
                                stockOnHandList = emptyList(),
                                selectedOnHandStock = null,
                                availableStock = 0.0,
                                maxQty = 0.0,
                                qty = "0",
                                stockError = Message.Resource(R.string.no_stock_available)
                            )
                        }
                    } else {
                        val isBatchManaged = stockList.any { it.isBatchManaged == true }
                        val hasValuationType = !isBatchManaged && stockList.any { !it.valuationType.isNullOrEmpty() }

                        val resolved = resolveStockSelection(
                            stockList = stockList,
                            remainingQty = line.remainingQuantity ?: 0.0,
                            pending = pendingStockRestore
                        )
                        pendingStockRestore = null
                        _state.update {
                            it.copy(
                                stockOnHandList = stockList,
                                selectedOnHandStock = resolved.selected,
                                availableStock = resolved.availableQty,
                                maxQty = resolved.maxQty,
                                qty = resolved.qty,
                                stockSearch = resolved.searchText,
                                qtyError = null,
                                stockError = null
                            )
                        }
                    }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(stockError = Message.Real(result.message)) }
                is NetworkResult.HttpError -> _state.update { it.copy(stockError = Message.Real(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(stockError = Message.Real(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(stockError = Message.Real(result.message)) }
                else -> {}
            }
            _state.update { it.copy(isStockLoading = false) }
        }
    }

    private fun onOnHandStockSelected(stock: MaterialOnHandStock) {
        val availQty = stock.availableQty ?: 0.0
        val remainingQty = _state.value.selReservationLine?.remainingQuantity ?: 0.0
        val maxQty = minOf(remainingQty, availQty)
        _state.update {
            it.copy(
                selectedOnHandStock = stock,
                availableStock = availQty,
                maxQty = maxQty,
                qty = if (maxQty > 0) formatQty(maxQty) else "0",
                stockError = null,
                qtyError = null
            )
        }
    }

    private fun clearMaterial() {
        _state.update { it.copy(
            selReservationLine = null,
            selectedOnHandStock = null,
            matInput = "",
            qty = "",
            stockOnHandList = emptyList(),
            stockSearch = "",
            matSearchText = "",
            matErrorMessage = null,
            binInput = "",
            selBinData = null,
            binCodeError = null,
            availableStock = 0.0,
            maxQty = 0.0,
            qtyError = null,
            stockError = null
        ) }
    }

    private fun addLine() {
        val st = _state.value
        Log.e("IssueDetailsViewModel", "addLine: line  ${st.selReservationLine} ", )
        Log.e("IssueDetailsViewModel", "addLine: bin ${st.selBinData} ", )
        val line = st.selReservationLine ?: return
        val bin = st.selBinData ?: return

        
        var hasError = false
        val isBatchManaged = st.stockOnHandList.any { it.isBatchManaged == true }
        val hasValuationType = !isBatchManaged && st.stockOnHandList.any { !it.valuationType.isNullOrEmpty() }

        if ((isBatchManaged || hasValuationType) && st.selectedOnHandStock == null) {
            val err: Message = if (isBatchManaged) {
                Message.Resource(R.string.select_a_batch)
            } else {
                Message.Resource(R.string.select_valuation_type)
            }
            _state.update { it.copy(stockError = err) }
            hasError = true
            Log.e("IssueDetailsViewModel", "addLine:  $err ", )
        }
        
        val qtyValue = st.qty.toDoubleOrNull() ?: 0.0
        if (qtyValue <= 0) {
            _state.update { it.copy(qtyError = Message.Resource(R.string.please_enter_valid_quantity)) }
            hasError = true
            Log.d("IssueDetailsViewModel", "addLine: qty <=0")
        }

        if (qtyValue > st.maxQty) {
            _state.update { it.copy(qtyError = Message.Resource(R.string.qty_exceed_max_qty) ) }
            hasError = true
            Log.d("IssueDetailsViewModel", "addLine: qtyValue > st.maxQty")
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
            valuationType = st.selectedOnHandStock?.valuationType,
            batch = st.selectedOnHandStock?.batch,
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
                    _state.update {
                        it.copy(
                            saved = true,
                            savingSuccessMessage = Message.Real(result.message?:""),
                            addedLines = emptyList(),
                            reservationLines = result.data.reservationLines,
                            filteredMaterials = result.data.reservationLines
                        )
                    }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(savingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.HttpError -> _state.update { it.copy(savingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(savingErrorMessage = Message.Real(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(savingErrorMessage = Message.Real(result.message)) }
                else -> {}
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
