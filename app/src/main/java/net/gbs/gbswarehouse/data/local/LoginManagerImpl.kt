package net.gbs.gbswarehouse.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import net.gbs.gbswarehouse.domain.model.Permissions
import javax.inject.Inject

class LoginManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : LoginManager {
    private val sharedPreferences =
        context.getSharedPreferences(
            "login_pref",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_PLANT_CODES = "user_plant_codes"

        private const val PERM_RECEIVE_VENDOR_RAW = "isReceiveVendorRawMaterials"
        private const val PERM_PUT_AWAY_VENDOR_RAW = "isPutAwayVendorRawMaterials"
        private const val PERM_RECEIVE_VENDOR_SPARE = "isReceiveVendorSpareParts"
        private const val PERM_PUT_AWAY_VENDOR_SPARE = "isPutAwayVendorSpareParts"
        private const val PERM_RECEIVE_VENDOR_PKG = "isReceiveVendorPackagingMaterials"
        private const val PERM_PUT_AWAY_VENDOR_PKG = "isPutAwayVendorPackagingMaterials"
        private const val PERM_RECEIVE_VENDOR_POULTRY = "isReceiveVendorPoultryMaterials"
        private const val PERM_PUT_AWAY_VENDOR_POULTRY = "isPutAwayVendorPoultryMaterials"
        private const val PERM_RECEIVE_FG_PLASTIC = "isReceiveFinishedGoodsPlastic"
        private const val PERM_PUT_AWAY_FG_PLASTIC = "isPutAwayFinishedGoodsPlastic"
        private const val PERM_RECEIVE_FG_FEED = "isReceiveFinishedGoodsFeed"
        private const val PERM_PUT_AWAY_FG_FEED = "isPutAwayFinishedGoodsFeed"
        private const val PERM_RECEIVE_TRANSIT = "isReceiveTransitOrderMaterial"
        private const val PERM_PUT_AWAY_TRANSIT = "isPutAwayTransitOrderMaterial"
        private const val PERM_RECEIVE_SPARE_RETURN = "isReceiveSparePartsReturn"
        private const val PERM_PUT_AWAY_SPARE_RETURN = "isPutAwaySparePartsReturn"
        private const val PERM_BLIND_RECEIVE = "isBlindReceive"
        private const val PERM_ISSUE_TRANSFER_PROD = "isIssueTransferToProduction"
        private const val PERM_ISSUE_SPARE_MAINT = "isIssueSparePartsToMaintenance"
        private const val PERM_ISSUE_INTERNAL = "isIssueToInternalOrder"
        private const val PERM_DIRECT_TRANSFER = "isDirectTransfer"
        private const val PERM_PUT_AWAY_DIRECT_TRANSFER = "isPutAwayDirectTransfer"
        private const val PERM_DIRECT_ISSUE_COST = "isDirectIssueOnCostCenter"
        private const val PERM_SCRAP_ISSUE = "isScrapIssue"
        private const val PERM_ISSUE_FG_PLASTIC = "isIssueFinishedGoodsPlastic"
        private const val PERM_ISSUE_FG_FEED = "isIssueFinishedGoodsFeed"
        private const val PERM_ISSUE_TRANSFER_ORDER = "isIssueTransferOrder"
        private const val PERM_PHYSICAL_COUNT_VIEW = "isPhysicalCountView"
        private const val PERM_PHYSICAL_COUNT_SUBMIT = "isPhysicalCountSubmit"
        private const val PERM_ERP_MODE = "erpMode"
        private const val PERM_REQUIRES_ERP = "requiresErpCredentials"
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(
            USER_ID,
            null
        )
    }

    override fun getUserCompanies(): List<String> {
        return getUserPlantCodes()
    }

    override fun getUserPlantCodes(): List<String> {
        return sharedPreferences.getStringSet(
            USER_PLANT_CODES,
            null
        ).orEmpty().toList()
    }

