package net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.details_screen

import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine

sealed class IssueDetailsEvent {
    data class LoadOrderDetails(val reservationNumber: String) : IssueDetailsEvent()
    data class OnBarcodeScanned(val barcode: String) : IssueDetailsEvent()
    data class OnBinInputChanged(val text: String) : IssueDetailsEvent()
    data class OnScanBin(val binCode: String) : IssueDetailsEvent()
    object OnClearBin : IssueDetailsEvent()
    
    data class OnMaterialSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnMaterialSelected(val line: SparePartsReturnReservationLine) : IssueDetailsEvent()
    object OnClearMaterial : IssueDetailsEvent()
    object OnToggleMaterialDialog : IssueDetailsEvent()
    
    data class OnStockSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnStockSelected(val stock: SAPMaterialBatchValuationStock) : IssueDetailsEvent()
    
    data class OnQtyChanged(val text: String) : IssueDetailsEvent()
    data class OnDeleteLine(val materialCode: String) : IssueDetailsEvent()

    object OnAddLine : IssueDetailsEvent()
    object OnIssueAll : IssueDetailsEvent()
    object OnDismissError : IssueDetailsEvent()
    object OnDismissSuccess : IssueDetailsEvent()
}
