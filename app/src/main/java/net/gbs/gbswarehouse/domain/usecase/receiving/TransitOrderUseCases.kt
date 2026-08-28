package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveTransitOrderMaterialDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllTransitOrderMaterialPurchaseOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllTransitOrderMaterialPurchaseOrders()
}

class GetTransitOrderMaterialPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(poNumber: String) = repository.getTransitOrderMaterialPurchaseOrderData(poNumber)
}

class ReceiveTransitOrderMaterialUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveTransitOrderMaterialDto) = repository.receiveTransitOrderMaterial(request)
}
