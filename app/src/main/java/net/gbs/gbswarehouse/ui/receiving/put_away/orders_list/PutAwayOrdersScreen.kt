package net.gbs.gbswarehouse.ui.receiving.put_away.orders_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.*
import net.gbs.gbswarehouse.domain.model.PutAwayOrder
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun PutAwayOrdersScreen(
    navController: NavController,
    receiptType: String,
    viewModel: PutAwayOrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colors = AppTheme.colors

    LaunchedEffect(receiptType) {
        viewModel.getOrders(receiptType)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.putaway),
                subtitle = arabicScreenNames(receiptType),
                accent = YELLOW,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        },
        containerColor = colors.bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBox(
                value = searchText,
                onValueChange = viewModel::onSearchTextChanged,
                placeholder = stringResource(R.string.search_by_number),
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = YELLOW
                    )
                } else if (errorMessage != null) {
                    ErrorCard(
                        label = stringResource(R.string.error),
                        message = errorMessage!!,
                        accent = DANGER
                    )
                } else if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_results), color = colors.muted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            PutAwayOrderCard(
                                order = order,
                                accent = YELLOW,
                                onClick = {
                                    navController.navigate(
                                        Screens.PutAwayDetails.createRoute(
                                            receiptType,
                                            order.orderNumber
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PutAwayOrderCard(order: PutAwayOrder, accent: Color, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, accent.copy(0.15f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(0.1f))
                        .border(1.dp, accent.copy(0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📦", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = order.orderNumber,
                        color = colors.text,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.putaway),
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(stringResource(R.string.go_forward), color = colors.dim, fontSize = 20.sp)
        }
        MarginVertical(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.items_count),
                value = order.itemCount.toString(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.date),
                value = order.earliestReceivedAt.substringBefore("T"),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
