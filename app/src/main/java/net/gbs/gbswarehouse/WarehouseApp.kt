package net.gbs.gbswarehouse

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.core.session.SessionState
import net.gbs.gbswarehouse.ui.HomeScreen.HomeScreen
import net.gbs.gbswarehouse.ui.OpTypeScreen
import net.gbs.gbswarehouse.ui.SubMenu
import net.gbs.gbswarehouse.ui.audit.details_screen.AuditDetailsScreen
import net.gbs.gbswarehouse.ui.audit.orders_list.AuditOrdersScreen
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen.IssueCostCenterDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_cost_center.orders_list.IssuingCostCenterOrdersScreen
import net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer.IssuingDirectTransferScreen
import net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.details_screen.IssueInternalOrdersDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.orders_list.IssuingInternalOrdersScreen
import net.gbs.gbswarehouse.ui.issue.issuing_package_to_production_line.details_screen.IssuePackagingMaterialsToProductionDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_package_to_production_line.orders_list.IssuingPackagingMaterialsToProductionScreen
import net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen.IssueRawMaterialsToProductionDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.orders_list.IssuingRawMaterialsToProductionScreen
import net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.details_screen.IssueSalesOrdersDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.orders_list.IssuingSalesOrdersScreen
import net.gbs.gbswarehouse.ui.issue.issuing_sales_orders_plastic.details_screen.IssueSalesOrdersDetailsPlasticScreen
import net.gbs.gbswarehouse.ui.issue.issuing_sales_orders_plastic.orders_list.IssuingSalesOrdersPlasticScreen
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.IssueScrapScreen
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.details_screen.IssueScrapDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_scrap.orders_list.IssuingScrapScreen
import net.gbs.gbswarehouse.ui.issue.issuing_spare_parts_for_maintainance.details_screen.IssueSparePartsForMaintenanceDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_spare_parts_for_maintainance.orders_list.IssuingSparePartsForMaintenanceScreen
import net.gbs.gbswarehouse.ui.issue.issuing_to_vendor.details_screen.IssueToVendorDetailsScreen
import net.gbs.gbswarehouse.ui.issue.issuing_to_vendor.orders_list.IssuingToVendorScreen
import net.gbs.gbswarehouse.ui.login.LoginScreen
import net.gbs.gbswarehouse.ui.receiving.put_away.details_screen.PutAwayDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.put_away.orders_list.PutAwayOrdersScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen.InspectingPackagingMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen.PutAwayPackagingMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.details_screen.ReceivingPackagingMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list.InspectingPackagingMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list.PutAwayPackagingMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list.ReceivingPackagingMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.ReceivingPoultryFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list.ReceivingPoultryFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen.ReceivingProductFromBranchDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.orders_list.PutAwayProductFromBranchListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.orders_list.ReceivingProductFromBranchListScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen.InspectingFinishedGoodFromProductionDetailsScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen.PutAwayFinishedGoodFromProductionDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen.ReceivingFinishedGoodFromProductionDetailsScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.InspectingFinishedGoodFromProductionListScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.PutAwayFinishedGoodFromProductionListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.ReceivingFinishedGoodFromProductionListScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen.InspectingFinishedGoodFromProductionPlasticDetailsScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen.PutAwayFinishedGoodFromProductionPlasticDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen.ReceivingFinishedGoodsPlasticDetailsScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list.InspectingFinishedGoodFromProductionPlasticListScreen
// import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list.PutAwayFinishedGoodFromProductionPlasticListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.orders_list.ReceivingFinishedGoodsPlasticListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen.InspectingRawMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen.PutAwayRawMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen.ReceivingRawMaterialsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list.InspectingRawMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list.PutAwayRawMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list.ReceivingRawMaterialsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.details_screen.InspectingReturnedProductDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.details_screen.PutAwayReturnedProductDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.details_screen.ReceivingReturnedProductDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.orders_list.InspectingReturnedProductListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.orders_list.PutAwayReturnedProductListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_returned_product.orders_list.ReceivingReturnedProductListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production_line.details_screen.InspectingSparePartsFromProductionDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production_line.details_screen.PutAwaySparePartsFromProductionDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.details_screen.ReceivingSparePartsFromProductionDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production_line.orders_list.InspectingSparePartsFromProductionListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production_line.orders_list.PutAwaySparePartsFromProductionListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.orders_list.ReceivingSparePartsFromProductionListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.PutAwaySparePartsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen.ReceivingSparePartsFromVendorDetailsScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list.InspectingSparePartsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list.PutAwaySparePartsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list.ReceivingSparePartsFromVendorListScreen
import net.gbs.gbswarehouse.ui.receiving.receiving_without_order.blind_receiving.BlindReceivingScreen
import net.gbs.gbswarehouse.ui.settings.SettingsScreen
import javax.inject.Inject

val LocalNavController = staticCompositionLocalOf<NavController> { error("No NavController provided") }

