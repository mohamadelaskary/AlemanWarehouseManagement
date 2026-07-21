package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.Bin
import net.gbs.gbswarehouse.DatePickerField
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.ORDERS
import net.gbs.gbswarehouse.OrderNumberSearchBox
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.ValuationTypeSpinner
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.HoneywellScanner
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingSparePartsFromVendorDetailsScreen(
    navController: NavController,
){

    val viewModel: ReceivingSparePartsFromVendorDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val doneLabel = stringResource(R.string.received_qty)
    var order: PurchaseOrder? = null
    ScannerEffect(
        onScan = viewModel::onBarcodeScanned
    )
    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.receiving),
                subtitle = stringResource(R.string.spare_parts_from_vendor),
                accent = colors.green, onBack = {
                    navController.popBackStack()
                })
            OrderNumberSearchBox(
                value = state.orderNumber,
                onValueChange = {
                    viewModel.onEvent(
                        ReceivingSparePartsFromVendorEvent.OnPurchaseOrderNumberEntered(it)
                    )
                },
                onSearchButtonClicked = {
                    viewModel.onEvent(
                        ReceivingSparePartsFromVendorEvent.OnSearchButtonClicked
                    )
                },
                placeholder = stringResource(R.string.search_by_number),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
            when (state.purchaseOrderSearchStatus) {
                PurchaseOrderSearchStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            color = colors.green,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                PurchaseOrderSearchStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(state.searchError,
                            color = colors.text,
                            modifier = Modifier.align(Alignment.Center))
                    }
                }
                PurchaseOrderSearchStatus.IDLE -> {

                }
                else -> {
                    order = state.purchaseOrder
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 14.dp, end = 14.dp,
                            top = 10.dp,
                            bottom =  20.dp
                        )
                    ) {

                        // ── Order Info Card (non-blind) ────────────────────────────
                        item {
                            Section(label = stringResource(R.string.order_data), accent = colors.green) {
                                val fields = listOf(
                                    Triple(stringResource(R.string.po_number), order?.poNumber!!, null),
                                    Triple(stringResource(R.string.vendor), order?.vendorName!!, null),
                                    Triple(stringResource(R.string.items_count), "${order?.purchaseOrderItems!!.size} ${stringResource(R.string.items)}", null),
                                )
                                // 2-column grid
                                for (i in fields.indices step 2) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val f1 = fields[i]
                                        val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                        if (f1.first == stringResource(R.string.vendor)) {
                                            InfoCell(
                                                f1.first, f1.second, f1.third ?: colors.text,
                                                modifier = Modifier.weight(1f)
                                            )
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


                        // ── Normal/Issue: Material Section ────────────────────────

                        item {
                            Section(label = stringResource(R.string.item_data), accent = colors.green) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MatCodeInput(
                                        value = state.selectedMaterial?.materialCode?:"",
                                        onValueChange = {
                                            viewModel.onEvent(
                                                ReceivingSparePartsFromVendorEvent.OnMaterialSelected(it)
                                            )
                                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlineBtn(stringResource(R.string.select_item), colors.green) { viewModel.matDlg = true }
                                }
                                if (state.selectedMaterial != null) {
                                    Spacer(Modifier.height(8.dp))
                                    SelectedMaterialCard(
                                        mat = state.selectedMaterial!!,
                                        accent = colors.green,
                                        doneLabel = doneLabel,
                                        qty = state.quantity,
                                        onQtyChange = {
                                            viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnQuantityEntered(it))
                                                      },
                                        saved = viewModel.saved,
                                        saving = viewModel.saving,
                                        onClear = { viewModel.onEvent(
                                            ReceivingSparePartsFromVendorEvent.OnClearMaterialButtonClicked
                                        ) },
                                        onValuationTypeChange = {
                                            viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnValuationTypeSelected(it))
                                        },
                                        expiryDate = state.selectedExpiryDate,
                                        onExpiryDateChange = {
                                            viewModel.onEvent(
                                                ReceivingSparePartsFromVendorEvent.OnExpiryDateSelected(it)
                                            )
                                        },
                                        warehouses = state.warehouses,
                                        onWarehouseSelected = {
                                            viewModel.onEvent(
                                                ReceivingSparePartsFromVendorEvent.OnWarehouseSelected(it)
                                            )
                                        },
                                        quantityError = state.quantityError,
                                        warehouseError = state.warehouseError,
                                        valuationTypeError = state.valuationTypeError
                                    )
                                }
                            }
                        }
                        val material = state.selectedMaterial

                        val isReadyToAddMaterial =
                            material != null && state.quantity.isNotEmpty()
                        Log.d("ReceivingSparePartsFromVendorDetailsScreen", "isReadyToAddMaterial: $isReadyToAddMaterial")
                        if (isReadyToAddMaterial){
                            item {
                                Spacer(Modifier.height(10.dp))
                                AddSparePartsLineButton(
                                    enabled = true,
                                    onClick = {
                                        viewModel.onEvent(
                                            ReceivingSparePartsFromVendorEvent.OnAddButtonClicked
                                        )
                                    },
                                )
                            }
                        }
                        if(state.addedItems.isNotEmpty()){
                            itemsIndexed(state.addedItems){_,item->
                                ReceivingSparePartsFromVendorLineItem(item)
                            }
                        }

                        // ── Success Card ──────────────────────────────────────────
                       if(state.showSuccessDialog){
                           item {
                               SuccessDialog(
                                   net.gbs.gbswarehouse.ui.theme.GREEN,
                                   state.savingSuccessStatusMessage,
                                   onClose = {
                                       viewModel.onEvent(
                                           ReceivingSparePartsFromVendorEvent.HideSuccessDialog
                                       )
                                   }
                               )
                           }
                       }
                        if(state.showErrorDialog){
                            item {
                                ErrorDialog(
                                    net.gbs.gbswarehouse.ui.theme.DANGER,
                                    state.savingErrorStatusMessage,
                                    onClose = {
                                        viewModel.onEvent(
                                            ReceivingSparePartsFromVendorEvent.HideErrorDialog
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }

        // ── Fixed Save Button ─────────────────────────────────────────────
        Log.d(TAG, "ReceivingSparePartsFromVendorDetailsScreen: ${state.addedItems}")
        if (state.addedItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent, 0.35f to colors.bg
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), colors.green, {
                        viewModel.onEvent(
                            ReceivingSparePartsFromVendorEvent.OnSaveButtonClicked
                        )
                    },
                    saving = state.isSavingLoading
                )
            }

        }

    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, {
                viewModel.onEvent(
                    ReceivingSparePartsFromVendorEvent.OnMaterialSearchTextEntered(it)
                )
            }, stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 90.dp,
                )
            ) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    MaterialDialogItem(m, viewModel.accent, doneLabel) {
                        viewModel.onEvent(
                            ReceivingSparePartsFromVendorEvent.OnMaterialSelected(m.materialCode)
                        )
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }


}
@Composable
fun ReceivingSparePartsFromVendorLineItem(
    item: SparePartsLine
) {
    val colors = AppTheme.colors
    val accent = GREEN

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.card
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.materialCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = accent
                )
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = item.receivedQuantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colors.text
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = item.uom,
                        fontSize = 12.sp,
                        color = colors.muted
                    )
                }
            }
            MarginTenDp()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,

            ) {
                Text(
                    text = item.materialDescription,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.text
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(R.string.warehouse)} : ${item.warehouseCode}",
                    color = colors.muted,
                    fontSize = 13.sp
                )
            }
            if (item.valuationType.isNotBlank() || item.expiryDate.isNotBlank()) {

                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (item.valuationType.isNotBlank()) {
                        Chip("🏷 ${item.valuationType}")
                    }

                    if (item.expiryDate.isNotBlank()) {
                        Chip("📅 ${item.expiryDate}")
                    }
                }
            }

            }

    }
    MarginTenDp()
}

