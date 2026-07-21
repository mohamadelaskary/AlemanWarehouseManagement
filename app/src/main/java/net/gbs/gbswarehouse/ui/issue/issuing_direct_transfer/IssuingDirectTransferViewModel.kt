package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.BINS
import net.gbs.gbswarehouse.Bin
import net.gbs.gbswarehouse.DirectTransferWarehouseToType
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MATERIALS
import net.gbs.gbswarehouse.Material
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.ORDERS
import net.gbs.gbswarehouse.OpType
import net.gbs.gbswarehouse.Operation
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.directTransferWarehouseToTypes
import net.gbs.gbswarehouse.domain.model.Warehouse
import kotlin.collections.filter
import kotlin.text.lowercase


class IssuingDirectTransferViewModel: ViewModel() {
    var selectedValuationType by mutableStateOf<ValuationType?>(null)
    var selectedWarehouseTo by mutableStateOf<Warehouse?>(null)
    var selectedWarehouseToType by mutableStateOf<DirectTransferWarehouseToType?>(
        directTransferWarehouseToTypes[0]
    )
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

    var binInputFrom  by mutableStateOf("")
    var binInputTo  by mutableStateOf("")

    var binDlgFrom    by mutableStateOf(false)
    var binDlgTo    by mutableStateOf(false)
    var selBinFrom    by mutableStateOf<Bin?>(null)
    var selBinTo    by mutableStateOf<Bin?>(null)


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
        val list = ORDERS["SCRAP"] ?: emptyList()
        return if (orderSearch.isBlank()) list
        else list.filter { it.id.contains(orderSearch) || it.info.contains(orderSearch) }
    }

    val filteredMaterials: List<Material> get() {
        return if (matSearch.isBlank()) MATERIALS
        else MATERIALS.filter {
            it.code.lowercase().contains(matSearch.lowercase()) || it.desc.contains(matSearch)
        }
    }

    val filteredBinsFrom: List<Bin> get() {
        return if (binInputFrom.isBlank()) BINS
        else BINS.filter {
            it.code.lowercase().contains(binInputFrom.lowercase()) ||
                    it.zone.contains(binInputFrom) || it.aisle.contains(binInputFrom)
        }
    }
    val filteredBinsTo: List<Bin> get() {
        return if (binInputTo.isBlank()) BINS
        else BINS.filter {
            it.code.lowercase().contains(binInputTo.lowercase()) ||
                    it.zone.contains(binInputTo) || it.aisle.contains(binInputTo)
        }
    }

    val blindOk: Boolean get() = bCode.trim().isNotEmpty() && bDesc.trim().isNotEmpty() && vendorName.trim().isNotEmpty() && bQty.isNotEmpty()

    val showFixedSave: Boolean get() {
        if (variant == "quality" || saved) return false
        val isBlind = selOp?.blind == true
        return when {
            isBlind  -> blindOk
            variant == "putaway" -> selBinTo != null && qty.isNotEmpty()
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
    fun selectBinFrom(bin: Bin) { selBinFrom = bin; binInputFrom = bin.code; binDlgFrom = false }

    fun selectBinTo(bin: Bin) { selBinTo = bin; binInputTo = bin.code; binDlgTo = false }

    fun clearBinFrom() { selBinFrom = null; binInputFrom = ""; qty = "" }
    fun clearBinTo() { selBinTo = null; binInputTo = ""; qty = "" }


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
        selMat = null; qty = ""; smpQty = ""; selBinTo = null
        binInputFrom = "";binInputTo=""; matInput = ""; matSearch = ""
        saved = false; saveKind = null; saving = false
        bCode = ""; bDesc = ""; bQty = "";vendorName=""
    }

}