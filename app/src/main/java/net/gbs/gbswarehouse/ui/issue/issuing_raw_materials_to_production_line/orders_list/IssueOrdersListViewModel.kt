package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.orders_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.BINS
import net.gbs.gbswarehouse.Bin
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.Material
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.ORDERS
import net.gbs.gbswarehouse.OpType
import net.gbs.gbswarehouse.Operation
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.RAW_MATERIALS
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.domain.usecase.issuing.GetTransferToProductionReservationsUseCase
import javax.inject.Inject

@HiltViewModel
class IssueOrdersListViewModel @Inject constructor(
    private val getTransferToProductionReservationsUseCase: GetTransferToProductionReservationsUseCase
): ViewModel() {
    var orders by mutableStateOf<List<SparePartsReturnReservationOrder>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showMaterialsOrderIndex by mutableIntStateOf(-1)

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = getTransferToProductionReservationsUseCase()) {
                is NetworkResult.Success -> {
                    orders = result.data
                }
                is NetworkResult.BusinessError -> {
                    errorMessage = result.message
                }
                is NetworkResult.HttpError -> {
                    errorMessage = result.message
                }
                is NetworkResult.NetworkError -> {
                    errorMessage = result.message
                }
                is NetworkResult.UnknownError -> {
                    errorMessage = result.message
                }
                else -> {
                    errorMessage = "Unknown error"
                }
            }
            isLoading = false
        }
    }

    var currentClickedOrderIndex by mutableStateOf(-1)
    var showMaterials by mutableStateOf(false)
    var screen    by mutableStateOf(Screens.Home)
    var mainCat   by mutableStateOf<String?>(null)       // "recv" | "issue"
    var selOp     by mutableStateOf<Operation?>(null)
    var selOpType by mutableStateOf<OpType?>(null)
    var selOrder  by mutableStateOf<Order?>(null)

    // list search
    var orderSearch by mutableStateOf("")
    var expandedOrderNumbers by mutableStateOf<Set<String>>(emptySet())

    fun toggleOrderExpanded(reservationNumber: String) {
        expandedOrderNumbers = if (expandedOrderNumbers.contains(reservationNumber)) {
            expandedOrderNumbers - reservationNumber
        } else {
            expandedOrderNumbers + reservationNumber
        }
    }

    // detail fields
    var matInput  by mutableStateOf("")
    var matDlg    by mutableStateOf(false)
    var matSearch by mutableStateOf("")
    var selMat    by mutableStateOf<Material?>(null)
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

    val accent: androidx.compose.ui.graphics.Color get() =
        if (mainCat == "issue") ORANGE else selOpType?.accent ?: GREEN

    val filteredOrders: List<SparePartsReturnReservationOrder> get() {
        return if (orderSearch.isBlank()) orders
        else orders.filter { 
            it.reservationNumber?.contains(orderSearch) == true || 
            it.orderDescription?.contains(orderSearch, ignoreCase = true) == true 
        }
    }

    val filteredMaterials: List<Material> get() {
        return if (matSearch.isBlank()) RAW_MATERIALS
        else RAW_MATERIALS.filter {
            it.code.lowercase().contains(matSearch.lowercase()) || it.desc.contains(matSearch)
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
    fun selectMaterial(mat: Material) {
        selMat = mat; matInput = mat.code; matDlg = false; matSearch = ""
    }

    fun clearMaterial() { selMat = null; matInput = ""; qty = ""; smpQty = "" }

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

    private fun resetDetail() {
        selMat = null; qty = ""; smpQty = ""; selBin = null
        binInput = ""; matInput = ""; matSearch = ""
        saved = false; saveKind = null; saving = false
        bCode = ""; bDesc = ""; bQty = "";vendorName=""
    }
}