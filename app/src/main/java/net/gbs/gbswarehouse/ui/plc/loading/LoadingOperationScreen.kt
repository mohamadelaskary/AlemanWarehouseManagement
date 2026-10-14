package net.gbs.gbswarehouse.ui.plc.loading

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.ConveyorStatus
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.ui.plc.PLC_ACCENT
import net.gbs.gbswarehouse.ui.plc.color
import net.gbs.gbswarehouse.ui.plc.label
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun LoadingOperationScreen(
    operationId: String,
    navController: NavController,
    viewModel: LoadingOperationViewModel = hiltViewModel()
) {
    val operation by viewModel.operation.collectAsState()
    val orderItems by viewModel.orderItems.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colors = AppTheme.colors

    var showFailureDialog by remember { mutableStateOf(false) }

    LaunchedEffect(operationId) {
        viewModel.bind(operationId)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.plc_loading_screen),
                subtitle = operation?.conveyorName ?: "",
                accent = PLC_ACCENT,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val current = operation
            if (current == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.plc_no_operation_available),
                        color = colors.muted
                    )
                }
            } else {
                LoadingOperationContent(
                    operation = current,
                    orderItems = orderItems,
                    isBusy = isBusy,
                    onPause = viewModel::pause,
                    onResume = viewModel::resume,
                    onAddOnePack = viewModel::addOnePack,
                    onManualStart = viewModel::manualStart,
                    onManualStop = viewModel::manualStop,
                    onReportFailure = { showFailureDialog = true },
                    onOpenConveyors = { navController.navigate(Screens.LoadingConveyors.createRoute()) },
                    onBackToOrders = { navController.popBackStack(Screens.LoadingOrders.route, false) }
                )
            }
        }
    }

    if (errorMessage != null) {
        ErrorDialog(
            accent = DANGER,
            message = errorMessage!!.text,
            onClose = viewModel::clearError
        )
    }

    if (showFailureDialog) {
        ReportFailureDialog(
            onDismiss = { showFailureDialog = false },
            onConfirm = { reason ->
                showFailureDialog = false
                viewModel.reportFailure(reason)
            }
        )
    }
}

