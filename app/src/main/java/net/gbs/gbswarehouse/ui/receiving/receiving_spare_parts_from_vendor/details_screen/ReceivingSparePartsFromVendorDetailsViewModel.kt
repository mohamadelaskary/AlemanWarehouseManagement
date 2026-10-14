package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.BINS
import net.gbs.gbswarehouse.Bin
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.ORDERS
import net.gbs.gbswarehouse.OpType
import net.gbs.gbswarehouse.Operation
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.QualityDecision
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.mapper.toPurchaseOrder
import net.gbs.gbswarehouse.data.remote.mapper.toWarehouses
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.PurchaseOrderRequest
import net.gbs.gbswarehouse.domain.model.ReceiveVendorSparePartsRequest
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.GetReceivingSparePartsFromVendorPurchaseOrderDataUseCase
import net.gbs.gbswarehouse.domain.usecase.ReceiveVendorSparePartsUseCase
import net.gbs.gbswarehouse.formatQty
import net.gbs.gbswarehouse.spinnerLabel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.text.lowercase
@HiltViewModel
class ReceivingSparePartsFromVendorDetailsViewModel @Inject constructor(
    private val getReceivingSparePartsFromVendorPurchaseOrderDataUseCase: GetReceivingSparePartsFromVendorPurchaseOrderDataUseCase,
    private val getWarehousesUseCase: GetPlantWarehousesUseCase,
    private val receiveVendorSparePartsUseCase: ReceiveVendorSparePartsUseCase,
) : ViewModel() {


    private val pageName = "ReceiveVendorSpareParts"
    private val _uiState =
        MutableStateFlow(
            ReceivingSparePartsFromVendorState()
        )

    val uiState = _uiState.asStateFlow()
    fun onBarcodeScanned(barcode: String) {

        if (
            _uiState.value.materials.isNotEmpty()
        ){
            loadMaterial(barcode)
        }

    }
    fun onEvent(
        event: ReceivingSparePartsFromVendorEvent
    ) {

        when (event) {

            is ReceivingSparePartsFromVendorEvent.OnScreenLoaded ->
                getPoData(event.poData)

            ReceivingSparePartsFromVendorEvent.OnClearButtonClicked ->
                clear()

            is ReceivingSparePartsFromVendorEvent.OnMaterialSelected ->
                loadMaterial(event.materialCode)

            is ReceivingSparePartsFromVendorEvent.OnMaterialCodeInputEntered ->
                updateMaterialCodeInput(event.materialCode)

            is ReceivingSparePartsFromVendorEvent.OnWarehouseSelected ->
                selectWarehouse(event.warehouse)

            is ReceivingSparePartsFromVendorEvent.OnExpiryDateSelected ->
                updateExpiryDate(event.expiryDate)

            is ReceivingSparePartsFromVendorEvent.OnQuantityEntered ->
                updateQuantity(event.quantity.toString())

            ReceivingSparePartsFromVendorEvent.OnAddButtonClicked ->
                addItem()

            ReceivingSparePartsFromVendorEvent.OnSaveButtonClicked ->
                save()

            is ReceivingSparePartsFromVendorEvent.OnValuationTypeSelected -> updateValuationType(event.valuationType)
            ReceivingSparePartsFromVendorEvent.OnClearMaterialButtonClicked -> clearMaterial()
            ReceivingSparePartsFromVendorEvent.HideErrorDialog -> hideErrorDialog()
            ReceivingSparePartsFromVendorEvent.HideSuccessDialog -> hideSuccessDialog()
            ReceivingSparePartsFromVendorEvent.ShowErrorDialog -> showErrorDialog()
            ReceivingSparePartsFromVendorEvent.ShowSuccessDialog -> showSuccessDialog()
            is ReceivingSparePartsFromVendorEvent.OnMaterialSearchTextEntered -> {
                filteredMaterials(event.text)
            }

            is ReceivingSparePartsFromVendorEvent.OnDeleteAddedMaterialButtonClicked -> {
                val list = _uiState.value.addedItems.toMutableList()
                list.removeAt(event.index)
                _uiState.update {
                    it.copy(
                        addedItems = list
                    )
                }
            }
            is ReceivingSparePartsFromVendorEvent.OnEditAddedMaterialButtonClicked -> editAddedMaterial(event.index)

            is ReceivingSparePartsFromVendorEvent.OnWarehouseSearchTextChange ->
                _uiState.update {
                    it.copy(
                        warehouseSearchText = event.searchText
                    )
                }

            is ReceivingSparePartsFromVendorEvent.OnValuationSearchTextChange ->
                _uiState.update {
                    it.copy(
                        valuationSearchText = event.searchText
                    )
                }
        }

    }

    private fun updatePurchaseOrder(poData: String) {
        val purchaseOrder = PurchaseOrder.fromJson(poData)
        _uiState.update {
            it.copy(
                purchaseOrder = purchaseOrder,
                materials = purchaseOrder.purchaseOrderItems ?: emptyList(),
                filteredMaterials = purchaseOrder.purchaseOrderItems?:emptyList()
            )
        }
    }

    private fun filteredMaterials(text: String) {
        val filtered = if (text.isBlank()) {
            _uiState.value.materials
        } else {
            _uiState.value.materials.filter {
                it.materialCode.contains(text, ignoreCase = true) ||
                        it.materialDescription.contains(text, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                materialSearch = text,
                filteredMaterials = filtered
            )
        }
    }

    private fun showSuccessDialog() {
        _uiState.update {
            it.copy(
                showSuccessDialog = true
            )
        }
    }

    private fun showErrorDialog() {
        _uiState.update {
            it.copy(
                showErrorDialog = true
            )
        }
    }

    private fun hideErrorDialog() {
        _uiState.update {
            it.copy(
                showErrorDialog = false
            )
        }
    }

    private fun hideSuccessDialog() {
        _uiState.update {
            it.copy(
                showSuccessDialog = false
            )
        }
    }

    private fun updateValuationType(
        selectedValuationType: ValuationType
    ){
        _uiState.update{
            it.copy(
                selectedValuationType = selectedValuationType,
                valuationTypeError = null
            )
        }
    }
    private fun clear(){
        _uiState.update {
            it.copy(
                selectedMaterial = null,
                warehouses = emptyList(),
                selectedExpiryDate = null,
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                addedItems = emptyList(),
                error = null
            )
        }
    }
    private fun updateOrderNumber(
        number: String
    ) {

        _uiState.update {

            it.copy(
                orderNumber = number
            )

        }

    }

    private fun getPoData(orderNumber: String) {
        viewModelScope.launch {

            _uiState.update {

                it.copy(
                    purchaseOrderGetDataStatus = PurchaseOrderGetDataStatus.IDLE,
                    error = null
                )

            }

            when (
                val result =
                    getReceivingSparePartsFromVendorPurchaseOrderDataUseCase(
                        poNumber = orderNumber
                    )
            ) {
                NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            purchaseOrderGetDataStatus = PurchaseOrderGetDataStatus.LOADING
                        )
                    }
                }
                is NetworkResult.Success -> {
                    val purchaseOrder = result.data
                    _uiState.update {
                        it.copy(
                            purchaseOrderGetDataStatus = PurchaseOrderGetDataStatus.SUCCESS(purchaseOrder),
                            purchaseOrder = purchaseOrder,
                            materials = purchaseOrder.purchaseOrderItems,
                            filteredMaterials = purchaseOrder.purchaseOrderItems
                        )
                    }
                }

                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        is NetworkResult.UnknownError -> result.message
                    }
                    _uiState.update {
                        it.copy(
                            purchaseOrderGetDataStatus = PurchaseOrderGetDataStatus.ERROR(Message.Real(message)),
                            searchError = Message.Real(message)
                        )
                    }
                }

            }

        }

    }
    private fun updateMaterialCodeInput(code: String) {
        _uiState.update { it.copy(materialCodeInput = code) }
    }

    private fun loadMaterial(
        materialCode: String
    ) {
        val state = _uiState.value
        val selectedMaterial = state.materials.find { material -> material.materialCode == materialCode }
        if (selectedMaterial==null){
            _uiState.update {
                it.copy(
                    materialCodeInput = materialCode,
                    materialCodeError = Message.Resource(R.string.wrong_material_code)
                )
            }
            return
        }
        val alreadyAddedMaterial = state.addedItems.find { it.purchaseOrderItem == selectedMaterial.poItemNumber }
        if (alreadyAddedMaterial!=null){
            _uiState.update {
                it.copy(
                    materialCodeInput = materialCode,
                    materialCodeError = Message.Resource(R.string.material_already_added)
                )
            }
            return
        }
        viewModelScope.launch {

            _uiState.update {

                it.copy(
                    isWarehousesLoading = true,
                    materialCodeInput = materialCode,
                )

            }
            val warehouses =
                getWarehousesUseCase(selectedMaterial.plantCode)

            when (warehouses) {
                is NetworkResult.Loading -> _uiState.value.isWarehousesLoading
                is NetworkResult.Success -> {

                    _uiState.update {

                        it.copy(

                            isWarehousesLoading = false,

                            selectedMaterial = selectedMaterial,
                            quantity = selectedMaterial.remainingQuantity.toString(),
                            selectedExpiryDate = if (selectedMaterial.isBatchManaged) SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) else null,
                            warehouses = warehouses.data

                        )

                    }

                }

                else -> {
                    val message = when (warehouses) {
                        is NetworkResult.BusinessError -> warehouses.message
                        is NetworkResult.HttpError -> warehouses.message
                        is NetworkResult.NetworkError -> warehouses.message
                        is NetworkResult.UnknownError -> warehouses.message
                    }
                    _uiState.update {
                        it.copy(
                            isWarehousesLoading = false,
                            error = Message.Real(message)
                        )
                    }
                }

            }

        }

    }

    private fun updateQuantity (quantity:String){
        _uiState.update {
            it.copy(
                quantity = quantity,
                quantityError = null
            )
        }
    }

    private fun selectWarehouse (selectedWarehouse: Warehouse){
        _uiState.update {
            it.copy(
                selectedWarehouse = selectedWarehouse,
                warehouseError = null
            )
        }
    }

    private fun updateExpiryDate (expiryDate:String?) {
        _uiState.update {
            it.copy(selectedExpiryDate = expiryDate)
        }
    }

    private fun addItem (){
        Log.d("ReceivingSparePartsFromVendorDetailsViewModel", "addItem: Clicked")
        val state = _uiState.value
        val material = state.selectedMaterial ?: return

        var hasError = false
        val quantity = state.quantity
        if ((quantity.isEmpty()) || (quantity.toFloatOrNull() == null) || (quantity.toFloat() <= 0)) {
            _uiState.update { it.copy(quantityError = Message.Resource(R.string.please_enter_a_valid_quantity)) }
            hasError = true
        } else if (quantity.toFloat() > material.remainingQuantity) {
            _uiState.update { it.copy(quantityError = Message.Resource(
                R.string.quantity_cannot_exceed_remaining_quantity,
                material.remainingQuantity.toString()
            )) }
            hasError = true
        }
        if (material.isBatchManaged&&state.selectedExpiryDate==null){
            _uiState.update { it.copy(warehouseError = Message.Resource(R.string.please_select_expiry_date)) }
            hasError = true
        }

        if (state.selectedWarehouse == null) {
            _uiState.update { it.copy(warehouseError = Message.Resource(R.string.please_select_a_warehouse)) }
            hasError = true
        }

        if (material.valuationTypes.isNotEmpty() && state.selectedValuationType == null) {
            _uiState.update { it.copy(valuationTypeError = Message.Resource(R.string.please_select_a_valuation_type)) }
            hasError = true
        }

        if (hasError) return

        val poNumber = state.purchaseOrder?.poNumber ?: return
        val warehouse = state.selectedWarehouse!!
        val expiryDate = if (state.selectedExpiryDate==null) null else state.selectedExpiryDate+"T00:00:00.000Z"
        val valuationType =
            if (material.valuationTypes.isEmpty())
                ""
            else
                state.selectedValuationType!!.type
        Log.d("ReceivingSparePartsFromVendorDetailsViewModel", "addItem: ${state.quantity}")

        _uiState.update {
            it.copy(
                addedItems = it.addedItems.plus(
                    SparePartsLine(
                        materialCode = material.materialCode,
                        materialDescription = material.materialDescription,
                        plantCode = material.plantCode,
                        warehouseCode = warehouse.warehouseCode,
                        purchaseOrder = poNumber,
                        purchaseOrderItem = material.poItemNumber,
                        uom = material.uom,
                        receivedQuantity = if (quantity.isEmpty()) 0f else quantity.toFloat(),
                        valuationType = valuationType,
                        expiryDate = expiryDate,
                        isBatchManaged = material.isBatchManaged,
                    )
                )
            )
        }
        clearMaterial()
        Log.d("ReceivingSparePartsFromVendorDetailsViewModel", "addItem: ${_uiState.value.addedItems}")
    }

    private fun editAddedMaterial(index: Int) {
        val state = _uiState.value
        val item = state.addedItems.getOrNull(index) ?: return
        val list = state.addedItems.toMutableList().apply { removeAt(index) }
        val material = state.materials.find { it.materialCode == item.materialCode && it.poItemNumber == item.purchaseOrderItem }
            ?: state.materials.find { it.materialCode == item.materialCode }
        val warehouse = state.warehouses.find { it.warehouseCode == item.warehouseCode }
        val valuationType = material?.valuationTypes?.find { it.type == item.valuationType }
        _uiState.update {
            it.copy(
                addedItems = list,
                selectedMaterial = material,
                materialCodeInput = item.materialCode,
                quantity = formatQty(item.receivedQuantity),
                selectedWarehouse = warehouse,
                warehouseSearchText = warehouse?.spinnerLabel().orEmpty(),
                selectedValuationType = valuationType,
                valuationSearchText = valuationType?.spinnerLabel().orEmpty(),
                selectedExpiryDate = item.expiryDate?.take(10),
                materialCodeError = null,
                quantityError = null,
                warehouseError = null,
                valuationTypeError = null,
                isWarehousesLoading = false
            )
        }
    }

    private fun clearMaterial(){
        _uiState.update {
            it.copy(
                selectedMaterial = null,
                materialCodeInput = "",
                selectedExpiryDate = null,
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                quantityError = null,
                warehouseError = null,
                valuationTypeError = null,
                materialCodeError = null,
                warehouseSearchText = "",
                valuationSearchText = "",
            )
        }
    }
    private fun save(){
        val state = _uiState.value
        if (state.isSavingLoading) return
        val addedItems = state.addedItems
        if (addedItems.isEmpty()) {
            _uiState.update {
                it.copy(
                    savingErrorStatusMessage = Message.Resource(R.string.please_add_items_to_receive_first),
                    isSavingLoading = false
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showSuccessDialog = false,
                    showErrorDialog = false,
                    savingSuccessStatusMessage = null,
                    savingErrorStatusMessage = null,
                    isSavingLoading = true
                )
            }
            val result = receiveVendorSparePartsUseCase(
                ReceiveVendorSparePartsRequest(
                    poNumber = state.purchaseOrder?.poNumber ?: "",
                    lines = addedItems,
                )
            )
            when(result){
                is NetworkResult.Success -> {
                    showSuccessDialog()
                    _uiState.update {
                        it.copy(
                            showSuccessDialog = true,
                            showErrorDialog = false,
                            savingSuccessStatusMessage = Message.Real(result.message!!),
                            savingErrorStatusMessage = null,
                            isSavingLoading = false,
                        )
                    }
                    resetDetail(result.data)
                }
                else -> {
                    val message = when (result) {
                        is NetworkResult.BusinessError -> result.message
                        is NetworkResult.HttpError -> result.message
                        is NetworkResult.NetworkError -> result.message
                        is NetworkResult.UnknownError -> result.message
                        else -> {}
                    }
                    showErrorDialog()
                    _uiState.update {
                        it.copy(
                            showSuccessDialog = false,
                            showErrorDialog = true,
                            savingSuccessStatusMessage = null,
                            savingErrorStatusMessage = Message.Real(message.toString()),
                            isSavingLoading = false,
                        )
                    }
                }
            }
        }
    }

    private fun resetDetail(data: PurchaseOrder?) {
        _uiState.update {
            it.copy(
                purchaseOrder = data,
                materials = data?.purchaseOrderItems ?: emptyList(),
                selectedMaterial = null,
                materialCodeInput = "",
                warehouses = emptyList(),
                selectedExpiryDate = null,
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                addedItems = emptyList(),
                error = null,
                materialCodeError = null,
                searchError = null,
                quantityError = null,
                warehouseError = null,
                valuationTypeError = null,
                isWarehousesLoading = false,
                isSavingLoading = false,
            )
        }
    }

    var expiryDate by mutableStateOf("")
    var selectedValuationType by mutableStateOf<ValuationType?>(null)
    var selectedWarehouse by mutableStateOf<Warehouse?>(null)
    var qualityDecision by mutableStateOf<QualityDecision?>(null)
    var qualityDecider by mutableStateOf("")
    var screen    by mutableStateOf(Screens.Home)
    var mainCat   by mutableStateOf<String?>(null)       // "recv" | "issue"
    var selOp     by mutableStateOf<Operation?>(null)
    var selOpType by mutableStateOf<OpType?>(null)
    var selOrder  by mutableStateOf<Order?>(null)

    // list search
    var orderSearch by mutableStateOf("")

    // detail fields
    var matInput  by mutableStateOf("")
    var matDlg    by mutableStateOf(false)
    var matSearch by mutableStateOf("")
    var selMat    by mutableStateOf<SparePartsMaterial?>(null)
    var qty       by mutableStateOf("")
    var smpQty    by mutableStateOf("")

    var binInput  by mutableStateOf("")
    var binDlg    by mutableStateOf(false)
    var selBin    by mutableStateOf<Bin?>(null)

    // blind receive
    var bCode  by mutableStateOf("")
    var bDesc  by mutableStateOf("")
    var vendorName  by mutableStateOf("")
    var bQty   by mutableStateOf("")

    // save state
    var saved    by mutableStateOf(false)
    var saveKind by mutableStateOf<String?>(null)  // "save" | "accept" | "reject"
    var saving   by mutableStateOf(false)

    // ── Computed ─────────────────────────────────────────────────────────────
    val variant: String get() = when {
        mainCat == "issue"            -> "issue"
        selOpType?.id == "quality"    -> "quality"
        selOpType?.id == "putaway"    -> "putaway"
        else                          -> "normal"
    }

    val accent: Color get() =
        if (mainCat == "issue") ORANGE else selOpType?.accent ?: GREEN

    val filteredOrders: List<Order> get() {
        val list = ORDERS["PO"] ?: emptyList()
        return if (orderSearch.isBlank()) list
        else list.filter { it.id.contains(orderSearch) || it.info.contains(orderSearch) }
    }

    val filteredMaterials: List<SparePartsMaterial> get() {
        return if (matSearch.isBlank()) _uiState.value.materials
        else _uiState.value.materials.filter {
            it.materialCode.lowercase().contains(matSearch.lowercase()) || it.materialDescription.contains(matSearch)
        }
    }

    val filteredBins: List<Bin> get() {
        return if (binInput.isBlank()) BINS
        else BINS.filter {
            it.code.lowercase().contains(binInput.lowercase()) ||
                    it.zone.contains(binInput) || it.aisle.contains(binInput)
        }
    }

    val blindOk: Boolean get() = bCode.trim().isNotEmpty() && bDesc.trim().isNotEmpty() && vendorName.trim().isNotEmpty() && bQty.isNotEmpty()

    val showFixedSave: Boolean get() {
        if (variant == "quality" || saved) return false
        val isBlind = selOp?.blind == true
        return when {
            isBlind  -> blindOk
            variant == "putaway" -> selBin != null && qty.isNotEmpty()
            else -> selMat != null && qty.isNotEmpty()
        }
    }

    val saveLabel: Message get() = when (variant) {
        "putaway" -> Message.Real("⌘ ") + Message.Resource(R.string.confirm_putaway)
        "issue"   -> Message.Real("↑ ") + Message.Resource(R.string.save_issuing)
        else      -> Message.Real("↓ ") + Message.Resource(R.string.save_receiving)
    }

    // ── Navigation ─────────────────────────────────────────────────────────




    // ── Detail actions ──────────────────────────────────────────────────────
//    fun selectMaterial(mat: SparePartsMaterial) {
//        selMat = mat; matInput = mat.materialCode; matDlg = false; matSearch = ""
//    }


    fun selectBin(bin: Bin) { selBin = bin; binInput = bin.code; binDlg = false }

    fun clearBin() { selBin = null; binInput = ""; qty = "" }

    fun doSave(kind: String) {
        viewModelScope.launch {
            saving = true
            delay(1200)
            saving = false
            saved = true
            saveKind = kind
        }
    }




    fun searchPoNumber(it: String) {

    }
}