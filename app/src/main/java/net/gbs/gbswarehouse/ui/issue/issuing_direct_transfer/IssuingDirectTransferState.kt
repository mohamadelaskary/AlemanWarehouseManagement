package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

import net.gbs.gbswarehouse.data.remote.dto.response.DirectTransferLineDto
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class GroupedMaterial(
    val materialCode: String,
    val materialName: String,
    val stocks: List<MaterialOnHandStock>
)

data class IssuingDirectTransferState(
    val isLoading: Boolean = false,
    val isBinFromLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,

    val binFromInput: String = "",
    val binFromData: StorageBinLocation? = null,
    val binFromError: Message? = null,

    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),
    val groupedMaterials: List<GroupedMaterial> = emptyList(),
    val materialSearchText: String = "",
    val selectedMaterial: GroupedMaterial? = null,
    val matErrorMessage: Message? = null,

    val selectedOnHandStock: MaterialOnHandStock? = null,
    val stockSearchText: String = "",
    val availableQty: Double = 0.0,
    val maxQty: Double = Double.MAX_VALUE,
    val stockError: Message? = null,

    val productionWarehouses: List<SAPWarehouse> = emptyList(),
    val selectedProductionWarehouse: SAPWarehouse? = null,
    val warehouseSearch: String = "",

    val workersGroupsList: List<WorkersGroup> = emptyList(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val workersGroupSearch: String = "",
    val workersGroupError: Message? = null,

    val selectedAgingGroup: WorkersGroup? = null,
    val agingGroupSearch: String = "",
    val agingGroupError: Message? = null,

    val quantity: String = "",
    val quantityError: Message? = null,

    val addedLines: List<DirectTransferLineDto> = emptyList(),

    val savingSuccessMessage: Message? = null,
    val savingErrorMessage: Message? = null
)
