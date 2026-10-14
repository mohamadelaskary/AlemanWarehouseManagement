package net.gbs.gbswarehouse.ui.plc.conveyors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ErrorCard
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.GRAY
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.ConveyorStatus
import net.gbs.gbswarehouse.domain.model.StreamStatus
import net.gbs.gbswarehouse.ui.plc.PLC_ACCENT
import net.gbs.gbswarehouse.ui.plc.color
import net.gbs.gbswarehouse.ui.plc.label
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ConveyorsScreen(
    navController: NavController,
    viewModel: ConveyorsViewModel = hiltViewModel()
) {
    val conveyors by viewModel.conveyors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val streamStatus by viewModel.streamStatus.collectAsState()
    val colors = AppTheme.colors

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.plc_conveyors),
                subtitle = stringResource(R.string.plc_loading),
                accent = PLC_ACCENT,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && conveyors.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PLC_ACCENT
                )
            } else if (errorMessage != null && conveyors.isEmpty()) {
                ErrorCard(
                    label = stringResource(R.string.error),
                    message = errorMessage!!.text,
                    accent = DANGER
                )
            } else if (conveyors.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StreamStatusBar(status = streamStatus)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_conveyors_found), color = colors.muted)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    StreamStatusBar(status = streamStatus)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(conveyors) { conveyor ->
                            ConveyorStatusCard(
                                conveyor = conveyor,
                                onManualStart = { viewModel.manualStart(conveyor) },
                                onManualStop = { viewModel.manualStop(conveyor) },
                                onResetFault = { viewModel.resetFault(conveyor) },
                                onOpenOperation = { operationId ->
                                    navController.navigate(Screens.LoadingOperation.createRoute(operationId))
                                }
                            )
                            MarginVertical(8.dp)
                        }
                    }
                }
            }
        }
    }

    if (errorMessage != null && conveyors.isNotEmpty()) {
        ErrorDialog(
            accent = DANGER,
            message = errorMessage!!.text,
            onClose = viewModel::clearError
        )
    }
}

@Composable
private fun StreamStatusBar(status: StreamStatus) {
    val (labelRes, color) = when (status) {
        StreamStatus.LIVE -> R.string.plc_live to GREEN
        StreamStatus.CONNECTING -> R.string.plc_reconnecting to ORANGE
        StreamStatus.OFFLINE -> R.string.plc_stream_offline to DANGER
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = stringResource(labelRes),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ConveyorStatusCard(
    conveyor: Conveyor,
    onManualStart: () -> Unit,
    onManualStop: () -> Unit,
    onResetFault: () -> Unit,
    onOpenOperation: (String) -> Unit
) {
    val colors = AppTheme.colors
    val statusColor = conveyor.status.color()
    val operation = conveyor.currentOperation
    val faultMessage = listOfNotNull(conveyor.faultName, conveyor.faultText)
        .filter { it.isNotBlank() }
        .joinToString(" — ")
        .ifBlank {
            if (conveyor.faultCode != 0) "E${conveyor.faultCode}"
            else stringResource(R.string.conveyor_error)
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, statusColor.copy(0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(statusColor.copy(0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u2699", color = statusColor, fontSize = 17.sp)
                }
                Column {
                    Text(
                        text = conveyor.name,
                        color = colors.text,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = conveyor.status.label(),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Pill(label = conveyor.status.label(), color = statusColor,)
        }

        MarginVertical(10.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IndicatorChip(
                label = stringResource(R.string.plc_motor),
                active = conveyor.motorRunning,
                activeColor = GREEN,
                modifier = Modifier.weight(1f)
            )
            IndicatorChip(
                label = stringResource(R.string.plc_sensor),
                active = conveyor.sensorBlocked,
                activeColor = DANGER,
                modifier = Modifier.weight(1f)
            )
            IndicatorChip(
                label = stringResource(R.string.plc_remote),
                active = conveyor.remoteEnabled,
                activeColor = GREEN,
                modifier = Modifier.weight(1f)
            )
            IndicatorChip(
                label = stringResource(R.string.plc_online),
                active = conveyor.online,
                activeColor = GREEN,
                inactiveColor = DANGER,
                modifier = Modifier.weight(1f)
            )
        }

        if (conveyor.hasFault || conveyor.status == ConveyorStatus.ERROR) {
            MarginVertical(10.dp)
            Text(
                text = faultMessage,
                color = DANGER,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            MarginVertical(6.dp)
            OutlineBtn(
                label = stringResource(R.string.reset_fault),
                color = DANGER,
                onClick = onResetFault
            )
        }

        MarginVertical(12.dp)

        if (operation != null) {
            InfoCell(
                label = stringResource(R.string.current_operation),
                value = operation.operationId,
                color = statusColor,
                modifier = Modifier.fillMaxWidth()
            )
            MarginVertical(8.dp)
            InfoCell(
                label = stringResource(R.string.current_item),
                value = "${operation.itemName} — ${operation.orderNumber}",
                modifier = Modifier.fillMaxWidth()
            )
            MarginVertical(8.dp)
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
                    label = stringResource(R.string.sales_order),
                    value = operation.orderNumber,
                    modifier = Modifier.weight(1f)
                )
            }
            MarginVertical(10.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCell(
                    label = stringResource(R.string.target_quantity),
                    value = operation.requiredQuantity.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = stringResource(R.string.sales_order_item),
                    value = operation.salesOrderItem ?: "-",
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = stringResource(R.string.material_code),
                    value = operation.materialCode ?: "-",
                    modifier = Modifier.weight(1f)
                )
            }
            MarginVertical(10.dp)
            LinearProgress(
                progress = operation.progress,
                color = statusColor
            )
            MarginVertical(12.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlineBtn(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.plc_loading_screen),
                    color = PLC_ACCENT,
                    onClick = { onOpenOperation(operation.operationId) }
                )
                if (operation.status == ConveyorStatus.RUNNING ||
                    operation.status == ConveyorStatus.PAUSED
                ) {
                    OutlineBtn(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.manual_stop),
                        color = DANGER,
                        onClick = onManualStop
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.conveyor_idle),
                color = colors.muted,
                fontSize = 11.sp
            )
            MarginVertical(8.dp)
            OutlineBtn(
                label = if (conveyor.status == ConveyorStatus.MANUAL) {
                    stringResource(R.string.manual_stop)
                } else {
                    stringResource(R.string.manual_start)
                },
                color = if (conveyor.status == ConveyorStatus.MANUAL) DANGER else PLC_ACCENT,
                onClick = if (conveyor.status == ConveyorStatus.MANUAL) onManualStop else onManualStart
            )
        }
    }
}

@Composable
private fun IndicatorChip(
    label: String,
    active: Boolean,
    activeColor: Color,
    inactiveColor: Color = GRAY,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val dotColor = if (active) activeColor else inactiveColor
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) activeColor.copy(0.12f) else colors.dark.copy(0.5f))
            .border(
                1.dp,
                if (active) activeColor.copy(0.35f) else colors.border2,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = label,
            color = if (active) colors.text else colors.muted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun LinearProgress(
    progress: Float,
    color: Color
) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color)
        )
    }
}
