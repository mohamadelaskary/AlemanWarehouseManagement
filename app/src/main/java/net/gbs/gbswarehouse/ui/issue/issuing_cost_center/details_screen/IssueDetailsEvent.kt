package net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.DirectIssueOnCostCenterLineDto
import net.gbs.gbswarehouse.domain.model.DirectIssueReservationLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock

sealed class IssueDetailsEvent {
    data class LoadOrderDetails(val reservationNumber: String) : IssueDetailsEvent()
    data class OnBarcodeScanned(val barcode: String) : IssueDetailsEvent()
    data class OnBinInputChanged(val text: String) : IssueDetailsEvent()
    data class OnScanBin(val binCode: String) : IssueDetailsEvent()
    object OnClearBin : IssueDetailsEvent()
    
    data class OnMaterialSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnMaterialSelected(val line: DirectIssueReservationLine) : IssueDetailsEvent()
    object OnClearMaterial : IssueDetailsEvent()
    object OnToggleMaterialDialog : IssueDetailsEvent()
    
    data class OnStockSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssueDetailsEvent()
    
    data class OnQtyChanged(val text: String) : IssueDetailsEvent()
    data class OnDeleteLine(val materialCode: String) : IssueDetailsEvent()
    data class OnEditLine(val addedLine: DirectIssueOnCostCenterLineDto) : IssueDetailsEvent()

    object OnAddLine : IssueDetailsEvent()
    object OnIssueAll : IssueDetailsEvent()
    object OnDismissError : IssueDetailsEvent()
    object OnDismissSuccess : IssueDetailsEvent()
}
