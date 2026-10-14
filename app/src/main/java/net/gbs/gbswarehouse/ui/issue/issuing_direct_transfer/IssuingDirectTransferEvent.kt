package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class IssuingDirectTransferEvent {
    data class OnBinFromInputChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnScanBinFrom(val binCode: String) : IssuingDirectTransferEvent()
    object OnClearBinFrom : IssuingDirectTransferEvent()

    data class OnMaterialSearchTextChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnMaterialSelected(val material: GroupedMaterial) : IssuingDirectTransferEvent()
    data class OnScanMaterial(val matCode: String) : IssuingDirectTransferEvent()
    object OnClearMaterial : IssuingDirectTransferEvent()

    data class OnStockSearchTextChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnStockSelected(val stock: MaterialOnHandStock) : IssuingDirectTransferEvent()

    data class OnProductionWarehouseSelected(val warehouse: SAPWarehouse) : IssuingDirectTransferEvent()
    data class OnWarehouseSearchTextChanged(val text: String) : IssuingDirectTransferEvent()

    data class OnWorkersGroupSelected(val workerGroup: WorkersGroup) : IssuingDirectTransferEvent()
    data class OnWorkersGroupTextChange(val text: String) : IssuingDirectTransferEvent()
    data class OnAgingGroupSelected(val agingGroup: WorkersGroup) : IssuingDirectTransferEvent()
    data class OnAgingGroupTextChange(val text: String) : IssuingDirectTransferEvent()

    data class OnQuantityChanged(val text: String) : IssuingDirectTransferEvent()

    object OnAddLine : IssuingDirectTransferEvent()
    object OnIssueAll : IssuingDirectTransferEvent()

    data class OnDeleteLine(val index: Int) : IssuingDirectTransferEvent()
    data class OnEditLine(val index: Int) : IssuingDirectTransferEvent()

    object OnDismissDialogs : IssuingDirectTransferEvent()

    data class OnBarcodeScanned(val barcode: String) : IssuingDirectTransferEvent()
}
