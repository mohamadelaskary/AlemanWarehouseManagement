package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.WorksGroupSpinner
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.FinishedGoodsFeedProductionOrderCard
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingFinishedGoodFromProductionDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: ReceivingFinishedGoodsFeedDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = LocalFocusManager.current
    LaunchedEffect(state.orderGetDataStatus) {
        if (state.orderGetDataStatus is FinishedGoodsFeedOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnScreenLoaded(orderId))
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
                subtitle = stringResource(R.string.finished_good_from_production_feed),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.orderGetDataStatus) {
                FinishedGoodsFeedOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is FinishedGoodsFeedOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError?.text.orEmpty(), color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnScreenLoaded(orderId))
                            }
                        }
                    }
                }
                else -> {
                    val order = state.productionOrder
                    if (order != null) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 14.dp, end = 14.dp, top = 10.dp,
                                bottom = 20.dp
                            )
                        ) {
                            item {
                                Section(label = stringResource(R.string.order_data), accent = accent) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        InfoCell(stringResource(R.string.production_order_number_), order.orderNumber, accent, Modifier.weight(1f))
//                                        OutlineBtn(stringResource(R.string.change_po), accent) {
//                                            viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnChangeOrderButtonClicked)
//                                        }
                                    }
                                }
                            }

                            item {
                                Section(label = stringResource(R.string.item_data), accent = accent) {
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedFinishedGoodsFeedMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnWarehouseSelected(it)) },
                                            workersGroups = state.workersGroups,
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnWorkersGroupSelected(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            workersGroupError = state.workersGroupError,
                                            warehouseSearchText = state.warehouseSearchText,
                                            onWarehouseSearchTextChanged = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnWarehouseSearchTextChanged(it)) },
                                            workersGroupSearchText = state.workersGroupSearchText,
                                            onWorkersGroupSearchTextChanged = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnWorkersGroupSearchTextChanged(it)) },
                                            state = state
                                        )
                                    }
                                }
                            }

                            // A finished-goods feed order only ever has a single material, so there is no added-lines
// list to build up: the form is filled in and saved straight away.
//                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
//                                item {
//                                    Spacer(Modifier.height(10.dp))
//                                    AddFinishedGoodLineButton(
//                                        onClick = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnAddButtonClicked) }
//                                    )
//                                }
//                            }
//
//                            itemsIndexed(state.addedItems) { index, item ->
//                                ReceivingFinishedGoodsFeedLineItem(item) {
//                                    viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnDeleteAddedMaterialButtonClicked(index))
//                                }
//                            }
                        }
                    }
                }
            }
        }

        if (state.isReadyToReceive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), accent,
                    { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }
//        if (state.savingErrorStatusMessage.isNotEmpty()){
//            navController.popBackStack()
//        }
        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage?.text.orEmpty()) {
                viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.HideSuccessDialog)
            }
        }
