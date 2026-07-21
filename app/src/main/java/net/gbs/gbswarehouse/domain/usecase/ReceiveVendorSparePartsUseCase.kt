package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.data.remote.mapper.toReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.domain.model.ReceiveVendorSparePartsRequest
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class ReceiveVendorSparePartsUseCase @Inject constructor(
    private val repository: ReceivingRepository
) {

    suspend operator fun invoke(
        request: ReceiveVendorSparePartsRequest
    ) =
        repository.receiveVendorSpareParts(request.toReceiveVendorSparePartsRequestDto())

}