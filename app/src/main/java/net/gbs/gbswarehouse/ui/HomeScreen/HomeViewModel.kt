package net.gbs.gbswarehouse.ui.HomeScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.data.local.LoginManager
import javax.inject.Inject

data class HomeUiState(
    val showNotAuthorizedDialog: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    val sessionManager: SessionManager,
    private val loginManager: LoginManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onCategoryClick(id: String, onAuthorized: () -> Unit) {
        val permissions = loginManager.getPermissions()
        val isAuthorized = when (id) {
            "recv" -> permissions.isReceiveVendorRawMaterials ||
                    permissions.isReceiveVendorSpareParts ||
                    permissions.isReceiveVendorPackagingMaterials ||
                    permissions.isReceiveVendorPoultryMaterials ||
                    permissions.isReceiveFinishedGoodsPlastic ||
                    permissions.isReceiveFinishedGoodsFeed ||
                    permissions.isReceiveTransitOrderMaterial ||
                    permissions.isReceiveSparePartsReturn ||
                    permissions.isBlindReceive
            "putaway" -> permissions.isPutAwayVendorRawMaterials ||
                    permissions.isPutAwayVendorSpareParts ||
                    permissions.isPutAwayVendorPackagingMaterials ||
                    permissions.isPutAwayVendorPoultryMaterials ||
                    permissions.isPutAwayFinishedGoodsPlastic ||
                    permissions.isPutAwayFinishedGoodsFeed ||
                    permissions.isPutAwayTransitOrderMaterial ||
                    permissions.isPutAwaySparePartsReturn ||
                    permissions.isPutAwayDirectTransfer
            "issue" -> permissions.isIssueTransferToProduction ||
                    permissions.isIssueSparePartsToMaintenance ||
                    permissions.isIssueToInternalOrder ||
                    permissions.isDirectTransfer ||
                    permissions.isDirectIssueOnCostCenter ||
                    permissions.isScrapIssue ||
                    permissions.isIssueFinishedGoodsPlastic ||
                    permissions.isIssueFinishedGoodsFeed ||
                    permissions.isIssueTransferOrder
            "audit" -> permissions.isPhysicalCountView || permissions.isPhysicalCountSubmit
            else -> true
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
