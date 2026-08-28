package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.orders_list

import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrder

data class ReceivingRawMaterialsFromVendorOrdersListState (
    val poList:List<RawMaterialsPurchaseOrder> = emptyList(),
    val poFilteredList:List<RawMaterialsPurchaseOrder> = emptyList(),
    val searchText:String="",
    val loadingStatus: LoadingRawMaterialsPurchaseOrdersListStatus = LoadingRawMaterialsPurchaseOrdersListStatus.IDLE,
    val isMaterialsShowed:Boolean = false,
    val showMaterialsClickedCardIndex:Int = -1
)

sealed class LoadingRawMaterialsPurchaseOrdersListStatus {
    data object IDLE : LoadingRawMaterialsPurchaseOrdersListStatus()
    data object LOADING : LoadingRawMaterialsPurchaseOrdersListStatus()
    data class SUCCESS (val ordersList:List<RawMaterialsPurchaseOrder>) : LoadingRawMaterialsPurchaseOrdersListStatus()
    data class ERROR(val statusMessage:String) : LoadingRawMaterialsPurchaseOrdersListStatus()
}
