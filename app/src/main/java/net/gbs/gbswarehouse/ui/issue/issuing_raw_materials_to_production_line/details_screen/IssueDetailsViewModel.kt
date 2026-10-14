package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen

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
import net.gbs.gbswarehouse.data.remote.dto.response.PostTransferToProductionDto
import net.gbs.gbswarehouse.data.remote.dto.response.TransferToProductionLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferToProductionReservationLine
import net.gbs.gbswarehouse.domain.usecase.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialOnHandStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetTransferToProductionReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.IssueTransferToProductionUseCase
import net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen.IssueDetailsEvent.*
import javax.inject.Inject
import net.gbs.gbswarehouse.StockSelectionRestore
import net.gbs.gbswarehouse.resolveStockSelection

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val getTransferToProductionReservationLinesUseCase: GetTransferToProductionReservationLinesUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getWorkersGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val issueTransferToProductionUseCase: IssueTransferToProductionUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(IssueDetailsState())
    val state: StateFlow<IssueDetailsState> = _state.asStateFlow()

    /**
     * Batch / valuation type + quantity of the line being edited, held until the stock
     * list for its bin has been re-fetched by loadStock.
     */
    private var pendingStockRestore: StockSelectionRestore? = null

    fun onBarcodeScanned(barcode: String) {
        onEvent(IssueDetailsEvent.OnBarcodeScanned(barcode))
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
                } else {
                    onEvent(OnScanBin(event.barcode))
                }
            }
            is OnBinInputChanged -> _state.update { it.copy(binInput = event.text, binCodeError = null) }
            is OnScanBin -> scanBin(event.binCode)
            OnClearBin -> clearBin()
            
            is OnMaterialSearchTextChanged -> {
                _state.update { state ->
                    val available = state.reservationLines.filterNot { it.isFullyIssued() }
                    val filtered = if (event.text.isEmpty()) {
                        available
                    } else {
                        available.filter {
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
            
            is OnWarehouseSearchTextChanged -> _state.update { it.copy(warehouseSearch = event.text) }
            is OnWarehouseSelected -> _state.update {
                it.copy(
                    selectedWarehouse = event.warehouse, 
                    warehouseSearch = event.warehouse.warehouseDescription ?: event.warehouse.warehouseCode,
                    warehouseError = null
                ) 
            }
            
            is OnQtyChanged -> {
                val maxQty = _state.value.maxQty
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > maxQty) {
                    Message.Resource(R.string.qty_exceed_max_qty) + formatQty(maxQty)
                } else null
                _state.update { it.copy(qty = event.text, qtyError = error) }
            }
            is OnNotesChanged -> {
                _state.update { it.copy(notes = event.notes) }
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
                        saved = false,
                        savingErrorMessage = null,
                        savingSuccessMessage = null,
                    )
                }
            }

            is OnAgingGroupSelected -> {
                _state.update {
                    it.copy(
                        selectedAgingGroup = event.it,
                        agingGroupSearch = event.it.workerGroupDescription ?: "",
                        agingGroupError = null
                    )
                }
            }
            is OnAgingGroupTextChange -> {
                _state.update {
                    it.copy(
                        agingGroupSearch = event.it,
                        agingGroupError = null
                    )
                }
            }
            is OnWorkersGroupSelected -> {
                _state.update {
                    it.copy(
                        selectedWorkersGroup = event.it,
                        workersGroupSearch = event.it.workerGroupDescription ?: "",
                        workersGroupError = null
                    )
                }
            }
            is OnWorkersGroupTextChange -> {
                _state.update {
                    it.copy(
                        workersGroupSearch = event.it,
                        workersGroupError = null
                    )
                }
            }
        }
    }

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toLong().toString() else qty.toString()
    }

    private fun TransferToProductionReservationLine.isFullyIssued(): Boolean {
        return (remainingQuantity ?: 0.0) <= 0.0
    }

    private fun removeItem(materialCode: String) {
        val lineWithMaterialCode = _state.value.addedLines.find { it.materialCode == materialCode }
        if (lineWithMaterialCode != null) {
            _state.update { it.copy(addedLines = it.addedLines - lineWithMaterialCode) }
        }
    }

    private fun editLine(addedLine: TransferToProductionLineDto) {
        removeItem(addedLine.materialCode)
        val line = _state.value.reservationLines.find { it.reservationItem == addedLine.reservationItem } ?: return
        selectReservationLine(line)
        _state.update {
            it.copy(
                binInput = addedLine.storageBinCode,
                notes = addedLine.notes ?: ""
            )
        }
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
            _state.update { it.copy(isLoading = true, loadingErrorMessage = null, reservationNumber = reservationNumber) }
            when (val result = getTransferToProductionReservationLinesUseCase(reservationNumber)) {
                is NetworkResult.Success -> {
                    val lines = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            reservationLines = lines,
                            filteredMaterials = lines.filterNot { it.isFullyIssued() }
                        )
                    }
                    if (lines.isNotEmpty()) {
                        loadPlantWarehouses(lines[0].plantCode ?: "")
                        loadWorkersGroups()
                    }
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

    private fun loadPlantWarehouses(plantCode: String) {
        viewModelScope.launch {
            when (val result = getPlantWarehousesUseCase(plantCode)) {
                is NetworkResult.Success -> _state.update { it.copy(warehousesList = result.data ?: emptyList()) }
                else -> {}
            }
        }
    }
    private fun loadWorkersGroups() {
        viewModelScope.launch {
            when (val result = getWorkersGroupsUseCase()) {
                is NetworkResult.Success -> _state.update { it.copy(workersGroupsList = result.data ?: emptyList()) }
                else -> {}
            }
        }
    }

    private fun scanBin(binCode: String) {
        val line = _state.value.selReservationLine
        if (line == null) {
            _state.update {
                it.copy(
                    binCodeError = Message.Resource(R.string.please_select_material_first),
                    binInput = "",
                    selBinData = null
                )
            }
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
                            binCodeError = null
                        )
                    }
                    loadStock(line, binData?.storageBinCode ?: binCode)
                }
                is NetworkResult.BusinessError -> clearBinOnError(Message.Real(result.message))
                is NetworkResult.HttpError -> clearBinOnError(Message.Real(result.message))
                is NetworkResult.NetworkError -> clearBinOnError(Message.Real(result.message))
                is NetworkResult.UnknownError -> clearBinOnError(Message.Real(result.message))
                else -> {}
            }
            _state.update { it.copy(isBinLoading = false) }
        }
    }

    private fun clearBinOnError(message: Message?) {
        _state.update {
            it.copy(
                binCodeError = message,
                binInput = "",
                selBinData = null,
                selectedOnHandStock = null,
                stockOnHandList = emptyList(),
                availableStock = 0.0,
                maxQty = 0.0,
                qty = "0",
                qtyError = null,
                stockSearch = "",
                stockError = null
            )
        }
    }

    private fun clearBin() {
        _state.update {
            it.copy(
                selBinData = null,
                binInput = "",
                binCodeError = null,
                qty = "",
                qtyError = null,
                selectedOnHandStock = null,
                stockOnHandList = emptyList(),
                availableStock = 0.0,
                maxQty = 0.0,
                stockSearch = "",
                stockError = null
            )
        }
    }

    private fun selectReservationLine(line: TransferToProductionReservationLine) {
        val alreadyAddedLine = _state.value.addedLines.find { it.materialCode == line.materialCode }
        if (alreadyAddedLine != null){
            _state.update {
                it.copy(
                    matErrorMessage = Message.Resource(R.string.material_already_added),
                    matDlg = false
                )
            }
            return
        }

        if (line.isFullyIssued()) {
            _state.update {
                it.copy(
                    matErrorMessage = Message.Resource(R.string.material_already_fully_issued),
                    matDlg = false
                )
            }
            return
        }

        _state.update {
            it.copy(selReservationLine = line,
                matInput = line.materialCode ?: "",
                matErrorMessage = null,
                maxQty = 0.0,
                qty = "0",
                qtyError = null,
                stockSearch = "",
                selectedOnHandStock = null,
                stockOnHandList = emptyList(),
                availableStock = 0.0,
                stockError = null,
                binInput = "",
                selBinData = null,
                binCodeError = null,
                warehouseSearch = "",
                selectedWarehouse = null,
                matDlg = false,
            )
        }
    }

    private fun loadStock(line: TransferToProductionReservationLine, storageBinCode: String) {
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
                                qtyError = null,
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
                is NetworkResult.BusinessError -> _state.update {
                    it.copy(
                        stockOnHandList = emptyList(),
                        selectedOnHandStock = null,
                        availableStock = 0.0,
                        maxQty = 0.0,
                        qty = "0",
                        stockError = Message.Real(result.message)
                    )
                }
                is NetworkResult.HttpError -> _state.update {
                    it.copy(
                        stockOnHandList = emptyList(),
                        selectedOnHandStock = null,
                        availableStock = 0.0,
                        maxQty = 0.0,
                        qty = "0",
                        stockError = Message.Real(result.message)
                    )
                }
                is NetworkResult.NetworkError -> _state.update {
                    it.copy(
                        stockOnHandList = emptyList(),
                        selectedOnHandStock = null,
                        availableStock = 0.0,
                        maxQty = 0.0,
                        qty = "0",
                        stockError = Message.Real(result.message)
                    )
                }
                is NetworkResult.UnknownError -> _state.update {
                    it.copy(
                        stockOnHandList = emptyList(),
                        selectedOnHandStock = null,
                        availableStock = 0.0,
                        maxQty = 0.0,
                        qty = "0",
                        stockError = Message.Real(result.message)
                    )
                }
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
            qtyError = null,
            stockOnHandList = emptyList(),
            stockSearch = "",
            matSearchText = "",
            matErrorMessage = null,
            availableStock = 0.0,
            maxQty = 0.0,
            stockError = null,
            binInput = "",
            selBinData = null,
            binCodeError = null,
            warehouseSearch = "",
            workersGroupSearch = "",
            agingGroupSearch = "",
            selectedWarehouse = null,
            matDlg = false,
            workersGroupError = null,
            agingGroupError = null,
            notes = null,

        ) }
    }

    private fun addLine() {
        val st = _state.value
        val line = st.selReservationLine
        if (line == null) {
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.please_select_material_first)) }
            return
        }
        val bin = st.selBinData
        if (bin == null) {
            _state.update { it.copy(binCodeError = Message.Resource(R.string.please_scan_bin_code_first)) }
            return
        }

        var hasError = false
        val isBatchManaged = st.stockOnHandList.any { it.isBatchManaged == true }
        val hasValuationType = !isBatchManaged && st.stockOnHandList.any { !it.valuationType.isNullOrEmpty() }
        var stockError: Message? = null
        var warehouseError: Message? = null
        var qtyError: Message? = null

        if (st.stockOnHandList.isEmpty()) {
            stockError = Message.Resource(R.string.no_stock_available)
            hasError = true
        } else if ((isBatchManaged || hasValuationType) && st.selectedOnHandStock == null) {
            stockError = Message.Resource(
                if (isBatchManaged) R.string.select_a_batch else R.string.select_valuation_type
            )
            hasError = true
        }

        if (st.selectedWarehouse == null) {
            warehouseError = Message.Resource(R.string.please_select_receiving_warehouse)
            hasError = true
        }

        val qtyValue = st.qty.toDoubleOrNull() ?: 0.0
        if (qtyValue <= 0.0) {
            qtyError = Message.Resource(R.string.please_enter_a_valid_quantity)
            hasError = true
        } else if (qtyValue > st.maxQty) {
            qtyError = Message.Resource(R.string.qty_exceed_max_qty) + formatQty(st.maxQty)
            hasError = true
        } else if (st.selectedOnHandStock != null && qtyValue > (st.selectedOnHandStock.availableQty ?: 0.0)) {
            qtyError = Message.Resource(R.string.qty_exceed_max_qty) + formatQty(st.selectedOnHandStock.availableQty ?: 0.0)
            hasError = true
        }

        if (hasError) {
            _state.update {
                it.copy(
                    stockError = stockError ?: it.stockError,
                    warehouseError = warehouseError ?: it.warehouseError,
                    qtyError = qtyError ?: it.qtyError
                )
            }
            return
        }

        val warehouse = st.selectedWarehouse!!
        val selectedStock = st.selectedOnHandStock
        val newLine = TransferToProductionLineDto(
            materialCode = line.materialCode ?: "",
            plantCode = bin.plantCode ?: "",
            warehouseCode = bin.warehouseCode ?: "",
            uom = line.uom ?: "",
            quantity = qtyValue,
            reservationNumber = line.reservationNumber,
            reservationItem = line.reservationItem,
            storageBinCode = bin.storageBinCode ?: "",
            valuationType = if (isBatchManaged) null else selectedStock?.valuationType,
            batch = if (isBatchManaged) selectedStock?.batch else null,
            receivingPlantCode = warehouse.plantCode ?: "",
            receivingWarehouseCode = warehouse.warehouseCode,
            receivingValuationType = if (isBatchManaged) null else selectedStock?.valuationType,
            receivingBatch = if (isBatchManaged) selectedStock?.batch else null,
            notes = st.notes?.trim()?.takeIf { it.isNotEmpty() },
            workersGroupCode = st.selectedWorkersGroup?.workerGroupCode ?: "",
            agingGroupCode = st.selectedAgingGroup?.workerGroupCode ?: ""
        )
        
        _state.update { it.copy(
            addedLines = it.addedLines + newLine,
            selectedWarehouse = null,
            warehouseSearch = "",
            warehouseError = null
        ) }
        clearMaterial()
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, savingErrorMessage = null) }
            val request = PostTransferToProductionDto(lines = st.addedLines)
            when (val result = issueTransferToProductionUseCase(request)) {
                is NetworkResult.Success -> {
                    val refreshedLines = result.data.reservationLines
                    _state.update {
                        it.copy(
                            saved = true,
                            savingSuccessMessage = Message.real(result.message),
                            addedLines = emptyList()
                        )
                    }
                    if (refreshedLines.isNotEmpty()) {
                        applyRefreshedLines(refreshedLines)
                    } else if (_state.value.reservationNumber.isNotEmpty()) {
                        loadOrderDetails(_state.value.reservationNumber)
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

    private fun applyRefreshedLines(refreshedLines: List<TransferToProductionReservationLine>) {
        _state.update { state ->
            val merged = if (state.reservationLines.isEmpty()) {
                refreshedLines
            } else {
                state.reservationLines.map { existing ->
                    val match = refreshedLines.firstOrNull {
                        it.reservationNumber == existing.reservationNumber &&
                            it.reservationItem == existing.reservationItem
                    } ?: return@map existing
                    existing.copy(
                        quantity = match.quantity ?: existing.quantity,
                        receivedQuantity = match.receivedQuantity ?: existing.receivedQuantity,
                        remainingQuantity = match.remainingQuantity ?: existing.remainingQuantity,
                        batch = match.batch ?: existing.batch,
                        isBatchManaged = match.isBatchManaged ?: existing.isBatchManaged,
                        materialDescription = match.materialDescription ?: existing.materialDescription,
                        uom = match.uom ?: existing.uom,
                        plantCode = match.plantCode ?: existing.plantCode,
                        storageLocationCode = match.storageLocationCode ?: existing.storageLocationCode,
                        receivingPlantCode = match.receivingPlantCode ?: existing.receivingPlantCode,
                        receivingStorageLocationCode = match.receivingStorageLocationCode ?: existing.receivingStorageLocationCode
                    )
                }
            }
            val search = state.matSearchText
            val available = merged.filterNot { it.isFullyIssued() }
            val filtered = if (search.isEmpty()) {
                available
            } else {
                available.filter {
                    it.materialCode?.contains(search, ignoreCase = true) == true ||
                        it.materialDescription?.contains(search, ignoreCase = true) == true
                }
            }
            state.copy(reservationLines = merged, filteredMaterials = filtered)
        }
    }
}