@Composable
private fun LoadingOperationContent(
    operation: LoadingOperation,
    orderItems: List<OrderItem>,
    isBusy: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onAddOnePack: () -> Unit,
    onManualStart: () -> Unit,
    onManualStop: () -> Unit,
    onReportFailure: () -> Unit,
    onOpenConveyors: () -> Unit,
    onBackToOrders: () -> Unit
) {
    val colors = AppTheme.colors
    val statusColor = operation.status.color()
    var showLines by remember { mutableStateOf(false) }

    val allLoaded = operation.remainingQuantity <= 0 && operation.requiredQuantity > 0
    var countdownSeconds by remember(operation.operationId) { mutableStateOf(10) }

    LaunchedEffect(allLoaded) {
        if (allLoaded) {
            countdownSeconds = 10
            while (countdownSeconds > 0) {
                delay(1000L)
                countdownSeconds--
            }
            if (countdownSeconds == 0) {
                onBackToOrders()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (allLoaded) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SUCC.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, SUCC.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.all_quantity_loaded),
                            color = SUCC,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        MarginVertical(6.dp)
                        Text(
                            text = stringResource(R.string.returning_in_seconds, countdownSeconds),
                            color = colors.text,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        MarginVertical(10.dp)
                        FullBtn(
                            label = stringResource(R.string.plc_loading_orders),
                            color = SUCC,
                            onClick = onBackToOrders,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                MarginVertical(12.dp)
            }
        }
        item {
            Section(label = stringResource(R.string.loading_progress), accent = statusColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Pill(label = operation.status.label(), color = statusColor,)
                    Text(
                        text = "${operation.loadedQuantity} / ${operation.requiredQuantity}",
                        color = statusColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                MarginVertical(12.dp)
                ProgressBar(
                    progress = operation.progress,
                    color = statusColor
                )
            }
        }

        // Number of Materials Collapsible Section
        if (orderItems.isNotEmpty()) {
            item {
                Section(label = stringResource(R.string.items), accent = PLC_ACCENT) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLines = !showLines },
                        colors = CardDefaults.cardColors(containerColor = colors.card),
                        border = BorderStroke(1.dp, PLC_ACCENT.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${stringResource(R.string.number_of_materials)}: ${orderItems.size}",
                                    color = colors.text,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (showLines) "▲" else "▼",
                                    color = PLC_ACCENT,
                                    fontSize = 14.sp
                                )
                            }

                            if (showLines) {
                                MarginVertical(10.dp)
                                orderItems.forEach { item ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(colors.dark.copy(0.4f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = item.materialCode ?: item.name,
                                                color = PLC_ACCENT,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${item.loadedQuantity} / ${item.requiredQuantity} ${item.uom.orEmpty()}",
                                                color = colors.text,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (item.name.isNotEmpty() && item.name != item.materialCode) {
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = item.name,
                                                color = colors.muted,
                                                fontSize = 11.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Section(label = stringResource(R.string.current_operation), accent = PLC_ACCENT) {
                InfoCell(
                    label = stringResource(R.string.operation_id),
                    value = operation.operationId,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                InfoCell(
                    label = stringResource(R.string.item_name),
                    value = operation.itemName,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                InfoCell(
                    label = stringResource(R.string.conveyor_name),
                    value = operation.conveyorName,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                InfoCell(
                    label = stringResource(R.string.sales_order),
                    value = operation.orderNumber,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Section(label = stringResource(R.string.plc_loading), accent = PLC_ACCENT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCell(
                        label = stringResource(R.string.loaded_quantity),
                        value = operation.loadedQuantity.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCell(
                        label = stringResource(R.string.remaining_quantity),
                        value = operation.remainingQuantity.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCell(
                        label = stringResource(R.string.required_quantity),
                        value = operation.requiredQuantity.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            LoadingControls(
                operation = operation,
                isBusy = isBusy,
                onPause = onPause,
                onResume = onResume,
                onAddOnePack = onAddOnePack,
                onManualStart = onManualStart,
                onManualStop = onManualStop,
                onReportFailure = onReportFailure
            )
        }

        if (operation.isFinished) {
            item {
                Section(label = stringResource(R.string.conveyor_status), accent = statusColor) {
                    Text(
                        text = if (operation.status == ConveyorStatus.COMPLETED) {
                            stringResource(R.string.operation_completed)
                        } else {
                            stringResource(R.string.operation_failed) +
                                    (operation.failureReason?.let { " — $it" } ?: "")
                        },
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    MarginVertical(12.dp)
                    FullBtn(
                        label = stringResource(R.string.plc_conveyors),
                        color = PLC_ACCENT,
                        onClick = onOpenConveyors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MarginVertical(8.dp)
                    FullBtn(
                        label = stringResource(R.string.plc_loading_orders),
                        color = GREEN,
                        onClick = onBackToOrders,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Central control-availability rules. Actions are derived from the operation
 * state only, so the same rules apply on every entry point.
 */
@Composable
private fun LoadingControls(
    operation: LoadingOperation,
    isBusy: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onAddOnePack: () -> Unit,
    onManualStart: () -> Unit,
    onManualStop: () -> Unit,
    onReportFailure: () -> Unit
) {
    Section(label = stringResource(R.string.plc_loading), accent = PLC_ACCENT) {
        when (operation.status) {
            ConveyorStatus.RUNNING -> {
                FullBtn(
                    label = stringResource(R.string.pause),
                    color = ORANGE,
                    onClick = onPause,
                    saving = isBusy,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlineBtn(stringResource(R.string.add_one_pack), PLC_ACCENT) { onAddOnePack() }
                    OutlineBtn(stringResource(R.string.manual_stop), DANGER) { onManualStop() }
                    OutlineBtn(stringResource(R.string.report_failure), DANGER) { onReportFailure() }
                }
            }

            ConveyorStatus.PAUSED -> {
                FullBtn(
                    label = stringResource(R.string.resume),
                    color = GREEN,
                    onClick = onResume,
                    saving = isBusy,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlineBtn(stringResource(R.string.add_one_pack), PLC_ACCENT) { onAddOnePack() }
                    OutlineBtn(stringResource(R.string.manual_stop), DANGER) { onManualStop() }
                    OutlineBtn(stringResource(R.string.report_failure), DANGER) { onReportFailure() }
                }
            }

            ConveyorStatus.IDLE -> {
                FullBtn(
                    label = stringResource(R.string.start),
                    color = GREEN,
                    onClick = onResume,
                    saving = isBusy,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                OutlineBtn(stringResource(R.string.manual_start), PLC_ACCENT) { onManualStart() }
            }

            ConveyorStatus.MANUAL -> {
                FullBtn(
                    label = stringResource(R.string.manual_stop),
                    color = DANGER,
                    onClick = onManualStop,
                    saving = isBusy,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ConveyorStatus.ERROR -> {
                Text(
                    text = stringResource(R.string.operation_failed) +
                            (operation.failureReason?.let { " — $it" } ?: ""),
                    color = DANGER,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                MarginVertical(10.dp)
                OutlineBtn(stringResource(R.string.manual_start), PLC_ACCENT) { onManualStart() }
            }

            ConveyorStatus.COMPLETED -> {
                Text(
                    text = stringResource(R.string.operation_completed),
                    color = SUCC,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    color: Color
) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(7.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(color)
        )
    }
}

@Composable
private fun ReportFailureDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val defaultReason = stringResource(R.string.failure_reason)
    var reason by remember { mutableStateOf(defaultReason) }

    BottomSheetDialog(
        title = stringResource(R.string.confirm_report_failure),
        onClose = onDismiss
    ) {
        MarginVertical(12.dp)
        Field(
            label = stringResource(R.string.failure_reason),
            hint = stringResource(R.string.failure_reason),
            value = reason,
            onValueChange = { reason = it }
        )
        MarginVertical(16.dp)
        FullBtn(
            label = stringResource(R.string.report_failure),
            color = DANGER,
            onClick = { onConfirm(reason) },
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(8.dp)
        FullBtn(
            label = stringResource(R.string.cancel),
            color = PLC_ACCENT,
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(16.dp)
    }
}
