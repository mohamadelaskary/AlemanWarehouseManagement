package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line.details_screen

import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class ReceivingFinishedGoodsFeedState (
    val orderNumber: String = "",
    val orderGetDataStatus: FinishedGoodsFeedOrderGetDataStatus = FinishedGoodsFeedOrderGetDataStatus.IDLE,
    val isWarehousesLoading: Boolean = false,
    val isWorkersGroupsLoading: Boolean = false,
    val productionOrder: FinishedGoodsFeedProductionOrder? = null,
    val materialCodeInput: String = "",
    val materialSearch: String = "",
    val filteredMaterials: List<FinishedGoodsFeedProductionOrderItem> = emptyList(),
    val materials: List<FinishedGoodsFeedProductionOrderItem> = emptyList(),
    val selectedMaterial: FinishedGoodsFeedProductionOrderItem? = null,
    val warehouses: List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse? = null,
    val workersGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val quantity: String = "",
    val warehouseSearchText: String = "",
    val workersGroupSearchText: String = "",
    val addedItems: List<FinishedGoodsFeedLine> = emptyList(),
    val materialCodeError: String = "",
    val searchError: String = "",
    val quantityError: String = "",
    val warehouseError: String = "",
    val workersGroupError: String = "",
    val error: String = "",
    val savingSuccessStatusMessage: String = "",
    val savingErrorStatusMessage: String = "",
    val isSavingLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    
    val allOrders: List<FinishedGoodsFeedProductionOrder> = emptyList(),
    val filteredOrders: List<FinishedGoodsFeedProductionOrder> = emptyList(),
    val orderSearch: String = "",
    val showChangeOrderDialog: Boolean = false,
    val isAllOrdersLoading: Boolean = false,
    val isReadyToReceive: Boolean = false
)

sealed class FinishedGoodsFeedOrderGetDataStatus {
    data object IDLE : FinishedGoodsFeedOrderGetDataStatus()
    data object LOADING : FinishedGoodsFeedOrderGetDataStatus()
    data class SUCCESS(val order: FinishedGoodsFeedProductionOrder) : FinishedGoodsFeedOrderGetDataStatus()
    data class ERROR(val error: String) : FinishedGoodsFeedOrderGetDataStatus()
}
