package net.gbs.gbswarehouse.ui.issue.issuing_cost_center.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.DirectIssueOnCostCenterLineDto
import net.gbs.gbswarehouse.domain.model.DirectIssueReservationLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation

data class IssueDetailsState(
    val reservationLines: List<DirectIssueReservationLine> = emptyList(),
    val filteredMaterials: List<DirectIssueReservationLine> = emptyList(),
    val addedLines: List<DirectIssueOnCostCenterLineDto> = emptyList(),
    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),

    val isLoading: Boolean = false,
    val isBinLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,

    val binInput: String = "",
    val selBinData: StorageBinLocation? = null,
    val binCodeError: Message? = null,

    val matInput: String = "",
    val matSearchText: String = "",
    val matErrorMessage : Message? = null,
    val selReservationLine: DirectIssueReservationLine? = null,
    val matDlg: Boolean = false,

    val selectedOnHandStock: MaterialOnHandStock? = null,
    val stockSearch: String = "",
    val stockError : Message? = null,
    val availableStock: Double = 0.0,

    val qty: String = "",
    val qtyError: Message? = null,
    val maxQty: Double = 0.0,

    val loadingErrorMessage: Message? = null,
    val savingErrorMessage: Message? = null,
    val savingSuccessMessage :Message? = null,
)
