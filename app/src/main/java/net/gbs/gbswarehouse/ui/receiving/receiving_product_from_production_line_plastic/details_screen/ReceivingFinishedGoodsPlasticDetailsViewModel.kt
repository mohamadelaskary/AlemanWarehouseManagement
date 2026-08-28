package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen

import android.content.Context
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
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveFinishedGoodsPlasticDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllFinishedGoodsPlasticProductionOrdersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetFinishedGoodsPlasticProductionOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceiveFinishedGoodsPlasticUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingFinishedGoodsPlasticDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getFinishedGoodsPlasticProductionOrderDataUseCase: GetFinishedGoodsPlasticProductionOrderDataUseCase,
    private val getAllFinishedGoodsPlasticProductionOrdersUseCase: GetAllFinishedGoodsPlasticProductionOrdersUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val receiveFinishedGoodsPlasticUseCase: ReceiveFinishedGoodsPlasticUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingFinishedGoodsPlasticState())
    val uiState = _uiState.asStateFlow()

    var matDlg by mutableStateOf(false)
    val accent = net.gbs.gbswarehouse.GREEN

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialCodeInputEntered(barcode))
        onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialSelected(barcode))
    }

    fun onEvent(event: ReceivingFinishedGoodsPlasticEvent) {
        when (event) {
            is ReceivingFinishedGoodsPlasticEvent.OnScreenLoaded -> {
                if (_uiState.value.productionOrder == null) {
                    getOrderData(event.orderNumber)
                }
                loadWorkerGroups()
            }
            is ReceivingFinishedGoodsPlasticEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.materialCode, materialCodeError = "") }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnMaterialSelected -> {
                selectMaterial(event.materialCode)
            }
            is ReceivingFinishedGoodsPlasticEvent.OnMaterialSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.materials.filter {
                        it.materialCode?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(materialSearch = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnQuantityEntered -> {
                if (event.quantity.toDoubleOrNull() == null) {
                    _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
                    return
                }
                if (event.quantity.toDouble() <= 0) {
                    _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
                    return
                }
                if(event.quantity.toDouble() > (uiState.value.selectedMaterial?.remainingQuantity?.toDouble() ?: 0.0)){
                     _uiState.update {
                         it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, (uiState.value.selectedMaterial?.remainingQuantity?.toDouble() ?: 0.0).toString()))
                     }
                }
                _uiState.update { 
                    it.copy(quantity = event.quantity, quantityError = "")
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnWarehouseSelected -> {
                _uiState.update { 
                    it.copy(selectedWarehouse = event.warehouse, warehouseError = "", warehouseSearchText = event.warehouse.warehouseDescription)
                }
                val state = _uiState.value
                if (state.selectedWarehouse != null && state.selectedWorkersGroup != null && state.quantity.isNotEmpty() && state.unloadingPoint.isNotBlank() && state.naolon.isNotBlank()) {
                    _uiState.update {
                        it.copy(isReadyToReceive = true)
                    }
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnWarehouseSearchTextChanged -> {
                _uiState.update { it.copy(warehouseSearchText = event.text) }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnWorkersGroupSelected -> {
                _uiState.update {
                    it.copy(
                        selectedWorkersGroup = event.workersGroup,
                        workersGroupError = "",
                        workersGroupSearchText = event.workersGroup.workerGroupDescription
                    )

                }
                val state = _uiState.value
                if (state.selectedWarehouse != null && state.selectedWorkersGroup != null && state.quantity.isNotEmpty() && state.unloadingPoint.isNotBlank() && state.naolon.isNotBlank()) {
                    _uiState.update {
                        it.copy(isReadyToReceive = true)
                    }
                }

            }
            is ReceivingFinishedGoodsPlasticEvent.OnWorkersGroupSearchTextChanged -> {
                _uiState.update { it.copy(workersGroupSearchText = event.text) }
                val state = _uiState.value
                if (state.selectedWarehouse != null && state.selectedWorkersGroup != null && state.quantity.isNotEmpty() && state.unloadingPoint.isNotBlank() && state.naolon.isNotBlank()) {
                    _uiState.update {
                        it.copy(isReadyToReceive = true)
                    }
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnUnloadingPointEntered -> {
                _uiState.update { it.copy(unloadingPoint = event.unloadingPoint, unloadingPointError = "") }
                val state = _uiState.value
                if (state.selectedWarehouse != null && state.selectedWorkersGroup != null && state.quantity.isNotEmpty() && state.unloadingPoint.isNotBlank() && state.naolon.isNotBlank()) {
                    _uiState.update {
                        it.copy(isReadyToReceive = true)
                    }
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnNaolonEntered -> {
                _uiState.update { it.copy(naolon = event.naolon, naolonError = "") }
                val state = _uiState.value
                if (state.selectedWarehouse != null && state.selectedWorkersGroup != null && state.quantity.isNotEmpty() && state.unloadingPoint.isNotBlank() && state.naolon.isNotBlank()) {
                    _uiState.update {
                        it.copy(isReadyToReceive = true)
                    }
                }
            }
            ReceivingFinishedGoodsPlasticEvent.OnAddButtonClicked -> {
                addItem()
            }
            is ReceivingFinishedGoodsPlasticEvent.OnDeleteAddedMaterialButtonClicked -> {
                _uiState.update { state ->
                    val newList = state.addedItems.toMutableList()
                    newList.removeAt(event.index)
                    state.copy(addedItems = newList)
                }
            }
            ReceivingFinishedGoodsPlasticEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingFinishedGoodsPlasticEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null, selectedWorkersGroup = null, materialCodeInput = "", materialCodeError = "", warehouseSearchText = "", workersGroupSearchText = "", unloadingPoint = "", naolon = "") }
            }
            ReceivingFinishedGoodsPlasticEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingFinishedGoodsPlasticEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }
            ReceivingFinishedGoodsPlasticEvent.OnChangeOrderButtonClicked -> {
                _uiState.update { it.copy(showChangeOrderDialog = true) }
                loadAllOrders()
            }
            is ReceivingFinishedGoodsPlasticEvent.OnOrderSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.allOrders.filter {
                        it.orderNumber.contains(event.text, ignoreCase = true)
                    }
                    state.copy(orderSearch = event.text, filteredOrders = filtered)
                }
            }
            is ReceivingFinishedGoodsPlasticEvent.OnOrderSelected -> {
                _uiState.update { it.copy(showChangeOrderDialog = false) }
                getOrderData(event.orderNumber)
            }
            ReceivingFinishedGoodsPlasticEvent.OnDismissChangeOrderDialog -> {
                _uiState.update { it.copy(showChangeOrderDialog = false) }
            }

            is ReceivingFinishedGoodsPlasticEvent.OnPalletWeightEntered ->
                _uiState.update { it.copy(palletWeight = event.palletWeight) }
        }
    }

    private fun getOrderData(orderNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(orderGetDataStatus = FinishedGoodsPlasticOrderGetDataStatus.LOADING) }
            val result = getFinishedGoodsPlasticProductionOrderDataUseCase(orderNumber)
            when (result) {
                is NetworkResult.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            productionOrder = order,
                            materials = order.productionOrderItems,
                            filteredMaterials = order.productionOrderItems,
                            orderGetDataStatus = FinishedGoodsPlasticOrderGetDataStatus.SUCCESS(order)
                        )
                    }
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
                            orderGetDataStatus = FinishedGoodsPlasticOrderGetDataStatus.ERROR(message)
                        )
                    }
                }
            }
        }
    }

    private fun loadAllOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAllOrdersLoading = true) }
            val result = getAllFinishedGoodsPlasticProductionOrdersUseCase()
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
            _uiState.update { it.copy(selectedMaterial = material, materialCodeInput = materialCode, materialCodeError = "", quantity = (material.remainingQuantity?.toDouble() ?: 0.0).toString()) }
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
        val wh = state.selectedWarehouse
        val wg = state.selectedWorkersGroup
        val unloadingPoint = state.unloadingPoint
