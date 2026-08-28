package net.gbs.gbswarehouse.ui.issue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SearchableSpinner
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.theme.ORANGE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehousesSpinner(hint:String=stringResource(R.string.please_select_a_warehouse), items : List<SAPWarehouse>, onItemSelected: (SAPWarehouse) -> Unit, accent: Color= ORANGE, label: String = stringResource(R.string.warehouse), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    //   val colors = AppTheme.colors
//    var expanded by remember {
//        mutableStateOf(false)
//    }
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
            itemLabel = { "${it.warehouseDescription} (${it.warehouseCode})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchValuationTypeSpinner(
    items : List<SAPMaterialBatchValuationStock>,
    onItemSelected: (SAPMaterialBatchValuationStock) -> Unit,
    accent: Color= ORANGE,
    searchText: String="",
    onSearchTextChanged: (String) -> Unit,
    isBatchManaged:Boolean
) {
    //   val colors = AppTheme.colors
//    var expanded by remember {
//        mutableStateOf(false)
//    }
    val label = if (isBatchManaged) stringResource(R.string.batch) else stringResource(R.string.valuation_type)
    val hint  = if (isBatchManaged) stringResource(R.string.select_a_batch) else stringResource(R.string.select_valuation_type)
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
            itemLabel = { "${it.batch} (${it.quantity.toString()})" },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}