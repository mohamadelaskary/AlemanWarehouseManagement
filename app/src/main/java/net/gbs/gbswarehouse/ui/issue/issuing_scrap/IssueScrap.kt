package net.gbs.gbswarehouse.ui.issue.issuing_scrap

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SuccessDialog
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.ui.issue.OnHandStockSection
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.ScannerEffect
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.spinnerLabel
import net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer.IssuingSelectedBinCard
import net.gbs.gbswarehouse.ui.theme.AppColors

@Composable
fun IssueScrapScreen(navController: NavController) {
    val viewModel: IssueScrapViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val colors = AppTheme.colors
    val accent = YELLOW

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = stringResource(R.string.issuing), subtitle = stringResource(R.string.scrap),
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
                // 1. Maintenance Order Number
                item {
                    Field(
                        label = stringResource(R.string.maintenance_order_number),
                        hint = stringResource(R.string.enter_maintenance_order_number),
                        value = state.documentHeaderText,
                        onValueChange = { viewModel.onEvent(IssueScrapEvent.OnDocumentHeaderChanged(it)) },
                        isNumber = false,
                        withLabel = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.documentHeaderError != null) {
                        Text(state.documentHeaderError!!.text, color = colors.danger, fontSize = 10.sp)
                    }
                    MarginTenDp()
                }

                // 2. Material Section (First step now)
                item {
                    Section(label = stringResource(R.string.item_data), accent = accent) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                MatCodeInput(
                                    value = state.matInput,
                                    onValueChange = {
                                        viewModel.onEvent(IssueScrapEvent.OnMatInputChanged(it))
                                        if (it.length >= 4) viewModel.onEvent(
                                            IssueScrapEvent.OnScanMaterial(it)
                                        )
                                    },
                                    colors = colors
                                )
                                if (state.matErrorMessage != null) {
                                    Text(
                                        state.matErrorMessage!!.text,
                                        color = colors.danger,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                            OutlineBtn(
                                stringResource(R.string.select_item),
                                accent
                            ) {
                                viewModel.onEvent(IssueScrapEvent.OnToggleMaterialDialog)
                            }
                        }
                    }

                    if (state.selectedMatStock != null) {
                        Spacer(Modifier.height(8.dp))
                        IssueScrapSelectedMaterialCard(
                            state = state,
                            accent = accent,
                            viewModel = viewModel,
                            onClear = { viewModel.onEvent(IssueScrapEvent.OnClearMaterial) }
                        )
                    }
                }

                // 3. Added Items Section
                if (state.addedLines.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(10.dp))
                        Section(label = stringResource(R.string.added_items), accent = accent) {
                            state.addedLines.forEachIndexed { index, line ->
                                ScrapAddedLineItem(
                                    materialCode = line.materialCode,
                                    materialDescription = line.materialDescription?:"",
                                    qty = line.quantity,
                                    uom = line.uom,
                                    costCenter = line.costCenterCode,
                                    itemText = line.itemText ?: "",
                                    company = line.companyCode,
                                    batch = line.batch,
                                    valuationType = line.valuationType,
                                    accent = accent,
                                    onDelete = { viewModel.onEvent(IssueScrapEvent.OnDeleteLine(index)) },
                                    onEdit = { viewModel.onEvent(IssueScrapEvent.OnEditLine(index)) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

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
                    stringResource(R.string.save_issuing), accent, { viewModel.onEvent(IssueScrapEvent.OnIssueAll) },
                    saving = state.isSaving
                )
            }
        }

        if (state.savingSuccessMessage != null) {
            SuccessDialog(accent = colors.green, message = state.savingSuccessMessage!!.text, onClose = {
                viewModel.onEvent(IssueScrapEvent.OnDismissDialogs)
            })
        }
        if (state.savingErrorMessage != null) {
            ErrorDialog(accent = colors.danger, message = state.savingErrorMessage!!.text, onClose = {
                viewModel.onEvent(IssueScrapEvent.OnDismissDialogs)
            })
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
        BottomSheetDialog(title = stringResource(R.string.select_item), colors = colors, onClose = { viewModel.onEvent(IssueScrapEvent.OnToggleMaterialDialog) }) {
            SearchBox(
                state.matSearchText, { viewModel.onEvent(IssueScrapEvent.OnMaterialSearchTextChanged(it)) }, stringResource(R.string.search_for_item),
                Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                val filtered = state.eligibleStock.filter { 
                    it.materialCode?.contains(state.matSearchText, true) == true || 
                    it.description?.contains(state.matSearchText, true) == true 
                }
                itemsIndexed(filtered) { _, stock ->
                    ScrapMaterialDialogItem(stock, accent) {
                        viewModel.onEvent(IssueScrapEvent.OnMaterialSelected(stock))
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun BottomSheetDialog(title: String, colors: AppColors, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border2,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .heightIn(max = 560.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* consume */ }
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp, color = colors.border,
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✕", color = colors.muted, fontSize = 16.sp,
                        modifier = Modifier.clickable { onClose() }.padding(horizontal = 2.dp))
                    Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                content()
            }
        }
    }
}

@Composable
fun BinLocationSearchableSpinner(
    items: List<net.gbs.gbswarehouse.domain.model.MaterialBinLocation>,
    onItemSelected: (net.gbs.gbswarehouse.domain.model.MaterialBinLocation) -> Unit,
    accent: Color,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    isLoading: Boolean
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.width(3.dp).height(12.dp).background(accent, RoundedCornerShape(2.dp)))
            Text(stringResource(R.string.bin), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            if (isLoading) {
                Spacer(Modifier.width(8.dp))
                CircularProgressIndicator(color = accent, modifier = Modifier.height(14.dp).width(14.dp), strokeWidth = 2.dp)
            }
        }
        Spacer(Modifier.height(8.dp))
        SearchableSpinner(
            hint = stringResource(R.string.select_bin),
            items = items,
            itemLabel = { it.spinnerLabel() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@Composable
fun IssueScrapSelectedMaterialCard(
    state: IssueScrapState,
    accent: Color,
    viewModel: IssueScrapViewModel,
    onClear: () -> Unit,
) {
    val colors = AppTheme.colors
    val mat = state.selectedMatStock ?: return
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(mat.materialCode ?: "", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(mat.description ?: "", color = colors.text, fontSize = 12.sp)
            }
            IconButton(onClick = onClear) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = colors.muted)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.issued_from_warehouse_location_data), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        BinLocationSearchableSpinner(
            items = state.binLocations,
            onItemSelected = { viewModel.onEvent(IssueScrapEvent.OnBinLocationSelected(it)) },
            accent = accent,
            searchText = state.binSearchText,
            onSearchTextChanged = { viewModel.onEvent(IssueScrapEvent.OnBinSearchTextChanged(it)) },
            isLoading = state.isBinLocationsLoading
        )
        if (state.binCodeError != null){
            Text(state.binCodeError!!.text, color = colors.danger, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        if (state.selBinData != null) {
            Spacer(Modifier.height(8.dp))
            IssuingSelectedBinCard(
                bin = state.selBinData,
                accent = accent,
                onClear = { viewModel.onEvent(IssueScrapEvent.OnClearBin) }
            )
        }

        if (state.selBinData != null) {
            Spacer(Modifier.height(8.dp))
            OnHandStockSection(
                stockList = state.stockOnHandList,
                selectedStock = state.selectedOnHandStock,
                availableQty = state.availableStock,
                isStockLoading = state.isStockLoading,
                stockError = state.stockError,
                stockSearchText = state.stockSearch,
                onSearchTextChanged = { viewModel.onEvent(IssueScrapEvent.OnStockSearchTextChanged(it)) },
                onStockSelected = { viewModel.onEvent(IssueScrapEvent.OnOnHandStockSelected(it)) },
                accent = accent,
                colors = colors
            )
        }
        
        MarginTenDp()
        
        CompanySearchableSpinner(
            items = state.companies,
            onItemSelected = { viewModel.onEvent(IssueScrapEvent.OnCompanySelected(it)) },
            accent = accent,
            searchText = state.companySearchText,
            onSearchTextChanged = { viewModel.onEvent(IssueScrapEvent.OnCompanySearchTextChanged(it)) }
        )

        if (state.selectedCompany != null) {
            MarginTenDp()
            CostCenterSearchableSpinner(
                items = state.costCenters,
                onItemSelected = { viewModel.onEvent(IssueScrapEvent.OnCostCenterSelected(it)) },
                accent = accent,
                searchText = state.costCenterSearchText,
                onSearchTextChanged = { viewModel.onEvent(IssueScrapEvent.OnCostCenterSearchTextChanged(it)) }
            )
        }

        MarginTenDp()
        Field(label = stringResource(R.string.notes), hint = stringResource(R.string.notes), value = state.notes?:"",
            onValueChange = { viewModel.onEvent(IssueScrapEvent.OnNotesChanged(it)) })

        MarginTenDp()
        Field(
            label = "${stringResource(R.string.issued_qty)} (${state.maxQty})",
            hint = stringResource(R.string.enter_quantity),
            value = state.quantity,
            onValueChange = { viewModel.onEvent(IssueScrapEvent.OnQuantityChanged(it)) },
            isNumber = true,
            withLabel = true
        )
        if (state.quantityError != null) {
            Text(state.quantityError!!.text, color = colors.danger, fontSize = 10.sp)
        }

        Spacer(Modifier.height(16.dp))
        FullBtn(
            label = stringResource(R.string.add),
            color = accent,
            onClick = { viewModel.onEvent(IssueScrapEvent.OnAddLine) },
            enabled = state.selectedCostCenter != null &&
                      state.selBinData != null &&
                      state.quantity.isNotBlank() && state.quantityError == null
        )
    }
}

@Composable
fun CompanySearchableSpinner(
    items: List<Company>,
    onItemSelected: (Company) -> Unit,
    accent: Color,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.width(3.dp).height(12.dp).background(accent, RoundedCornerShape(2.dp)))
            Text(stringResource(R.string.company), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        SearchableSpinner(
            hint = stringResource(R.string.select_company),
            items = items,
            itemLabel = { "${it.companyDescription} (${it.companyCode})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@Composable
fun CostCenterSearchableSpinner(
    items: List<CostCenter>,
    onItemSelected: (CostCenter) -> Unit,
    accent: Color,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.width(3.dp).height(12.dp).background(accent, RoundedCornerShape(2.dp)))
            Text(stringResource(R.string.cost_center), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        SearchableSpinner(
            hint = stringResource(R.string.select_cost_center),
            items = items,
            itemLabel = { "${it.costCenterDescription} (${it.costCenterCode})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@Composable
fun ScrapMaterialDialogItem(stock: SAPMaterialBatchValuationStock, accent: Color, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(10.dp))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stock.materialCode ?: "", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("${stock.quantity ?: 0} ${stock.unitOfMeasure ?: ""}", color = colors.muted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(stock.description ?: "", color = colors.text, fontSize = 13.sp)
        val batchLabel = if (stock.batchManagement.isNullOrEmpty()) stringResource(R.string.valuation_type_) else stringResource(R.string.batch)+": "
        if (!stock.batch.isNullOrEmpty()) {
            Text(batchLabel+" ${stock.batch}", color = colors.muted, fontSize = 10.sp)
        }
    }
}

@Composable
fun ScrapAddedLineItem(
    materialCode: String,
    materialDescription: String,
    qty: Double,
    uom: String,
    costCenter: String,
    itemText: String,
    company: String? = null,
    batch: String? = null,
    valuationType: String? = null,
    accent: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(materialDescription, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScrapChip(stringResource(R.string.cost_center) + ": $costCenter", accent)
                if (company != null)
                    ScrapChip(stringResource(R.string.company) + ": $company", accent)
                if (batch != null)
                    ScrapChip(stringResource(R.string.batch) + ": $batch", accent)
                if (valuationType != null)
                    ScrapChip(stringResource(R.string.valuation_type) + ": $valuationType", accent)
                if (itemText.isNotEmpty())
                    ScrapChip(itemText, accent)
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

@Composable
fun ScrapChip(text: String, accent: Color) {
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
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, colors: AppColors) {
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
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(stringResource(R.string.material_code_), color = colors.text, fontSize = 13.sp)
                inner()
            }
        )
    }
}
