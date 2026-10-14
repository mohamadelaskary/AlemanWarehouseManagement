package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.DirectIssueReservationLine
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetDirectIssueOnCostCenterReservationLinesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(reservationNumber: String): NetworkResult<List<DirectIssueReservationLine>> {
        return repository.getDirectIssueOnCostCenterReservationLines(reservationNumber)
    }
}
