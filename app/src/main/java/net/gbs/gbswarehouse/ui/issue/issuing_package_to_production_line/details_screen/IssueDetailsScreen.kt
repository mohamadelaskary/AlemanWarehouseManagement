package net.gbs.gbswarehouse.ui.issue.issuing_package_to_production_line.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

import net.gbs.gbswarehouse.Bin
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ui.theme.BG
import net.gbs.gbswarehouse.ui.theme.TEXT
import net.gbs.gbswarehouse.ui.theme.MUTED
import net.gbs.gbswarehouse.ui.theme.DARK
import net.gbs.gbswarehouse.ui.theme.BORDER
import net.gbs.gbswarehouse.ui.theme.BORDER2
import net.gbs.gbswarehouse.ui.theme.CARD
import net.gbs.gbswarehouse.ui.theme.CARD2
import net.gbs.gbswarehouse.ui.theme.DIM
import net.gbs.gbswarehouse.DropdownSpinner
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.Material
import net.gbs.gbswarehouse.ORDERS
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessCard
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.packagingLines
import net.gbs.gbswarehouse.partialIssueReasons
import net.gbs.gbswarehouse.productionLines
import net.gbs.gbswarehouse.shifts
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BinCodeInput

import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.SelectedBinCard
import net.gbs.gbswarehouse.ui.theme.AppTheme
import kotlin.collections.find

