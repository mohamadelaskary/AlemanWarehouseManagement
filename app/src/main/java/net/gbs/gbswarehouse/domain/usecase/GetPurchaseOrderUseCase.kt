package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.data.remote.mapper.toPurchaseOrderRequestDto
import net.gbs.gbswarehouse.domain.model.PurchaseOrderRequest
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetPurchaseOrderUseCase @Inject constructor(
    private val repository: ReceivingRepository
) {

    suspend operator fun invoke(
        request: PurchaseOrderRequest
    ) =
        repository.getPurchaseOrderData(request.toPurchaseOrderRequestDto())

}