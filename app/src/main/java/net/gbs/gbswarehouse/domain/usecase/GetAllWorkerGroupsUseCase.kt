package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllWorkerGroupsUseCase @Inject constructor(
    private val repository: ReceivingRepository
) {

    suspend operator fun invoke() =
        repository.getAllWorkerGroups()

}