package net.gbs.gbswarehouse.domain.usecase.issuing

data class IssuingUseCases(
    val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    val getMaterialStockUseCase: GetMaterialStockUseCase,
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
    val directIssueOnCostCenterUseCase: DirectIssueOnCostCenterUseCase
)
