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
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.mapper.toPurchaseOrder
import net.gbs.gbswarehouse.data.remote.mapper.toWarehouses
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.PurchaseOrderRequest
import net.gbs.gbswarehouse.domain.model.ReceiveVendorSparePartsRequest
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.GetPurchaseOrderUseCase
import net.gbs.gbswarehouse.domain.usecase.ReceiveVendorSparePartsUseCase
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.text.lowercase
@HiltViewModel
class ReceivingSparePartsFromVendorDetailsViewModel @Inject constructor(
    private val getPurchaseOrderUseCase: GetPurchaseOrderUseCase,
    private val getWarehousesUseCase: GetPlantWarehousesUseCase,
    private val receiveVendorSparePartsUseCase: ReceiveVendorSparePartsUseCase
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

            is ReceivingSparePartsFromVendorEvent.OnPurchaseOrderNumberEntered ->
                updateOrderNumber(event.orderNumber)

            ReceivingSparePartsFromVendorEvent.OnSearchButtonClicked ->
                search()

            ReceivingSparePartsFromVendorEvent.OnClearButtonClicked ->
                clear()

            is ReceivingSparePartsFromVendorEvent.OnMaterialSelected ->
                loadMaterial(event.materialCode)

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
                valuationTypeError = ""
            )
        }
    }
    private fun clear(){
        _uiState.update {
            it.copy(
                selectedMaterial = null,
                warehouses = emptyList(),
                selectedExpiryDate = "",
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                addedItems = emptyList(),
                error = ""
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

    private fun search() {
        val orderNumber = _uiState.value.orderNumber
        if (orderNumber.isEmpty()){
            _uiState.update {
                it.copy(
                    searchError = "Please enter order number"
                )
            }
            return
        }
        viewModelScope.launch {

            _uiState.update {

                it.copy(
                    purchaseOrderSearchStatus = PurchaseOrderSearchStatus.IDLE,
                    error = ""
                )

            }

            when (

                val result =
                    getPurchaseOrderUseCase(
                        PurchaseOrderRequest(
                            pageName = pageName,
                            companyCodes = listOf(
                                "1001","1002"
                            ),
                            poNumber = _uiState.value.orderNumber
                        )
                    )

            ) {
                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            purchaseOrderSearchStatus = PurchaseOrderSearchStatus.LOADING
                        )
                    }
                }
                is NetworkResult.Success -> {
                    val purchaseOrder = result.data.data?.toPurchaseOrder()
                    _uiState.update {
                        it.copy(
                            purchaseOrderSearchStatus = PurchaseOrderSearchStatus.SUCCESS,
                            purchaseOrder = purchaseOrder,
                            materials = purchaseOrder?.purchaseOrderItems ?: emptyList(),
                            filteredMaterials = purchaseOrder?.purchaseOrderItems?:emptyList()
                        )
                    }
                }

                is NetworkResult.Error -> {

                    _uiState.update {

                        it.copy(

                            purchaseOrderSearchStatus = PurchaseOrderSearchStatus.ERROR,

                            searchError = result.message

                        )

                    }

                }

            }

        }

    }
    private fun loadMaterial(
        materialCode: String
    ) {
        val selectedMaterial = _uiState.value.materials.find { material -> material.materialCode == materialCode }
        if (selectedMaterial==null){
            _uiState.update {
                it.copy(
                    materialCodeError = "Wrong material code"
                )
            }
            return
        }
        viewModelScope.launch {

            _uiState.update {

                it.copy(
                    isWarehousesLoading = true,
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
                            warehouses = warehouses.data.data.toWarehouses()

                        )

                    }

                }

                is NetworkResult.Error -> {

                    _uiState.update {

                        it.copy(

                            isWarehousesLoading = false,

                            error = warehouses.message

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
                quantityError = ""
            )
        }
    }

    private fun selectWarehouse (selectedWarehouse: Warehouse){
        _uiState.update {
            it.copy(
                selectedWarehouse = selectedWarehouse,
                warehouseError = ""
            )
        }
    }

    private fun updateExpiryDate (expiryDate:String) {
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
        if (quantity.isEmpty() || quantity.toFloatOrNull() == null || quantity.toFloat() <= 0) {
            _uiState.update { it.copy(quantityError = "Please enter a valid quantity") }
            hasError = true
        } else if (quantity.toFloat() > material.remainingQuantity) {
            _uiState.update { it.copy(quantityError = "Quantity cannot exceed remaining quantity (${material.remainingQuantity})") }
            hasError = true
        }

        if (state.selectedWarehouse == null) {
            _uiState.update { it.copy(warehouseError = "Please select a warehouse") }
            hasError = true
        }

        if (material.valuationTypes.isNotEmpty() && state.selectedValuationType == null) {
            _uiState.update { it.copy(valuationTypeError = "Please select a valuation type") }
            hasError = true
        }

        if (hasError) return

        val poNumber = state.purchaseOrder?.poNumber ?: return
        val warehouse = state.selectedWarehouse!!
        val expiryDate = state.selectedExpiryDate
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
                    )
                )
            )
        }
        clearMaterial()
        Log.d("ReceivingSparePartsFromVendorDetailsViewModel", "addItem: ${_uiState.value.addedItems}")
    }

    private fun clearMaterial(){
        _uiState.update {
            it.copy(
                selectedMaterial = null,
                selectedExpiryDate = "",
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                quantityError = "",
                warehouseError = "",
                valuationTypeError = "",
                materialCodeError = ""
            )
        }
    }
    private fun save(){
        val state = _uiState.value
        val addedItems = state.addedItems
        if (addedItems.isEmpty()) {
            _uiState.update {
                it.copy(
                    savingErrorStatusMessage = "من فضلك اضف اصناف للاستلام أولا!",
                    isSavingLoading = false
                )
            }
            return
        }
        viewModelScope.launch {
            val result = receiveVendorSparePartsUseCase(
                ReceiveVendorSparePartsRequest(
                    userId = "25d9118f-a8f2-48fd-8150-8509a18ca903",
                    companyCodes = listOf("1001","1002"),
                    poNumber = state.purchaseOrder?.poNumber ?: "",
                    lines = addedItems,
                )
            )
            when(result){
                is NetworkResult.Error -> {
                    showErrorDialog()
                    _uiState.update {
                        it.copy(
                            showSuccessDialog = false,
                            showErrorDialog = true,
                            savingSuccessStatusMessage = "",
                            savingErrorStatusMessage = result.message,
                            isSavingLoading = false,
                        )
                    }
                }
                NetworkResult.Loading -> {
                    _uiState.update{
                        it.copy(
                            showSuccessDialog = false,
                            showErrorDialog = false,
                            savingSuccessStatusMessage = "",
                            savingErrorStatusMessage = "",
                            isSavingLoading = true
                        )
                    }
                }
                is NetworkResult.Success -> {
                    showSuccessDialog()
                    _uiState.update {
                        it.copy(
                            showSuccessDialog = true,
                            showErrorDialog = false,
                            savingSuccessStatusMessage = result.message!!,
                            savingErrorStatusMessage = "",
                            isSavingLoading = false,
                        )
                    }
                    resetDetail(result.data.data?.toPurchaseOrder())
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
                warehouses = emptyList(),
                selectedExpiryDate = "",
                selectedWarehouse = null,
                selectedValuationType = null,
                quantity = "",
                addedItems = emptyList(),
                error = "",
                materialCodeError = "",
                searchError = "",
                quantityError = "",
                warehouseError = "",
                valuationTypeError = "",
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

    val saveLabel: String get() = when (variant) {
        "putaway" -> "⌘  تأكيد التسكين"
        "issue"   -> "↑  حفظ الصرف"
        else      -> "↓  حفظ الاستلام"
    }

    // ── Navigation ─────────────────────────────────────────────────────────




    // ── Detail actions ──────────────────────────────────────────────────────
    fun selectMaterial(mat: SparePartsMaterial) {
        selMat = mat; matInput = mat.materialCode; matDlg = false; matSearch = ""
    }


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