package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.details_screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.WorkersGroupsSpinner
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.MaterialImageButton
import net.gbs.gbswarehouse.ui.issue.OnHandStockSection
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen.IssuingSelectedBinCard
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen.MatCodeInput
import net.gbs.gbswarehouse.ui.receiving.ReceivingInfoCell
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BinCodeInput
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.ORANGE
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun IssueSalesOrdersDetailsScreen(
    navController: NavController,
    carNo: String
) {
    val viewModel: IssueDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = ORANGE

    LaunchedEffect(carNo) {
        viewModel.onEvent(IssueDetailsEvent.LoadOrderDetails(carNo))
    }

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.issuing),
                subtitle = stringResource(R.string.finished_goods_feed_issuing),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else if (state.error != null && state.order == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.error!!.text, color = colors.text)
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(IssueDetailsEvent.LoadOrderDetails(carNo))
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 14.dp, end = 14.dp, top = 10.dp,
                        bottom = if (state.addedItems.isNotEmpty()) 90.dp else 20.dp
                    )
                ) {
                    // Header
                    item {
                        state.order?.let { order ->
                            Section(label = stringResource(R.string.order_data), accent = accent) {
                                val fields = listOf(
                                    Triple(stringResource(R.string.car_number), order.carNo ?: order.salesOrderNumber ?: "", null),
                                    Triple(stringResource(R.string.items_count), "${order.lines.size} " + stringResource(R.string.items), null),
                                    Triple("Customer", "${order.customerCode} - ${order.customerName}", null),
                                    Triple(stringResource(R.string.shipping_point), order.lines.firstOrNull()?.shippingPointCode ?: "", null),
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = colors.card),
                                    border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = stringResource(R.string.car_number),
                                                    color = colors.muted,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = order.carNo ?: order.salesOrderNumber ?: "",
                                                    color = accent,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = accent.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    text = order.lines.firstOrNull()?.orderType ?: "Sales Order",
                                                    color = accent,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))
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
                                            ReceivingInfoCell(stringResource(R.string.factory_car_description), order.factoryCarDescription ?: "", colors.text)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(10.dp))
                        WorkersGroupsSpinner(
                            hint = stringResource(R.string.select_workers_group),
                            items = state.workersGroups,
                            onItemSelected = {viewModel.onEvent(IssueDetailsEvent.OnWorkersGroupSelected(it))},
                            searchText = state.workersGroupSearchText,
                            onSearchTextChanged = {viewModel.onEvent(IssueDetailsEvent.OnWorkersGroupSearchTextChanged(it))}
                        )
                    }

                    // Material Selection Section (with Bin input inside SelectedIssueMaterialCard)
                    item {
                        Section(label = stringResource(R.string.item_data), accent = accent) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    MatCodeInput(
                                        value = state.materialCodeInput,
                                        onValueChange = {
                                            viewModel.onEvent(IssueDetailsEvent.OnMaterialCodeInputChanged(it))
                                        },
                                        colors = colors
                                    )
                                    if (state.error != null && state.selectedMaterial == null) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(state.error!!.text, color = colors.danger, fontSize = 10.sp)
                                    }
                                }
                                OutlineBtn(stringResource(R.string.select_material), accent) {
                                    viewModel.onEvent(IssueDetailsEvent.OnToggleMaterialDialog)
                                }
                            }

                            
                            if (state.selectedMaterial != null) {
                                Spacer(Modifier.height(8.dp))
                                SelectedIssueMaterialCard(
                                    state = state,
                                    accent = accent,
                                    onBinInputChanged = { viewModel.onEvent(IssueDetailsEvent.OnBinInputChanged(it)) },
                                    onScanBin = { viewModel.onEvent(IssueDetailsEvent.OnScanBin(it)) },
                                    onClearBin = { viewModel.onEvent(IssueDetailsEvent.OnClearBin) },
                                    onStockSearchChanged = { viewModel.onEvent(IssueDetailsEvent.OnStockSearchTextChanged(it)) },
                                    onStockSelected = { viewModel.onEvent(IssueDetailsEvent.OnOnHandStockSelected(it)) },
                                    onQtyChange = { viewModel.onEvent(IssueDetailsEvent.OnQuantityChanged(it)) },
                                    onAdd = { viewModel.onEvent(IssueDetailsEvent.OnAddLine) },
                                    onClear = { viewModel.onEvent(IssueDetailsEvent.OnClearMaterial) },
                                    workersGroups = state.workersGroups,
                                    selectedWorkersGroup = state.selectedWorkersGroup,
                                    workersGroupSearchText = state.workersGroupSearchText,
                                    onWorkersGroupSearchTextChanged = { viewModel.onEvent(IssueDetailsEvent.OnWorkersGroupSearchTextChanged(it)) },
                                    onWorkersGroupSelected = { viewModel.onEvent(IssueDetailsEvent.OnWorkersGroupSelected(it)) },
                                    qtyError = state.quantityError
                                )
                            }
                        }
                    }

                    // Added Items Section
                    if (state.addedItems.isNotEmpty()) {
                        item {
                            Section(label = stringResource(R.string.added_items), accent = accent) {
                                state.addedItems.forEachIndexed { index, line ->
                                    AddedIssueLineItem(
                                        line = line,
                                        accent = accent,
                                        onDelete = { viewModel.onEvent(IssueDetailsEvent.OnDeleteLine(index)) },
                                        onEdit = { viewModel.onEvent(IssueDetailsEvent.OnEditLine(index)) }
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Save Button
        if (state.addedItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    state.successMessage?.text ?: stringResource(R.string.success_save),
                    accent,
                    { viewModel.onEvent(IssueDetailsEvent.OnSave) },
                    saving = state.isSaving
                )
            }
        }

        // Dialogs
        if (state.savingSuccess) {
            SuccessDialog(
                accent = colors.succ,
                message = state.successMessage?.text ?: "",
                loadingButtonText = stringResource(R.string.plc_loading),
                onLoadingClick = {
                    val orderNo = state.order?.lines?.firstOrNull()?.salesOrder ?: state.order?.salesOrder ?: carNo
                    viewModel.onEvent(IssueDetailsEvent.OnDismissDialogs)
                    navController.navigate(Screens.LoadingOrders.createRoute(orderNo))
                },
                onClose = {
                    viewModel.onEvent(IssueDetailsEvent.OnDismissDialogs)
                }
            )
        }
        if (state.savingError != null) {
            ErrorDialog(colors.danger, state.savingError!!.text) {
                viewModel.onEvent(IssueDetailsEvent.OnDismissDialogs)
            }
        }
    }
    if (state.isSaving) {
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
    if (state.showMaterialDialog) {
        BottomSheetDialog(
            title = stringResource(R.string.select_material),
            onClose = { viewModel.onEvent(IssueDetailsEvent.OnToggleMaterialDialog) }
        ) {
            SearchBox(
                state.materialSearchText,
                { viewModel.onEvent(IssueDetailsEvent.OnMaterialSearchTextChanged(it)) },
                stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    FinishedGoodsFeedItemDialogItem(m, accent) {
                        viewModel.onEvent(IssueDetailsEvent.OnMaterialSelected(m))
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun SelectedIssueMaterialCard(
    state: IssueDetailsState,
    accent: Color,
    onBinInputChanged: (String) -> Unit,
    onScanBin: (String) -> Unit,
    onClearBin: () -> Unit,
    onStockSearchChanged: (String) -> Unit,
    onStockSelected: (MaterialOnHandStock) -> Unit,
    onQtyChange: (String) -> Unit,
    onAdd: () -> Unit,
    onClear: () -> Unit,
    workersGroups: List<WorkersGroup>,
    selectedWorkersGroup: WorkersGroup?,
    workersGroupSearchText: String,
    onWorkersGroupSearchTextChanged: (String) -> Unit,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    qtyError: Message? = null
) {
    val colors = AppTheme.colors
    val item = state.selectedMaterial ?: return
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
            val matCode = item.materialCode
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
        Text(item.materialDescription ?: "", color = colors.text, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoCell(stringResource(R.string.required), item.quantity.toString(), accent, Modifier.weight(1f))
            InfoCell(stringResource(R.string.remaining_quantity_), item.remainingQuantity.toString(), accent, Modifier.weight(1f))
            InfoCell(stringResource(R.string.unit), item.uom ?: "", accent, Modifier.weight(1f))
        }
        
        Spacer(Modifier.height(10.dp))
        Text(stringResource(R.string.issuing_location_data), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        BinCodeInput(
            value = state.binInput,
            onValueChange = {
                onBinInputChanged(it)
                if (it.length >= 4) {
                    onScanBin(it)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.binCodeError != null) {
            Text(state.binCodeError!!.text, color = colors.danger, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }

        if (state.binData != null) {
            Spacer(Modifier.height(8.dp))
            IssuingSelectedBinCard(
                bin = state.binData,
                accent = accent,
                colors = colors,
                onClear = onClearBin
            )
        }

        if (state.binData != null) {
            Spacer(Modifier.height(10.dp))
            OnHandStockSection(
                stockList = state.stockOnHandList,
                selectedStock = state.selectedOnHandStock,
                availableQty = state.availableStock,
                isStockLoading = state.isStockLoading,
                stockError = state.stockError,
                stockSearchText = state.stockSearch,
                onSearchTextChanged = onStockSearchChanged,
                onStockSelected = onStockSelected,
                accent = accent,
                colors = colors
            )
        }

        Spacer(Modifier.height(10.dp))
        Field(stringResource(R.string.issued_qty), "0", state.quantity, onQtyChange, isNumber = true, center = true)
        if (qtyError != null) {
            Text(qtyError!!.text, color = colors.danger, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        
        Spacer(Modifier.height(12.dp))
        FullBtn(stringResource(R.string.add), accent, onAdd)
    }
}

@Composable
fun AddedIssueLineItem(line: FinishedGoodsFeedLine, accent: Color, onDelete: () -> Unit, onEdit: () -> Unit) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(line.materialCode, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${line.receivedQuantity} ${line.uom}", color = colors.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                FlowRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip(stringResource(R.string.bin) + ": " + line.warehouseCode)
                    if (line.workerGroupName != null) {
                        Chip(stringResource(R.string.workers_group) + ": " + line.workerGroupName)
                    }
                    if (line.batch != null) {
                        Chip(stringResource(R.string.batch) + ": " + line.batch)
                    }
                    if (line.valuationType != null) {
                        Chip(stringResource(R.string.valuation_type) + ": " + line.valuationType)
                    }
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

@Composable
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = ORANGE.copy(.10f)) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp, color = ORANGE, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun FinishedGoodsFeedItemDialogItem(item: FinishedGoodsFeedItem, accent: Color, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(colors.card).border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.materialCode ?: "", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("#${item.salesOrder ?: ""}", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(item.materialDescription ?: "", color = colors.text, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text("${stringResource(R.string.remaining_quantity_)}: ${item.remainingQuantity}", color = colors.muted, fontSize = 10.sp)
    }
}
