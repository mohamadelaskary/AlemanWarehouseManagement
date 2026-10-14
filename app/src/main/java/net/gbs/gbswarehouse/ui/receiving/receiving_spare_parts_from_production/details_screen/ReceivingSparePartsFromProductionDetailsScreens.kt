package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.details_screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import net.gbs.gbswarehouse.ValuationTypeSpinner
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.SparePartsReturnLine
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.receiving.ReceivingBottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.ReceivingMatCodeInput
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.ReceivingSparePartsFromVendorLineItem
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingSparePartsFromProductionDetailsScreen(
    navController: NavController,
    orderData: String,
    returnReason:String,
) {
    val viewModel: ReceivingSparePartsFromProductionDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green
    val doneLabel = stringResource(R.string.received_qty)

    var matDlg by remember { mutableStateOf(false) }

    ScannerEffect(
        onScan = { barcode ->
            viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialCodeInputEntered(barcode))
        }
    )

    LaunchedEffect(orderData) {
        viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnScreenLoaded(orderData,returnReason))
    }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(state.selectedMaterial) {
        if (state.selectedMaterial != null) {
            focusManager.clearFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.receiving),
                subtitle = stringResource(R.string.return_spare_parts_to_warehouse),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp,
                    top = 10.dp,
                    bottom = if (state.addedItems.isNotEmpty()) 90.dp else 20.dp
                )
            ) {
                // Reservation Header Info
                item {
                    Section(label = stringResource(R.string.reservation_details), accent = accent) {
//                        InfoCell(stringResource(R.string.reservation_order_number), "#$reservationNumber", accent, modifier = Modifier.fillMaxWidth())
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "#${state.orderData?.reservationNumber}",
                                    color = accent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    state.orderData?.requirementDate?.substring(0,10) ?: "",
                                    color = colors.muted,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            if (!state.orderData?.orderNumber.isNullOrEmpty())
                                Text(
                                    stringResource(R.string.order_description) + ": ${state.orderData?.orderDescription}",
                                    color = colors.text,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
                            else
                                Text(
                                    stringResource(R.string.cost_center_description) + "${state.orderData?.costCenterDescription}",
                                    color = colors.text,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
//            if (!order.orderDescription.isNullOrBlank()) {
//                Text(
//                    (stringResource(R.string.order_description) + order.orderDescription) ?: "",
//                    color = colors.muted,
//                    fontSize = 11.sp,
//                    maxLines = 1
//                )
//            } else {
//                Text(
//                    (stringResource(R.string.cost_center_description) + order.costCenterDescription)
//                        ?: "",
//                    color = colors.muted,
//                    fontSize = 11.sp,
//                    maxLines = 1
//                )
//            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    if (!state.orderData?.costCenterCode.isNullOrBlank()){
                                        Text(
                                            stringResource(R.string.cost_center),
                                            color = colors.muted,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            state.orderData?.costCenterCode ?: "-",
                                            color = colors.text,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        Text(
                                            stringResource(R.string.order_number),
                                            color = colors.muted,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            state.orderData?.orderNumber ?: "-",
                                            color = colors.text,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.items_count), color = colors.muted, fontSize = 10.sp)
                                    Text("${state.orderData?.itemCount ?: 0}", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Material Selection
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
                                    ReceivingMatCodeInput(
                                        value = state.materialCodeInput,
                                        onValueChange = {
                                            viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialCodeInputEntered(it))
                                        },
                                        onKeyboardAction = {
                                            viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialSelected(state.materialCodeInput))
                                        }
                                    )
                                    if (state.materialCodeError != null){
                                        Text(state.materialCodeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                                    }
                                }
                            OutlineBtn(stringResource(R.string.select_item), accent) { matDlg = true }
                        }

                        if (state.selectedMaterial != null) {
                            Spacer(Modifier.height(8.dp))
                            val mat = state.selectedMaterial!!
                            SelectedMaterialCard(
                                mat = SparePartsMaterial(
                                    poItemNumber = mat.reservationItem ?: "",
                                    materialCode = mat.materialCode ?: "",
                                    materialDescription = mat.materialDescription ?: "",
                                    orderedQuantity = mat.quantity?.toFloat() ?: 0f,
                                    receivedQuantity = mat.receivedQuantity?.toFloat() ?: 0f,
                                    remainingQuantity = mat.remainingQuantity?.toFloat() ?: 0f,
                                    uom = mat.uom ?: "",
                                    plantCode = mat.plantCode ?: "",
                                    valuationTypes = mat.valuationTypes,
                                    isBatchManaged = mat.isBatchManaged ?: false
                                ),
                                accent = accent,
                                doneLabel = doneLabel,
                                qty = state.quantity,
                                onQtyChange = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnQuantityEntered(it))
                                },
                                saved = false,
                                saving = state.isSaving,
                                onClear = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnClearMaterialButtonClicked) },
                                onValuationTypeChange = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnValuationTypeSelected(it))
                                },
                                warehouses = state.warehouses,
                                onWarehouseSelected = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnWarehouseSelected(it))
                                },
                                expiryDate = null,
                                onExpiryDateChange = {},
                                quantityError = state.quantityError,
                                warehouseError = state.warehouseError,
                                valuationTypeError = state.valuationTypeError,
                                warehouseSearchText = state.warehouseSearchText,
                                onWarehouseSearchTextChanged = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnWarehouseSearchTextChanged(it))
                                },
                                valuationTypeSearchText = state.valuationSearchText,
                                onValuationTypeSearchTextChanged = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnValuationSearchTextChanged(it))
                                },
                                recipientName = state.recipientName,
                                recipientNameError = state.recipientNameError,
                                onRecipientNameChange = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnGoodsRecipientEntered(it))
                                },
                                itemText = state.itemText,
                                onNotesTextChange = {
                                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnNotesEntered(it))
                                }
                            )

