package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.TransferToProductionLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferToProductionReservationLine
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class IssueDetailsState(
    val reservationNumber: String = "",
    val reservationLines: List<TransferToProductionReservationLine> = emptyList(),
    val filteredMaterials: List<TransferToProductionReservationLine> = emptyList(),
    val addedLines: List<TransferToProductionLineDto> = emptyList(),
    val warehousesList: List<SAPWarehouse> = emptyList(),
    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),

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
    val selReservationLine: TransferToProductionReservationLine? = null,
    val matDlg: Boolean = false,

    val selectedOnHandStock: MaterialOnHandStock? = null,
    val stockSearch: String = "",
    val stockError : Message? = null,
    val availableStock: Double = 0.0,

    val selectedWarehouse: SAPWarehouse? = null,
    val warehouseSearch: String = "",
    val warehouseError: Message? = null,

    val qty: String = "",
    val qtyError: Message? = null,
    val maxQty: Double = 0.0,

    val loadingErrorMessage: Message? = null,
    val savingErrorMessage: Message? = null,
    val savingSuccessMessage : Message? = null,
    val workersGroupsList: List<WorkersGroup> = listOf(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val workersGroupSearch: String = "",
    val workersGroupError: Message? = null,
    val agingGroupsList: List<WorkersGroup> = listOf(),
    val selectedAgingGroup: WorkersGroup? = null,
    val agingGroupSearch: String = "",
    val agingGroupError: Message? = null,
    val notes:String? = null,
)

