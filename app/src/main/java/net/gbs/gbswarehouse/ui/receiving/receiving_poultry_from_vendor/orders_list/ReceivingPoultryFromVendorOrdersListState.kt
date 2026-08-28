package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.orders_list

import net.gbs.gbswarehouse.data.remote.dto.response.PoultryMaterialsPurchaseOrderDto
import net.gbs.gbswarehouse.domain.model.PackagingMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrder

data class ReceivingPoultryFromVendorOrdersListState (
    val poList:List<PoultryMaterialsPurchaseOrder> = emptyList(),
    val poFilteredList:List<PoultryMaterialsPurchaseOrder> = emptyList(),
    val searchText:String="",
    val loadingStatus: LoadingPoultryPurchaseOrdersListStatus = LoadingPoultryPurchaseOrdersListStatus.LOADING,
    val isMaterialsShowed:Boolean = false,
    val showMaterialsClickedCardIndex:Int = -1
)

sealed class LoadingPoultryPurchaseOrdersListStatus {
    object IDLE : LoadingPoultryPurchaseOrdersListStatus()
    data object LOADING : LoadingPoultryPurchaseOrdersListStatus()
    data class SUCCESS (val ordersList:List<PoultryMaterialsPurchaseOrder>) : LoadingPoultryPurchaseOrdersListStatus()
    data class ERROR(val statusMessage:String) : LoadingPoultryPurchaseOrdersListStatus()
}
