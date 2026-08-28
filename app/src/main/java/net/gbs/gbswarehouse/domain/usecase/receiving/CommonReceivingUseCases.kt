package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetPlantWarehousesUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(plantCode: String) = repository.getPlantWarehouses(plantCode)
}

class GetAllWorkerGroupsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllWorkerGroups()
}

class GetAllLoadingVendorsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllLoadingVendors()
}

class GetAllCarNumbersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllCarNumbers()
}

class GetAllCarTypesUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllCarTypes()
}

class GetNaolonPerTonValueUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(plantCode: String, naolonDescription: String, carType: String) = repository.getNaolonPerTonValue(plantCode, naolonDescription, carType)
}

class GetAllNaolonDescriptionsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllNaolonDescriptions()
}

class GetAllPackageTypesUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllPackageTypes()
}
