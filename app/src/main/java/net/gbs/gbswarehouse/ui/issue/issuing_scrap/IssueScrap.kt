package net.gbs.gbswarehouse.ui.issue.issuing_scrap

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.CostCenterSpinner
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessCard
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.costCenters
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.details_screen.BinDialogItem
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.details_screen.MaterialDialogItem
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.details_screen.PutAwaySelectedMaterialCard
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.MatCodeInput
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.warehouses

@Composable
fun IssueScrapScreen(navController: NavController){
    val viewModel: IssueScrapViewModel = viewModel()

    val accent = YELLOW
    val doneLabel = "الكمية المصروفة"
    val colors = AppTheme.colors
    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "صرف", subtitle = "إعدام",
                accent = accent, onBack = {
                    //  viewModel.back()
                    navController.popBackStack()
                })

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 10.dp,
                    bottom = if (viewModel.showFixedSave) 90.dp else 20.dp
                )
            ) {

//                // ── Order Info Card (non-blind) ────────────────────────────
//                item {
//                    Section(label = "بيانات الأمر", accent = accent) {
//                        val fields = listOf(
//                            Triple("رقم المستند", ord.id, null),
//                            Triple("الحالة",      ord.status, ord.statusColor),
//                            Triple("الجهة",       ord.info,   null),
//                            Triple("التاريخ",     ord.date,   null),
//                            Triple("عدد الأصناف","${ord.items} أصناف", null),
//                        )
//                        // 2-column grid
//                        for (i in fields.indices step 2) {
//                            Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
//                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                                val f1 = fields[i]
//                                val f2 = if (i + 1 < fields.size) fields[i + 1] else null
//                                // if "الجهة" span full width
//                                if (f1.first == "الجهة") {
//                                    InfoCell(f1.first, f1.second, f1.third ?: TEXT, modifier = Modifier.weight(1f))
//                                } else {
//                                    net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.InfoCell(
//                                        f1.first, f1.second, f1.third ?: TEXT,
//                                        modifier = Modifier.weight(1f)
//                                    )
//                                    if (f2 != null) {
//                                        net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.InfoCell(
//                                            f2.first, f2.second, f2.third ?: TEXT,
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
                item {
                    Field(
                        label = "رقم أمر الصيانة",
                        hint = "ادخل رقم أمر الصيانة",
                        value = viewModel.maintenanceOrderNumber,
                        onValueChange = { viewModel.maintenanceOrderNumber = it },
                        isNumber = true,
                        center = false,
                        ltr = false,
                        withLabel = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MarginTenDp()
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
                                onClear = { viewModel.clearMaterial() },
                                onValuationTypeSelected = {viewModel.selectedValuationType = it}
                            )
                        }
                    }
                }



               item{
                   MarginTenDp()
                   CostCenterSpinner(
                       hint = "اختر مركز التكلفة",
                       items = costCenters,
                       onItemSelected = { viewModel.selectedCostCenter = it }
                   )
               }
                item {
                    Field(
                        label = "سبب الاعدام",
                        hint = "ادخل سبب الاعدام",
                        value = viewModel.scrapReason,
                        onValueChange = { viewModel.scrapReason = it },
                        isNumber = true,
                        center = false,
                        ltr = false,
                        withLabel = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MarginTenDp()
                }
                item{
                    MarginTenDp()
                    WarehousesSpinner(
                        hint = "اختر المخزن",
                        items = warehouses,
                        onItemSelected = { viewModel.selectedWarehouse = it }
                    )
                }
                item {
                    Field(
                        label = "اسم القائم بالفحص",
                        hint = "ادخل اسم القائم بالفحص",
                        value = viewModel.examinerName,
                        onValueChange = { viewModel.examinerName = it },
                        isNumber = true,
                        center = false,
                        ltr = false,
                        withLabel = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MarginTenDp()
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
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    "حفظ الصرف", accent, { viewModel.doSave("save") },
                    saving = viewModel.saving
                )
            }
        }
    }
    // ── Bin Dialog ────────────────────────────────────────────────────────
    if (viewModel.binDlgFrom) {
        BottomSheetDialog(title = "اختيار موقع الصرف", onClose = { viewModel.binDlgFrom = false }) {
            SearchBox(viewModel.binInputFrom, { viewModel.binInputFrom = it }, "بحث عن Bin...",
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(
                start = 14.dp, end = 14.dp, bottom = 20.dp
            )) {
                itemsIndexed(viewModel.filteredBinsFrom) { _, b ->
                    BinDialogItem(b, viewModel.accent) { viewModel.selectBinFrom(b) }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
    if (viewModel.binDlgTo) {
        BottomSheetDialog(title = "اختيار موقع الصرف", onClose = { viewModel.binDlgTo = false }) {
            SearchBox(viewModel.binInputTo, { viewModel.binInputTo = it }, "بحث عن Bin...",
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(
                start = 14.dp, end = 14.dp, bottom = 20.dp
            )) {
                itemsIndexed(viewModel.filteredBinsTo) { _, b ->
                    BinDialogItem(b, viewModel.accent) { viewModel.selectBinTo(b) }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (viewModel.matDlg) {
        BottomSheetDialog(title = "اختيار الصنف", onClose = { viewModel.matDlg = false }) {
            SearchBox(
                viewModel.matSearch, { viewModel.matSearch = it }, "بحث عن صنف...",
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 20.dp,
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