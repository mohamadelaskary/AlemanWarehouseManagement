package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveRawMaterialsDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllRawMaterialsPurchaseOrdersByCarNumberUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllRawMaterialsPurchaseOrdersByCarNumber()
}

class GetRawMaterialsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(carNo: String) = repository.getRawMaterialsPurchaseOrderData(carNo)
}

class ReceiveRawMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveRawMaterialsDto) = repository.receiveRawMaterials(request)
}
