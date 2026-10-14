package net.gbs.gbswarehouse.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.domain.usecase.loading.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoadingModule {

    @Provides
    @Singleton
    fun provideLoadingUseCases(
        getLoadingOrders: GetLoadingOrdersUseCase,
        getLoadingOrderDetails: GetLoadingOrderDetailsUseCase,
        getCachedLoadingOrders: GetCachedLoadingOrdersUseCase,
        observeConveyors: ObserveConveyorsUseCase,
        getConveyors: GetConveyorsUseCase,
        observeLoadingOperations: ObserveLoadingOperationsUseCase,
        observeLoadingEvents: ObserveLoadingEventsUseCase,
        observeStreamStatus: ObserveStreamStatusUseCase,
        observeOperation: ObserveOperationUseCase,
        startLoading: StartLoadingUseCase,
        pauseLoading: PauseLoadingUseCase,
        resumeLoading: ResumeLoadingUseCase,
        manualStartConveyor: ManualStartConveyorUseCase,
        manualStopConveyor: ManualStopConveyorUseCase,
        addOnePack: AddOnePackUseCase,
        reportLoadingFailure: ReportLoadingFailureUseCase,
        getLoadingOperation: GetLoadingOperationUseCase,
        getActiveLoadingOperation: GetActiveLoadingOperationUseCase,
        resetConveyorFault: ResetConveyorFaultUseCase,
        getConveyorSettings: GetConveyorSettingsUseCase,
        updateConveyorSettings: UpdateConveyorSettingsUseCase,
        getBagTypes: GetBagTypesUseCase,
        getLoadingRunsReport: GetLoadingRunsReportUseCase
    ) = LoadingUseCases(
        getLoadingOrders = getLoadingOrders,
        getLoadingOrderDetails = getLoadingOrderDetails,
        getCachedLoadingOrders = getCachedLoadingOrders,
        observeConveyors = observeConveyors,
        getConveyors = getConveyors,
        observeLoadingOperations = observeLoadingOperations,
        observeLoadingEvents = observeLoadingEvents,
        observeStreamStatus = observeStreamStatus,
        observeOperation = observeOperation,
        startLoading = startLoading,
        pauseLoading = pauseLoading,
        resumeLoading = resumeLoading,
        manualStartConveyor = manualStartConveyor,
        manualStopConveyor = manualStopConveyor,
        addOnePack = addOnePack,
        reportLoadingFailure = reportLoadingFailure,
        getLoadingOperation = getLoadingOperation,
        getActiveLoadingOperation = getActiveLoadingOperation,
        resetConveyorFault = resetConveyorFault,
        getConveyorSettings = getConveyorSettings,
        updateConveyorSettings = updateConveyorSettings,
        getBagTypes = getBagTypes,
        getLoadingRunsReport = getLoadingRunsReport
    )
}
