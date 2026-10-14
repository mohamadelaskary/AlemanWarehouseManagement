package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.TransportingVendor
import net.gbs.gbswarehouse.data.remote.dto.response.RawMaterialsLineDto
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrder
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.transportationTypes
import net.gbs.gbswarehouse.transportingVendors

data class ReceivingRawMaterialsFromVendorDetailsState(
    val purchaseOrder: RawMaterialsPurchaseOrder? = null,
    val getDataStatus: RawMaterialsPurchaseOrderGetDataStatus = RawMaterialsPurchaseOrderGetDataStatus.IDLE,

    val selectedMaterial: RawMaterialsPurchaseOrderItem? = null,
    val materialSearchText: String = "",
    val filteredMaterials: List<RawMaterialsPurchaseOrderItem> = emptyList(),
    val materialCodeError: Message? = null,

    val warehouses: List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse? = null,
    val warehouseSearchText: String = "",
    val warehouseError: Message? = null,

    val carNumbers: List<CarNumber> = emptyList(),
    val selectedCarNumber: String? = null,
    val carNumberSearchText: String = "",
    val carNumberError: Message? = null,

    val unloadingPoint: String = "",
    val unloadingPointError: Message? = null,

    val workerGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkerGroup: WorkersGroup? = null,
    val workerGroupSearchText: String = "",
    val workerGroupError: Message? = null,

    val naolonDescriptions: List<NaolonDescription> = emptyList(),
    val selectedNaolonDescription: NaolonDescription? = null,
    val naolonDescriptionSearchText: String = "",
    val naolonDescriptionError: Message? = null,

    val carTypes: List<CarType> = emptyList(),
    val selectedCarType: CarType? = null,
    val carTypeSearchText: String = "",
    val carTypeError: Message? = null,

    val contNo: String = "",
    val contNoError: Message? = null,

    val loadingVendors: List<LoadingVendor> = emptyList(),
    val selectedLoadingVendor: LoadingVendor? = null,
    val loadingVendorSearchText: String = "",
    val loadingVendorError: Message? = null,

    val packageTypes: List<PackageType> = emptyList(),
    val selectedPackageType: PackageType? = null,
    val packageTypeSearchText: String = "",
    val packageTypeError: Message? = null,

    val naolonValue: String? = null,
    val naolonLoadingStatus: NaolonLoadingStatus = NaolonLoadingStatus.IDLE,

    val quantity: String = "",
    val quantityError: Message? = null,

    val addedItems: List<RawMaterialsLineDto> = emptyList(),

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveErrorMessage: Message? = null,
    val saveSuccessMessage: Message? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val transportationVendor: TransportationType? = null,
)

sealed class RawMaterialsPurchaseOrderGetDataStatus {
    data object IDLE : RawMaterialsPurchaseOrderGetDataStatus()
    data object LOADING : RawMaterialsPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: RawMaterialsPurchaseOrder,val successMessage: Message) : RawMaterialsPurchaseOrderGetDataStatus()
    data class ERROR(val message: Message) : RawMaterialsPurchaseOrderGetDataStatus()
}

sealed class NaolonLoadingStatus {
    data object IDLE : NaolonLoadingStatus()
    data object LOADING : NaolonLoadingStatus()
    data class SUCCESS(val value: Int) : NaolonLoadingStatus()
    data class ERROR(val message: Message) : NaolonLoadingStatus()
}
