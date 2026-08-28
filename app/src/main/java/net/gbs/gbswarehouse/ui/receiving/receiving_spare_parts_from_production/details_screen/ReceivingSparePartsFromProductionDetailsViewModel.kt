package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.details_screen

import android.content.Context
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
import net.gbs.gbswarehouse.data.remote.dto.response.PostSparePartsReturnDto
import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsReturnLineDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.SparePartsReturnLine
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetSparePartsReturnReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.PostSparePartsReturnUseCase
import javax.inject.Inject

@HiltViewModel
class ReceivingSparePartsFromProductionDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSparePartsReturnReservationLinesUseCase: GetSparePartsReturnReservationLinesUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val postSparePartsReturnUseCase: PostSparePartsReturnUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingSparePartsFromProductionDetailsState())
    val uiState = _uiState.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialCodeInputEntered(barcode))
    }

    fun onEvent(event: ReceivingSparePartsFromProductionDetailsEvent) {
        when (event) {
            is ReceivingSparePartsFromProductionDetailsEvent.OnScreenLoaded -> {
                val orderData = SparePartsReturnReservationOrder.fromJson(event.orderData)
                    _uiState.update {
                        it.copy(
                            orderData = orderData,
                            returnReason = event.returnReason
                        )
                    }
                    loadReservationLines(orderData.reservationNumber?:"")

             //   loadWorkerGroups()
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.barcode, materialCodeError = "") }
                selectMaterial(event.barcode)
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnMaterialSearchTextChanged -> {
                _uiState.update { state ->
                    val filtered = state.materials.filter {
                        it.materialCode?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(materialSearchText = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnMaterialSelected -> {
                selectMaterial(event.materialCode)
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnQuantityEntered -> {
                _uiState.update { it.copy(quantity = event.quantity, quantityError = "") }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnWarehouseSelected -> {
                _uiState.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = "", warehouseSearchText = event.warehouse.warehouseDescription) }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnWarehouseSearchTextChanged -> {
                _uiState.update { it.copy(warehouseSearchText = event.text) }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnNotesEntered -> {
                _uiState.update { it.copy(itemText = event.notes) }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnGoodsRecipientEntered -> {
                _uiState.update { it.copy(recipientName = event.name, recipientNameError = "") }
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnValuationTypeSelected -> {
                _uiState.update { it.copy(selectedValuationType = event.valuationType, valuationTypeError = "") }
            }
//            is ReceivingSparePartsFromProductionDetailsEvent.OnUnloadingPointEntered -> {
//                _uiState.update { it.copy(unloadingPoint = event.unloadingPoint) }
//            }
//            is ReceivingSparePartsFromProductionDetailsEvent.OnNaolonEntered -> {
//                _uiState.update { it.copy(naolon = event.naolon) }
//            }
            ReceivingSparePartsFromProductionDetailsEvent.OnAddButtonClicked -> {
                addItem()
            }
            is ReceivingSparePartsFromProductionDetailsEvent.OnDeleteAddedItemClicked -> {
                _uiState.update { state ->
                    val newList = state.addedItems.toMutableList()
                    newList.removeAt(event.index)
                    state.copy(addedItems = newList)
                }
            }
            ReceivingSparePartsFromProductionDetailsEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingSparePartsFromProductionDetailsEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null,
                    materialCodeInput = "", warehouseSearchText = "",
                    itemText = "", recipientName = ""
                ) }
            }
            ReceivingSparePartsFromProductionDetailsEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingSparePartsFromProductionDetailsEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }

            is ReceivingSparePartsFromProductionDetailsEvent.OnValuationSearchTextChanged ->
                _uiState.update { it.copy(valuationSearchText = event.text) }
        }
    }

    private fun loadReservationLines(reservationNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMaterialsLoading = true, error = "") }
            when (val result = getSparePartsReturnReservationLinesUseCase(reservationNumber)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            materials = result.data,
                            filteredMaterials = result.data,
                            isMaterialsLoading = false
                        )
                    }
                    if (result.data.size == 1) {
                        selectMaterial(result.data[0].materialCode ?: "")
                    }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        else -> "Error loading materials"
                    }
                    _uiState.update { it.copy(error = message, isMaterialsLoading = false) }
                }
            }
        }
    }

