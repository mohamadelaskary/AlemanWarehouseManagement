package net.gbs.gbswarehouse.ui.issue.issuing_scrap

import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.MaterialBinLocation
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock

sealed class IssueScrapEvent {
    data class OnDocumentHeaderChanged(val text: String) : IssueScrapEvent()

    data class OnMatInputChanged(val text: String) : IssueScrapEvent()
    data class OnScanMaterial(val matCode: String) : IssueScrapEvent()
    data class OnMaterialSelected(val stock: SAPMaterialBatchValuationStock) : IssueScrapEvent()
    object OnClearMaterial : IssueScrapEvent()
    object OnToggleMaterialDialog : IssueScrapEvent()
    data class OnMaterialSearchTextChanged(val text: String) : IssueScrapEvent()

    data class OnBinInputChanged(val text: String) : IssueScrapEvent()
    data class OnScanBin(val binCode: String) : IssueScrapEvent()
    object OnClearBin : IssueScrapEvent()
    data class OnBinLocationSelected(val binLocation: MaterialBinLocation) : IssueScrapEvent()
    data class OnBinSearchTextChanged(val text: String) : IssueScrapEvent()

    data class OnStockSearchTextChanged(val text: String) : IssueScrapEvent()
    data class OnOnHandStockSelected(val stock: MaterialOnHandStock) : IssueScrapEvent()

    data class OnCompanySelected(val company: Company) : IssueScrapEvent()
    data class OnCompanySearchTextChanged(val text: String) : IssueScrapEvent()

    data class OnCostCenterSelected(val costCenter: CostCenter) : IssueScrapEvent()
    data class OnCostCenterSearchTextChanged(val text: String) : IssueScrapEvent()

    data class OnNotesChanged(val text: String) : IssueScrapEvent()

    data class OnQuantityChanged(val text: String) : IssueScrapEvent()

    object OnAddLine : IssueScrapEvent()
    object OnIssueAll : IssueScrapEvent()
    
    data class OnDeleteLine(val index: Int) : IssueScrapEvent()
    data class OnEditLine(val index: Int) : IssueScrapEvent()
    object OnDismissDialogs : IssueScrapEvent()

    data class OnBarcodeScanned(val barcode: String) : IssueScrapEvent()
}
