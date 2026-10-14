package net.gbs.gbswarehouse.ui.issue.issuing_spare_parts_for_maintainance.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsIssueToMaintenanceLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
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
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssueDetailsEvent()
    
    data class OnWarehouseSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnWarehouseSelected(val warehouse: SAPWarehouse) : IssueDetailsEvent()
    
    data class OnQtyChanged(val text: String) : IssueDetailsEvent()
    data class OnDeleteLine(val materialCode: String) : IssueDetailsEvent()
    data class OnEditLine(val addedLine: SparePartsIssueToMaintenanceLineDto) : IssueDetailsEvent()

    object OnAddLine : IssueDetailsEvent()
    object OnIssueAll : IssueDetailsEvent()
    object OnDismissError : IssueDetailsEvent()
    object OnDismissSuccess : IssueDetailsEvent()
}
