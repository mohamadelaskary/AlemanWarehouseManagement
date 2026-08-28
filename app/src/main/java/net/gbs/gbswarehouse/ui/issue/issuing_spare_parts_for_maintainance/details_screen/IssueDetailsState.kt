package net.gbs.gbswarehouse.ui.issue.issuing_spare_parts_for_maintainance.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsIssueToMaintenanceLineDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine

data class IssueDetailsState(
    val reservationLines: List<SparePartsReturnReservationLine> = emptyList(),
    val filteredMaterials: List<SparePartsReturnReservationLine> = emptyList(),
    val addedLines: List<SparePartsIssueToMaintenanceLineDto> = emptyList(),
    val warehousesList: List<SAPWarehouse> = emptyList(),
    val stockList: List<SAPMaterialBatchValuationStock> = emptyList(),

    val isLoading: Boolean = false,
    val isBinLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,

    val saveKind: String? = null,

    val binInput: String = "",
    val selBinData: StorageBinLocation? = null,
    val binCodeError: String? = null,

    val matInput: String = "",
    val matSearchText: String = "",
    val matErrorMessage : String? = null,
    val selReservationLine: SparePartsReturnReservationLine? = null,
    val matDlg: Boolean = false,

    val selectedStock: SAPMaterialBatchValuationStock? = null,
    val stockSearch: String = "",
    val stockError :String = "",

    val selectedWarehouse: SAPWarehouse? = null,
    val warehouseSearch: String = "",
    val warehouseError: String = "",

    val qty: String = "",
    val qtyError: String? = null,
    val maxQty: Double = 0.0,

    val loadingErrorMessage: String? = null,
    val savingErrorMessage: String? = null,
    val savingSuccessMessage :String? = null,

)