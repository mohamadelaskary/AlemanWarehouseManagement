package net.gbs.gbswarehouse.ui.receiving.receiving_poultry_from_vendor.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.NaolonPerTon
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingPoultryFromVendorEvent {
    data class OnScreenLoaded(val poNumber: String) : ReceivingPoultryFromVendorEvent()
    data class OnMaterialCodeInputEntered(val materialCode: String) : ReceivingPoultryFromVendorEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingPoultryFromVendorEvent()
    data class OnMaterialSearchTextEntered(val text: String) : ReceivingPoultryFromVendorEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingPoultryFromVendorEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingPoultryFromVendorEvent()

    object OnAddButtonClicked : ReceivingPoultryFromVendorEvent()
    data class OnDeleteAddedMaterialButtonClicked(val index: Int) : ReceivingPoultryFromVendorEvent()
    object OnSaveButtonClicked : ReceivingPoultryFromVendorEvent()
    object OnClearMaterialButtonClicked : ReceivingPoultryFromVendorEvent()
    object HideSuccessDialog : ReceivingPoultryFromVendorEvent()
    object HideErrorDialog : ReceivingPoultryFromVendorEvent()
    
    object OnChangePoButtonClicked : ReceivingPoultryFromVendorEvent()
    data class OnPurchaseOrderSearchTextEntered(val text: String) : ReceivingPoultryFromVendorEvent()
    data class OnPurchaseOrderSelected(val poNumber: String) : ReceivingPoultryFromVendorEvent()
    object OnDismissChangePoDialog : ReceivingPoultryFromVendorEvent()
    data class OnWarehouseSearchTextChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnCarNoSelected(val carNo: CarNumber) : ReceivingPoultryFromVendorEvent()
    data class OnCarNoTextSearchChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnUnloadingPointEntered(val unloadingPoint: String) : ReceivingPoultryFromVendorEvent()

    data class OnNaolonEntered(val naolon: String) : ReceivingPoultryFromVendorEvent()

    data class OnContNoEntered(val contNo: String) : ReceivingPoultryFromVendorEvent()

    data class OnLoadingVendorSelected(val loadingVendor: LoadingVendor) : ReceivingPoultryFromVendorEvent()
    data class OnLoadingVendorTextSearchChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnWorkerGroupSelected(val workerGroup: WorkersGroup) : ReceivingPoultryFromVendorEvent()
    data class OnWorkerGroupTextSearchChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnNaolonDescriptionSelected(val naolonDescription: NaolonDescription) : ReceivingPoultryFromVendorEvent()
    data class OnNaolonDescriptionTextSearchChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnCarTypeSelected(val carType: CarType) : ReceivingPoultryFromVendorEvent()
    data class OnCarTypeTextSearchChange(val searchText: String) : ReceivingPoultryFromVendorEvent()

    data class OnExpiryDateEntered(val expiryDate: String) : ReceivingPoultryFromVendorEvent()

    data class OnNaolonPerTonLoaded(val naolonPerTon: NaolonPerTon) : ReceivingPoultryFromVendorEvent()

    data class OnPackingTypeSelected(val packingType: PackageType) : ReceivingPoultryFromVendorEvent()

    data class  OnPackingTypeTextSearchChange(val text: String) : ReceivingPoultryFromVendorEvent()

}
