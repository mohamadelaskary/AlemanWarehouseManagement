package net.gbs.gbswarehouse.ui.issue.issuing_scrap

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
import net.gbs.gbswarehouse.data.remote.dto.response.PostScrapIssueDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.ScrapIssueLine
import net.gbs.gbswarehouse.domain.usecase.issuing.GetCompaniesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetCostCentersUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialOnHandStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetScrapEligibleStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.ScrapIssueUseCase
import javax.inject.Inject
import net.gbs.gbswarehouse.StockSelectionRestore
import net.gbs.gbswarehouse.resolveStockSelection
import net.gbs.gbswarehouse.spinnerLabel

@HiltViewModel
class IssueScrapViewModel @Inject constructor(
    private val getScrapEligibleStockUseCase: GetScrapEligibleStockUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getCostCentersUseCase: GetCostCentersUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    private val scrapIssueUseCase: ScrapIssueUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(IssueScrapState())
    val state: StateFlow<IssueScrapState> = _state.asStateFlow()

    /**
     * Quantity of the line being edited, held until the stock list for its bin has been
     * re-fetched by loadStock so the reload does not overwrite it with the maximum.
     */
    private var pendingStockRestore: StockSelectionRestore? = null

    init {
        loadEligibleStock()
        loadCompanies()
    }

    private fun loadEligibleStock() {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true) }
            when (val result = getScrapEligibleStockUseCase()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(eligibleStock = result.data) }
                }
                else -> {}
            }
            _state.update { it.copy(isStockLoading = false) }
        }
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            _state.update { it.copy(isCompaniesLoading = true) }
            when (val result = getCompaniesUseCase()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(companies = result.data) }
                }
                else -> {}
            }
            _state.update { it.copy(isCompaniesLoading = false) }
        }
    }

    private fun loadCostCenters(companyCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCostCentersLoading = true) }
            when (val result = getCostCentersUseCase(companyCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(costCenters = result.data) }
                }
                else -> {}
            }
            _state.update { it.copy(isCostCentersLoading = false) }
        }
    }

    fun onBarcodeScanned(barcode: String) {
        onEvent(IssueScrapEvent.OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssueScrapEvent) {
        when (event) {
            is IssueScrapEvent.OnBarcodeScanned -> {
                val st = _state.value
                if (st.selectedMatStock == null) {
                    val mat = st.eligibleStock.find { it.materialCode == event.barcode }
                    if (mat != null) {
                        onEvent(IssueScrapEvent.OnMaterialSelected(mat))
                    } else {
                        _state.update { it.copy(matErrorMessage = Message.Resource(R.string.material_not_in_scrap_list)) }
                    }
                } else if (st.selBinData == null || st.binInput.isEmpty()) {
                    onEvent(IssueScrapEvent.OnScanBin(event.barcode))
                }
            }
            is IssueScrapEvent.OnDocumentHeaderChanged -> _state.update { it.copy(documentHeaderText = event.text) }

            is IssueScrapEvent.OnMatInputChanged -> _state.update { it.copy(matInput = event.text) }
            is IssueScrapEvent.OnScanMaterial -> {
                val mat = _state.value.eligibleStock.find { it.materialCode == event.matCode }
                if (mat != null) {
                    onEvent(IssueScrapEvent.OnMaterialSelected(mat))
                } else {
                    _state.update { it.copy(matErrorMessage = Message.Resource(R.string.material_not_in_scrap_list)) }
                }
            }
            is IssueScrapEvent.OnMaterialSelected -> selectMaterial(event.stock)
            IssueScrapEvent.OnClearMaterial -> clearMaterial()
            IssueScrapEvent.OnToggleMaterialDialog -> _state.update { it.copy(matDlg = !it.matDlg) }
            is IssueScrapEvent.OnMaterialSearchTextChanged -> _state.update { it.copy(matSearchText = event.text) }

            is IssueScrapEvent.OnBinInputChanged -> _state.update { it.copy(binInput = event.text, binCodeError = null) }
            is IssueScrapEvent.OnScanBin -> scanBin(event.binCode)
            IssueScrapEvent.OnClearBin -> clearBin()

            is IssueScrapEvent.OnStockSearchTextChanged -> _state.update { it.copy(stockSearch = event.text) }
            is IssueScrapEvent.OnOnHandStockSelected -> onOnHandStockSelected(event.stock)

            is IssueScrapEvent.OnCompanySelected -> {
                _state.update { it.copy(selectedCompany = event.company, selectedCostCenter = null, costCenters = emptyList()) }
                loadCostCenters(event.company.companyCode)
            }
            is IssueScrapEvent.OnCompanySearchTextChanged -> _state.update { it.copy(companySearchText = event.text) }

            is IssueScrapEvent.OnCostCenterSelected -> _state.update { it.copy(selectedCostCenter = event.costCenter) }
            is IssueScrapEvent.OnCostCenterSearchTextChanged -> _state.update { it.copy(costCenterSearchText = event.text) }

            is IssueScrapEvent.OnNotesChanged -> _state.update { it.copy(notes = event.text) }

            is IssueScrapEvent.OnQuantityChanged -> {
                val maxQty = _state.value.maxQty
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > maxQty) {
                    Message.Resource(R.string.qty_exceed_max_qty) + formatQty(maxQty)
                } else null
                _state.update { it.copy(quantity = event.text, quantityError = error) }
            }

            IssueScrapEvent.OnAddLine -> addLine()
            IssueScrapEvent.OnIssueAll -> issueAll()
            is IssueScrapEvent.OnDeleteLine -> {
                _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(event.index) }) }
            }
            is IssueScrapEvent.OnEditLine -> editLine(event.index)
            IssueScrapEvent.OnDismissDialogs -> _state.update { it.copy(savingSuccessMessage = null, savingErrorMessage = null) }
        }
    }

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toLong().toString() else qty.toString()
    }

    private fun editLine(index: Int) {
        val line = _state.value.addedLines.getOrNull(index) ?: return
        _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(index) }) }

        // Re-select the material so the bin / stock section becomes usable again.
        val mat = _state.value.eligibleStock.find { it.materialCode == line.materialCode }
        if (mat == null) return
        selectMaterial(mat)

        val costCenter = _state.value.costCenters.find { it.costCenterCode == line.costCenterCode }
        val company = _state.value.companies.find { it.companyCode == line.companyCode }
        _state.update {
            it.copy(
                binInput = line.storageBinCode,
                quantity = formatQty(line.quantity),
                notes = line.itemText,
                selectedCostCenter = costCenter,
                costCenterSearchText = costCenter?.spinnerLabel() ?: "",
                selectedCompany = company,
                companySearchText = company?.spinnerLabel() ?: "",
                quantityError = null,
                stockError = null
            )
        }

        if (line.storageBinCode.isEmpty()) return
        // Re-scan the bin, then re-apply the saved batch / valuation type and quantity.
        pendingStockRestore = StockSelectionRestore(
            batch = line.batch,
            valuationType = line.valuationType,
            qty = formatQty(line.quantity)
        )
        scanBin(line.storageBinCode)
    }

    private fun selectMaterial(stock: SAPMaterialBatchValuationStock) {
        val alreadyAdded = _state.value.addedLines.any { it.materialCode == stock.materialCode }
        if (alreadyAdded) {
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.material_already_added), matDlg = false) }
            return
        }
        _state.update {
            it.copy(
                selectedMatStock = stock,
                matInput = stock.materialCode ?: "",
                matDlg = false,
                matErrorMessage = null,
                selBinData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                quantity = ""
            )
        }
    }

    private fun clearMaterial() {
        _state.update {
            it.copy(
                selectedMatStock = null,
                matInput = "",
                matErrorMessage = null,
                selBinData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                quantity = "",
                quantityError = null
            )
        }
    }

    private fun clearBin() {
        _state.update {
            it.copy(
                selBinData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                quantity = "",
                quantityError = null
            )
        }
    }

    private fun scanBin(binCode: String) {
        val matStock = _state.value.selectedMatStock
        if (matStock == null) {
            _state.update { it.copy(binCodeError = Message.Resource(R.string.please_select_material_first)) }
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
                            binInput = binData.storageBinCode,
                            binCodeError = null
                        )
                    }
                    loadStock(matStock, binData.storageBinCode)
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

    private fun loadStock(matStock: SAPMaterialBatchValuationStock, storageBinCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true, stockError = null) }
            when (val result = getMaterialOnHandStockUseCase(storageBinCode, matStock.materialCode ?: "")) {
                is NetworkResult.Success -> {
                    val stockList = result.data
                    if (stockList.isEmpty()) {
                        _state.update {
                            it.copy(
                                stockOnHandList = emptyList(),
                                selectedOnHandStock = null,
                                availableStock = 0.0,
                                maxQty = 0.0,
                                quantity = "0",
                                stockError = Message.Resource(R.string.no_stock_available)
                            )
                        }
                    } else {
                        val resolved = resolveStockSelection(stockList, Double.MAX_VALUE, pendingStockRestore)
                        pendingStockRestore = null
                        _state.update {
                            it.copy(
                                stockOnHandList = stockList,
                                selectedOnHandStock = resolved.selected,
                                availableStock = resolved.availableQty,
                                maxQty = resolved.maxQty,
                                quantity = resolved.qty,
                                stockSearch = resolved.searchText,
                                quantityError = null,
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
        val maxQty = availQty
        _state.update {
            it.copy(
                selectedOnHandStock = stock,
                availableStock = availQty,
                maxQty = maxQty,
                quantity = if (maxQty > 0) formatQty(maxQty) else "0",
                stockError = null,
                quantityError = null
            )
        }
    }

    private fun addLine() {
        val st = _state.value
        val matStock = st.selectedMatStock ?: return
        val bin = st.selBinData ?: return
        val company = st.selectedCompany ?: return
        val costCenter = st.selectedCostCenter ?: return

        var hasError = false
        val isBatchManaged = st.stockOnHandList.any { it.isBatchManaged == true }
        val hasValuationType = !isBatchManaged && st.stockOnHandList.any { !it.valuationType.isNullOrEmpty() }

        if ((isBatchManaged || hasValuationType) && st.selectedOnHandStock == null) {
            val err = if (isBatchManaged) {
                Message.Resource(R.string.select_a_batch)
            } else {
                Message.Resource(R.string.select_valuation_type)
            }
            _state.update { it.copy(stockError = err) }
            hasError = true
        }

        val qtyValue = st.quantity.toDoubleOrNull() ?: 0.0
        if (qtyValue <= 0) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.please_enter_a_valid_quantity)) }
            hasError = true
        }

        if (qtyValue > st.maxQty) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.qty_exceed_max_qty) + formatQty(st.maxQty)) }
            hasError = true
        }

        if (hasError) return

        val isDuplicate = st.addedLines.any { it.materialCode == matStock.materialCode }
        if (isDuplicate) {
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.material_already_added)) }
            return
        }

        val line = ScrapIssueLine(
            materialCode = matStock.materialCode ?: "",
            materialDescription = matStock.description ?: "",
            plantCode = matStock.plantCode ?: "",
            warehouseCode = bin.warehouseCode,
            uom = matStock.unitOfMeasure ?: "",
            quantity = qtyValue,
            storageBinCode = bin.storageBinCode,
            costCenterCode = costCenter.costCenterCode,
            itemText = st.notes,
            batch = st.selectedOnHandStock?.batch,
            valuationType = st.selectedOnHandStock?.valuationType,
            companyCode = company.companyCode
        )

        _state.update { it.copy(
            addedLines = it.addedLines + line,
            selectedMatStock = null,
            matInput = "",
            selBinData = null,
            binInput = "",
            stockOnHandList = emptyList(),
            selectedOnHandStock = null,
            availableStock = 0.0,
            maxQty = 0.0,
            quantity = "",
            selectedCompany = null,
            selectedCostCenter = null,
            notes = "",
            quantityError = null,
            matErrorMessage = null,
            companySearchText = "",
            costCenterSearchText = "",
            matSearchText = ""
        ) }
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
//        if (st.documentHeaderText.isBlank()) {
//            _state.update { it.copy(documentHeaderError = Message.Resource(R.string.enter_maintenance_order_number_error)) }
//            return
//        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = PostScrapIssueDto(
                documentHeaderText = st.documentHeaderText,
                lines = st.addedLines.toDto()
            )
            when (val result = scrapIssueUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(addedLines = emptyList(), savingSuccessMessage = Message.real(result.message), documentHeaderText = "", selectedMatStock = null) }
                    loadEligibleStock()
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
