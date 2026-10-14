package net.gbs.gbswarehouse.ui.plc.order_details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.BottomSheetDialog
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ErrorCard
import net.gbs.gbswarehouse.ErrorDialog
import net.gbs.gbswarehouse.Field
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.ui.plc.PLC_ACCENT
import net.gbs.gbswarehouse.ui.plc.color
import net.gbs.gbswarehouse.ui.plc.label
import net.gbs.gbswarehouse.ui.plc.orDash
import net.gbs.gbswarehouse.ui.plc.orTrimmed
import net.gbs.gbswarehouse.ui.plc.startLoadingService
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun LoadingOrderDetailsScreen(
    orderId: Int,
    navController: NavController,
    viewModel: LoadingOrderDetailsViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val conveyors by viewModel.conveyors.collectAsState()
    val isStarting by viewModel.isStarting.collectAsState()
    val context = LocalContext.current
    val colors = AppTheme.colors

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.plc_order_details),
                subtitle = order?.orderNumber ?: "",
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
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PLC_ACCENT
                    )
                    errorMessage != null && order == null -> ErrorCard(
                    label = stringResource(R.string.error),
                    message = errorMessage!!.text,
                    accent = DANGER
                    )
                    order != null -> LoadingOrderDetailsContent(
                        order = order!!,
                        onItemClick = viewModel::selectItem
                    )
                }
            }
        }
    }

    if (errorMessage != null && order != null) {
        ErrorDialog(
            accent = DANGER,
            message = errorMessage!!.text,
            onClose = viewModel::clearError
        )
    }

    if (selectedItem != null) {
        val startQty by viewModel.startQty.collectAsState()
        ConveyorSelectSheet(
            item = selectedItem!!,
            startQty = startQty,
            onQtyChange = viewModel::updateStartQty,
            conveyors = conveyors,
            isStarting = isStarting,
            onDismiss = viewModel::clearSelectedItem,
            onConfirm = { conveyor ->
                viewModel.startLoading(conveyor) { operation ->
                    context.startLoadingService(operation.operationId)
                    navController.navigate(Screens.LoadingOperation.createRoute(operation.operationId))
                }
            }
        )
    }
}

@Composable
private fun LoadingOrderDetailsContent(
    order: LoadingOrder,
    onItemClick: (OrderItem) -> Unit
) {
    val colors = AppTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Section(label = stringResource(R.string.plc_order_details), accent = PLC_ACCENT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.status),
                        color = colors.muted,
                        fontSize = 12.sp
                    )
                    Pill(
                        label = order.status.orDash(),
                        color = PLC_ACCENT,
                    )
                }
                MarginVertical(8.dp)
                InfoCell(
                    label = stringResource(R.string.order_number),
                    value = order.orderNumber,
                    modifier = Modifier.fillMaxWidth()
                )
                MarginVertical(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCell(
                        label = stringResource(R.string.vehicle_number),
                        value = order.vehicleNumber.orDash(),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCell(
                        label = stringResource(R.string.shipping_point_code),
                        value = order.shippingPointCode.orDash(),
                        modifier = Modifier.weight(1f)
                    )
                }
                MarginVertical(8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCell(
                        label = stringResource(R.string.worker_group_code),
                        value = order.workerGroupCode.orDash(),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCell(
                        label = stringResource(R.string.created_at),
                        value = order.createdAt.orDash().take(10),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        item {
            Section(label = stringResource(R.string.items), accent = PLC_ACCENT) {
                Text(
                    text = stringResource(R.string.tap_item_to_select),
                    color = colors.muted,
                    fontSize = 11.sp
                )
                MarginVertical(8.dp)
            }
        }
        items(order.items) { item ->
            LoadingOrderItemCard(item = item, onClick = { onItemClick(item) })
            MarginVertical(8.dp)
        }
    }
}

@Composable
fun LoadingOrderItemCard(
    item: OrderItem,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val isDone = item.remainingQuantity <= 0
    val accent = if (isDone) SUCC else PLC_ACCENT

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, accent.copy(0.18f), RoundedCornerShape(12.dp))
            .clickable(enabled = !isDone) { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                color = colors.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (isDone) {
                Pill(label = stringResource(R.string.conveyor_completed), color = accent,)
            }
        }
        MarginVertical(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.required_quantity),
                value = item.requiredQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.loaded_quantity),
                value = item.loadedQuantity.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.remaining_quantity),
                value = item.remainingQuantity.toString(),
                color = if (isDone) accent else colors.text,
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.material_code),
                value = item.materialCode.orDash(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.uom),
                value = item.uom.orDash(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.plant_code),
                value = item.plantCode.orDash(),
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(10.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.sales_order),
                value = item.salesOrderItemOrDash(item.salesOrder),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.bag_weight_kg),
                value = item.bagWeightKg.orTrimmed(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.bag_length_mm),
                value = item.bagLengthMm.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun OrderItem.salesOrderItemOrDash(salesOrder: String?): String {
    val order = salesOrder.orDash()
    val itemNo = salesOrderItem.orDash()
    return if (itemNo == "-") order else "$order / $itemNo"
}

@Composable
private fun ConveyorSelectSheet(
    item: OrderItem,
    startQty: String,
    onQtyChange: (String) -> Unit,
    conveyors: List<Conveyor>,
    isStarting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Conveyor) -> Unit
) {
    val available = conveyors.filter { it.isAvailable }

    BottomSheetDialog(
        title = stringResource(R.string.select_conveyor),
        onClose = onDismiss
    ) {
        MarginVertical(12.dp)
        InfoCell(
            label = stringResource(R.string.item_name),
            value = item.name,
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(8.dp)
        InfoCell(
            label = stringResource(R.string.remaining_quantity),
            value = item.remainingQuantity.toString(),
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(8.dp)
        Field(
            label = stringResource(R.string.receiving_qty),
            hint = "0",
            value = startQty,
            onValueChange = onQtyChange,
            isNumber = true,
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(8.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.order_quantity),
                value = item.quantity.orTrimmed(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.bag_weight_kg),
                value = item.bagWeightKg.orTrimmed(),
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(12.dp)

        if (available.isEmpty()) {
            Text(
                text = stringResource(R.string.no_available_conveyors),
                color = DANGER,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            available.forEach { conveyor ->
                ConveyorSelectRow(
                    conveyor = conveyor,
                    enabled = !isStarting,
                    onClick = { onConfirm(conveyor) }
                )
                MarginVertical(8.dp)
            }
        }

        MarginVertical(12.dp)
        FullBtn(
            label = stringResource(R.string.cancel),
            color = DANGER,
            onClick = onDismiss,
            saving = isStarting,
            modifier = Modifier.fillMaxWidth()
        )
        MarginVertical(16.dp)
    }
}

@Composable
private fun ConveyorSelectRow(
    conveyor: Conveyor,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val statusColor = conveyor.status.color()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.dark)
            .border(1.dp, statusColor.copy(0.25f), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(statusColor.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text("\u2699", color = statusColor, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conveyor.name,
                color = colors.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = conveyor.status.label(),
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Pill(label = stringResource(R.string.start), color = PLC_ACCENT,)
    }
}