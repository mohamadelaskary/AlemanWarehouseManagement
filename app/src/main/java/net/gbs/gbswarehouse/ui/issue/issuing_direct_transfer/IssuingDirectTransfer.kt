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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehouseToTypeSelector
import net.gbs.gbswarehouse.directTransferWarehouseToTypes
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.ui.issue.BatchValuationTypeSpinner
import net.gbs.gbswarehouse.ui.issue.WarehousesSpinner
import net.gbs.gbswarehouse.ui.theme.AppColors
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.BORDER2
import net.gbs.gbswarehouse.ui.theme.DARK
import net.gbs.gbswarehouse.ui.theme.MUTED
import net.gbs.gbswarehouse.ui.theme.ORANGE
import net.gbs.gbswarehouse.ui.theme.TEXT
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun IssuingDirectTransferScreen(navController: NavController) {
    val viewModel: IssuingDirectTransferViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.yellow

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = stringResource(R.string.issuing), subtitle = stringResource(R.string.direct_transfer),
                accent = accent, onBack = {
                    navController.popBackStack()
                })

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 10.dp,
                    bottom = if (state.addedLines.isNotEmpty()) 90.dp else 20.dp
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
                        if (!state.binFromError.isNullOrEmpty()){
                            Text(state.binFromError?:"", color = colors.danger, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
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
                            MatCodeInput(
                                value = state.matInput,
                                onValueChange = {
                                    viewModel.onEvent(IssuingDirectTransferEvent.OnMatInputChanged(it))
                                    if (it.length >= 4) viewModel.onEvent(IssuingDirectTransferEvent.OnScanMaterial(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = colors
                            )
                            if (!state.matErrorMessage.isNullOrEmpty()){
                                Text(state.matErrorMessage?:"", color = colors.danger, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                            if (state.selectedMatStock != null || state.stockList.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                IssuingSelectedMaterialCard(
                                    state = state,
                                    accent = accent,
                                    viewModel = viewModel,
                                    onClear = { viewModel.onEvent(IssuingDirectTransferEvent.OnClearMaterial) }
                                )
                            }
                        }
                    }
                }

                // 3. Destination Section
                if (state.selectedMatStock != null) {
                    item {
                        WarehouseToTypeSelector(
                            directTransferWarehouseToTypes,
                            onItemSelected = {
                                viewModel.onEvent(IssuingDirectTransferEvent.OnWarehouseTypeSelected(it))
                            }
                        )
                    }

                    item {
                        MarginTenDp()
                        Section(label = stringResource(R.string.issued_to_warehouse_location_data), accent = accent) {
                            if (state.warehouseType.id == "1") {
                                BinCodeInput(
                                    value = state.binToInput,
                                    onValueChange = {
                                        viewModel.onEvent(IssuingDirectTransferEvent.OnBinToInputChanged(it))
                                        if (it.length >= 4) viewModel.onEvent(IssuingDirectTransferEvent.OnScanBinTo(it))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (!state.binToError.isNullOrEmpty()){
                                    Text(state.binToError?:"", color = colors.danger, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                }
                                if (state.binToData != null) {
                                    Spacer(Modifier.height(8.dp))
                                    IssuingSelectedBinCard(
                                        bin = state.binToData!!,
                                        accent = accent,
                                        onClear = { viewModel.onEvent(IssuingDirectTransferEvent.OnClearBinTo) }
                                    )
                                }
                            } else {
                                WarehousesSpinner(
                                    items = state.productionWarehouses,
                                    onItemSelected = {
                                        viewModel.onEvent(IssuingDirectTransferEvent.OnProductionWarehouseSelected(it))
                                    },
                                    onSearchTextChanged = {}
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(Modifier.height(16.dp))
                        FullBtn(
                            label = stringResource(R.string.add),
                            color = accent,
                            onClick = { viewModel.onEvent(IssuingDirectTransferEvent.OnAddLine) },
                            enabled = (state.binToData != null || state.selectedProductionWarehouse != null) && state.quantity.isNotEmpty() && state.quantityError == null
                        )
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
                                    warehouseToCode = line.receivingWarehouseCode
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── Fixed Save Button ─────────────────────────────────────────────
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
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_issuing), accent, { viewModel.onEvent(IssuingDirectTransferEvent.OnIssueAll) },
                    saving = state.isSaving
                )
            }
        }
        
        if (state.savingSuccessMessage != null) {
            SuccessDialog(accent = colors.green, message = state.savingSuccessMessage!!, onClose = {
                viewModel.onEvent(IssuingDirectTransferEvent.OnDismissDialogs)
            })
        }
        if (state.savingErrorMessage != null) {
            ErrorDialog(accent = colors.danger, message = state.savingErrorMessage!!, onClose = {
                viewModel.onEvent(IssuingDirectTransferEvent.OnDismissDialogs)
            })
        }
    }
}

@Composable
fun IssuingSelectedBinCard(bin: StorageBinLocation, accent: Color, onClear: () -> Unit) {
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
            Text("📍 ${bin.storageBinCode}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
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
fun IssuingSelectedMaterialCard(
    state: IssuingDirectTransferState,
    accent: Color,
    viewModel: IssuingDirectTransferViewModel,
    onClear: () -> Unit,
) {
    val colors = AppTheme.colors
    val mat = state.selectedMatStock ?: state.stockList.firstOrNull() ?: return
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.card)
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoCell(
                label = stringResource(R.string.material_code),
                value = mat.materialCode?:"",
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = colors.muted
                )
            }
        }
        
        if (state.stockList.isNotEmpty()) {
            MarginTenDp()
            BatchValuationTypeSpinner(
                items = state.stockList,
                onItemSelected = { viewModel.onEvent(IssuingDirectTransferEvent.OnStockSelected(it)) },
                accent = accent,
                searchText = state.stockSearch,
                onSearchTextChanged = { viewModel.onEvent(IssuingDirectTransferEvent.OnStockSearchTextChanged(it)) },
                isBatchManaged = true 
            )
        }
        
        if (state.selectedMatStock != null) {
            MarginTenDp()
            Field(
                label = "${stringResource(R.string.issued_qty)} (${mat.quantity})",
                hint = stringResource(R.string.enter_quantity),
                value = state.quantity,
                onValueChange = { viewModel.onEvent(IssuingDirectTransferEvent.OnQuantityChanged(it)) },
                isNumber = true,
                withLabel = true
            )
            if (state.quantityError != null) {
                Text(state.quantityError, color = colors.danger, fontSize = 10.sp)
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
    warehouseToCode:String,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(materialCode, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$qty $uom", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

@Composable
fun DirectTransferChip(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = 0.1f),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
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
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,colors: AppColors) {
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
            cursorBrush = SolidColor(ORANGE),
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
