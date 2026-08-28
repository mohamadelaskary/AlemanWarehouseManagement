package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.PostIssueToInternalOrderDto
import net.gbs.gbswarehouse.domain.model.PostIssueToInternalOrderResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class IssueToInternalOrderUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: PostIssueToInternalOrderDto): NetworkResult<PostIssueToInternalOrderResult> {
        return repository.issueToInternalOrder(request)
    }
}
