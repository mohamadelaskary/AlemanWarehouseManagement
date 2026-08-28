package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.PostTransferToProductionDto
import net.gbs.gbswarehouse.domain.model.PostTransferToProductionResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class IssueTransferToProductionUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: PostTransferToProductionDto): NetworkResult<PostTransferToProductionResult> {
        return repository.issueTransferToProduction(request)
    }
}
