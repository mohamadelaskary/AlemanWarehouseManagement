package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetStorageBinLocationIssuingUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(storageBinCode: String): NetworkResult<StorageBinLocation> {
        return repository.getStorageBinLocation(storageBinCode)
    }
}
