package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.SparePartsReturnReservationLine
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetIssueToInternalOrderReservationLinesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(reservationNumber: String): NetworkResult<List<SparePartsReturnReservationLine>> {
        return repository.getIssueToInternalOrderReservationLines(reservationNumber)
    }
}
