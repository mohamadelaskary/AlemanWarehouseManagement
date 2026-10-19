package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.MaterialBinLocation
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetMaterialBinLocationsUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(
        plantCode: String,
        warehouseCode: String,
        materialCode: String,
        valuationType: String
    ): NetworkResult<List<MaterialBinLocation>> {
        return repository.getMaterialBinLocations(plantCode, warehouseCode, materialCode, valuationType)
    }
}
