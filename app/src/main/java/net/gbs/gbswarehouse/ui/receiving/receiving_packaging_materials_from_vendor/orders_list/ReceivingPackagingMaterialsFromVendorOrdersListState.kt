package net.gbs.gbswarehouse.ui.receiving.receiving_packaging_materials_from_vendor.orders_list

import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder

data class ReceivingPackagingMaterialsFromVendorOrdersListState (
    val poList:List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val poFilteredList:List<PackagingMaterialsPurchaseOrder> = emptyList(),
    val searchText:String="",
    val loadingStatus: LoadingPackagingPurchaseOrdersListStatus = LoadingPackagingPurchaseOrdersListStatus.LOADING,
    val isMaterialsShowed:Boolean = false,
    val showMaterialsClickedCardIndex:Int = -1
)

sealed class LoadingPackagingPurchaseOrdersListStatus {
    object IDLE : LoadingPackagingPurchaseOrdersListStatus()
    data object LOADING : LoadingPackagingPurchaseOrdersListStatus()
    data class SUCCESS (val ordersList:List<PackagingMaterialsPurchaseOrder>) : LoadingPackagingPurchaseOrdersListStatus()
    data class ERROR(val statusMessage:String) : LoadingPackagingPurchaseOrdersListStatus()
}
