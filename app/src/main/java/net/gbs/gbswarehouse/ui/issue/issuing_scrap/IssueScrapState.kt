package net.gbs.gbswarehouse.ui.issue.issuing_scrap

import net.gbs.gbswarehouse.data.remote.dto.response.ScrapIssueLineDto
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.ScrapIssueLine
import net.gbs.gbswarehouse.domain.model.StorageBinLocation

data class IssueScrapState(
    val isLoading: Boolean = false,
    val isCompaniesLoading: Boolean = false,
    val isCostCentersLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isBinLoading: Boolean = false,

    val documentHeaderText: String = "",
    val documentHeaderError: String? = null,

    val binInput: String = "",
    val binData: StorageBinLocation? = null,
    val binErrorMessage: String? = null,

    val matInput: String = "",
    val eligibleStock: List<SAPMaterialBatchValuationStock> = emptyList(),
    val selectedMatStock: SAPMaterialBatchValuationStock? = null,
    val matErrorMessage: String? = null,
    val matDlg: Boolean = false,
    val matSearchText: String = "",

    val companies: List<Company> = emptyList(),
    val selectedCompany: Company? = null,
    val companySearchText: String = "",

    val costCenters: List<CostCenter> = emptyList(),
    val selectedCostCenter: CostCenter? = null,
    val costCenterSearchText: String = "",
    val costCenterErrorMessage: String = "",

    val notes: String? = null,

//    val warehouses: List<SAPWarehouse> = emptyList(),
//    val selectedWarehouse: SAPWarehouse? = null,
//    val warehouseSearchText: String = "",

    val quantity: String = "",
    val quantityError: String? = null,

    val addedLines: List<ScrapIssueLine> = emptyList(),

    val savingSuccessMessage: String? = null,
    val savingErrorMessage: String? = null,

    val binCode: String = "", // Added for consistency with previous issue screens if needed
)
