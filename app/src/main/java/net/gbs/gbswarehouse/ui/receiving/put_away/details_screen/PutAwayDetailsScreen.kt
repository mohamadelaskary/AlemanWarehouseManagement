package net.gbs.gbswarehouse.ui.receiving.put_away.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.*
import net.gbs.gbswarehouse.domain.model.PutAwayOrderLine
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.ScannerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutAwayDetailsScreen(
    navController: NavController,
    receiptType: String,
    orderNumber: String,
    viewModel: PutAwayDetailsViewModel = hiltViewModel()
) {
    val orderLines by viewModel.orderLines.collectAsState()
    val selectedBin by viewModel.selectedBin.collectAsState()
    val selectedLine by viewModel.selectedLine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val colors = AppTheme.colors

    var binCode by remember { mutableStateOf("") }
    var materialCode by remember { mutableStateOf("") }
    var putAwayQty by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(receiptType, orderNumber) {
        viewModel.getOrderLines(receiptType, orderNumber)
    }

    ScannerEffect { barcode ->
        // Logic to decide if it's a bin or material
        if (selectedBin == null || barcode.length > 8) {
             // Likely a material or a long bin code? 
             // Actually let's just try both or use a specific order.
             // Usually, bin is scanned first.
             if (selectedBin == null) {
                 binCode = barcode
                 viewModel.validateBin(barcode)
             } else {
                 materialCode = barcode
                 val line = orderLines.find { l -> l.materialCode == barcode }
                 if (line != null) viewModel.selectLine(line)
             }
        } else {
             binCode = barcode
             viewModel.validateBin(barcode)
        }
    }

    if (errorMessage != null) {
        ErrorDialog(accent = YELLOW, message = errorMessage!!) {
            viewModel.clearError()
        }
    }

    if (successMessage != null) {
        SuccessDialog(accent = GREEN, message = successMessage!!) {
            viewModel.clearSuccess()
            putAwayQty = ""
            materialCode = ""
            // Re-fetch data to update remaining quantities
            viewModel.getOrderLines(receiptType, orderNumber)
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.putaway),
                subtitle = arabicScreenNames(receiptType),
                accent = YELLOW,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        },
        containerColor = colors.bg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            item {
                Section(label = stringResource(R.string.order_data), accent = YELLOW) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCell(label = stringResource(R.string.po_number), value = orderNumber, modifier = Modifier.weight(1f))
                        InfoCell(label = stringResource(R.string.items), value = orderLines.size.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            // Bin Selection Section
            item {
                Section(label = stringResource(R.string.putaway_data), accent = YELLOW) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Field(
                            label = stringResource(R.string.select_bin),
                            hint = stringResource(R.string.bin_code_hint),
                            value = binCode,
                            onValueChange = {
                                binCode = it
                                if (it.length >= 5) viewModel.validateBin(it)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedBin != null) {
                            IconButton(
                                onClick = {
                                    binCode = ""
                                    viewModel.clearBin()
                                },
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.dark)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Bin", tint = DANGER)
                            }
                        }
                    }
                    if (selectedBin != null) {
                        MarginVertical(12.dp)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCell(label = stringResource(R.string.zone), value = selectedBin!!.sectionName, modifier = Modifier.weight(1f))
                            InfoCell(label = stringResource(R.string.warehouse), value = selectedBin!!.warehouseCode, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Material Selection Section
            item {
                Section(label = stringResource(R.string.material_details), accent = YELLOW) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Field(
                            label = stringResource(R.string.material_code),
                            hint = stringResource(R.string.scan_or_enter_material_code),
                            value = materialCode,
                            onValueChange = {
                                materialCode = it
                                val line = orderLines.find { l -> l.materialCode == it }
                                if (line != null) viewModel.selectLine(line)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.dark)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search Material", tint = YELLOW)
                        }
                    }

                    selectedLine?.let { line ->
                        MarginVertical(16.dp)
                        PutAwayMaterialCard(
                            line = line,
                            accent = YELLOW,
                            putAwayQty = putAwayQty,
                            onQtyChange = { putAwayQty = it },
                            onClear = {
                                viewModel.selectLine(null)
                                materialCode = ""
                                putAwayQty = ""
                            }
                        )
                    }
                }
            }

            item {
                MarginVertical(16.dp)
                FullBtn(
                    label = stringResource(R.string.confirm_putaway),
                    color = YELLOW,
                    onClick = {
                        val qty = putAwayQty.toDoubleOrNull() ?: 0.0
                        if (qty > 0 && qty <= (selectedLine?.remainingQuantity ?: 0.0)) {
                            viewModel.confirmPutAway(qty)
                        } else {
                            // Show validation error
                        }
                    },
                    enabled = selectedBin != null && selectedLine != null && putAwayQty.isNotEmpty(),
                    saving = isLoading
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colors.card
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orderLines) { line ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.dark)
                            .clickable {
                                viewModel.selectLine(line)
                                materialCode = line.materialCode
                                showBottomSheet = false
                            }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(line.materialCode, color = YELLOW, fontWeight = FontWeight.Bold)
                            Text(line.materialDescription ?: "", color = colors.text, fontSize = 12.sp)
                            Text("Rem: ${line.remainingQuantity} ${line.uom}", color = colors.muted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PutAwayMaterialCard(
    line: PutAwayOrderLine,
    accent: Color,
    putAwayQty: String,
    onQtyChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val colors = AppTheme.colors
    val progress = if (line.receivedQuantity > 0) line.putAwayQuantity.toFloat() / line.receivedQuantity.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(0.05f))
            .border(1.dp, accent.copy(0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = line.materialCode,
                color = accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = line.materialDescription ?: "",
            color = colors.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        MarginVertical(12.dp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoCell(
                label = stringResource(R.string.received_qty),
                value = line.receivedQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.putaway_qty),
                value = line.putAwayQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.required),
                value = line.remainingQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        MarginVertical(12.dp)

        // Progress Section
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = 10.sp)
                Text(
                    "${(progress * 100).toInt()}%",
                    color = accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.dark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(3.dp))
                        .background(accent)
                )
            }
        }

        MarginVertical(16.dp)

        Field(
            label = stringResource(R.string.putaway_qty),
            hint = "0",
            value = putAwayQty,
            onValueChange = onQtyChange,
            isNumber = true,
            center = true
        )
    }
}