@Composable
fun IssuePackagingMaterialsToProductionDetailsScreen(
    navController: NavController,
    orderId: String
){
    val viewModel: IssueDetailsViewModel = viewModel()
    val ord = ORDERS["PROD"]?.find { it.id == orderId }!!
    val accent = YELLOW
    val doneLabel = "الكمية المصروفة"
    val colors = AppTheme.colors
    val dims = AppTheme.dims

    LaunchedEffect(viewModel.saved) {
        if (viewModel.saved) {
            delay(1000)
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "صرف", subtitle = "مواد تعبئة وتغليف إلى الانتاج",
                accent = accent, onBack = {
                    //  vm.back()
                    navController.popBackStack()
                })

            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding, end = dims.padding,
                    top = 10.dp,
                    bottom = if (viewModel.showFixedSave) dims.bottomBarHeight else 20.dp
                )
            ) {

                // ── Order Info Card (non-blind) ────────────────────────────
                item {
                    Section(label = "بيانات الأمر", accent = accent) {
                        val fields = listOf(
                            Triple("رقم امر الإنتاج", ord.id, null),
                            Triple("الحالة",      ord.status, ord.statusColor),
                            Triple("الجهة",       ord.info,   null),
                            Triple("التاريخ",     ord.date,   null),
                            Triple("عدد الأصناف","${ord.items} أصناف", null),
                        )
                        // 2-column grid
                        for (i in fields.indices step 2) {
                            Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val f1 = fields[i]
                                val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                // if "الجهة" span full width
                                if (f1.first == "الجهة") {
                                    InfoCell(f1.first, f1.second, f1.third ?: colors.text, Modifier.weight(1f))
                                } else {
                                    InfoCell(
                                        f1.first, f1.second, f1.third ?: colors.text,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (f2 != null) {
                                        InfoCell(
                                            f2.first, f2.second, f2.third ?: colors.text,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    DropdownSpinner(
                        hint = "اختر خط التعبئة (اختياري)",
                        items = packagingLines,
                        selectedItem = viewModel.selectedProductionLine,
                        onItemSelected = {viewModel.selectedProductionLine = it },
                        itemLabel = {it.name},
                        modifier = Modifier.weight(1f),

                    )
                    MarginTenDp()
                }

                item {
                    DropdownSpinner(
                        hint = "اختر الشيفت (اختياري)",
                        items = shifts,
                        selectedItem = viewModel.selectedShift,
                        onItemSelected = {viewModel.selectedShift = it },
                        itemLabel = {it.name},
                        modifier = Modifier.weight(1f),

                        )
                    MarginTenDp()
                }

                item {
                    Section(label = "بيانات موقع الصرف", accent = accent) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            BinCodeInput(
                                value = viewModel.binInput,
                                onValueChange = { viewModel.binInput = it },
                                modifier = Modifier.weight(1f)
                            )
                            OutlineBtn("اختر Bin", accent) { viewModel.binDlg = true }
                        }
                        if (viewModel.selBin != null) {
                            Spacer(Modifier.height(8.dp))
                            SelectedBinCard(
                                bin = viewModel.selBin!!,
                                accent = accent,
                                qty = viewModel.qty,
                                onQtyChange = { viewModel.qty = it },
                                onClear = { viewModel.clearBin() }
                            )
                        }
                    }
                }

                item {
                    Section(label = "بيانات الصنف", accent = accent) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            MatCodeInput(
                                value = viewModel.matInput,
                                onValueChange = { viewModel.matInput = it },
                                modifier = Modifier.weight(1f)
                            )
                            OutlineBtn("اختر مادة", accent) { viewModel.matDlg = true }
                        }
                        if (viewModel.selMat != null) {
                            Spacer(Modifier.height(8.dp))
                            PutAwaySelectedMaterialCard(
                                mat = viewModel.selMat!!,
                                accent = accent,
                                isQuality = false,
                                isIssue = false,
                                doneLabel = doneLabel,
                                smpQty = viewModel.smpQty,
                                onSmpQtyChange = { viewModel.smpQty = it },
                                qty = viewModel.qty,
                                onQtyChange = { viewModel.qty = it },
                                saved = viewModel.saved,
                                saving = viewModel.saving,
                                onAccept = { viewModel.doSave("accept") },
                                onReject = { viewModel.doSave("reject") },
                                onClear = { viewModel.clearMaterial() }
                            )
                        }
                    }
                }

                // ── Success Card ──────────────────────────────────────────
                if (viewModel.saved) {
                    item { SuccessCard(kind = viewModel.saveKind, accent = accent) }
                }
            }
        }

        // ── Fixed Save Button ─────────────────────────────────────────────
        if (viewModel.showFixedSave && !viewModel.saved) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent, 0.35f to colors.bg
                        )
                    )
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    "حفظ الصرف", accent, { viewModel.doSave("save") },
                    saving = viewModel.saving
                )
            }
        }
    }
    // ── Bin Dialog ────────────────────────────────────────────────────────
    if (viewModel.binDlg) {
        val dims = AppTheme.dims
        BottomSheetDialog(title = "اختيار موقع الصرف", onClose = { viewModel.binDlg = false }) {
            SearchBox(viewModel.binInput, { viewModel.binInput = it }, "بحث عن Bin...",
                Modifier.padding(horizontal = dims.padding, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(
                start = dims.padding, end = dims.padding, bottom = 20.dp
            )) {
                itemsIndexed(viewModel.filteredBins) { _, b ->
                    BinDialogItem(b, viewModel.accent) { viewModel.selectBin(b) }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (viewModel.matDlg) {
        val dims = AppTheme.dims
        BottomSheetDialog(title = "اختيار الصنف", onClose = { viewModel.matDlg = false }) {
            SearchBox(
                viewModel.matSearch, { viewModel.matSearch = it }, "بحث عن صنف...",
                Modifier.padding(horizontal = dims.padding, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding, end = dims.padding, bottom = 20.dp,
                )
            ) {
                itemsIndexed(viewModel.filteredMaterials) { _, m ->
                    MaterialDialogItem(m, viewModel.accent, doneLabel) {
                        viewModel.selectMaterial(m)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}


// ── Material Card ─────────────────────────────────────────────────────────────
@Composable
fun PutAwaySelectedMaterialCard(
    mat: Material, accent: Color, isQuality: Boolean, isIssue: Boolean,
    doneLabel: String, smpQty: String, onSmpQtyChange: (String) -> Unit,
    qty: String, onQtyChange: (String) -> Unit,
    saved: Boolean, saving: Boolean,
    onAccept: () -> Unit, onReject: () -> Unit, onClear: () -> Unit
) {
    val progress = if (mat.qty > 0) mat.done.toFloat() / mat.qty else 0f
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f))
            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(mat.code, color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.desc, color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf( doneLabel to mat.done.toString(),"المطلوب" to mat.qty.toString(),
                "الوحدة" to mat.unit,).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = dims.fontSizeCaption,
                        modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // Progress bar
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("التنفيذ", color = colors.muted, fontSize = dims.fontSizeCaption)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = dims.fontSizeCaption,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(4.dp)
                    .clip(RoundedCornerShape(2.dp)).background(colors.dark)
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(2.dp)).background(accent)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (isQuality) {
            Field("كمية العينة", "0", smpQty, onSmpQtyChange, isNumber = true, center = true)
            if (!saved) {
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FullBtn("✓  قبول", SUCC, onAccept, saving, smpQty.isNotEmpty(),
                        modifier = Modifier.weight(1f))
                    FullBtn("✕  رفض", DANGER, onReject, saving, smpQty.isNotEmpty(),
                        modifier = Modifier.weight(1f))
                }
            }
        } else {
            val qLabel = "${if (isIssue) "الكمية المصروفة" else "كمية الصرف"} (${mat.unit})"
            Field(qLabel, "0", qty, onQtyChange, isNumber = true, center = true)
        }
    }
}

// ── Bin Card ──────────────────────────────────────────────────────────────────
@Composable
fun SelectedBinCard(bin: Bin, accent: Color, qty: String, onQtyChange: (String) -> Unit,
                    onClear: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f))
            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("📍 ${bin.code}", color = accent, fontSize = dims.fontSizeTitle, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle,
                "الرف" to bin.rack, "المتاح" to bin.avail).forEach { (l, v) ->
                Column(
                    modifier = Modifier.weight(1f)
                        .clip(RoundedCornerShape(6.dp)).background(colors.dark).padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text(l, color = colors.muted, fontSize = dims.fontSizeCaption)
                    Text(v, color = colors.text, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Material Code Input ───────────────────────────────────────────────────────
@Composable
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value, onValueChange = onValueChange, singleLine = true,
            textStyle = TextStyle(
                color = colors.text, fontSize = dims.fontSizeButton,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text("كود المادة...", color = colors.muted, fontSize = dims.fontSizeButton)
                inner()
            }
        )
    }
}

@Composable
fun BinCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value, onValueChange = onValueChange, singleLine = true,
            textStyle = TextStyle(
                color = colors.text, fontSize = dims.fontSizeButton,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(YELLOW),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text("كود البن (Bin Code)...", color = colors.muted, fontSize = dims.fontSizeButton)
                inner()
            }
        )
    }
}

// ── Dialog items ──────────────────────────────────────────────────────────────
@Composable
fun MaterialDialogItem(mat: Material, accent: Color, doneLabel: String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) accent.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(mat.code, color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
            Text(mat.unit, color = colors.muted, fontSize = dims.fontSizeSubtitle)
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.desc, color = colors.text, fontSize = dims.fontSizeBody, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("المطلوب: ${mat.qty}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
            Text("$doneLabel: ${mat.done}",
                color = if (mat.done == mat.qty) SUCC else colors.muted, fontSize = dims.fontSizeSubtitle)
        }
    }
}

@Composable
fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) YELLOW.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📍 ${bin.code}", color = YELLOW, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
            Text("متاح", color = SUCC, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle, "المتاح" to bin.avail)
                .forEach { (l, v) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(l, color = colors.muted, fontSize = dims.fontSizeCaption)
                        Text(v, color = colors.text, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
                    }
                }
        }
    }
}



