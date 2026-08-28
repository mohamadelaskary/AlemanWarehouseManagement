package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialLine
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrder
import net.gbs.gbswarehouse.domain.model.TransitOrderMaterialPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen.NaolonLoadingStatus

data class ReceivingProductFromBranchState (
    val orderNumber:String="",
    val purchaseOrderGetDataStatus: TransitPurchaseOrderGetDataStatus = TransitPurchaseOrderGetDataStatus.IDLE,
    val isWarehousesLoading:Boolean=false,
    val purchaseOrder: TransitOrderMaterialPurchaseOrder?=null,
    val materialCodeInput: String = "",
    val materialSearch:String="",
    val filteredMaterials:List<TransitOrderMaterialPurchaseOrderItem> = emptyList(),
    val materials:List<TransitOrderMaterialPurchaseOrderItem> = emptyList(),
    val selectedMaterial:TransitOrderMaterialPurchaseOrderItem?=null,
    val warehouses:List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse?=null,
    val quantity:String = "",
    val addedItems:List<TransitOrderMaterialLine> = emptyList(),
    val materialCodeError : String = "",
    val searchError : String = "",
    val quantityError: String = "",
    val warehouseError: String = "",
    val error:String="",
    val savingSuccessStatusMessage:String = "",
    val savingErrorStatusMessage:String = "",
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
    val warehouseSearchText: String = "",
    val allPurchaseOrders: List<TransitOrderMaterialPurchaseOrder> = emptyList(),
    val filteredPurchaseOrders: List<TransitOrderMaterialPurchaseOrder> = emptyList(),
    val purchaseOrderSearch: String = "",
    val isAllPurchaseOrdersLoading: Boolean = false,

    val selectedCarNo: CarNumber?=null,
    val carNos:List<CarNumber> = emptyList(),
    val carNoSearchText:String="",
    val carNoError:String="",

    val unloadingPoint:String="",
    val unloadingPointError:String="",

    val naolon :String="",
    val naolonError:String="",

    val contNo:String="",
    val contNoError:String="",

    val selectedLoadingVendor: LoadingVendor?=null,
    val loadingVendors:List<LoadingVendor> = emptyList(),
    val loadingVendorSearchText:String="",
    val loadingVendorError:String="",

    val selectedWorkerGroup: WorkersGroup?=null,
    val workerGroups:List<WorkersGroup> = emptyList(),
    val workerGroupSearchText:String="",
    val workerGroupError:String="",

    val selectedNaolonDescription: NaolonDescription?=null,
    val naolonDescriptions:List<NaolonDescription> = emptyList(),
    val naolonDescriptionSearchText:String="",
    val naolonLoadStatus: NaolonLoadingStatus = NaolonLoadingStatus.IDLE,

    val selectedCarType: CarType?=null,
    val carTypes:List<CarType> = emptyList(),
    val carTypeSearchText:String="",
    val carTypeError:String="",

    val packingTypes: List<PackageType> = emptyList(),
    val selectedPackingType: PackageType?=null,
    val packingTypeSearchText:String="",
    val packingTypeError:String="",
    val selectedOrderForMaterials: TransitOrderMaterialPurchaseOrder? = null,
    val showMaterialsClickedCardIndex: Int = -1,
    val isMaterialsShowed: Boolean = false
)

sealed class TransitPurchaseOrderGetDataStatus {
    data object IDLE : TransitPurchaseOrderGetDataStatus()
    data object LOADING : TransitPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: TransitOrderMaterialPurchaseOrder) : TransitPurchaseOrderGetDataStatus()
    data class ERROR(val error: String) : TransitPurchaseOrderGetDataStatus()
}
