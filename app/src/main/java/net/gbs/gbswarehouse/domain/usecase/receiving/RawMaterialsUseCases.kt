package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveRawMaterialsDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllRawMaterialsPurchaseOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllRawMaterialsPurchaseOrders()
}

class GetRawMaterialsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(poNumber: String) = repository.getRawMaterialsPurchaseOrderData(poNumber)
}

class ReceiveRawMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveRawMaterialsDto) = repository.receiveRawMaterials(request)
}
