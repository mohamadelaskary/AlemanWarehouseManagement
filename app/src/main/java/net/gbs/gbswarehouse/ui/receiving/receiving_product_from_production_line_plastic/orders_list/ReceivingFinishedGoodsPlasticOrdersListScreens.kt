package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list

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
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrderItem
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list.SearchBox
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingFinishedGoodsPlasticListScreen(
    navController: NavController,
) {
    val viewModel: ReceivingFinishedGoodsPlasticListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingFinishedGoodsPlasticOrdersListEvent.InitScreen)
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(
            title = stringResource(R.string.receiving),
            subtitle = stringResource(R.string.finished_good_from_production_plastic),
            accent = accent,
            onBack = { navController.popBackStack() }
        )

        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingFinishedGoodsPlasticOrdersListEvent.OnSearchTextChanged(it)) },
            onSearchButtonClicked = { },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.orderFilteredList.size} ${stringResource(R.string.items)}", color = colors.muted, fontSize = 11.sp)
            Text(stringResource(R.string.production_order_number_), color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingFinishedGoodsPlasticOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingFinishedGoodsPlasticOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingFinishedGoodsPlasticOrdersListEvent.InitScreen)
                        }
                    }
                }
            }
            is LoadingFinishedGoodsPlasticOrdersListStatus.SUCCESS, LoadingFinishedGoodsPlasticOrdersListStatus.IDLE -> {
                val list = state.orderFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.reload), accent) {
                                viewModel.onEvent(ReceivingFinishedGoodsPlasticOrdersListEvent.InitScreen)
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            FinishedGoodsPlasticProductionOrderCard(order = ord, accent = accent,
                                onClick = {
                                    navController.navigate(Screens.ReceivingFinishedGoodFromProductionPlasticDetailsScreen.createRoute(ord.orderNumber))
                                }, onItemsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingFinishedGoodsPlasticOrdersListEvent.OnShowItemsButtonClicked(index))
                                }, isItemsShowed = state.isItemsShowed && state.showItemsClickedCardIndex == index)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinishedGoodsPlasticProductionOrderCard(
    order: FinishedGoodsPlasticProductionOrder,
    accent: Color,
    onClick: () -> Unit,
    onItemsNumberButtonClicked: () -> Unit,
    isItemsShowed: Boolean = false
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
            Text(
                "#${order.orderNumber}",
                color = accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(6.dp))
//            if (isItemsShowed){
                order.productionOrderItems.forEachIndexed { index, material ->
                    FinishedGoodsPlasticItemInfoCard(material, index == order.productionOrderItems.lastIndex)
                }
//            }
//            if(!isItemsShowed){
//                OutlineBtn(
//                    "${order.productionOrderItems.size} ${stringResource(R.string.items)}",
//                    accent
//                ) {
//                    onItemsNumberButtonClicked()
//                }
//            }
        }
    }
}

@Composable
fun FinishedGoodsPlasticItemInfoCard(
    itemInfo: FinishedGoodsPlasticProductionOrderItem,
    last: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = itemInfo.materialCode ?: "",
            color = AppTheme.colors.text,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = itemInfo.materialDescription ?: "",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            fontSize = 10.sp,
            color = AppTheme.colors.text
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.remaining_quantity_)+" ${itemInfo.remainingQuantity} ${itemInfo.uom ?: ""}",            color = AppTheme.colors.muted,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
    if (!last) {
        Spacer(Modifier.height(1.dp))
    }
}
