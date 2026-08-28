package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.orders_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingSparePartsFromProductionListScreen(
    navController: NavController
) {
    val viewModel: ReceivingSparePartsFromProductionOrdersListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingSparePartsFromProductionOrdersListEvent.OnScreenLoaded)
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.receiving),
            subtitle = stringResource(R.string.return_spare_parts_to_warehouse),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        val ordersLabel = stringResource(R.string.orders)
        Column(modifier = Modifier.padding(14.dp)) {
            // Movement Type Spinner
            Text(
                text = stringResource(R.string.movement_type),
                color = colors.muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            SearchableSpinner(
                hint = stringResource(R.string.select_movement_type),
                items = state.movementTypes,
                itemLabel = { "${it.returnReason} (${it.movementType}) ${it.count} $ordersLabel" },
                onItemSelected = {
//                    it.movementType?.let {
//                        viewModel.onEvent(ReceivingSparePartsFromProductionOrdersListEvent.OnMovementTypeSelected(it))
//                    }
                    viewModel.onEvent(ReceivingSparePartsFromProductionOrdersListEvent.OnMovementTypeSelected(it))
                                 },
                modifier = Modifier.fillMaxWidth(),
                searchText = state.movementTypeSearchText,
                onSearchTextChanged = {
                    viewModel.onEvent(
                        ReceivingSparePartsFromProductionOrdersListEvent.OnMovementTypeSearchTextChanged(
                            it
                        )
                    )
                }
            )


        }

        if (state.isLoadingOrders) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else if (state.error.isNotBlank() && state.orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = colors.danger, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(14.dp))

                    // Search Box
                    SearchBox(
                        value = state.searchText,
                        onValueChange = { viewModel.onEvent(ReceivingSparePartsFromProductionOrdersListEvent.OnSearchTextChanged(it)) },
                        placeholder = stringResource(R.string.search_by_number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${state.filteredOrders.size} ${stringResource(R.string.items)}", color = colors.muted, fontSize = 11.sp)
                        Text("RESERVATION", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                items(state.filteredOrders) { order ->
                    ReservationOrderCard(
                        order = order,
                        accent = accent,
                        onClick = {
                            order.let {
                                navController.navigate(Screens.ReceivingSparePartsFromProductionDetailsScreen.createRoute(it.toJson(),state.selectedMovementType!!.returnReason?:""))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationOrderCard(
    order: SparePartsReturnReservationOrder,
    accent: Color,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "#${order.reservationNumber}",
                    color = accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    order.requirementDate?.substring(0,10) ?: "",
                    color = colors.muted,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            if (!order.orderNumber.isNullOrEmpty())
                Text(
                    stringResource(R.string.order_description) + ": ${order.orderDescription}",
                    color = colors.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            else
                Text(
                    stringResource(R.string.cost_center_description) + "${order.costCenterDescription}",
                    color = colors.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
//            if (!order.orderDescription.isNullOrBlank()) {
//                Text(
//                    (stringResource(R.string.order_description) + order.orderDescription) ?: "",
//                    color = colors.muted,
//                    fontSize = 11.sp,
//                    maxLines = 1
//                )
//            } else {
//                Text(
//                    (stringResource(R.string.cost_center_description) + order.costCenterDescription)
//                        ?: "",
//                    color = colors.muted,
//                    fontSize = 11.sp,
//                    maxLines = 1
//                )
//            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!order.costCenterCode.isNullOrBlank()){
                    Text(
                        stringResource(R.string.cost_center),
                        color = colors.muted,
                        fontSize = 10.sp
                    )
                    Text(
                        order.costCenterCode ?: "-",
                        color = colors.text,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    } else {
                        Text(
                            stringResource(R.string.order_number),
                            color = colors.muted,
                            fontSize = 10.sp
                        )
                        Text(
                            order.orderNumber ?: "-",
                            color = colors.text,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.items_count), color = colors.muted, fontSize = 10.sp)
                    Text("${order.itemCount ?: 0}", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
