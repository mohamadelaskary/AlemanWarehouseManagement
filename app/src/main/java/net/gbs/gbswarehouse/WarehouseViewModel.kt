package net.gbs.gbswarehouse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WarehouseViewModel : ViewModel() {

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

    val filteredOrders: List<Order> get() {
        val list = ORDERS[selOp?.refType] ?: emptyList()
        return if (orderSearch.isBlank()) list
        else list.filter { it.id.contains(orderSearch) || it.info.contains(orderSearch) }
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
//    fun navigate(to: Screens, op: Operation? = null, opType: OpType? = null,
//                 order: Order? = null, cat: String? = null) {
//        if (cat != null) mainCat = cat
//        if (op != null) selOp = op
//        if (opType != null) selOpType = opType
//        if (order != null) selOrder = order
//        if (to != screen) { orderSearch = ""; resetDetail() }
//        screen = to
//    }
//
//    fun back() {
//        when (screen) {
//            Screens.DETAIL  -> navigate(if (selOp?.blind == true) Screens.OPTYPE else Screens.LIST)
//            Screens.LIST    -> navigate(if (mainCat == "issue") Screens.SUBMENU else Screens.OPTYPE)
//            Screens.OPTYPE  -> navigate(Screens.SUBMENU)
//            Screens.SUBMENU -> navigate(Screens.HOME)
//            else -> {}
//        }
//    }

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