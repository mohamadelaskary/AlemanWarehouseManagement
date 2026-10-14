package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehousesSpinner
import net.gbs.gbswarehouse.WorkersGroupsSpinner
import net.gbs.gbswarehouse.DatePickerField
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
import net.gbs.gbswarehouse.ui.receiving.ReceivingInfoCell
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.CarNumberSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.CarTypeSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.LoadingVendorSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonCard
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonDescriptionSpinner
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonLoadingStatus
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.PackingTypeSpinner
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.transportationTypes
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen.ReceivingRawMaterialsFromVendorDetailsEvent
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
    val dims = AppTheme.dims
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
                            Text(state.searchError!!.text, color = colors.text)
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
                                start = dims.padding, end = dims.padding, top = 10.dp,
                                bottom = if (state.addedItems.isNotEmpty()) dims.bottomBarHeight else 20.dp
                            )
                        ) {
                            item {
                                Section(label = stringResource(R.string.order_data), accent = accent) {
                                    val fields = listOf(
                                        Triple(stringResource(R.string.car_number), order.carNo ?: "", null),
                                        Triple(stringResource(R.string.issuing_plant), order.issuingPlantDescription ?: "", null),
                                        Triple(stringResource(R.string.issuing_plant_code), order.issuingPlantCode ?: "", null),
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
                                        ReceivingInfoCell(stringResource(R.string.factory_car_description), order.factoryCarDescription ?: "", colors.text)
                                    }
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        Spacer(Modifier.height(4.dp))
                                        ReceivingInfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text)
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
                                            if (state.materialCodeError != null){
                                                Text(state.materialCodeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
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
                                            state = state,
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
//                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            productionDate = state.productionDate,
                                            onProductionDateEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnProductionDateEntered(it)) },
                                            productionDateError = state.productionDateError,
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
                                ReceivingTransitMaterialsLineItem(
                                    item = item,
                                    onDelete = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
                                    },
                                    onEdit = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnEditAddedMaterialButtonClicked(index))
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
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save_receiving), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
//            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
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
        val dims = AppTheme.dims
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = dims.padding, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = dims.padding, end = dims.padding, bottom = 20.dp)) {
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
    state: ReceivingProductFromBranchState,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit,
//    onCarNoSelected: (CarNumber) -> Unit,
    quantityError: Message? = null,
    warehouseError: Message? = null,
    carNoError: Message? = null,
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit,
    carNumberSearchText: String = "",
    carNumbersList: List<CarNumber> = emptyList(),
    onCarNumberSearchTextChange: (String) -> Unit,
    unloadingPoint: String = "",
    unloadingPointError: Message? = null,
    onUnloadingPointChange: (String) -> Unit,
    workersGroup: List<WorkersGroup> = emptyList(),
    workersGroupSearchText: String = "",
    onWorkersGroupSearchTextChange: (String) -> Unit,
    onWorkersGroupSelected: (WorkersGroup) -> Unit,
    workersGroupError: Message? = null,
    productionDate: String = "",
    onProductionDateEntered: (String) -> Unit,
    productionDateError: Message? = null,
    loadingVendors: List<LoadingVendor> = emptyList(),
    onLoadingVendorSelected: (LoadingVendor) -> Unit,
    loadingVendorSearchText: String = "",
    onSearchLoadingVendorTextChange: (String) -> Unit,
    loadingVendorError: Message? = null,
    packingTypes: List<PackageType> = emptyList(),
    onPackingTypeSelected: (PackageType) -> Unit,
    packingTypeError: Message? = null,
    packingTypeSearchText: String = "",
    onSearchPackingTypeTextChange: (String) -> Unit,
    carTypesList: List<CarType> = emptyList(),
    onCarTypeSelected: (CarType) -> Unit,
    carTypesErrorMessage: Message? = null,
    carTypeTextSearch: String = "",
    onCarTypeTextSearchChange: (String) -> Unit,
    naoloanDescriptions: List<NaolonDescription> = emptyList(),
    onNaolonDescriptionSelected: (NaolonDescription) -> Unit,
    naolonDescriptionTextSearch: String = "",
    onNaolonDescriptionTextSearchChange: (String) -> Unit,
    naolonDescriptionErrorMessage: Message? = null,
    naolonLoadingStatus: NaolonLoadingStatus
) {
    val progress = if ((mat.orderedQuantity ?: 0.0) > 0) (mat.receivedQuantity ?: 0.0) / mat.orderedQuantity!! else 0.0
    val colors = AppTheme.colors
    val dims = AppTheme.dims

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f)).border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(mat.materialCode ?: "", color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.ExtraBold)
            Text("✕", color = colors.muted, fontSize = dims.fontSizeBody,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription ?: "", color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text("#${mat.poNumber ?: ""}", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(doneLabel to mat.receivedQuantity.toString(), stringResource(R.string.required) to mat.orderedQuantity.toString(),
                stringResource(R.string.unit) to (mat.uom ?: ""),).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = dims.fontSizeCaption, modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = dims.fontSizeCaption)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = dims.fontSizeCaption, fontWeight = FontWeight.SemiBold)
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
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(shape=RoundedCornerShape(size = 10.dp))
                .background(accent.copy(0.3f))
                .border(1.dp, accent, RoundedCornerShape(10.dp))
        ){
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    state.transportingType?.arName?:"",
                    textAlign = TextAlign.Center,
                    color = colors.text
                )
            }
        }
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
        
