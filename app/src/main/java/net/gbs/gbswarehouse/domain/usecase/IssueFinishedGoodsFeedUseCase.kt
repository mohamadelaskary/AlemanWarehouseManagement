package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.data.remote.dto.request.IssueFinishedGoodsFeedBodyDto
import net.gbs.gbswarehouse.domain.model.FinishedGoodsFeedItem
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class IssueFinishedGoodsFeedUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: IssueFinishedGoodsFeedBodyDto): NetworkResult<List<FinishedGoodsFeedItem>> = 
        repository.issueFinishedGoodsFeed(request)
}
