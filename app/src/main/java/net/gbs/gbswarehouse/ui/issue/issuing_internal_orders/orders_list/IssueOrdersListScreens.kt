package net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.orders_list

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.ui.issue.ReservationLineItemInfoCard
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.MUTED
import net.gbs.gbswarehouse.ui.theme.TEXT

@Composable
fun IssuingInternalOrdersScreen(
    navController: NavController,
) {
    val viewModel: IssueOrdersListViewModel = hiltViewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.orange


    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "صرف", subtitle = "أمر داخلي", accent = accent,
            onBack = { navController.popBackStack() })

        // Search
        SearchBox(
            value = viewModel.orderSearch,
            onValueChange = { viewModel.orderSearch = it },
            placeholder = "بحث بالرقم أو الاسم...",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${orders.size} نتيجة", color = colors.muted, fontSize = 11.sp)
            Text("INTERNAL", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else if (viewModel.errorMessage != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }
        } else if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 36.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("لا توجد نتائج", color = colors.muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                itemsIndexed(orders) { index, ord ->
                    val resNum = ord.reservationNumber ?: ""
                    IssueOrderCard(
                        order = ord,
                        accent = accent,
                        text = colors.text,
                        muted = colors.muted,
                        isExpanded = viewModel.expandedOrderNumbers.contains(resNum),
                        onToggleExpand = { viewModel.toggleOrderExpanded(resNum) },
                        onClick = {
                            navController.navigate(
                                Screens.IssuingInternalOrdersDetailsScreen.createRoute(resNum)
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
fun IssueOrderCard(
    order: SparePartsReturnReservationOrder,
    accent: Color,
    muted: Color,
    text: Color,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val itemsCount = if (order.lines.isNotEmpty()) order.lines.size else (order.itemCount ?: 0)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.card)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.reservationNumber ?: "",
                color = accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (!order.orderDescription.isNullOrEmpty())
            Text(
                text = order.orderDescription ?: "لا يوجد وصف",
                color = colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!order.costCenterDescription.isNullOrEmpty())
                Text(
                    text = order.costCenterDescription ?: "لا يوجد وصف",
                    color = colors.text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            if (!order.costCenterCode.isNullOrEmpty())
                Text(
                    text = stringResource(R.string.cost_center_code) + " " + order.costCenterCode,
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoItem(
                label = stringResource(R.string.date),
                value = order.requirementDate?.take(10) ?: "",
                icon = "📅",
                muted = muted,
                text = text
            )
            OutlineBtn(
                label = if (isExpanded) {
                    stringResource(R.string.hide_items)
                } else {
                    "$itemsCount ${stringResource(R.string.items)}"
                },
                color = accent,
                onClick = onToggleExpand
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(10.dp))
            if (order.lines.isNotEmpty()) {
                order.lines.forEach { line ->
                    ReservationLineItemInfoCard(line = line)
                }
            } else {
                Text(
                    text = stringResource(R.string.no_items),
                    color = muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: String,muted: Color = MUTED,text: Color = TEXT) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 10.sp)
            Spacer(Modifier.width(4.dp))
            Text(label, color = muted, fontSize = 10.sp)
        }
        Text(value, color = text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
