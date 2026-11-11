package net.gbs.gbswarehouse.ui.audit.orders_list

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.BLUE
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.InventoryOrder
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun AuditOrdersScreen(
    navController: NavController,
) {
    val viewModel: AuditOrdersListViewModel = hiltViewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = BLUE

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.audit),
            subtitle = stringResource(R.string.inventory_orders),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        // Search
        SearchBox(
            value = viewModel.orderSearch,
            onValueChange = { viewModel.orderSearch = it },
            placeholder = stringResource(R.string.search_audit_orders),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${orders.size} ${stringResource(R.string.operations)}", color = colors.muted, fontSize = 11.sp)
            Text("AUDIT", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else if (viewModel.errorMessage != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage!!.text, color = Color.Red, fontSize = 14.sp)
            }
        } else if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 36.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(orders) { _, ord ->
                    AuditOrderCard(
                        order = ord,
                        accent = accent,
                        onClick = {
                            navController.navigate(
                                Screens.AuditDetailsScreen.createRoute(ord.inventoryOrderId.toString())
                            )
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AuditOrderCard(order: InventoryOrder, accent: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (hovered) colors.card2 else colors.card)
            .border(1.dp, if (hovered) colors.border2 else colors.border, RoundedCornerShape(12.dp))
            .clickable { onClick(); hovered = false }
            .padding(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#${order.orderNumber}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Pill(order.status ?: "InProgress", Color.Blue)
        }
        Spacer(Modifier.height(6.dp))
        if (!order.description.isNullOrEmpty()) {
            Text(order.description, color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(6.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${stringResource(R.string.warehouse)}: ${order.warehouseCode ?: "-"}", color = colors.muted, fontSize = 11.sp)
            Text("${stringResource(R.string.plant)}: ${order.plantCode ?: "-"}", color = colors.muted, fontSize = 11.sp)
            Text("${order.scannedItemCount ?: 0} / ${order.assignedItemCount ?: 0}", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
