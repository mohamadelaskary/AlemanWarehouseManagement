package net.gbs.gbswarehouse.data.remote.dto.response

data class LoginResponse (
    override val responseStatus: ResponseStatus,
    val data: UserDto,
) : BaseResponse

data class UserDto(
    val id: String?,
    val userName: String?,
    val email: String?,
    val plantCodes: List<String>?,
    val companyCodes: List<String>?,
    val accessToken: String?,
    val refreshToken: String?,
    val permissions: PermissionsDto?
)

data class PermissionsDto(
    val isReceiveVendorRawMaterials: Boolean?,
    val isPutAwayVendorRawMaterials: Boolean?,
    val isReceiveVendorSpareParts: Boolean?,
    val isPutAwayVendorSpareParts: Boolean?,
    val isReceiveVendorPackagingMaterials: Boolean?,
    val isPutAwayVendorPackagingMaterials: Boolean?,
    val isReceiveVendorPoultryMaterials: Boolean?,
    val isPutAwayVendorPoultryMaterials: Boolean?,
    val isReceiveFinishedGoodsPlastic: Boolean?,
    val isPutAwayFinishedGoodsPlastic: Boolean?,
    val isReceiveFinishedGoodsFeed: Boolean?,
    val isPutAwayFinishedGoodsFeed: Boolean?,
    val isReceiveTransitOrderMaterial: Boolean?,
    val isPutAwayTransitOrderMaterial: Boolean?,
    val isReceiveSparePartsReturn: Boolean?,
    val isPutAwaySparePartsReturn: Boolean?,
    val isBlindReceive: Boolean?,
    val isIssueTransferToProduction: Boolean?,
    val isIssueSparePartsToMaintenance: Boolean?,
    val isIssueToInternalOrder: Boolean?,
    val isDirectTransfer: Boolean?,
    val isPutAwayDirectTransfer: Boolean?,
    val isDirectIssueOnCostCenter: Boolean?,
    val isScrapIssue: Boolean?,
    val isIssueFinishedGoodsPlastic: Boolean?,
    val isIssueFinishedGoodsFeed: Boolean?,
    val isIssueTransferOrder: Boolean?,
    val isPhysicalCountView: Boolean?,
    val isPhysicalCountSubmit: Boolean?,
    val erpMode: String?,
    val requiresErpCredentials: Boolean?
)
