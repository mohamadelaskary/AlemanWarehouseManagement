package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.IssueTransferOrderDto
import net.gbs.gbswarehouse.domain.model.PostTransferOrderResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class IssueTransferOrderUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: IssueTransferOrderDto): NetworkResult<PostTransferOrderResult> {
        return repository.issueTransferOrder(request)
    }
}
