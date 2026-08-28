package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialLine
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.receiving.ReceivingMatCodeInput
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.BottomSheetDialog
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.CarNumberSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.CarTypeSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.InfoCell
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.LoadingVendorSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonCard
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonDescriptionSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonLoadingStatus
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.PackingTypeSpinner
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.DANGER
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun ReceivingProductFromBranchDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.green
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(state.purchaseOrderGetDataStatus) {
        if (state.purchaseOrderGetDataStatus is TransitPurchaseOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                subtitle = stringResource(R.string.product_from_branch),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.purchaseOrderGetDataStatus) {
                TransitPurchaseOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is TransitPurchaseOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError, color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                                    Row(
                                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        InfoCell(stringResource(R.string.po_number), order.poNumber, accent, Modifier.weight(1f))
                                    }
                                    InfoCell(stringResource(R.string.vendor), order.vendorName ?: "", colors.text, Modifier.fillMaxWidth())
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        InfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text, Modifier.fillMaxWidth())
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
                                                onValueChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered(it)) },
                                                onKeyboardAction = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(state.materialCodeInput)) }
                                            )
                                            if (state.materialCodeError.isNotEmpty()){
                                                Text(state.materialCodeError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                                            }
                                        }
                                        OutlineBtn(stringResource(R.string.select_item), accent) { viewModel.matDlg = true }
                                    }
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedTransitMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSelected(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            carNoError = state.carNoError,
                                            searchText = state.warehouseSearchText,
                                            onSearchTextChanged = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSearchTextChange(it)) },
                                            carNumberSearchText = state.carNoSearchText,
                                            carNumbersList = state.carNos,
                                            onCarNumberSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoTextSearchChange(it)) },
                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            contractNumber = state.contNo,
                                            onContractNumberEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnContNoEntered(it)) },
                                            contractNumberError = state.contNoError,
                                            loadingVendors = state.loadingVendors,
                                            onLoadingVendorSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorSelected(it)) },
                                            loadingVendorSearchText = state.loadingVendorSearchText,
                                            onSearchLoadingVendorTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorTextSearchChange(it)) },
                                            loadingVendorError = state.loadingVendorError,
                                            packingTypes = state.packingTypes,
                                            onPackingTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeSelected(it)) },
                                            packingTypeError = state.packingTypeError,
                                            packingTypeSearchText = state.packingTypeSearchText,
                                            onSearchPackingTypeTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeTextSearchChange(it)) },
                                            carTypesList = state.carTypes,
                                            onCarTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeSelected(it)) },
                                            carTypesErrorMessage = state.carTypeError,
                                            carTypeTextSearch = state.carTypeSearchText,
                                            onCarTypeTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeTextSearchChange(it)) },
                                            naoloanDescriptions = state.naolonDescriptions,
                                            onNaolonDescriptionSelected = {
                                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionSelected(it))
                                                                          },
                                            naolonDescriptionTextSearch = state.naolonDescriptionSearchText,
                                            onNaolonDescriptionTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionTextSearchChange(it)) },
                                            naolonDescriptionErrorMessage = state.naolonError,
                                            naolonLoadingStatus = state.naolonLoadStatus,
                                        )
                                    }
                                }
                            }

                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    AddTransitLineButton(
                                        onClick = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnAddButtonClicked) }
                                    )
                                }
                            }

                            itemsIndexed(state.addedItems) { index, item ->
                                ReceivingTransitMaterialsLineItem(item) {
                                    viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
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
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    TransitMaterialDialogItem(m, accent, doneLabel) {
                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(m.materialCode ?: ""))
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun SelectedTransitMaterialCard(
    mat: TransitOrderMaterialPurchaseOrderItem,
    accent: Color,
    doneLabel: String,
    qty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
    onCarNoSelected: (CarNumber) -> Unit,
    quantityError: String = "",
    warehouseError: String = "",
    carNoError: String = "",
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit,
    carNumberSearchText: String = "",
    carNumbersList: List<CarNumber> = emptyList(),
    onCarNumberSearchTextChange: (String) -> Unit,
    unloadingPoint: String = "",
    unloadingPointError: String = "",
    onUnloadingPointChange: (String) -> Unit,
    workersGroup: List<WorkersGroup> = emptyList(),
    workersGroupSearchText: String = "",
    onWorkersGroupSearchTextChange: (String) -> Unit,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    workersGroupError: String = "",
    contractNumber: String = "",
    onContractNumberEntered: (String) -> Unit,
    contractNumberError: String = "",
    loadingVendors: List<LoadingVendor> = emptyList(),
    onLoadingVendorSelected: (LoadingVendor) -> Unit,
    loadingVendorSearchText: String = "",
    onSearchLoadingVendorTextChange: (String) -> Unit,
    loadingVendorError: String = "",
    packingTypes: List<PackageType> = emptyList(),
    onPackingTypeSelected: (PackageType) -> Unit,
    packingTypeError: String = "",
    packingTypeSearchText: String = "",
    onSearchPackingTypeTextChange: (String) -> Unit,
    carTypesList: List<CarType> = emptyList(),
    onCarTypeSelected: (CarType) -> Unit,
    carTypesErrorMessage: String = "",
    carTypeTextSearch: String = "",
    onCarTypeTextSearchChange: (String) -> Unit,
    naoloanDescriptions: List<NaolonDescription> = emptyList(),
    onNaolonDescriptionSelected: (NaolonDescription) -> Unit,
    naolonDescriptionTextSearch: String = "",
    onNaolonDescriptionTextSearchChange: (String) -> Unit,
    naolonDescriptionErrorMessage: String = "",
    naolonLoadingStatus: NaolonLoadingStatus
) {
    val progress = if ((mat.orderedQuantity ?: 0.0) > 0) (mat.receivedQuantity ?: 0.0) / mat.orderedQuantity!! else 0.0
    val colors = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f)).border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(mat.materialCode ?: "", color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = 12.sp,
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
        if (quantityError.isNotEmpty()) Text(quantityError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        WarehousesSpinner(
            hint = stringResource(R.string.warehouse),
            items = warehouses,
            onItemSelected = onWarehouseSelected,
            accent = accent,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
        if (warehouseError.isNotEmpty()) Text(warehouseError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        
        Spacer(Modifier.height(10.dp))
        CarNumberSpinner(
            hint = stringResource(R.string.select_car_number),
            items = carNumbersList,
            onItemSelected = onCarNoSelected,
            accent = accent,
            label = stringResource(R.string.car_number),
            searchText = carNumberSearchText,
            onSearchTextChanged = { onCarNumberSearchTextChange(it) }
        )
        if (carNoError.isNotEmpty()) Text(carNoError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        Field(
            label = stringResource(R.string.unloading_point),
            hint = stringResource(R.string.enter_unloading_point),
            value = unloadingPoint,
            onValueChange = onUnloadingPointChange,
            isNumber = false,
            center = true
            )
        if (unloadingPointError.isNotEmpty()) Text(unloadingPointError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        WorkersGroupsSpinner(
            hint = stringResource(R.string.select_workers_group),
            items = workersGroup,
            onItemSelected = onWorkersGroupSelected,
            accent = accent,
            label = stringResource(R.string.workers_group),
            searchText = workersGroupSearchText,
            onSearchTextChanged = { onWorkersGroupSearchTextChange(it) }
        )
        if (workersGroupError.isNotEmpty()) Text(workersGroupError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        Field(
            label = stringResource(R.string.cont_no),
            hint = stringResource(R.string.enter_cont_no),
            value = contractNumber,
            onValueChange = onContractNumberEntered,
            isNumber = false,
            center = true
        )
        if (contractNumberError.isNotEmpty()) Text(contractNumberError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        LoadingVendorSpinner(
            items = loadingVendors,
            onItemSelected = onLoadingVendorSelected,
            accent = accent,
            searchText = loadingVendorSearchText,
            onSearchTextChanged = { onSearchLoadingVendorTextChange(it) }
        )
        if (loadingVendorError.isNotEmpty()) Text(loadingVendorError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        PackingTypeSpinner(
            hint = stringResource(R.string.select_packing_type),
            items = packingTypes,
            onItemSelected = onPackingTypeSelected,
            accent = accent,
            searchText = packingTypeSearchText,
            onSearchTextChanged = { onSearchPackingTypeTextChange(it) }
        )
        if (packingTypeError.isNotEmpty()) Text(packingTypeError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        NaolonCard(
            carTypes = carTypesList,
            onCarTypeSelected =  onCarTypeSelected,
            carTypeTextSearch = carTypeTextSearch,
            onCarTypeTextSearchChange = { onCarTypeTextSearchChange(it) },
            carTypesErrorMessage = carTypesErrorMessage,
            naoloanDescriptions = naoloanDescriptions,
            onNaolonDescriptionSelected = onNaolonDescriptionSelected,
            naolonDescriptionTextSearch = naolonDescriptionTextSearch,
            onNaolonDescriptionTextSearchChange = { onNaolonDescriptionTextSearchChange(it) },
            naolonDescriptionErrorMessage = naolonDescriptionErrorMessage,
            accent = accent,
            naolonLoadingStatus = naolonLoadingStatus,
        )
    }
}

@Composable
fun AddTransitLineButton(onClick: () -> Unit) {
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
fun ReceivingTransitMaterialsLineItem(item: TransitOrderMaterialLine, onDelete: () -> Unit) {
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
                    Chip(stringResource(R.string.warehouse) + ": " + item.warehouseCode)
                    if (item.carNo != null) {
                        Chip(stringResource(R.string.car_number) + ": " + item.carNo)
                    }
                    Chip(stringResource(R.string.po_number) + ": " + item.purchaseOrder)
                    if (!item.unloadingPoint.isNullOrBlank()) {
                        Chip(stringResource(R.string.unloading_point) + ": " + item.unloadingPoint)
                    }
                    if (!item.wrokerGroupCode.isNullOrBlank()) {
                        Chip(stringResource(R.string.workers_group) + ": " + item.wrokerGroupCode)
                    }
                    if (!item.contNo.isNullOrBlank()) {
                        Chip(stringResource(R.string.cont_no) + ": " + item.contNo)
                    }
                    if (!item.loadingVendorCode.isNullOrBlank()) {
                        Chip(stringResource(R.string.loading_vendor) + ": " + item.loadingVendorCode)
                    }
                    if (!item.packTypeCode.isNullOrBlank()) {
                        Chip(stringResource(R.string.packing_type) + ": " + item.packTypeCode)
                    }
                    if (!item.naolonDescription.isNullOrBlank()) {
                        Chip(stringResource(R.string.naolon) + ": " + item.naolonDescription)
                    }
                    if (!item.carType.isNullOrBlank()) {
                        Chip(stringResource(R.string.car_type) + ": " + item.carType)
                    }
                    if (item.naolon != null) {
                        Chip(stringResource(R.string.naolon_cost) + ": " + item.naolon)
                    }
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
fun InspectingProductFromBranchDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.purple
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(state.purchaseOrderGetDataStatus) {
        if (state.purchaseOrderGetDataStatus is TransitPurchaseOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                title = stringResource(R.string.quality_decision),
                subtitle = stringResource(R.string.product_from_branch),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.purchaseOrderGetDataStatus) {
                TransitPurchaseOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is TransitPurchaseOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError, color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                                    Row(
                                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        InfoCell(stringResource(R.string.po_number), order.poNumber, accent, Modifier.weight(1f))
                                    }
                                    InfoCell(stringResource(R.string.vendor), order.vendorName ?: "", colors.text, Modifier.fillMaxWidth())
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        InfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text, Modifier.fillMaxWidth())
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
                                                onValueChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered(it)) },
                                                onKeyboardAction = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(state.materialCodeInput)) }
                                            )
                                            if (state.materialCodeError.isNotEmpty()){
                                                Text(state.materialCodeError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                                            }
                                        }
                                        OutlineBtn(stringResource(R.string.select_item), accent) { viewModel.matDlg = true }
                                    }
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedTransitMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSelected(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            carNoError = state.carNoError,
                                            searchText = state.warehouseSearchText,
                                            onSearchTextChanged = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSearchTextChange(it)) },
                                            carNumberSearchText = state.carNoSearchText,
                                            carNumbersList = state.carNos,
                                            onCarNumberSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoTextSearchChange(it)) },
                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            contractNumber = state.contNo,
                                            onContractNumberEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnContNoEntered(it)) },
                                            contractNumberError = state.contNoError,
                                            loadingVendors = state.loadingVendors,
                                            onLoadingVendorSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorSelected(it)) },
                                            loadingVendorSearchText = state.loadingVendorSearchText,
                                            onSearchLoadingVendorTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorTextSearchChange(it)) },
                                            loadingVendorError = state.loadingVendorError,
                                            packingTypes = state.packingTypes,
                                            onPackingTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeSelected(it)) },
                                            packingTypeError = state.packingTypeError,
                                            packingTypeSearchText = state.packingTypeSearchText,
                                            onSearchPackingTypeTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeTextSearchChange(it)) },
                                            carTypesList = state.carTypes,
                                            onCarTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeSelected(it)) },
                                            carTypesErrorMessage = state.carTypeError,
                                            carTypeTextSearch = state.carTypeSearchText,
                                            onCarTypeTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeTextSearchChange(it)) },
                                            naoloanDescriptions = state.naolonDescriptions,
                                            onNaolonDescriptionSelected = {
                                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionSelected(it))
                                                                          },
                                            naolonDescriptionTextSearch = state.naolonDescriptionSearchText,
                                            onNaolonDescriptionTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionTextSearchChange(it)) },
                                            naolonDescriptionErrorMessage = state.naolonError,
                                            naolonLoadingStatus = state.naolonLoadStatus,
                                        )
                                    }
                                }
                            }

                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    AddTransitLineButton(
                                        onClick = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnAddButtonClicked) }
                                    )
                                }
                            }

                            itemsIndexed(state.addedItems) { index, item ->
                                ReceivingTransitMaterialsLineItem(item) {
                                    viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
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
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    TransitMaterialDialogItem(m, accent, doneLabel) {
                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(m.materialCode ?: ""))
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun PutAwayProductFromBranchDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.yellow
    val doneLabel = stringResource(R.string.received_qty)

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    LaunchedEffect(state.purchaseOrderGetDataStatus) {
        if (state.purchaseOrderGetDataStatus is TransitPurchaseOrderGetDataStatus.SUCCESS) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(orderId) {
        viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                title = stringResource(R.string.putaway),
                subtitle = stringResource(R.string.product_from_branch),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            when (state.purchaseOrderGetDataStatus) {
                TransitPurchaseOrderGetDataStatus.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accent)
                    }
                }
                is TransitPurchaseOrderGetDataStatus.ERROR -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.searchError, color = colors.text)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.retry), accent) {
                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnScreenLoaded(orderId))
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
                                    Row(
                                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        InfoCell(stringResource(R.string.po_number), order.poNumber, accent, Modifier.weight(1f))
                                    }
                                    InfoCell(stringResource(R.string.vendor), order.vendorName ?: "", colors.text, Modifier.fillMaxWidth())
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        InfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text, Modifier.fillMaxWidth())
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
                                                onValueChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialCodeInputEntered(it)) },
                                                onKeyboardAction = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(state.materialCodeInput)) }
                                            )
                                            if (state.materialCodeError.isNotEmpty()){
                                                Text(state.materialCodeError, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                                            }
                                        }
                                        OutlineBtn(stringResource(R.string.select_item), accent) { viewModel.matDlg = true }
                                    }
                                    if (state.selectedMaterial != null) {
                                        Spacer(Modifier.height(8.dp))
                                        SelectedTransitMaterialCard(
                                            mat = state.selectedMaterial!!,
                                            accent = accent,
                                            doneLabel = doneLabel,
                                            qty = state.quantity,
                                            onQtyChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnQuantityEntered(it)) },
                                            onClear = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnClearMaterialButtonClicked) },
                                            warehouses = state.warehouses,
                                            onWarehouseSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSelected(it)) },
                                            quantityError = state.quantityError,
                                            warehouseError = state.warehouseError,
                                            carNoError = state.carNoError,
                                            searchText = state.warehouseSearchText,
                                            onSearchTextChanged = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWarehouseSearchTextChange(it)) },
                                            carNumberSearchText = state.carNoSearchText,
                                            carNumbersList = state.carNos,
                                            onCarNumberSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoTextSearchChange(it)) },
                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            contractNumber = state.contNo,
                                            onContractNumberEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnContNoEntered(it)) },
                                            contractNumberError = state.contNoError,
                                            loadingVendors = state.loadingVendors,
                                            onLoadingVendorSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorSelected(it)) },
                                            loadingVendorSearchText = state.loadingVendorSearchText,
                                            onSearchLoadingVendorTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnLoadingVendorTextSearchChange(it)) },
                                            loadingVendorError = state.loadingVendorError,
                                            packingTypes = state.packingTypes,
                                            onPackingTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeSelected(it)) },
                                            packingTypeError = state.packingTypeError,
                                            packingTypeSearchText = state.packingTypeSearchText,
                                            onSearchPackingTypeTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnPackingTypeTextSearchChange(it)) },
                                            carTypesList = state.carTypes,
                                            onCarTypeSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeSelected(it)) },
                                            carTypesErrorMessage = state.carTypeError,
                                            carTypeTextSearch = state.carTypeSearchText,
                                            onCarTypeTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarTypeTextSearchChange(it)) },
                                            naoloanDescriptions = state.naolonDescriptions,
                                            onNaolonDescriptionSelected = {
                                                viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionSelected(it))
                                                                          },
                                            naolonDescriptionTextSearch = state.naolonDescriptionSearchText,
                                            onNaolonDescriptionTextSearchChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnNaolonDescriptionTextSearchChange(it)) },
                                            naolonDescriptionErrorMessage = state.naolonError,
                                            naolonLoadingStatus = state.naolonLoadStatus,
                                        )
                                    }
                                }
                            }

                            if (state.selectedMaterial != null && state.quantity.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(10.dp))
                                    AddTransitLineButton(
                                        onClick = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnAddButtonClicked) }
                                    )
                                }
                            }

                            itemsIndexed(state.addedItems) { index, item ->
                                ReceivingTransitMaterialsLineItem(item) {
                                    viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
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
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.35f to colors.bg))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.confirm_putaway), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredMaterials) { _, m ->
                    TransitMaterialDialogItem(m, accent, doneLabel) {
                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSelected(m.materialCode ?: ""))
                        viewModel.matDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun TransitMaterialDialogItem(mat: TransitOrderMaterialPurchaseOrderItem, accent: Color, doneLabel: String, onClick: () -> Unit) {
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
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription ?: "", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("${stringResource(R.string.required)}: ${mat.orderedQuantity}", color = colors.muted, fontSize = 10.sp)
            Text("$doneLabel: ${mat.receivedQuantity}", color = if (mat.receivedQuantity == mat.orderedQuantity) colors.succ else colors.muted, fontSize = 10.sp)
        }
    }
}