@Composable
fun WarehouseApp () {
    val navController = rememberNavController()
    var showSessionDialog by remember {
        mutableStateOf(false)
    }
    val appViewModel: AppViewModel = hiltViewModel()
    val sessionState by appViewModel.sessionManager
        .sessionState
        .collectAsState()
    LaunchedEffect(Unit) {
        when(sessionState){

            SessionState.Expired -> {
                showSessionDialog = true
            }

            else -> {}
        }
    }

    if(showSessionDialog){

        AlertDialog(

            onDismissRequest = {},

            confirmButton = {

                TextButton(

                    onClick = {

                        showSessionDialog = false

                        navController.navigate(Screens
                            .LoginScreen.route) {

                            popUpTo(0)

                        }

                    }

                ) {

                    Text("OK")

                }

            },

            title = {
                Text("Session Expired")
            },

            text = {
                Text("Please login again.")
            }

        )

    }
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Screens.LoginScreen.route,

            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },

            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },

            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },

            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }

        ) {

            composable(Screens.Home.route) {
                HomeScreen(navController)
            }

            composable(Screens.SubMenu.route) { backStackEntry ->
                val category =
                    backStackEntry.arguments?.getString("category")

                SubMenu(
                    navController = navController,
                    category = category
                )
            }

            composable(Screens.OpTypeScreen.route) { backStackEntry ->
                val opId = backStackEntry.arguments?.getString("opId")
                OpTypeScreen(
                    navController = navController,
                    opId = opId
                )
            }
            composable(Screens.ReceivingRawMaterialsFromVendor.route) { backStackEntry ->

                ReceivingRawMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingRawMaterialsFromVendor.route) {
                InspectingRawMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.PutAwayRawMaterialsFromVendor.route) {
                PutAwayRawMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingRawMaterialsFromVendorDetailsScreen.route) {
                ReceivingRawMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.InspectingRawMaterialsFromVendorDetailsScreen.route) {
                InspectingRawMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.PutAwayRawMaterialsFromVendorDetailsScreen.route) {
                PutAwayRawMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.ReceivingSparePartsFromVendor.route) { backStackEntry ->

                ReceivingSparePartsFromVendorListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingSparePartsFromVendor.route) {
                InspectingSparePartsFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.PutAwaySparePartsFromVendor.route) {
                PutAwaySparePartsFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.ReceivingSparePartsFromVendorPOsList.route){
                ReceivingSparePartsFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.ReceivingSparePartsFromVendorDetailsScreen.route) {
                ReceivingSparePartsFromVendorDetailsScreen(
                    navController = navController,
                    poNumber = it.arguments?.getString("poNumber")!!
                )
            }

//            composable(Screens.InspectingSparePartsFromVendorDetailsScreen.route) {
//                InspectingSparePartsFromVendorDetailsScreen(
//                    navController = navController,
//                    orderId = it.arguments?.getString("orderId")!!
//                )
//            }
            composable(Screens.PutAwaySparePartsFromVendorDetailsScreen.route) {
                PutAwaySparePartsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.ReceivingPackagingMaterialsFromVendor.route) { backStackEntry ->

                ReceivingPackagingMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingPackagingMaterialsFromVendor.route) {
                InspectingPackagingMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.PutAwayPackagingMaterialsFromVendor.route) {
                PutAwayPackagingMaterialsFromVendorListScreen(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingPackagingMaterialsFromVendorDetailsScreen.route) {
                ReceivingPackagingMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.InspectingPackagingMaterialsFromVendorDetailsScreen.route) {
                InspectingPackagingMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.PutAwayPackagingMaterialsFromVendorDetailsScreen.route) {
                PutAwayPackagingMaterialsFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.ReceivingFinishedGoodFromProduction.route) { backStackEntry ->

                ReceivingFinishedGoodFromProductionListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingFinishedGoodFromProduction.route) {
                net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.InspectingFinishedGoodFromProductionListScreenOld(
                    navController = navController,
                )
            }
            composable(Screens.PutAwayFinishedGoodFromProduction.route) {
                net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.orders_list.PutAwayFinishedGoodFromProductionListScreenOld(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingFinishedGoodFromProductionDetailsScreen.route) {
                ReceivingFinishedGoodFromProductionDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.InspectingFinishedGoodFromProductionDetailsScreen.route) {
                net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen.InspectingFinishedGoodFromProductionDetailsScreenOld(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.PutAwayFinishedGoodFromProductionDetailsScreen.route) {
                net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen.PutAwayFinishedGoodFromProductionDetailsScreenOld(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.ReceivingFinishedGoodFromProductionPlastic.route) { backStackEntry ->
                ReceivingFinishedGoodsPlasticListScreen(
                    navController = navController,
                )
            }

            composable(Screens.PutAwayFinishedGoodFromProductionPlastic.route) {
//                PutAwayFinishedGoodFromProductionPlasticListScreen(
//                    navController = navController,
//                )
            }

            composable(Screens.ReceivingFinishedGoodFromProductionPlasticDetailsScreen.route) {
                ReceivingFinishedGoodsPlasticDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.PutAwayFinishedGoodFromProductionPlasticDetailsScreen.route) {
//                PutAwayFinishedGoodFromProductionPlasticDetailsScreen(
//                    navController = navController,
//                    orderId = it.arguments?.getString("orderId")!!
//                )
            }

            composable(Screens.ReceivingSparePartsFromProduction.route) { backStackEntry ->

                ReceivingSparePartsFromProductionListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingSparePartsFromProduction.route) {
                InspectingSparePartsFromProductionListScreen(
                    navController = navController,
                )
            }
            composable(Screens.PutAwaySparePartsFromProduction.route) {
                PutAwaySparePartsFromProductionListScreen(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingSparePartsFromProductionDetailsScreen.route) {
                ReceivingSparePartsFromProductionDetailsScreen(
                    navController = navController,
                    orderData = it.arguments?.getString("orderData")!!,
                    returnReason = it.arguments?.getString("returnReason")!!
                )
            }

            composable(Screens.InspectingSparePartsFromProductionDetailsScreen.route) {
                InspectingSparePartsFromProductionDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.PutAwaySparePartsFromProductionDetailsScreen.route) {
                PutAwaySparePartsFromProductionDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.ReceivingProductFromBranch.route) { backStackEntry ->

                ReceivingProductFromBranchListScreen(
                    navController = navController,
                )
            }


            composable(Screens.PutAwayProductFromBranch.route) {
                PutAwayProductFromBranchListScreen(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingProductFromBranchDetailsScreen.route) {
                ReceivingProductFromBranchDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }


            composable(Screens.ReceivingReturnedProduct.route) { backStackEntry ->

                ReceivingReturnedProductListScreen(
                    navController = navController,
                )
            }

            composable(Screens.InspectingReturnedProduct.route) {
                InspectingReturnedProductListScreen(
                    navController = navController,
                )
            }
            composable(Screens.PutAwayReturnedProduct.route) {
                PutAwayReturnedProductListScreen(
                    navController = navController,
                )
            }

            composable(Screens.ReceivingReturnedProductDetailsScreen.route) {
                ReceivingReturnedProductDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.InspectingReturnedProductDetailsScreen.route) {
                InspectingReturnedProductDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.PutAwayReturnedProductDetailsScreen.route) {
                PutAwayReturnedProductDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.BlindReceiving.route) {
                BlindReceivingScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingToVendor.route) {
                IssuingToVendorScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingToVendorDetailsScreen.route) {
                IssueToVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingRawMaterialsToProduction.route) {
                IssuingRawMaterialsToProductionScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingRawMaterialsToProductionDetailsScreen.route) {
                IssueRawMaterialsToProductionDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingSparePartsForMaintenance.route) {
                IssuingSparePartsForMaintenanceScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingSparePartsForMaintenanceDetailsScreen.route) {
                IssueSparePartsForMaintenanceDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingScrap.route) {
                IssuingScrapScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingScrapDetailsScreen.route) {
                IssueScrapDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingInternalOrders.route) {
                IssuingInternalOrdersScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingInternalOrdersDetailsScreen.route) {
                IssueInternalOrdersDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingSalesOrders.route) {
                IssuingSalesOrdersScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingSalesOrdersDetailsScreen.route) {
                IssueSalesOrdersDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingSalesOrdersPlastic.route) {
                IssuingSalesOrdersPlasticScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingSalesOrdersDetailsPlasticScreen.route) {
                IssueSalesOrdersDetailsPlasticScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingPackagingMaterialsToProductionLineScreen.route) {
                IssuingPackagingMaterialsToProductionScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingPackagingMaterialsToProductionLineDetailsScreen.route) {
                IssuePackagingMaterialsToProductionDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingCostCenterOrders.route) {
                IssuingCostCenterOrdersScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssuingCostCenterOrdersDetailsScreen.route) {
                IssueCostCenterDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.AuditOrders.route) {
                AuditOrdersScreen(
                    navController = navController,
                )
            }
            composable(Screens.AuditDetailsScreen.route) {
                AuditDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }
            composable(Screens.IssuingDirectTransferScreen.route) {
                IssuingDirectTransferScreen(
                    navController = navController,
                )
            }
            composable(Screens.IssueScrapScreen.route) {
                IssueScrapScreen(
                    navController = navController,
                )
            }
            composable(Screens.SettingsScreen.route) {
                SettingsScreen(
                    navController = navController,
                )
            }
            composable(Screens.LoginScreen.route) {
                LoginScreen(
                    navController = navController,
                )
            }
            composable(Screens.ReceivingPoultryFromVendorOrdersListScreen.route) {
                ReceivingPoultryFromVendorListScreen(
                    navController = navController,
                )
            }
            composable(Screens.ReceivingPoultryFromVendorDetailsScreen.route) {
                ReceivingPoultryFromVendorDetailsScreen(
                    navController = navController,
                    orderId = it.arguments?.getString("orderId")!!
                )
            }

            composable(Screens.PutAwayOrders.route) {
                PutAwayOrdersScreen(
                    navController = navController,
                    receiptType = it.arguments?.getString("receiptType")!!
                )
            }

            composable(Screens.PutAwayDetails.route) {
                PutAwayDetailsScreen(
                    navController = navController,
                    receiptType = it.arguments?.getString("receiptType")!!,
                    orderNumber = it.arguments?.getString("orderNumber")!!
                )
            }
        }
    }
}
@HiltViewModel
class AppViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()