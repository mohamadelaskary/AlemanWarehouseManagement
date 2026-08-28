package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetMaterialStockUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(
        plantCode: String,
        materialCode: String,
        storageLocationCode: String? = null
    ): NetworkResult<List<SAPMaterialBatchValuationStock>> {
        return repository.getMaterialStock(plantCode, materialCode, storageLocationCode)
    }
}
