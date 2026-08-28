package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.PostDirectTransferDto
import net.gbs.gbswarehouse.domain.model.PostDirectTransferResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class DirectTransferUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: PostDirectTransferDto): NetworkResult<PostDirectTransferResult> {
        return repository.directTransfer(request)
    }
}
