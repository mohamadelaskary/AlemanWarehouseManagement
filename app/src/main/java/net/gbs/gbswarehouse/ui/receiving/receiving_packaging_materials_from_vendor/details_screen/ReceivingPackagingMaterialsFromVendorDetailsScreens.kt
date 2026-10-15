package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.DatePickerField
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsLine
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.MaterialImageButton
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list.PackagingPurchaseOrderCard
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
fun ReceivingPackagingMaterialsFromVendorDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: ReceivingPackagingMaterialsFromVendorDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = LocalFocusManager.current
    LaunchedEffect(state.purchaseOrderGetDataStatus) {
        if (state.purchaseOrderGetDataStatus is PackagingPurchaseOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnScreenLoaded(orderId))
    }
    LaunchedEffect(state.selectedMaterial) {
        if (state.selectedMaterial != null) {
            focusManager.clearFocus()
        }
    }

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.receiving),
                subtitle = stringResource(R.string.packaging_materials_from_vendor),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.purchaseOrderGetDataStatus) {
                PackagingPurchaseOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is PackagingPurchaseOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError?.text ?: "", color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnScreenLoaded(orderId))
                            }
                        }
                    }
                }
                else -> {
                    val order = state.purchaseOrder
                    if (order != null) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 14.dp, end = 14.dp, top = 10.dp,
                                bottom = if (state.addedItems.isNotEmpty()) 90.dp else 20.dp
                            )
                        ) {
                            item {
                                Section(label = stringResource(R.string.order_data), accent = accent) {
                                    val fields = listOf(
                                        Triple(stringResource(R.string.car_number), order.carNo ?: "", null),
                                        Triple(stringResource(R.string.vendor), order.vendorName ?: "", null),
                                        Triple(stringResource(R.string.vendor_code), order.vendorCode ?: "", null),
                                        Triple(stringResource(R.string.items_count), "${order.purchaseOrderItems.size} " + stringResource(R.string.items), null),
                                    )
                                    for (i in fields.indices step 2) {
                                        Row(Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            val f1 = fields[i]
                                            val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                            ReceivingInfoCell(f1.first, f1.second, f1.third ?: colors.text, modifier = Modifier.weight(1f))
                                            if (f2 != null) {
                                                ReceivingInfoCell(f2.first, f2.second, f2.third ?: colors.text, modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                    if (!order.factoryCarDescription.isNullOrBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        ReceivingInfoCell(stringResource(R.string.car_number), order.factoryCarDescription ?: "", colors.text)
                                    }
                                }
                            }

                            item {
                                Section(label = stringResource(R.string.item_data), accent = accent) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            ReceivingMatCodeInput(
                                                value = state.materialCodeInput,
                                                onValueChange = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialCodeInputEntered(it)) },
                                                onKeyboardAction = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSelected(state.materialCodeInput)) }
                                            )
                                            if (state.materialCodeError != null){
                                                Text(state.materialCodeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                                            }
                                        }
                                        OutlineBtn(stringResource(R.string.select_item), accent) { viewModel.matDlg = true }
                                    }
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedPackagingMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnWarehouseSelected(it)) },
                                            carNo = state.carNo,
                                            onCarNoChange = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnCarNoEntered(it)) },
                                            productionDate = state.productionDate,
                                            onProductionDateChange = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnProductionDateEntered(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            carNoError = state.carNoError,
                                            productionDateError = state.productionDateError,
                                            searchText = state.warehouseSearchText,
                                            onSearchTextChanged = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnWarehouseSearchTextChange(it)) }
                                        )
                                    }
                                }
                            }

                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    ReceivingAddLineButton(
                                        onClick = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnAddButtonClicked) }
                                    )
                                }
                            }

                            itemsIndexed(state.addedItems) { index, item ->
                                ReceivingPackagingMaterialsLineItem(item,
                                    onDelete = {
                                        viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnDeleteAddedMaterialButtonClicked(index))
                                    },
                                    onEdit = {
                                        viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnEditAddedMaterialButtonClicked(index))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.addedItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), accent,
                    { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage?.text.orEmpty()) {
                viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.HideSuccessDialog)
            }
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage?.text.orEmpty()) {
                viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.HideErrorDialog)
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
    if (viewModel.matDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    ReceivingMaterialDialogItem(
                        materialCode = m.materialCode ?: "",
                        materialDescription = m.materialDescription ?: "",
                        uom = m.uom ?: "",
                        orderedQty = m.orderedQuantity.toString(),
                        receivedQty = m.receivedQuantity.toString(),
                        doneLabel = doneLabel,
                        accent = accent,
                        poNumber = ""
                    ) {
                        viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnMaterialSelected(m.materialCode ?: ""))
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    if (state.showChangePoDialog) {
        LaunchedEffect(Unit) {
            focusManager.clearFocus()
        }
        ReceivingBottomSheetDialog(title = stringResource(R.string.car_number), onClose = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnDismissChangePoDialog) }) {
            SearchBox(state.purchaseOrderSearch, { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnPurchaseOrderSearchTextEntered(it)) },
                stringResource(R.string.search_by_number), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            if (state.isAllPurchaseOrdersLoading) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                    itemsIndexed(state.filteredPurchaseOrders) { index, po ->
                        PackagingPurchaseOrderCard(order = po, accent = accent, isMaterialsShowed = state.showMaterialsList && state.showMaterialsListOrderIndex == index,
                            onClick = { viewModel.onEvent(ReceivingPackagingMaterialsFromVendorEvent.OnPurchaseOrderSelected(po.poNumber)) },
                            onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(
                                        ReceivingPackagingMaterialsFromVendorEvent.OnShowMaterialsListButtonClicked(index)
                                    )
                            })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedPackagingMaterialCard(
    mat: PackagingMaterialsPurchaseOrderItem,
    accent: Color,
    doneLabel: String,
    qty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
    carNo: String,
    onCarNoChange: (String) -> Unit,
    productionDate: String,
    onProductionDateChange: (String) -> Unit,
    quantityError: Message? = null,
    warehouseError: Message? = null,
    carNoError: Message? = null,
    productionDateError: Message? = null,
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit
) {
    val ordered = mat.orderedQuantity ?: 0
    val received = mat.receivedQuantity ?: 0
    val progress = if (ordered > 0) received.toDouble() / ordered.toDouble() else 0.0
    val colors = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f)).border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
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
                if (!mat.poNumber.isNullOrBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text("#${mat.poNumber}", color = colors.green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("✕", color = colors.text, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription ?: "", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(doneLabel to mat.receivedQuantity.toString(), stringResource(R.string.required) to mat.orderedQuantity.toString(),
                stringResource(R.string.unit) to (mat.uom ?: ""),).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp, modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = 9.sp)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(3.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(colors.dark)) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction = progress.toFloat().coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(2.dp)).background(accent))
            }
        }
        Spacer(Modifier.height(10.dp))
        Field("${stringResource(R.string.receiving_qty)} (${mat.uom ?: ""})", "", qty, onQtyChange, isNumber = true, center = true)
        if (quantityError != null) Text(quantityError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(R.string.warehouse),
            items = warehouses,
            onItemSelected = onWarehouseSelected,
            accent = accent,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
        if (warehouseError != null) Text(warehouseError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        Field(stringResource(R.string.car_number), stringResource(R.string.car_number_hint), carNo, onCarNoChange, center = true)
        if (carNoError != null) Text(carNoError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        Text(
            stringResource(R.string.production_date),
            color = colors.muted,
            fontSize = dims.fontSizeTitle,
        )
        DatePickerField(
            value = productionDate,
            onDateSelected = onProductionDateChange,
            label = stringResource(R.string.production_date),
            hint = stringResource(R.string.production_date)
        )
        if (productionDateError != null) Text(productionDateError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
    }
}


@Composable
fun ReceivingPackagingMaterialsLineItem(item: PackagingMaterialsLine, onDelete: () -> Unit, onEdit: () -> Unit) {
    val colors = AppTheme.colors
    val accent = GREEN

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.materialCode, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = accent)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = item.receivedQuantity.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colors.text)
                    Spacer(Modifier.width(5.dp))
                    Text(text = item.uom, fontSize = 12.sp, color = colors.muted)
                }
            }
            MarginVertical(5.dp)
            Text(text = item.materialDescription, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.text)
            MarginVertical(5.dp)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceivingChip(stringResource(R.string.warehouse) + ": " + item.warehouseCode)
                    if (item.carNo != null) {
                        ReceivingChip(stringResource(R.string.car_number) + ": " + item.carNo)
                    }
                    if (item.productionDate != null) {
                        ReceivingChip(stringResource(R.string.production_date) + ": " + item.productionDate.take(10))
                    }
                    ReceivingChip(stringResource(R.string.po_number) + ": " + item.purchaseOrder)
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = colors.text)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = colors.danger)
                    }
                }
            }
        }
    }
}