//        LaunchedEffect(state.showSuccessDialog) {
//            if (state.showSuccessDialog) {
//                delay(1000)
//                navController.popBackStack()
//            }
//        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage?.text.orEmpty()) {
                viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.HideErrorDialog)
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
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    FinishedGoodsFeedMaterialDialogItem(m, accent, doneLabel) {
                        viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnMaterialSelected(m.materialCode ?: ""))
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    if (state.showChangeOrderDialog) {
        LaunchedEffect(Unit) {
            focusManager.clearFocus()
        }
        BottomSheetDialog(title = stringResource(R.string.po_number), onClose = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnDismissChangeOrderDialog) }) {
            SearchBox(state.orderSearch, { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnOrderSearchTextEntered(it)) },
                stringResource(R.string.search_by_number), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            if (state.isAllOrdersLoading) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                    itemsIndexed(state.filteredOrders) { _, ord ->
                        FinishedGoodsFeedProductionOrderCard(order = ord, accent = accent,
                            onClick = { viewModel.onEvent(ReceivingFinishedGoodsFeedEvent.OnOrderSelected(ord.orderNumber)) },
                            onItemsNumberButtonClicked = { })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedFinishedGoodsFeedMaterialCard(
    mat: FinishedGoodsFeedProductionOrderItem,
    accent: Color,
    doneLabel: String,
    qty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
    workersGroups: List<WorkersGroup>,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    quantityError: Message? = null,
    warehouseError: Message? = null,
    workersGroupError: Message? = null,
    warehouseSearchText: String,
    onWarehouseSearchTextChanged: (String) -> Unit,
    workersGroupSearchText: String,
    onWorkersGroupSearchTextChanged: (String) -> Unit,
    state: ReceivingFinishedGoodsFeedState
) {
    val progress = 0.0 // Feed production items don't have received quantity in model
    val colors = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f)).border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically) {
            Text(mat.materialCode ?: "", color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
//            Text("✕", color = colors.muted, fontSize = 12.sp,
//                modifier = Modifier.clickable { onClear() }.padding(4.dp))
//        }
        Spacer(Modifier.height(4.dp))
        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(stringResource(R.string.received_qty) to mat.receivedQuantity.toString(),
                stringResource(R.string.required) to mat.orderedQuantity.toString(),
                stringResource(R.string.unit) to (mat.uom ?: ""),).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp, modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Field("${stringResource(R.string.receiving_qty)} (${mat.uom ?: ""})", "", qty, onQtyChange, isNumber = true, center = true)
        if (quantityError != null) Text(quantityError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))

        if (state.selectedWarehouse==null) {
            Spacer(Modifier.height(10.dp))
            WarehousesSpinner(
                hint = stringResource(R.string.warehouse),
                items = warehouses,
                onItemSelected = onWarehouseSelected,
                accent = accent,
                searchText = warehouseSearchText,
                onSearchTextChanged = onWarehouseSearchTextChanged
            )
        } else {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.bg.copy(alpha = 0.65f)).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.weight(1f)){
                        Text(stringResource(R.string.warhouse_code),color = colors.muted, fontSize = 12.sp)
                        Text(state.selectedWarehouse.warehouseCode, color = accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.weight(1f)){
                        Text(stringResource(R.string.warehouse_),color = colors.muted, fontSize = 12.sp)
                        Text(state.selectedWarehouse.warehouseDescription, color = accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
        if (warehouseError != null) Text(warehouseError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        net.gbs.gbswarehouse.WorkersGroupsSpinner(
            hint = stringResource(R.string.please_select_workers_group),
            items = workersGroups,
            onItemSelected = onWorkersGroupSelected,
            searchText = workersGroupSearchText,
            onSearchTextChanged = onWorkersGroupSearchTextChanged
        )
        if (workersGroupError != null) Text(workersGroupError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun AddFinishedGoodLineButton(onClick: () -> Unit) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(colors.addMaterialButtonBackground).border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.add), color = colors.addMaterialButtonTextColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun ReceivingFinishedGoodsFeedLineItem(item: FinishedGoodsFeedLine, onDelete: () -> Unit) {
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(stringResource(R.string.warehouse) + ": " + item.warehouseCode)
                    if (item.workerGroupName != null) {
                        Spacer(Modifier.height(4.dp))
                        Chip(stringResource(R.string.workers_group) + ": " + item.workerGroupName)
                    }
                    Spacer(Modifier.height(4.dp))
                    Chip(stringResource(R.string.production_order_number_) + item.orderNumber)
                }
                Row {
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = colors.danger)
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = GREEN.copy(.10f)) {
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontSize = 12.sp, color = GREEN, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun FinishedGoodsFeedMaterialDialogItem(mat: FinishedGoodsFeedProductionOrderItem, accent: Color, doneLabel: String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(colors.dark).border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(mat.materialCode ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(mat.uom ?: "", color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("${stringResource(R.string.required)}: ${mat.orderedQuantity}", color = colors.muted, fontSize = 10.sp)
        }
    }
}