//        val naolon = state.naolon.toDoubleOrNull() ?: 0.0
        val palletWeight = state.palletWeight.toDoubleOrNull() ?: 0.0
        if (mat == null) return
        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }
        if (qty > (mat.remainingQuantity?.toDouble() ?: 0.0)) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, (mat.remainingQuantity?.toDouble() ?: 0.0).toString())) }
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

        val line = FinishedGoodsPlasticLine(
            materialCode = mat.materialCode ?: "",
            materialDescription = "",
            plantCode = "",
            warehouseCode = wh.warehouseCode,
            orderNumber = state.productionOrder?.orderNumber ?: "",
            itemNumber = mat.itemNumber?:"",
            uom = mat.uom ?: "",
            receivedQuantity = qty,
            unloadingPoint = unloadingPoint.ifBlank { null },
//            naolon = if (state.naolon.isNotBlank()) naolon else null,
            workerGroupCode = wg.workerGroupCode,
            workerGroupName = wg.workerGroupDescription,
            palletWeight = palletWeight
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
                unloadingPoint = "",
                naolon = "",
                palletWeight = "",
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
        val unloadingPoint = state.unloadingPoint
        val naolon = state.naolon.toDoubleOrNull() ?: 0.0
        val palletWeight = state.palletWeight.toDoubleOrNull()

        if (state.addedItems.isEmpty()) {
            if (mat == null) return
            if (qty <= 0) {
                _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
                return
            }
            if (qty > (mat.remainingQuantity?.toDouble() ?: 0.0)) {
                _uiState.update { it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, (mat.remainingQuantity?.toDouble() ?: 0.0).toString())) }
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

            val line = FinishedGoodsPlasticLine(
                materialCode = mat.materialCode ?: "",
                materialDescription = "",
                plantCode = "",
                warehouseCode = wh.warehouseCode,
                orderNumber = state.productionOrder?.orderNumber ?: "",
                itemNumber = mat.itemNumber?:"",
                uom = mat.uom ?: "",
                receivedQuantity = qty,
                unloadingPoint = unloadingPoint.ifBlank { null },
                naolon = if (state.naolon.isNotBlank()) naolon else null,
                workerGroupCode = wg.workerGroupCode,
                workerGroupName = wg.workerGroupDescription,
                palletWeight = palletWeight
            )
            _uiState.update { it.copy(addedItems = listOf(line)) }
        }

        val linesToSave = _uiState.value.addedItems
        if (linesToSave.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingLoading = true) }
            val result = receiveFinishedGoodsPlasticUseCase(
                ReceiveFinishedGoodsPlasticDto(
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
                            workersGroupSearchText = "",
                            unloadingPoint = "",
                            naolon = "",
                            isReadyToReceive = false
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
