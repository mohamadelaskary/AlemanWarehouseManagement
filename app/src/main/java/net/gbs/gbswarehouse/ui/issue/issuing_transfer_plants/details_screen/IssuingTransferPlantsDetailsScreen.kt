package net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.details_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.*
import net.gbs.gbswarehouse.data.remote.dto.request.IssueTransferOrderLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferOrderItem
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.issue.OnHandStockSection
import net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.details_screen.IssuingTransferPlantsDetailsEvent.*
import net.gbs.gbswarehouse.ui.receiving.ReceivingInfoCell
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.BinCodeInput
import net.gbs.gbswarehouse.ui.theme.AppColors
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.gbswarehouse.ui.theme.GREEN
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun IssuingTransferPlantsDetailsScreen(
    navController: NavController,
    carNo: String
) {
    val viewModel: IssuingTransferPlantsDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dims = AppTheme.dims
    val colors = AppTheme.colors
    val accent = ORANGE

    ScannerEffect(onScan = viewModel::onBarcodeScanned)
    LaunchedEffect(carNo) {
        viewModel.onEvent(LoadOrderDetails(carNo))
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.issuing),
                subtitle = stringResource(R.string.warehouse_transfers_between_plants_outcoming),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            } else if (state.loadingErrorMessage != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column {
                        Text(
                            state.loadingErrorMessage!!.text,
                            color = Color.Red,
                            modifier = Modifier.padding(20.dp)
                        )
                        ElevatedButton(
                            onClick = { viewModel.onEvent(LoadOrderDetails(carNo)) },
                            modifier = Modifier.padding(20.dp),
                            border = BorderStroke(1.dp, colors.border),
                            content = { Text(stringResource(R.string.reload)) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = dims.padding, end = dims.padding,
                        top = 10.dp,
                        bottom = if (state.addedLines.isNotEmpty()) dims.bottomBarHeight else 20.dp
                    )
                ) {
                    if (state.transferOrder != null) {
                        val ord = state.transferOrder!!
                        item {
                            Section(label = "Transfer Order Details", accent = accent) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = colors.card),
                                    border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        val fields = listOf(
                                            Triple(stringResource(R.string.car_number), ord.carNo ?: ord.transferOrderNumber ?: "", null),
                                            Triple(stringResource(R.string.items_count), "${ord.lines.size} " + stringResource(R.string.items), null),
                                            Triple("Issuing Plant", ord.issuingPlantDescription ?: ord.lines.firstOrNull()?.issuingPlantCode ?: "", null),
                                            Triple("Receiving Plant", ord.lines.firstOrNull()?.receivingPlantDescription ?: ord.lines.firstOrNull()?.receivingPlantCode ?: "", null),
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = ord.transferOrderNumber ?: "",
                                                    color = colors.muted,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "#${ord.carNo ?: ord.transferOrderNumber ?: ""}",
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
                                                    text = "TRANSFER",
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
                                        if (!ord.factoryCarDescription.isNullOrBlank()) {
                                            Spacer(Modifier.height(4.dp))
                                            ReceivingInfoCell(stringResource(R.string.factory_car_description), ord.factoryCarDescription ?: "", colors.text)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }

                    item {
                        Spacer(Modifier.height(10.dp))
                        Field(stringResource(R.string.destination) + " *",
                            stringResource(R.string.enter_destination), state.destination,
                            {
                                viewModel.onEvent(OnDestinationChanged(it))
                            })
                        if (state.destinationError != null) {
                            Text(state.destinationError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    item {
                        Section(label = stringResource(R.string.item_data), accent = accent) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MatCodeInput(
                                        value = state.matInput,
                                        onValueChange = {
                                            viewModel.onEvent(OnMaterialSearchTextChanged(it))
                                        }
                                    )
                                    if (state.matErrorMessage != null) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                            Text(state.matErrorMessage!!.text, color = colors.danger)
                                        }
                                    }
                                }
                                OutlineBtn("اختر مادة", accent) { viewModel.onEvent(OnToggleItemDialog) }
                            }
                            if (state.selItem != null) {
                                Spacer(Modifier.height(8.dp))
                                IssuingSelectedTransferItemCard(
                                    state = state,
                                    accent = accent,
                                    colors = colors,
                                    onBinInputChanged = { viewModel.onEvent(OnBinInputChanged(it)) },
                                    onScanBin = { viewModel.onEvent(OnScanBin(it)) },
                                    onClearBin = { viewModel.onEvent(OnClearBin) },
                                    onStockSearchChanged = { viewModel.onEvent(OnStockSearchTextChanged(it)) },
                                    onStockSelected = { viewModel.onEvent(OnOnHandStockSelected(it)) },
                                    onWorkersGroupSelected = { viewModel.onEvent(OnWorkersGroupSelected(it)) },
                                    onWorkersGroupSearchChanged = { viewModel.onEvent(OnWorkersGroupSearchTextChanged(it)) },
                                    onQtyChange = { viewModel.onEvent(OnQtyChanged(it)) },
                                    onAdd = { viewModel.onEvent(OnAddLine) },
                                    onClear = { viewModel.onEvent(OnClearItem) }
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    if (state.addedLines.isNotEmpty()) {
                        item {
                            Section(label = stringResource(R.string.added_items), accent = accent) {
                                state.addedLines.forEach { added ->
                                    AddedTransferLineItem(added, accent, colors,
                                        onDelete = {
                                            viewModel.onEvent(OnDeleteLine(it))
                                        },
                                        onEdit = {
                                            viewModel.onEvent(OnEditLine(it))
                                        }
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    if (state.savingSuccessMessage != null) {
                        item {
                            SuccessDialog(
                                accent = GREEN,
                                message = state.savingSuccessMessage!!.text,
                                loadingButtonText = stringResource(R.string.plc_loading),
                                onLoadingClick = {
                                    val ord = state.transferOrder
                                    val orderNo = ord?.transferOrderNumber ?: ord?.carNo ?: carNo
                                    viewModel.onEvent(OnDismissSuccess)
                                    navController.navigate(Screens.LoadingOrders.createRoute(orderNo))
                                },
                                onClose = {
                                    viewModel.onEvent(OnDismissSuccess)
                                }
                            )
                        }
                    }
                    if (state.savingErrorMessage != null) {
                        item {
                            ErrorDialog(accent = DANGER, message = state.savingErrorMessage!!.text, onClose = {
                                viewModel.onEvent(OnDismissSuccess)
                            })
                        }
                    }
                }
            }
        }

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
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    "تأكيد الصرف", accent, { viewModel.onEvent(OnIssueAll) },
                    saving = state.isSaving
                )
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
    if (state.matDlg) {
        BottomSheetDialog(title = "اختيار الصنف", onClose = { viewModel.onEvent(OnToggleItemDialog) }) {
            SearchBox(
                state.matSearchText, { viewModel.onEvent(OnMaterialSearchTextChanged(it)) }, "بحث عن صنف...",
                Modifier.padding(horizontal = dims.padding, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding, end = dims.padding, bottom = 20.dp,
                )
            ) {
                itemsIndexed(state.filteredItems) { _, m ->
                    IssuingTransferItemDialogItem(m, accent, colors = colors) {
                        viewModel.onEvent(OnItemSelected(m))
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun IssuingSelectedTransferItemCard(
    state: IssuingTransferPlantsDetailsState,
    accent: Color,
    colors: AppColors,
    onBinInputChanged: (String) -> Unit,
    onScanBin: (String) -> Unit,
    onClearBin: () -> Unit,
    onStockSearchChanged: (String) -> Unit,
    onStockSelected: (MaterialOnHandStock) -> Unit,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    onWorkersGroupSearchChanged: (String) -> Unit,
    onQtyChange: (String) -> Unit,
    onAdd: () -> Unit,
    onClear: () -> Unit,
) {
    val dims = AppTheme.dims
    val item = state.selItem ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f))
            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.materialCode ?: "", color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.text, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(item.materialDescription ?: "", color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.SemiBold)
        if (!item.poNumber.isNullOrEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text("#${item.poNumber}", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoCell(
                label = stringResource(R.string.required),
                value = (item.orderedQuantity ?: 0.0).toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = "المتبقي",
                value = (item.remainingQuantity ?: 0.0).toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.unit),
                value = item.uom ?: "",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))

        Text("بيانات موقع الصرف (Bin)", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(state.binCodeError!!.text, color = colors.danger)
            }
        }

        if (state.selBinData != null) {
            Spacer(Modifier.height(8.dp))
            IssuingSelectedBinCard(
                bin = state.selBinData,
                accent = accent,
                colors = colors,
                onClear = onClearBin
            )
        }

        if (state.selBinData != null) {
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
        WorkersGroupsSpinner(
            items = state.workersGroups,
            onItemSelected = onWorkersGroupSelected,
            searchText = state.workersGroupSearchText,
            onSearchTextChanged = onWorkersGroupSearchChanged,
            accent = accent
        )
        if (state.workersGroupError != null) {
            Text(state.workersGroupError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(10.dp))

        val qLabel = "كمية الصرف (${item.uom ?: ""})"
        Field(qLabel, "0", state.qty, onQtyChange, isNumber = true, center = true)
        if (state.qtyError != null) {
            Text(state.qtyError!!.text, color = colors.danger, fontSize = dims.fontSizeSubtitle, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(12.dp))
        FullBtn("إضافة للقائمة", accent, onAdd)
    }
}

@Composable
fun IssuingSelectedBinCard(bin: StorageBinLocation, accent: Color, colors: AppColors, onClear: () -> Unit) {
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
            Text("📍 ${bin.storageBinCode}", color = accent, fontSize = dims.fontSizeTitle, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
    }
}

@Composable
fun IssuingTransferItemDialogItem(item: TransferOrderItem, accent: Color, colors: AppColors, onClick: () -> Unit) {
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card2)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.materialCode ?: "", color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
            Text("#${item.poNumber ?: ""}", color = accent, fontSize = dims.fontSizeBody, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.materialDescription ?: "", color = colors.text, fontSize = dims.fontSizeBody, modifier = Modifier.weight(1f))
            Text("Rem: ${item.remainingQuantity ?: 0.0}", color = colors.muted, fontSize = dims.fontSizeBody)
        }
    }
}

@Composable
fun AddedTransferLineItem(added: IssueTransferOrderLineDto, accent: Color, colors: AppColors, onDelete: (String) -> Unit, onEdit: (IssueTransferOrderLineDto) -> Unit) {
    val dims = AppTheme.dims
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bg)
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(added.materialCode, color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
            Text("${added.quantity} ${added.uom}", color = colors.text, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Bin: ${added.storageBinCode}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
                if (!added.batch.isNullOrEmpty()) {
                    Text("Batch: ${added.batch}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
                }
                if (!added.workerGroupCode.isNullOrEmpty()) {
                    Text("Worker Group: ${added.workerGroupCode}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onEdit(added) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = colors.text)
                }
                IconButton(onClick = { onDelete(added.materialCode) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = colors.danger)
                }
            }
        }
    }
}
