package net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.details_screen

import net.gbs.gbswarehouse.data.remote.dto.request.IssueTransferOrderLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.TransferOrderItem
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class IssuingTransferPlantsDetailsEvent {
    data class LoadOrderDetails(val carNo: String) : IssuingTransferPlantsDetailsEvent()
    data class OnBarcodeScanned(val barcode: String) : IssuingTransferPlantsDetailsEvent()
    data class OnBinInputChanged(val text: String) : IssuingTransferPlantsDetailsEvent()
    data class OnScanBin(val binCode: String) : IssuingTransferPlantsDetailsEvent()
    object OnClearBin : IssuingTransferPlantsDetailsEvent()

    data class OnMaterialSearchTextChanged(val text: String) : IssuingTransferPlantsDetailsEvent()
    data class OnItemSelected(val item: TransferOrderItem) : IssuingTransferPlantsDetailsEvent()
    object OnClearItem : IssuingTransferPlantsDetailsEvent()
    object OnToggleItemDialog : IssuingTransferPlantsDetailsEvent()

    data class OnStockSearchTextChanged(val text: String) : IssuingTransferPlantsDetailsEvent()
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssuingTransferPlantsDetailsEvent()

    data class OnDestinationChanged(val text: String) : IssuingTransferPlantsDetailsEvent()
    data class OnWorkersGroupSelected(val workersGroup: WorkersGroup) : IssuingTransferPlantsDetailsEvent()
    data class OnWorkersGroupSearchTextChanged(val text: String) : IssuingTransferPlantsDetailsEvent()

    data class OnQtyChanged(val text: String) : IssuingTransferPlantsDetailsEvent()
    object OnAddLine : IssuingTransferPlantsDetailsEvent()
    object OnIssueAll : IssuingTransferPlantsDetailsEvent()
    object OnDismissError : IssuingTransferPlantsDetailsEvent()
    data class OnDeleteLine(val materialCode: String) : IssuingTransferPlantsDetailsEvent()
    data class OnEditLine(val addedLine: IssueTransferOrderLineDto) : IssuingTransferPlantsDetailsEvent()
    object OnDismissSuccess : IssuingTransferPlantsDetailsEvent()
}
