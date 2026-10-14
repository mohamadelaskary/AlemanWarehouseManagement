package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen

import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingFinishedGoodsFeedEvent {
    data class OnScreenLoaded(val orderNumber: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnMaterialCodeInputEntered(val materialCode: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnMaterialSearchTextEntered(val text: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingFinishedGoodsFeedEvent()
    data class OnWarehouseSearchTextChanged(val text: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnWorkersGroupSelected(val workersGroup: WorkersGroup) : ReceivingFinishedGoodsFeedEvent()
    data class OnWorkersGroupSearchTextChanged(val text: String) : ReceivingFinishedGoodsFeedEvent()
    object OnSaveButtonClicked : ReceivingFinishedGoodsFeedEvent()
    object OnClearMaterialButtonClicked : ReceivingFinishedGoodsFeedEvent()
    object HideSuccessDialog : ReceivingFinishedGoodsFeedEvent()
    object HideErrorDialog : ReceivingFinishedGoodsFeedEvent()
    
    object OnChangeOrderButtonClicked : ReceivingFinishedGoodsFeedEvent()
    data class OnOrderSearchTextEntered(val text: String) : ReceivingFinishedGoodsFeedEvent()
    data class OnOrderSelected(val orderNumber: String) : ReceivingFinishedGoodsFeedEvent()
    object OnDismissChangeOrderDialog : ReceivingFinishedGoodsFeedEvent()
}
