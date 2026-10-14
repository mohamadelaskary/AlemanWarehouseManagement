package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.details_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.IssueFinishedGoodsFeedBodyDto
import net.gbs.gbswarehouse.data.remote.dto.request.IssueFinishedGoodsFeedLineDto
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.usecase.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.GetFinishedGoodsFeedOrderLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.IssueFinishedGoodsFeedUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialOnHandStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import javax.inject.Inject
import net.gbs.gbswarehouse.StockSelectionRestore
import net.gbs.gbswarehouse.resolveStockSelection
import net.gbs.gbswarehouse.spinnerLabel

@HiltViewModel
class IssueDetailsViewModel @Inject constructor(
    private val getFinishedGoodsFeedOrderLinesUseCase: GetFinishedGoodsFeedOrderLinesUseCase,
    private val issueFinishedGoodsFeedUseCase: IssueFinishedGoodsFeedUseCase,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(IssueDetailsState())
    val state = _state.asStateFlow()

    /**
     * Quantity of the line being edited, held until the stock list for its bin has been
     * re-fetched by loadStock so the reload does not overwrite it with the maximum.
     */
    private var pendingStockRestore: StockSelectionRestore? = null

    fun onBarcodeScanned(code: String) {
        onEvent(IssueDetailsEvent.OnBarcodeScanned(code))
    }

    fun onEvent(event: IssueDetailsEvent) {
        when (event) {
            is IssueDetailsEvent.LoadOrderDetails -> loadOrderDetails(event.carNo)
            is IssueDetailsEvent.OnBarcodeScanned -> {
                if (_state.value.selectedMaterial == null) {
                    val material = _state.value.order?.lines?.find { it.materialCode == event.code }
                    if (material != null) {
                        onEvent(IssueDetailsEvent.OnMaterialSelected(material))
                    } else {
                        _state.update {
                            it.copy(
                                error = Message.Resource(R.string.material_not_found_in_list),
                                showMaterialDialog = false
                            )
                        }
                    }
                } else if (_state.value.binData == null || _state.value.binInput.isEmpty()) {
                    scanBin(event.code)
                } else {
                    val material = _state.value.order?.lines?.find { it.materialCode == event.code }
                    if (material != null) {
                        onEvent(IssueDetailsEvent.OnMaterialSelected(material))
                    } else {
                        scanBin(event.code)
                    }
                }
            }
            is IssueDetailsEvent.OnBinInputChanged -> _state.update { it.copy(binInput = event.text, binCodeError = null) }
            is IssueDetailsEvent.OnScanBin -> scanBin(event.binCode)
            is IssueDetailsEvent.OnClearBin -> clearBin()
            is IssueDetailsEvent.OnStockSearchTextChanged -> _state.update { it.copy(stockSearch = event.text) }
            is IssueDetailsEvent.OnOnHandStockSelected -> onOnHandStockSelected(event.stock)

            is IssueDetailsEvent.OnMaterialCodeInputChanged -> _state.update { it.copy(materialCodeInput = event.text) }
            is IssueDetailsEvent.OnMaterialSelected -> selectMaterial(event.item)
            is IssueDetailsEvent.OnMaterialSearchTextChanged -> {
                _state.update { it.copy(materialSearchText = event.text) }
                filterMaterials()
            }
            is IssueDetailsEvent.OnToggleMaterialDialog -> _state.update { it.copy(showMaterialDialog = !it.showMaterialDialog) }
            is IssueDetailsEvent.OnClearMaterial -> clearMaterial()
            is IssueDetailsEvent.OnQuantityChanged -> {
                val maxQty = _state.value.maxQty
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > maxQty) {
                    Message.Resource(R.string.qty_exceed_max_qty) + formatQty(maxQty)
                } else null
                _state.update { it.copy(quantity = event.text, quantityError = error) }
            }
            is IssueDetailsEvent.OnWorkersGroupSelected -> _state.update { it.copy(selectedWorkersGroup = event.workersGroup) }
            is IssueDetailsEvent.OnWorkersGroupSearchTextChanged -> _state.update { it.copy(workersGroupSearchText = event.text) }
            is IssueDetailsEvent.OnAddLine -> addLine()
            is IssueDetailsEvent.OnDeleteLine -> {
                _state.update { it.copy(addedItems = it.addedItems.toMutableList().apply { removeAt(event.index) }) }
            }
            is IssueDetailsEvent.OnEditLine -> editLine(event.index)
            is IssueDetailsEvent.OnSave -> save()
            is IssueDetailsEvent.OnDismissDialogs -> _state.update { it.copy(savingSuccess = false, savingError = null) }
        }
    }

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toLong().toString() else qty.toString()
    }

    private fun editLine(index: Int) {
        val line = _state.value.addedItems.getOrNull(index) ?: return
        _state.update { it.copy(addedItems = it.addedItems.toMutableList().apply { removeAt(index) }) }

        // Re-select the material so the bin / stock section becomes usable again. Search the
        // unfiltered order items, otherwise this fails while a material search text is set.
        val mat = _state.value.order?.lines.orEmpty()
            .firstOrNull { it.materialCode == line.materialCode }
        if (mat == null) return
        selectMaterial(mat)

        val group = _state.value.workersGroups.find { it.workerGroupCode == line.workerGroupCode }
        _state.update {
            it.copy(
                binInput = line.storageBinCode ?: "",
                quantity = formatQty(line.receivedQuantity),
                selectedWorkersGroup = group,
                workersGroupSearchText = group?.spinnerLabel() ?: "",
                quantityError = null,
                stockError = null
            )
        }

        if (line.storageBinCode.isNullOrEmpty()) return
        // Re-scan the bin, then re-apply the saved batch / valuation type and quantity.
        pendingStockRestore = StockSelectionRestore(
            batch = line.batch,
            valuationType = line.valuationType,
            qty = formatQty(line.receivedQuantity)
        )
        scanBin(line.storageBinCode)
    }

    private fun loadOrderDetails(carNo: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getFinishedGoodsFeedOrderLinesUseCase(carNo)) {
                is NetworkResult.Success -> {
                    val order = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            order = order,
                            filteredMaterials = order.lines
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false, error = Message.Real("Failed to load order items")) }
                }
            }
            
            when (val result = getAllWorkerGroupsUseCase()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(workersGroups = result.data) }
                }
                else -> {}
            }
        }
    }

    private fun selectMaterial(item: FinishedGoodsFeedItem) {
        val alreadyAdded = _state.value.addedItems.any { it.materialCode == item.materialCode }
        if (alreadyAdded) {
            _state.update {
                it.copy(
                    error = Message.Resource(R.string.material_already_added),
                    showMaterialDialog = false
                )
            }
            return
        }

        _state.update {
            it.copy(
                selectedMaterial = item,
                materialCodeInput = item.materialCode ?: "",
                showMaterialDialog = false,
                error = null,
                binData = null,
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

        val bin = _state.value.binData
        if (bin != null) {
            loadStock(item, bin.storageBinCode ?: "")
        }
    }

    private fun clearMaterial() {
        _state.update {
            it.copy(
                selectedMaterial = null,
                materialCodeInput = "",
                quantity = "",
                quantityError = null,
                binData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                error = null
            )
        }
    }

    private fun scanBin(code: String) {
        val material = _state.value.selectedMaterial
        if (material == null) {
            _state.update { it.copy(binCodeError = Message.Resource(R.string.please_scan_bin_code_first), binInput = "", binData = null) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isBinLoading = true, binCodeError = null) }
            when (val result = getStorageBinLocationIssuingUseCase(code)) {
                is NetworkResult.Success -> {
                    val bin = result.data
//                    if (bin.plantCode == material.plantCode) {
                        _state.update {
                            it.copy(
                                isBinLoading = false,
                                binData = bin,
                                binInput = bin.storageBinCode ?: code,
                                binCodeError = null
                            )
                        }
                        loadStock(material, bin.storageBinCode ?: code)
//                    } else {
//                        _state.update {
//                            it.copy(
//                                isBinLoading = false,
//                                binCodeError = "Bin plant code does not match material plant code",
//                                binInput = "",
//                                binData = null,
//                                stockOnHandList = emptyList(),
//                                selectedOnHandStock = null,
//                                availableStock = 0.0,
//                                maxQty = 0.0,
//                                quantity = ""
//                            )
//                        }
//                    }
                }
                is NetworkResult.BusinessError -> {
                    _state.update {
                        it.copy(
                            isBinLoading = false,
                            binCodeError = Message.Real(result.message),
                            binInput = "",
                            binData = null,
                            stockOnHandList = emptyList(),
                            selectedOnHandStock = null,
                            availableStock = 0.0,
                            maxQty = 0.0,
                            quantity = ""
                        )
                    }
                }
                is NetworkResult.HttpError -> {
                    _state.update {
                        it.copy(
                            isBinLoading = false,
                            binCodeError = Message.Real(result.message),
                            binInput = "",
                            binData = null,
                            stockOnHandList = emptyList(),
                            selectedOnHandStock = null,
                            availableStock = 0.0,
                            maxQty = 0.0,
                            quantity = ""
                        )
                    }
                }
                is NetworkResult.NetworkError -> {
                    _state.update {
                        it.copy(
                            isBinLoading = false,
                            binCodeError = Message.Real(result.message),
                            binInput = "",
                            binData = null,
                            stockOnHandList = emptyList(),
                            selectedOnHandStock = null,
                            availableStock = 0.0,
                            maxQty = 0.0,
                            quantity = ""
                        )
                    }
                }
                is NetworkResult.UnknownError -> {
                    _state.update {
                        it.copy(
                            isBinLoading = false,
                            binCodeError = Message.Real(result.message),
                            binInput = "",
                            binData = null,
                            stockOnHandList = emptyList(),
                            selectedOnHandStock = null,
                            availableStock = 0.0,
                            maxQty = 0.0,
                            quantity = ""
                        )
                    }
                }
                else -> {
                    _state.update {
                        it.copy(
                            isBinLoading = false,
                            binCodeError = Message.Real("Invalid bin code"),
                            binInput = "",
                            binData = null,
                            stockOnHandList = emptyList(),
                            selectedOnHandStock = null,
                            availableStock = 0.0,
                            maxQty = 0.0,
                            quantity = ""
                        )
                    }
                }
            }
        }
    }

    private fun clearBin() {
        _state.update {
            it.copy(
                binData = null,
                binInput = "",
                binCodeError = null,
                quantity = "",
                selectedOnHandStock = null,
                stockOnHandList = emptyList(),
                availableStock = 0.0,
                maxQty = 0.0
            )
        }
    }

    private fun loadStock(material: FinishedGoodsFeedItem, storageBinCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true, stockError = null) }
            when (val result = getMaterialOnHandStockUseCase(storageBinCode, material.materialCode ?: "")) {
                is NetworkResult.Success -> {
                    val stockList = result.data ?: emptyList()
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
                        val remainingQty = material.remainingQuantity ?: 0.0
                        val resolved = resolveStockSelection(stockList, remainingQty, pendingStockRestore)
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
        val remainingQty = _state.value.selectedMaterial?.remainingQuantity ?: 0.0
        val maxQty = minOf(remainingQty, availQty)
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

    private fun filterMaterials() {
        val query = _state.value.materialSearchText
        val items = _state.value.order?.lines ?: emptyList()
        val filtered = if (query.isEmpty()) {
            items
        } else {
            items.filter {
                it.materialCode?.contains(query, ignoreCase = true) == true ||
                        it.materialDescription?.contains(query, ignoreCase = true) == true
            }
        }
        _state.update { it.copy(filteredMaterials = filtered) }
    }

    private fun addLine() {
        val st = _state.value
        val bin = st.binData
        val material = st.selectedMaterial
        
        if (material == null) {
            _state.update { it.copy(error = Message.Real("Please select material first")) }
            return
        }
        if (bin == null) {
            _state.update { it.copy(binCodeError = Message.Real("Please scan bin first")) }
            return
        }

        var hasError = false
        val isBatchManaged = st.stockOnHandList.any { it.isBatchManaged == true }
        val hasValuationType = !isBatchManaged && st.stockOnHandList.any { !it.valuationType.isNullOrEmpty() }

        if ((isBatchManaged || hasValuationType) && st.selectedOnHandStock == null) {
            val err: Message = if (isBatchManaged) Message.Resource(R.string.select_a_batch) else Message.Resource(R.string.select_valuation_type)
            _state.update { it.copy(stockError = err) }
            hasError = true
        }

        val qtyStr = st.quantity
        val qty = qtyStr.toDoubleOrNull() ?: 0.0
        if (qty <= 0) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.please_enter_valid_quantity)) }
            hasError = true
        }
        if (qty > st.maxQty) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.qty_exceed_max_qty) + formatQty(st.maxQty)) }
            hasError = true
        }

        if (hasError) return

        val newLine = FinishedGoodsFeedLine(
            materialCode = material.materialCode ?: "",
            materialDescription = material.materialDescription ?: "",
            plantCode = material.plantCode ?: "",
            warehouseCode = bin.warehouseCode ?: "",
            orderNumber = material.salesOrder ?: st.order?.salesOrder ?: "",
            itemNumber = material.itemNo ?: "",
            uom = material.uom ?: "",
            receivedQuantity = qty,
            workerGroupCode = st.selectedWorkersGroup?.workerGroupCode,
            workerGroupName = st.selectedWorkersGroup?.workerGroupDescription,
            storageBinCode = bin.storageBinCode,
            batch = st.selectedOnHandStock?.batch,
            valuationType = st.selectedOnHandStock?.valuationType
        )
        
        _state.update {
            it.copy(
                addedItems = it.addedItems + newLine,
                selectedMaterial = null,
                materialCodeInput = "",
                quantity = "",
                quantityError = null,
                binData = null,
                binInput = "",
                binCodeError = null,
                stockOnHandList = emptyList(),
                selectedOnHandStock = null,
                availableStock = 0.0,
                maxQty = 0.0,
                error = null
            )
        }
    }

    private fun save() {
        val st = _state.value
        if (st.addedItems.isEmpty()) {
            _state.update { it.copy(savingError = Message.Real("Please add items first")) }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, savingError = null) }
            val request = IssueFinishedGoodsFeedBodyDto(
                shippingPointCode = st.order?.lines?.firstOrNull()?.shippingPointCode,
                carNo = st.order?.carNo,
                workerGroupCode = st.selectedWorkersGroup?.workerGroupCode,
                lines = ArrayList(st.addedItems.map {
                    IssueFinishedGoodsFeedLineDto(
                        salesOrder = it.orderNumber,
                        itemNo = it.itemNumber,
                        materialCode = it.materialCode,
                        quantity = it.receivedQuantity,
                        uom = it.uom,
                        plantCode = it.plantCode,
                        warehouseCode = it.warehouseCode,
                        storageBinCode = it.storageBinCode,
                        batch = it.batch,
                        valuationType = it.valuationType
                    )
                })
            )
            
            when (val result = issueFinishedGoodsFeedUseCase(request)) {
                is NetworkResult.Success -> {
                    val updatedItems = result.data
                    _state.update {
                        it.copy(
                            isSaving = false,
                            savingSuccess = true,
                            successMessage = Message.real(result.message),
                            addedItems = emptyList(),
                            order = it.order?.copy(lines = updatedItems),
                            filteredMaterials = updatedItems,
                            selectedMaterial = null,
                            materialCodeInput = "",
                            binData = null,
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
                is NetworkResult.BusinessError -> {
                    _state.update { it.copy(isSaving = false, savingError = Message.Real(result.message)) }
                }
                is NetworkResult.HttpError -> {
                    _state.update { it.copy(isSaving = false, savingError = Message.Real(result.message)) }
                }
                is NetworkResult.NetworkError -> {
                    _state.update { it.copy(isSaving = false, savingError = Message.Real(result.message)) }
                }
                is NetworkResult.UnknownError -> {
                    _state.update { it.copy(isSaving = false, savingError = Message.Real(result.message)) }
                }
                else -> {
                    _state.update { it.copy(isSaving = false, savingError = Message.Resource(R.string.unknown_error)) }
                }
            }
        }
    }
}
