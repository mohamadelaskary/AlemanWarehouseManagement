package net.gbs.gbswarehouse
//
//import androidx.compose.foundation.*
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDirection
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//
//// ═══════════════════════════════════════════════════════════════════════════
////  DETAIL SCREEN
//// ═══════════════════════════════════════════════════════════════════════════
//@Composable
//fun DetailScreen(vm: WarehouseViewModel) {
//    val op = vm.selOp ?: return
//    val accent    = vm.accent
//    val isBlind   = op.blind
//    val isPutaway = vm.variant == "putaway"
//    val isQuality = vm.variant == "quality"
//    val isIssue   = vm.variant == "issue"
//    val doneLabel = if (isIssue) "المصروف" else "المستلم"
//    val subtitle  = if (vm.selOrder != null) "#${vm.selOrder!!.id}"
//    else if (isBlind) "إدخال يدوي — بدون أمر شراء" else ""
//
//    Box(modifier = Modifier.fillMaxSize().background(BG)) {
//        Column(Modifier.fillMaxSize()) {
//            TopBar(title = op.name, subtitle = subtitle.ifEmpty { null },
//                accent = accent, onBack = {
//                  //  vm.back()
//                })
//
//            LazyColumn(
//                contentPadding = PaddingValues(
//                    start = 14.dp, end = 14.dp,
//                    top = 10.dp,
//                    bottom = if (vm.showFixedSave) 90.dp else 20.dp
//                )
//            ) {
//
//                // ── Order Info Card (non-blind) ────────────────────────────
//                if (!isBlind && vm.selOrder != null) {
//                    val ord = vm.selOrder!!
//                    item {
//                        Section(label = "بيانات الأمر", accent = accent) {
//                            val fields = listOf(
//                                Triple("رقم المستند", ord.id, null),
//                                Triple("الحالة",      ord.status, ord.statusColor),
//                                Triple("الجهة",       ord.info,   null),
//                                Triple("التاريخ",     ord.date,   null),
//                                Triple("عدد الأصناف","${ord.items} أصناف", null),
//                            )
//                            // 2-column grid
//                            for (i in fields.indices step 2) {
//                                Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
//                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                                    val f1 = fields[i]
//                                    val f2 = if (i + 1 < fields.size) fields[i + 1] else null
//                                    // if "الجهة" span full width
//                                    if (f1.first == "الجهة") {
//                                        InfoCell(f1.first, f1.second, f1.third ?: TEXT)
//                                    } else {
//                                        InfoCell(f1.first, f1.second, f1.third ?: TEXT,
//                                            modifier = Modifier.weight(1f))
//                                        if (f2 != null) {
//                                            InfoCell(f2.first, f2.second, f2.third ?: TEXT,
//                                                modifier = Modifier.weight(1f))
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // ── Blind Receive: Manual Entry ───────────────────────────
//                if (isBlind) {
//                    item {
//                        Section(label = "بيانات المادة — إدخال يدوي", accent = PURPLE) {
//                            // Info hint
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clip(RoundedCornerShape(8.dp))
//                                    .background(Color(0xFF1A0F2E))
//                                    .border(1.dp, PURPLE.copy(0.15f), RoundedCornerShape(8.dp))
//                                    .padding(10.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                Text("ℹ️", fontSize = 13.sp)
//                                Text("لا يوجد أمر شراء — أدخل بيانات المادة يدوياً",
//                                    color = PURPLE, fontSize = 10.sp, lineHeight = 16.sp,
//                                    modifier = Modifier.weight(1f))
//                            }
//                            Spacer(Modifier.height(12.dp))
//                            Field("كود المادة *", "مثال: MAT-10001", vm.bCode,
//                                { vm.bCode = it }, ltr = true)
//                            Spacer(Modifier.height(10.dp))
//                            Field("وصف المادة *", "أدخل وصف المادة...", vm.bDesc, { vm.bDesc = it })
//                            Spacer(Modifier.height(10.dp))
//                            Field("المورد *", "أدخل اسم المورد...", vm.vendorName, { vm.vendorName = it })
//                            if (!isQuality) {
//                                Spacer(Modifier.height(10.dp))
//                                Field("الكمية *", "0", vm.bQty, { vm.bQty = it },
//                                    isNumber = true, center = true)
//                            }
//                            // Quality controls for blind
//                            if (isQuality && vm.blindOk) {
//                                Spacer(Modifier.height(12.dp))
//                                Field("كمية العينة *", "0", vm.smpQty, { vm.smpQty = it },
//                                    isNumber = true, center = true)
//                                if (!vm.saved) {
//                                    Spacer(Modifier.height(10.dp))
//                                    Row(Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                        FullBtn("✓  قبول", SUCC, { vm.doSave("accept") },
//                                            vm.saving, vm.smpQty.isNotEmpty(),
//                                            modifier = Modifier.weight(1f))
//                                        FullBtn("✕  رفض", DANGER, { vm.doSave("reject") },
//                                            vm.saving, vm.smpQty.isNotEmpty(),
//                                            modifier = Modifier.weight(1f))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // ── Normal/Issue: Material Section ────────────────────────
//                if (!isBlind) {
//                    item {
//                        Section(label = "بيانات الصنف", accent = accent) {
//                            Row(Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                verticalAlignment = Alignment.CenterVertically) {
//                                MatCodeInput(
//                                    value = vm.matInput,
//                                    onValueChange = { vm.matInput = it },
//                                    modifier = Modifier.weight(1f)
//                                )
//                                OutlineBtn("اختر مادة", accent) { vm.matDlg = true }
//                            }
//                            if (vm.selMat != null) {
//                                Spacer(Modifier.height(8.dp))
//                                SelectedMaterialCard(
//                                    mat = vm.selMat!!,
//                                    accent = accent,
//                                    isQuality = isQuality,
//                                    isIssue = isIssue,
//                                    doneLabel = doneLabel,
//                                    smpQty = vm.smpQty,
//                                    onSmpQtyChange = { vm.smpQty = it },
//                                    qty = vm.qty,
//                                    onQtyChange = { vm.qty = it },
//                                    saved = vm.saved,
//                                    saving = vm.saving,
//                                    onAccept = { vm.doSave("accept") },
//                                    onReject = { vm.doSave("reject") },
//                                    onClear = { vm.clearMaterial() }
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // ── Putaway Section ───────────────────────────────────────
//                if (isPutaway) {
//                    item {
//                        Section(label = "بيانات التسكين", accent = accent) {
//                            Row(Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                verticalAlignment = Alignment.CenterVertically) {
//                                BinCodeInput(
//                                    value = vm.binInput,
//                                    onValueChange = { vm.binInput = it },
//                                    modifier = Modifier.weight(1f)
//                                )
//                                OutlineBtn("اختر Bin", accent) { vm.binDlg = true }
//                            }
//                            if (vm.selBin != null) {
//                                Spacer(Modifier.height(8.dp))
//                                SelectedBinCard(
//                                    bin = vm.selBin!!,
//                                    accent = accent,
//                                    qty = vm.qty,
//                                    onQtyChange = { vm.qty = it },
//                                    onClear = { vm.clearBin() }
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // ── Success Card ──────────────────────────────────────────
//                if (vm.saved) {
//                    item { SuccessCard(kind = vm.saveKind, accent = accent) }
//                }
//            }
//        }
//
//        // ── Fixed Save Button ─────────────────────────────────────────────
//        if (vm.showFixedSave && !vm.saved) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .fillMaxWidth()
//                    .background(
//                        Brush.verticalGradient(
//                            0f to Color.Transparent, 0.35f to BG
//                        )
//                    )
//                    .padding(horizontal = 14.dp, vertical = 12.dp)
//            ) {
//                FullBtn(vm.saveLabel, accent, { vm.doSave("save") },
//                    saving = vm.saving)
//            }
//        }
//    }
//
//    // ── Material Dialog ───────────────────────────────────────────────────
//    if (vm.matDlg) {
//        BottomSheetDialog(title = "اختيار الصنف", onClose = { vm.matDlg = false }) {
//            SearchBox(vm.matSearch, { vm.matSearch = it }, "بحث عن صنف...",
//                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
//            LazyColumn(
//                contentPadding = PaddingValues(
//                    start = 14.dp, end = 14.dp, bottom = 20.dp,
//                )
//            ) {
//                itemsIndexed(vm.filteredMaterials) { _, m ->
//                    MaterialDialogItem(m, vm.accent, doneLabel) {
//                        vm.selectMaterial(m)
//                    }
//                    Spacer(Modifier.height(6.dp))
//                }
//            }
//        }
//    }
//
//    // ── Bin Dialog ────────────────────────────────────────────────────────
//    if (vm.binDlg) {
//        BottomSheetDialog(title = "اختيار موقع التسكين", onClose = { vm.binDlg = false }) {
//            SearchBox(vm.binInput, { vm.binInput = it }, "بحث عن Bin...",
//                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
//            LazyColumn(contentPadding = PaddingValues(
//                    start = 14.dp, end = 14.dp, bottom = 20.dp
//                )) {
//                itemsIndexed(vm.filteredBins) { _, b ->
//                    BinDialogItem(b, vm.accent) { vm.selectBin(b) }
//                    Spacer(Modifier.height(6.dp))
//                }
//            }
//        }
//    }
//}
//
//// ── Material Card ─────────────────────────────────────────────────────────────
//@Composable
//fun SelectedMaterialCard(
//    mat: Material, accent: Color, isQuality: Boolean, isIssue: Boolean,
//    doneLabel: String, smpQty: String, onSmpQtyChange: (String) -> Unit,
//    qty: String, onQtyChange: (String) -> Unit,
//    saved: Boolean, saving: Boolean,
//    onAccept: () -> Unit, onReject: () -> Unit, onClear: () -> Unit
//) {
//    val progress = if (mat.qty > 0) mat.done.toFloat() / mat.qty else 0f
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(accent.copy(0.06f))
//            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
//            .padding(12.dp)
//    ) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically) {
//            Text(mat.code, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
//            Text("✕", color = MUTED, fontSize = 12.sp,
//                modifier = Modifier.clickable { onClear() }.padding(4.dp))
//        }
//        Spacer(Modifier.height(4.dp))
//        Text(mat.desc, color = TEXT, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
//            lineHeight = 18.sp)
//        Spacer(Modifier.height(8.dp))
//        // Stats row
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            listOf("الوحدة" to mat.unit, "المطلوب" to mat.qty.toString(),
//                doneLabel to mat.done.toString()).forEach { (l, v) ->
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(l, color = MUTED, fontSize = 9.sp,
//                        modifier = Modifier.padding(bottom = 2.dp))
//                    Text(v, color = TEXT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//        Spacer(Modifier.height(10.dp))
//        // Progress bar
//        Column {
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Text("التنفيذ", color = MUTED, fontSize = 9.sp)
//                Text("${(progress * 100).toInt()}%", color = accent, fontSize = 9.sp,
//                    fontWeight = FontWeight.SemiBold)
//            }
//            Spacer(Modifier.height(3.dp))
//            Box(
//                modifier = Modifier.fillMaxWidth().height(4.dp)
//                    .clip(RoundedCornerShape(2.dp)).background(DARK)
//            ) {
//                Box(
//                    modifier = Modifier.fillMaxHeight()
//                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
//                        .clip(RoundedCornerShape(2.dp)).background(accent)
//                )
//            }
//        }
//        Spacer(Modifier.height(10.dp))
//        if (isQuality) {
//            Field("كمية العينة", "0", smpQty, onSmpQtyChange, isNumber = true, center = true)
//            if (!saved) {
//                Spacer(Modifier.height(10.dp))
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    FullBtn("✓  قبول", SUCC, onAccept, saving, smpQty.isNotEmpty(),
//                        modifier = Modifier.weight(1f))
//                    FullBtn("✕  رفض", DANGER, onReject, saving, smpQty.isNotEmpty(),
//                        modifier = Modifier.weight(1f))
//                }
//            }
//        } else {
//            val qLabel = "${if (isIssue) "الكمية المصروفة" else "الكمية المستلمة"} (${mat.unit})"
//            Field(qLabel, "0", qty, onQtyChange, isNumber = true, center = true)
//        }
//    }
//}
//
//// ── Bin Card ──────────────────────────────────────────────────────────────────
//@Composable
//fun SelectedBinCard(bin: Bin, accent: Color, qty: String, onQtyChange: (String) -> Unit,
//                    onClear: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(accent.copy(0.06f))
//            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
//            .padding(12.dp)
//    ) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically) {
//            Text("📍 ${bin.code}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
//            Text("✕", color = MUTED, fontSize = 12.sp,
//                modifier = Modifier.clickable { onClear() }.padding(4.dp))
//        }
//        Spacer(Modifier.height(8.dp))
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle,
//                "الرف" to bin.rack, "المتاح" to bin.avail).forEach { (l, v) ->
//                Column(
//                    modifier = Modifier.weight(1f)
//                        .clip(RoundedCornerShape(6.dp)).background(DARK).padding(horizontal = 6.dp, vertical = 8.dp)
//                ) {
//                    Text(l, color = MUTED, fontSize = 9.sp)
//                    Text(v, color = TEXT, fontSize = 11.sp, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
////        Spacer(Modifier.height(12.dp))
////        Field("الكمية المراد تسكينها", "0", qty, onQtyChange, isNumber = true, center = true)
//    }
//}
//
//// ── Material Code Input ───────────────────────────────────────────────────────
//@Composable
//fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
//    Row(
//        modifier = modifier
//            .clip(RoundedCornerShape(10.dp))
//            .background(DARK)
//            .border(1.dp, BORDER2, RoundedCornerShape(10.dp))
//            .padding(horizontal = 10.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        BasicTextField(
//            value = value, onValueChange = onValueChange, singleLine = true,
//            textStyle = TextStyle(
//                color = TEXT, fontSize = 13.sp,
//                textDirection = TextDirection.Ltr
//            ),
//            cursorBrush = SolidColor(GREEN),
//            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
//            decorationBox = { inner ->
//                if (value.isEmpty()) Text("كود المادة...", color = MUTED, fontSize = 13.sp)
//                inner()
//            }
//        )
//    }
//}
//
//@Composable
//fun BinCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
//    Row(
//        modifier = modifier
//            .clip(RoundedCornerShape(10.dp))
//            .background(DARK)
//            .border(1.dp, BORDER2, RoundedCornerShape(10.dp))
//            .padding(horizontal = 10.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        BasicTextField(
//            value = value, onValueChange = onValueChange, singleLine = true,
//            textStyle = TextStyle(
//                color = TEXT, fontSize = 13.sp,
//                textDirection = TextDirection.Ltr
//            ),
//            cursorBrush = SolidColor(YELLOW),
//            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
//            decorationBox = { inner ->
//                if (value.isEmpty()) Text("كود البن (Bin Code)...", color = MUTED, fontSize = 13.sp)
//                inner()
//            }
//        )
//    }
//}
//
//// ── Dialog items ──────────────────────────────────────────────────────────────
//@Composable
//fun MaterialDialogItem(mat: Material, accent: Color, doneLabel: String, onClick: () -> Unit) {
//    var hovered by remember { mutableStateOf(false) }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(if (hovered) CARD2 else DARK)
//            .border(1.dp, if (hovered) accent.copy(0.35f) else BORDER, RoundedCornerShape(10.dp))
//            .clickable { onClick(); hovered = false }
//            .padding(all = 11.dp)
//    ) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(mat.code, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
//            Text(mat.unit, color = MUTED, fontSize = 10.sp)
//        }
//        Spacer(Modifier.height(4.dp))
//        Text(mat.desc, color = TEXT, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
//            lineHeight = 18.sp)
//        Spacer(Modifier.height(6.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
//            Text("المطلوب: ", color = MUTED, fontSize = 10.sp).let {}
//            Text("المطلوب: ${mat.qty}", color = MUTED, fontSize = 10.sp)
//            Text("$doneLabel: ${mat.done}",
//                color = if (mat.done == mat.qty) SUCC else MUTED, fontSize = 10.sp)
//        }
//    }
//}
//
//@Composable
//fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
//    var hovered by remember { mutableStateOf(false) }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(if (hovered) CARD2 else DARK)
//            .border(1.dp, if (hovered) YELLOW.copy(0.35f) else BORDER, RoundedCornerShape(10.dp))
//            .clickable { onClick(); hovered = false }
//            .padding(12.dp)
//    ) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("📍 ${bin.code}", color = YELLOW, fontSize = 10.sp, fontWeight = FontWeight.Bold)
//            Text("متاح", color = SUCC, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
//        }
//        Spacer(Modifier.height(4.dp))
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle, "المتاح" to bin.avail)
//                .forEach { (l, v) ->
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(l, color = MUTED, fontSize = 9.sp)
//                        Text(v, color = TEXT, fontSize = 11.sp, fontWeight = FontWeight.Bold)
//                    }
//                }
//        }
//    }
//}
//
//// ── Bottom Sheet Dialog ───────────────────────────────────────────────────────
//@Composable
//fun BottomSheetDialog(title: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
//    Dialog(
//        onDismissRequest = onClose,
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.65f))
//                .clickable(
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ) { onClose() },
//            contentAlignment = Alignment.BottomCenter
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//                    .background(CARD)
//                    .border(1.dp, BORDER2,
//                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//                    .heightIn(max = 560.dp)
//                    .clickable(
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() }
//                    ) { /* consume */ }
//            ) {
//                // Dialog Header
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .border(
//                            width = 1.dp, color = BORDER,
//                            shape = RoundedCornerShape(0.dp)
//                        )
//                        .padding(horizontal = 14.dp, vertical = 14.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("✕", color = MUTED, fontSize = 18.sp,
//                        modifier = Modifier.clickable { onClose() }.padding(horizontal = 2.dp))
//                    Text(title, color = TEXT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
//                }
//                content()
//            }
//        }
//    }
//}
//
//// ── FullBtn overload with Modifier ────────────────────────────────────────────
//@Composable
//fun FullBtn(label: String, color: Color, onClick: () -> Unit,
//            saving: Boolean = false, enabled: Boolean = true, modifier: Modifier = Modifier) {
//    val bg     = if (!enabled) DIM.copy(0.22f) else color.copy(0.12f)
//    val border = if (!enabled) DIM else color.copy(0.35f)
//    val txtCol = if (!enabled) DIM else color
//
//    Box(
//        modifier = modifier
//            .clip(RoundedCornerShape(10.dp))
//            .background(bg)
//            .border(1.dp, border, RoundedCornerShape(10.dp))
//            .clickable(enabled = enabled && !saving) { onClick() }
//            .padding(vertical = 13.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        if (saving) {
//            CircularProgressIndicator(
//                color = color, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
//        } else {
//            Text(label, color = txtCol, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
//        }
//    }
//}
//
//// ── InfoCell with Modifier ────────────────────────────────────────────────────
//@Composable
//fun InfoCell(label: String, value: String, color: Color = TEXT, modifier: Modifier = Modifier) {
//    Column(
//        modifier = modifier
//            .clip(RoundedCornerShape(8.dp))
//            .background(DARK)
//            .padding(all = 8.dp)
//    ) {
//        Text(label, color = MUTED, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
//            modifier = Modifier.padding(bottom = 2.dp))
//        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
//    }
//}