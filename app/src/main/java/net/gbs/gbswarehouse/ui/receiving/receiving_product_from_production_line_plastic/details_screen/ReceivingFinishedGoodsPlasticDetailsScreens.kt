package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.WorkersGroupsSpinner
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list.FinishedGoodsPlasticProductionOrderCard
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingFinishedGoodsPlasticDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: ReceivingFinishedGoodsPlasticDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = LocalFocusManager.current
    LaunchedEffect(state.orderGetDataStatus) {
        if (state.orderGetDataStatus is FinishedGoodsPlasticOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnScreenLoaded(orderId))
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
                subtitle = stringResource(R.string.finished_good_from_production),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.orderGetDataStatus) {
                FinishedGoodsPlasticOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is FinishedGoodsPlasticOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError, color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnScreenLoaded(orderId))
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
                                bottom = if (state.isReadyToReceive) 90.dp else 20.dp
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
//                                            viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnChangeOrderButtonClicked)
//                                        }
                                    }
                                }
                            }

                            item {
                                Section(label = stringResource(R.string.item_data), accent = accent) {
/*                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            MatCodeInput(
                                                value = state.materialCodeInput,
                                                onValueChange = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialCodeInputEntered(it)) },
                                                onKeyboardAction = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialSelected(state.materialCodeInput)) }
                                            )
                                            if (state.materialCodeError.isNotEmpty()){
                                                Text(state.materialCodeError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                                            }
                                        }
                                        OutlineBtn(stringResource(R.string.select_item), accent) { viewModel.matDlg = true }
                                    } */
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedFinishedGoodsPlasticMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnWarehouseSelected(it)) },
                                            warehouseSearchText = state.warehouseSearchText,
                                            onWarehouseSearchTextChanged = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnWarehouseSearchTextChanged(it)) },
                                            workersGroups = state.workersGroups,
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnWorkersGroupSelected(it)) },
                                            workersGroupSearchText = state.workersGroupSearchText,
                                            onWorkersGroupSearchTextChanged = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnWorkersGroupSearchTextChanged(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnUnloadingPointEntered(it)) },
                                            naolon = state.naolon,
                                            onNaolonChange = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnNaolonEntered(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            workersGroupError = state.workersGroupError,
                                            unloadingPointError = state.unloadingPointError,
                                            naolonError = state.naolonError
                                        )
                                    }
                                }
                            }

//                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()&&state.selectedWarehouse!=null&&state.selectedWorkersGroup!=null&&state.unloadingPoint!=null) {
//                                item {
//                                    Spacer(Modifier.height(10.dp))
//                                    AddFinishedGoodLineButton(
//                                        onClick = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnAddButtonClicked) }
//                                    )
//                                }
//                            }
//
//                            itemsIndexed(state.addedItems) { index, item ->
//                                ReceivingFinishedGoodsPlasticLineItem(item) {
//                                    viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnDeleteAddedMaterialButtonClicked(index))
//                                }
//                            }
                        }
                    }
                }
            }
        }

        if (state.selectedMaterial!=null&& state.quantity.isNotEmpty()&&state.selectedWarehouse!=null&&state.selectedWorkersGroup!=null&&state.unloadingPoint.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), accent,
                    { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }
        
        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage) {
                viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.HideSuccessDialog)
            }
            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage) {
                viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    FinishedGoodsPlasticMaterialDialogItem(m, accent) {
                        viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnMaterialSelected(m.materialCode ?: ""))
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
        BottomSheetDialog(title = stringResource(R.string.po_number), onClose = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnDismissChangeOrderDialog) }) {
            SearchBox(state.orderSearch, { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnOrderSearchTextEntered(it)) },
                stringResource(R.string.search_by_number), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            if (state.isAllOrdersLoading) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                    itemsIndexed(state.filteredOrders) { _, ord ->
                        FinishedGoodsPlasticProductionOrderCard(order = ord, accent = accent,
                            onClick = { viewModel.onEvent(ReceivingFinishedGoodsPlasticEvent.OnOrderSelected(ord.orderNumber)) },
                            onItemsNumberButtonClicked = { })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedFinishedGoodsPlasticMaterialCard(
    mat: FinishedGoodsPlasticProductionOrderItem,
    accent: Color,
    doneLabel: String,
    qty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
    warehouseSearchText: String,
    onWarehouseSearchTextChanged: (String) -> Unit,
    workersGroups: List<WorkersGroup>,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    workersGroupSearchText: String,
    onWorkersGroupSearchTextChanged: (String) -> Unit,
    unloadingPoint: String,
    onUnloadingPointChange: (String) -> Unit,
    naolon: String,
    onNaolonChange: (String) -> Unit,
    quantityError: String = "",
    warehouseError: String = "",
    workersGroupError: String = "",
    unloadingPointError: String = "",
    naolonError: String = ""
) {
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                stringResource(R.string.received_qty) to mat.receivedQuantity.toString(),
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
        if (quantityError.isNotEmpty()) Text(quantityError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(R.string.warehouse),
            items = warehouses,
            onItemSelected = onWarehouseSelected,
            accent = accent,
            searchText = warehouseSearchText,
            onSearchTextChanged = onWarehouseSearchTextChanged
        )
        if (warehouseError.isNotEmpty()) Text(warehouseError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        net.gbs.gbswarehouse.WorkersGroupsSpinner(
            hint = stringResource(R.string.please_select_workers_group),
            items = workersGroups,
            onItemSelected = onWorkersGroupSelected,
            accent = accent,
            searchText = workersGroupSearchText,
            onSearchTextChanged = onWorkersGroupSearchTextChanged
        )
        if (workersGroupError.isNotEmpty()) Text(workersGroupError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))

        Spacer(Modifier.height(10.dp))
        Field(stringResource(R.string.unloading_point),
            stringResource(R.string.enter_unloading_point), unloadingPoint, onUnloadingPointChange, center = true)
        if (unloadingPointError.isNotEmpty()) Text(unloadingPointError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))

//        Spacer(Modifier.height(10.dp))
//        Field(stringResource(R.string.naolon),
//            stringResource(R.string.enter_shipping_cost), naolon, onNaolonChange, isNumber = true, center = true)
//        if (naolonError.isNotEmpty()) Text(naolonError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        Field(stringResource(R.string.pallet_weight),
            stringResource(R.string.enter_pallet_weight_optional), naolon, onNaolonChange, isNumber = true, center = true)
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
fun ReceivingFinishedGoodsPlasticLineItem(item: FinishedGoodsPlasticLine, onDelete: () -> Unit) {
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
                Column(Modifier.weight(1f)) {
                    Chip(stringResource(R.string.warehouse) + ": " + item.warehouseCode)
                    if (item.workerGroupName != null) {
                        Spacer(Modifier.height(4.dp))
                        Chip(stringResource(R.string.operations) + ": " + item.workerGroupName)
                    }
                    if (item.unloadingPoint != null) {
                        Spacer(Modifier.height(4.dp))
                        Chip(stringResource(R.string.unloading_point) + ": " + item.unloadingPoint)
                    }
                    if (item.naolon != null) {
                        Spacer(Modifier.height(4.dp))
                        Chip(stringResource(R.string.naolon) + ": " + item.naolon)
                    }
                    Spacer(Modifier.height(4.dp))
                    Chip("Order: " + item.orderNumber)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
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
fun FinishedGoodsPlasticMaterialDialogItem(mat: FinishedGoodsPlasticProductionOrderItem, accent: Color, onClick: () -> Unit) {
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
            Text("${stringResource(R.string.required)}: ${mat.remainingQuantity}", color = colors.muted, fontSize = 10.sp)
        }
    }
}
