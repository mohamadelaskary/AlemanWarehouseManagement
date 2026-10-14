package net.gbs.gbswarehouse.ui.issue.issuing_transfer_plants.details_screen

import net.gbs.gbswarehouse.data.remote.dto.request.IssueTransferOrderLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferOrder
import net.gbs.gbswarehouse.domain.model.TransferOrderItem
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class IssuingTransferPlantsDetailsState(
    val isLoading: Boolean = false,
    val loadingErrorMessage: Message? = null,
    val carNo: String = "",
    val transferOrder: TransferOrder? = null,
    val orderItems: List<TransferOrderItem> = emptyList(),
    val filteredItems: List<TransferOrderItem> = emptyList(),
    val selItem: TransferOrderItem? = null,
    val matInput: String = "",
    val matSearchText: String = "",
    val matErrorMessage: Message? = null,
    val matDlg: Boolean = false,

    val binInput: String = "",
    val isBinLoading: Boolean = false,
    val selBinData: StorageBinLocation? = null,
    val binCodeError: Message? = null,

    val isStockLoading: Boolean = false,
    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),
    val selectedOnHandStock: MaterialOnHandStock? = null,
    val stockSearch: String = "",
    val stockError: Message? = null,
    val availableStock: Double = 0.0,
    val maxQty: Double = 0.0,

    val destination: String = "",
    val destinationError: Message? = null,
    val workersGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val workersGroupSearchText: String = "",
    val workersGroupError: Message? = null,

    val qty: String = "",
    val qtyError: Message? = null,

    val addedLines: List<IssueTransferOrderLineDto> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val savingErrorMessage: Message? = null,
    val savingSuccessMessage: Message? = null
)
