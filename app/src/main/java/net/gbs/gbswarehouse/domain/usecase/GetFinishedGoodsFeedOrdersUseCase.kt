package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetFinishedGoodsFeedOrdersUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke() = repository.getFinishedGoodsFeedOrdersByCarNumber()
}
