package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_production_line_plastic.details_screen

import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrder
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticLine
import net.gbs.gbswarehouse.domain.model.FinishedGoodsPlasticProductionOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class ReceivingFinishedGoodsPlasticState (
    val orderNumber: String = "",
    val orderGetDataStatus: FinishedGoodsPlasticOrderGetDataStatus = FinishedGoodsPlasticOrderGetDataStatus.IDLE,
    val isWarehousesLoading: Boolean = false,
    val isWorkersGroupsLoading: Boolean = false,
    val productionOrder: FinishedGoodsPlasticProductionOrder? = null,
    val materialCodeInput: String = "",
    val materialSearch: String = "",
    val filteredMaterials: List<FinishedGoodsPlasticProductionOrderItem> = emptyList(),
    val materials: List<FinishedGoodsPlasticProductionOrderItem> = emptyList(),
    val selectedMaterial: FinishedGoodsPlasticProductionOrderItem? = null,
    val warehouses: List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse? = null,
    val workersGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkersGroup: WorkersGroup? = null,
    val quantity: String = "",
    val unloadingPoint: String = "",
    val naolon: String = "",
    val warehouseSearchText: String = "",
    val workersGroupSearchText: String = "",
    val addedItems: List<FinishedGoodsPlasticLine> = emptyList(),
    val materialCodeError: String = "",
    val searchError: String = "",
    val quantityError: String = "",
    val warehouseError: String = "",
    val workersGroupError: String = "",
    val unloadingPointError: String = "",
    val naolonError: String = "",
    val error: String = "",
    val savingSuccessStatusMessage: String = "",
    val savingErrorStatusMessage: String = "",
    val isSavingLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    
    val allOrders: List<FinishedGoodsPlasticProductionOrder> = emptyList(),
    val filteredOrders: List<FinishedGoodsPlasticProductionOrder> = emptyList(),
    val orderSearch: String = "",
    val showChangeOrderDialog: Boolean = false,
    val isAllOrdersLoading: Boolean = false,
    val isReadyToReceive: Boolean = false,
    val palletWeight: String = "",
)

sealed class FinishedGoodsPlasticOrderGetDataStatus {
    data object IDLE : FinishedGoodsPlasticOrderGetDataStatus()
    data object LOADING : FinishedGoodsPlasticOrderGetDataStatus()
    data class SUCCESS(val order: FinishedGoodsPlasticProductionOrder) : FinishedGoodsPlasticOrderGetDataStatus()
    data class ERROR(val error: String) : FinishedGoodsPlasticOrderGetDataStatus()
}
