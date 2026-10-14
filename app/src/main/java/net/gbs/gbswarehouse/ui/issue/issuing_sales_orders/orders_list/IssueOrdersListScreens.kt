package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.orders_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Lucide
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedOrder
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun IssuingSalesOrdersScreen(
    navController: NavController,
) {
    val viewModel: IssueOrdersListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.orange

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = stringResource(R.string.issuing), subtitle = stringResource(R.string.finished_goods_feed_issuing), accent = accent,
            onBack = { navController.popBackStack() })

        // Search
        SearchBox(
            value = state.orderSearch,
            onValueChange = { viewModel.onEvent(IssueOrdersListEvent.OnSearchTextChanged(it)) },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${state.filteredOrders.size} " + stringResource(R.string.result), color = colors.muted, fontSize = 11.sp)
            Text("SO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(Modifier.height(6.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error!!, color = colors.text)
                    Spacer(Modifier.height(10.dp))
                    OutlineBtn(stringResource(R.string.retry), accent) {
                        viewModel.onEvent(IssueOrdersListEvent.Refresh)
                    }
                }
            }
        } else if (state.filteredOrders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 36.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(state.filteredOrders) { _, ord ->
                    val carNo = ord.carNo ?: ""
                    FinishedGoodsFeedOrderCard(
                        order = ord,
                        accent = accent,
                        onClick = {
                            navController.navigate(Screens.IssuingSalesOrdersDetailsScreen.createRoute(carNo))
                        },
                        isMaterialsShowed = state.expandedCarNumbers.contains(carNo),
                        onToggleItems = {
                            viewModel.onEvent(IssueOrdersListEvent.OnToggleOrderItems(carNo))
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun FinishedGoodsFeedOrderCard(
    order: FinishedGoodsFeedOrder,
    accent: Color,
    onClick: () -> Unit,
    isMaterialsShowed: Boolean = false,
    onToggleItems: () -> Unit
) {
    val colors = AppTheme.colors
    val itemsCount = if (order.lines.isNotEmpty()) order.lines.size else (order.itemCount ?: 0)
    val shippingPoint = order.lines.firstOrNull()?.shippingPointCode
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Car,
                        contentDescription = "Car",
                        modifier = Modifier.width(18.dp),
                        tint = accent
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "#${order.carNo ?: ""}",
                        color = accent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = order.factoryCarDescription ?: "",
                    color = colors.muted,
                    fontSize = 10.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Text("${order.customerCode} - ${order.customerName}", color = colors.text, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text(order.orderDescription ?: "", color = colors.muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            if (!shippingPoint.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.shipping_point) + ": $shippingPoint", color = colors.text, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(8.dp))

            if (isMaterialsShowed) {
                if (order.lines.isNotEmpty()) {
                    order.lines.forEach { item ->
                        FinishedGoodsFeedItemInfoCard(item)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_items),
                        color = colors.muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OutlineBtn(
                    label = if (isMaterialsShowed) {
                        stringResource(R.string.hide_items)
                    } else {
                        "$itemsCount ${stringResource(R.string.items)}"
                    },
                    color = accent,
                    onClick = onToggleItems
                )
            }
        }
    }
}

@Composable
fun FinishedGoodsFeedItemInfoCard(item: FinishedGoodsFeedItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.dark)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.materialCode ?: "",
                    color = AppTheme.colors.text,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "#${item.salesOrder ?: ""}",
                    color = AppTheme.colors.orange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.materialDescription ?: "",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    color = AppTheme.colors.text,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = stringResource(R.string.remaining_quantity_) + " ${item.remainingQuantity} ${item.uom}",
                    color = AppTheme.colors.muted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
