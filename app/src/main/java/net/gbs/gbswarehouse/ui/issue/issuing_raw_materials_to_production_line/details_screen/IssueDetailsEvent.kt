package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.TransferToProductionLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.TransferToProductionReservationLine
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class IssueDetailsEvent {
    data class LoadOrderDetails(val reservationNumber: String) : IssueDetailsEvent()
    data class OnBarcodeScanned(val barcode: String) : IssueDetailsEvent()
    data class OnBinInputChanged(val text: String) : IssueDetailsEvent()
    data class OnScanBin(val binCode: String) : IssueDetailsEvent()
    object OnClearBin : IssueDetailsEvent()
    
    data class OnMaterialSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnMaterialSelected(val line: TransferToProductionReservationLine) : IssueDetailsEvent()
    object OnClearMaterial : IssueDetailsEvent()
    object OnToggleMaterialDialog : IssueDetailsEvent()
    
    data class OnStockSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssueDetailsEvent()
    
    data class OnWarehouseSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnWarehouseSelected(val warehouse: SAPWarehouse) : IssueDetailsEvent()
    
    data class OnQtyChanged(val text: String) : IssueDetailsEvent()
    data class OnNotesChanged(val notes: String) : IssueDetailsEvent()
    data class OnDeleteLine(val materialCode: String) : IssueDetailsEvent()
    data class OnEditLine(val addedLine: TransferToProductionLineDto) : IssueDetailsEvent()
    data class OnWorkersGroupSelected(val it: WorkersGroup): IssueDetailsEvent()
    data class OnWorkersGroupTextChange(val it: String): IssueDetailsEvent()
    data class OnAgingGroupSelected(val it: WorkersGroup): IssueDetailsEvent()
    data class OnAgingGroupTextChange(val it: String): IssueDetailsEvent()

    object OnAddLine : IssueDetailsEvent()
    object OnIssueAll : IssueDetailsEvent()
    object OnDismissError : IssueDetailsEvent()
    object OnDismissSuccess : IssueDetailsEvent()
}
