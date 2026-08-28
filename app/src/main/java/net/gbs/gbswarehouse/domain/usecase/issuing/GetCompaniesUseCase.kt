package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.Company
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Company>> {
        return repository.getCompanies()
    }
}
