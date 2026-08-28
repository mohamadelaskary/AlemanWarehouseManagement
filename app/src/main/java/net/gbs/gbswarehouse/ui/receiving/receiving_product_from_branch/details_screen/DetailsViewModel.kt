package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

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
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveTransitOrderMaterialDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialLine
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllCarNumbersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllCarTypesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllLoadingVendorsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllNaolonDescriptionsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPackageTypesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetNaolonPerTonValueUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetTransitOrderMaterialPurchaseOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceiveTransitOrderMaterialUseCase
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonLoadingStatus
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getTransitOrderMaterialPurchaseOrderDataUseCase: GetTransitOrderMaterialPurchaseOrderDataUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getWorkersGroupUseCase: GetAllWorkerGroupsUseCase,
    private val getAllLoadingVendorsUseCase: GetAllLoadingVendorsUseCase,
    private val getAllPackageTypesUseCase: GetAllPackageTypesUseCase,
    private val getAllCarTypesUseCase: GetAllCarTypesUseCase,
    private val getAllNaolonDescriptionsUseCase: GetAllNaolonDescriptionsUseCase,
    private val getNaolonPerTonValueUseCase: GetNaolonPerTonValueUseCase,
    private val getAllCarNumbersUseCase: GetAllCarNumbersUseCase,
    private val receiveTransitOrderMaterialUseCase: ReceiveTransitOrderMaterialUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceivingProductFromBranchState())
    val uiState = _uiState.asStateFlow()

    var matDlg by mutableStateOf(false)

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered(barcode))
        onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(barcode))
    }

    fun onEvent(event: ReceivingProductFromBranchEvent) {
        when (event) {
            is ReceivingProductFromBranchEvent.OnScreenLoaded -> {
                if (_uiState.value.purchaseOrder == null) {
                    getPOData(event.poNumber)
                }
                loadAllNeededData()
            }
            is ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.materialCode, materialCodeError = "") }
            }
            is ReceivingProductFromBranchEvent.OnMaterialSelected -> {
                selectMaterial(event.materialCode)
            }
            is ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered -> {
                _uiState.update { state ->
                    val filtered = state.materials.filter {
                        it.materialCode?.contains(event.text, ignoreCase = true) == true ||
                                it.materialDescription?.contains(event.text, ignoreCase = true) == true
                    }
                    state.copy(materialSearch = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingProductFromBranchEvent.OnQuantityEntered -> {
                _uiState.update { it.copy(quantity = event.quantity, quantityError = "") }
                val state = _uiState.value
                if (state.naolonLoadStatus is NaolonLoadingStatus.SUCCESS) {
                    val naolonPerTon = (state.naolonLoadStatus as NaolonLoadingStatus.SUCCESS).naolon.toDoubleOrNull() ?: 0.0
                    val qty = event.quantity.toDoubleOrNull() ?: 0.0
                    val totalNaolon = naolonPerTon * qty
                    _uiState.update { it.copy(naolon = totalNaolon.toString()) }
                }
            }
            is ReceivingProductFromBranchEvent.OnWarehouseSelected -> {
                _uiState.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = "") }
            }
            is ReceivingProductFromBranchEvent.OnCarNoSelected -> {
                _uiState.update { it.copy(selectedCarNo = event.carNo, carNoError = "") }
            }
            ReceivingProductFromBranchEvent.OnAddButtonClicked -> {
                addItem()
            }
            is ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked -> {
                _uiState.update { state ->
                    val newList = state.addedItems.toMutableList()
                    newList.removeAt(event.index)
                    state.copy(addedItems = newList)
                }
            }
            ReceivingProductFromBranchEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingProductFromBranchEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null, selectedCarNo = null, materialCodeInput = "", materialCodeError = "") }
            }
            ReceivingProductFromBranchEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingProductFromBranchEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }
            is ReceivingProductFromBranchEvent.OnWarehouseSearchTextChange -> _uiState.update { it.copy(warehouseSearchText = event.searchText) }
            is ReceivingProductFromBranchEvent.OnCarNoTextSearchChange ->
                _uiState.update { it.copy(carNoSearchText = event.searchText, carNoError = "") }
            is ReceivingProductFromBranchEvent.OnCarTypeSelected -> {
                _uiState.update { it.copy(selectedCarType = event.carType, carTypeError = "", naolonLoadStatus = NaolonLoadingStatus.LOADING) }
                calculateNaolon()
            }
            is ReceivingProductFromBranchEvent.OnCarTypeTextSearchChange ->
                _uiState.update { it.copy(carTypeSearchText = event.searchText, carTypeError = "") }
            is ReceivingProductFromBranchEvent.OnContNoEntered ->
                _uiState.update { it.copy(contNo = event.contNo, contNoError = "") }
            is ReceivingProductFromBranchEvent.OnLoadingVendorSelected ->
                _uiState.update { it.copy(selectedLoadingVendor = event.loadingVendor, loadingVendorError = "") }
            is ReceivingProductFromBranchEvent.OnLoadingVendorTextSearchChange ->
                _uiState.update { it.copy(loadingVendorSearchText = event.searchText, loadingVendorError = "") }
            is ReceivingProductFromBranchEvent.OnNaolonDescriptionSelected -> {
                _uiState.update {
                    it.copy(
                        naolonLoadStatus = NaolonLoadingStatus.LOADING,
                        selectedNaolonDescription = event.naolonDescription,
                    )
                }
                calculateNaolon()
            }
            is ReceivingProductFromBranchEvent.OnNaolonDescriptionTextSearchChange ->
                _uiState.update { it.copy(naolonDescriptionSearchText = event.searchText) }
            is ReceivingProductFromBranchEvent.OnUnloadingPointEntered ->
                _uiState.update { it.copy(unloadingPoint = event.unloadingPoint, unloadingPointError = "") }
            is ReceivingProductFromBranchEvent.OnWorkerGroupSelected ->
                _uiState.update { it.copy(selectedWorkerGroup = event.workerGroup, workerGroupError = "") }
            is ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange ->
                _uiState.update { it.copy(workerGroupSearchText = event.searchText, workerGroupError = "") }
            is ReceivingProductFromBranchEvent.OnPackingTypeSelected ->
                _uiState.update {
                    it.copy(selectedPackingType = event.packingType, packingTypeError = "")
                }
            is ReceivingProductFromBranchEvent.OnPackingTypeTextSearchChange ->
                _uiState.update { it.copy(packingTypeSearchText = event.text, packingTypeError = "") }

            else -> {}
        }
    }

    private fun calculateNaolon() {
        val state = _uiState.value
        if (state.selectedCarType != null && state.selectedNaolonDescription != null) {
            viewModelScope.launch {
                val result = getNaolonPerTonValueUseCase(
                    state.selectedMaterial?.plantCode ?: "",
                    state.selectedNaolonDescription.naolonDescription ?: "",
                    state.selectedCarType.vehicleTypeName ?: ""
                )
                if (result is NetworkResult.Success) {
                    val naolonPerTon = result.data.naolonPerTon ?: 0f
                    val qty = _uiState.value.quantity.toDoubleOrNull() ?: 0.0
                    val totalNaolon = naolonPerTon.toDouble() * qty
                    _uiState.update {
                        it.copy(
                            naolonLoadStatus = NaolonLoadingStatus.SUCCESS(result.data.naolonPerTon.toString()),
                            naolon = totalNaolon.toString()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            naolonLoadStatus = NaolonLoadingStatus.ERROR(context.getString(R.string.error_in_calculating_naolon)),
                        )
                    }
                }
            }
        }
    }

    private fun loadAllNeededData() {
        loadWorkersGroup()
        loadCarNumbers()
        loadNaolonDescriptions()
        loadPackingTypes()
        loadLoadingVendors()
        loadCarTypes()
    }

    private fun loadCarTypes() {
        viewModelScope.launch {
            val result = getAllCarTypesUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(carTypes = result.data) }
            }
        }
    }

    private fun loadLoadingVendors() {
        viewModelScope.launch {
            val result = getAllLoadingVendorsUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(loadingVendors = result.data) }
            }
        }
    }

    private fun loadPackingTypes() {
        viewModelScope.launch {
            val result = getAllPackageTypesUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(packingTypes = result.data) }
            }
        }
    }

    private fun loadNaolonDescriptions() {
        viewModelScope.launch {
            val result = getAllNaolonDescriptionsUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(naolonDescriptions = result.data) }
            }
        }
    }

    private fun loadCarNumbers() {
        viewModelScope.launch {
            val result = getAllCarNumbersUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(carNos = result.data) }
            }
        }
    }

    private fun loadWorkersGroup() {
        viewModelScope.launch {
            val result = getWorkersGroupUseCase()
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(workerGroups = result.data) }
            }
        }
    }

    private fun getPOData(poNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(purchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.LOADING) }
            val result = getTransitOrderMaterialPurchaseOrderDataUseCase(poNumber)
            when (result) {
                is NetworkResult.Success -> {
                    val po = result.data
                    _uiState.update {
                        it.copy(
                            purchaseOrder = po,
                            materials = po.purchaseOrderItems,
                            filteredMaterials = po.purchaseOrderItems,
                            purchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.SUCCESS(po)
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
                            purchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.ERROR(message)
                        )
                    }
                }
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
                    quantity = material.remainingQuantity.toString(),
                    quantityError = ""
                )
            }
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
        val carNo = state.selectedCarNo
        val workerGroup = state.selectedWorkerGroup
        val loadingVendor = state.selectedLoadingVendor
        val packingType = state.selectedPackingType
        val carType = state.selectedCarType
        val naolonDesc = state.selectedNaolonDescription
        val unloadingPoint = state.unloadingPoint
        val contNo = state.contNo

        if (mat == null) return

        var hasError = false

        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
            hasError = true
        } else if (qty > (mat.remainingQuantity ?: 0.0)) {
            _uiState.update { it.copy(quantityError = context.getString(R.string.quantity_cannot_exceed_remaining_quantity, mat.remainingQuantity.toString())) }
            hasError = true
        }

        if (wh == null) {
            _uiState.update { it.copy(warehouseError = context.getString(R.string.please_select_a_warehouse)) }
            hasError = true
        }

        if (carNo == null) {
            _uiState.update { it.copy(carNoError = context.getString(R.string.please_enter_car_number)) }
            hasError = true
        }

        if (workerGroup == null) {
            _uiState.update { it.copy(workerGroupError = context.getString(R.string.please_select_workers_group)) }
            hasError = true
        }

        if (loadingVendor == null) {
            _uiState.update { it.copy(loadingVendorError = context.getString(R.string.select_loading_vendor)) }
            hasError = true
        }

        if (packingType == null) {
            _uiState.update { it.copy(packingTypeError = context.getString(R.string.select_packing_type)) }
            hasError = true
        }

        if (carType == null) {
            _uiState.update { it.copy(carTypeError = context.getString(R.string.select_car_type)) }
            hasError = true
        }

        if (naolonDesc == null) {
            _uiState.update { it.copy(naolonError = context.getString(R.string.select_naolon_description)) }
            hasError = true
        }

        if (unloadingPoint.isBlank()) {
            _uiState.update { it.copy(unloadingPointError = context.getString(R.string.please_enter_unloading_point)) }
            hasError = true
        }

        if (contNo.isBlank()) {
            _uiState.update { it.copy(contNoError = context.getString(R.string.enter_cont_no)) }
            hasError = true
        }

        if (hasError) return

        val alreadyAdded = state.addedItems.any { it.purchaseOrder == state.purchaseOrder?.poNumber && it.purchaseOrderItem == mat.poItemNumber }
        if (alreadyAdded) {
            _uiState.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
            return
        }

        val line = TransitOrderMaterialLine(
            materialCode = mat.materialCode ?: "",
            materialDescription = mat.materialDescription ?: "",
            plantCode = mat.plantCode ?: "",
            warehouseCode = wh!!.warehouseCode,
            purchaseOrder = state.purchaseOrder?.poNumber ?: "",
            purchaseOrderItem = mat.poItemNumber,
            uom = mat.uom ?: "",
            receivedQuantity = qty,
            carNo = carNo!!.carNumber,
            unloadingPoint = unloadingPoint,
            naolon = state.naolon.toDoubleOrNull(),
            contNo = contNo,
            loadingVendorCode = loadingVendor!!.vendorCode,
            wrokerGroupCode = workerGroup!!.workerGroupCode,
            naolonDescription = naolonDesc!!.naolonDescription,
            packTypeCode = packingType!!.packTypeCode,
            carType = carType!!.vehicleTypeName
        )

        _uiState.update {
            it.copy(
                addedItems = it.addedItems + line,
                selectedMaterial = null,
                quantity = "",
                selectedWarehouse = null,
                selectedCarNo = null,
                materialCodeInput = "",
                selectedWorkerGroup = null,
                selectedLoadingVendor = null,
                selectedPackingType = null,
                selectedCarType = null,
                selectedNaolonDescription = null,
                unloadingPoint = "",
                contNo = "",
                naolon = "",
                naolonLoadStatus = NaolonLoadingStatus.IDLE,
                warehouseSearchText = "",
                carNoSearchText = "",
                workerGroupSearchText = "",
                loadingVendorSearchText = "",
                packingTypeSearchText = "",
                carTypeSearchText = "",
                naolonDescriptionSearchText = ""
            )
        }
    }

    private fun saveReceiving() {
        val state = _uiState.value
        if (state.addedItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingLoading = true) }

            val result = receiveTransitOrderMaterialUseCase(
                ReceiveTransitOrderMaterialDto(
                    poNumber = state.purchaseOrder?.poNumber ?: "",
                    lines = state.addedItems.map { it.toDto() }
                )
            )

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
