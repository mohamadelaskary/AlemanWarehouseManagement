package net.gbs.gbswarehouse

sealed class Screens(val route: String) {

    object Home : Screens("home")

    object SubMenu : Screens("submenu/{category}") {
        fun createRoute(category: String) =
            "submenu/$category"
    }

    object OpTypeScreen : Screens("opType/{opId}") {
        fun createRoute(opId: String) =
            "opType/${opId}"
    }

    object ReceivingRawMaterialsFromVendor : Screens("receiving_raw_materials_from_vendor"){
        fun createRoute() =
            "receiving_raw_materials_from_vendor"
    }
    object InspectingRawMaterialsFromVendor : Screens("inspecting_raw_materials_from_vendor"){
        fun createRoute() =
            "inspecting_raw_materials_from_vendor"
    }

    object PutAwayRawMaterialsFromVendor : Screens("put_away_raw_materials_from_vendor"){
        fun createRoute() =
            "put_away_raw_materials_from_vendor"
    }



    object IssuingRawMaterialToProductionLineScreen : Screens("issuing_raw_material_to_production_line"){
        fun createRoute() =
            "issuing_raw_material_to_production_line"
    }

    object ReceivingRawMaterialsFromVendorDetailsScreen : Screens("receiving_raw_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_raw_materials_from_vendor_details_screen/$orderId"
    }

    object InspectingRawMaterialsFromVendorDetailsScreen : Screens("inspecting_raw_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_raw_materials_from_vendor_details_screen/$orderId"

    }
    object PutAwayRawMaterialsFromVendorDetailsScreen : Screens("put_away_raw_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_raw_materials_from_vendor_details_screen/$orderId"
    }
    object ReceivingSparePartsFromVendor : Screens("receiving_spare_parts_from_vendor"){
        fun createRoute() =
            "receiving_spare_parts_from_vendor"
    }
    object InspectingSparePartsFromVendor : Screens("inspecting_spare_parts_from_vendor"){
        fun createRoute() =
            "inspecting_spare_parts_from_vendor"
    }

    object PutAwaySparePartsFromVendor : Screens("put_away_spare_parts_from_vendor"){
        fun createRoute() =
            "put_away_spare_parts_from_vendor"
    }

    

    object ReceivingSparePartsFromVendorDetailsScreen : Screens("receiving_spare_parts_from_vendor_details_screen"){
        fun createRoute(
        ) =
            "receiving_spare_parts_from_vendor_details_screen"
    }

    object InspectingSparePartsFromVendorDetailsScreen : Screens("inspecting_spare_parts_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_spare_parts_from_vendor_details_screen/$orderId"

    }
    object PutAwaySparePartsFromVendorDetailsScreen : Screens("put_away_spare_parts_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_spare_parts_from_vendor_details_screen/$orderId"
    }
    object ReceivingPackagingMaterialsFromVendor : Screens("receiving_packaging_materials_from_vendor"){
        fun createRoute() =
            "receiving_packaging_materials_from_vendor"
    }
    object InspectingPackagingMaterialsFromVendor : Screens("inspecting_packaging_materials_from_vendor"){
        fun createRoute() =
            "inspecting_packaging_materials_from_vendor"
    }

    object PutAwayPackagingMaterialsFromVendor : Screens("put_away_packaging_materials_from_vendor"){
        fun createRoute() =
            "put_away_packaging_materials_from_vendor"
    }



    object ReceivingPackagingMaterialsFromVendorDetailsScreen : Screens("receiving_packaging_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_packaging_materials_from_vendor_details_screen/$orderId"
    }

    object InspectingPackagingMaterialsFromVendorDetailsScreen : Screens("inspecting_packaging_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_packaging_materials_from_vendor_details_screen/$orderId"

    }
    object PutAwayPackagingMaterialsFromVendorDetailsScreen : Screens("put_away_packaging_materials_from_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_packaging_materials_from_vendor_details_screen/$orderId"
    }

    object ReceivingFinishedGoodFromProduction : Screens("receiving_finished_good_from_production"){
        fun createRoute() =
            "receiving_finished_good_from_production"
    }
    object InspectingFinishedGoodFromProduction : Screens("inspecting_finished_good_from_production"){
        fun createRoute() =
            "inspecting_finished_good_from_production"
    }

