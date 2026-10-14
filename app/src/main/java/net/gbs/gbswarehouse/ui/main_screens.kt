package net.gbs.gbswarehouse.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.gbs.gbswarehouse.BLUE
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.ISSUE_OPS
import net.gbs.gbswarehouse.LanguageToggleButton
import net.gbs.gbswarehouse.LogoutButton
import net.gbs.gbswarehouse.OP_TYPES_WITHOUT_INSPECTION
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.OpType
import net.gbs.gbswarehouse.Operation
import net.gbs.gbswarehouse.PURPLE
import net.gbs.gbswarehouse.PUT_AWAY_OPS
import net.gbs.gbswarehouse.RECV_OPS
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.ReceivingScreenNames
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.ThemeToggleButton
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.ui.theme.AppTheme


@Composable
fun SubMenu(
    navController: NavController,
    category: String?,
    viewModel: SubMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(category) {
        viewModel.loadCounts(category)
    }

    val isRecv = category == "recv"
    val isPutAway = category == "putaway"
    val ops = when (category) {
        "recv" -> RECV_OPS
        "putaway" -> PUT_AWAY_OPS
        else -> ISSUE_OPS
    }
    val accent = when (category) {
        "recv" -> GREEN
        "putaway" -> YELLOW
        else -> ORANGE
    }
    val titleText = when (category) {
        "recv" -> stringResource(R.string.receiving)
        "putaway" -> stringResource(R.string.putaway)
        else -> stringResource(R.string.issuing)
    }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg)
    ) {
        TopBar(
            title    = stringResource(R.string.list_of, titleText),
            accent   = accent,
            onBack   = { navController.popBackStack() },
            navController = navController
        )
        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = accent
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            itemsIndexed(ops) { index, op ->
                val count = uiState.countsMap[op.id]
                SubmenuItem(
                    index  = index,
                    op     = op,
                    accent = accent,
                    count  = count,
                    onClick = {
                        if (isPutAway) {
                            if (op.id == "issue-direct-transfer-putaway") {
                                navController.navigate(Screens.DirectTransferPutAwayOrders.createRoute())
                            } else {
                                navController.navigate(Screens.PutAwayOrders.createRoute(op.id))
                            }
                        } else if (isRecv) {
                            when (op.id) {
                                ReceivingScreenNames.RawMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingRawMaterialsFromVendor.createRoute()
                                )
                                ReceivingScreenNames.SparePartsFromVendor -> navController.navigate(
                                    Screens.ReceivingSparePartsFromVendorPOsList.createRoute()
                                )
                                ReceivingScreenNames.PackagingMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingPackagingMaterialsFromVendor.createRoute()
                                )
                                ReceivingScreenNames.PoultryMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingPoultryFromVendorOrdersListScreen.createRoute()
                                )
                                "recv-blind" -> navController.navigate(
                                    Screens.BlindReceiving.createRoute()
                                )
                                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProduction.createRoute()
                                )
                                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProductionPlastic.createRoute()
                                )
                                ReceivingScreenNames.SparePartsReturn -> navController.navigate(
                                    Screens.ReceivingSparePartsFromProduction.createRoute()
                                )
                                ReceivingScreenNames.TransitOrderMaterial -> navController.navigate(
                                    Screens.ReceivingProductFromBranch.createRoute()
                                )
                                "recv-return" -> navController.navigate(
                                    Screens.ReceivingReturnedProduct.createRoute()
                                )
                                else -> navController.navigate(
                                    Screens.OpTypeScreen.createRoute(op.id)
                                )
                            }
                        } else {
                            when(op.id) {
                                "issue-sub" -> navController.navigate(
                                    Screens.IssuingToVendor.createRoute()
                                )
                                "issue-package" -> navController.navigate(
                                    Screens.IssuingPackagingMaterialsToProductionLineScreen.createRoute()
                                )
                                "issue-raw" -> navController.navigate(
                                    Screens.IssuingRawMaterialsToProduction.createRoute()
                                )
                                "issue-spare" -> navController.navigate(
                                    Screens.IssuingSparePartsForMaintenance.createRoute()
                                )
                                "issue-internal" -> navController.navigate(
                                    Screens.IssuingInternalOrders.createRoute()
                                )
                                "issue-sales" -> navController.navigate(
                                    Screens.IssuingSalesOrders.createRoute()
                                )
                                "issue-sales_plastic" -> navController.navigate(
                                    Screens.IssuingSalesOrdersPlastic.createRoute()
                                )
                                "issue-scrap" -> navController.navigate(
                                    Screens.IssueScrapScreen.createRoute()
                                )
                                "issue-cost-center" -> navController.navigate(
                                    Screens.IssuingCostCenterOrders.createRoute()
                                )
                                "issue-direct-transfer" ->navController.navigate(
                                    Screens.IssuingDirectTransferScreen.createRoute()
                                )
                                "issue-transfer-plants" -> navController.navigate(
                                    Screens.IssuingTransferPlants.createRoute()
                                )
                                "issue-direct-transfer-putaway" -> navController.navigate(
                                    Screens.DirectTransferPutAwayOrders.createRoute()
                                )
                            }
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SubmenuItem(
    index: Int,
    op: Operation,
    accent: Color,
    count: Int? = null,
    onClick: () -> Unit
) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    val lang = SettingsPrefs(context = LocalContext.current).getLanguage()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (hovered) colors.card2 else colors.card)
            .border(1.dp, if (hovered) colors.border2 else colors.border, RoundedCornerShape(12.dp))
            .clickable { onClick(); hovered = false }
            .padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(0.09f))
                .border(1.dp, accent.copy(0.18f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Text("${index + 1}", color = accent, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold) }
        Column(modifier = Modifier.weight(1f)) {
            Text(if (lang=="ar")op.arName else op.enName, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                lineHeight = 18.sp)
            if (op.blind) {
                Text(
                    stringResource(R.string.no_reference_manual_entry), color = PURPLE, fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
            }
        }
        if (count != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(0.12f))
                    .border(1.dp, accent.copy(0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(stringResource(R.string.go_forward), color = colors.dim, fontSize = 18.sp)
    }
}

@Composable
fun OpTypeScreen(
    navController: NavController,
    opId: String?
) {
    val colors = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg)
    ) {
        TopBar(
            title = stringResource(R.string.receiving),
            accent = GREEN,
            onBack = { navController.popBackStack() },
            navController = navController
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp)
        ) {
            item {
                Text(stringResource(R.string.select_operation_type), color = colors.muted, fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 14.dp, start = 2.dp))
            }
            itemsIndexed(
                OP_TYPES_WITHOUT_INSPECTION
            ) { _, t ->
                OpTypeCard(opType = t, onClick = {
                    when(t.id) {
                        "receive" -> {
                            when(opId) {
                                ReceivingScreenNames.RawMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingRawMaterialsFromVendor.createRoute()
                                )
                                ReceivingScreenNames.SparePartsFromVendor -> navController.navigate(
                                    Screens.ReceivingSparePartsFromVendorPOsList.createRoute()
                                )
                                ReceivingScreenNames.PackagingMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingPackagingMaterialsFromVendor.createRoute()
                                )
                                ReceivingScreenNames.PoultryMaterialsFromVendor -> navController.navigate(
                                    Screens.ReceivingPoultryFromVendorOrdersListScreen.createRoute()
                                )
                                "recv-blind" -> navController.navigate(
                                    Screens.BlindReceiving.createRoute()
                                )
                                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProduction.createRoute()
                                )
                                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProductionPlastic.createRoute()
                                )
                                ReceivingScreenNames.SparePartsReturn -> navController.navigate(
                                    Screens.ReceivingSparePartsFromProduction.createRoute()
                                )
                                ReceivingScreenNames.TransitOrderMaterial -> navController.navigate(
                                    Screens.ReceivingProductFromBranch.createRoute()
                                )
                                "recv-return" -> navController.navigate(
                                    Screens.ReceivingReturnedProduct.createRoute()
                                )
                            }
                        }
                        "quality" ->{
                            when(opId){
                                "recv-raw" -> navController.navigate(
                                    Screens.InspectingRawMaterialsFromVendor.createRoute()
                                )
                                "recv-spare" -> navController.navigate(
                                    Screens.InspectingSparePartsFromVendor.createRoute()
                                )
                                "recv-pkg" -> navController.navigate(
                                    Screens.InspectingPackagingMaterialsFromVendor.createRoute()
                                )
                                "recv-prod-fg" -> navController.navigate(
                                    Screens.InspectingFinishedGoodFromProduction.createRoute()
                                )
                                "recv-prod-spare" -> navController.navigate(
                                    Screens.InspectingSparePartsFromProduction.createRoute()
                                )
                                "recv-branch" -> navController.navigate(
                                    Screens.InspectingProductFromBranch.createRoute()
                                )
                                "recv-return" -> navController.navigate(
                                    Screens.InspectingReturnedProduct.createRoute()
                                )
                            }
                        }
                        "putaway" ->
                            if (opId != null) {
                                navController.navigate(
                                    Screens.PutAwayOrders.createRoute(opId)
                                )
                            }
                    }
                })
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun OpTypeCard(opType: OpType, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    val colors = AppTheme.colors
    val lang = SettingsPrefs(context = LocalContext.current).getLanguage()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (hovered) colors.card2 else colors.card)
            .border(1.dp, if (hovered) opType.accent.copy(0.4f) else opType.accent.copy(0.15f),
                RoundedCornerShape(14.dp))
            .clickable { onClick(); hovered = false }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(opType.accent.copy(0.12f))
                .border(1.dp, opType.accent.copy(0.28f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Text(opType.icon, color = opType.accent, fontSize = 22.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(if (lang=="ar")opType.label else opType.enLabel, color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(3.dp))
            Text(if (lang=="ar")opType.arHint else opType.enHint, color = colors.muted, fontSize = 11.sp)
        }
        Text(stringResource(R.string.go_forward), color = colors.dim, fontSize = 20.sp)
    }
}
