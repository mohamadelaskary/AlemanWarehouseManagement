package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

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
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.ValuationTypeSpinner
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.Warehouse
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.data.remote.mapper.toLineDto
import net.gbs.gbswarehouse.domain.model.SparePartsReturnLine
import net.gbs.gbswarehouse.ui.MaterialImageButton
import net.gbs.gbswarehouse.ui.receiving.ReceivingAddLineButton
import net.gbs.gbswarehouse.ui.receiving.ReceivingBottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.ReceivingChip
import net.gbs.gbswarehouse.ui.receiving.ReceivingInfoCell
import net.gbs.gbswarehouse.ui.receiving.ReceivingMatCodeInput
import net.gbs.gbswarehouse.ui.receiving.ReceivingMaterialDialogItem
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.AppTheme.dims
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.honeywellscanner.ScannerEffect


@Composable
fun ReceivingSparePartsFromVendorDetailsScreen(
    navController: NavController,
    poNumber: String
){
    val viewModel: ReceivingSparePartsFromVendorDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val doneLabel = stringResource(R.string.received_qty)
    val accent = colors.green

//        LaunchedEffect(Unit) {
//            viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnPurchaseOrderNumberEntered(poNumber))
//            viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnSearchButtonClicked)
//        }

    ScannerEffect(
        onScan = viewModel::onBarcodeScanned
    )
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(state.purchaseOrderGetDataStatus) {
        if (state.purchaseOrderGetDataStatus is PurchaseOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(poNumber) {
        viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnScreenLoaded(poNumber))
    }
    LaunchedEffect(state.selectedMaterial) {
        if (state.selectedMaterial != null) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(state.showSuccessDialog) {
        if (state.showSuccessDialog) {
            delay(1000)
//            navController.popBackStack()
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.receiving),
                subtitle = stringResource(R.string.spare_parts_from_vendor),
                accent = colors.green, onBack = {
                    navController.popBackStack()
                })

            when (state.purchaseOrderGetDataStatus) {
                PurchaseOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            color = colors.green,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is PurchaseOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError?.text.orEmpty(), color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), colors.green) {
                                viewModel.onEvent(ReceivingSparePartsFromVendorEvent.OnScreenLoaded(poNumber))
                            }
                        }
                    }
                }
                else -> {
                    val order = state.purchaseOrder
                    if (order != null) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 14.dp, end = 14.dp,
                                top = 10.dp,
                                bottom = if (state.addedItems.isNotEmpty()) 90.dp else 20.dp
                            )
                        ) {

                            // ── Order Info Card ────────────────────────────
                            item {
                                Section(label = stringResource(R.string.order_data), accent = colors.green) {
                                    val fields = listOf(
                                        Triple(stringResource(R.string.po_number), order.poNumber, null),
                                        Triple(stringResource(R.string.vendor), order.vendorName, null),
//                                        Triple(stringResource(R.string.items_count), "${order.purchaseOrderItems.size} ${stringResource(R.string.items)}", null),
                                    )
                                    for (i in fields.indices step 2) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val f1 = fields[i]
                                            val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                            ReceivingInfoCell(f1.first, f1.second, f1.third ?: colors.text, Modifier.weight(1f))
                                            if (f2 != null) {
                                                ReceivingInfoCell(f2.first, f2.second, f2.third ?: colors.text, Modifier.weight(1f))
                                            } else {
                                                Spacer(Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // ── Material Section ────────────────────────
                            item {
                                Section(label = stringResource(R.string.item_data), accent = colors.green) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                    Column(
                                        modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ReceivingMatCodeInput(
                                            value = state.materialCodeInput,
                                            onValueChange = {
                                                viewModel.onEvent(
                                                    ReceivingSparePartsFromVendorEvent.OnMaterialCodeInputEntered(
                                                        it
                                                    )
                                                )
                                            },
                                            onKeyboardAction = {
                                                viewModel.onEvent(
                                                    ReceivingSparePartsFromVendorEvent.OnMaterialSelected(
                                                        state.materialCodeInput
                                                    )
                                                )
                                            },
                                        )
                                        state.materialCodeError?.let {
                                            Text(it.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                                        }
                                    }
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
                                            valuationTypeError = state.valuationTypeError,
                                            warehouseSearchText = state.warehouseSearchText,
                                            onWarehouseSearchTextChanged = {
                                                viewModel.onEvent(
                                                    ReceivingSparePartsFromVendorEvent.OnWarehouseSearchTextChange(
                                                        it
                                                    )
                                                )
                                            },
                                            valuationTypeSearchText = state.valuationSearchText,
                                            onValuationTypeSearchTextChanged = {
                                                viewModel.onEvent(
                                                    ReceivingSparePartsFromVendorEvent.OnValuationSearchTextChange(
                                                        it
                                                    )
                                                )
                                            },
                                        )
                                    }
                                }
                            }

                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()){
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    ReceivingAddLineButton(
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
                                itemsIndexed(state.addedItems){index,item->
                                    ReceivingSparePartsFromVendorLineItem(item,
                                        onDeleteAddedMaterialButtonClicked = {
                                            viewModel.onEvent(
                                                ReceivingSparePartsFromVendorEvent.OnDeleteAddedMaterialButtonClicked(
                                                    index
                                                )
                                            )
                                        },
                                        onEditAddedMaterialButtonClicked = {
                                            viewModel.onEvent(
                                                ReceivingSparePartsFromVendorEvent.OnEditAddedMaterialButtonClicked(
                                                    index
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            // ── Dialogs ──────────────────────────────────────────
                            if(state.showSuccessDialog){
                                item {
                                    SuccessDialog(
                                        net.gbs.gbswarehouse.ui.theme.GREEN,
                                        state.savingSuccessStatusMessage?.text.orEmpty(),
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
                                        DANGER,
                                        state.savingErrorStatusMessage?.text.orEmpty(),
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
        }

        // ── Fixed Save Button ─────────────────────────────────────────────
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
                    saving = state.isSavingLoading,
                    enabled = !state.isSavingLoading
                )
            }
        }
    }
    if (state.isSavingLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = accent)
        }
    }

    // ── Material Dialog ───────────────────────────────────────────────────
    if (viewModel.matDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
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
                    ReceivingMaterialDialogItem(
                        materialCode = m.materialCode,
                        materialDescription = m.materialDescription,
                        uom = m.uom,
                        orderedQty = m.orderedQuantity.toString(),
                        receivedQty = m.receivedQuantity.toString(),
                        doneLabel = doneLabel,
                        accent = viewModel.accent
                    ) {
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
    item: SparePartsLine,
    onDeleteAddedMaterialButtonClicked: () -> Unit,
    onEditAddedMaterialButtonClicked: () -> Unit
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
            MarginVertical(5.dp)
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
//            Row(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = "${stringResource(R.string.warehouse)} : ${item.warehouseCode}",
//                    color = colors.muted,
//                    fontSize = 13.sp
//                )
//            }
            if (item.warehouseCode.isNotEmpty()||item.valuationType.isNullOrEmpty()) {
                MarginVertical(5.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReceivingChip(
                            stringResource(R.string.warehouse) + ": " + item.warehouseCode
                        )

                        if (!item.valuationType.isNullOrEmpty()) {
                            ReceivingChip(
                                stringResource(R.string.valuation_type_) + " ${item.valuationType}"
                            )
                        }

                        if (item.expiryDate!=null){
                            ReceivingChip(
                                stringResource(R.string.expiry_date) + " ${item.expiryDate.substring(0,10)}"
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onEditAddedMaterialButtonClicked) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = colors.text)
                        }
                        IconButton(
                            onClick = onDeleteAddedMaterialButtonClicked
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete", tint = colors.danger
                            )
                        }
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
        Text(title, color = colors.muted, fontSize = 11.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colors.text)
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
    expiryDate: String?,
    onExpiryDateChange: (String?) -> Unit,
    quantityError: Message? = null,
    warehouseError: Message? = null,
    valuationTypeError: Message? = null,
    warehouseSearchText:String,
    onWarehouseSearchTextChanged: (String) -> Unit,
    valuationTypeSearchText:String,
    onValuationTypeSearchTextChanged: (String) -> Unit,
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
            val matCode = mat.materialCode
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(matCode ?: "", color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(8.dp))
                if (!matCode.isNullOrEmpty()) {
                    MaterialImageButton(materialCode = matCode, accent = accent)
                }
            }
            Text("✕", color = colors.text, fontSize = dims.fontSizeBody,
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
        if (quantityError != null) {
            Text(quantityError.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(R.string.warehouse),
            items = warehouses,
            onItemSelected = onWarehouseSelected,
            accent = accent,
            searchText = warehouseSearchText,
            onSearchTextChanged = onWarehouseSearchTextChanged
        )

        if (warehouseError != null) {
            Text(warehouseError.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        if (mat.valuationTypes.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            ValuationTypeSpinner(
                hint = stringResource(R.string.valuation_type),
                items = mat.valuationTypes,
                onItemSelected = {
                    onValuationTypeChange(it)
                },
                accent = accent,
                searchText = valuationTypeSearchText,
                onSearchTextChanged = {
                    onValuationTypeSearchTextChanged(it)
                }
            )
            if (valuationTypeError != null) {
                Text(valuationTypeError.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        if (mat.isBatchManaged) {
            Text(stringResource(R.string.expiry_date), color = colors.muted,  fontSize = dims.fontSizeCaption)
            DatePickerField(
                value = expiryDate,
                onDateSelected = { onExpiryDateChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.expiry_date),
                hint = stringResource(R.string.enter_expiry_date_optional)
            )
        }
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
                    navController.popBackStack()
                })

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 10.dp,
                    bottom = if (vm.showFixedSave) 90.dp else 20.dp
                )
            ) {
                    item {
                        Section(label = stringResource(R.string.order_data), accent = accent) {
                            val fields = listOf(
                                Triple(stringResource(R.string.po_number), ord.id, null),
                                Triple(stringResource(R.string.status),      ord.status, ord.statusColor),
                                Triple(stringResource(R.string.info),       ord.info,   null),
                                Triple(stringResource(R.string.date),     ord.date,   null),
                                Triple(stringResource(R.string.items_count),"${ord.items} ${stringResource(R.string.items)}", null),
                            )
                            for (i in fields.indices step 2) {
                                Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val f1 = fields[i]
                                    val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                    ReceivingInfoCell(f1.first, f1.second, f1.third ?: colors.text, modifier = Modifier.weight(1f))
                                    if (f2 != null) {
                                        ReceivingInfoCell(f2.first, f2.second, f2.third ?: colors.text, modifier = Modifier.weight(1f))
                                    } else {
                                        Spacer(Modifier.weight(1f))
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
                                ReceivingMatCodeInput(
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
            }
        }

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

    if (vm.matDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_item), onClose = { vm.matDlg = false }) {
            SearchBox(vm.matSearch, { vm.matSearch = it }, stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 20.dp,
                )
            ) {
                itemsIndexed(vm.filteredMaterials) { _, m ->
                    ReceivingMaterialDialogItem(
                        materialCode = m.materialCode,
                        materialDescription = m.materialDescription,
                        uom = m.uom,
                        orderedQty = m.orderedQuantity.toString(),
                        receivedQty = m.receivedQuantity.toString(),
                        doneLabel = doneLabel,
                        accent = vm.accent
                    ) {
                    //    vm.selectMaterial(m)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    if (vm.binDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_bin), onClose = { vm.binDlg = false }) {
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
                        )
                    FullBtn("✕  ${stringResource(R.string.reject)}", colors.danger, onReject, saving, smpQty.isNotEmpty(),
                       )
                }
            }
        } else {
            val qLabel = "${if (isIssue) stringResource(R.string.issued_qty) else stringResource(R.string.putaway_qty)} (${mat.uom})"
            Field(qLabel, "0", qty, onQtyChange, isNumber = true, center = true)
        }
    }
}

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

@Composable
fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) accent.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📍 ${bin.code}", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

