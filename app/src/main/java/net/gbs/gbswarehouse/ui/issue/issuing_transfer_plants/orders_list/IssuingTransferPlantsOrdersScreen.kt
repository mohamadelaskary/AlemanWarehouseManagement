package net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.orders_list

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
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Lucide
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.TransferOrder
import net.gbs.gbswarehouse.ui.issue.TransferOrderItemInfoCard
import net.gbs.gbswarehouse.ui.issue.issuing_spare_parts_for_maintainance.orders_list.InfoItem
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun IssuingTransferPlantsScreen(
    navController: NavController,
) {
    val viewModel: IssuingTransferPlantsOrdersViewModel = hiltViewModel()
    val orders = viewModel.filteredOrders
    val colors = AppTheme.colors
    val accent = ORANGE

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.issuing),
            subtitle = stringResource(R.string.warehouse_transfers_between_plants_outcoming),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        SearchBox(
            value = viewModel.orderSearch,
            onValueChange = { viewModel.orderSearch = it },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${orders.size} نتيجة", color = colors.muted, fontSize = 11.sp)
            Text("TRANSFER", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
                itemsIndexed(orders) { _, ord ->
                    val carNo = ord.carNo ?: ""
                    TransferOrderCard(
                        order = ord,
                        accent = accent,
                        text = colors.text,
                        muted = colors.muted,
                        isExpanded = viewModel.expandedCarNumbers.contains(carNo),
                        onToggleExpand = { viewModel.toggleOrderExpanded(carNo) },
                        onClick = {
                            navController.navigate(
                                Screens.IssuingTransferPlantsDetailsScreen.createRoute(carNo)
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
fun TransferOrderCard(
    order: TransferOrder,
    accent: Color,
    muted: Color,
    text: Color,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val itemsCount = if (order.lines.isNotEmpty()) order.lines.size else (order.itemCount ?: 0)
    val factoryCar = order.factoryCarDescription
    val receivingPlant = order.lines.firstOrNull()?.receivingPlantDescription
        ?: order.lines.firstOrNull()?.receivingPlantCode
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
                    text = factoryCar ?: "",
                    color = muted,
                    fontSize = 10.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${order.issuingPlantDescription ?: ""} ➔ ${receivingPlant ?: ""}",
                color = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            if (isExpanded) {
                if (order.lines.isNotEmpty()) {
                    order.lines.forEach { item ->
                        TransferOrderItemInfoCard(item = item)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_items),
                        color = muted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
        }
    }
}