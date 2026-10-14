package net.gbs.gbswarehouse.ui.issue.issuing_sales_orders.details_screen

import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class IssueDetailsState(
    val order: FinishedGoodsFeedOrder? = null,
    val isLoading: Boolean = false,
    val error: Message? = null,
    
    val binInput: String = "",
    val binData: StorageBinLocation? = null,
    val isBinLoading: Boolean = false,
    val binCodeError: Message? = null,
    
    val stockOnHandList: List<MaterialOnHandStock> = emptyList(),
    val selectedOnHandStock: MaterialOnHandStock? = null,
    val isStockLoading: Boolean = false,
    val stockError: Message? = null,
    val stockSearch: String = "",
    val availableStock: Double = 0.0,
    val maxQty: Double = 0.0,
    
    val materialCodeInput: String = "",
    val selectedMaterial: FinishedGoodsFeedItem? = null,
    val materialSearchText: String = "",
    val filteredMaterials: List<FinishedGoodsFeedItem> = emptyList(),
    val showMaterialDialog: Boolean = false,
    
    val quantity: String = "",
    val quantityError: Message? = null,
    
    val workersGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val isWorkersGroupsLoading: Boolean = false,
    val workersGroupSearchText: String = "",
    
    val addedItems: List<FinishedGoodsFeedLine> = emptyList(),
    
    val isSaving: Boolean = false,
    val savingSuccess: Boolean = false,
    val savingError: Message? = null,
    val successMessage: Message? = null,
) {

}
