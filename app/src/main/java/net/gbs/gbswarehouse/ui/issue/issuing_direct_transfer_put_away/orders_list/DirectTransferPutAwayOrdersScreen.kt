package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer_put_away.orders_list

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
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.ErrorCard
import net.gbs.gbswarehouse.InfoCell
import net.gbs.gbswarehouse.MarginVertical
import net.gbs.gbswarehouse.SearchBox
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.DirectTransferPutAwayDocument
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun DirectTransferPutAwayOrdersScreen(
    navController: NavController,
    viewModel: DirectTransferPutAwayOrdersViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colors = AppTheme.colors

    LaunchedEffect(Unit) {
        viewModel.getDocuments()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.stock_transfers_putaway),
                subtitle = stringResource(R.string.transfer_documents),
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
                } else if (documents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_results), color = colors.muted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(documents) { document ->
                            DirectTransferPutAwayDocumentCard(
                                document = document,
                                accent = YELLOW,
                                onClick = {
                                    navController.navigate(
                                        Screens.DirectTransferPutAwayDetails.createRoute(document.receiptNo)
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
fun DirectTransferPutAwayDocumentCard(
    document: DirectTransferPutAwayDocument,
    accent: Color,
    onClick: () -> Unit
) {
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
                    Text("\uD83D\uDCE6", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = document.receiptNo,
                        color = colors.text,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.transfer_documents),
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
                label = stringResource(R.string.plant),
                value = document.plantCodes.orEmpty(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.warehouse),
                value = document.warehouseCodes.orEmpty(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.items_count),
                value = document.itemCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(8.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCell(
                label = stringResource(R.string.receiving_plant),
                value = document.receivingPlantCodes.orEmpty(),
                modifier = Modifier.weight(1f)
            )
            InfoCell(
                label = stringResource(R.string.receiving_warehouse),
                value = document.receivingWarehouseCodes.orEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
        MarginVertical(8.dp)
        InfoCell(
            label = stringResource(R.string.transferred_at),
            value = document.transferredAt.orEmpty().substringBefore("T"),
            modifier = Modifier.fillMaxWidth()
        )
        if (!document.materialCode.isNullOrBlank() || !document.materialDescription.isNullOrBlank()) {
            MarginVertical(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!document.materialCode.isNullOrBlank()) {
                    InfoCell(
                        label = stringResource(R.string.material_code),
                        value = document.materialCode,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!document.materialDescription.isNullOrBlank()) {
                    InfoCell(
                        label = stringResource(R.string.material_description),
                        value = document.materialDescription,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        if (document.remainingQuantity != null || !document.carNo.isNullOrBlank()) {
            MarginVertical(8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (document.remainingQuantity != null) {
                    InfoCell(
                        label = stringResource(R.string.remaining_quantity),
                        value = document.remainingQuantity.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!document.carNo.isNullOrBlank()) {
                    InfoCell(
                        label = stringResource(R.string.car_number),
                        value = document.carNo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
