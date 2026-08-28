package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveVendorPoultryMaterialsDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllPoultryMaterialsPurchaseOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllPoultryMaterialsPurchaseOrders()
}

class GetPoultryMaterialsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(poNumber: String) = repository.getPoultryMaterialsPurchaseOrderData(poNumber)
}

class ReceivePoultryMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveVendorPoultryMaterialsDto) = repository.receivePoultryMaterials(request)
}
