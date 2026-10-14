package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WorkersGroupsSpinner
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.issue.OnHandStockSection
import net.gbs.gbswarehouse.ui.issue.WarehousesSpinner
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.ScannerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuingDirectTransferScreen(navController: NavController) {
    val viewModel: IssuingDirectTransferViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val accent = colors.yellow

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.issuing),
                subtitle = stringResource(R.string.direct_transfer),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding,
                    end = dims.padding,
                    top = 10.dp,
                    bottom = if (state.addedLines.isNotEmpty()) dims.bottomBarHeight else 20.dp
                )
            ) {
                // 1. From Bin Section
                item {
                    Section(label = stringResource(R.string.issued_from_warehouse_location_data), accent = accent) {
                        BinCodeInput(
                            value = state.binFromInput,
                            onValueChange = {
                                viewModel.onEvent(IssuingDirectTransferEvent.OnBinFromInputChanged(it))
                                if (it.length >= 4) viewModel.onEvent(IssuingDirectTransferEvent.OnScanBinFrom(it))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.binFromError != null) {
                            Text(
                                state.binFromError!!.text,
                                color = colors.danger,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (state.binFromData != null) {
                            Spacer(Modifier.height(8.dp))
                            IssuingSelectedBinCard(
                                bin = state.binFromData!!,
                                accent = accent,
                                onClear = { viewModel.onEvent(IssuingDirectTransferEvent.OnClearBinFrom) }
                            )
                        }
                    }
                }

                // 2. Material Section
                if (state.binFromData != null) {
                    item {
                        Section(label = stringResource(R.string.item_data), accent = accent) {
                            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp).height(12.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(accent)
                                    )
                                    Text(
                                        stringResource(R.string.select_material),
                                        color = accent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                                SearchableSpinner(
                                    hint = stringResource(R.string.select_material),
                                    items = state.groupedMaterials,
                                    itemLabel = {
                                        if (it.materialName.isNotEmpty()) "${it.materialName} (${it.materialCode})" else it.materialCode
                                    },
                                    onItemSelected = { viewModel.onEvent(IssuingDirectTransferEvent.OnMaterialSelected(it)) },
                                    searchText = state.materialSearchText,
                                    onSearchTextChanged = { viewModel.onEvent(IssuingDirectTransferEvent.OnMaterialSearchTextChanged(it)) }
                                )
                            }
                            if (state.matErrorMessage != null) {
                                Text(
                                    state.matErrorMessage!!.text,
                                    color = colors.danger,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Stock / Batch / Valuation / Quantity Section
                if (state.selectedMaterial != null) {
                    val stocks = state.selectedMaterial!!.stocks
                    val isBatchManaged = stocks.any { it.isBatchManaged == true }
                    val hasValuationType = !isBatchManaged && stocks.any { !it.valuationType.isNullOrEmpty() }

                    item {
                        OnHandStockSection(
                            stockList = stocks,
                            selectedStock = state.selectedOnHandStock,
                            availableQty = state.availableQty,
                            isStockLoading = state.isStockLoading,
                            stockError = state.stockError,
                            stockSearchText = state.stockSearchText,
                            onSearchTextChanged = { viewModel.onEvent(IssuingDirectTransferEvent.OnStockSearchTextChanged(it)) },
                            onStockSelected = { viewModel.onEvent(IssuingDirectTransferEvent.OnStockSelected(it)) },
                            accent = accent
                        )
                    }

                    // Show Quantity Input when ready
                    if (state.selectedOnHandStock != null || (!isBatchManaged && !hasValuationType)) {
                        item {
                            MarginTenDp()
                            Field(
                                label = "${stringResource(R.string.qty_)} (${state.availableQty})",
                                hint = stringResource(R.string.enter_quantity),
                                value = state.quantity,
                                onValueChange = { viewModel.onEvent(IssuingDirectTransferEvent.OnQuantityChanged(it)) },
                                isNumber = true,
                                withLabel = true
                            )
                            if (state.quantityError != null) {
                                Text(state.quantityError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle)
                            }
                        }

                        // Destination Section
                        item {
                            MarginTenDp()
                            Section(label = stringResource(R.string.issued_to_warehouse_location_data), accent = accent) {
                                WarehousesSpinner(
                                    items = state.productionWarehouses,
                                    onItemSelected = {
                                        viewModel.onEvent(IssuingDirectTransferEvent.OnProductionWarehouseSelected(it))
                                    },
                                    searchText = state.warehouseSearch,
                                    onSearchTextChanged = {
                                        viewModel.onEvent(IssuingDirectTransferEvent.OnWarehouseSearchTextChanged(it))
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(Modifier.height(10.dp))
                            WorkersGroupsSpinner(
                                label = stringResource(R.string.workers_group),
                                hint = stringResource(R.string.select_workers_group),
                                items = state.workersGroupsList,
                                onItemSelected = { viewModel.onEvent(IssuingDirectTransferEvent.OnWorkersGroupSelected(it)) },
                                accent = accent,
                                searchText = state.workersGroupSearch,
                                onSearchTextChanged = { viewModel.onEvent(IssuingDirectTransferEvent.OnWorkersGroupTextChange(it)) },
                            )
                            if (state.workersGroupError != null) Text(state.workersGroupError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle, modifier = Modifier.padding(top = 4.dp))
                            Spacer(Modifier.height(10.dp))
                            WorkersGroupsSpinner(
                                label = stringResource(R.string.aging_group),
                                hint = stringResource(R.string.select_aging_group),
                                items = state.workersGroupsList,
                                onItemSelected = { viewModel.onEvent(IssuingDirectTransferEvent.OnAgingGroupSelected(it)) },
                                accent = accent,
                                searchText = state.agingGroupSearch,
                                onSearchTextChanged = { viewModel.onEvent(IssuingDirectTransferEvent.OnAgingGroupTextChange(it)) },
                            )
                            if (state.agingGroupError != null) Text(state.agingGroupError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle, modifier = Modifier.padding(top = 4.dp))
                        }

                        item {
                            Spacer(Modifier.height(16.dp))
                            FullBtn(
                                label = stringResource(R.string.add),
                                color = accent,
                                onClick = { viewModel.onEvent(IssuingDirectTransferEvent.OnAddLine) },
                                enabled = state.selectedProductionWarehouse != null && state.quantity.isNotEmpty() && state.quantityError == null
                            )
                        }
                    }
                }

                // 4. Added Items List
                if (state.addedLines.isNotEmpty()) {
                    item {
                        Section(label = stringResource(R.string.added_items), accent = accent) {
                            state.addedLines.forEachIndexed { index, line ->
                                AddedLineItem(
                                    materialCode = line.materialCode,
                                    qty = line.quantity,
                                    uom = line.uom,
                                    bin = line.storageBinCode,
                                    batch = line.batch,
                                    valuationType = line.valuationType,
                                    accent = accent,
                                    onDelete = { viewModel.onEvent(IssuingDirectTransferEvent.OnDeleteLine(index)) },
                                    onEdit = { viewModel.onEvent(IssuingDirectTransferEvent.OnEditLine(index)) },
                                    warehouseToCode = line.receivingWarehouseCode,
                                    workerGroupCode = line.workerGroupCode,
                                    agingGroupCode = line.agingGroupCode
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Fixed Save Button
        if (state.addedLines.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent, 0.35f to colors.bg
                        )
                    )
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_issuing),
                    accent,
                    { viewModel.onEvent(IssuingDirectTransferEvent.OnIssueAll) },
                    saving = state.isSaving
                )
            }
        }

        if (state.savingSuccessMessage != null) {
            SuccessDialog(accent = colors.green, message = state.savingSuccessMessage!!.text, onClose = {
                viewModel.onEvent(IssuingDirectTransferEvent.OnDismissDialogs)
            })
        }
//        LaunchedEffect(state.savingSuccessMessage) {
//            if (state.savingSuccessMessage != null) {
//                delay(1000)
//                navController.popBackStack()
//            }
//        }
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
        if (state.savingErrorMessage != null) {
            ErrorDialog(accent = colors.danger, message = state.savingErrorMessage!!.text, onClose = {
                viewModel.onEvent(IssuingDirectTransferEvent.OnDismissDialogs)
            })
        }
    }
}

@Composable
fun IssuingSelectedBinCard(bin: StorageBinLocation, accent: Color, onClear: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f))
            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📍 ${bin.storageBinCode}", color = accent, fontSize = dims.fontSizeTitle, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = colors.muted,
                    modifier = Modifier.padding(4.dp).size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddedLineItem(
    materialCode: String,
    qty: Double,
    uom: String,
    bin: String,
    batch: String?,
    valuationType: String?,
    accent: Color,
    warehouseToCode: String,
    workerGroupCode: String?,
    agingGroupCode: String?,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(materialCode, color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
            Text("$qty $uom", color = colors.text, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DirectTransferChip(stringResource(R.string.bin_from) + " $bin", accent)
                DirectTransferChip(stringResource(R.string.warehouse_to_code) + " $warehouseToCode", accent)
                if (!batch.isNullOrEmpty()) DirectTransferChip(stringResource(R.string.batch) + ": $batch", accent)
                if (!valuationType.isNullOrEmpty()) DirectTransferChip(stringResource(R.string.valuation_type_) + " $valuationType", accent)
                if (!workerGroupCode.isNullOrEmpty()) DirectTransferChip(stringResource(R.string.workers_group) + ": $workerGroupCode", accent)
                if (!agingGroupCode.isNullOrEmpty()) DirectTransferChip(stringResource(R.string.aging_group) + ": $agingGroupCode", accent)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = colors.text)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colors.danger
                    )
                }
            }
        }
    }
}

@Composable
fun DirectTransferChip(text: String, accent: Color) {
    val dims = AppTheme.dims
    Surface(
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = 0.1f),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = dims.fontSizeSubtitle,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BinCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = colors.text,
                fontSize = dims.fontSizeButton,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(colors.yellow),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(stringResource(R.string.bin_code_hint), color = colors.muted, fontSize = dims.fontSizeButton)
                inner()
            }
        )
    }
}
