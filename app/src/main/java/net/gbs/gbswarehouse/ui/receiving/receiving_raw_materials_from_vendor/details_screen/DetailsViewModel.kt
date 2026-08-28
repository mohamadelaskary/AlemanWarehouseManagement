package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.RawMaterialsLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveRawMaterialsDto
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
import android.content.Context
import android.util.Log
import android.util.Log.d
import dagger.hilt.android.qualifiers.ApplicationContext
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.transportationTypes
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
                    _state.update { it.copy(materialCodeError = context.getString(R.string.material_not_found)) }
                }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnClearMaterial -> {
                _state.update { it.copy(selectedMaterial = null, quantity = "", quantityError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSearchTextChanged -> {
                _state.update { it.copy(warehouseSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSelected -> {
                _state.update { it.copy(selectedWarehouse = event.warehouse, warehouseError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSearchTextChanged -> {
                _state.update { it.copy(carNumberSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSelected -> {
                _state.update { it.copy(selectedCarNumber = event.carNumber, carNumberError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnUnloadingPointChanged -> {
                _state.update { it.copy(unloadingPoint = event.text, unloadingPointError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSearchTextChanged -> {
                _state.update { it.copy(workerGroupSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSelected -> {
                _state.update { it.copy(selectedWorkerGroup = event.workerGroup, workerGroupError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSearchTextChanged -> {
                _state.update { it.copy(naolonDescriptionSearchText = event.text) }

            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSelected -> {
                _state.update { it.copy(selectedNaolonDescription = event.naolonDescription, naolonDescriptionError = "") }
                fetchNaolonValue()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSearchTextChanged -> {
                _state.update { it.copy(carTypeSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSelected -> {
                _state.update { it.copy(selectedCarType = event.carType, carTypeError = "") }
                fetchNaolonValue()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnContNoChanged -> {
                _state.update { it.copy(contNo = event.text, contNoError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSearchTextChanged -> {
                _state.update { it.copy(loadingVendorSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSelected -> {
                _state.update { it.copy(selectedLoadingVendor = event.loadingVendor, loadingVendorError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSearchTextChanged -> {
                _state.update { it.copy(packageTypeSearchText = event.text) }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSelected -> {
                _state.update { it.copy(selectedPackageType = event.packageType, packageTypeError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnQuantityChanged -> {
                _state.update { it.copy(quantity = event.text, quantityError = "") }
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnAddItem -> {
                addItem()
            }
            is ReceivingRawMaterialsFromVendorDetailsEvent.OnRemoveItem -> {
                _state.update {
                    it.copy(addedItems = it.addedItems.toMutableList().apply { removeAt(event.index) })
                }
            }
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
            _state.update { it.copy(materialCodeError = context.getString(R.string.material_already_added)) }
            return
        }
        val emanTransportationType = _state.value.loadingVendors.find { it.vendorCode == "11800005" }
        _state.update { it.copy(
            selectedMaterial = material,
            materialSearchText = "",
            filteredMaterials = emptyList(),
            quantity = material.remainingQuantity?.toString() ?: "",
            materialCodeError = "",
            selectedCarNumber = material.carNo?:"",
            selectedLoadingVendor = emanTransportationType
        ) }
        loadWarehouses(material.plantCode ?: "")
        fetchNaolonValue()
    }
    fun clearMaterial(){
        _state.update {
            it.copy(
                selectedMaterial = null,
                quantity = "",
                quantityError = "",
                materialCodeError = "",
                selectedCarNumber = null,
                selectedWarehouse = null,
                warehouseSearchText = "",
                warehouseError = "",
                workerGroupError = "",
                selectedWorkerGroup = null,
                workerGroupSearchText = "",
                loadingVendorError = "",
                selectedLoadingVendor = null,
                loadingVendorSearchText = "",
                packageTypeError = "",
                selectedPackageType = null,
                packageTypeSearchText = "",
                naolonDescriptionError = "",
                selectedNaolonDescription = null,
                naolonDescriptionSearchText = "",
                carTypeError = "",
                selectedCarType = null,
                carTypeSearchText = "",
                contNoError = "",
                unloadingPoint = "",
                unloadingPointError = "",
                naolonValue = null,
                naolonLoadingStatus = NaolonLoadingStatus.IDLE,


            )
        }
    }

    private fun loadOrder(poNumber: String) {
        viewModelScope.launch {
            _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.LOADING) }
            when (val result = getRawMaterialsPurchaseOrderDataUseCase(poNumber)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(purchaseOrder = result.data, getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.SUCCESS(result.data)) }
                }
                is NetworkResult.HttpError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(result.message)) }
                is NetworkResult.NetworkError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(result.message)) }
                is NetworkResult.BusinessError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(result.message)) }
                is NetworkResult.UnknownError -> _state.update { it.copy(getDataStatus = RawMaterialsPurchaseOrderGetDataStatus.ERROR(result.message)) }
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
                        _state.update { it.copy(naolonValue = "0", naolonLoadingStatus = NaolonLoadingStatus.ERROR(context.getString(R.string.failed_to_load))) }
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
            _state.update { it.copy(warehouseError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedCarNumber == null) {
            _state.update { it.copy(carNumberError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.unloadingPoint.isEmpty()) {
            _state.update { it.copy(unloadingPointError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedWorkerGroup == null) {
            _state.update { it.copy(workerGroupError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.transportationVendor==transportationTypes.first()&&st.selectedNaolonDescription == null) {
            _state.update { it.copy(naolonDescriptionError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedCarType == null) {
            _state.update { it.copy(carTypeError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.contNo.isEmpty()) {
            _state.update { it.copy(contNoError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedLoadingVendor == null) {
            _state.update { it.copy(loadingVendorError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.selectedPackageType == null) {
            _state.update { it.copy(packageTypeError = context.getString(R.string.required_field)) }
            hasError = true
        }
        if (st.quantity.isEmpty() || (st.quantity.toDoubleOrNull() ?: 0.0) <= 0.0) {
            _state.update { it.copy(quantityError = context.getString(R.string.please_enter_a_valid_quantity)) }
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
                packTypeCode = st.selectedPackageType?.packTypeCode
            )
            _state.update { it.copy(addedItems = it.addedItems + newItem) }
            clearMaterial()
        }
    }

    private fun saveReceiving() {
        val st = _state.value
        if (st.addedItems.isEmpty() || st.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val request = ReceiveRawMaterialsDto(
                poNumber = st.purchaseOrder?.poNumber ?: "",
                lines = st.addedItems
            )
            when (val result = receiveRawMaterialsUseCase(request)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isSaving = false, saveSuccess = true, showSuccessDialog = true, addedItems = emptyList()) }
                }
                else -> {
                    _state.update { it.copy(isSaving = false, saveError = context.getString(R.string.failed_to_save), showErrorDialog = true) }
                }
            }
        }
    }
}
