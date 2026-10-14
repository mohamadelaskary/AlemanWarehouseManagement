package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveTransitOrderMaterialDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllTransitOrderMaterialPurchaseOrdersByCarNumberUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllTransitOrderMaterialPurchaseOrdersByCarNumber()
}

class GetTransitOrderMaterialPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(carNo: String) = repository.getTransitOrderMaterialPurchaseOrderData(carNo)
}

class ReceiveTransitOrderMaterialUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveTransitOrderMaterialDto) = repository.receiveTransitOrderMaterial(request)
}
