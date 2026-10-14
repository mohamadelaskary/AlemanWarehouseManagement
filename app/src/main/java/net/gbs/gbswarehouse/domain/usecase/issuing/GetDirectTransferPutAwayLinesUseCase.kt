package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.DirectTransferPutAwayLine
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetDirectTransferPutAwayLinesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(receiptNo: String): NetworkResult<List<DirectTransferPutAwayLine>> {
        return repository.getDirectTransferPutAwayLines(receiptNo)
    }
}
