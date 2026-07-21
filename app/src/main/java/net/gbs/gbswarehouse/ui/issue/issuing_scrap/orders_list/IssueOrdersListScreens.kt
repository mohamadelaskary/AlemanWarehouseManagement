package net.gbs.gbswarehouse.ui.issue.issuing_scrap.orders_list

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list.OrderCard
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list.OrdersListViewModel

@Composable
fun IssuingScrapScreen(
    navController: NavController,
) {
    val viewModel: IssueOrdersListViewModel = viewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.orange


    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "صرف", subtitle = "هالك أو إعدام أو خردة", accent = accent,
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
            Text("SCRAP" ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        if (orders.isEmpty()) {
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
                    OrderCard(order = ord, accent = accent,
                        onClick = {
                           // vm.navigate(Screens.DETAIL, order = ord)
                            navController.navigate(
                                Screens.IssuingScrapDetailsScreen.createRoute(ord.id)
                            )
                        },
                        isMaterialsShowed = viewModel.showMaterials && viewModel.currentClickedOrderIndex == index,
                        showMaterialsClicked = {
                            if (viewModel.currentClickedOrderIndex == index && viewModel.showMaterials) {
                                viewModel.showMaterials = false
                            } else {
                                viewModel.currentClickedOrderIndex = index
                                viewModel.showMaterials = true
                            }
                        })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}