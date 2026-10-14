package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import android.util.Log.d
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
import net.gbs.gbswarehouse.data.remote.dto.response.RawMaterialsLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveRawMaterialsDto
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllCarNumbersUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllCarTypesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllLoadingVendorsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllNaolonDescriptionsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllPackageTypesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetNaolonPerTonValueUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.GetRawMaterialsPurchaseOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.ReceiveRawMaterialsUseCase
import net.gbs.gbswarehouse.formatQty
import net.gbs.gbswarehouse.spinnerLabel
import net.gbs.gbswarehouse.transportationTypes
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen.RawMaterialsPurchaseOrderGetDataStatus.*
import javax.inject.Inject

private const val TAG = "RawMaterialsDetailsViewModel"

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getRawMaterialsPurchaseOrderDataUseCase: GetRawMaterialsPurchaseOrderDataUseCase,
    private val receiveRawMaterialsUseCase: ReceiveRawMaterialsUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getAllCarNumbersUseCase: GetAllCarNumbersUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val getAllNaolonDescriptionsUseCase: GetAllNaolonDescriptionsUseCase,
    private val getAllCarTypesUseCase: GetAllCarTypesUseCase,
    private val getAllLoadingVendorsUseCase: GetAllLoadingVendorsUseCase,
    private val getAllPackageTypesUseCase: GetAllPackageTypesUseCase,
    private val getNaolonPerTonValueUseCase: GetNaolonPerTonValueUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReceivingRawMaterialsFromVendorDetailsState())
    val state: StateFlow<ReceivingRawMaterialsFromVendorDetailsState> = _state.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialCodeScanned(barcode))
    }

    fun onEvent(event: ReceivingRawMaterialsFromVendorDetailsEvent) {
        when (event) {
            is ReceivingRawMaterialsFromVendorDetailsEvent.LoadOrder -> {
                loadOrder(event.poNumber)
                loadInitialData()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialSearchTextChanged -> {
                _state.update { state ->
                    val filtered = if (event.text.isEmpty()) {
                        state.purchaseOrder?.purchaseOrderItems ?: emptyList()
                    } else {
                        state.purchaseOrder?.purchaseOrderItems?.filter {
                            it.materialCode?.contains(event.text, ignoreCase = true) ?: false ||
                                    it.materialDescription?.contains(event.text, ignoreCase = true) ?: false
                        } ?: emptyList()
                    }
                    state.copy(materialSearchText = event.text, filteredMaterials = filtered)
                }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialSelected -> {
                selectMaterial(event.material)
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialCodeScanned -> {
                val material = _state.value.purchaseOrder?.purchaseOrderItems?.find { it.materialCode == event.barcode }
                if (material != null) {
                    selectMaterial(material)
                } else {
                    _state.update { it.copy(materialCodeError = Message.Resource(R.string.material_not_found)) }
                }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnClearMaterial -> {
                _state.update { it.copy(selectedMaterial = null, quantity = "", quantityError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSearchTextChanged -> {
                _state.update { it.copy(warehouseSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSelected -> {
                _state.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSearchTextChanged -> {
                _state.update { it.copy(carNumberSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSelected -> {
                _state.update { it.copy(selectedCarNumber = event.carNumber, carNumberError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnUnloadingPointChanged -> {
                _state.update { it.copy(unloadingPoint = event.text, unloadingPointError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSearchTextChanged -> {
                _state.update { it.copy(workerGroupSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSelected -> {
                _state.update { it.copy(selectedWorkerGroup = event.workerGroup, workerGroupError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSearchTextChanged -> {
                _state.update { it.copy(naolonDescriptionSearchText = event.text) }

            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSelected -> {
                _state.update { it.copy(selectedNaolonDescription = event.naolonDescription, naolonDescriptionError = null) }
                fetchNaolonValue()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSearchTextChanged -> {
                _state.update { it.copy(carTypeSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSelected -> {
                _state.update { it.copy(selectedCarType = event.carType, carTypeError = null) }
                fetchNaolonValue()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnContNoChanged -> {
                _state.update { it.copy(contNo = event.text, contNoError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSearchTextChanged -> {
                _state.update { it.copy(loadingVendorSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSelected -> {
                _state.update { it.copy(selectedLoadingVendor = event.loadingVendor, loadingVendorError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSearchTextChanged -> {
                _state.update { it.copy(packageTypeSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSelected -> {
                _state.update { it.copy(selectedPackageType = event.packageType, packageTypeError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnQuantityChanged -> {
                _state.update { it.copy(quantity = event.text, quantityError = null) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnAddItem -> {
                addItem()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnRemoveItem -> {
                _state.update {
                    it.copy(addedItems = it.addedItems.toMutableList().apply { removeAt(event.index) })
                }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnEditItem -> editItem(event.index)
            ReceivingRawMaterialsFromVendorDetailsEvent.OnSave -> {
                saveReceiving()
            }
            ReceivingRawMaterialsFromVendorDetailsEvent.OnDismissDialogs -> {
                _state.update { it.copy(showSuccessDialog = false, showErrorDialog = false) }
            }

            is ReceivingRawMaterialsFromVendorDetailsEvent.OnTransportationVendorChanged -> {
                val emanTransportationType = _state.value.loadingVendors.find { it.vendorCode == "11800021" }
                _state.update {
                    it.copy(
                        transportationVendor = event.transportationType,
                        naolonValue = null,
                        selectedLoadingVendor = if (event.transportationType == transportationTypes.first()) emanTransportationType else null
                    )
                }
                d(TAG, "onEvent: ${_state.value.selectedLoadingVendor}")
            }
        }
    }

    private fun selectMaterial(material: RawMaterialsPurchaseOrderItem) {
        val alreadyAdded = _state.value.addedItems.any { it.purchaseOrderItem == material.poItemNumber }
        if (alreadyAdded) {
            _state.update { it.copy(materialCodeError = Message.Resource(R.string.material_already_added)) }
            return
        }
        setTransportingType(material.isFactoryCar)
        val emanTransportationType = _state.value.loadingVendors.find { it.vendorCode == "11800005" }

        _state.update { it.copy(
            selectedMaterial = material,
            materialSearchText = "",
            filteredMaterials = emptyList(),
            quantity = material.remainingQuantity?.toString() ?: "",
            materialCodeError = null,
            selectedCarNumber = material.carNo?:"",
            selectedLoadingVendor = emanTransportationType
        ) }
        loadWarehouses(material.plantCode ?: "")
        fetchNaolonValue()
    }

    private fun setTransportingType(
        factoryCar: Boolean?
    ) {
        if (factoryCar == true)
            _state.update {
                it.copy(
                    transportationVendor = transportationTypes.first()
                )
            }
        else
            _state.update {
                it.copy(
                    transportationVendor = transportationTypes[1]
                )
            }
    }

    fun clearMaterial(){
        _state.update {
            it.copy(
                selectedMaterial = null,
                quantity = "",
                quantityError = null,
                materialCodeError = null,
                selectedCarNumber = null,
                selectedWarehouse = null,
                warehouseSearchText = "",
                warehouseError = null,
                workerGroupError = null,
                selectedWorkerGroup = null,
                workerGroupSearchText = "",
                loadingVendorError = null,
                selectedLoadingVendor = null,
                loadingVendorSearchText = "",
                packageTypeError = null,
                selectedPackageType = null,
                packageTypeSearchText = "",
                naolonDescriptionError = null,
                selectedNaolonDescription = null,
                naolonDescriptionSearchText = "",
                carTypeError = null,
                selectedCarType = null,
                carTypeSearchText = "",
                contNoError = null,
                unloadingPoint = "",
                unloadingPointError = null,
                naolonValue = null,
                naolonLoadingStatus = NaolonLoadingStatus.IDLE,
                contNo = "",

            )
        }
    }

    private fun loadOrder(poNumber: String) {
        viewModelScope.launch {
            _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.LOADING) }
            when (val result = getRawMaterialsPurchaseOrderDataUseCase(poNumber)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(purchaseOrder = result.data, getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.SUCCESS(result.data, Message.real(result.message))) }
                }
                is NetworkResult.HttpError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(Message.Real(result.message))) }
                is NetworkResult.NetworkError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(Message.Real(result.message))) }
                is NetworkResult.BusinessError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(Message.Real(result.message))) }
                is NetworkResult.UnknownError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(Message.Real(result.message))) }
                else -> {}
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val carNumbersRes = getAllCarNumbersUseCase()
            if (carNumbersRes is NetworkResult.Success) _state.update { it.copy(carNumbers = carNumbersRes.data) }

            val workerGroupsRes = getAllWorkerGroupsUseCase()
            if (workerGroupsRes is NetworkResult.Success) _state.update { it.copy(workerGroups = workerGroupsRes.data) }

            val naolonDescRes = getAllNaolonDescriptionsUseCase()
            if (naolonDescRes is NetworkResult.Success) _state.update { it.copy(naolonDescriptions = naolonDescRes.data) }

            val carTypesRes = getAllCarTypesUseCase()
            if (carTypesRes is NetworkResult.Success) _state.update { it.copy(carTypes = carTypesRes.data) }

            val loadingVendorsRes = getAllLoadingVendorsUseCase()
            if (loadingVendorsRes is NetworkResult.Success) _state.update { it.copy(loadingVendors = loadingVendorsRes.data) }

            val packageTypesRes = getAllPackageTypesUseCase()
            if (packageTypesRes is NetworkResult.Success) _state.update { it.copy(packageTypes = packageTypesRes.data) }
        }
    }

    private fun loadWarehouses(plantCode: String) {
        viewModelScope.launch {
            val result = getPlantWarehousesUseCase(plantCode)
            if (result is NetworkResult.Success) {
                _state.update { it.copy(warehouses = result.data) }
            }
        }
    }

    private fun fetchNaolonValue() {
        val st = _state.value
        val plantCode = st.selectedMaterial?.plantCode
        val naolonDesc = st.selectedNaolonDescription?.naolonDescription
        val carType = st.selectedCarType?.vehicleTypeName

        if (plantCode != null && naolonDesc != null && carType != null) {
            viewModelScope.launch {
                _state.update { it.copy(naolonLoadingStatus = NaolonLoadingStatus.LOADING) }
                when (val result = getNaolonPerTonValueUseCase(plantCode, naolonDesc, carType)) {
                    is NetworkResult.Success -> {
                        val value = result.data.naolonPerTon ?: 0
                        _state.update { it.copy(naolonValue = value.toString(), naolonLoadingStatus = NaolonLoadingStatus.SUCCESS(value)) }
                    }
                    else -> {
                        _state.update { it.copy(naolonValue = "0", naolonLoadingStatus = NaolonLoadingStatus.ERROR(Message.Resource(R.string.failed_to_load))) }
                    }
                }
            }
        }
    }

    private fun addItem() {
        val st = _state.value
        if (st.isSaving) return
        var hasError = false

        if (st.selectedWarehouse == null) {
            _state.update { it.copy(warehouseError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedCarNumber == null) {
            _state.update { it.copy(carNumberError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
        if (st.unloadingPoint.isEmpty()) {
            _state.update { it.copy(unloadingPointError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedWorkerGroup == null) {
            _state.update { it.copy(workerGroupError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
//        if (st.transportationVendor==transportationTypes.first()&&st.selectedNaolonDescription == null) {
//            _state.update { it.copy(naolonDescriptionError = context.getString(R.string.required_field)) }
//            hasError = true
//        }
        if (st.selectedCarType == null) {
            _state.update { it.copy(carTypeError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
//        if (st.contNo.isEmpty()) {
//            _state.update { it.copy(contNoError = context.getString(R.string.required_field)) }
//            hasError = true
//        }
        if (st.selectedLoadingVendor == null) {
            _state.update { it.copy(loadingVendorError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedPackageType == null) {
            _state.update { it.copy(packageTypeError = Message.Resource(R.string.required_field)) }
            hasError = true
        }
        if (st.quantity.isEmpty() || (st.quantity.toDoubleOrNull() ?: 0.0) <= 0.0) {
            _state.update { it.copy(quantityError = Message.Resource(R.string.please_enter_a_valid_quantity)) }
            hasError = true
        }



        if (!hasError && st.selectedMaterial != null) {
            val newItem = RawMaterialsLineDto(
                materialCode = st.selectedMaterial.materialCode ?: "",
                plantCode = st.selectedMaterial.plantCode ?: "",
                warehouseCode = st.selectedWarehouse?.warehouseCode ?: "",
                purchaseOrder = st.purchaseOrder?.poNumber ?: "",
                purchaseOrderItem = st.selectedMaterial.poItemNumber,
                uom = st.selectedMaterial.uom ?: "",
                receivedQuantity = st.quantity.toDoubleOrNull() ?: 0.0,
                carNo = st.selectedCarNumber,
                unloadingPoint = st.unloadingPoint,
                naolon = st.naolonValue?.toDoubleOrNull(),
                contNo = st.contNo,
                loadingVendorCode = st.selectedLoadingVendor?.vendorCode,
                wrokerGroupCode = st.selectedWorkerGroup?.workerGroupCode,
                naolonDescription = st.selectedNaolonDescription?.naolonDescription,
                packTypeCode = st.selectedPackageType?.packTypeCode,
                carType = st.selectedCarType?.vehicleTypeName
            )
            _state.update { it.copy(addedItems = it.addedItems + newItem) }
            clearMaterial()
        }
    }

    private fun saveReceiving() {
        val st = _state.value
        if (st.addedItems.isEmpty() || st.isSaving) return
        if (st.addedItems.size!=st.purchaseOrder?.purchaseOrderItems?.size){
            _state.update {
                it.copy(
                    showErrorDialog = true,
                    saveErrorMessage = Message.Resource(R.string.please_add_all_car_items_to_save_receiving)
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = ReceiveRawMaterialsDto(
                poNumber = st.purchaseOrder.poNumber,
                lines = st.addedItems
            )
            when (val result = receiveRawMaterialsUseCase(request)) {
                is NetworkResult.Success -> {
                    val updatedOrder = result.data.purchaseOrder
                    if (updatedOrder != null) {
                        val filtered = if (st.materialSearchText.isEmpty()) {
                            updatedOrder.purchaseOrderItems
                        } else {
                            updatedOrder.purchaseOrderItems.filter {
                                it.materialCode?.contains(st.materialSearchText, ignoreCase = true) == true ||
                                        it.materialDescription?.contains(st.materialSearchText, ignoreCase = true) == true
                            }
                        }
                        _state.update {
                            it.copy(
                                purchaseOrder = updatedOrder,
                                filteredMaterials = filtered,
                                getDataStatus = SUCCESS(updatedOrder, Message.real(result.message))
                            )
                        }
                    }
                    clearMaterial()
                    _state.update { 
                        it.copy(
                            isSaving = false, 
                            saveSuccess = true, 
                            saveSuccessMessage = Message.real(result.message), 
                            showSuccessDialog = true, 
                            addedItems = emptyList()
                        ) 
                    }
                }
                is NetworkResult.HttpError -> {
                    _state.update { it.copy(isSaving = false, saveErrorMessage = Message.real(result.message), showErrorDialog = true) }
                }

                is NetworkResult.BusinessError -> _state.update { it.copy(isSaving = false, saveErrorMessage = Message.real(result.message), showErrorDialog = true) }
                NetworkResult.Loading -> {_state.update { it.copy(isSaving = true) }}
                is NetworkResult.NetworkError -> _state.update { it.copy(isSaving = false, saveErrorMessage = Message.real(result.message), showErrorDialog = true) }
                is NetworkResult.UnknownError -> _state.update { it.copy(isSaving = false, saveErrorMessage = Message.real(result.message), showErrorDialog = true) }
            }
        }
    }

    private fun editItem(index: Int) {
        val state = _state.value
        val item = state.addedItems.getOrNull(index) ?: return
        val newList = state.addedItems.toMutableList().apply { removeAt(index) }
        val items = state.purchaseOrder?.purchaseOrderItems.orEmpty()
        val material = items.find { it.materialCode == item.materialCode && it.poItemNumber == item.purchaseOrderItem }
            ?: items.find { it.materialCode == item.materialCode }
        val warehouse = state.warehouses.find { it.warehouseCode == item.warehouseCode }
        val carNumber = state.carNumbers.find { it.carNumber == item.carNo }
        val workerGroup = state.workerGroups.find { it.workerGroupCode == item.wrokerGroupCode }
        val naolonDescription = state.naolonDescriptions.find { it.naolonDescription == item.naolonDescription }
        val loadingVendor = state.loadingVendors.find { it.vendorCode == item.loadingVendorCode }
        val packageType = state.packageTypes.find { it.packTypeCode == item.packTypeCode }
        val carType = state.carTypes.find {
            it.vehicleTypeName == item.carType || it.description == item.carType
        }
        _state.update {
            it.copy(
                addedItems = newList,
                selectedMaterial = material,
                quantity = formatQty(item.receivedQuantity),
                selectedWarehouse = warehouse,
                warehouseSearchText = warehouse?.spinnerLabel().orEmpty(),
                selectedCarNumber = item.carNo,
                carNumberSearchText = carNumber?.spinnerLabel().orEmpty(),
                unloadingPoint = item.unloadingPoint.orEmpty(),
                selectedWorkerGroup = workerGroup,
                workerGroupSearchText = workerGroup?.spinnerLabel().orEmpty(),
                selectedNaolonDescription = naolonDescription,
                naolonDescriptionSearchText = naolonDescription?.spinnerLabel().orEmpty(),
                naolonValue = item.naolon?.let { q -> formatQty(q) },
                naolonLoadingStatus = NaolonLoadingStatus.SUCCESS(item.naolon?.toInt() ?: 0),
                contNo = item.contNo.orEmpty(),
                selectedLoadingVendor = loadingVendor,
                loadingVendorSearchText = loadingVendor?.spinnerLabel().orEmpty(),
                selectedPackageType = packageType,
                packageTypeSearchText = packageType?.spinnerLabel().orEmpty(),
                selectedCarType = carType,
                carTypeSearchText = carType?.spinnerLabel().orEmpty(),
                materialCodeError = null,
                quantityError = null,
                warehouseError = null,
                carNumberError = null,
                unloadingPointError = null,
                workerGroupError = null,
                naolonDescriptionError = null,
                loadingVendorError = null,
                packageTypeError = null,
                carTypeError = null
            )
        }
    }
}
