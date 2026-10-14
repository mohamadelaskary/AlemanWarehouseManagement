package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.TransferOrder
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class GetTransferOrderDataUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(carNo: String): NetworkResult<TransferOrder> {
        return repository.getTransferOrderDataByCarNumber(carNo)
    }
}
