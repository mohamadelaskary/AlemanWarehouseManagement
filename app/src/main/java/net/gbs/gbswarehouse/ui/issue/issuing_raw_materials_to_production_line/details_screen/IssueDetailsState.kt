package net.gbs.gbswarehouse.ui.issue.issuing_raw_materials_to_production_line.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.TransferToProductionLineDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.model.TransferToProductionReservationLine

data class IssueDetailsState(
    val reservationLines: List<TransferToProductionReservationLine> = emptyList(),
    val filteredMaterials: List<TransferToProductionReservationLine> = emptyList(),
    val addedLines: List<TransferToProductionLineDto> = emptyList(),
    val warehousesList: List<SAPWarehouse> = emptyList(),
    val stockList: List<SAPMaterialBatchValuationStock> = emptyList(),

    val isLoading: Boolean = false,
    val isBinLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,

    val saveKind: String? = null,

    val binInput: String = "",
    val selBinData: StorageBinLocation? = null,
    val binCodeError: String? = null,

    val matInput: String = "",
    val matSearchText: String = "",
    val matErrorMessage : String? = null,
    val selReservationLine: TransferToProductionReservationLine? = null,
    val matDlg: Boolean = false,

    val selectedStock: SAPMaterialBatchValuationStock? = null,
    val stockSearch: String = "",
    val stockError :String = "",

    val selectedWarehouse: SAPWarehouse? = null,
    val warehouseSearch: String = "",
    val warehouseError: String = "",

    val qty: String = "",
    val qtyError: String? = null,
    val maxQty: Double = 0.0,

    val loadingErrorMessage: String? = null,
    val savingErrorMessage: String? = null,
    val savingSuccessMessage :String? = null,

)