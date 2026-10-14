package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.AlEmanLoadingVendor
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveTransitOrderMaterialDto
import net.gbs.gbswarehouse.data.remote.mapper.toDto
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.Message
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
import net.gbs.gbswarehouse.transportationTypes
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonLoadingStatus
import javax.inject.Inject
import net.gbs.gbswarehouse.formatQty
import net.gbs.gbswarehouse.spinnerLabel

@HiltViewModel
class DetailsViewModel @Inject constructor(
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
                    getPOData(event.carNo)
                }
                loadAllNeededData()
            }
            is ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered -> {
                _uiState.update { it.copy(materialCodeInput = event.materialCode, materialCodeError = null) }
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
                _uiState.update { it.copy(quantity = event.quantity, quantityError = null) }
                val state = _uiState.value
                if (state.naolonLoadStatus is NaolonLoadingStatus.SUCCESS) {
                    val naolonPerTon = (state.naolonLoadStatus as NaolonLoadingStatus.SUCCESS).naolon.toDoubleOrNull() ?: 0.0
                    val qty = event.quantity.toDoubleOrNull() ?: 0.0
                    val totalNaolon = naolonPerTon * qty
                    _uiState.update { it.copy(naolon = totalNaolon.toString()) }
                }
            }
            is ReceivingProductFromBranchEvent.OnWarehouseSelected -> {
                _uiState.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = null) }
            }
            is ReceivingProductFromBranchEvent.OnCarNoSelected -> {
                _uiState.update { it.copy(selectedCarNo = event.carNo, carNoError = null) }
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
            is ReceivingProductFromBranchEvent.OnEditAddedMaterialButtonClicked -> {
                editAddedMaterial(event.index)
            }
            ReceivingProductFromBranchEvent.OnSaveButtonClicked -> {
                saveReceiving()
            }
            ReceivingProductFromBranchEvent.OnClearMaterialButtonClicked -> {
                _uiState.update { it.copy(selectedMaterial = null, quantity = "", selectedWarehouse = null, selectedCarNo = null, materialCodeInput = "", materialCodeError = null) }
            }
            ReceivingProductFromBranchEvent.HideSuccessDialog -> {
                _uiState.update { it.copy(showSuccessDialog = false) }
            }
            ReceivingProductFromBranchEvent.HideErrorDialog -> {
                _uiState.update { it.copy(showErrorDialog = false) }
            }
            is ReceivingProductFromBranchEvent.OnWarehouseSearchTextChange -> _uiState.update { it.copy(warehouseSearchText = event.searchText) }
            is ReceivingProductFromBranchEvent.OnCarNoTextSearchChange ->
                _uiState.update { it.copy(carNoSearchText = event.searchText, carNoError = null) }
            is ReceivingProductFromBranchEvent.OnCarTypeSelected -> {
                _uiState.update { it.copy(selectedCarType = event.carType, carTypeError = null, naolonLoadStatus = NaolonLoadingStatus.LOADING) }
                calculateNaolon()
            }
            is ReceivingProductFromBranchEvent.OnCarTypeTextSearchChange ->
                _uiState.update { it.copy(carTypeSearchText = event.searchText, carTypeError = null) }
            is ReceivingProductFromBranchEvent.OnProductionDateEntered ->
                _uiState.update { it.copy(productionDate = event.productionDate, productionDateError = null) }
            is ReceivingProductFromBranchEvent.OnLoadingVendorSelected ->
                _uiState.update { it.copy(selectedLoadingVendor = event.loadingVendor, loadingVendorError = null) }
            is ReceivingProductFromBranchEvent.OnLoadingVendorTextSearchChange ->
                _uiState.update { it.copy(loadingVendorSearchText = event.searchText, loadingVendorError = null) }
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
                _uiState.update { it.copy(unloadingPoint = event.unloadingPoint, unloadingPointError = null) }
            is ReceivingProductFromBranchEvent.OnWorkerGroupSelected ->
                _uiState.update { it.copy(selectedWorkerGroup = event.workerGroup, workerGroupError = null) }
            is ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange ->
                _uiState.update { it.copy(workerGroupSearchText = event.searchText, workerGroupError = null) }
            is ReceivingProductFromBranchEvent.OnPackingTypeSelected ->
                _uiState.update {
                    it.copy(selectedPackingType = event.packingType, packingTypeError = null)
                }
            is ReceivingProductFromBranchEvent.OnPackingTypeTextSearchChange ->
                _uiState.update { it.copy(packingTypeSearchText = event.text, packingTypeError = null) }

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
                    _uiState.update {
                        it.copy(
                            naolonLoadStatus = NaolonLoadingStatus.SUCCESS(result.data.naolonPerTon.toString()),
                            naolon = naolonPerTon.toString()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            naolonLoadStatus = NaolonLoadingStatus.ERROR(Message.Resource(R.string.error_in_calculating_naolon)),
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

    private fun getPOData(carNo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(purchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.LOADING) }
            val result = getTransitOrderMaterialPurchaseOrderDataUseCase(carNo)
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
                    val message: Message = when (result) {
                        is NetworkResult.BusinessError -> Message.Real(result.message)
                        is NetworkResult.HttpError -> Message.Real(result.message)
                        is NetworkResult.NetworkError -> Message.Real(result.message)
                        else -> Message.Real("Error loading PO data")
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
            val alreadyAdded = state.addedItems.any { it.purchaseOrder == (material.poNumber ?: state.purchaseOrder?.poNumber) && it.purchaseOrderItem == material.poItemNumber }
            if (alreadyAdded) {
                _uiState.update { it.copy(materialCodeError = Message.Resource(R.string.material_already_added)) }
                return
            }

            _uiState.update {
                it.copy(
                    selectedMaterial = material,
                    materialCodeInput = materialCode,
                    materialCodeError = null,
                    quantity = material.remainingQuantity.toString(),
                    quantityError = null,
                    selectedCarNo = material.carNo,
                    transportingType = if (material.isFactoryCar) transportationTypes.first() else transportationTypes[1],
                    selectedLoadingVendor = if (material.isFactoryCar) AlEmanLoadingVendor else null
                )
            }
            loadWarehouses(material.plantCode ?: "")
        } else {
            _uiState.update { it.copy(materialCodeError = Message.Resource(R.string.wrong_material_code)) }
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
        val productionDate = state.productionDate

        if (mat == null) return

        var hasError = false

        if (qty <= 0) {
            _uiState.update { it.copy(quantityError = Message.Resource(R.string.please_enter_a_valid_quantity)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: qty <=0")
        } else if (qty > (mat.remainingQuantity ?: 0.0)) {
            _uiState.update { it.copy(quantityError = Message.Resource(R.string.quantity_cannot_exceed_remaining_quantity, mat.remainingQuantity.toString())) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: qty > remaining qty")
        }

        if (wh == null) {
            _uiState.update { it.copy(warehouseError = Message.Resource(R.string.please_select_a_warehouse)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: warehouse null")
        }

//        if (carNo == null) {
//            _uiState.update { it.copy(carNoError = context.getString(R.string.please_enter_car_number)) }
//            hasError = true
//        }

        if (workerGroup == null) {
            _uiState.update { it.copy(workerGroupError = Message.Resource(R.string.please_select_workers_group)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: workersGroup null")
        }

        if (loadingVendor == null) {
            _uiState.update { it.copy(loadingVendorError = Message.Resource(R.string.select_loading_vendor)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: loading vendor null")
        }

        if (packingType == null) {
            _uiState.update { it.copy(packingTypeError = Message.Resource(R.string.select_packing_type)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: packing type null")
        }

        if (carType == null) {
            _uiState.update { it.copy(carTypeError = Message.Resource(R.string.select_car_type)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: car type null")
        }

        if (naolonDesc == null && state.selectedMaterial.isFactoryCar) {
            _uiState.update { it.copy(naolonError = Message.Resource(R.string.select_naolon_description)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: naolon desc null")
        }

        if (unloadingPoint.isBlank()) {
            _uiState.update { it.copy(unloadingPointError = Message.Resource(R.string.please_enter_unloading_point)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: unloading point null")
        }

        if (productionDate.isBlank()) {
            _uiState.update { it.copy(productionDateError = Message.Resource(R.string.please_select_production_date)) }
            hasError = true
            Log.d("ReceivingProductFromBranchViewModel", "addItem: production date null")
        }

        if (hasError) return

        val alreadyAdded = state.addedItems.any { it.purchaseOrder == (mat.poNumber ?: state.purchaseOrder?.poNumber) && it.purchaseOrderItem == mat.poItemNumber }
        if (alreadyAdded) {
            _uiState.update { it.copy(materialCodeError = Message.Resource(R.string.material_already_added)) }
            return
        }

        val line = TransitOrderMaterialLine(
            materialCode = mat.materialCode ?: "",
            materialDescription = mat.materialDescription ?: "",
            plantCode = mat.plantCode ?: "",
            warehouseCode = wh!!.warehouseCode,
            purchaseOrder = mat.poNumber ?: state.purchaseOrder?.poNumber ?: "",
            purchaseOrderItem = mat.poItemNumber,
            uom = mat.uom ?: "",
            receivedQuantity = qty,
            carNo = carNo!!,
            unloadingPoint = unloadingPoint,
            naolon = state.naolon.toDoubleOrNull(),
            contNo = productionDate,
            loadingVendorCode = loadingVendor!!.vendorCode,
            wrokerGroupCode = workerGroup!!.workerGroupCode,
            naolonDescription = naolonDesc?.naolonDescription,
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
                productionDate = "",
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
        if (state.addedItems.size!=state.materials.size){
            _uiState.update {
                it.copy(
                    showErrorDialog = true,
                    savingErrorStatusMessage = Message.Resource(R.string.please_add_all_car_items_to_save_receiving)
                )
            }
        }
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
                    val updatedPo = result.data.purchaseOrder
                    if (updatedPo != null) {
                        _uiState.update {
                            it.copy(
                                isSavingLoading = false,
                                showSuccessDialog = true,
                                savingSuccessStatusMessage = Message.of(result.message, R.string.success_save),
                                addedItems = emptyList(),
                                purchaseOrder = updatedPo,
                                materials = updatedPo.purchaseOrderItems,
                                filteredMaterials = if (it.materialSearch.isBlank()) {
                                    updatedPo.purchaseOrderItems
                                } else {
                                    updatedPo.purchaseOrderItems.filter { item ->
                                        item.materialCode?.contains(it.materialSearch, ignoreCase = true) == true ||
                                                item.materialDescription?.contains(it.materialSearch, ignoreCase = true) == true
                                    }
                                },
                                purchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.SUCCESS(updatedPo),
                                selectedMaterial = null,
                                materialCodeInput = "",
                                quantity = "",
                                selectedWarehouse = null,
                                selectedCarNo = null,
                                selectedWorkerGroup = null,
                                selectedLoadingVendor = null,
                                selectedPackingType = null,
                                selectedCarType = null,
                                selectedNaolonDescription = null,
                                unloadingPoint = "",
                                productionDate = "",
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
                    } else {
                        _uiState.update {
                            it.copy(
                                isSavingLoading = false,
                                showSuccessDialog = true,
                                savingSuccessStatusMessage = Message.of(result.message, R.string.success_save),
                                addedItems = emptyList()
                            )
                        }
                        state.purchaseOrder?.poNumber?.let { getPOData(it) }
                    }
                }
                else -> {
                    val message: Message = when (result) {
                        is NetworkResult.BusinessError -> Message.Real(result.message)
                        is NetworkResult.HttpError -> Message.Real(result.message)
                        is NetworkResult.NetworkError -> Message.Real(result.message)
                        else -> Message.Real("Error saving receiving")
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

    private fun editAddedMaterial(index: Int) {
        val state = _uiState.value
        val item = state.addedItems.getOrNull(index) ?: return
        val newList = state.addedItems.toMutableList().apply { removeAt(index) }
        val material = state.materials.find { it.materialCode == item.materialCode && it.poItemNumber == item.purchaseOrderItem }
            ?: state.materials.find { it.materialCode == item.materialCode }
        val warehouse = state.warehouses.find { it.warehouseCode == item.warehouseCode }
        val carNumber = state.carNos.find { it.carNumber == item.carNo }
        val workerGroup = state.workerGroups.find { it.workerGroupCode == item.wrokerGroupCode }
        val loadingVendor = state.loadingVendors.find { it.vendorCode == item.loadingVendorCode }
        val packingType = state.packingTypes.find { it.packTypeCode == item.packTypeCode }
        val naolonDescription = state.naolonDescriptions.find { it.naolonDescription == item.naolonDescription }
        val carType = state.carTypes.find { it.vehicleTypeName == item.carType || it.description == item.carType }
        _uiState.update {
            it.copy(
                addedItems = newList,
                selectedMaterial = material,
                materialCodeInput = item.materialCode,
                quantity = formatQty(item.receivedQuantity),
                selectedWarehouse = warehouse,
                warehouseSearchText = warehouse?.spinnerLabel().orEmpty(),
                selectedCarNo = item.carNo,
                carNoSearchText = carNumber?.spinnerLabel().orEmpty(),
                unloadingPoint = item.unloadingPoint.orEmpty(),
                naolon = item.naolon?.let { formatQty(it) }.orEmpty(),
                productionDate = item.contNo.orEmpty(),
                selectedWorkerGroup = workerGroup,
                workerGroupSearchText = workerGroup?.spinnerLabel().orEmpty(),
                selectedLoadingVendor = loadingVendor,
                loadingVendorSearchText = loadingVendor?.spinnerLabel().orEmpty(),
                selectedPackingType = packingType,
                packingTypeSearchText = packingType?.spinnerLabel().orEmpty(),
                selectedNaolonDescription = naolonDescription,
                naolonDescriptionSearchText = naolonDescription?.spinnerLabel().orEmpty(),
                selectedCarType = carType,
                carTypeSearchText = carType?.spinnerLabel().orEmpty(),
                naolonLoadStatus = NaolonLoadingStatus.SUCCESS(item.naolon?.let { formatQty(it) } ?: "0"),
                materialCodeError = null,
                quantityError = null,
                warehouseError = null,
                carNoError = null,
                workerGroupError = null,
                loadingVendorError = null,
                packingTypeError = null,
                unloadingPointError = null,
                productionDateError = null
            )
        }
    }
}
