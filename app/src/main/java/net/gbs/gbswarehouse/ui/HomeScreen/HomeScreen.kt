package net.gbs.gbswarehouse.ui.HomeScreen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.BLUE
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.LogoutButton
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.PURPLE
import net.gbs.gbswarehouse.YELLOW
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun HomeScreen(navController: NavController) {
    val colors = AppTheme.colors
    val cards = listOf(
        Triple("recv",    GREEN,  stringResource(R.string.receiving) to "↓"),
        Triple("putaway", YELLOW, stringResource(R.string.putaway)   to "⌘"),
        Triple("issue",   ORANGE, stringResource(R.string.issuing)     to "↑"),
        Triple("audit",   BLUE,   stringResource(R.string.audit)       to ""),
        Triple("plc",     PURPLE, stringResource(R.string.plc_loading) to "")
    )

    val viewModel: HomeViewModel = hiltViewModel()
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
                    LogoutButton(onClick = {
                        navController.navigate(Screens.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        viewModel.sessionManager.logout()
                    })
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
                val cnt = when (id) {
                    "recv" -> "8 ${stringResource(R.string.operations)}"
                    "putaway" -> "8 ${stringResource(R.string.operations)}"
                    "issue" -> "9 ${stringResource(R.string.operations)}"
                    else -> ""
                }
                val detail = when (id) {
                    "recv" -> "${stringResource(R.string.raw_materials)} · ${stringResource(R.string.spare_parts)} · ${stringResource(R.string.packaging)} · ${stringResource(R.string.finished_good)} · ${stringResource(R.string.returns)}"
                    "putaway" -> "${stringResource(R.string.raw_materials)} · ${stringResource(R.string.spare_parts)} · ${stringResource(R.string.packaging)} · ${stringResource(R.string.finished_good)} · ${stringResource(R.string.transfers)}"
                    "issue" -> "${stringResource(R.string.production)} · ${stringResource(R.string.maintenance)} · ${stringResource(R.string.sales)} · ${stringResource(R.string.internal_order)} · ${stringResource(R.string.scrap)}"
                    else -> "${stringResource(R.string.plc_loading_orders)} · ${stringResource(R.string.plc_conveyors)}"
                }

                HomeCard(label = label, icon = icon, cnt = cnt, detail = detail, id = id,
                    accent = accent, onClick = {
                        navController.navigate(
                            when (id) {
                                "audit" -> Screens.AuditOrders.createRoute()
                                "plc" -> Screens.LoadingOrders.createRoute()
                                else -> Screens.SubMenu.createRoute(id)
                            }
                        )
                    })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun HomeCard(label: String, icon: String, cnt: String, detail: String,
             accent: Color, id: String, onClick: () -> Unit) {
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
        if (id == "recv" || id == "putaway" || id == "issue") {
            Spacer(Modifier.height(3.dp))
            Text(cnt, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        if (id == "recv" || id == "putaway" || id == "issue") {
            Spacer(Modifier.height(8.dp))
            Text(detail, color = colors.muted, fontSize = 11.sp, lineHeight = 18.sp)
        }
    }
}
