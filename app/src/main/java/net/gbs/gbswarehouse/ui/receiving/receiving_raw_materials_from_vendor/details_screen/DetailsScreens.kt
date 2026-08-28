package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.TransportingVendor
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.WorkersGroupsSpinner
import net.gbs.gbswarehouse.data.remote.dto.response.RawMaterialsLineDto
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.transportationTypes
import net.gbs.gbswarehouse.ui.receiving.ReceivingAddLineButton
import net.gbs.gbswarehouse.ui.receiving.ReceivingBottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.ReceivingChip
import net.gbs.gbswarehouse.ui.receiving.ReceivingInfoCell
import net.gbs.gbswarehouse.ui.receiving.ReceivingMatCodeInput
import net.gbs.gbswarehouse.ui.receiving.ReceivingMaterialDialogItem
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingRawMaterialsFromVendorDetailsScreen(
    navController: NavController,
    orderId: String
){
    val viewModel: DetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green
    var showMatDlg by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.LoadOrder(orderId))
    }

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    if (state.showSuccessDialog) {
        SuccessDialog(
            accent = accent,
            message = stringResource(R.string.save_success_message),
            onClose = {
                viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnDismissDialogs)
                navController.popBackStack()
            }
        )
    }

    if (state.showErrorDialog) {
        ErrorDialog(
            accent = accent,
            message = state.saveError,
            onClose = { viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnDismissDialogs) }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = stringResource(R.string.receiving), subtitle = stringResource(R.string.raw_materials_from_vendor),
                accent = accent, onBack = {
                    navController.popBackStack()
                })

            when (val status = state.getDataStatus) {
                is RawMaterialsPurchaseOrderGetDataStatus.LOADING -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is RawMaterialsPurchaseOrderGetDataStatus.ERROR -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(status.message, color = colors.text)
                    }
                }
                is RawMaterialsPurchaseOrderGetDataStatus.SUCCESS, RawMaterialsPurchaseOrderGetDataStatus.IDLE -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 14.dp, end = 14.dp,
                            top = 10.dp,
                            bottom = if (state.addedItems.isNotEmpty()) 90.dp else 20.dp
                        )
                    ) {
                        item {
                            Section(label = stringResource(R.string.order_data), accent = colors.green) {
                                val po = state.purchaseOrder
                                val fields = listOf(
                                    Triple(stringResource(R.string.po_number), po?.poNumber ?: "", null),
                                    Triple(stringResource(R.string.vendor), po?.vendorName ?: "", null),
                                    Triple(stringResource(R.string.vendor_code), po?.vendorCode ?: "", null),
                                    Triple(stringResource(R.string.items_count), "${po?.purchaseOrderItems?.size ?: 0} " + stringResource(R.string.items), null),
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
                            }
                        }

                        item {
                            Section(label = stringResource(R.string.item_data), accent = accent) {
                                Column {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ReceivingMatCodeInput(
                                            value = state.selectedMaterial?.materialCode ?: "",
                                            onValueChange = { /* handled by dialog */ },
                                            modifier = Modifier.weight(1f),
                                            enabled = false
                                        )
                                        OutlineBtn(stringResource(R.string.select_material), accent) {
                                            showMatDlg = true
                                        }
                                    }
                                    if (state.materialCodeError.isNotEmpty()) {
                                        Text(
                                            state.materialCodeError,
                                            color = Color.Red,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }
                                
                                if (state.selectedMaterial != null) {
                                    Spacer(Modifier.height(8.dp))
                                    SelectedRawMaterialToVendorMaterialCard(
                                        mat = state.selectedMaterial!!,
                                        state = state,
                                        onEvent = viewModel::onEvent,
                                        accent = accent,
                                        onTransportationVendorChanged = {
                                            viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnTransportationVendorChanged(it))
                                        }
                                    )
                                }
                            }
                        }

                        if (state.addedItems.isNotEmpty()) {
                            item {
                                Section(label = stringResource(R.string.added_items), accent = accent) {
                                    state.addedItems.forEachIndexed { index, item ->
                                        ReceivingRawMaterialsLineItem(item = item, onDelete = {
                                            viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnRemoveItem(index))
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.addedItems.isNotEmpty()) {
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
                    stringResource(R.string.save_receiving) + " (${state.addedItems.size})", accent, {
                        viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnSave)
                    },
                    saving = state.isSaving
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
    }

    if (showMatDlg) {
        ReceivingBottomSheetDialog(title = stringResource(R.string.select_item), onClose = { showMatDlg = false }) {
            SearchBox(state.materialSearchText, { viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialSearchTextChanged(it)) }, stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 14.dp, end = 14.dp, bottom = 20.dp,
                )
            ) {
                val list = if (state.materialSearchText.isEmpty()) state.purchaseOrder?.purchaseOrderItems ?: emptyList() else state.filteredMaterials
                itemsIndexed(list) { _, m ->
                    ReceivingMaterialDialogItem(
                        materialCode = m.materialCode ?: "",
                        materialDescription = m.materialDescription ?: "",
                        uom = m.uom ?: "",
                        orderedQty = m.orderedQuantity.toString(),
                        receivedQty = m.receivedQuantity.toString(),
                        doneLabel = stringResource(R.string.received_qty),
                        accent = accent
                    ) {
                        viewModel.onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnMaterialSelected(m))
                        showMatDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun SelectedRawMaterialToVendorMaterialCard(
    mat: RawMaterialsPurchaseOrderItem,
    state: ReceivingRawMaterialsFromVendorDetailsState,
    onEvent: (ReceivingRawMaterialsFromVendorDetailsEvent) -> Unit,
    accent: Color,
    onTransportationVendorChanged: (TransportationType) -> Unit
) {
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
            Text(mat.materialCode ?: "", color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnClearMaterial) }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription ?: "", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf( stringResource(R.string.required) to mat.orderedQuantity.toString(), stringResource(R.string.received_qty) to mat.receivedQuantity.toString(), stringResource(R.string.unit) to (mat.uom ?: "")).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp)
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            transportationTypes.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = transportationTypes.size
                    ),
                    selected = state.transportationVendor == option,
                    onClick  = { onTransportationVendorChanged(option) },
                    colors   = SegmentedButtonDefaults.colors(
                        activeContainerColor   = GREEN.copy(alpha = 0.15f),
                        activeContentColor     = GREEN,
                        activeBorderColor      = GREEN.copy(alpha = 0.60f),
                        inactiveContainerColor = colors.card,
                        inactiveContentColor   = colors.muted,
                        inactiveBorderColor    = colors.border2,
                    ),
                    icon = {},
                    label = {
                        Text(
                            text = option.arName,
                            fontSize = 13.sp,
                            fontWeight = if (state.transportationVendor == option) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        
        WarehousesSpinner(
            items = state.warehouses,
            onItemSelected = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSelected(it)) },
            searchText = state.warehouseSearchText,
            onSearchTextChanged = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnWarehouseSearchTextChanged(it)) }
        )
        if (state.warehouseError.isNotEmpty()) Text(state.warehouseError, color = Color.Red, fontSize = 10.sp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg.copy(alpha = 0.65f)).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.car_number),color = colors.muted)
                Text(state.selectedMaterial?.carNo?:"", color = accent, fontWeight = FontWeight.Bold)
            }
        }
