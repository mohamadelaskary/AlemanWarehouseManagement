package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.orders_list

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.BORDER
import net.gbs.gbswarehouse.ui.theme.CARD
import net.gbs.gbswarehouse.ui.theme.DARK
import net.gbs.gbswarehouse.ui.theme.MUTED
import net.gbs.gbswarehouse.ui.theme.SUCC
import net.gbs.gbswarehouse.ui.theme.TEXT

@Composable
fun IssuingRawMaterialsToProductionScreen(
    navController: NavController,
) {
    val viewModel: IssueOrdersListViewModel = hiltViewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.orange


    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "صرف", subtitle = "مواد خام للإنتاج", accent = accent,
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
            Text("PROD", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
                    IssueOrderCard(order = ord, accent = accent, text = colors.text, muted = colors.muted,
                        onClick = {
                            navController.navigate(
                                Screens.IssuingRawMaterialsToProductionDetailsScreen.createRoute(ord.reservationNumber ?: "")
                            )
                        })
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
    muted: Color ,
    text: Color ,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
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
        if (!order.costCenterDescription.isNullOrEmpty())
            Text(
                text = order.costCenterDescription ?: "لا يوجد وصف",
                color = colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoItem(label = "التاريخ", value = order.requirementDate?.substring(0,10) ?: "", icon = "📅", muted = muted, text = text)
            InfoItem(label = "الأصناف", value = "${order.itemCount ?: 0} أصناف", icon = "📦", muted = muted, text = text)
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
