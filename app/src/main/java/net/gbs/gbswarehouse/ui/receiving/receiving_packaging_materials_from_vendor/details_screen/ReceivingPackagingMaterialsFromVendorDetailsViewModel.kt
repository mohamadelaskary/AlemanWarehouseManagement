package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.util.Log.d
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ReceivePackagingMaterialsDto
import net.gbs.gbswarehouse.data.remote.dto.response.ReceivePackagingMaterialsFromDiffPOsDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsLine
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPackagingMaterialsPurchaseOrdersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetPackagingMaterialsPurchaseOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceivePackagingMaterialsFromDiffPOsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceivePackagingMaterialsUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingPackagingMaterialsFromVendorDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPackagingMaterialsPurchaseOrderDataUseCase: GetPackagingMaterialsPurchaseOrderDataUseCase,
    private val getAllPackagingMaterialsPurchaseOrdersUseCase: GetAllPackagingMaterialsPurchaseOrdersUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val receivePackagingMaterialsUseCase: ReceivePackagingMaterialsUseCase,
    private val receivePackagingMaterialsFromDiffPOsUseCase: ReceivePackagingMaterialsFromDiffPOsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingPackagingMaterialsFromVendorState())
    val uiState = _uiState.asStateFlow()

    var matDlg by mutableStateOf(false)
    val accent = net.gbs.gbswarehouse.GREEN

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialCodeInputEntered(barcode))
        onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSelected(barcode))
    }

    fun onEvent(event: ReceivingPackagingMaterialsFromVendorEvent) {
        when (event) {
            is ReceivingPackagingMaterialsFromVendorEvent.OnScreenLoaded -> {
                if (_uiState.value.purchaseOrder == null) {
                    getPOData(event.poNumber)
                }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.materialCode, materialCodeError = "") }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSelected -> {
                selectMaterial(event.materialCode)
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.materials.filter {
                        it.materialCode?.contains(event.text, ignoreCase = true) == true ||
                                it.materialDescription?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(materialSearch = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnQuantityEntered -> {
                _uiState.update { it.copy(quantity = event.quantity, quantityError = "") }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnWarehouseSelected -> {
                _uiState.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = "") }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnCarNoEntered -> {
                _uiState.update { it.copy(carNo = event.carNo, carNoError = "") }
            }
            ReceivingPackagingMaterialsFromVendorEvent.OnAddButtonClicked -> {
                addItem()
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnDeleteAddedMaterialButtonClicked -> {
                _uiState.update { state ->
                    val newList = state.addedItems.toMutableList()
                    newList.removeAt(event.index)
                    state.copy(addedItems = newList)
                }
            }
            ReceivingPackagingMaterialsFromVendorEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingPackagingMaterialsFromVendorEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null, carNo = "", materialCodeInput = "", materialCodeError = "") }
            }
            ReceivingPackagingMaterialsFromVendorEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingPackagingMaterialsFromVendorEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }
            ReceivingPackagingMaterialsFromVendorEvent.OnChangePoButtonClicked -> {
                _uiState.update { it.copy(showChangePoDialog = true) }
                loadAllPOs()
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnPurchaseOrderSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.allPurchaseOrders.filter {
                        it.poNumber.contains(event.text, ignoreCase = true) ||
                                (it.vendorName?.contains(event.text, ignoreCase = true) == true)
                    }
                    state.copy(purchaseOrderSearch = event.text, filteredPurchaseOrders = filtered)
                }
            }
            is ReceivingPackagingMaterialsFromVendorEvent.OnPurchaseOrderSelected -> {
                _uiState.update { it.copy(showChangePoDialog = false) }
                getPOData(event.poNumber)
            }
            ReceivingPackagingMaterialsFromVendorEvent.OnDismissChangePoDialog -> {
                _uiState.update { it.copy(showChangePoDialog = false) }
            }

            is ReceivingPackagingMaterialsFromVendorEvent.OnWarehouseSearchTextChange -> _uiState.update { it.copy(warehouseSearchText = event.searchText) }
        }
    }

    private fun getPOData(poNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(purchaseOrderGetDataStatus = PackagingPurchaseOrderGetDataStatus.LOADING) }
            val result = getPackagingMaterialsPurchaseOrderDataUseCase(poNumber)
            when (result) {
                is NetworkResult.Success -> {
                    val po = result.data
                    _uiState.update {
                        it.copy(
                            purchaseOrder = po,
                            materials = po.purchaseOrderItems,
                            filteredMaterials = po.purchaseOrderItems,
                            purchaseOrderGetDataStatus = PackagingPurchaseOrderGetDataStatus.SUCCESS(po)
                        )
                    }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        else -> "Error loading PO data"
                    }
                    _uiState.update {
                        it.copy(
                            searchError = message,
                            purchaseOrderGetDataStatus = PackagingPurchaseOrderGetDataStatus.ERROR(message)
                        )
                    }
                }
            }
        }
    }

    private fun loadAllPOs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAllPurchaseOrdersLoading = true) }
            val result = getAllPackagingMaterialsPurchaseOrdersUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update {
                    it.copy(
                        allPurchaseOrders = result.data,
                        filteredPurchaseOrders = result.data,
                        isAllPurchaseOrdersLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isAllPurchaseOrdersLoading = false) }
            }
        }
    }

    private fun selectMaterial(materialCode: String) {
        val state = _uiState.value
        val material = state.materials.find { it.materialCode == materialCode }
        if (material != null) {
            val alreadyAdded = state.addedItems.any { it.purchaseOrder == state.purchaseOrder?.poNumber && it.purchaseOrderItem == material.poItemNumber }
            if (alreadyAdded) {
                _uiState.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
                return
            }
            _uiState.update {
                it.copy(
                    selectedMaterial = material,
                    materialCodeInput = materialCode,
                    materialCodeError = "",
                    carNo = material.carNo?:"",
                    quantity = material.remainingQuantity.toString()
                ) }
            loadWarehouses(material.plantCode ?: "")
        } else {
            _uiState.update { it.copy(materialCodeError = context.getString(R.string.wrong_material_code)) }
        }
    }

    private fun loadWarehouses(plantCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWarehousesLoading = true) }
            val result = getPlantWarehousesUseCase(plantCode)
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(warehouses = result.data, isWarehousesLoading = false) }
            } else {
                _uiState.update { it.copy(isWarehousesLoading = false) }
            }
        }
    }

    private fun addItem() {
        val state = _uiState.value
        val mat = state.selectedMaterial
        val qty = state.quantity.toDoubleOrNull() ?: 0.0
        val carNo = state.carNo
        val wh = state.selectedWarehouse

        if (mat == null) return
        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }
        if (qty > (mat.remainingQuantity ?: 0.0)) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, mat.remainingQuantity.toString())) }
            return
        }
        if (wh == null) {
            _uiState.update { it.copy(warehouseError = context.getString(R.string.please_select_a_warehouse)) }
            return
        }

        val alreadyAdded = state.addedItems.any { it.purchaseOrder == state.purchaseOrder?.poNumber && it.purchaseOrderItem == mat.poItemNumber }
        if (alreadyAdded) {
            _uiState.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
            return
        }
        if (carNo.isEmpty()){
            _uiState.update { it.copy(carNoError = context.getString(R.string.please_enter_car_number)) }
            return
        }

        val line = PackagingMaterialsLine(
            materialCode = mat.materialCode ?: "",
            materialDescription = mat.materialDescription ?: "",
            plantCode = mat.plantCode ?: "",
            warehouseCode = wh.warehouseCode,
            purchaseOrder = state.purchaseOrder?.poNumber ?: "",
            purchaseOrderItem = mat.poItemNumber,
            uom = mat.uom ?: "",
            receivedQuantity = qty,
            carNo = state.carNo.ifBlank { null }
        )

        _uiState.update {
            it.copy(
                addedItems = it.addedItems + line,
                selectedMaterial = null,
                quantity = "",
                selectedWarehouse = null,
                carNo = "",
                materialCodeInput = "",
                warehouseSearchText = ""
            )
        }
    }

    private fun saveReceiving() {
        val state = _uiState.value
        if (state.addedItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingLoading = true) }
            
            val poNumbers = state.addedItems.map { it.purchaseOrder }.distinct()
            val result = if (poNumbers.size == 1 && poNumbers[0] == state.purchaseOrder?.poNumber) {
                receivePackagingMaterialsUseCase(
                    ReceivePackagingMaterialsDto(
                        poNumber = poNumbers[0],
                        lines = state.addedItems.map { it.toDto() }
                    )
                )
            } else {
                receivePackagingMaterialsFromDiffPOsUseCase(
                    ReceivePackagingMaterialsFromDiffPOsDto(
                        lines = state.addedItems.map { it.toDto() }
                    )
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSavingLoading = false,
                            showSuccessDialog = true,
                            savingSuccessStatusMessage = context.getString(R.string.success_save),
                            addedItems = emptyList()
                        )
                    }
                    // Refresh current PO if still on same screen
                    state.purchaseOrder?.poNumber?.let { getPOData(it) }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        else -> "Error saving receiving"
                    }
                    _uiState.update {
                        it.copy(
                            isSavingLoading = false,
                            showErrorDialog = true,
                            savingErrorStatusMessage = message
                        )
                    }
                }
            }
        }
    }
}
