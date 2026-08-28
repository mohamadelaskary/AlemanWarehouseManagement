package net.gbs.gbswarehouse.ui.receiving.receiving_raw_materials_from_vendor.details_screen

import net.gbs.gbswarehouse.TransportationType
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.RawMaterialsPurchaseOrderItem
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingRawMaterialsFromVendorDetailsEvent {
    data class LoadOrder(val poNumber: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    
    data class OnMaterialSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnMaterialSelected(val material: RawMaterialsPurchaseOrderItem) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnMaterialCodeScanned(val barcode: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data object OnClearMaterial : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnWarehouseSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnCarNumberSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnCarNumberSelected(val carNumber: String) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnUnloadingPointChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnWorkerGroupSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnWorkerGroupSelected(val workerGroup: WorkersGroup) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnNaolonDescriptionSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnNaolonDescriptionSelected(val naolonDescription: NaolonDescription) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnCarTypeSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnCarTypeSelected(val carType: CarType) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnContNoChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnLoadingVendorSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnLoadingVendorSelected(val loadingVendor: LoadingVendor) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnPackageTypeSearchTextChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnPackageTypeSelected(val packageType: PackageType) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnQuantityChanged(val text: String) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data object OnAddItem : ReceivingRawMaterialsFromVendorDetailsEvent()
    data class OnRemoveItem(val index: Int) : ReceivingRawMaterialsFromVendorDetailsEvent()

    data object OnSave : ReceivingRawMaterialsFromVendorDetailsEvent()
    data object OnDismissDialogs : ReceivingRawMaterialsFromVendorDetailsEvent()

    data class OnTransportationVendorChanged(val transportationType: TransportationType) : ReceivingRawMaterialsFromVendorDetailsEvent()
}
