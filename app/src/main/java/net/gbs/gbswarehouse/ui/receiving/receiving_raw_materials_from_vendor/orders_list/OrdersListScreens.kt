package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Lucide
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingRawMaterialsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: OrdersListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green


    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = stringResource(R.string.receiving), subtitle = stringResource(R.string.receiving_raw_materials_fron_vendor), accent = accent,
            onBack = { navController.popBackStack() })

        // Search
        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingRawMaterialsFromVendorOrdersListEvent.OnSearchTextChanged(it)) },
            placeholder = stringResource(R.string.search_by_number_or_name),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.poFilteredList.size}"+stringResource(R.string.result), color = colors.muted, fontSize = 11.sp)
            Text("PO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingRawMaterialsPurchaseOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingRawMaterialsPurchaseOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingRawMaterialsFromVendorOrdersListEvent.Refresh)
                        }
                    }
                }
            }
            is LoadingRawMaterialsPurchaseOrdersListStatus.SUCCESS, LoadingRawMaterialsPurchaseOrdersListStatus.IDLE -> {
                val list = state.poFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_result), color = colors.muted, fontSize = 13.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.refresh), accent) {
                                viewModel.onEvent(ReceivingRawMaterialsFromVendorOrdersListEvent.Refresh)
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            RawMaterialsPurchaseOrderCard(order = ord, accent = accent,
                                onClick = {
                                    navController.navigate(Screens.ReceivingRawMaterialsFromVendorDetailsScreen.createRoute(ord.carNo ?: ""))
                                }, onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingRawMaterialsFromVendorOrdersListEvent.OnShowMaterialsClicked(index))
                                }, isMaterialsShowed = state.isMaterialsShowed && state.showMaterialsClickedCardIndex == index)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RawMaterialsPurchaseOrderCard(
    order: RawMaterialsPurchaseOrder,
    accent: Color,
    onClick: () -> Unit,
    onMaterialsNumberButtonClicked: () -> Unit,
    isMaterialsShowed: Boolean = false
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
                        modifier = Modifier.size(18.dp),
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
                if (!order.factoryCarDescription.isNullOrBlank()) {
                    Text(
                        text = order.factoryCarDescription,
                        color = colors.muted,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(order.vendorName ?: "", color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("${stringResource(R.string.vendor_code)}: ${order.vendorCode ?: ""}", color = colors.muted, fontSize = 10.sp)
            Spacer(Modifier.height(6.dp))
            if (isMaterialsShowed){
                order.purchaseOrderItems.forEachIndexed { index, material ->
                    RawMaterialInfoCard(material, index == order.purchaseOrderItems.lastIndex)
                }
            }
            if(!isMaterialsShowed){
                OutlineBtn(
                    "${order.purchaseOrderItems.size} "+stringResource(R.string.materials),
                    accent
                ) {
                    onMaterialsNumberButtonClicked()
                }
            }
        }
    }
}

@Composable
fun RawMaterialInfoCard(
    materialInfo: RawMaterialsPurchaseOrderItem,
    last: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.dark)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = materialInfo.materialCode ?: "",
                    color = AppTheme.colors.text,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "#${materialInfo.poNumber ?: ""}",
                    color = AppTheme.colors.green,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = materialInfo.materialDescription ?: "",
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    color = AppTheme.colors.text
                )
                Text(
                    text = stringResource(R.string.remaining_quantity_) + " ${materialInfo.remainingQuantity} ${materialInfo.uom}",
                    color = AppTheme.colors.muted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun InspectingRawMaterialsFromVendorListScreen(
    navController: NavController,
) {
    // Keep as is or update to use a separate ViewModel/State if needed.
    // For now, I'll just leave a placeholder that doesn't break.
    val colors = AppTheme.colors
    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "مراجعة الجودة", subtitle = "مواد خام من مورد", accent = colors.purple,
            onBack = { navController.popBackStack() })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("قريباً...", color = colors.text)
        }
    }
}
@Composable
fun PutAwayRawMaterialsFromVendorListScreen(
    navController: NavController,
) {
    val colors = AppTheme.colors
    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "تسكين", subtitle = "مواد خام من مورد", accent = colors.yellow,
            onBack = { navController.popBackStack() })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("قريباً...", color = colors.text)
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
            Pill(order.status, order.statusColor,)
        }
        Spacer(Modifier.height(6.dp))
        Text(order.info, color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(order.date, color = colors.muted, fontSize = 10.sp)
            OutlineBtn(
                if (isMaterialsShowed) stringResource(R.string.hide_items) else "${order.items} ${stringResource(
                    R.string.items)}",
                accent
            ) { showMaterialsClicked() }
        }
        if (isMaterialsShowed) {
            MarginTenDp()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(order.materialsToString(), color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}
