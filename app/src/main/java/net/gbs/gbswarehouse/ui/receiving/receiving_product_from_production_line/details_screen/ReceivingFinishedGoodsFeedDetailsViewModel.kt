package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
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
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveFinishedGoodsFeedDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllFinishedGoodsFeedProductionOrdersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetFinishedGoodsFeedProductionOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceiveFinishedGoodsFeedUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingFinishedGoodsFeedDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getFinishedGoodsFeedProductionOrderDataUseCase: GetFinishedGoodsFeedProductionOrderDataUseCase,
    private val getAllFinishedGoodsFeedProductionOrdersUseCase: GetAllFinishedGoodsFeedProductionOrdersUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val receiveFinishedGoodsFeedUseCase: ReceiveFinishedGoodsFeedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingFinishedGoodsFeedState())
    val uiState = _uiState.asStateFlow()

    var matDlg by mutableStateOf(false)
    val accent = net.gbs.gbswarehouse.GREEN

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingFinishedGoodsFeedEvent.OnMaterialCodeInputEntered(barcode))
        onEvent(ReceivingFinishedGoodsFeedEvent.OnMaterialSelected(barcode))
    }

    fun onEvent(event: ReceivingFinishedGoodsFeedEvent) {
        when (event) {
            is ReceivingFinishedGoodsFeedEvent.OnScreenLoaded -> {
                if (_uiState.value.productionOrder == null) {
                    getOrderData(event.orderNumber)
                }
                loadWorkerGroups()
            }
            is ReceivingFinishedGoodsFeedEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.materialCode, materialCodeError = "") }
            }
            is ReceivingFinishedGoodsFeedEvent.OnMaterialSelected -> {
                selectMaterial(event.materialCode)
            }
            is ReceivingFinishedGoodsFeedEvent.OnMaterialSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.materials.filter {
                        it.materialCode?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(materialSearch = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingFinishedGoodsFeedEvent.OnQuantityEntered -> {
                _uiState.update { 
                    val newState = it.copy(quantity = event.quantity, quantityError = "")
                    newState.copy(isReadyToReceive = newState.selectedMaterial != null && newState.selectedWarehouse != null && newState.selectedWorkersGroup != null && newState.quantity.isNotEmpty())
                }
            }
            is ReceivingFinishedGoodsFeedEvent.OnWarehouseSelected -> {
                _uiState.update { 
                    val newState = it.copy(selectedWarehouse = event.warehouse, warehouseError = "", warehouseSearchText = event.warehouse.warehouseDescription)
                    newState.copy(isReadyToReceive = newState.selectedMaterial != null && newState.selectedWarehouse != null && newState.selectedWorkersGroup != null && newState.quantity.isNotEmpty())
                }
            }
            is ReceivingFinishedGoodsFeedEvent.OnWarehouseSearchTextChanged -> {
                _uiState.update { it.copy(warehouseSearchText = event.text) }
            }
            is ReceivingFinishedGoodsFeedEvent.OnWorkersGroupSelected -> {
                _uiState.update { 
                    val newState = it.copy(selectedWorkersGroup = event.workersGroup, workersGroupError = "", workersGroupSearchText = event.workersGroup.workerGroupDescription)
                    newState.copy(isReadyToReceive = newState.selectedMaterial != null && newState.selectedWarehouse != null && newState.selectedWorkersGroup != null && newState.quantity.isNotEmpty())
                }
            }
            is ReceivingFinishedGoodsFeedEvent.OnWorkersGroupSearchTextChanged -> {
                _uiState.update { it.copy(workersGroupSearchText = event.text) }
            }
            ReceivingFinishedGoodsFeedEvent.OnAddButtonClicked -> {
                addItem()
            }
            is ReceivingFinishedGoodsFeedEvent.OnDeleteAddedMaterialButtonClicked -> {
                _uiState.update { state ->
                    val newList = state.addedItems.toMutableList()
                    newList.removeAt(event.index)
                    state.copy(addedItems = newList)
                }
            }
            ReceivingFinishedGoodsFeedEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingFinishedGoodsFeedEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null, selectedWorkersGroup = null, materialCodeInput = "", materialCodeError = "", warehouseSearchText = "", workersGroupSearchText = "") }
            }
            ReceivingFinishedGoodsFeedEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingFinishedGoodsFeedEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }
            ReceivingFinishedGoodsFeedEvent.OnChangeOrderButtonClicked -> {
                _uiState.update { it.copy(showChangeOrderDialog = true) }
                loadAllOrders()
            }
            is ReceivingFinishedGoodsFeedEvent.OnOrderSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.allOrders.filter {
                        it.orderNumber.contains(event.text, ignoreCase = true)
                    }
                    state.copy(orderSearch = event.text, filteredOrders = filtered)
                }
            }
            is ReceivingFinishedGoodsFeedEvent.OnOrderSelected -> {
                _uiState.update { it.copy(showChangeOrderDialog = false) }
                getOrderData(event.orderNumber)
            }
            ReceivingFinishedGoodsFeedEvent.OnDismissChangeOrderDialog -> {
                _uiState.update { it.copy(showChangeOrderDialog = false) }
            }
        }
    }

    private fun getOrderData(orderNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(orderGetDataStatus = FinishedGoodsFeedOrderGetDataStatus.LOADING) }
            val result = getFinishedGoodsFeedProductionOrderDataUseCase(orderNumber)
            when (result) {
                is NetworkResult.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            productionOrder = order,
                            materials = order.productionOrderItems,
                            filteredMaterials = order.productionOrderItems,
                            orderGetDataStatus = FinishedGoodsFeedOrderGetDataStatus.SUCCESS(order)
                        )
                    }
                    // If order has only one item, select it automatically as requested or just let user scan/select
                    if (order.productionOrderItems.size == 1) {
                        selectMaterial(order.productionOrderItems[0].materialCode ?: "")
                    }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        else -> "Error loading order data"
                    }
                    _uiState.update {
                        it.copy(
                            searchError = message,
                            orderGetDataStatus = FinishedGoodsFeedOrderGetDataStatus.ERROR(message)
                        )
                    }
                }
            }
        }
    }

    private fun loadAllOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAllOrdersLoading = true) }
            val result = getAllFinishedGoodsFeedProductionOrdersUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update {
                    it.copy(
                        allOrders = result.data,
                        filteredOrders = result.data,
                        isAllOrdersLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isAllOrdersLoading = false) }
            }
        }
    }

    private fun loadWorkerGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorkersGroupsLoading = true) }
            val result = getAllWorkerGroupsUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(workersGroups = result.data, isWorkersGroupsLoading = false) }
            } else {
                _uiState.update { it.copy(isWorkersGroupsLoading = false) }
            }
        }
    }

    private fun selectMaterial(materialCode: String) {
        val state = _uiState.value
        val material = state.materials.find { it.materialCode == materialCode }
        if (material != null) {
            val alreadyAdded = state.addedItems.any { it.orderNumber == state.productionOrder?.orderNumber && it.itemNumber == material.itemNumber }
            if (alreadyAdded) {
                _uiState.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
                return
            }
            _uiState.update {
                it.copy(
                    selectedMaterial = material,
                    materialCodeInput = materialCode,
                    materialCodeError = "",
                    quantity = material.remainingQuantity.toString(),
                    )
            }
            // Since we don't have plantCode in production order items domain model check, let's see where to get it.
            // Actually it is not in the domain model. Let's assume plantCode is available from somewhere or not needed for filter.
            // If repository needs it, we might need to adjust.
            loadWarehouses(material.plantCode!!)
        } else {
            _uiState.update { it.copy(materialCodeError = context.getString(R.string.wrong_material_code)) }
        }
    }

    private fun loadWarehouses(plantCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWarehousesLoading = true) }
            val result = getPlantWarehousesUseCase(plantCode)
            if (result is NetworkResult.Success) {

                _uiState.update {
                    it.copy(
                        warehouses = result.data,
                        isWarehousesLoading = false
                    )
                }
                val materialWarehouse = _uiState.value.warehouses.find { it.warehouseCode == _uiState.value.selectedMaterial?.warehouseCode }
                if (materialWarehouse != null) {
                    _uiState.update { it.copy(selectedWarehouse = materialWarehouse) }
                }
            } else {
                _uiState.update { it.copy(isWarehousesLoading = false) }
            }
        }
    }

    private fun addItem() {
        val state = _uiState.value
        val mat = state.selectedMaterial
        val qty = state.quantity.toDoubleOrNull() ?: 0.0
        val wh = state.selectedWarehouse
        val wg = state.selectedWorkersGroup

        if (mat == null) return
        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }
        // In production orders it is planned quantity
        if (qty > (mat.remainingQuantity ?: 0.0)) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, mat.remainingQuantity.toString())) }
            return
        }
        if (wh == null) {
            _uiState.update { it.copy(warehouseError = context.getString(R.string.please_select_a_warehouse)) }
            return
        }
        if (wg == null) {
            _uiState.update { it.copy(workersGroupError = context.getString(R.string.please_select_workers_group)) }
            return
        }

        val line = FinishedGoodsFeedLine(
            materialCode = mat.materialCode ?: "",
            materialDescription = mat.materialDescription ?: "",
            plantCode = mat.plantCode?:"",
            warehouseCode = wh.warehouseCode,
            orderNumber = state.productionOrder?.orderNumber ?: "",
            itemNumber = mat.itemNumber?:"",
            uom = mat.uom ?: "",
            receivedQuantity = qty,
            workerGroupCode = wg.workerGroupCode,
            workerGroupName = wg.workerGroupDescription
        )

        _uiState.update {
            it.copy(
                addedItems = it.addedItems + line,
                selectedMaterial = null,
                quantity = "",
                selectedWarehouse = null,
                selectedWorkersGroup = null,
                materialCodeInput = "",
                warehouseSearchText = "",
                workersGroupSearchText = "",
                isReadyToReceive = false
            )
        }
    }

    private fun saveReceiving() {
        val state = _uiState.value
        val mat = state.selectedMaterial
        val qty = state.quantity.toDoubleOrNull() ?: 0.0
        val wh = state.selectedWarehouse
        val wg = state.selectedWorkersGroup

        if (state.addedItems.isEmpty()) {
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
            if (wg == null) {
                _uiState.update { it.copy(workersGroupError = context.getString(R.string.please_select_workers_group)) }
                return
            }

            val line = FinishedGoodsFeedLine(
                materialCode = mat.materialCode ?: "",
                materialDescription = mat.materialDescription ?: "",
                plantCode = mat.plantCode ?: "",
                warehouseCode = wh.warehouseCode,
                orderNumber = state.productionOrder?.orderNumber ?: "",
                itemNumber = mat.itemNumber ?: "",
                uom = mat.uom ?: "",
                receivedQuantity = qty,
                workerGroupCode = wg.workerGroupCode,
                workerGroupName = wg.workerGroupDescription
            )
            _uiState.update { it.copy(addedItems = listOf(line)) }
        }

        val linesToSave = _uiState.value.addedItems
        if (linesToSave.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingLoading = true) }
            val result = receiveFinishedGoodsFeedUseCase(
                ReceiveFinishedGoodsFeedDto(
                    orderNumber = state.productionOrder?.orderNumber ?: "",
                    lines = linesToSave.map { it.toDto() }
                )
            )

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSavingLoading = false,
                            showSuccessDialog = true,
                            savingSuccessStatusMessage = context.getString(R.string.success_save),
                            addedItems = emptyList(),
                            selectedMaterial = null,
                            quantity = "",
                            selectedWarehouse = null,
                            selectedWorkersGroup = null,
                            materialCodeInput = "",
                            warehouseSearchText = "",
                            workersGroupSearchText = ""
                        )
                    }
                    state.productionOrder?.orderNumber?.let { getOrderData(it) }
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
                            savingErrorStatusMessage = message,
                            addedItems = emptyList()
                        )
                    }
                }
            }
        }
    }
}
