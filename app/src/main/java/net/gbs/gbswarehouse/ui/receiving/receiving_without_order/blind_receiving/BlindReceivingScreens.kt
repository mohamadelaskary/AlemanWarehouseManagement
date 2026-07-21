package net.gbs.gbswarehouse.ui.receiving.receiving_without_order.blind_receiving

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.MATERIALS
import net.gbs.gbswarehouse.MaterialsSpinner
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.QtyFieldWithUOMSpinner
import net.gbs.gbswarehouse.QualityDecisionSpinner
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WorksGroupSpinner
import net.gbs.gbswarehouse.qualityDecisions
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen.BinDialogItem
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen.BottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BinCodeInput
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.SelectedBinCard
import net.gbs.gbswarehouse.uoms
import net.gbs.gbswarehouse.workersGroup
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun BlindReceivingScreen(
    navController : NavController,
) {
    val viewModel: BlindReceivingViewModel = viewModel()
    val colors = AppTheme.colors
    val accent = colors.purple
    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = "استلام", subtitle = "استلام بدون مرجع",
                accent = accent, onBack = {
                    //  vm.back()
                    navController.popBackStack()
                })
            LazyColumn {
                item {
                    Section(label = "بيانات المادة — إدخال يدوي", accent = colors.purple) {
                        // Info hint
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.purple.copy(alpha = 0.1f))
                                .border(1.dp, colors.purple.copy(0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("ℹ️", fontSize = 13.sp)
                            Text(
                                "لا يوجد أمر شراء — أدخل بيانات المادة يدوياً",
                                color = colors.purple, fontSize = 10.sp, lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        MaterialsSpinner(
                            label = "كود الصنف (اختياري)",
                            hint = "اكتب او اختر كود الصنف",
                            MATERIALS,
                            onItemSelected = { viewModel.selectedMaterial = it }
                        )
                        Spacer(Modifier.height(10.dp))
                        Field(
                            "وصف المادة (اختياري)",
                            "أدخل وصف المادة...",
                            viewModel.bDesc,
                            { viewModel.bDesc = it })
                        Spacer(Modifier.height(10.dp))
                        Field(
                            "المورد (اختياري)",
                            "أدخل اسم المورد...",
                            viewModel.vendorName,
                            { viewModel.vendorName = it })
                        Spacer(Modifier.height(20.dp))


                        WorksGroupSpinner("مجموعة العمال (اختياري)", workersGroup, onItemSelected = {
                            viewModel.selectedWorkersGroup = it
                        })




                        Spacer(Modifier.height(10.dp))
                        QtyFieldWithUOMSpinner(
                            label = "الكمية",
                            hint = "ادخل الكمية",
                            value = viewModel.qty,
                            isNumber = true,
                            onValueChange = { viewModel.qty = it },
                            onItemSelected = { viewModel.selectedUOM = it },
                            uOMItems = uoms
                        )
                        Spacer(Modifier.height(10.dp))
                        QualityDecisionSpinner(
                            hint = "قرار الجودة (اختياري)",
                            items = qualityDecisions,
                            onItemSelected = {
                                viewModel.selectedQualityDecision = it
                            }
                        )
                        Section(label = "بيانات التسكين", accent = accent) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                        // ── Bin Dialog ────────────────────────────────────────────────────────
                        if (viewModel.binDlg) {
                            BottomSheetDialog(title = "اختيار موقع التسكين", onClose = { viewModel.binDlg = false }) {
                                SearchBox(viewModel.binInput, { viewModel.binInput = it }, "بحث عن Bin...",
                                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                                LazyColumn(contentPadding = PaddingValues(
                                    start = 14.dp, end = 14.dp, bottom = 20.dp
                                )) {
                                    itemsIndexed(viewModel.filteredBins) { _, b ->
                                        BinDialogItem(b, viewModel.accent) { viewModel.selectBin(b) }
                                        Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                        if (viewModel.showSaveButton) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            0f to Color.Transparent, 0.35f to colors.bg
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                FullBtn(
                                    "حفظ", accent, { viewModel.doSave("save") },
                                    saving = viewModel.saving
                                )
                            }

                        }

                    }
                    if (viewModel.saved) {
                        SuccessCard(kind = viewModel.saveKind, accent = accent)
                    }
                }
            }
        }

    }
}
// ── SuccessCard ───────────────────────────────────────────────────────────────
@Composable
fun SuccessCard(kind: String?, accent: Color) {
    val isAccept = kind == "accept"
    val isReject = kind == "reject"
    val colors = AppTheme.colors
    val col   = if (isAccept) colors.succ else if (isReject) colors.danger else accent
    val label = if (isAccept) "تم القبول بنجاح" else if (isReject) "تم الرفض وتسجيل النتيجة" else "تم الحفظ بنجاح"
    val icon  = if (isReject) "✕" else "✓"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(col.copy(0.07f))
            .border(1.dp, col.copy(0.28f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(col.copy(0.14f))
                .border(2.dp, col, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = col, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = col, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text("تم تسجيل الحركة في النظام", color = colors.muted, fontSize = 11.sp)
    }
}