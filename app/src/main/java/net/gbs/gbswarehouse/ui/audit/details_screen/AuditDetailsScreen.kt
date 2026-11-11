package net.gbs.gbswarehouse.ui.audit.details_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.*
import net.gbs.gbswarehouse.domain.model.InventoryAssignment
import net.gbs.gbswarehouse.domain.model.InventoryOrder
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.ui.MaterialImageButton
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun AuditDetailsScreen(
    navController: NavController,
    orderId: String
) {
    val viewModel: AuditDetailsViewModel = hiltViewModel()
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val accent = BLUE
    val context = LocalContext.current

    LaunchedEffect(orderId) {
        viewModel.initOrder(orderId.toIntOrNull() ?: 1)
    }

    val successText = viewModel.successMessage?.text
    LaunchedEffect(successText) {
        if (!successText.isNullOrEmpty()) {
            Toast.makeText(context, successText, Toast.LENGTH_SHORT).show()
            viewModel.successMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(net.gbs.gbswarehouse.R.string.audit),
                subtitle = stringResource(net.gbs.gbswarehouse.R.string.order_data),
                accent = accent,
                onBack = { navController.popBackStack() }
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding,
                    end = dims.padding,
                    top = 10.dp,
                    bottom = dims.bottomBarHeight + 20.dp
                )
            ) {
                // Order Info Card
                viewModel.order?.let { ord ->
                    item {
                        Section(label = stringResource(net.gbs.gbswarehouse.R.string.order_data), accent = accent) {
                            val fields = listOf(
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.order_number), ord.orderNumber, null),
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.description), ord.description ?: "-", null),
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.warehouse), ord.warehouseCode ?: "-", null),
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.plant), ord.plantCode ?: "-", null),
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.status), ord.status ?: "-", null),
                                Triple(stringResource(net.gbs.gbswarehouse.R.string.scanned_items), "${ord.scannedItemCount ?: 0} / ${ord.assignedItemCount ?: 0}", accent)
                            )
                            for (i in fields.indices step 2) {
                                Row(
                                    Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val f1 = fields[i]
                                    val f2 = if (i + 1 < fields.size) fields[i + 1] else null
                                    InfoCell(f1.first, f1.second, f1.third ?: colors.text, modifier = Modifier.weight(1f))
                                    if (f2 != null) {
                                        InfoCell(f2.first, f2.second, f2.third ?: colors.text, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }

                // Bin Section
                item {
                    Section(label = stringResource(net.gbs.gbswarehouse.R.string.bin), accent = accent) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BinCodeInput(
                                value = viewModel.binInput,
                                onValueChange = {
                                    viewModel.binInput = it
                                    if (it.isNotBlank()) {
                                        viewModel.scanOrEnterBin(it)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (viewModel.selBin != null) {
                            Spacer(Modifier.height(8.dp))
                            SelectedBinLocationCard(
                                bin = viewModel.selBin!!,
                                accent = accent,
                                onClear = { viewModel.clearBin() }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // Material Section
                item {
                    Section(label = stringResource(net.gbs.gbswarehouse.R.string.item_data), accent = accent) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MatCodeInput(
                                value = viewModel.matInput,
                                onValueChange = {
                                    viewModel.matInput = it
                                    if (it.isNotBlank()) {
                                        viewModel.onBarcodeScanned(it)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            OutlineBtn(stringResource(net.gbs.gbswarehouse.R.string.select_item), accent) {
                                viewModel.matDlg = true
                            }
                        }
                        if (viewModel.selAssignment != null) {
                            Spacer(Modifier.height(8.dp))
                            SelectedAssignmentCard(
                                assignment = viewModel.selAssignment!!,
                                accent = accent,
                                qty = viewModel.qty,
                                onQtyChange = { viewModel.qty = it },
                                onClear = { viewModel.clearMaterial() }
                            )
                        }
                    }
                }
            }
        }

        // Error Dialog
        val errorText = (viewModel.saveErrorMessage ?: viewModel.materialErrorMessage ?: viewModel.binErrorMessage)?.text
        val activeError = viewModel.saveErrorMessage ?: viewModel.materialErrorMessage ?: viewModel.binErrorMessage
        if (activeError != null && errorText != null) {
            ErrorDialog(
                accent = Color.Red,
                message = errorText,
                onClose = {
                    viewModel.saveErrorMessage = null
                    viewModel.materialErrorMessage = null
                    viewModel.binErrorMessage = null
                }
            )
        }

        // Fixed Save Button
        if (viewModel.selAssignment != null && viewModel.selBin != null && viewModel.qty.isNotEmpty() && !viewModel.saved) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, colors.bg)
                        )
                    )
                    .padding(horizontal = dims.padding, vertical = 12.dp)
            ) {
                FullBtn(
                    stringResource(net.gbs.gbswarehouse.R.string.save),
                    accent,
                    { viewModel.saveScan() },
                    saving = viewModel.saving
                )
            }
        }
    }

    // Material Selection Dialog
    if (viewModel.matDlg) {
        BottomSheetDialog(title = stringResource(net.gbs.gbswarehouse.R.string.select_item), onClose = { viewModel.matDlg = false }) {
            SearchBox(
                viewModel.matSearch,
                { viewModel.searchMaterials(it) },
                stringResource(net.gbs.gbswarehouse.R.string.search_for_item),
                Modifier.padding(horizontal = dims.padding, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding, end = dims.padding, bottom = 20.dp
                )
            ) {
                itemsIndexed(viewModel.filteredMaterials) { index, assignment ->
                    if (index == viewModel.filteredMaterials.size - 1 && viewModel.hasMore) {
                        viewModel.loadMoreAssignments()
                    }
                    AssignmentDialogItem(assignment, accent) {
                        viewModel.selectAssignment(assignment)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    // Bin Selection Dialog
    if (viewModel.binDlg) {
        BottomSheetDialog(title = stringResource(net.gbs.gbswarehouse.R.string.select_bin), onClose = { viewModel.binDlg = false }) {
            SearchBox(
                viewModel.binInput,
                { viewModel.binInput = it },
                stringResource(net.gbs.gbswarehouse.R.string.search_for_bin),
                Modifier.padding(horizontal = dims.padding, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dims.padding, end = dims.padding, bottom = 20.dp
                )
            ) {
                itemsIndexed(BINS) { _, b ->
                    BinDialogItem(b, accent) {
                        viewModel.scanOrEnterBin(b.code)
                        viewModel.binDlg = false
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
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
                if (value.isEmpty()) Text(stringResource(net.gbs.gbswarehouse.R.string.bin_code_hint), color = colors.muted, fontSize = 13.sp)
                inner()
            }
        )
    }
}

@Composable
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
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
            cursorBrush = SolidColor(colors.green),
            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(stringResource(net.gbs.gbswarehouse.R.string.item_code_hint), color = colors.muted, fontSize = 13.sp)
                inner()
            }
        )
    }
}

@Composable
fun SelectedBinLocationCard(
    bin: StorageBinLocation,
    accent: Color,
    onClear: () -> Unit
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📍 ${bin.storageBinCode}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                "المنطقة" to bin.sectionCode,
                "الممر" to bin.roadCode,
                "المخزن" to bin.warehouseCode
            ).forEach { (l, v) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.dark)
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text(l, color = colors.muted, fontSize = 9.sp)
                    Text(v, color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SelectedAssignmentCard(
    assignment: InventoryAssignment,
    accent: Color,
    qty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(assignment.itemCode ?: "", color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(8.dp))
                if (!assignment.itemCode.isNullOrEmpty()) {
                    MaterialImageButton(materialCode = assignment.itemCode, accent = accent)
                }
            }
            Text(
                "✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            assignment.itemName ?: "", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Field(stringResource(net.gbs.gbswarehouse.R.string.counted_qty), "0", qty, onQtyChange, isNumber = true, center = true)
    }
}

@Composable
fun AssignmentDialogItem(assignment: InventoryAssignment, accent: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) accent.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(assignment.itemCode ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(assignment.warehouseCode ?: "", color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            assignment.itemName ?: "", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) colors.yellow.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📍 ${bin.code}", color = colors.yellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle)
                .forEach { (l, v) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(l, color = colors.muted, fontSize = 9.sp)
                        Text(v, color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
        }
    }
}
