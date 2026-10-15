package net.gbs.gbswarehouse.ui.issue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.ui.MaterialImageButton
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine
import net.gbs.gbswarehouse.domain.model.TransferOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.theme.AppColors
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.ORANGE
import net.gbs.gbswarehouse.batchSpinnerLabel
import net.gbs.gbswarehouse.spinnerLabel
import net.gbs.gbswarehouse.valuationSpinnerLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehousesSpinner(
    hint: String = stringResource(R.string.please_select_a_warehouse),
    items: List<SAPWarehouse>,
    onItemSelected: (SAPWarehouse) -> Unit,
    accent: Color = ORANGE,
    label: String = stringResource(R.string.warehouse),
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.spinnerLabel() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchValuationTypeSpinner(
    items: List<SAPMaterialBatchValuationStock>,
    onItemSelected: (SAPMaterialBatchValuationStock) -> Unit,
    accent: Color = ORANGE,
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit,
    isBatchManaged: Boolean
) {
    val label = if (isBatchManaged) stringResource(R.string.batch) else stringResource(R.string.valuation_type)
    val hint = if (isBatchManaged) stringResource(R.string.select_a_batch) else stringResource(R.string.select_valuation_type)
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        val quantityLabel = stringResource(R.string.qty_)
        val dateLabel = stringResource(R.string.date)
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { "${it.batch} ($quantityLabel ${it.quantity.toString()}) $dateLabel: ${it.batchDate?.take(10)}" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnHandStockSection(
    stockList: List<MaterialOnHandStock>,
    selectedStock: MaterialOnHandStock?,
    availableQty: Double,
    isStockLoading: Boolean,
    stockError: Message?,
    stockSearchText: String,
    onSearchTextChanged: (String) -> Unit,
    onStockSelected: (MaterialOnHandStock) -> Unit,
    accent: Color = ORANGE,
    colors: AppColors = AppTheme.colors
) {
    val dims = AppTheme.dims
    if (isStockLoading) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator(color = accent)
        }
    } else {
        if (stockList.isNotEmpty()) {
            val isBatchManaged = stockList.any { it.isBatchManaged == true }
            val hasValuationType = !isBatchManaged && stockList.any { !it.valuationType.isNullOrEmpty() }

            if (isBatchManaged) {
                // 1- isBatchManaged == true
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp).height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(accent)
                        )
                        Text(
                            stringResource(R.string.batch),
                            color = accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                    SearchableSpinner(
                        hint = stringResource(R.string.select_a_batch),
                        items = stockList,
                        itemLabel = { it.batchSpinnerLabel() },
                        onItemSelected = onStockSelected,
                        searchText = stockSearchText,
                        onSearchTextChanged = onSearchTextChanged
                    )
                }
            } else if (hasValuationType) {
                // 2- isBatchManaged == false and valuationType != null
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp).height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(accent)
                        )
                        Text(
                            stringResource(R.string.valuation_type),
                            color = accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                    SearchableSpinner(
                        hint = stringResource(R.string.select_valuation_type),
                        items = stockList,
                        itemLabel = { it.valuationSpinnerLabel() },
                        onItemSelected = onStockSelected,
                        searchText = stockSearchText,
                        onSearchTextChanged = onSearchTextChanged
                    )
                }
            } else {
                // 3- else: display a text with label "Available quantity"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.dark)
                        .padding(12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.available_quantity),
                            color = colors.muted,
                            fontSize = 12.sp
                        )
                        Text(
                            "$availableQty",
                            color = accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if ((isBatchManaged || hasValuationType) && selectedStock != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.dark)
                        .padding(12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.available_quantity),
                            color = colors.muted,
                            fontSize = 12.sp
                        )
                        Text(
                            "$availableQty",
                            color = accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (stockError != null) {
            Text(
                stockError.text,
                color = colors.danger,
                fontSize = dims.fontSizeSubtitle,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun ReservationLineItemInfoCard(line: SparePartsReturnReservationLine) {
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
                val matCode = line.materialCode
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = matCode ?: "",
                        color = AppTheme.colors.text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    if (!matCode.isNullOrEmpty()) {
                        MaterialImageButton(materialCode = matCode)
                    }
                }
                val qtyVal = line.remainingQuantity ?: line.quantity
                if (qtyVal != null) {
                    Text(
                        text = "${stringResource(R.string.remaining_quantity_)} $qtyVal ${line.uom ?: ""}",
                        color = AppTheme.colors.muted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
            val desc1 = line.materialDescription
            if (!desc1.isNullOrEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = desc1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    color = AppTheme.colors.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun TransferOrderItemInfoCard(item: TransferOrderItem) {
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
                val itemCode = item.materialCode
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = itemCode ?: "",
                        color = AppTheme.colors.text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    if (!itemCode.isNullOrEmpty()) {
                        MaterialImageButton(materialCode = itemCode)
                    }
                }
                Text(
                    text = "#${item.poNumber ?: ""}",
                    color = AppTheme.colors.orange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            val desc2 = item.materialDescription
            if (!desc2.isNullOrEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = desc2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    color = AppTheme.colors.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            val qtyVal = item.remainingQuantity ?: item.orderedQuantity
            if (qtyVal != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${stringResource(R.string.remaining_quantity_)} $qtyVal ${item.uom ?: ""}",
                    color = AppTheme.colors.muted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

