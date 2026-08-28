package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

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
import net.gbs.gbswarehouse.data.remote.dto.response.DirectTransferLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectTransferDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectTransferUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import javax.inject.Inject

@HiltViewModel
class IssuingDirectTransferViewModel @Inject constructor(
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    private val getMaterialStockUseCase: GetMaterialStockUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val directTransferUseCase: DirectTransferUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(IssuingDirectTransferState())
    val state: StateFlow<IssuingDirectTransferState> = _state.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        onEvent(IssuingDirectTransferEvent.OnBarcodeScanned(barcode))
    }

    fun onEvent(event: IssuingDirectTransferEvent) {
        when (event) {
            is IssuingDirectTransferEvent.OnBarcodeScanned -> {
                val st = _state.value
                when {
                    st.binFromData == null -> onEvent(IssuingDirectTransferEvent.OnScanBinFrom(event.barcode))
                    st.selectedMatStock == null && st.stockList.isEmpty() -> onEvent(IssuingDirectTransferEvent.OnScanMaterial(event.barcode))
                    st.warehouseType.id == "1" && st.binToData == null -> onEvent(IssuingDirectTransferEvent.OnScanBinTo(event.barcode))
                }
            }
            is IssuingDirectTransferEvent.OnBinFromInputChanged -> _state.update { it.copy(binFromInput = event.text) }
            is IssuingDirectTransferEvent.OnScanBinFrom -> scanBinFrom(event.binCode)
            IssuingDirectTransferEvent.OnClearBinFrom -> _state.update { it.copy(binFromData = null, binFromInput = "") }

            is IssuingDirectTransferEvent.OnMatInputChanged -> _state.update { it.copy(matInput = event.text) }
            is IssuingDirectTransferEvent.OnScanMaterial -> scanMaterial(event.matCode)
            IssuingDirectTransferEvent.OnClearMaterial -> clearMaterial()

            is IssuingDirectTransferEvent.OnWarehouseTypeSelected -> {
                _state.update { it.copy(warehouseType = event.type) }
                if (event.type.id == "2" && _state.value.productionWarehouses.isEmpty()) {
                    loadProductionWarehouses()
                }
            }

            is IssuingDirectTransferEvent.OnBinToInputChanged -> _state.update { it.copy(binToInput = event.text) }
            is IssuingDirectTransferEvent.OnScanBinTo -> scanBinTo(event.binCode)
            IssuingDirectTransferEvent.OnClearBinTo -> _state.update { it.copy(binToData = null, binToInput = "") }

            is IssuingDirectTransferEvent.OnProductionWarehouseSelected -> _state.update { it.copy(selectedProductionWarehouse = event.warehouse) }

            is IssuingDirectTransferEvent.OnStockSearchTextChanged -> _state.update { it.copy(stockSearch = event.text) }
            is IssuingDirectTransferEvent.OnStockSelected -> {
                _state.update { it.copy(
                    selectedMatStock = event.stock,
                    stockSearch = "${event.stock.batch} (${event.stock.quantity})",
                    maxQty = event.stock.quantity?.toDouble() ?: Double.MAX_VALUE,
                    quantity = (event.stock.quantity ?: 0.0).toString()
                ) }
            }

            is IssuingDirectTransferEvent.OnQuantityChanged -> {
                val qtyValue = event.text.toDoubleOrNull() ?: 0.0
                val error = if (qtyValue > _state.value.maxQty) {
                    context.getString(R.string.qty_exceed_max_qty) + _state.value.maxQty
                } else null
                _state.update { it.copy(quantity = event.text, quantityError = error) }
            }

            IssuingDirectTransferEvent.OnAddLine -> addLine()
            IssuingDirectTransferEvent.OnIssueAll -> issueAll()
            is IssuingDirectTransferEvent.OnDeleteLine -> {
                _state.update { it.copy(addedLines = it.addedLines.toMutableList().apply { removeAt(event.index) }) }
            }
            IssuingDirectTransferEvent.OnDismissDialogs -> {
                _state.update { it.copy(savingErrorMessage = null, savingSuccessMessage = null) }
            }
        }
    }

    private fun scanBinFrom(binCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isBinFromLoading = true, binFromError = null) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(binFromData = result.data, binFromInput = result.data.storageBinCode) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(binFromError = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(binFromError = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(binFromError = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(binFromError = result.message) }
                else -> {}
            }
            _state.update { it.copy(isBinFromLoading = false) }
        }
    }

    private fun scanMaterial(matCode: String) {
        val st = _state.value
        val binFrom = st.binFromData ?: return
        val plantCode = binFrom.plantCode
        val warehouseCode = binFrom.warehouseCode

        // Optional: Check if the material is already fully processed or added
        // But since we can have multiple batches, we allow scanning.
        // We will prevent duplicates in addLine.

        viewModelScope.launch {
            _state.update { it.copy(isStockLoading = true, matErrorMessage = null) }
            when (val result = getMaterialStockUseCase(plantCode, matCode, warehouseCode)) {
                is NetworkResult.Success -> {
                    val stockList = result.data
                    if (stockList.isEmpty()) {
                        _state.update { it.copy(matErrorMessage = context.getString(R.string.no_stock_for_this_material_in_that_location)) }
                    } else {
                        val firstStock = stockList[0]
                        // If it's a single item and no batch, auto-select it
                        if (stockList.size == 1 && firstStock.batch.isNullOrEmpty()) {
                            _state.update { it.copy(
                                selectedMatStock = firstStock,
                                matInput = firstStock.materialCode?:"",
                                maxQty = firstStock.quantity?.toDouble() ?: Double.MAX_VALUE,
                                quantity = (firstStock.quantity ?: 0.0).toString(),
                                stockList = emptyList()
                            ) }
                        } else {
                            _state.update { it.copy(
                                stockList = stockList,
                                matInput = matCode
                            ) }
                        }
                    }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(matErrorMessage = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(matErrorMessage = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(matErrorMessage = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(matErrorMessage = result.message) }
                else -> {}
            }
            _state.update { it.copy(isStockLoading = false) }
        }
    }

    private fun scanBinTo(binCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isBinToLoading = true, binToError = null) }
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(binToData = result.data, binToInput = result.data.storageBinCode) }
                }
                is NetworkResult.BusinessError -> _state.update { it.copy(binToError = result.message) }
                is NetworkResult.HttpError -> _state.update { it.copy(binToError = result.message) }
                is NetworkResult.NetworkError -> _state.update { it.copy(binToError = result.message) }
                is NetworkResult.UnknownError -> _state.update { it.copy(binToError = result.message) }
                else -> {}
            }
            _state.update { it.copy(isBinToLoading = false) }
        }
    }

    private fun loadProductionWarehouses() {
        val plantCode = _state.value.binFromData?.plantCode ?: return
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

    private fun clearMaterial() {
        _state.update { it.copy(
            selectedMatStock = null,
            matInput = "",
            quantity = "",
            stockList = emptyList(),
            stockSearch = "",
            maxQty = Double.MAX_VALUE,
            matErrorMessage = null,
            binToData = null,
            binToInput = "",
            binFromData = null,
            binFromInput = "",
            selectedProductionWarehouse = null
        ) }
    }

    private fun addLine() {
        val st = _state.value
        val binFrom = st.binFromData ?: return
        val matStock = st.selectedMatStock ?: return
        val qtyValue = st.quantity.toDoubleOrNull() ?: 0.0
        
        if (qtyValue <= 0) {
            _state.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }

        if (qtyValue > (matStock.quantity?.toDouble() ?: 0.0)) {
            _state.update { it.copy(quantityError = context.getString(R.string.qty_exceed_max_qty) + (matStock.quantity ?: 0.0)) }
            return
        }

        val receivingPlantCode: String
        val receivingWarehouseCode: String
        
        if (st.warehouseType.id == "1") {
            val binTo = st.binToData ?: return
            receivingPlantCode = binTo.plantCode
            receivingWarehouseCode = binTo.warehouseCode
        } else {
            val whTo = st.selectedProductionWarehouse ?: return
            receivingPlantCode = whTo.plantCode
            receivingWarehouseCode = whTo.warehouseCode
        }

        // Check for duplicates in addedLines
        val isDuplicate = st.addedLines.any { 
            it.materialCode == matStock.materialCode && 
            it.batch == matStock.batch && 
            it.receivingWarehouseCode == receivingWarehouseCode
        }

        if (isDuplicate) {
            _state.update { it.copy(matErrorMessage = context.getString(R.string.material_already_added)) }
            return
        }

        val line = DirectTransferLineDto(
            materialCode = matStock.materialCode?:"",
            plantCode = binFrom.plantCode,
            warehouseCode = binFrom.warehouseCode,
            uom = matStock.unitOfMeasure?:"",
            quantity = qtyValue,
            storageBinCode = binFrom.storageBinCode,
            valuationType = null, 
            batch = matStock.batch,
            receivingPlantCode = receivingPlantCode,
            receivingWarehouseCode = receivingWarehouseCode,
            receivingValuationType = null,
            receivingBatch = matStock.batch 
        )
        
        _state.update { it.copy(
            addedLines = it.addedLines + line,
            selectedMatStock = null,
            matInput = "",
            quantity = "",
            stockList = emptyList(),
            stockSearch = "",
            binToData = null,
            binToInput = "",
            selectedProductionWarehouse = null,
            quantityError = null,
            matErrorMessage = null
        ) }
    }

    private fun issueAll() {
        val st = _state.value
        if (st.addedLines.isEmpty() || st.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = PostDirectTransferDto(lines = st.addedLines)
            when (val result = directTransferUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(addedLines = emptyList(), savingSuccessMessage = result.message) }
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
