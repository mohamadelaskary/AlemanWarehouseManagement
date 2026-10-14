package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import net.gbs.gbswarehouse.data.remote.dto.request.CreateBlindReceiveRequestDto
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

class SearchBlindReceiveMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(filter: String, pageSize: Int = 30) = repository.searchBlindReceiveMaterials(filter, pageSize)
}

class GetBlindReceiveUomsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getBlindReceiveUoms()
}

class GetBlindReceiveQualityDecisionsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getBlindReceiveQualityDecisions()
}

class GetUserPlantsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getUserPlants()
}

class CreateBlindReceiveUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: CreateBlindReceiveRequestDto) = repository.createBlindReceive(request)
}

