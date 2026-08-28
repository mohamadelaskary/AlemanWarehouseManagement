package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.orders_list

import net.gbs.gbswarehouse.domain.model.PurchaseOrder

data class ReceivingSparePartsFromVendorOrdersListState (
    val poList:List<PurchaseOrder> = emptyList(),
    val poFilteredList:List<PurchaseOrder> = emptyList(),
    val searchText:String="",
    val loadingStatus: LoadingPurchaseOrdersListStatus = LoadingPurchaseOrdersListStatus.LOADING,
    val isMaterialsShowed:Boolean = false,
    val showMaterialsClickedCardIndex:Int = -1
    )

sealed class LoadingPurchaseOrdersListStatus {
    object IDLE : LoadingPurchaseOrdersListStatus()
    data object LOADING : LoadingPurchaseOrdersListStatus()
    data class SUCCESS (val ordersList:List<PurchaseOrder>) : LoadingPurchaseOrdersListStatus()
    data class ERROR(val statusMessage:String) : LoadingPurchaseOrdersListStatus()

}