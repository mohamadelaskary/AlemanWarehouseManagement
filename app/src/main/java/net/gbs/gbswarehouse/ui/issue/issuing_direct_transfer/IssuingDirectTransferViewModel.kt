package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

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
import net.gbs.gbswarehouse.data.remote.dto.response.DirectTransferLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectTransferDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectTransferUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialOnHandStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.domain.usecase.GetAllWorkerGroupsUseCase
import javax.inject.Inject
import net.gbs.gbswarehouse.StockSelectionRestore
import net.gbs.gbswarehouse.resolveStockSelection
import net.gbs.gbswarehouse.spinnerLabel
import net.gbs.gbswarehouse.stockSpinnerLabel

@HiltViewModel
class IssuingDirectTransferViewModel @Inject constructor(
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val directTransferUseCase: DirectTransferUseCase,
    private val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(IssuingDirectTransferState())
    val state: StateFlow<IssuingDirectTransferState> = _state.asStateFlow()

    init {
        loadWorkersGroups()
    }

    private fun loadWorkersGroups() {
        viewModelScope.launch {
            when (val result = getAllWorkerGroupsUseCase()) {
                is NetworkResult.Success -> _state.update { it.copy(workersGroupsList = result.data ?: emptyList()) }
                else -> {}
            }
        }
    }

    /** Saved line waiting for its bin stock list to load before it can be re-applied. */
    private var pendingLineRestore: DirectTransferLineDto? = null

    fun onBarcodeScanned(barcode: String) {
        onEvent(IssuingDirectTransferEvent.OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssuingDirectTransferEvent) {
        when (event) {
            is IssuingDirectTransferEvent.OnBarcodeScanned -> {
                val st = _state.value
                when {
                    st.binFromData == null -> onEvent(IssuingDirectTransferEvent.OnScanBinFrom(event.barcode))
                    st.selectedMaterial == null && st.groupedMaterials.isNotEmpty() -> onEvent(IssuingDirectTransferEvent.OnScanMaterial(event.barcode))
                }
            }
            is IssuingDirectTransferEvent.OnBinFromInputChanged -> _state.update { it.copy(binFromInput = event.text) }
            is IssuingDirectTransferEvent.OnScanBinFrom -> scanBinFrom(event.binCode)
            IssuingDirectTransferEvent.OnClearBinFrom -> clearBinFrom()

            is IssuingDirectTransferEvent.OnMaterialSearchTextChanged -> _state.update { it.copy(materialSearchText = event.text) }
            is IssuingDirectTransferEvent.OnMaterialSelected -> onMaterialSelected(event.material)
            is IssuingDirectTransferEvent.OnScanMaterial -> scanMaterial(event.matCode)
            IssuingDirectTransferEvent.OnClearMaterial -> clearMaterial()

            is IssuingDirectTransferEvent.OnStockSearchTextChanged -> _state.update { it.copy(stockSearchText = event.text) }
            is IssuingDirectTransferEvent.OnStockSelected -> onStockSelected(event.stock)

            is IssuingDirectTransferEvent.OnProductionWarehouseSelected -> _state.update { it.copy(
                selectedProductionWarehouse = event.warehouse,
                warehouseSearch = "${event.warehouse.warehouseDescription} (${event.warehouse.warehouseCode})"
            ) }
            is IssuingDirectTransferEvent.OnWarehouseSearchTextChanged -> _state.update { it.copy(warehouseSearch = event.text) }

            is IssuingDirectTransferEvent.OnWorkersGroupSelected -> _state.update { it.copy(
                selectedWorkersGroup = event.workerGroup,
                workersGroupSearch = event.workerGroup.spinnerLabel(),
                workersGroupError = null
            ) }
            is IssuingDirectTransferEvent.OnWorkersGroupTextChange -> _state.update { it.copy(workersGroupSearch = event.text) }
            is IssuingDirectTransferEvent.OnAgingGroupSelected -> _state.update { it.copy(
                selectedAgingGroup = event.agingGroup,
                agingGroupSearch = event.agingGroup.spinnerLabel(),
                agingGroupError = null
            ) }
            is IssuingDirectTransferEvent.OnAgingGroupTextChange -> _state.update { it.copy(agingGroupSearch = event.text) }

            is IssuingDirectTransferEvent.OnQuantityChanged -> onQuantityChanged(event.text)

            IssuingDirectTransferEvent.OnAddLine -> addLine()
            IssuingDirectTransferEvent.OnIssueAll -> issueAll()
            is IssuingDirectTransferEvent.OnDeleteLine -> {
                _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(event.index) }) }
            }
            is IssuingDirectTransferEvent.OnEditLine -> editLine(event.index)
            IssuingDirectTransferEvent.OnDismissDialogs -> {
                _state.update { it.copy(savingErrorMessage = null, savingSuccessMessage = null) }
            }
        }
    }

    private fun scanBinFrom(binCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                isBinFromLoading = true,
                binFromError = null,
                binFromData = null,
                stockOnHandList = emptyList(),
                groupedMaterials = emptyList(),
                selectedMaterial = null,
                selectedOnHandStock = null,
                matErrorMessage = null,
                materialSearchText = "",
                stockSearchText = "",
                quantity = "",
                quantityError = null
            ) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(binFromData = result.data, binFromInput = result.data.storageBinCode) }
                    loadMaterialsInBin(result.data.storageBinCode)
                    loadProductionWarehouses(result.data.plantCode)
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(binFromError = Message.Real(result.message)) }
                is NetworkResult.HttpError -> _state.update { it.copy(binFromError = Message.Real(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(binFromError = Message.Real(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(binFromError = Message.Real(result.message)) }
                else -> {}
            }
            _state.update { it.copy(isBinFromLoading = false) }
        }
    }

    private fun loadMaterialsInBin(storageBinCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true, matErrorMessage = null) }
            when (val result = getMaterialOnHandStockUseCase(storageBinCode = storageBinCode)) {
                is NetworkResult.Success -> {
                    val rawList = result.data
                    if (rawList.isEmpty()) {
                        _state.update { it.copy(
                            stockOnHandList = emptyList(),
                            groupedMaterials = emptyList(),
                            matErrorMessage = Message.Resource(R.string.no_stock_for_this_material_in_that_location)
                        ) }
                    } else {
                        val groupedMap = rawList.groupBy { it.materialCode ?: "" }
                        val groupedList = groupedMap.map { (code, stocks) ->
                            val name = stocks.firstOrNull { !it.materialDescription.isNullOrEmpty() }?.materialDescription?:""
                            GroupedMaterial(
                                materialCode = code,
                                materialName = name,
                                stocks = stocks
                            )
                        }
                        _state.update { it.copy(
                            stockOnHandList = rawList,
                            groupedMaterials = groupedList
                        ) }
                        if (groupedList.size == 1) {
                            onMaterialSelected(groupedList.first())
                        }
                        // An edit that had to re-scan the bin is waiting on this list.
                        applyPendingLineRestore()
                    }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(matErrorMessage = Message.Real(result.message)) }
                is NetworkResult.HttpError -> _state.update { it.copy(matErrorMessage = Message.Real(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(matErrorMessage = Message.Real(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(matErrorMessage = Message.Real(result.message)) }
                else -> {}
            }
            _state.update { it.copy(isStockLoading = false) }
        }
    }

    private fun scanMaterial(matCode: String) {
        val groupedList = _state.value.groupedMaterials
        val matched = groupedList.find { it.materialCode.equals(matCode, ignoreCase = true) }
        if (matched != null) {
            onMaterialSelected(matched)
        } else {
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.no_stock_for_this_material_in_that_location)) }
        }
    }

    private fun onMaterialSelected(groupedMat: GroupedMaterial) {
        val stocks = groupedMat.stocks
        val isBatchManaged = stocks.any { it.isBatchManaged == true }
        val hasValuationType = !isBatchManaged && stocks.any { !it.valuationType.isNullOrEmpty() }

        val labelText = if (groupedMat.materialName.isNotEmpty()) "${groupedMat.materialName} (${groupedMat.materialCode})" else groupedMat.materialCode

        if (!isBatchManaged && !hasValuationType) {
            val firstStock = stocks.firstOrNull()
            val totalAvailQty = stocks.sumOf { it.availableQty ?: 0.0 }
            _state.update { it.copy(
                selectedMaterial = groupedMat,
                materialSearchText = labelText,
                selectedOnHandStock = firstStock,
                availableQty = totalAvailQty,
                maxQty = totalAvailQty,
                quantity = if (totalAvailQty > 0) formatQty(totalAvailQty) else "",
                quantityError = null,
                matErrorMessage = null,
                stockSearchText = ""
            ) }
        } else {
            _state.update { it.copy(
                selectedMaterial = groupedMat,
                materialSearchText = labelText,
                selectedOnHandStock = null,
                availableQty = 0.0,
                maxQty = 0.0,
                quantity = "",
                quantityError = null,
                matErrorMessage = null,
                stockSearchText = ""
            ) }
            if (stocks.size == 1) {
                onStockSelected(stocks.first())
            }
        }
    }

    private fun onStockSelected(stock: MaterialOnHandStock) {
        val availQty = stock.availableQty ?: 0.0
        val stockLabel = if (stock.isBatchManaged == true) (stock.batch ?: "") else (stock.valuationType ?: "")
        _state.update { it.copy(
            selectedOnHandStock = stock,
            availableQty = availQty,
            maxQty = availQty,
            quantity = if (availQty > 0) formatQty(availQty) else "",
            quantityError = null,
            stockSearchText = stockLabel
        ) }
    }

    private fun onQuantityChanged(text: String) {
        val qtyValue = text.toDoubleOrNull() ?: 0.0
        val maxQty = _state.value.maxQty
        val error = if (qtyValue > maxQty) {
            Message.Resource(R.string.qty_exceed_max_qty) + maxQty
        } else null
        _state.update { it.copy(quantity = text, quantityError = error) }
    }

    private fun loadProductionWarehouses(plantCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getPlantWarehousesUseCase(plantCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(productionWarehouses = result.data) }
                }
                else -> {}
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun clearBinFrom() {
        _state.update { it.copy(
            binFromData = null,
            binFromInput = "",
            stockOnHandList = emptyList(),
            groupedMaterials = emptyList(),
            selectedMaterial = null,
            selectedOnHandStock = null,
            matErrorMessage = null,
            materialSearchText = "",
            stockSearchText = "",
            quantity = "",
            quantityError = null,
            selectedProductionWarehouse = null,
            warehouseSearch = "",
            selectedWorkersGroup = null,
            workersGroupSearch = "",
            workersGroupError = null,
            selectedAgingGroup = null,
            agingGroupSearch = "",
            agingGroupError = null
        ) }
    }

    private fun clearMaterial() {
        _state.update { it.copy(
            selectedMaterial = null,
            selectedOnHandStock = null,
            materialSearchText = "",
            stockSearchText = "",
            quantity = "",
            quantityError = null,
            matErrorMessage = null,
            availableQty = 0.0,
            maxQty = Double.MAX_VALUE,
            selectedWorkersGroup = null,
            workersGroupSearch = "",
            workersGroupError = null,
            selectedAgingGroup = null,
            agingGroupSearch = "",
            agingGroupError = null
        ) }
    }

    private fun addLine() {
        val st = _state.value
        val binFrom = st.binFromData ?: return
        val selectedMat = st.selectedMaterial ?: return
        val qtyValue = st.quantity.toDoubleOrNull() ?: 0.0
        val warehouseTo = st.selectedProductionWarehouse ?: return

        if (qtyValue <= 0) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.please_enter_a_valid_quantity)) }
            return
        }

        if (qtyValue > st.maxQty) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.qty_exceed_max_qty) + st.maxQty) }
            return
        }

        val selectedStock = st.selectedOnHandStock
        val batch = selectedStock?.batch
        val valuationType = selectedStock?.valuationType

        val isDuplicate = st.addedLines.any {
            it.materialCode == selectedMat.materialCode &&
            it.batch == batch &&
            it.receivingWarehouseCode == warehouseTo.warehouseCode
        }

        if (isDuplicate) {
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.material_already_added)) }
            return
        }

        val line = DirectTransferLineDto(
            materialCode = selectedMat.materialCode,
            plantCode = binFrom.plantCode,
            warehouseCode = binFrom.warehouseCode,
            uom = selectedStock?.uom ?: "",
            quantity = qtyValue,
            storageBinCode = binFrom.storageBinCode,
            valuationType = valuationType,
            batch = batch,
            receivingPlantCode = warehouseTo.plantCode,
            receivingWarehouseCode = warehouseTo.warehouseCode,
            receivingValuationType = null,
            receivingBatch = batch,
            workerGroupCode = st.selectedWorkersGroup?.workerGroupCode,
            agingGroupCode = st.selectedAgingGroup?.workerGroupCode,
            notes = null
        )

        _state.update { it.copy(
            addedLines = it.addedLines + line,
            selectedMaterial = null,
            selectedOnHandStock = null,
            materialSearchText = "",
            stockSearchText = "",
            quantity = "",
            quantityError = null,
            matErrorMessage = null,
            availableQty = 0.0,
            maxQty = Double.MAX_VALUE,
            selectedWorkersGroup = null,
            workersGroupSearch = "",
            workersGroupError = null,
            selectedAgingGroup = null,
            agingGroupSearch = "",
            agingGroupError = null
        ) }
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
//        if (st.addedLines.size!=st.groupedMaterials.size){
//            _state.update {
//                it.copy(
//                    savingSuccessMessage = Message.Resource(R.string.please_add_all_car_items_to_save_receiving)
//                )
//            }
//            return
//        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = PostDirectTransferDto(lines = st.addedLines)
            when (val result = directTransferUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(addedLines = emptyList(), savingSuccessMessage = Message.real(result.message)) }
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

    private fun editLine(index: Int) {
        val line = _state.value.addedLines.getOrNull(index) ?: return
        _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(index) }) }

        // The material list only holds what is in the currently scanned bin, so if the user
        // moved on to another bin the saved one has to be re-scanned before it can be restored.
        pendingLineRestore = line
        val inCurrentBin = _state.value.groupedMaterials.any { it.materialCode == line.materialCode }
        if (inCurrentBin) {
            applyPendingLineRestore()
        } else if (line.storageBinCode.isNotEmpty()) {
            scanBinFrom(line.storageBinCode)
        } else {
            pendingLineRestore = null
            _state.update { it.copy(matErrorMessage = Message.Resource(R.string.no_stock_for_this_material_in_that_location)) }
        }
    }

    /** Re-applies the saved line once its material is present in [state.groupedMaterials]. */
    private fun applyPendingLineRestore() {
        val line = pendingLineRestore ?: return
        val mat = _state.value.groupedMaterials.find { it.materialCode == line.materialCode }
        if (mat == null) {
            // Still not available; leave the pending line so a later load can apply it.
            return
        }
        pendingLineRestore = null

        onMaterialSelected(mat)

        val isBatchManaged = mat.stocks.any { it.isBatchManaged == true }
        val stock = if (isBatchManaged) {
            mat.stocks.find { it.batch == line.batch }
        } else {
            mat.stocks.firstOrNull { it.valuationType == line.valuationType }
                ?: mat.stocks.firstOrNull()
        }
        val warehouse = _state.value.productionWarehouses
            .find { it.warehouseCode == line.receivingWarehouseCode }
        val workerGroup = _state.value.workersGroupsList.find { it.workerGroupCode == line.workerGroupCode }
        val agingGroup = _state.value.workersGroupsList.find { it.workerGroupCode == line.agingGroupCode }

        _state.update {
            it.copy(
                selectedOnHandStock = stock,
                stockSearchText = stock?.stockSpinnerLabel() ?: "",
                quantity = formatQty(line.quantity),
                selectedProductionWarehouse = warehouse,
                warehouseSearch = warehouse?.spinnerLabel() ?: "",
                availableQty = stock?.availableQty ?: it.availableQty,
                maxQty = stock?.availableQty ?: it.maxQty,
                selectedWorkersGroup = workerGroup,
                workersGroupSearch = workerGroup?.spinnerLabel() ?: "",
                selectedAgingGroup = agingGroup,
                agingGroupSearch = agingGroup?.spinnerLabel() ?: "",
                quantityError = null,
                matErrorMessage = null
            )
        }
    }

    private fun formatQty(qty: Double): String {
        return if (qty % 1.0 == 0.0) qty.toInt().toString() else qty.toString()
    }
}
