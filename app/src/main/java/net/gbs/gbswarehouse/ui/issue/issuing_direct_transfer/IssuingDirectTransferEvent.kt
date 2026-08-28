package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.DirectTransferWarehouseToType

sealed class IssuingDirectTransferEvent {
    data class OnBinFromInputChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnScanBinFrom(val binCode: String) : IssuingDirectTransferEvent()
    object OnClearBinFrom : IssuingDirectTransferEvent()

    data class OnMatInputChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnScanMaterial(val matCode: String) : IssuingDirectTransferEvent()
    object OnClearMaterial : IssuingDirectTransferEvent()
    
    data class OnWarehouseTypeSelected(val type: DirectTransferWarehouseToType) : IssuingDirectTransferEvent()
    
    data class OnBinToInputChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnScanBinTo(val binCode: String) : IssuingDirectTransferEvent()
    object OnClearBinTo : IssuingDirectTransferEvent()
    
    data class OnProductionWarehouseSelected(val warehouse: SAPWarehouse) : IssuingDirectTransferEvent()
    
    data class OnStockSearchTextChanged(val text: String) : IssuingDirectTransferEvent()
    data class OnStockSelected(val stock: SAPMaterialBatchValuationStock) : IssuingDirectTransferEvent()
    
    data class OnQuantityChanged(val text: String) : IssuingDirectTransferEvent()
    
    object OnAddLine : IssuingDirectTransferEvent()
    object OnIssueAll : IssuingDirectTransferEvent()
    
    data class OnDeleteLine(val index: Int) : IssuingDirectTransferEvent()
    
    object OnDismissDialogs : IssuingDirectTransferEvent()

    data class OnBarcodeScanned(val barcode: String) : IssuingDirectTransferEvent()
}