    object PutAwayFinishedGoodFromProduction : Screens("put_away_finished_good_from_production"){
        fun createRoute() =
            "put_away_finished_good_from_production"
    }



    object ReceivingFinishedGoodFromProductionDetailsScreen : Screens("receiving_finished_good_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_finished_good_from_production_details_screen/$orderId"
    }

    object InspectingFinishedGoodFromProductionDetailsScreen : Screens("inspecting_finished_good_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_finished_good_from_production_details_screen/$orderId"

    }
    object PutAwayFinishedGoodFromProductionDetailsScreen : Screens("put_away_finished_good_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_finished_good_from_production_details_screen/$orderId"
    }
    object ReceivingFinishedGoodFromProductionPlastic : Screens("receiving_finished_good_from_production_plastic"){
        fun createRoute() =
            "receiving_finished_good_from_production_plastic"
    }
//    object InspectingFinishedGoodFromProductionPlastic : Screens("inspecting_finished_good_from_production_plastic"){
//        fun createRoute() =
//            "inspecting_finished_good_from_production_plastic"
//    }

    object PutAwayFinishedGoodFromProductionPlastic : Screens("put_away_finished_good_from_production_plastic"){
        fun createRoute() =
            "put_away_finished_good_from_production_plastic"
    }



    object ReceivingFinishedGoodFromProductionPlasticDetailsScreen : Screens("receiving_finished_good_from_production_plastic_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_finished_good_from_production_plastic_details_screen/$orderId"
    }

//    object InspectingFinishedGoodFromProductionPlasticDetailsScreen : Screens("inspecting_finished_good_from_production_plastic_details_screen/{orderId}"){
//        fun createRoute(
//            orderId: String
//        ) =
//            "inspecting_finished_good_from_production_plastic_details_screen/$orderId"
//
//    }
    object PutAwayFinishedGoodFromProductionPlasticDetailsScreen : Screens("put_away_finished_good_from_production_plastic_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_finished_good_from_production_plastic_details_screen/$orderId"
    }



    object ReceivingSparePartsFromProduction : Screens("receiving_spare_parts_from_production"){
        fun createRoute() =
            "receiving_spare_parts_from_production"
    }
    object InspectingSparePartsFromProduction : Screens("inspecting_spare_parts_from_production"){
        fun createRoute() =
            "inspecting_spare_parts_from_production"
    }

    object PutAwaySparePartsFromProduction : Screens("put_away_spare_parts_from_production"){
        fun createRoute() =
            "put_away_spare_parts_from_production"
    }



    object ReceivingSparePartsFromProductionDetailsScreen : Screens("receiving_spare_parts_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_spare_parts_from_production_details_screen/$orderId"
    }

    object InspectingSparePartsFromProductionDetailsScreen : Screens("inspecting_spare_parts_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_spare_parts_from_production_details_screen/$orderId"

    }
    object PutAwaySparePartsFromProductionDetailsScreen : Screens("put_away_spare_parts_from_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_spare_parts_from_production_details_screen/$orderId"
    }

    object ReceivingProductFromBranch : Screens("receiving_product_from_branch"){
        fun createRoute() =
            "receiving_product_from_branch"
    }
    object InspectingProductFromBranch : Screens("inspecting_product_from_branch"){
        fun createRoute() =
            "inspecting_product_from_branch"
    }

    object PutAwayProductFromBranch : Screens("put_away_product_from_branch"){
        fun createRoute() =
            "put_away_product_from_branch"
    }



    object ReceivingProductFromBranchDetailsScreen : Screens("receiving_product_from_branch_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_product_from_branch_details_screen/$orderId"
    }

    object InspectingProductFromBranchDetailsScreen : Screens("inspecting_product_from_branch_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_product_from_branch_details_screen/$orderId"

    }
    object PutAwayProductFromBranchDetailsScreen : Screens("put_away_product_from_branch_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_product_from_branch_details_screen/$orderId"
    }

    object ReceivingReturnedProduct : Screens("receiving_returned_product"){
        fun createRoute() =
            "receiving_returned_product"
    }
    object InspectingReturnedProduct : Screens("inspecting_returned_product"){
        fun createRoute() =
            "inspecting_returned_product"
    }

    object PutAwayReturnedProduct : Screens("put_away_returned_product"){
        fun createRoute() =
            "put_away_returned_product"
    }



