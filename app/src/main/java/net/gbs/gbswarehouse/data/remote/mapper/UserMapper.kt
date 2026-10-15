package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.PermissionsDto
import net.gbs.gbswarehouse.data.remote.dto.response.UserDto
import net.gbs.gbswarehouse.domain.model.Permissions
import net.gbs.gbswarehouse.domain.model.User

fun UserDto.toUser(): User {
    return User(
        id = id ?: "",
        userName = userName ?: "",
        email = email ?: "",
        plantCodes = plantCodes ?: companyCodes ?: emptyList(),
        permissions = permissions?.toPermissions() ?: Permissions.default()
    )
}

fun PermissionsDto.toPermissions(): Permissions {
    return Permissions(
        isReceiveVendorRawMaterials = isReceiveVendorRawMaterials ?: false,
        isPutAwayVendorRawMaterials = isPutAwayVendorRawMaterials ?: false,
        isReceiveVendorSpareParts = isReceiveVendorSpareParts ?: false,
        isPutAwayVendorSpareParts = isPutAwayVendorSpareParts ?: false,
        isReceiveVendorPackagingMaterials = isReceiveVendorPackagingMaterials ?: false,
        isPutAwayVendorPackagingMaterials = isPutAwayVendorPackagingMaterials ?: false,
        isReceiveVendorPoultryMaterials = isReceiveVendorPoultryMaterials ?: false,
        isPutAwayVendorPoultryMaterials = isPutAwayVendorPoultryMaterials ?: false,
        isReceiveFinishedGoodsPlastic = isReceiveFinishedGoodsPlastic ?: false,
        isPutAwayFinishedGoodsPlastic = isPutAwayFinishedGoodsPlastic ?: false,
        isReceiveFinishedGoodsFeed = isReceiveFinishedGoodsFeed ?: false,
        isPutAwayFinishedGoodsFeed = isPutAwayFinishedGoodsFeed ?: false,
        isReceiveTransitOrderMaterial = isReceiveTransitOrderMaterial ?: false,
        isPutAwayTransitOrderMaterial = isPutAwayTransitOrderMaterial ?: false,
        isReceiveSparePartsReturn = isReceiveSparePartsReturn ?: false,
        isPutAwaySparePartsReturn = isPutAwaySparePartsReturn ?: false,
        isBlindReceive = isBlindReceive ?: false,
        isIssueTransferToProduction = isIssueTransferToProduction ?: false,
        isIssueSparePartsToMaintenance = isIssueSparePartsToMaintenance ?: false,
        isIssueToInternalOrder = isIssueToInternalOrder ?: false,
        isDirectTransfer = isDirectTransfer ?: false,
        isPutAwayDirectTransfer = isPutAwayDirectTransfer ?: false,
        isDirectIssueOnCostCenter = isDirectIssueOnCostCenter ?: false,
        isScrapIssue = isScrapIssue ?: false,
        isIssueFinishedGoodsPlastic = isIssueFinishedGoodsPlastic ?: false,
        isIssueFinishedGoodsFeed = isIssueFinishedGoodsFeed ?: false,
        isIssueTransferOrder = isIssueTransferOrder ?: false,
        isPhysicalCountView = isPhysicalCountView ?: false,
        isPhysicalCountSubmit = isPhysicalCountSubmit ?: false,
        erpMode = erpMode ?: "",
        requiresErpCredentials = requiresErpCredentials ?: false
    )
}

fun Permissions.Companion.default(): Permissions {
    return Permissions(
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, "", false
    )
}