//        CarNumberSpinner(
//            items = state.carNumbers,
//            onItemSelected = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSelected(it)) },
//            searchText = state.carNumberSearchText,
//            onSearchTextChanged = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnCarNumberSearchTextChanged(it)) }
//        )
//        if (state.carNumberError.isNotEmpty()) Text(state.carNumberError, color = Color.Red, fontSize = 10.sp)

        Field(stringResource(R.string.unloading_point) + " *", stringResource(R.string.enter_unloading_point), state.unloadingPoint, { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnUnloadingPointChanged(it)) })
        if (state.unloadingPointError.isNotEmpty()) Text(state.unloadingPointError, color = Color.Red, fontSize = 10.sp)

        WorkersGroupsSpinner(
            items = state.workerGroups,
            onItemSelected = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSelected(it)) },
            searchText = state.workerGroupSearchText,
            onSearchTextChanged = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnWorkerGroupSearchTextChanged(it)) }
        )
        if (state.workerGroupError.isNotEmpty()) Text(state.workerGroupError, color = Color.Red, fontSize = 10.sp)
        if (state.transportationVendor==transportationTypes.first()) {
            NaolonDescriptionSpinner(
                items = state.naolonDescriptions,
                onItemSelected = {
                    onEvent(
                        ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSelected(
                            it
                        )
                    )
                },
                searchText = state.naolonDescriptionSearchText,
                onSearchTextChanged = {
                    onEvent(
                        ReceivingRawMaterialsFromVendorDetailsEvent.OnNaolonDescriptionSearchTextChanged(
                            it
                        )
                    )
                }
            )
            if (state.naolonDescriptionError.isNotEmpty()) Text(
                state.naolonDescriptionError,
                color = Color.Red,
                fontSize = 10.sp
            )
        }
        CarTypeSpinner(
            items = state.carTypes,
            onItemSelected = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSelected(it)) },
            searchText = state.carTypeSearchText,
            onSearchTextChanged = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnCarTypeSearchTextChanged(it)) }
        )
        if (state.carTypeError.isNotEmpty()) Text(state.carTypeError, color = Color.Red, fontSize = 10.sp)
        if (state.transportationVendor==transportationTypes.first()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)).background(colors.dark).padding(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(R.string.naolon_cost) + ":",
                        color = colors.muted,
                        fontSize = 12.sp
                    )
                    Text(state.naolonValue?:"", color = accent, fontWeight = FontWeight.Bold)
                }
            }
        }

        Field(stringResource(R.string.cont_no) + " *", stringResource(R.string.enter_cont_no), state.contNo, { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnContNoChanged(it)) })
        if (state.contNoError.isNotEmpty()) Text(state.contNoError, color = Color.Red, fontSize = 10.sp)
        if (state.transportationVendor==transportationTypes.first()){
            Spacer(Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.bg.copy(alpha = 0.65f)).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.loading_vendor),color = colors.muted)
                    Text(state.selectedLoadingVendor?.vendorName?:"", color = accent, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LoadingVendorSpinner(
                items = state.loadingVendors,
                onItemSelected = {
                    onEvent(
                        ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSelected(
                            it
                        )
                    )
                },
                searchText = state.loadingVendorSearchText,
                onSearchTextChanged = {
                    onEvent(
                        ReceivingRawMaterialsFromVendorDetailsEvent.OnLoadingVendorSearchTextChanged(
                            it
                        )
                    )
                }
            )
            if (state.loadingVendorError.isNotEmpty()) Text(
                state.loadingVendorError,
                color = Color.Red,
                fontSize = 10.sp
            )
        }
        PackageTypeSpinner(
            items = state.packageTypes,
            onItemSelected = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSelected(it)) },
            searchText = state.packageTypeSearchText,
            onSearchTextChanged = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnPackageTypeSearchTextChanged(it)) }
        )
        if (state.packageTypeError.isNotEmpty()) Text(state.packageTypeError, color = Color.Red, fontSize = 10.sp)

        Field(stringResource(R.string.receiving_qty) + " *", "0.0", state.quantity, { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnQuantityChanged(it)) }, isNumber = true)
        if (state.quantityError.isNotEmpty()) Text(state.quantityError, color = Color.Red, fontSize = 10.sp)

        Spacer(Modifier.height(16.dp))
        
        ReceivingAddLineButton(accent = accent, onClick = { onEvent(ReceivingRawMaterialsFromVendorDetailsEvent.OnAddItem) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarNumberSpinner(hint:String=stringResource(R.string.select_car_number), items : List<CarNumber>, onItemSelected: (CarNumber) -> Unit, accent: Color= AppTheme.colors.green, label: String = stringResource(R.string.car_number), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        SpinnerLabel(label, accent)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.carDescription} (${it.carNumber})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NaolonDescriptionSpinner(hint:String=stringResource(R.string.select_naolon_description), items : List<NaolonDescription>, onItemSelected: (NaolonDescription) -> Unit, accent: Color= AppTheme.colors.green, label: String = stringResource(R.string.naolon), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        SpinnerLabel(label, accent)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.naolonDescription}" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypeSpinner(hint:String=stringResource(R.string.select_car_type), items : List<CarType>, onItemSelected: (CarType) -> Unit, accent: Color= AppTheme.colors.green, label: String = stringResource(R.string.car_type), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        SpinnerLabel(label, accent)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.description} (${it.vehicleTypeName})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingVendorSpinner(hint:String=stringResource(R.string.select_loading_vendor), items : List<LoadingVendor>, onItemSelected: (LoadingVendor) -> Unit, accent: Color= AppTheme.colors.green, label: String = stringResource(R.string.loading_vendor), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        SpinnerLabel(label, accent)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.vendorName} (${it.vendorCode})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageTypeSpinner(hint:String=stringResource(R.string.select_packing_type), items : List<PackageType>, onItemSelected: (PackageType) -> Unit, accent: Color= AppTheme.colors.green, label: String = stringResource(R.string.packing_type), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        SpinnerLabel(label, accent)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.packTypeDescription} (${it.packTypeCode})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@Composable
fun SpinnerLabel(label: String, accent: Color) {
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
            label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
fun ReceivingRawMaterialsLineItem(item: RawMaterialsLineDto, onDelete: () -> Unit) {
    val colors = AppTheme.colors
    val accent = AppTheme.colors.green

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.materialCode, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accent)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = item.receivedQuantity.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.text)
                    Spacer(Modifier.width(5.dp))
                    Text(text = item.uom, fontSize = 12.sp, color = colors.muted)
                }
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReceivingChip(text = stringResource(R.string.plant_) + item.plantCode)
                ReceivingChip(text = stringResource(R.string.warehouse_) + item.warehouseCode)
                if (!item.carNo.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.car_number_) + item.carNo)
                if (!item.unloadingPoint.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.unloading_point_) + item.unloadingPoint)
                if (item.naolon != null) ReceivingChip(text = stringResource(R.string.naolon_cost_) + item.naolon)
                if (!item.naolonDescription.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.naolon_description_) + item.naolonDescription)
                if (!item.contNo.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.contract_number_) + item.contNo)
                if (!item.loadingVendorCode.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.loading_vendor_) + item.loadingVendorCode)
                if (!item.wrokerGroupCode.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.workers_group_) + item.wrokerGroupCode)
                if (!item.packTypeCode.isNullOrEmpty()) ReceivingChip(text = stringResource(R.string.packing_type_) + item.packTypeCode)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}


// Keep Inspecting and PutAway as placeholders if they are not the focus
@Composable
fun InspectingRawMaterialsFromVendorDetailsScreen(navController: NavController, orderId: String) {
    Box(Modifier.fillMaxSize().background(AppTheme.colors.bg), contentAlignment = Alignment.Center) { Text(stringResource(R.string.quality_inspection) + " - " + stringResource(R.string.under_implementation), color = AppTheme.colors.text) }
}

@Composable
fun PutAwayRawMaterialsFromVendorDetailsScreen(navController: NavController, orderId: String) {
    Box(Modifier.fillMaxSize().background(AppTheme.colors.bg), contentAlignment = Alignment.Center) { Text(stringResource(R.string.putaway) + " - " + stringResource(R.string.under_implementation), color = AppTheme.colors.text) }
}
