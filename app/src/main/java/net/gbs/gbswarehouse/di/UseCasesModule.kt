package net.gbs.gbswarehouse.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectIssueOnCostCenterUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.DirectTransferUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectIssueOnCostCenterReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectIssueOnCostCenterReservationsUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetIssueToInternalOrderReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetIssueToInternalOrderReservationsUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetMaterialStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetScrapEligibleStockUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetSparePartsToMaintenanceReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetSparePartsToMaintenanceReservationsUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetTransferToProductionReservationLinesUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.GetTransferToProductionReservationsUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.IssueSparePartsToMaintenanceUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.IssueToInternalOrderUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.IssueTransferToProductionUseCase
import net.gbs.gbswarehouse.domain.usecase.issuing.IssuingUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun provideIssuingUseCases(repository: IssuingRepository): IssuingUseCases {
        return IssuingUseCases(
            getPlantWarehousesUseCase = GetPlantWarehousesUseCase(repository),
            getMaterialStockUseCase = GetMaterialStockUseCase(repository),
            getScrapEligibleStockUseCase = GetScrapEligibleStockUseCase(repository),
            getStorageBinLocationIssuingUseCase = GetStorageBinLocationIssuingUseCase(repository),
            getTransferToProductionReservationsUseCase = GetTransferToProductionReservationsUseCase(
                repository
            ),
            getTransferToProductionReservationLinesUseCase = GetTransferToProductionReservationLinesUseCase(
                repository
            ),
            issueTransferToProductionUseCase = IssueTransferToProductionUseCase(repository),
            getSparePartsToMaintenanceReservationsUseCase = GetSparePartsToMaintenanceReservationsUseCase(
                repository
            ),
            getSparePartsToMaintenanceReservationLinesUseCase = GetSparePartsToMaintenanceReservationLinesUseCase(
                repository
            ),
            issueSparePartsToMaintenanceUseCase = IssueSparePartsToMaintenanceUseCase(repository),
            getIssueToInternalOrderReservationsUseCase = GetIssueToInternalOrderReservationsUseCase(
                repository
            ),
            getIssueToInternalOrderReservationLinesUseCase = GetIssueToInternalOrderReservationLinesUseCase(
                repository
            ),
            issueToInternalOrderUseCase = IssueToInternalOrderUseCase(repository),
            directTransferUseCase = DirectTransferUseCase(repository),
            getDirectIssueOnCostCenterReservationsUseCase = GetDirectIssueOnCostCenterReservationsUseCase(
                repository
            ),
            getDirectIssueOnCostCenterReservationLinesUseCase = GetDirectIssueOnCostCenterReservationLinesUseCase(
                repository
            ),
            directIssueOnCostCenterUseCase = DirectIssueOnCostCenterUseCase(repository)
        )
    }
}
