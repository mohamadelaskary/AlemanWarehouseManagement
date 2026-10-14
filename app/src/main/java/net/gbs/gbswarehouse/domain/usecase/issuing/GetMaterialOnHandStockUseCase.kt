package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetMaterialOnHandStockUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(
        storageBinCode: String,
        materialCode: String? = null
    ): NetworkResult<List<MaterialOnHandStock>> {
        return repository.getMaterialOnHandStock(storageBinCode, materialCode)
    }
}