//        Spacer(Modifier.height(10.dp))
////        CarNumberSpinner(
////            hint = stringResource(R.string.select_car_number),
////            items = carNumbersList,
////            onItemSelected = onCarNoSelected,
////            accent = accent,
////            label = stringResource(R.string.car_number),
////            searchText = carNumberSearchText,
////            onSearchTextChanged = { onCarNumberSearchTextChange(it) }
////        )
//        if (carNoError.isNotEmpty()) Text(
//            carNoError,
//            color = DANGER,
//            fontSize = 10.sp,
//            modifier = Modifier.padding(top = 4.dp)
//        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg.copy(alpha = 0.65f)).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.car_number),color = colors.muted)
                Text(state.selectedMaterial?.factoryCarDescription?:state.selectedMaterial?.carNo?:"", color = accent, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(10.dp))
        Field(
            label = stringResource(R.string.unloading_point),
            hint = stringResource(R.string.enter_unloading_point),
            value = unloadingPoint,
            onValueChange = onUnloadingPointChange,
            isNumber = false,
            center = true
            )
        if (unloadingPointError != null) Text(unloadingPointError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
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
        if (workersGroupError != null) Text(workersGroupError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))

        Spacer(Modifier.height(10.dp))

        Text(stringResource(R.string.production_date), color = colors.muted, fontSize = dims.fontSizeCaption)
        DatePickerField(
            label = stringResource(R.string.production_date),
            hint = stringResource(R.string.please_select_production_date),
            value = productionDate,
            onDateSelected = onProductionDateEntered,
            dateFormatter = { millis ->
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(Date(millis))
            }
        )
        if (productionDateError != null) Text(productionDateError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        Log.d("SelectedTransitMaterialCard", "SelectedTransitMaterialCard: ${state.selectedMaterial?.isFactoryCar}")
        if (state.selectedMaterial?.isFactoryCar!=true) {
            Spacer(Modifier.height(10.dp))
            LoadingVendorSpinner(
                items = loadingVendors,
                onItemSelected = onLoadingVendorSelected,
                accent = accent,
                searchText = loadingVendorSearchText,
                onSearchTextChanged = { onSearchLoadingVendorTextChange(it) }
            )
            if (loadingVendorError != null) Text(
                loadingVendorError!!.text,
                color = DANGER,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
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
                    Text(stringResource(R.string.loading_vendor),color = colors.muted)
                    Text(state.selectedLoadingVendor?.vendorName?:"", color = accent, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        PackingTypeSpinner(
            hint = stringResource(R.string.select_packing_type),
            items = packingTypes,
            onItemSelected = onPackingTypeSelected,
            accent = accent,
            searchText = packingTypeSearchText,
            onSearchTextChanged = { onSearchPackingTypeTextChange(it) }
        )
        if (packingTypeError != null) Text(packingTypeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
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
            isFactoryCar = state.selectedMaterial?.isFactoryCar?:true
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
fun ReceivingTransitMaterialsLineItem(item: TransitOrderMaterialLine, onDelete: () -> Unit, onEdit: () -> Unit) {
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
                        Chip(stringResource(R.string.production_date) + ": " + item.contNo.take(10))
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
    val dims = AppTheme.dims
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
                            Text(state.searchError!!.text, color = colors.text)
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
                                start = dims.padding, end = dims.padding, top = 10.dp,
                                bottom = if (state.addedItems.isNotEmpty()) dims.bottomBarHeight else 20.dp
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
                                        ReceivingInfoCell(stringResource(R.string.factory_car_description), order.factoryCarDescription ?: "", colors.text)
                                    }
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        Spacer(Modifier.height(4.dp))
                                        ReceivingInfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text)
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
                                            if (state.materialCodeError != null){
                                                Text(state.materialCodeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
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
//                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            productionDate = state.productionDate,
                                            onProductionDateEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnProductionDateEntered(it)) },
                                            productionDateError = state.productionDateError,
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
                                            state = state,
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
                                ReceivingTransitMaterialsLineItem(
                                    item = item,
                                    onDelete = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
                                    },
                                    onEdit = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnEditAddedMaterialButtonClicked(index))
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
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.save), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
//            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        val dims = AppTheme.dims
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = dims.padding, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = dims.padding, end = dims.padding, bottom = 20.dp)) {
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
    val dims = AppTheme.dims
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
                            Text(state.searchError!!.text, color = colors.text)
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
                                start = dims.padding, end = dims.padding, top = 10.dp,
                                bottom = if (state.addedItems.isNotEmpty()) dims.bottomBarHeight else 20.dp
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
                                        ReceivingInfoCell(stringResource(R.string.factory_car_description), order.factoryCarDescription ?: "", colors.text)
                                    }
                                    if (!order.warehouseCode.isNullOrEmpty()){
                                        Spacer(Modifier.height(4.dp))
                                        ReceivingInfoCell(stringResource(R.string.warehouse), "${order.warehouseCode} - ${order.warehouseDescription}", colors.text)
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
                                            if (state.materialCodeError != null){
                                                Text(state.materialCodeError!!.text, color = DANGER, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
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
//                                            onCarNoSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnCarNoSelected(it)) },
                                            unloadingPoint = state.unloadingPoint,
                                            unloadingPointError = state.unloadingPointError,
                                            onUnloadingPointChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnUnloadingPointEntered(it)) },
                                            workersGroup = state.workerGroups,
                                            workersGroupSearchText = state.workerGroupSearchText,
                                            onWorkersGroupSearchTextChange = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupTextSearchChange(it)) },
                                            onWorkersGroupSelected = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnWorkerGroupSelected(it)) },
                                            workersGroupError = state.workerGroupError,
                                            productionDate = state.productionDate,
                                            onProductionDateEntered = { viewModel.onEvent(ReceivingProductFromBranchEvent.OnProductionDateEntered(it)) },
                                            productionDateError = state.productionDateError,
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
                                            state =  state,
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
                                ReceivingTransitMaterialsLineItem(
                                    item = item,
                                    onDelete = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnDeleteAddedMaterialButtonClicked(index))
                                    },
                                    onEdit = {
                                        viewModel.onEvent(ReceivingProductFromBranchEvent.OnEditAddedMaterialButtonClicked(index))
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
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(R.string.confirm_putaway), accent,
                    { viewModel.onEvent(ReceivingProductFromBranchEvent.OnSaveButtonClicked) },
                    saving = state.isSavingLoading
                )
            }
        }

        if (state.showSuccessDialog) {
            SuccessDialog(colors.succ, state.savingSuccessStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideSuccessDialog)
            }
//            navController.popBackStack()
        }
        if (state.showErrorDialog) {
            ErrorDialog(DANGER, state.savingErrorStatusMessage!!.text) {
                viewModel.onEvent(ReceivingProductFromBranchEvent.HideErrorDialog)
            }
        }
    }

    if (viewModel.matDlg) {
        val dims = AppTheme.dims
        BottomSheetDialog(title = stringResource(R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(state.materialSearch, { viewModel.onEvent(ReceivingProductFromBranchEvent.OnMaterialSearchTextEntered(it)) },
                stringResource(R.string.search_for_item), Modifier.padding(horizontal = dims.padding, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(start = dims.padding, end = dims.padding, bottom = 20.dp)) {
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
    val dims = AppTheme.dims
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(colors.dark).border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }.padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(mat.materialCode ?: "", color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
            Text("#${mat.poNumber ?: ""}", color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.materialDescription ?: "", color = colors.text, fontSize = dims.fontSizeBody, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("${stringResource(R.string.required)}: ${mat.orderedQuantity}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
            Text("$doneLabel: ${mat.receivedQuantity}", color = if (mat.receivedQuantity == mat.orderedQuantity) colors.succ else colors.muted, fontSize = dims.fontSizeSubtitle)
        }
    }
}
