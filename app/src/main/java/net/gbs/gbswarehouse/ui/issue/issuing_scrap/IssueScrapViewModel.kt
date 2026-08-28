package net.gbs.gbswarehouse.ui.issue.issuing_scrap

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
import net.gbs.gbswarehouse.data.remote.dto.response.PostScrapIssueDto
import net.gbs.gbswarehouse.data.remote.dto.response.ScrapIssueLineDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.ScrapIssueLine
import net.gbs.gbswarehouse.domain.usecase.issuing.GetCompaniesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetCostCentersUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetScrapEligibleStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.ScrapIssueUseCase
import javax.inject.Inject

@HiltViewModel
class IssueScrapViewModel @Inject constructor(
    private val getScrapEligibleStockUseCase: GetScrapEligibleStockUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getCostCentersUseCase: GetCostCentersUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val scrapIssueUseCase: ScrapIssueUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(IssueScrapState())
    val state: StateFlow<IssueScrapState> = _state.asStateFlow()

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

//    private fun loadWarehouses(plantCode: String) {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            when (val result = getPlantWarehousesUseCase(plantCode)) {
//                is NetworkResult.Success -> {
//                    _state.update { it.copy(warehouses = result.data) }
//                }
//                else -> {}
//            }
//            _state.update { it.copy(isLoading = false) }
//        }
//    }

    fun onBarcodeScanned(barcode: String) {
        onEvent(IssueScrapEvent.OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssueScrapEvent) {
        when (event) {
            is IssueScrapEvent.OnBarcodeScanned -> {
                val st = _state.value
                if (st.binData == null) {
                    onEvent(IssueScrapEvent.OnScanBin(event.barcode))
                } else {
                    onEvent(IssueScrapEvent.OnScanMaterial(event.barcode))
                }
            }
            is IssueScrapEvent.OnDocumentHeaderChanged -> _state.update { it.copy(documentHeaderText = event.text) }
            
            is IssueScrapEvent.OnBinInputChanged -> _state.update { it.copy(binInput = event.text) }
            is IssueScrapEvent.OnScanBin -> scanBin(event.binCode)
            IssueScrapEvent.OnClearBin -> _state.update { it.copy(binData = null, binInput = "", binErrorMessage = null) }

            is IssueScrapEvent.OnMatInputChanged -> _state.update { it.copy(matInput = event.text) }
            is IssueScrapEvent.OnScanMaterial -> {
                val binData = _state.value.binData
                if (binData == null) {
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.please_scan_bin_first)) }
                    return
                }

                val alreadyAdded = _state.value.addedLines.any { it.materialCode == event.matCode }
                if (alreadyAdded) {
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.material_already_added)) }
                    return
                }
                val stock = _state.value.eligibleStock.find { it.materialCode == event.matCode }
                if (stock != null) {
                    if(!(binData.warehouseCode==stock.storageLocationCode && binData.plantCode == stock.plantCode) ){
                        _state.update { it.copy(matErrorMessage = context.getString(R.string.material_selected_not_found_in_warehouse_scanned)) }
                        return
                    }
                    onEvent(IssueScrapEvent.OnMaterialSelected(stock))
                } else {
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.material_not_in_scrap_list)) }
                }
            }
            is IssueScrapEvent.OnMaterialSelected -> {
                val binData = _state.value.binData
                if (binData == null) {
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.please_scan_bin_first)) }
                    return
                }
                if(!(binData.warehouseCode==event.stock.storageLocationCode && binData.plantCode == event.stock.plantCode) ){
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.material_selected_not_found_in_warehouse_scanned)) }
                    return
                }
                if (_state.value.addedLines.any { it.materialCode == event.stock.materialCode }) {
                    _state.update { it.copy(matErrorMessage = context.getString(R.string.material_already_added)) }
                    return
                }
                _state.update { it.copy(
                    selectedMatStock = event.stock,
                    matInput = event.stock.materialCode ?: "",
                    matDlg = false,
                    matErrorMessage = null,
                    quantity = (event.stock.quantity ?: 0).toString()
                ) }
               // loadWarehouses(event.stock.plantCode ?: "")
            }
            IssueScrapEvent.OnClearMaterial -> _state.update { it.copy(selectedMatStock = null, matInput = "", quantity = "") }
            IssueScrapEvent.OnToggleMaterialDialog -> _state.update { it.copy(matDlg = !it.matDlg) }
            is IssueScrapEvent.OnMaterialSearchTextChanged -> _state.update { it.copy(matSearchText = event.text) }

            is IssueScrapEvent.OnCompanySelected -> {
                _state.update { it.copy(selectedCompany = event.company, selectedCostCenter = null, costCenters = emptyList()) }
                loadCostCenters(event.company.companyCode)
            }
            is IssueScrapEvent.OnCompanySearchTextChanged -> _state.update { it.copy(companySearchText = event.text) }

            is IssueScrapEvent.OnCostCenterSelected -> _state.update { it.copy(selectedCostCenter = event.costCenter) }
            is IssueScrapEvent.OnCostCenterSearchTextChanged -> _state.update { it.copy(costCenterSearchText = event.text) }

            is IssueScrapEvent.OnNotesChanged -> _state.update { it.copy(notes = event.text) }

            is IssueScrapEvent.OnQuantityChanged -> _state.update { it.copy(quantity = event.text) }

            IssueScrapEvent.OnAddLine -> addLine()
            IssueScrapEvent.OnIssueAll -> issueAll()
            is IssueScrapEvent.OnDeleteLine -> {
                _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(event.index) }) }
            }
            IssueScrapEvent.OnDismissDialogs -> _state.update { it.copy(savingSuccessMessage = null, savingErrorMessage = null) }
        }
    }

    private fun addLine() {
        val st = _state.value
        val matStock = st.selectedMatStock ?: return
        val company = st.selectedCompany ?: return
        val costCenter = st.selectedCostCenter ?: return
        val warehouse = st.binData ?: return
        val qtyValue = st.quantity.toDoubleOrNull() ?: 0.0

        if (qtyValue <= 0) {
            _state.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }
        if (qtyValue> matStock.quantity?.toDouble()!!){
            _state.update { it.copy(quantityError = context.getString(R.string.quantity_exceeds_stock)) }
            return
        }

        val isDuplicate = st.addedLines.any { it.materialCode == matStock.materialCode }
        if (isDuplicate) {
            _state.update {
                it.copy(
                    matErrorMessage = context.getString(R.string.material_already_added),
                    matDlg = false
                )
            }
            return
        }

        val line = ScrapIssueLine(
            materialCode = matStock.materialCode ?: "",
            materialDescription = matStock.description ?: "",
            plantCode = matStock.plantCode ?: "",
            warehouseCode = warehouse.warehouseCode,
            uom = matStock.unitOfMeasure ?: "",
            quantity = qtyValue,
            storageBinCode = st.binData.storageBinCode ?: "",
            costCenterCode = costCenter.costCenterCode,
            itemText = st.notes
        )

        _state.update { it.copy(
            addedLines = it.addedLines + line,
            selectedMatStock = null,
            matInput = "",
            quantity = "",
            selectedCompany = null,
            selectedCostCenter = null,
            notes = "",
            quantityError = null,
            matErrorMessage = null,
            binData = null,
            binInput = "",
            companySearchText = "",
            costCenterSearchText = "",
            matSearchText = ""
        ) }
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
        if (st.documentHeaderText.isBlank()) {
            _state.update { it.copy(documentHeaderError = context.getString(R.string.enter_maintenance_order_number_error)) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = PostScrapIssueDto(
                documentHeaderText = st.documentHeaderText,
                lines = st.addedLines.toDto()
            )
            when (val result = scrapIssueUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(addedLines = emptyList(), savingSuccessMessage = result.message, documentHeaderText = "") }
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

    private fun scanBin(binCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isBinLoading = true, binErrorMessage = null) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(binData = result.data, binInput = result.data.storageBinCode) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(binErrorMessage = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(binErrorMessage = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(binErrorMessage = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(binErrorMessage = result.message) }
                else -> {}
            }
            _state.update { it.copy(isBinLoading = false) }
        }
    }
}
