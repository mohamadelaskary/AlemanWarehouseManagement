package net.gbs.gbswarehouse.domain.usecase.issuing

data class IssuingUseCases(
    val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    val getMaterialStockUseCase: GetMaterialStockUseCase,
    val getMaterialOnHandStockUseCase: GetMaterialOnHandStockUseCase,
    val getScrapEligibleStockUseCase: GetScrapEligibleStockUseCase,
    val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase,
    val getTransferToProductionReservationsUseCase: GetTransferToProductionReservationsUseCase,
    val getTransferToProductionReservationLinesUseCase: GetTransferToProductionReservationLinesUseCase,
    val issueTransferToProductionUseCase: IssueTransferToProductionUseCase,
    val getSparePartsToMaintenanceReservationsUseCase: GetSparePartsToMaintenanceReservationsUseCase,
    val getSparePartsToMaintenanceReservationLinesUseCase: GetSparePartsToMaintenanceReservationLinesUseCase,
    val issueSparePartsToMaintenanceUseCase: IssueSparePartsToMaintenanceUseCase,
    val getIssueToInternalOrderReservationsUseCase: GetIssueToInternalOrderReservationsUseCase,
    val getIssueToInternalOrderReservationLinesUseCase: GetIssueToInternalOrderReservationLinesUseCase,
    val issueToInternalOrderUseCase: IssueToInternalOrderUseCase,
    val directTransferUseCase: DirectTransferUseCase,
    val getDirectIssueOnCostCenterReservationsUseCase: GetDirectIssueOnCostCenterReservationsUseCase,
    val getDirectIssueOnCostCenterReservationLinesUseCase: GetDirectIssueOnCostCenterReservationLinesUseCase,
    val directIssueOnCostCenterUseCase: DirectIssueOnCostCenterUseCase,
    val getTransferOrdersUseCase: GetTransferOrdersUseCase,
    val getTransferOrderDataUseCase: GetTransferOrderDataUseCase,
    val issueTransferOrderUseCase: IssueTransferOrderUseCase,
    val getDirectTransferPutAwayDocumentsUseCase: GetDirectTransferPutAwayDocumentsUseCase,
    val getDirectTransferPutAwayLinesUseCase: GetDirectTransferPutAwayLinesUseCase,
    val confirmDirectTransferPutAwayUseCase: ConfirmDirectTransferPutAwayUseCase
)
