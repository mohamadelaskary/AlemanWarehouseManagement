package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen

import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsLine
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.PoultryMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

data class ReceivingPoultryFromVendorState (
    val orderNumber:String="",
    val purchaseOrderGetDataStatus: PoultryPurchaseOrderGetDataStatus = PoultryPurchaseOrderGetDataStatus.IDLE,
    val isWarehousesLoading:Boolean=false,
    val purchaseOrder: PoultryMaterialsPurchaseOrder?=null,
    val materialCodeInput: String = "",
    val materialSearch:String="",
    val filteredMaterials:List<PoultryMaterialsPurchaseOrderItem> = emptyList(),
    val materials:List<PoultryMaterialsPurchaseOrderItem> = emptyList(),
    val selectedMaterial:PoultryMaterialsPurchaseOrderItem?=null,
    val warehouses:List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse?=null,
    val quantity:String = "",
    val addedItems:List<PoultryMaterialsLine> = emptyList(),
    val materialCodeError : Message? = null,
    val searchError : Message? = null,
    val quantityError: Message? = null,
    val warehouseError: Message? = null,
    val error:Message? = null,
    val savingSuccessStatusMessage:Message? = null,
    val savingErrorStatusMessage:Message? = null,
    val isSavingLoading:Boolean = false,
    val showSuccessDialog:Boolean = false,
    val showErrorDialog:Boolean = false,
    val warehouseSearchText: String = "",
    val allPurchaseOrders: List<PoultryMaterialsPurchaseOrder> = emptyList(),
    val filteredPurchaseOrders: List<PoultryMaterialsPurchaseOrder> = emptyList(),
    val purchaseOrderSearch: String = "",
    val showChangePoDialog: Boolean = false,
    val isAllPurchaseOrdersLoading: Boolean = false,

    val selectedCarNo: String?=null,
//    val carNos:List<CarNumber> = emptyList(),
//    val carNoSearchText:String="",
//    val carNoError:String="",

    val unloadingPoint:String="",
    val unloadingPointError:Message? = null,

    val naolon :String="",
    val naolonError:Message? = null,

    val contNo:String="",
    val contNoError:Message? = null,

    val selectedLoadingVendor: LoadingVendor?=null,
    val loadingVendors:List<LoadingVendor> = emptyList(),
    val loadingVendorSearchText:String="",
    val loadingVendorError:Message? = null,

    val selectedWorkerGroup: WorkersGroup?=null,
    val workerGroups:List<WorkersGroup> = emptyList(),
    val workerGroupSearchText:String="",
    val workerGroupError:Message? = null,

    val selectedNaolonDescription: NaolonDescription?=null,
    val naolonDescriptions:List<NaolonDescription> = emptyList(),
    val naolonDescriptionSearchText:String="",
    val naolonLoadStatus: NaolonLoadingStatus = NaolonLoadingStatus.IDLE,

    val selectedCarType: CarType?=null,
    val carTypes:List<CarType> = emptyList(),
    val carTypeSearchText:String="",
    val carTypeError:Message? = null,

    val expiryDate:String="",
    val expiryDateError:Message? = null,

    val packingTypes: List<PackageType> = emptyList(),
    val selectedPackingType: PackageType?=null,
    val packingTypeSearchText:String="",
    val packingTypeError:Message? = null,
    val transportationVendor: TransportationType? = null,
    )

sealed class PoultryPurchaseOrderGetDataStatus {
    data object IDLE : PoultryPurchaseOrderGetDataStatus()
    data object LOADING : PoultryPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: PoultryMaterialsPurchaseOrder) : PoultryPurchaseOrderGetDataStatus()
    data class ERROR(val error: Message) : PoultryPurchaseOrderGetDataStatus()
}

sealed class NaolonLoadingStatus {
    data object IDLE : NaolonLoadingStatus()
    data object LOADING : NaolonLoadingStatus()
    data class SUCCESS(val naolon: String) : NaolonLoadingStatus()
    data class ERROR(val error: Message) : NaolonLoadingStatus()
}
