package net.gbs.gbswarehouse.ui.issue.issuing_internal_orders.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.IssueToInternalOrderLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine

data class IssueDetailsState(
    val reservationLines: List<SparePartsReturnReservationLine> = emptyList(),
    val filteredMaterials: List<SparePartsReturnReservationLine> = emptyList(),
    val addedLines: List<IssueToInternalOrderLineDto> = emptyList(),
    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),
    val reservationNumber: String = "",

    val isLoading: Boolean = false,
    val isBinLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,

    val saveKind: String? = null,

    val binInput: String = "",
    val selBinData: StorageBinLocation? = null,
    val binCodeError: Message? = null,

    val matInput: String = "",
    val matSearchText: String = "",
    val matErrorMessage : Message? = null,
    val selReservationLine: SparePartsReturnReservationLine? = null,
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
    val savingSuccessMessage : Message? = null,
)
