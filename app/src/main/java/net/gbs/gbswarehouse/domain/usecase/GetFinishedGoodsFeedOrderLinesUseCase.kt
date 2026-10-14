package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetFinishedGoodsFeedOrderLinesUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(carNo: String) =
        repository.getFinishedGoodsFeedOrderLinesByCarNumber(carNo)
}
