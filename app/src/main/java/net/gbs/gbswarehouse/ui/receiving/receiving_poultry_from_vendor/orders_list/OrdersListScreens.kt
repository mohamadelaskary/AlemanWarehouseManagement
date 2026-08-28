package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

import android.util.Log
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.WarehouseViewModel
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingPackagingMaterialsFromVendorListScreenOld(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = viewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.green


    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "استلام", subtitle = "مواد تعبئة وتغليف من مورد", accent = accent,
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
            Text("PO" ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
                          //  viewModel.navigate(Screens.DETAIL, order = ord)
                            viewModel.currentClickedOrderIndex = index
                            navController.navigate(Screens.ReceivingPackagingMaterialsFromVendorDetailsScreen.createRoute(ord.id))
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

@Composable
fun InspectingPackagingMaterialsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = viewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.purple

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "مراجعة الجودة", subtitle = "مواد تعبئة وتغليف من مورد", accent = accent,
            onBack = {
              //  viewModel.back()
                navController.popBackStack()
            })

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
            Text("PO" ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
                       //     viewModel.navigate(Screens.DETAIL, order = ord)
                            navController.navigate(Screens.InspectingPackagingMaterialsFromVendorDetailsScreen.createRoute(ord.id))
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
@Composable
fun PutAwayPackagingMaterialsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = viewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = colors.yellow

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "تسكين", subtitle = "مواد تعبئة وتغليف من مورد", accent = accent,
            onBack = {
               // viewModel.back()
                navController.popBackStack()
            })

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
            Text("PO" ?: "", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
                        //    viewModel.navigate(Screens.DETAIL, order = ord)
                            navController.navigate(Screens.PutAwayPackagingMaterialsFromVendorDetailsScreen.createRoute(ord.id))
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


@Composable
fun OrderCard(order: Order, accent: Color, onClick: () -> Unit,isMaterialsShowed : Boolean = false,showMaterialsClicked:()->Unit) {
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("#${order.id}", color = accent, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Pill(order.status, order.statusColor)
        }
        Spacer(Modifier.height(6.dp))
        Text(order.info, color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(order.date, color = colors.muted, fontSize = 10.sp)
        //    Text("${order.items} أصناف", color = MUTED, fontSize = 10.sp)
            OutlineBtn("${order.items}أصناف", accent) { showMaterialsClicked() }
        }
        if (isMaterialsShowed) {
            MarginTenDp()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(order.materialsToString(), color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}