    object ReceivingReturnedProductDetailsScreen : Screens("receiving_returned_product_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "receiving_returned_product_details_screen/$orderId"
    }

    object InspectingReturnedProductDetailsScreen : Screens("inspecting_returned_product_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "inspecting_returned_product_details_screen/$orderId"

    }
    object PutAwayReturnedProductDetailsScreen : Screens("put_away_returned_product_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "put_away_returned_product_details_screen/$orderId"
    }
    object BlindReceiving : Screens("blind_receiving"){
        fun createRoute() =
            "blind_receiving"
    }

    object IssuingToVendor : Screens("issuing_to_vendor"){
        fun createRoute() =
            "issuing_to_vendor"
    }

    object IssuingToVendorDetailsScreen : Screens("issuing_to_vendor_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_to_vendor_details_screen/$orderId"
    }
    object IssuingSalesOrders : Screens("issuing_sales_orders"){
        fun createRoute() =
            "issuing_sales_orders"
    }

    object IssuingSalesOrdersDetailsScreen : Screens("issuing_sales_orders_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_sales_orders_details_screen/$orderId"
    }
    object IssuingSalesOrdersPlastic : Screens("issuing_sales_orders_plastic"){
        fun createRoute() =
            "issuing_sales_orders_plastic"
    }

    object IssuingSalesOrdersDetailsPlasticScreen : Screens("issuing_sales_orders_details_plastic_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_sales_orders_details_plastic_screen/$orderId"
    }
    object IssuingSparePartsForMaintenance : Screens("issuing_spare_parts_for_maintenance"){
        fun createRoute() =
            "issuing_spare_parts_for_maintenance"
    }

    object IssuingSparePartsForMaintenanceDetailsScreen : Screens("issuing_spare_parts_for_maintenance_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_spare_parts_for_maintenance_details_screen/$orderId"
    }
    object IssuingInternalOrders : Screens("issuing_internal_orders"){
        fun createRoute() =
            "issuing_internal_orders"
    }

    object IssuingInternalOrdersDetailsScreen : Screens("issuing_internal_orders_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_internal_orders_details_screen/$orderId"
    }
    object IssuingCostCenterOrders : Screens("issuing_cost_center_orders"){
        fun createRoute() =
            "issuing_cost_center_orders"
    }

    object IssuingCostCenterOrdersDetailsScreen : Screens("issuing_cost_center_orders_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_cost_center_orders_details_screen/$orderId"
    }
    object AuditOrders : Screens("audit_orders"){
        fun createRoute() =
            "audit_orders"
    }

    object AuditDetailsScreen : Screens("audit_orders_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "audit_orders_details_screen/$orderId"
    }
    object IssuingRawMaterialsToProduction : Screens("issuing_raw_materials_to_production"){
        fun createRoute() =
            "issuing_raw_materials_to_production"
    }

    object IssuingRawMaterialsToProductionDetailsScreen : Screens("issuing_raw_materials_to_production_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_raw_materials_to_production_details_screen/$orderId"
    }
    object IssuingScrap : Screens("issuing_scrap"){
        fun createRoute() =
            "issuing_scrap"
    }

    object IssuingScrapDetailsScreen : Screens("issuing_scrap_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_scrap_details_screen/$orderId"
    }

    object IssuingPackagingMaterialsToProductionLineScreen : Screens("issuing_packaging_materials_to_production_line"){
        fun createRoute() =
            "issuing_packaging_materials_to_production_line"
    }

    object IssuingPackagingMaterialsToProductionLineDetailsScreen : Screens("issuing_packaging_materials_to_production_line_details_screen/{orderId}"){
        fun createRoute(
            orderId: String
        ) =
            "issuing_packaging_materials_to_production_line_details_screen/$orderId"
    }
    object IssuingDirectTransferScreen : Screens("issuing_direct_transfer"){
        fun createRoute() =
            "issuing_direct_transfer"
    }
    object IssueScrapScreen : Screens("issue_scrap"){
        fun createRoute() =
            "issue_scrap"
    }
    object SettingsScreen : Screens("settings"){
        fun createRoute() =
            "settings"
    }
    object LoginScreen : Screens("login"){
        fun createRoute() =
            "login"
    }

}