@Composable
private fun Info(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Column(modifier) {
        Text(
            title,
            color = colors.muted,
            fontSize = 11.sp
        )

        Spacer(Modifier.height(3.dp))

        Text(
            value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = colors.text
        )
    }
}

@Composable
private fun Chip(text: String) {

    Surface(
        shape = RoundedCornerShape(50),
        color = GREEN.copy(.10f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            fontSize = 12.sp,
            color = GREEN,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun AddSparePartsLineButton (
    enabled: Boolean,
    onClick: () -> Unit
){
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.addMaterialButtonBackground)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.add), color = colors.addMaterialButtonTextColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun SelectedMaterialCard(
    mat: SparePartsMaterial, accent: Color,
    doneLabel: String,
    qty: String, onQtyChange: (String) -> Unit,
    saved: Boolean, saving: Boolean, onClear: () -> Unit,
    onValuationTypeChange: (ValuationType) -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    quantityError: String = "",
    warehouseError: String = "",
    valuationTypeError: String = "",
) {
    val progress = if (mat.orderedQuantity > 0) mat.receivedQuantity / mat.orderedQuantity else 0f
    val colors = AppTheme.colors

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
            Text(mat.materialCode, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf( doneLabel to mat.receivedQuantity.toString(),stringResource(R.string.required) to mat.orderedQuantity.toString(),
                stringResource(R.string.unit) to mat.uom,).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp,
                        modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // Progress bar
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = 9.sp)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = 9.sp,
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
        val qLabel = "${stringResource(R.string.receiving_qty)} (${mat.uom})"
        Field(qLabel, hint="", value = qty, {
            onQtyChange(it)}, isNumber = true, center = true)
        if (quantityError.isNotEmpty()) {
            Text(quantityError, color = net.gbs.gbswarehouse.ui.theme.DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(R.string.warehouse),
            items = warehouses,
            onItemSelected = { onWarehouseSelected(it) }
        )
        if (warehouseError.isNotEmpty()) {
            Text(warehouseError, color = net.gbs.gbswarehouse.ui.theme.DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        if (mat.valuationTypes.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            ValuationTypeSpinner(
                hint = stringResource(R.string.valuation_type),
                items = mat.valuationTypes,
                onItemSelected = {
                    onValuationTypeChange(it)
                }
            )
            if (valuationTypeError.isNotEmpty()) {
                Text(valuationTypeError, color = net.gbs.gbswarehouse.ui.theme.DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        DatePickerField(
            value = expiryDate,
            onDateSelected = { onExpiryDateChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.expiry_date),
            hint = stringResource(R.string.enter_expiry_date_optional)
        )
    }
}
@Composable
fun PutAwaySparePartsFromVendorDetailsScreen(
    navController: NavController,
    orderId: String
){
    val vm: ReceivingSparePartsFromVendorDetailsViewModel = hiltViewModel()
    val colors = AppTheme.colors
    val ord = ORDERS["PO"]?.find { it.id == orderId }!!
    val accent = colors.yellow
    val doneLabel = stringResource(R.string.received_qty)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = stringResource(R.string.putaway), subtitle = stringResource(R.string.spare_parts_from_vendor),
                accent = accent, onBack = {
                    //  vm.back()
                    navController.popBackStack()
                })

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 10.dp,
                    bottom = if (vm.showFixedSave) 90.dp else 20.dp
                )
            ) {

                // ── Order Info Card (non-blind) ────────────────────────────
                    item {
                        Section(label = stringResource(R.string.order_data), accent = accent) {
                            val fields = listOf(
                                Triple(stringResource(R.string.po_number), ord.id, null),
                                Triple(stringResource(R.string.status),      ord.status, ord.statusColor),
                                Triple(stringResource(R.string.info),       ord.info,   null),
                                Triple(stringResource(R.string.date),     ord.date,   null),
                                Triple(stringResource(R.string.items_count),"${ord.items} ${stringResource(R.string.items)}", null),
                            )
                            // 2-column grid
                            for (i in fields.indices step 2) {
                                Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val f1 = fields[i]
                                    val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                    if (f1.first == stringResource(R.string.info)) {
                                        InfoCell(f1.first, f1.second, f1.third ?: colors.text,
                                            modifier = Modifier.weight(1f))
                                    } else {
                                        InfoCell(f1.first, f1.second, f1.third ?: colors.text,
                                            modifier = Modifier.weight(1f))
                                        if (f2 != null) {
                                            InfoCell(f2.first, f2.second, f2.third ?: colors.text,
                                                modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }





                    item {
                        Section(label = stringResource(R.string.item_data), accent = accent) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                MatCodeInput(
                                    value = vm.matInput,
                                    onValueChange = { vm.matInput = it },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlineBtn(stringResource(R.string.select_item), accent) { vm.matDlg = true }
                            }
                            if (vm.selMat != null) {
                                Spacer(Modifier.height(8.dp))
                                PutAwaySelectedMaterialCard(
                                    mat = vm.selMat!!,
                                    accent = accent,
                                    isQuality = false,
                                    isIssue = false,
                                    doneLabel = doneLabel,
                                    smpQty = vm.smpQty,
                                    onSmpQtyChange = { vm.smpQty = it },
                                    qty = vm.qty,
                                    onQtyChange = { vm.qty = it },
                                    saved = vm.saved,
                                    saving = vm.saving,
                                    onAccept = { vm.doSave("accept") },
                                    onReject = { vm.doSave("reject") },
                                    onClear = { vm.onEvent(
                                        ReceivingSparePartsFromVendorEvent.OnClearMaterialButtonClicked
                                    ) }
                                )
                            }
                        }
                    }



                    item {
                        Section(label = stringResource(R.string.putaway_data), accent = accent) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                BinCodeInput(
                                    value = vm.binInput,
                                    onValueChange = { vm.binInput = it },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlineBtn(stringResource(R.string.select_bin), accent) { vm.binDlg = true }
                            }
                            if (vm.selBin != null) {
                                Spacer(Modifier.height(8.dp))
                                SelectedBinCard(
                                    bin = vm.selBin!!,
                                    accent = accent,
                                    qty = vm.qty,
                                    onQtyChange = { vm.qty = it },
                                    onClear = { vm.clearBin() }
                                )
                            }
                        }
                    }


                // ── Success Card ──────────────────────────────────────────
//                if (vm.saved) {
//                    item { SuccessCard(kind = vm.saveKind, accent = accent) }
//                }
            }
        }

        // ── Fixed Save Button ─────────────────────────────────────────────
        if (vm.showFixedSave && !vm.saved) {
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
                FullBtn(stringResource(R.string.confirm_putaway), accent, { vm.doSave("save") },
                    saving = vm.saving)
            }
        }
    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (vm.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { vm.matDlg = false }) {
            SearchBox(vm.matSearch, { vm.matSearch = it }, stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 20.dp,
                )
            ) {
                itemsIndexed(vm.filteredMaterials) { _, m ->
                    MaterialDialogItem(m, vm.accent, doneLabel) {
                        vm.selectMaterial(m)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    // ── Bin Dialog ────────────────────────────────────────────────────────
    if (vm.binDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_bin), onClose = { vm.binDlg = false }) {
            SearchBox(vm.binInput, { vm.binInput = it }, stringResource(R.string.search_for_bin),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(
                start = 14.dp, end = 14.dp, bottom = 20.dp
            )) {
                itemsIndexed(vm.filteredBins) { _, b ->
                    BinDialogItem(b, vm.accent) { vm.selectBin(b) }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}




// ── Material Card ─────────────────────────────────────────────────────────────
@Composable
fun PutAwaySelectedMaterialCard(
    mat: SparePartsMaterial, accent: Color, isQuality: Boolean, isIssue: Boolean,
    doneLabel: String, smpQty: String, onSmpQtyChange: (String) -> Unit,
    qty: String, onQtyChange: (String) -> Unit,
    saved: Boolean, saving: Boolean,
    onAccept: () -> Unit, onReject: () -> Unit, onClear: () -> Unit,
) {
    val progress = if (mat.orderedQuantity > 0) mat.receivedQuantity.toFloat() / mat.orderedQuantity else 0f
    val colors = AppTheme.colors

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
            Text(mat.materialCode, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf( doneLabel to mat.receivedQuantity.toString(),stringResource(R.string.required) to mat.orderedQuantity.toString(),
                stringResource(R.string.unit) to mat.uom,).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp,
                        modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // Progress bar
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = 9.sp)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = 9.sp,
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
            Field(stringResource(R.string.sample_qty), "0", smpQty, onSmpQtyChange, isNumber = true, center = true)
            if (!saved) {
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FullBtn("✓  ${stringResource(R.string.accept)}", colors.succ, onAccept, saving, smpQty.isNotEmpty(),
                        modifier = Modifier.weight(1f))
                    FullBtn("✕  ${stringResource(R.string.reject)}", colors.danger, onReject, saving, smpQty.isNotEmpty(),
                        modifier = Modifier.weight(1f))
                }
            }
        } else {
            val qLabel = "${if (isIssue) stringResource(R.string.issued_qty) else stringResource(R.string.putaway_qty)} (${mat.uom})"
            Field(qLabel, "0", qty, onQtyChange, isNumber = true, center = true)
        }
    }
}

// ── Bin Card ──────────────────────────────────────────────────────────────────
@Composable
fun SelectedBinCard(bin: Bin, accent: Color, qty: String, onQtyChange: (String) -> Unit,
                    onClear: () -> Unit) {
    val colors = AppTheme.colors
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
            Text("📍 ${bin.code}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(stringResource(R.string.zone) to bin.zone, stringResource(R.string.aisle) to bin.aisle,
                stringResource(R.string.rack) to bin.rack, stringResource(R.string.available) to bin.avail).forEach { (l, v) ->
                Column(
                    modifier = Modifier.weight(1f)
                        .clip(RoundedCornerShape(6.dp)).background(colors.dark).padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text(l, color = colors.muted, fontSize = 9.sp)
                    Text(v, color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Material Code Input ───────────────────────────────────────────────────────
@Composable
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
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
                color = colors.text, fontSize = 13.sp,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(colors.green),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(stringResource(R.string.item_code_hint), color = colors.muted, fontSize = 13.sp)
                inner()
            }
        )
    }
}

@Composable
fun BinCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
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
                color = colors.text, fontSize = 13.sp,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(colors.yellow),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(stringResource(R.string.bin_code_hint), color = colors.muted, fontSize = 13.sp)
                inner()
            }
        )
    }
}

// ── Dialog items ──────────────────────────────────────────────────────────────
@Composable
fun MaterialDialogItem(mat: SparePartsMaterial, accent: Color, doneLabel: String, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
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
            Text(mat.materialCode, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(mat.uom, color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("${stringResource(R.string.required)}: ${mat.orderedQuantity}", color = colors.muted, fontSize = 10.sp)
            Text("$doneLabel: ${mat.receivedQuantity}",
                color = if (mat.receivedQuantity == mat.orderedQuantity) colors.succ else colors.muted, fontSize = 10.sp)
        }
    }
}

@Composable
fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) colors.yellow.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📍 ${bin.code}", color = colors.yellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.available), color = colors.succ, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(stringResource(R.string.zone) to bin.zone, stringResource(R.string.aisle) to bin.aisle, stringResource(R.string.available) to bin.avail)
                .forEach { (l, v) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(l, color = colors.muted, fontSize = 9.sp)
                        Text(v, color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
        }
    }
}

// ── Bottom Sheet Dialog ───────────────────────────────────────────────────────
@Composable
fun BottomSheetDialog(title: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val colors = AppTheme.colors
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
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

// ── FullBtn overload with Modifier ────────────────────────────────────────────
@Composable
fun FullBtn(label: String, color: Color, onClick: () -> Unit,
            saving: Boolean = false, enabled: Boolean = true, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val bg     = if (!enabled) colors.dim.copy(0.22f) else color.copy(0.12f)
    val border = if (!enabled) colors.dim else color.copy(0.35f)
    val txtCol = if (!enabled) colors.dim else color

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled && !saving) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        if (saving) {
            CircularProgressIndicator(
                color = color, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else {
            Text(label, color = txtCol, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ── InfoCell with Modifier ────────────────────────────────────────────────────
@Composable
fun InfoCell(label: String, value: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val textColor = if (color == Color.Unspecified) colors.text else color
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .padding(all = 8.dp)
    ) {
        Text(label, color = colors.muted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp))
        Text(value, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
