package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessCard
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.data.remote.dto.response.TransferToProductionLineDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferToProductionReservationLine
import net.gbs.gbswarehouse.ui.issue.BatchValuationTypeSpinner
import net.gbs.gbswarehouse.ui.issue.WarehousesSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BinCodeInput
import net.gbs.gbswarehouse.ui.theme.AppColors
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.BG
import net.gbs.gbswarehouse.ui.theme.BORDER
import net.gbs.gbswarehouse.ui.theme.BORDER2
import net.gbs.gbswarehouse.ui.theme.CARD
import net.gbs.gbswarehouse.ui.theme.DARK
import net.gbs.gbswarehouse.ui.theme.DIM
import net.gbs.gbswarehouse.ui.theme.ORANGE
import net.gbs.honeywellscanner.R
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun IssueRawMaterialsToProductionDetailsScreen(
    navController: NavController,
    orderId: String
){
    val viewModel: IssueDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(orderId) {
        viewModel.onEvent(IssueDetailsEvent.LoadOrderDetails(orderId))
    }
    val colors = AppTheme.colors
    val accent = ORANGE

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "صرف", subtitle = "تحويلات لمخازن الانتاج",
                accent = accent, onBack = {
                    navController.popBackStack()
                })

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else if (state.loadingErrorMessage != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        Text(
                            state.loadingErrorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(20.dp)
                        )
                        ElevatedButton(
                            onClick = { viewModel.onEvent(IssueDetailsEvent.LoadOrderDetails(orderId)) },
                            modifier = Modifier.padding(20.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = colors.border
                            ) ,
                            content = {Text(stringResource(net.gbs.gbswarehouse.R.string.reload))}
                        )

                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 14.dp, end = 14.dp,
                        top = 10.dp,
                        bottom = if (state.addedLines.isNotEmpty()) 90.dp else 20.dp
                    )
                ) {
                    // Order Info Card
                    if (state.reservationLines.isNotEmpty()) {
                        val ord = state.reservationLines[0]
                        item {
                            Section(label = stringResource(net.gbs.gbswarehouse.R.string.reservation_details), accent = accent) {
                                InfoCell(
                                    label = stringResource(net.gbs.gbswarehouse.R.string.reservation_order_number),
                                    value = ord.reservationNumber,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (ord.costCenterDescription!=null) {
                                    Spacer(Modifier.height(6.dp))
                                    InfoCell(
                                        label = stringResource(net.gbs.gbswarehouse.R.string.info),
                                        value = ord.costCenterDescription ?: "",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (!ord.costCenterCode.isNullOrEmpty()) {
                                        InfoCell(
                                            label = stringResource(net.gbs.gbswarehouse.R.string.cost_center),
                                            value = ord.costCenterCode,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    InfoCell(
                                        label = stringResource(net.gbs.gbswarehouse.R.string.items_count),
                                        value = "${state.reservationLines.size} ${stringResource(net.gbs.gbswarehouse.R.string.items)}",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Section(
                            label = "بيانات موقع الصرف",
                            accent = accent
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    BinCodeInput(
                                        value = state.binInput,
                                        onValueChange = {
                                            viewModel.onEvent(
                                                IssueDetailsEvent.OnBinInputChanged(it)
                                            )

                                            if (it.length >= 4) {
                                                viewModel.onEvent(
                                                    IssueDetailsEvent.OnScanBin(it)
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (!state.binCodeError.isNullOrEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = state.binCodeError.orEmpty(),
                                                color = colors.danger
                                            )
                                        }
                                    }
                                }
                            }

                            if (state.selBinData != null) {
                                Spacer(Modifier.height(8.dp))

                                IssuingSelectedBinCard(
                                    bin = state.selBinData!!,
                                    accent = accent,
                                    colors = colors,
                                    onClear = {
                                        viewModel.onEvent(
                                            IssueDetailsEvent.OnClearBin
                                        )
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Section(label = stringResource(net.gbs.gbswarehouse.R.string.item_data), accent = accent) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                        MatCodeInput(
                                            value = state.matInput,
                                            colors = colors,
                                            onValueChange = {
                                                viewModel.onEvent(
                                                    IssueDetailsEvent.OnMaterialSearchTextChanged(
                                                        it
                                                    )
                                                )
                                            },
                                        )
                                    if (!state.matErrorMessage.isNullOrEmpty())
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                            Text(state.matErrorMessage?:"", color = colors.danger)
                                        }
                                }
                                OutlineBtn("اختر مادة", accent) { viewModel.onEvent(IssueDetailsEvent.OnToggleMaterialDialog) }
                            }
                            if (state.selReservationLine != null) {
                                Spacer(Modifier.height(8.dp))
                                IssuingSelectedMaterialCard(
                                    state = state,
                                    accent = accent,
                                    onStockSearchChanged = { viewModel.onEvent(IssueDetailsEvent.OnStockSearchTextChanged(it)) },
                                    onStockSelected = { viewModel.onEvent(IssueDetailsEvent.OnStockSelected(it)) },
                                    onWarehouseSearchChanged = { viewModel.onEvent(IssueDetailsEvent.OnWarehouseSearchTextChanged(it)) },
                                    onWarehouseSelected = { viewModel.onEvent(IssueDetailsEvent.OnWarehouseSelected(it)) },
                                    onQtyChange = { viewModel.onEvent(IssueDetailsEvent.OnQtyChanged(it)) },
                                    onAdd = { viewModel.onEvent(IssueDetailsEvent.OnAddLine) },
                                    onClear = { viewModel.onEvent(IssueDetailsEvent.OnClearMaterial) },
                                    colors = colors,
                                    warehouseError = state.warehouseError,
                                )
                            }
                        }
                    }
                    
                    if (state.addedLines.isNotEmpty()) {
                        item {
                            Section(label = stringResource(net.gbs.gbswarehouse.R.string.added_items), accent = accent) {
                                state.addedLines.forEach { added ->
                                    AddedLineItem(added, accent,colors,
                                        onDelete = {
                                            viewModel.onEvent(
                                                IssueDetailsEvent.OnDeleteLine(it)
                                            )
                                        })
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

//                    if (state.saved) {
                        if (state.savingSuccessMessage!=null) {
                            item {
                                SuccessDialog(accent = net.gbs.gbswarehouse.ui.theme.GREEN, message = state.savingSuccessMessage?:"", onClose = {
                                    viewModel.onEvent(IssueDetailsEvent.OnDismissSuccess)
                                })
                            }
                        }
                        if (state.savingErrorMessage!=null){
                            item {
                                ErrorDialog(accent = net.gbs.gbswarehouse.ui.theme.DANGER, message = state.savingErrorMessage?:"", onClose = {
                                    viewModel.onEvent(IssueDetailsEvent.OnDismissSuccess)
                                })
                            }
                        }

//                    }
                }
            }
        }

        // ── Fixed Issue Button ─────────────────────────────────────────────
        if (state.addedLines.isNotEmpty() && !state.saved) {
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
                    "تأكيد الصرف", accent, { viewModel.onEvent(IssueDetailsEvent.OnIssueAll) },
                    saving = state.isSaving
                )
            }
        }
    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (state.matDlg) {
        BottomSheetDialog(title = "اختيار الصنف",colors = colors, onClose = { viewModel.onEvent(IssueDetailsEvent.OnToggleMaterialDialog) }) {
            SearchBox(
                state.matSearchText, { viewModel.onEvent(IssueDetailsEvent.OnMaterialSearchTextChanged(it)) }, "بحث عن صنف...",
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 20.dp,
                )
            ) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    IssuingMaterialDialogItem(m, accent,colors = colors) {
                        viewModel.onEvent(IssueDetailsEvent.OnMaterialSelected(m))
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}


@Composable
fun InfoCell(label: String, value: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val colors = AppTheme.colors
    val finalColor = if (color == Color.Unspecified) colors.text else color
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(label, color = colors.muted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp))
        Text(value, color = finalColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun IssuingSelectedBinCard(bin: StorageBinLocation, accent: Color,colors: AppColors, onClear: () -> Unit) {
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
            Text("📍 ${bin.storageBinCode}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
    }
}

@Composable
fun IssuingSelectedMaterialCard(
    state: IssueDetailsState,
    accent: Color,
    colors: AppColors,
    onStockSearchChanged: (String) -> Unit,
    onStockSelected: (SAPMaterialBatchValuationStock) -> Unit,
    onWarehouseSearchChanged: (String) -> Unit,
    onWarehouseSelected: (SAPWarehouse) -> Unit,
    onQtyChange: (String) -> Unit,
    onAdd: () -> Unit,
    onClear: () -> Unit,
    warehouseError:String,
) {
    val line = state.selReservationLine ?: return
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
            Text(line.materialCode ?: "", color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.text, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(line.materialDescription ?: "", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoCell(
                label = stringResource(net.gbs.gbswarehouse.R.string.required),
                value = line.quantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = "المتبقي",
                value = line.remainingQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(net.gbs.gbswarehouse.R.string.unit),
                value = line.uom ?: "",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))

        if (line.isBatchManaged == true || line.valuationTypes.isNotEmpty()) {
            if (!state.isStockLoading) {
                BatchValuationTypeSpinner(
                    items = state.stockList,
                    onItemSelected = onStockSelected,
                    accent = accent,
                    searchText = state.stockSearch,
                    onSearchTextChanged = onStockSearchChanged,
                    isBatchManaged = line.isBatchManaged ?: false
                )
                if (state.stockError.isNotEmpty())
                    Text(
                        state.stockError,
                        color = colors.danger,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                MarginTenDp()
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(net.gbs.gbswarehouse.R.string.warehouse),
            items = state.warehousesList,
            onItemSelected = onWarehouseSelected,
            accent = accent,
            searchText = state.warehouseSearch,
            onSearchTextChanged = onWarehouseSearchChanged
        )
        if (warehouseError.isNotEmpty()) Text(warehouseError, color = colors.danger, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))

        MarginTenDp()

        val qLabel = "كمية الصرف (${line.uom ?: ""})"
        Field(qLabel, "0", state.qty, onQtyChange, isNumber = true, center = true)
        if (!state.qtyError.isNullOrEmpty()) {
            Text(state.qtyError, color = colors.danger, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        
        Spacer(Modifier.height(12.dp))
        FullBtn("إضافة للقائمة", accent, onAdd)
    }
}

@Composable
fun AddedLineItem(added: TransferToProductionLineDto, accent: Color,colors: AppColors,onDelete:(String)->Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bg)
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(added.materialCode, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${added.quantity} ${added.uom}", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
        FlowRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Chip(stringResource(net.gbs.gbswarehouse.R.string.bin) +" ${added.storageBinCode}")
            Chip(stringResource(net.gbs.gbswarehouse.R.string.issue_warehouse) +": ${added.receivingWarehouseCode}")
            if (added.batch != null) Chip(stringResource(net.gbs.gbswarehouse.R.string.batch)+": ${added.batch}")
            if (added.valuationType != null)Chip(stringResource(net.gbs.gbswarehouse.R.string.valuation_type_)+" ${added.valuationType}")
        }
            IconButton(onClick = { onDelete(added.materialCode) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = ORANGE.copy(.10f)) {
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontSize = 12.sp, color = ORANGE, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun IssuingMaterialDialogItem(line: TransferToProductionReservationLine, accent: Color,colors: AppColors, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bg)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(line.materialCode ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(line.uom ?: "", color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(line.materialDescription ?: "", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text("المطلوب: ${line.quantity} | المتبقي: ${line.remainingQuantity}", color = colors.muted, fontSize = 10.sp)
    }
}

@Composable
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,colors: AppColors) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.bg)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value, onValueChange = onValueChange, singleLine = true,
            textStyle = TextStyle(
                color = colors.text, fontSize = 13.sp,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(colors.orange),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text("كود المادة...", color = colors.muted, fontSize = 13.sp)
                inner()
            }
        )
    }
}

@Composable
fun BottomSheetDialog(title: String,colors: AppColors, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(colors.bg)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border2,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .heightIn(max = 560.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* consume */ }
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp, color = colors.border,
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✕", color = colors.muted, fontSize = 18.sp,
                        modifier = Modifier.clickable { onClose() }.padding(horizontal = 2.dp))
                    Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                content()
            }
        }
    }
}
