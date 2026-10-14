package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen

import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingFinishedGoodsPlasticEvent {
    data class OnScreenLoaded(val orderNumber: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnMaterialCodeInputEntered(val materialCode: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnMaterialSearchTextEntered(val text: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingFinishedGoodsPlasticEvent()
    data class OnWarehouseSearchTextChanged(val text: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnWorkersGroupSelected(val workersGroup: Int) : ReceivingFinishedGoodsPlasticEvent()
    data class OnWorkersGroupSearchTextChanged(val text: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnUnloadingPointEntered(val unloadingPoint: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnNaolonEntered(val naolon: String) : ReceivingFinishedGoodsPlasticEvent()
    object OnSaveButtonClicked : ReceivingFinishedGoodsPlasticEvent()
    object OnClearMaterialButtonClicked : ReceivingFinishedGoodsPlasticEvent()
    object HideSuccessDialog : ReceivingFinishedGoodsPlasticEvent()
    object HideErrorDialog : ReceivingFinishedGoodsPlasticEvent()
    
    object OnChangeOrderButtonClicked : ReceivingFinishedGoodsPlasticEvent()
    data class OnOrderSearchTextEntered(val text: String) : ReceivingFinishedGoodsPlasticEvent()
    data class OnOrderSelected(val orderNumber: String) : ReceivingFinishedGoodsPlasticEvent()
    object OnDismissChangeOrderDialog : ReceivingFinishedGoodsPlasticEvent()

    data class OnPalletWeightEntered(val palletWeight: String) : ReceivingFinishedGoodsPlasticEvent()
}
