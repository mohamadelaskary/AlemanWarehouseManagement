package net.gbs.gbswarehouse.ui.receiving.receiving_spare_parts_from_production.details_screen

import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.SparePartsReturnLine
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationOrder
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse

data class ReceivingSparePartsFromProductionDetailsState(
    val orderData: SparePartsReturnReservationOrder? = null,
    val materials: List<SparePartsReturnReservationLine> = emptyList(),
    val filteredMaterials: List<SparePartsReturnReservationLine> = emptyList(),
    val selectedMaterial: SparePartsReturnReservationLine? = null,

    val warehouses: List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse? = null,

    val quantity: String = "",

    val materialCodeInput: String = "",
    val materialSearchText: String = "",
    val warehouseSearchText: String = "",
    val valuationSearchText: String = "",

    val addedItems: List<SparePartsReturnLine> = emptyList(),

    val isMaterialsLoading: Boolean = false,
    val isWarehousesLoading: Boolean = false,
    val isSaving: Boolean = false,

    val materialCodeError: Message? = null,
    val quantityError: Message? = null,
    val warehouseError: Message? = null,
    val goodsRecipientError: Message? = null,
    val error: Message? = null,
    val successMessage: Message? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,

    val selectedValuationType: ValuationType? = null,
    val valuationTypeError: Message? = null,
    val returnReason: String?= null,
    val recipientName: String = "",
    val recipientNameError: Message? = null,
    val itemText: String? = null,
    val itemTextError: Message? = null,
)

data class SparePartsReturnReservationLineWithDetails(
    val line: SparePartsReturnLine,
    val warehouse: Warehouse,
    val quantity: Double,
    val notes: String?,
    val recipientName: String,
    val valuationType: String? = null
)
