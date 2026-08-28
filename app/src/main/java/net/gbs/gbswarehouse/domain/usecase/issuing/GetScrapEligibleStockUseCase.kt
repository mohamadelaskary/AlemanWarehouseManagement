package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SAPMaterialBatchValuationStock
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetScrapEligibleStockUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<SAPMaterialBatchValuationStock>> {
        return repository.getScrapEligibleStock()
    }
}
