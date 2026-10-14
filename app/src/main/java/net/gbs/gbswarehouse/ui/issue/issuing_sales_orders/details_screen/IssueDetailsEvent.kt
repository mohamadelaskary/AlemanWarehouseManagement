package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.details_screen

import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class IssueDetailsEvent {
    data class LoadOrderDetails(val carNo: String) : IssueDetailsEvent()
    
    data class OnBarcodeScanned(val code: String) : IssueDetailsEvent()
    data class OnBinInputChanged(val text: String) : IssueDetailsEvent()
    data class OnScanBin(val binCode: String) : IssueDetailsEvent()
    data object OnClearBin : IssueDetailsEvent()
    
    data class OnStockSearchTextChanged(val text: String) : IssueDetailsEvent()
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssueDetailsEvent()
    
    data class OnMaterialCodeInputChanged(val text: String) : IssueDetailsEvent()
    data class OnMaterialSelected(val item: FinishedGoodsFeedItem) : IssueDetailsEvent()
    data class OnMaterialSearchTextChanged(val text: String) : IssueDetailsEvent()
    data object OnToggleMaterialDialog : IssueDetailsEvent()
    data object OnClearMaterial : IssueDetailsEvent()
    
    data class OnQuantityChanged(val text: String) : IssueDetailsEvent()
    data class OnWorkersGroupSelected(val workersGroup: WorkersGroup) : IssueDetailsEvent()
    data class OnWorkersGroupSearchTextChanged(val text: String) : IssueDetailsEvent()
    
    data object OnAddLine : IssueDetailsEvent()
    data class OnDeleteLine(val index: Int) : IssueDetailsEvent()
    data class OnEditLine(val index: Int) : IssueDetailsEvent()
    
    data object OnSave : IssueDetailsEvent()
    data object OnDismissDialogs : IssueDetailsEvent()
}
