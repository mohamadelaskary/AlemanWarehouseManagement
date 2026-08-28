package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.orders_list

import net.gbs.gbswarehouse.domain.model.MovementType
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder

data class ReceivingSparePartsFromProductionOrdersListState(
    val movementTypes: List<MovementType> = emptyList(),
    val selectedMovementType: MovementType? = null,
    val orders: List<SparePartsReturnReservationOrder> = emptyList(),
    val filteredOrders: List<SparePartsReturnReservationOrder> = emptyList(),
    val searchText: String = "",
    val isLoadingMovementTypes: Boolean = false,
    val isLoadingOrders: Boolean = false,
    val error: String = "",
    val movementTypeSearchText:String = "",
)
