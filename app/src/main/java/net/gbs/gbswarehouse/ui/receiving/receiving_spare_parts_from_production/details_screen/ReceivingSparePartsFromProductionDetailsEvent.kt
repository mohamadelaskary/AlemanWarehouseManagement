package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.details_screen

import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingSparePartsFromProductionDetailsEvent {
    data class OnScreenLoaded(val orderData: String,val returnReason:String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnMaterialCodeInputEntered(val barcode: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnMaterialSearchTextChanged(val text: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnWarehouseSearchTextChanged(val text: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnValuationSearchTextChanged(val text: String) : ReceivingSparePartsFromProductionDetailsEvent()

    data class OnNotesEntered(val notes: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnGoodsRecipientEntered(val name: String) : ReceivingSparePartsFromProductionDetailsEvent()
    data object OnAddButtonClicked : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnDeleteAddedItemClicked(val index: Int) : ReceivingSparePartsFromProductionDetailsEvent()
    data class OnEditAddedItemClicked(val index: Int) : ReceivingSparePartsFromProductionDetailsEvent()
    data object OnSaveButtonClicked : ReceivingSparePartsFromProductionDetailsEvent()
    data object OnClearMaterialButtonClicked : ReceivingSparePartsFromProductionDetailsEvent()
    data object HideSuccessDialog : ReceivingSparePartsFromProductionDetailsEvent()
    data object HideErrorDialog : ReceivingSparePartsFromProductionDetailsEvent()

    data class OnValuationTypeSelected(val valuationType: ValuationType) : ReceivingSparePartsFromProductionDetailsEvent()
}