    override fun getPermissions(): Permissions {
        return Permissions(
            isReceiveVendorRawMaterials = sharedPreferences.getBoolean(PERM_RECEIVE_VENDOR_RAW, false),
            isPutAwayVendorRawMaterials = sharedPreferences.getBoolean(PERM_PUT_AWAY_VENDOR_RAW, false),
            isReceiveVendorSpareParts = sharedPreferences.getBoolean(PERM_RECEIVE_VENDOR_SPARE, false),
            isPutAwayVendorSpareParts = sharedPreferences.getBoolean(PERM_PUT_AWAY_VENDOR_SPARE, false),
            isReceiveVendorPackagingMaterials = sharedPreferences.getBoolean(PERM_RECEIVE_VENDOR_PKG, false),
            isPutAwayVendorPackagingMaterials = sharedPreferences.getBoolean(PERM_PUT_AWAY_VENDOR_PKG, false),
            isReceiveVendorPoultryMaterials = sharedPreferences.getBoolean(PERM_RECEIVE_VENDOR_POULTRY, false),
            isPutAwayVendorPoultryMaterials = sharedPreferences.getBoolean(PERM_PUT_AWAY_VENDOR_POULTRY, false),
            isReceiveFinishedGoodsPlastic = sharedPreferences.getBoolean(PERM_RECEIVE_FG_PLASTIC, false),
            isPutAwayFinishedGoodsPlastic = sharedPreferences.getBoolean(PERM_PUT_AWAY_FG_PLASTIC, false),
            isReceiveFinishedGoodsFeed = sharedPreferences.getBoolean(PERM_RECEIVE_FG_FEED, false),
            isPutAwayFinishedGoodsFeed = sharedPreferences.getBoolean(PERM_PUT_AWAY_FG_FEED, false),
            isReceiveTransitOrderMaterial = sharedPreferences.getBoolean(PERM_RECEIVE_TRANSIT, false),
            isPutAwayTransitOrderMaterial = sharedPreferences.getBoolean(PERM_PUT_AWAY_TRANSIT, false),
            isReceiveSparePartsReturn = sharedPreferences.getBoolean(PERM_RECEIVE_SPARE_RETURN, false),
            isPutAwaySparePartsReturn = sharedPreferences.getBoolean(PERM_PUT_AWAY_SPARE_RETURN, false),
            isBlindReceive = sharedPreferences.getBoolean(PERM_BLIND_RECEIVE, false),
            isIssueTransferToProduction = sharedPreferences.getBoolean(PERM_ISSUE_TRANSFER_PROD, false),
            isIssueSparePartsToMaintenance = sharedPreferences.getBoolean(PERM_ISSUE_SPARE_MAINT, false),
            isIssueToInternalOrder = sharedPreferences.getBoolean(PERM_ISSUE_INTERNAL, false),
            isDirectTransfer = sharedPreferences.getBoolean(PERM_DIRECT_TRANSFER, false),
            isPutAwayDirectTransfer = sharedPreferences.getBoolean(PERM_PUT_AWAY_DIRECT_TRANSFER, false),
            isDirectIssueOnCostCenter = sharedPreferences.getBoolean(PERM_DIRECT_ISSUE_COST, false),
            isScrapIssue = sharedPreferences.getBoolean(PERM_SCRAP_ISSUE, false),
            isIssueFinishedGoodsPlastic = sharedPreferences.getBoolean(PERM_ISSUE_FG_PLASTIC, false),
            isIssueFinishedGoodsFeed = sharedPreferences.getBoolean(PERM_ISSUE_FG_FEED, false),
            isIssueTransferOrder = sharedPreferences.getBoolean(PERM_ISSUE_TRANSFER_ORDER, false),
            isPhysicalCountView = sharedPreferences.getBoolean(PERM_PHYSICAL_COUNT_VIEW, false),
            isPhysicalCountSubmit = sharedPreferences.getBoolean(PERM_PHYSICAL_COUNT_SUBMIT, false),
            erpMode = sharedPreferences.getString(PERM_ERP_MODE, "") ?: "",
            requiresErpCredentials = sharedPreferences.getBoolean(PERM_REQUIRES_ERP, false)
        )
    }

