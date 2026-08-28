package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer

import net.gbs.gbswarehouse.data.remote.dto.response.DirectTransferLineDto
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.DirectTransferWarehouseToType
import net.gbs.gbswarehouse.directTransferWarehouseToTypes

data class IssuingDirectTransferState(
    val isLoading: Boolean = false,
    val isBinFromLoading: Boolean = false,
    val isBinToLoading: Boolean = false,
    val isStockLoading: Boolean = false,
    val isSaving: Boolean = false,

    val binFromInput: String = "",
    val binFromData: StorageBinLocation? = null,
    val binFromError: String? = null,

    val matInput: String = "",
    val matDataList: List<SAPMaterialBatchValuationStock> = emptyList(),
    val selectedMatStock: SAPMaterialBatchValuationStock? = null,
    val matErrorMessage: String? = null,

    val warehouseType: DirectTransferWarehouseToType = directTransferWarehouseToTypes[0],
    
    val binToInput: String = "",
    val binToData: StorageBinLocation? = null,
    val binToError: String? = null,
    
    val productionWarehouses: List<SAPWarehouse> = emptyList(),
    val selectedProductionWarehouse: SAPWarehouse? = null,

    val quantity: String = "",
    val quantityError: String? = null,

    val addedLines: List<DirectTransferLineDto> = emptyList(),
    
    val savingSuccessMessage: String? = null,
    val savingErrorMessage: String? = null,
    
    val stockSearch: String = "",
    val stockList: List<SAPMaterialBatchValuationStock> = emptyList(),
    val maxQty: Double = Double.MAX_VALUE
)