//                            Spacer(Modifier.height(8.dp))
//
//                            WorkersGroupsSpinner(
//                                hint = stringResource(R.string.workers_group),
//                                items = state.workersGroups,
//                                onItemSelected = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnWorkersGroupSelected(it)) },
//                                accent = accent,
//                                searchText = state.workersGroupSearchText,
//                                onSearchTextChanged = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnWorkersGroupSearchTextChanged(it)) }
//                            )
//
//                            if (state.workersGroupError.isNotEmpty()) {
//                                Text(state.workersGroupError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
//                            }
//
//                            Spacer(Modifier.height(8.dp))
//
//                            Field(
//                                label = stringResource(R.string.unloading_point),
//                                hint = stringResource(R.string.unloading_point),
//                                value = state.unloadingPoint,
//                                onValueChange = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnUnloadingPointEntered(it)) }
//                            )
//
//                            Spacer(Modifier.height(8.dp))
//
//                            Field(
//                                label = stringResource(R.string.naolon),
//                                hint = stringResource(R.string.naolon),
//                                value = state.naolon,
//                                onValueChange = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnNaolonEntered(it)) },
//                                isNumber = true
//                            )
                        }
                    }
                }

                // Add Button
                if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(10.dp))
                        AddSparePartsLineButton(
                            enabled = true,
                            onClick = {
                                viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnAddButtonClicked)
                            }
                        )
                    }
                }

                // Added Items List
                if (state.addedItems.isNotEmpty()) {
                    itemsIndexed(state.addedItems) { index, item ->
                        ReceivingSparePartsReturnLineItem(
                            item = SparePartsReturnLine(
                                materialCode = item.materialCode ?: "",
                                materialDescription = item.materialDescription ?: "",
                                plantCode = item.plantCode,
                                warehouseCode = item.warehouseCode,
                                reservationNumber = item.reservationNumber ?: "",
                                reservationItem = item.reservationItem ?: "",
                                uom = item.uom ?: "",
                                receivedQuantity = item.receivedQuantity,
                                valuationType = item.valuationType,
                                itemText = item.itemText,
                                recipientName = item.recipientName
                            ),
                            onDeleteAddedMaterialButtonClicked = {
                                viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnDeleteAddedItemClicked(index))
                            },
                            onEditAddedItemClicked = {
                                viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnEditAddedItemClicked(index))
                            }
                        )
                    }
                }
            }
        }

        // Fixed Save Button
        if (state.addedItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            0f to Color.Transparent, 0.35f to colors.bg
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    label = stringResource(R.string.save),
                    color = accent,
                    onClick = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnSaveButtonClicked) },
                    saving = state.isSaving
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(
                accent = accent,
                message = state.successMessage?.text.orEmpty(),
                onClose = {
                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.HideSuccessDialog)
                }
            )
        }
