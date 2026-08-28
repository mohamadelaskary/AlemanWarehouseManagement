package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.orders_list

import net.gbs.gbswarehouse.domain.model.MovementType

sealed class ReceivingSparePartsFromProductionOrdersListEvent {
    data object OnScreenLoaded : ReceivingSparePartsFromProductionOrdersListEvent()
    data class OnMovementTypeSelected(val movementType: MovementType) : ReceivingSparePartsFromProductionOrdersListEvent()
    data class OnMovementTypeSearchTextChanged(val text: String) : ReceivingSparePartsFromProductionOrdersListEvent()
    data class OnSearchTextChanged(val text: String) : ReceivingSparePartsFromProductionOrdersListEvent()
    data object OnRefreshMovementTypes : ReceivingSparePartsFromProductionOrdersListEvent()
}