    override fun saveUserData(
        userId: String,
        userCompanies: List<String>
    ) {
        saveUserData(userId, userCompanies, Permissions.default())
    }

    override fun saveUserData(
        userId: String,
        userPlantCodes: List<String>,
        permissions: Permissions
    ) {
        sharedPreferences.edit {
            putString(USER_ID, userId)
                .putStringSet(USER_PLANT_CODES, userPlantCodes.toSet())
                .putBoolean(PERM_RECEIVE_VENDOR_RAW, permissions.isReceiveVendorRawMaterials)
                .putBoolean(PERM_PUT_AWAY_VENDOR_RAW, permissions.isPutAwayVendorRawMaterials)
                .putBoolean(PERM_RECEIVE_VENDOR_SPARE, permissions.isReceiveVendorSpareParts)
                .putBoolean(PERM_PUT_AWAY_VENDOR_SPARE, permissions.isPutAwayVendorSpareParts)
                .putBoolean(PERM_RECEIVE_VENDOR_PKG, permissions.isReceiveVendorPackagingMaterials)
                .putBoolean(PERM_PUT_AWAY_VENDOR_PKG, permissions.isPutAwayVendorPackagingMaterials)
                .putBoolean(PERM_RECEIVE_VENDOR_POULTRY, permissions.isReceiveVendorPoultryMaterials)
                .putBoolean(PERM_PUT_AWAY_VENDOR_POULTRY, permissions.isPutAwayVendorPoultryMaterials)
                .putBoolean(PERM_RECEIVE_FG_PLASTIC, permissions.isReceiveFinishedGoodsPlastic)
                .putBoolean(PERM_PUT_AWAY_FG_PLASTIC, permissions.isPutAwayFinishedGoodsPlastic)
                .putBoolean(PERM_RECEIVE_FG_FEED, permissions.isReceiveFinishedGoodsFeed)
                .putBoolean(PERM_PUT_AWAY_FG_FEED, permissions.isPutAwayFinishedGoodsFeed)
                .putBoolean(PERM_RECEIVE_TRANSIT, permissions.isReceiveTransitOrderMaterial)
                .putBoolean(PERM_PUT_AWAY_TRANSIT, permissions.isPutAwayTransitOrderMaterial)
                .putBoolean(PERM_RECEIVE_SPARE_RETURN, permissions.isReceiveSparePartsReturn)
                .putBoolean(PERM_PUT_AWAY_SPARE_RETURN, permissions.isPutAwaySparePartsReturn)
                .putBoolean(PERM_BLIND_RECEIVE, permissions.isBlindReceive)
                .putBoolean(PERM_ISSUE_TRANSFER_PROD, permissions.isIssueTransferToProduction)
                .putBoolean(PERM_ISSUE_SPARE_MAINT, permissions.isIssueSparePartsToMaintenance)
                .putBoolean(PERM_ISSUE_INTERNAL, permissions.isIssueToInternalOrder)
                .putBoolean(PERM_DIRECT_TRANSFER, permissions.isDirectTransfer)
                .putBoolean(PERM_PUT_AWAY_DIRECT_TRANSFER, permissions.isPutAwayDirectTransfer)
                .putBoolean(PERM_DIRECT_ISSUE_COST, permissions.isDirectIssueOnCostCenter)
                .putBoolean(PERM_SCRAP_ISSUE, permissions.isScrapIssue)
                .putBoolean(PERM_ISSUE_FG_PLASTIC, permissions.isIssueFinishedGoodsPlastic)
                .putBoolean(PERM_ISSUE_FG_FEED, permissions.isIssueFinishedGoodsFeed)
                .putBoolean(PERM_ISSUE_TRANSFER_ORDER, permissions.isIssueTransferOrder)
                .putBoolean(PERM_PHYSICAL_COUNT_VIEW, permissions.isPhysicalCountView)
                .putBoolean(PERM_PHYSICAL_COUNT_SUBMIT, permissions.isPhysicalCountSubmit)
                .putString(PERM_ERP_MODE, permissions.erpMode)
                .putBoolean(PERM_REQUIRES_ERP, permissions.requiresErpCredentials)
        }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}
