package net.gbs.gbswarehouse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.ReceivingScreenNames
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.local.LoginManager
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

data class SubMenuUiState(
    val isLoading: Boolean = false,
    val countsMap: Map<String, Int> = emptyMap(),
    val errorMessage: String? = null,
    val showNotAuthorizedDialog: Boolean = false
)

@HiltViewModel
class SubMenuViewModel @Inject constructor(
    private val receivingRepository: ReceivingRepository,
    private val issuingRepository: IssuingRepository,
    private val loginManager: LoginManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubMenuUiState())
    val uiState: StateFlow<SubMenuUiState> = _uiState.asStateFlow()

    fun loadCounts(category: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (category) {
                "recv" -> {
                    when (val result = receivingRepository.getReceivingOpenOrderCounts()) {
                        is NetworkResult.Success -> {
                            val data = result.data
                            val map = mapOf(
                                ReceivingScreenNames.RawMaterialsFromVendor to data.rawMaterials,
                                ReceivingScreenNames.SparePartsFromVendor to data.spareParts,
                                ReceivingScreenNames.PackagingMaterialsFromVendor to data.packagingMaterials,
                                ReceivingScreenNames.PoultryMaterialsFromVendor to data.poultryMaterials,
                                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder to data.finishedGoodsFeed,
                                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder to data.finishedGoodsPlastic,
                                ReceivingScreenNames.SparePartsReturn to data.sparePartsReturn,
                                ReceivingScreenNames.TransitOrderMaterial to data.transitOrderMaterial
                            )
                            _uiState.update { it.copy(isLoading = false, countsMap = map) }
                        }
                        is NetworkResult.BusinessError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.HttpError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.NetworkError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.UnknownError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        else -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                }
                "putaway" -> {
                    when (val result = receivingRepository.getPutAwayOpenOrderCounts()) {
                        is NetworkResult.Success -> {
                            val data = result.data
                            val map = mapOf(
                                ReceivingScreenNames.RawMaterialsFromVendor to data.rawMaterials,
                                ReceivingScreenNames.SparePartsFromVendor to data.spareParts,
                                ReceivingScreenNames.PackagingMaterialsFromVendor to data.packagingMaterials,
                                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder to data.finishedGoodsFeed,
                                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder to data.finishedGoodsPlastic,
                                ReceivingScreenNames.SparePartsReturn to data.sparePartsReturn,
                                ReceivingScreenNames.TransitOrderMaterial to data.transitOrderMaterial,
                                "issue-direct-transfer-putaway" to data.directTransfer
                            )
                            _uiState.update { it.copy(isLoading = false, countsMap = map) }
                        }
                        is NetworkResult.BusinessError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.HttpError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.NetworkError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.UnknownError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        else -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                }
                else -> { // "issue"
                    when (val result = issuingRepository.getIssuingOpenOrderCounts()) {
                        is NetworkResult.Success -> {
                            val data = result.data
                            val map = mapOf(
                                "issue-raw" to data.transferToProduction,
                                "issue-spare" to data.sparePartsToMaintenance,
                                "issue-internal" to data.internalOrder,
                                "issue-sales" to data.finishedGoodsFeed,
                                "issue-cost-center" to data.directIssueOnCostCenter,
                                "issue-transfer-plants" to data.transferOrder
                            )
                            _uiState.update { it.copy(isLoading = false, countsMap = map) }
                        }
                        is NetworkResult.BusinessError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.HttpError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.NetworkError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        is NetworkResult.UnknownError -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                        }
                        else -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                }
            }
        }
    }

    fun checkOperationAuthorization(opId: String, isPutAway: Boolean, isRecv: Boolean, onAuthorized: () -> Unit) {
        val permissions = loginManager.getPermissions()
        val isAuthorized = if (isPutAway) {
            when (opId) {
                ReceivingScreenNames.RawMaterialsFromVendor -> permissions.isPutAwayVendorRawMaterials
                ReceivingScreenNames.SparePartsFromVendor -> permissions.isPutAwayVendorSpareParts
                ReceivingScreenNames.PackagingMaterialsFromVendor -> permissions.isPutAwayVendorPackagingMaterials
                ReceivingScreenNames.PoultryMaterialsFromVendor -> permissions.isPutAwayVendorPoultryMaterials
                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder -> permissions.isPutAwayFinishedGoodsFeed
                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder -> permissions.isPutAwayFinishedGoodsPlastic
                ReceivingScreenNames.SparePartsReturn -> permissions.isPutAwaySparePartsReturn
                ReceivingScreenNames.TransitOrderMaterial -> permissions.isPutAwayTransitOrderMaterial
                "issue-direct-transfer-putaway" -> permissions.isPutAwayDirectTransfer
                else -> true
            }
        } else if (isRecv) {
            when (opId) {
                ReceivingScreenNames.RawMaterialsFromVendor -> permissions.isReceiveVendorRawMaterials
                ReceivingScreenNames.SparePartsFromVendor -> permissions.isReceiveVendorSpareParts
                ReceivingScreenNames.PackagingMaterialsFromVendor -> permissions.isReceiveVendorPackagingMaterials
                ReceivingScreenNames.PoultryMaterialsFromVendor -> permissions.isReceiveVendorPoultryMaterials
                "recv-blind" -> permissions.isBlindReceive
                ReceivingScreenNames.FinishedGoodsFeedFromProductionOrder -> permissions.isReceiveFinishedGoodsFeed
                ReceivingScreenNames.FinishedGoodsPlasticFromProductionOrder -> permissions.isReceiveFinishedGoodsPlastic
                ReceivingScreenNames.SparePartsReturn -> permissions.isReceiveSparePartsReturn
                ReceivingScreenNames.TransitOrderMaterial -> permissions.isReceiveTransitOrderMaterial
                else -> true
            }
        } else {
            when (opId) {
                "issue-raw" -> permissions.isIssueTransferToProduction
                "issue-spare" -> permissions.isIssueSparePartsToMaintenance
                "issue-internal" -> permissions.isIssueToInternalOrder
                "issue-sales" -> permissions.isIssueFinishedGoodsFeed
                "issue-sales_plastic" -> permissions.isIssueFinishedGoodsPlastic
                "issue-cost-center" -> permissions.isDirectIssueOnCostCenter
                "issue-direct-transfer" -> permissions.isDirectTransfer
                "issue-transfer-plants" -> permissions.isIssueTransferOrder
                "issue-scrap" -> permissions.isScrapIssue
                else -> true
            }
        }

        if (isAuthorized) {
            onAuthorized()
        } else {
            _uiState.update { it.copy(showNotAuthorizedDialog = true) }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showNotAuthorizedDialog = false) }
    }
}