//        LaunchedEffect(state.showSuccessDialog) {
//            if (state.showSuccessDialog) {
//                delay(1000)
//                navController.popBackStack()
//            }
//        }

        if (state.showErrorDialog) {
            ErrorDialog(
                accent = accent,
                message = state.error?.text.orEmpty(),
                onClose = { viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.HideErrorDialog) }
            )
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
    // Material Selection Dialog
    if (matDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_item), onClose = { matDlg = false }) {
            SearchBox(
                value = state.materialSearchText,
                onValueChange = {
                    viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialSearchTextChanged(it))
                },
                placeholder = stringResource(R.string.search_for_item),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 90.dp,
                )
            ) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    MaterialDialogItem(
                        mat = SparePartsMaterial(
                            poItemNumber = m.reservationItem ?: "",
                            materialCode = m.materialCode ?: "",
                            materialDescription = m.materialDescription ?: "",
                            orderedQuantity = m.quantity?.toFloat() ?: 0f,
                            receivedQuantity = m.receivedQuantity?.toFloat() ?: 0f,
                            remainingQuantity = m.remainingQuantity?.toFloat() ?: 0f,
                            uom = m.uom ?: "",
                            plantCode = m.plantCode ?: "",
                            valuationTypes = m.valuationTypes,
                            isBatchManaged = m.isBatchManaged ?: false
                        ),
                        accent = accent,
                        doneLabel = doneLabel,
                        onClick = {
                            viewModel.onEvent(ReceivingSparePartsFromProductionDetailsEvent.OnMaterialSelected(m.materialCode ?: ""))
                            matDlg = false
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
@Composable
fun ReceivingSparePartsReturnLineItem(
    item: SparePartsReturnLine,
    onDeleteAddedMaterialButtonClicked: () -> Unit,
    onEditAddedItemClicked: () -> Unit
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
                        Chip(
                            stringResource(R.string.warehouse) + ": " + item.warehouseCode
                        )

                        if (!item.valuationType.isNullOrEmpty()) {
                            Chip(
                                stringResource(R.string.valuation_type_) + " ${item.valuationType}"
                            )
                        }

                        if (item.recipientName != null) {
                            Chip(
                                stringResource(R.string.receipient_name_) +
                                        item.recipientName
                            )
                        }
                        if (item.itemText != null) {
                            Chip(
                                stringResource(R.string.notes_) +
                                        item.itemText
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onEditAddedItemClicked) {
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
fun ReservationOrderCard(
    order: SparePartsReturnReservationOrder,
    accent: Color,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "#${order.reservationNumber}",
                    color = accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    order.requirementDate ?: "",
                    color = colors.muted,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.order_number) + ": ${order.orderNumber}",
                color = colors.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (!order.orderDescription.isNullOrBlank()) {
                Text(
                    order.orderDescription ?: "",
                    color = colors.muted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cost_center), color = colors.muted, fontSize = 10.sp)
                    Text(order.costCenterCode ?: "-", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.items_count), color = colors.muted, fontSize = 10.sp)
                    Text("${order.itemCount ?: 0}", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
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
    recipientName:String, recipientNameError: Message?, onRecipientNameChange: (String) -> Unit,
    itemText:String?, onNotesTextChange: (String) -> Unit
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
        if (quantityError != null) {
            Text(quantityError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
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
            Text(warehouseError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
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
                Text(valuationTypeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
            Spacer(Modifier.height(10.dp))
            Field(stringResource(R.string.receipient_name),
                stringResource(R.string.enter_receipient_name),recipientName, onRecipientNameChange, center = true)
            if (recipientNameError != null) Text(recipientNameError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(10.dp))
            Field(stringResource(R.string.notes),
                stringResource(R.string.notes),itemText?:"", onNotesTextChange, center = true)

        Spacer(Modifier.height(10.dp))
        if (mat.isBatchManaged) {
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
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = GREEN.copy(alpha = 0.10f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontSize = 11.sp,
            color = GREEN,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AddSparePartsLineButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) GREEN.copy(alpha = 0.12f) else colors.dim.copy(alpha = 0.22f))
            .border(1.dp, if (enabled) GREEN.copy(alpha = 0.35f) else colors.dim, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.add),
            color = if (enabled) GREEN else colors.dim,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun MaterialDialogItem(
    mat: SparePartsMaterial,
    accent: Color,
    doneLabel: String,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = mat.materialCode,
                color = accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = mat.uom, color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = mat.materialDescription,
            color = colors.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.required) + ": ${mat.orderedQuantity}",
                color = colors.muted,
                fontSize = 10.sp
            )
            Text(
                text = "$doneLabel: ${mat.receivedQuantity}",
                color = if (mat.receivedQuantity == mat.orderedQuantity) colors.succ else colors.muted,
                fontSize = 10.sp
            )
        }
    }
}
