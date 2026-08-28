package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.TransportingVendor
import net.gbs.gbswarehouse.data.remote.dto.response.RawMaterialsLineDto
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
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
    val materialCodeError: String = "",

    val warehouses: List<Warehouse> = emptyList(),
    val selectedWarehouse: Warehouse? = null,
    val warehouseSearchText: String = "",
    val warehouseError: String = "",

    val carNumbers: List<CarNumber> = emptyList(),
    val selectedCarNumber: String? = null,
    val carNumberSearchText: String = "",
    val carNumberError: String = "",

    val unloadingPoint: String = "",
    val unloadingPointError: String = "",

    val workerGroups: List<WorkersGroup> = emptyList(),
    val selectedWorkerGroup: WorkersGroup? = null,
    val workerGroupSearchText: String = "",
    val workerGroupError: String = "",

    val naolonDescriptions: List<NaolonDescription> = emptyList(),
    val selectedNaolonDescription: NaolonDescription? = null,
    val naolonDescriptionSearchText: String = "",
    val naolonDescriptionError: String = "",

    val carTypes: List<CarType> = emptyList(),
    val selectedCarType: CarType? = null,
    val carTypeSearchText: String = "",
    val carTypeError: String = "",

    val contNo: String = "",
    val contNoError: String = "",

    val loadingVendors: List<LoadingVendor> = emptyList(),
    val selectedLoadingVendor: LoadingVendor? = null,
    val loadingVendorSearchText: String = "",
    val loadingVendorError: String = "",

    val packageTypes: List<PackageType> = emptyList(),
    val selectedPackageType: PackageType? = null,
    val packageTypeSearchText: String = "",
    val packageTypeError: String = "",

    val naolonValue: String? = null,
    val naolonLoadingStatus: NaolonLoadingStatus = NaolonLoadingStatus.IDLE,

    val quantity: String = "",
    val quantityError: String = "",

    val addedItems: List<RawMaterialsLineDto> = emptyList(),

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String = "",
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val transportationVendor: TransportationType = transportationTypes.first(),
)

sealed class RawMaterialsPurchaseOrderGetDataStatus {
    data object IDLE : RawMaterialsPurchaseOrderGetDataStatus()
    data object LOADING : RawMaterialsPurchaseOrderGetDataStatus()
    data class SUCCESS(val po: RawMaterialsPurchaseOrder) : RawMaterialsPurchaseOrderGetDataStatus()
    data class ERROR(val message: String) : RawMaterialsPurchaseOrderGetDataStatus()
}

sealed class NaolonLoadingStatus {
    data object IDLE : NaolonLoadingStatus()
    data object LOADING : NaolonLoadingStatus()
    data class SUCCESS(val value: Int) : NaolonLoadingStatus()
    data class ERROR(val message: String) : NaolonLoadingStatus()
}
