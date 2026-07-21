package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_vendor.details_screen

import net.gbs.gbswarehouse.domain.model.PurchaseOrder
import net.gbs.gbswarehouse.domain.model.SparePartsLine
import net.gbs.gbswarehouse.domain.model.SparePartsMaterial
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed interface ReceivingSparePartsFromVendorEvent {
    data class OnPurchaseOrderNumberEntered(
        val orderNumber: String
    ) : ReceivingSparePartsFromVendorEvent
    data object OnSearchButtonClicked : ReceivingSparePartsFromVendorEvent
    data object OnClearButtonClicked : ReceivingSparePartsFromVendorEvent
    data class OnMaterialSelected(
        val materialCode: String
    ) : ReceivingSparePartsFromVendorEvent
    data class OnWarehouseSelected(
        val warehouse: Warehouse
    ) : ReceivingSparePartsFromVendorEvent
    data class OnValuationTypeSelected(
        val valuationType: ValuationType
    ) : ReceivingSparePartsFromVendorEvent
    data class OnExpiryDateSelected (
        val expiryDate: String
    ) : ReceivingSparePartsFromVendorEvent
    data class OnQuantityEntered(
        val quantity: String
    ) : ReceivingSparePartsFromVendorEvent

    data class OnMaterialSearchTextEntered (
        val text: String
    ) : ReceivingSparePartsFromVendorEvent
    data object OnAddButtonClicked : ReceivingSparePartsFromVendorEvent
    data object OnSaveButtonClicked: ReceivingSparePartsFromVendorEvent

    data object OnClearMaterialButtonClicked : ReceivingSparePartsFromVendorEvent
    data object ShowSuccessDialog : ReceivingSparePartsFromVendorEvent
    data object HideSuccessDialog : ReceivingSparePartsFromVendorEvent
    data object ShowErrorDialog : ReceivingSparePartsFromVendorEvent
    data object HideErrorDialog : ReceivingSparePartsFromVendorEvent
}
