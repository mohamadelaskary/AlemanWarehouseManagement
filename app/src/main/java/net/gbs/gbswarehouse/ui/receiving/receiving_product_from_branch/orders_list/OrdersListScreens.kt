package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.orders_list

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Lucide
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrder
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrderItem
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingProductFromBranchListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.green

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.receiving),
            subtitle = stringResource(R.string.product_from_branch),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        SearchBox(
            value = state.purchaseOrderSearch,
            onValueChange = { viewModel.onSearchTextEntered(it) },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.filteredPurchaseOrders.size} ${stringResource(R.string.items)}", color = colors.muted, fontSize = 11.sp)
            Text("PO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        if (state.isAllPurchaseOrdersLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else {
            val list = state.filteredPurchaseOrders
            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📂", fontSize = 36.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.reload), accent) {
                            viewModel.loadOrders()
                        }
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                    itemsIndexed(list) { index, ord ->
                        TransitPurchaseOrderCard(order = ord, accent = accent,
                            onClick = {
                                navController.navigate(Screens.ReceivingProductFromBranchDetailsScreen.createRoute(ord.carNo ?: ""))
                            }, onMaterialsNumberButtonClicked = {
                                viewModel.onShowMaterialsClicked(index)
                            },
                            isMaterialsShowed = state.isMaterialsShowed && state.showMaterialsClickedCardIndex == index
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InspectingProductFromBranchListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.purple

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.quality_decision),
            subtitle = stringResource(R.string.product_from_branch),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        if (state.isAllPurchaseOrdersLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.filteredPurchaseOrders) { _, it ->
                    TransitPurchaseOrderCard(it, accent, onClick = {})
                }
            }
        }
    }
}

@Composable
fun PutAwayProductFromBranchListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val accent = colors.yellow

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.putaway),
            subtitle = stringResource(R.string.product_from_branch),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        if (state.isAllPurchaseOrdersLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accent)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.filteredPurchaseOrders) { _, it ->
                    TransitPurchaseOrderCard(it, accent, onClick =  {
                        navController.navigate(Screens.PutAwayProductFromBranchDetailsScreen.createRoute(it.carNo ?: ""))
                    })
                }
            }
        }
    }
}

@Composable
fun TransitPurchaseOrderCard(
    order: TransitOrderMaterialPurchaseOrder,
    accent: Color,
    isMaterialsShowed: Boolean = false,
    onClick: () -> Unit,
    onMaterialsNumberButtonClicked: () -> Unit = {}
) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(); hovered = false },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hovered) colors.card2 else colors.card
        ),
        border = BorderStroke(1.dp, if (hovered) colors.border2 else colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
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
            if (!order.warehouseCode.isNullOrEmpty()){
                Text(
                    text = "${order.warehouseCode} - ${order.warehouseDescription}",
                    color = colors.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(order.issuingPlantDescription ?: "", color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("${stringResource(R.string.issuing_plant_code)}: ${order.issuingPlantCode ?: ""}", color = colors.muted, fontSize = 10.sp)
            Spacer(Modifier.height(6.dp))
            if (isMaterialsShowed) {
                order.purchaseOrderItems.forEach { material ->
                    TransitOrderMaterialInfoCard(material)
                }
            }
            if (!isMaterialsShowed) {
                OutlineBtn(
                    "${order.purchaseOrderItems.size} ${stringResource(R.string.items)}",
                    accent
                ) {
                    onMaterialsNumberButtonClicked()
                }
            }
        }
    }
}

@Composable
fun TransitOrderMaterialInfoCard(
    materialInfo: TransitOrderMaterialPurchaseOrderItem
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colors.dark)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = materialInfo.materialCode ?: "",
                    color = colors.text,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "#${materialInfo.poNumber ?: ""}",
                    color = colors.green,
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
                    text = materialInfo.materialDescription ?: "",
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    color = colors.text,
                )
                Text(
                    text = stringResource(R.string.remaining_quantity_) + " ${materialInfo.remainingQuantity} ${materialInfo.uom}",
                    color = colors.muted,
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
            if (!materialInfo.carNo.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = "🚛",
                        fontSize = 14.sp,
                        color = colors.text,
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = materialInfo.carNo,
                        color = colors.text,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