//    private fun loadWorkerGroups() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isWorkersGroupsLoading = true) }
//            val result = getAllWorkerGroupsUseCase()
//            if (result is NetworkResult.Success) {
//                _uiState.update { it.copy(workersGroups = result.data, isWorkersGroupsLoading = false) }
//            } else {
//                _uiState.update { it.copy(isWorkersGroupsLoading = false) }
//            }
//        }
//    }

    private fun selectMaterial(materialCode: String) {
        val state = _uiState.value
        val material = state.materials.find { it.materialCode == materialCode }
        if (material != null) {
            val alreadyAdded = state.addedItems.any { it.reservationItem == material.reservationItem }
            if (alreadyAdded) {
                _uiState.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
                return
            }
            _uiState.update { it.copy(selectedMaterial = material, materialCodeInput = materialCode, materialCodeError = "", quantity = (material.remainingQuantity ?: 0.0).toString()) }
            loadWarehouses(material.plantCode?:"")
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
        val itemText = state.itemText
        val recipientName = state.recipientName
        val valuationType = state.selectedValuationType

        if (mat == null) return
        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            return
        }
        if (wh == null) {
            _uiState.update { it.copy(warehouseError = context.getString(R.string.please_select_a_warehouse)) }
            return
        }
        if (recipientName.isBlank()) {
            _uiState.update { it.copy(recipientNameError = context.getString(R.string.please_enter_goods_recipient_name)) }
            return
        }
        if (mat.valuationTypes.isNotEmpty() && valuationType == null) {
            _uiState.update { it.copy(valuationTypeError = context.getString(R.string.please_select_a_valuation_type)) }
            return
        }

        val item = SparePartsReturnLine(
            materialCode = mat.materialCode?:"",
            materialDescription = mat.materialDescription?:"",
            plantCode = wh.plantCode,
            warehouseCode = wh.warehouseCode,
            uom = mat.uom?:"",
            receivedQuantity = qty,
            reservationNumber = state.orderData?.reservationNumber ?: "",
            reservationItem = mat.reservationItem ?: "",
            returnReason = state.returnReason?:"",
            recipientName = recipientName,
            valuationType = valuationType?.type,
            itemText = state.itemText,
        )

        _uiState.update {
            it.copy(
                addedItems = it.addedItems + item,
                selectedMaterial = null,
                selectedValuationType = null,
                quantity = "",
                selectedWarehouse = null,
                materialCodeInput = "",
                warehouseSearchText = "",
                valuationSearchText = "",
                itemText = "",
                recipientName = ""
            )
        }
    }

    private fun saveReceiving() {
        val state = _uiState.value
        
        // If nothing added, try to add current selection
        if (state.addedItems.isEmpty()) {
            val mat = state.selectedMaterial
            val qty = state.quantity.toDoubleOrNull() ?: 0.0
            val wh = state.selectedWarehouse
            val recipientName = state.recipientName
            val valuationType = state.selectedValuationType
            
            if (mat != null && qty > 0 && wh != null && recipientName.isNotBlank()
                && (mat.valuationTypes.isEmpty() || valuationType != null)) {
                addItem()
            } else {
                // Show errors if user tried to save without adding and current selection is incomplete
                if (mat == null) return
                if (qty <= 0) { _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }; return }
                if (wh == null) { _uiState.update { it.copy(warehouseError = context.getString(R.string.please_select_a_warehouse)) }; return }
                if (recipientName.isBlank()) { _uiState.update { it.copy(recipientNameError = context.getString(R.string.please_enter_goods_recipient_name)) }; return }
                if (mat.valuationTypes.isNotEmpty() && valuationType == null) {
                    _uiState.update { it.copy(valuationTypeError = context.getString(R.string.please_select_a_valuation_type)) }
                    return
                }
            }
        }

        val itemsToSave = _uiState.value.addedItems
        if (itemsToSave.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val request = PostSparePartsReturnDto(
                lines = itemsToSave.toDto()
            )
            
            when (val result = postSparePartsReturnUseCase(request)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            showSuccessDialog = true,
                            successMessage = context.getString(R.string.success_save),
                            addedItems = emptyList()
                        )
                    }
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        else -> "Error saving"
                    }
                    _uiState.update { it.copy(error = message, isSaving = false, showErrorDialog = true) }
                }
            }
        }
    }
}
