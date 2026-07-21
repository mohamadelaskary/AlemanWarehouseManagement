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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import net.gbs.gbswarehouse.RECV_OPS
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.ThemeToggleButton
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun HomeScreen(navController: NavController) {
    val colors = AppTheme.colors
    val cards = listOf(
        Triple("recv",  GREEN,  stringResource(R.string.receiving) to "↓"),
        Triple("issue", ORANGE, stringResource(R.string.issuing)    to "↑") ,
        Triple("audit", BLUE, stringResource(R.string.audit) to "")
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        // Header section
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ── your existing logo + title group ──────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(colors.card2)
                            .border(1.dp, Color(0xFF1E3A5A), RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("🏭", fontSize = 20.sp) }
                    Column {
                        Text(stringResource(R.string.warehouse_ops_system), color = colors.muted, fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                        Text(stringResource(R.string.main_warehouse), color = colors.text, fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LogoutButton(navController)
//                    LanguageToggleButton()
//                    ThemeToggleButton()
                }

            }
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(ORANGE.copy(0.13f), GREEN.copy(0.13f), Color.Transparent)
                        )
                    )
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp)
        ) {
            item {
                Text(stringResource(R.string.select_main_category), color = colors.muted, fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 14.dp, start = 2.dp))
            }
            itemsIndexed(cards) { _, triple ->
                val (id, accent, pair) = triple
                val (label, icon) = pair
                val cnt = if (id == "recv") "8 ${stringResource(R.string.operations)}" else "6 ${stringResource(R.string.operations)}"
                val detail = if (id == "recv") "${stringResource(R.string.raw_materials)} · ${stringResource(R.string.spare_parts)} · ${stringResource(R.string.packaging)} · ${stringResource(R.string.finished_good)} · ${stringResource(R.string.returns)}"
                else "${stringResource(R.string.production)} · ${stringResource(R.string.maintenance)} · ${stringResource(R.string.sales)} · ${stringResource(R.string.internal_order)} · ${stringResource(R.string.scrap)}"

                HomeCard(label = label, icon = icon, cnt = cnt, detail = detail,id = id,
                    accent = accent, onClick = {
                        navController.navigate(
                            if (id == "audit") Screens.AuditOrders.createRoute()
                            else
                                Screens.SubMenu.createRoute(id)
                        )
                    })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun HomeCard(label: String, icon: String, cnt: String, detail: String,
             accent: Color,id:String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (hovered) colors.card2 else colors.card)
            .border(1.dp, if (hovered) accent.copy(0.35f) else accent.copy(0.15f), RoundedCornerShape(16.dp))
            .clickable { onClick(); hovered = false }
            .padding(all = 20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(accent.copy(0.12f))
                    .border(1.dp, accent.copy(0.25f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (id=="audit")
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "audit icon",
                        modifier = Modifier.size(size = 15.dp),
                        tint = accent
                    )
                else
                    Text(icon, color = accent, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(stringResource(R.string.go_forward), color = colors.dim, fontSize = 20.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = colors.text, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
        if (id == "recv" || id == "issue") {
            Spacer(Modifier.height(3.dp))
            Text(cnt, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        if (id == "recv" || id == "issue") {
            Spacer(Modifier.height(8.dp))
            Text(detail, color = colors.muted, fontSize = 11.sp, lineHeight = 18.sp)
        }
    }
}
@Composable
fun SubMenu(
    navController: NavController,
    category: String?
) {
    val isRecv = category == "recv"
    val ops    = if (isRecv) RECV_OPS else ISSUE_OPS
    val accent = if (isRecv) GREEN else ORANGE
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg)
    ) {
        TopBar(
            title    = stringResource(R.string.list_of, if (isRecv) stringResource(R.string.receiving) else stringResource(R.string.issuing)),
            accent   = accent,
            onBack   = { navController.popBackStack() },
            navController = navController
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            itemsIndexed(ops) { index, op ->
                SubmenuItem(
                    index  = index,
                    op     = op,
                    accent = accent,
                    onClick = {
                        if (isRecv) {
                            if (op.id=="recv-blind")
                                  navController.navigate(
                            Screens.BlindReceiving.createRoute()
                            )
                             else
                                navController.navigate(
                                    Screens.OpTypeScreen.createRoute(op.id)
                                )
                        }else {
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
fun SubmenuItem(index: Int, op: Operation, accent: Color, onClick: () -> Unit) {
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
                                "recv-raw" -> navController.navigate(
                                    Screens.ReceivingRawMaterialsFromVendor.createRoute()
                                )
                                "recv-spare" -> navController.navigate(
                                    Screens.ReceivingSparePartsFromVendorDetailsScreen.createRoute()
                                )
                                "recv-pkg" -> navController.navigate(
                                    Screens.ReceivingPackagingMaterialsFromVendor.createRoute()
                                )
                                "recv-blind" -> navController.navigate(
                                    Screens.BlindReceiving.createRoute()
                                )
                                "recv-prod-fg" -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProduction.createRoute()
                                )
                                "recv-prod-fg-plastic" -> navController.navigate(
                                    Screens.ReceivingFinishedGoodFromProductionPlastic.createRoute()
                                )
                                "recv-prod-spare" -> navController.navigate(
                                    Screens.ReceivingSparePartsFromProduction.createRoute()
                                )
                                "recv-branch" -> navController.navigate(
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
                            when(opId) {
                                "recv-raw" -> navController.navigate(
                                    Screens.PutAwayRawMaterialsFromVendor.createRoute()
                                )
                                "recv-spare" -> navController.navigate(
                                    Screens.PutAwaySparePartsFromVendor.createRoute()
                                )
                                "recv-pkg" -> navController.navigate(
                                    Screens.PutAwayPackagingMaterialsFromVendor.createRoute()
                                )
                                "recv-prod-fg" -> navController.navigate(
                                    Screens.PutAwayFinishedGoodFromProduction.createRoute()
                                )
                                "recv-prod-spare" -> navController.navigate(
                                    Screens.PutAwaySparePartsFromProduction.createRoute()
                                )
                                "recv-branch" -> navController.navigate(
                                    Screens.PutAwayProductFromBranch.createRoute()
                                )
                                "recv-return" -> navController.navigate(
                                    Screens.PutAwayReturnedProduct.createRoute()
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
            Text(opType.hint, color = colors.muted, fontSize = 11.sp)
        }
        Text(stringResource(R.string.go_forward), color = colors.dim, fontSize = 20.sp)
    }
}
