package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectIssueOnCostCenterDto
import net.gbs.gbswarehouse.domain.model.PostDirectIssueOnCostCenterResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class DirectIssueOnCostCenterUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: PostDirectIssueOnCostCenterDto): NetworkResult<PostDirectIssueOnCostCenterResult> {
        return repository.directIssueOnCostCenter(request)
    }
}
