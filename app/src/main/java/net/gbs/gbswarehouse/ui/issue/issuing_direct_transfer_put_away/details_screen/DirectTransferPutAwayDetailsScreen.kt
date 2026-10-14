package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer_put_away.details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import net.gbs.gbswarehouse.domain.model.DirectTransferPutAwayLine
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.honeywellscanner.ScannerEffect

@Composable
fun DirectTransferPutAwayDetailsScreen(
    navController: NavController,
    receiptNo: String,
    viewModel: DirectTransferPutAwayDetailsViewModel = hiltViewModel()
) {
    val lines by viewModel.lines.collectAsState()
    val selectedLine by viewModel.selectedLine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val colors = AppTheme.colors

    var storageBinCode by remember { mutableStateOf("") }
    var materialCode by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    LaunchedEffect(receiptNo) {
        viewModel.getLines(receiptNo)
    }

    ScannerEffect(onScan = viewModel::onBarcodeScanned)

    if (errorMessage != null) {
        ErrorDialog(accent = DANGER, message = errorMessage!!.text) {
            viewModel.clearError()
        }
    }

    if (validationMessage != null) {
        ErrorDialog(accent = YELLOW, message = validationMessage!!.text) {
            viewModel.clearValidation()
        }
    }

    if (successMessage != null) {
        SuccessDialog(accent = GREEN, message = successMessage!!.text) {
            viewModel.clearSuccess()
            storageBinCode = ""
            materialCode = ""
            quantity = ""
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.stock_transfers_putaway),
                subtitle = stringResource(R.string.transfer_details),
                accent = YELLOW,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Section(label = stringResource(R.string.order_data), accent = YELLOW) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCell(
                                label = stringResource(R.string.receipt_no),
                                value = receiptNo,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCell(
                                label = stringResource(R.string.items),
                                value = lines.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Section(label = stringResource(R.string.material_details), accent = YELLOW) {
                        Field(
                            label = stringResource(R.string.material_code),
                            hint = stringResource(R.string.scan_or_enter_material_code),
                            value = materialCode,
                            onValueChange = {
                                materialCode = it
                                val line = lines.find { l -> l.materialCode == it }
                                if (line != null) {
                                    viewModel.selectLine(line)
                                } else if (it.isEmpty()) {
                                    viewModel.selectLine(null)
                                }
                            }
                        )
                        MarginVertical(12.dp)
                        if (lines.isEmpty() && !isLoading) {
                            Text(stringResource(R.string.no_results), color = colors.muted)
                        } else {
                            lines.forEach { line ->
                                DirectTransferPutAwayLineRow(
                                    line = line,
                                    isSelected = selectedLine?.goodsReceiptLineId == line.goodsReceiptLineId,
                                    accent = YELLOW,
                                    onClick = {
                                        viewModel.selectLine(line)
                                        materialCode = line.materialCode
                                    }
                                )
                                MarginVertical(8.dp)
                            }
                        }
                    }
                }

                selectedLine?.let { line ->
                    item {
                        Section(label = stringResource(R.string.putaway_data), accent = YELLOW) {
                            DirectTransferPutAwayLineCard(
                                line = line,
                                accent = YELLOW,
                                onClear = {
                                    viewModel.selectLine(null)
                                    materialCode = ""
                                    storageBinCode = ""
                                    quantity = ""
                                }
                            )
                        }
                    }

                    item {
                        Section(label = stringResource(R.string.storage_bin), accent = YELLOW) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Field(
                                    label = stringResource(R.string.select_bin),
                                    hint = stringResource(R.string.enter_storage_bin),
                                    value = storageBinCode,
                                    onValueChange = { storageBinCode = it },
                                    modifier = Modifier.weight(1f)
                                )
                                if (storageBinCode.isNotEmpty()) {
                                    IconButton(
                                        onClick = { storageBinCode = "" },
                                        modifier = Modifier
                                            .padding(top = 20.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.dark)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear Bin", tint = DANGER)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Section(label = stringResource(R.string.putaway_qty), accent = YELLOW) {
                            Field(
                                label = stringResource(R.string.putaway_qty),
                                hint = "0",
                                value = quantity,
                                onValueChange = { quantity = it },
                                isNumber = true,
                                center = true
                            )
                        }
                    }

                    item {
                        MarginVertical(8.dp)
                        FullBtn(
                            label = stringResource(R.string.confirm_putaway),
                            color = YELLOW,
                            onClick = {
                                viewModel.confirmPutAway(storageBinCode, quantity.toDoubleOrNull() ?: 0.0)
                            },
                            enabled = storageBinCode.isNotBlank() && quantity.isNotBlank(),
                            saving = isLoading
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YELLOW)
                }
            }
        }
    }
}

@Composable
fun DirectTransferPutAwayLineRow(
    line: DirectTransferPutAwayLine,
    isSelected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accent.copy(0.1f) else colors.dark)
            .border(
                1.dp,
                if (isSelected) accent.copy(0.5f) else colors.border,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = line.materialCode,
                color = if (isSelected) accent else colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${stringResource(R.string.remaining_quantity_)} ${line.remainingQuantity.toString()} ${line.uom.orEmpty()}",
                color = if (line.remainingQuantity > 0.0) colors.muted else DANGER,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (!line.materialDescription.isNullOrBlank()) {
            Text(
                text = line.materialDescription,
                color = colors.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (!line.batch.isNullOrEmpty() || !line.carNo.isNullOrBlank()) {
            val batchText = if (!line.batch.isNullOrEmpty()) "${stringResource(R.string.batch)}: ${line.batch}" else ""
            val carText = if (!line.carNo.isNullOrBlank()) "${stringResource(R.string.car_number)}: ${line.carNo}" else ""
            val combined = listOf(batchText, carText).filter { it.isNotEmpty() }.joinToString("  \u00B7  ")
            Text(
                text = combined,
                color = colors.muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun DirectTransferPutAwayLineCard(
    line: DirectTransferPutAwayLine,
    accent: Color,
    onClear: () -> Unit
) {
    val colors = AppTheme.colors
    val progress = if (line.transferredQuantity > 0.0)
        (line.putAwayQuantity / line.transferredQuantity).toFloat().coerceIn(0f, 1f)
    else 0f

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
        if (!line.materialDescription.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = line.materialDescription,
                color = colors.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${stringResource(R.string.batch)}: ${line.batch.orEmpty()}  \u00B7  ${stringResource(R.string.unit)}: ${line.uom.orEmpty()}",
            color = colors.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        MarginVertical(12.dp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoCell(
                label = stringResource(R.string.transferred_quantity),
                value = line.transferredQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.putaway_qty),
                value = line.putAwayQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.remaining_quantity),
                value = line.remainingQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        if (!line.carNo.isNullOrBlank()) {
            MarginVertical(8.dp)
            InfoCell(
                label = stringResource(R.string.car_number),
                value = line.carNo,
                modifier = Modifier.fillMaxWidth()
            )
        }

        MarginVertical(12.dp)

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
                        .fillMaxWidth(fraction = progress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(accent)
                )
            }
        }
    }
}
