package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SAPWarehouse
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetPlantWarehousesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(plantCode: String): NetworkResult<List<SAPWarehouse>> {
        return repository.getPlantWarehouses(plantCode)
    }
}
