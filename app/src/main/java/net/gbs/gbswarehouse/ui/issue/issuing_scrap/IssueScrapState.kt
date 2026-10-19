package net.gbs.gbswarehouse.ui.issue.issuing_scrap

import net.gbs.gbswarehouse.data.remote.dto.response.ScrapIssueLineDto
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.model.CostCenter
import net.gbs.gbswarehouse.domain.model.MaterialBinLocation
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.ScrapIssueLine
import net.gbs.gbswarehouse.domain.model.StorageBinLocation

data class IssueScrapState(
    val isLoading: Boolean = false,
    val isCompaniesLoading: Boolean = false,
    val isCostCentersLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isBinLoading: Boolean = false,
    val isBinLocationsLoading: Boolean = false,

    val documentHeaderText: String = "",
    val documentHeaderError: Message? = null,

    val eligibleStock: List<SAPMaterialBatchValuationStock> = emptyList(),
    val selectedMatStock: SAPMaterialBatchValuationStock? = null,
    val matInput: String = "",
    val matErrorMessage: Message? = null,
    val matDlg: Boolean = false,
    val matSearchText: String = "",

    val binInput: String = "",
    val selBinData: StorageBinLocation? = null,
    val binCodeError: Message? = null,
    val binLocations: List<MaterialBinLocation> = emptyList(),
    val selectedBinLocation: MaterialBinLocation? = null,
    val binSearchText: String = "",

    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),
    val selectedOnHandStock: MaterialOnHandStock? = null,
    val availableStock: Double = 0.0,
    val maxQty: Double = 0.0,
    val stockSearch: String = "",
    val stockError: Message? = null,

    val companies: List<Company> = emptyList(),
    val selectedCompany: Company? = null,
    val companySearchText: String = "",

    val costCenters: List<CostCenter> = emptyList(),
    val selectedCostCenter: CostCenter? = null,
    val costCenterSearchText: String = "",
    val costCenterErrorMessage: Message? = null,

    val notes: String? = null,

    val quantity: String = "",
    val quantityError: Message? = null,

    val addedLines: List<ScrapIssueLine> = emptyList(),

    val savingSuccessMessage: Message? = null,
    val savingErrorMessage: Message? = null,
)
