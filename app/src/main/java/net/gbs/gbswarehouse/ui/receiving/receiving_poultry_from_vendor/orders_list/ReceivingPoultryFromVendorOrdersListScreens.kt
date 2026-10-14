package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Lucide
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list.SearchBox
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingPoultryFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: ReceivingPoultryFromVendorListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingPoultryFromVendorOrdersListEvent.InitScreen)
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.receiving),
            subtitle = stringResource(R.string.poultry_from_vendor),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingPoultryFromVendorOrdersListEvent.OnSearchTextChanged(it)) },
            onSearchButtonClicked = { },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.poFilteredList.size} ${stringResource(R.string.items)}", color = colors.muted, fontSize = 11.sp)
            Text("PO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingPoultryPurchaseOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingPoultryPurchaseOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingPoultryFromVendorOrdersListEvent.InitScreen)
                        }
                    }
                }
            }
            is LoadingPoultryPurchaseOrdersListStatus.SUCCESS, LoadingPoultryPurchaseOrdersListStatus.IDLE -> {
                val list = state.poFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.reload), accent) {
                                viewModel.onEvent(ReceivingPoultryFromVendorOrdersListEvent.InitScreen)
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            PoultryPurchaseOrderCard(order = ord, accent = accent,
                                onClick = {
                                    navController.navigate(Screens.ReceivingPoultryFromVendorDetailsScreen.createRoute(ord.carNo ?: ""))
                                }, onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingPoultryFromVendorOrdersListEvent.OnShowMaterialsButtonClicked(index))
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
fun PoultryPurchaseOrderCard(
    order: PoultryMaterialsPurchaseOrder,
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
                    PoultryMaterialInfoCard(material, index == order.purchaseOrderItems.lastIndex)
                }
            }
            if(!isMaterialsShowed){
                OutlineBtn(
                    "${order.purchaseOrderItems.size} "+stringResource(R.string.items),
                    accent
                ) {
                    onMaterialsNumberButtonClicked()
                }
            }
        }
    }
}

@Composable
fun PoultryMaterialInfoCard(
    materialInfo: PoultryMaterialsPurchaseOrderItem,
    last: Boolean
) {
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
                    color = AppTheme.colors.text
                )
                Text(
                    text = "${stringResource(R.string.remaining_quantity_)} ${materialInfo.remainingQuantity} ${materialInfo.uom}",
                    color = AppTheme.colors.muted,
                    fontSize = 10.sp
                )
            }
        }
    }
    if (!last) {
        Spacer(Modifier.height(1.dp))
    }
}
