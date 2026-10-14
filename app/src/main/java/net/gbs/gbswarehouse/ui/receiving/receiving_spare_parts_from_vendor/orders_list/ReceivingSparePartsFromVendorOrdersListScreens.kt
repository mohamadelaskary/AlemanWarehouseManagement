package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.MarginTenDp
import net.gbs.gbswarehouse.Order
import net.gbs.gbswarehouse.OutlineBtn
import net.gbs.gbswarehouse.Pill
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.ui.theme.AppTheme

private const val TAG = "SparePartsOrdersListScreen"

@Composable
fun ReceivingSparePartsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: ReceivingSparePartsFromVendorListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.green

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
    }

    val purchasesCount = state.poList.count { !it.poNumber.endsWith("9") }
    val transfersCount = state.poList.count { it.poNumber.endsWith("9") }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = stringResource(R.string.receiving), subtitle = stringResource(R.string.spare_parts_from_vendor), accent = accent,
            onBack = { navController.popBackStack() })

        OrderGroupToggleRow(
            selectedGroup = state.selectedGroup,
            accent = accent,
            onGroupSelected = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnGroupSelected(it)) },
            purchasesCount = purchasesCount,
            transfersCount = transfersCount
        )

        // Search
        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnSearchTextChanged(it)) },
            onSearchButtonClicked = { /* already filtered on change */ },
            placeholder = stringResource(R.string.search_by_number),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.poFilteredList.size} ${stringResource(R.string.items)}", color = colors.muted, fontSize = 11.sp)
            Text("PO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingPurchaseOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingPurchaseOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text, modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
                        }
                    }
                }
            }
            is LoadingPurchaseOrdersListStatus.SUCCESS, LoadingPurchaseOrdersListStatus.IDLE -> {
                val list = state.poFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlineBtn(stringResource(R.string.reload), accent) {
                                viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            PurchaseOrderCard(order = ord, accent = accent,
                                onClick = {
                                    navController.navigate(Screens.ReceivingSparePartsFromVendorDetailsScreen.createRoute(ord.poNumber))
                                }, onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnShowMaterialsButtonClicked(index))
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
fun InspectingSparePartsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: ReceivingSparePartsFromVendorListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.purple

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
    }

    val purchasesCount = state.poList.count { !it.poNumber.endsWith("9") }
    val transfersCount = state.poList.count { it.poNumber.endsWith("9") }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "مراجعة الجودة", subtitle = "قطع غيار من مورد", accent = accent,
            onBack = { navController.popBackStack() })

        OrderGroupToggleRow(
            selectedGroup = state.selectedGroup,
            accent = accent,
            onGroupSelected = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnGroupSelected(it)) },
            purchasesCount = purchasesCount,
            transfersCount = transfersCount
        )

        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnSearchTextChanged(it)) },
            onSearchButtonClicked = {},
            placeholder = "بحث بالرقم أو الاسم...",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.poFilteredList.size} نتيجة", color = colors.muted, fontSize = 11.sp)
            Text("Inspecting", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingPurchaseOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingPurchaseOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text)
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
                        }
                    }
                }
            }
            is LoadingPurchaseOrdersListStatus.SUCCESS, LoadingPurchaseOrdersListStatus.IDLE -> {
                val list = state.poFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            PurchaseOrderCard(order = ord, accent = accent,
                                onClick = {
                                    Log.d(TAG, "InspectingSparePartsFromVendorListScreen: ${ord.toJson()}")
                                    navController.navigate(Screens.InspectingSparePartsFromVendorDetailsScreen.createRoute(ord.toJson()))
                                }, onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnShowMaterialsButtonClicked(index))
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
fun PutAwaySparePartsFromVendorListScreen(
    navController: NavController,
) {
    val viewModel: ReceivingSparePartsFromVendorListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    val accent = colors.yellow

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
    }

    val purchasesCount = state.poList.count { !it.poNumber.endsWith("9") }
    val transfersCount = state.poList.count { it.poNumber.endsWith("9") }

    Column(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        TopBar(title = "تسكين", subtitle = "قطع غيار من مورد", accent = accent,
            onBack = { navController.popBackStack() })

        OrderGroupToggleRow(
            selectedGroup = state.selectedGroup,
            accent = accent,
            onGroupSelected = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnGroupSelected(it)) },
            purchasesCount = purchasesCount,
            transfersCount = transfersCount
        )

        // Search
        SearchBox(
            value = state.searchText,
            onValueChange = { viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnSearchTextChanged(it)) },
            onSearchButtonClicked = {},
            placeholder = "بحث بالرقم أو الاسم...",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )

        // Count
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${state.poFilteredList.size} نتيجة", color = colors.muted, fontSize = 11.sp)
            Text("Put away", color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        when (val status = state.loadingStatus) {
            is LoadingPurchaseOrdersListStatus.LOADING -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
            }
            is LoadingPurchaseOrdersListStatus.ERROR -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(status.statusMessage, color = colors.text)
                        Spacer(Modifier.height(10.dp))
                        OutlineBtn(stringResource(R.string.retry), accent) {
                            viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.InitScreen)
                        }
                    }
                }
            }
            is LoadingPurchaseOrdersListStatus.SUCCESS, LoadingPurchaseOrdersListStatus.IDLE -> {
                val list = state.poFilteredList
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📂", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.no_results), color = colors.muted, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 20.dp)) {
                        itemsIndexed(list) { index, ord ->
                            PurchaseOrderCard(order = ord, accent = accent,
                                onClick = {
                                    navController.navigate(Screens.PutAwaySparePartsFromVendorDetailsScreen.createRoute(ord.poNumber))
                                }, onMaterialsNumberButtonClicked = {
                                    viewModel.onEvent(ReceivingSparePartsFromVendorOrdersListEvent.OnShowMaterialsButtonClicked(index))
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
fun OrderGroupToggleRow(
    selectedGroup: OrderGroup,
    accent: Color,
    onGroupSelected: (OrderGroup) -> Unit,
    purchasesCount: Int,
    transfersCount: Int
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val isPurchasesSelected = selectedGroup == OrderGroup.PURCHASES
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPurchasesSelected) accent else colors.card)
                .border(1.dp, if (isPurchasesSelected) accent else colors.border, RoundedCornerShape(8.dp))
                .clickable { onGroupSelected(OrderGroup.PURCHASES) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${stringResource(R.string.purchases)} ($purchasesCount)",
                color = if (isPurchasesSelected) Color.White else colors.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        val isTransfersSelected = selectedGroup == OrderGroup.TRANSFERS
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isTransfersSelected) accent else colors.card)
                .border(1.dp, if (isTransfersSelected) accent else colors.border, RoundedCornerShape(8.dp))
                .clickable { onGroupSelected(OrderGroup.TRANSFERS) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${stringResource(R.string.transfers)} ($transfersCount)",
                color = if (isTransfersSelected) Color.White else colors.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun PurchaseOrderCard(order: PurchaseOrder, accent: Color, onClick: () -> Unit,onMaterialsNumberButtonClicked:()->Unit,isMaterialsShowed : Boolean = false) {
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
                "#${order.poNumber}",
                color = accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
                // Pill(order.status, order.statusColor) // PurchaseOrder might not have status from API
            Spacer(Modifier.height(6.dp))
            Text(order.vendorName, color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(6.dp))
            if (isMaterialsShowed){
                order.purchaseOrderItems.forEachIndexed { index, material ->
                    MaterialInfoCard(material,index == order.purchaseOrderItems.lastIndex)
                }
            }
            if(!isMaterialsShowed){
                OutlineBtn(
                    "${order.purchaseOrderItems.size} ${stringResource(R.string.items)}",
                    accent
                ) {
                    onMaterialsNumberButtonClicked()
                }
            }
        }
    }
}

@Composable
fun MaterialInfoCard(
    materialInfo: SparePartsMaterial,
    last: Boolean
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = materialInfo.materialCode,
                color = AppTheme.colors.text,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = materialInfo.materialDescription,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.remaining_quantity_)+" ${materialInfo.remainingQuantity} ${materialInfo.uom}",
                color = AppTheme.colors.muted,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }

    if (!last) {
        Spacer(Modifier.height(1.dp))
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
            //    Text("${order.items} أصناف", color = MUTED, fontSize = 10.sp)
            OutlineBtn("${order.items}أصناف", accent) { showMaterialsClicked() }
        }
        if (isMaterialsShowed) {
            MarginTenDp()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(order.materialsToString(), color = colors.text, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun SearchBox(value: String, onValueChange: (String) -> Unit,onSearchButtonClicked: (String) -> Unit, placeholder: String,
              modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = colors.text, fontSize = 12.sp, textDirection = TextDirection.Rtl),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchButtonClicked(value)
            }),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier.weight(1f).padding(vertical = 9.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, color = colors.muted, fontSize = 12.sp)
                inner()
            }
        )
        if (value.isNotEmpty()) {
            Text("✕", color = colors.muted, fontSize = 13.sp,
                modifier = Modifier.clickable { onValueChange("") }.padding(4.dp))
            Text("🔍", color = colors.muted, fontSize = 13.sp,
                modifier = Modifier.clickable { onSearchButtonClicked(value) }.padding(4.dp))
        }

    }
}
