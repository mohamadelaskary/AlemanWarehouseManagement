package net.gbs.gbswarehouse.ui.plc.orders

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ErrorCard
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.ui.plc.PLC_ACCENT
import net.gbs.gbswarehouse.ui.plc.orDash
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun LoadingOrdersScreen(
    navController: NavController,
    viewModel: LoadingOrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colors = AppTheme.colors

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.plc_loading_orders),
                subtitle = stringResource(R.string.plc_conveyors),
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
            SearchBox(
                value = searchText,
                onValueChange = viewModel::onSearchTextChanged,
                placeholder = stringResource(R.string.search_orders),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OrderTypeToggleGroup(
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::onFilterSelected,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            MarginVertical(4.dp)

            FullBtn(
                label = stringResource(R.string.plc_conveyors),
                color = PLC_ACCENT,
                onClick = { navController.navigate(Screens.LoadingConveyors.createRoute()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            MarginVertical(12.dp)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PLC_ACCENT
                    )
                } else if (errorMessage != null) {
                    ErrorCard(
                        label = stringResource(R.string.error),
                        message = errorMessage!!.text,
                        accent = DANGER
                    )
                } else if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_orders_found), color = colors.muted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            LoadingOrderCard(
                                order = order,
                                onClick = {
                                    navController.navigate(
                                        Screens.LoadingOrderDetails.createRoute(order.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingOrderCard(
    order: LoadingOrder,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val fullyLoaded = order.items.all { it.remainingQuantity <= 0 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, PLC_ACCENT.copy(0.15f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PLC_ACCENT.copy(0.1f))
                        .border(1.dp, PLC_ACCENT.copy(0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\uD83D\uDCE6", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = order.orderNumber,
                        color = colors.text,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = order.vehicleNumber,
                        color = PLC_ACCENT,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Pill(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.items) + " ${order.items.size}",
                color = PLC_ACCENT
            )
        }
        MarginVertical(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.shipping_point),
                value = order.shippingPointCode.orDash(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.status),
                value = order.status.orDash(),
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.required_quantity),
                value = order.items.sumOf { it.requiredQuantity }.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.loaded_quantity),
                value = order.items.sumOf { it.loadedQuantity }.toString(),
                color = if (fullyLoaded) SUCC else colors.text,
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.remaining_quantity),
                value = order.items.sumOf { it.remainingQuantity }.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTypeToggleGroup(
    selectedFilter: OrderTypeFilter,
    onFilterSelected: (OrderTypeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val options = OrderTypeFilter.entries

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        options.forEachIndexed { index, filter ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = PLC_ACCENT.copy(alpha = 0.15f),
                    activeContentColor = PLC_ACCENT,
                    activeBorderColor = PLC_ACCENT.copy(alpha = 0.60f),
                    inactiveContainerColor = colors.card,
                    inactiveContentColor = colors.muted,
                    inactiveBorderColor = colors.border2
                ),
                icon = {},
                label = {
                    val labelRes = when (filter) {
                        OrderTypeFilter.ALL -> R.string.all_orders
                        OrderTypeFilter.FINISHED_GOODS_FEED -> R.string.finished_goods_feed
                        OrderTypeFilter.TRANSFER_ORDER -> R.string.transfer_orders
                    }
                    Text(
                        text = stringResource(labelRes),
                        fontSize = 11.sp,
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}