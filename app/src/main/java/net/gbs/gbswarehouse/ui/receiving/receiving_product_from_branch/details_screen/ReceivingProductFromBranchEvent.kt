package net.gbs.gbswarehouse.ui.receiving.receiving_product_from_branch.details_screen

import net.gbs.gbswarehouse.data.remote.dto.response.NaolonPerTon
import net.gbs.gbswarehouse.domain.model.CarNumber
import net.gbs.gbswarehouse.domain.model.CarType
import net.gbs.gbswarehouse.domain.model.LoadingVendor
import net.gbs.gbswarehouse.domain.model.NaolonDescription
import net.gbs.gbswarehouse.domain.model.PackageType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup

sealed class ReceivingProductFromBranchEvent {
    data class OnScreenLoaded(val carNo: String) : ReceivingProductFromBranchEvent()
    data class OnMaterialCodeInputEntered(val materialCode: String) : ReceivingProductFromBranchEvent()
    data class OnMaterialSelected(val materialCode: String) : ReceivingProductFromBranchEvent()
    data class OnMaterialSearchTextEntered(val text: String) : ReceivingProductFromBranchEvent()
    data class OnQuantityEntered(val quantity: String) : ReceivingProductFromBranchEvent()
    data class OnWarehouseSelected(val warehouse: Warehouse) : ReceivingProductFromBranchEvent()

    data object OnAddButtonClicked : ReceivingProductFromBranchEvent()
    data class OnDeleteAddedMaterialButtonClicked(val index: Int) : ReceivingProductFromBranchEvent()
    data class OnEditAddedMaterialButtonClicked(val index: Int) : ReceivingProductFromBranchEvent()
    data object OnSaveButtonClicked : ReceivingProductFromBranchEvent()
    data object OnClearMaterialButtonClicked : ReceivingProductFromBranchEvent()
    data object HideSuccessDialog : ReceivingProductFromBranchEvent()
    data object HideErrorDialog : ReceivingProductFromBranchEvent()

    data class OnPurchaseOrderSearchTextEntered(val text: String) : ReceivingProductFromBranchEvent()
    data class OnPurchaseOrderSelected(val carNo: String) : ReceivingProductFromBranchEvent()
    data class OnWarehouseSearchTextChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnCarNoSelected(val carNo: String) : ReceivingProductFromBranchEvent()
    data class OnCarNoTextSearchChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnUnloadingPointEntered(val unloadingPoint: String) : ReceivingProductFromBranchEvent()
    data class OnProductionDateEntered(val productionDate: String) : ReceivingProductFromBranchEvent()

    data class OnLoadingVendorSelected(val loadingVendor: LoadingVendor) : ReceivingProductFromBranchEvent()
    data class OnLoadingVendorTextSearchChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnWorkerGroupSelected(val workerGroup: WorkersGroup) : ReceivingProductFromBranchEvent()
    data class OnWorkerGroupTextSearchChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnNaolonDescriptionSelected(val naolonDescription: NaolonDescription) : ReceivingProductFromBranchEvent()
    data class OnNaolonDescriptionTextSearchChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnCarTypeSelected(val carType: CarType) : ReceivingProductFromBranchEvent()
    data class OnCarTypeTextSearchChange(val searchText: String) : ReceivingProductFromBranchEvent()

    data class OnNaolonPerTonLoaded(val naolonPerTon: NaolonPerTon) : ReceivingProductFromBranchEvent()

    data class OnPackingTypeSelected(val packingType: PackageType) : ReceivingProductFromBranchEvent()
    data class OnPackingTypeTextSearchChange(val text: String) : ReceivingProductFromBranchEvent()
}
