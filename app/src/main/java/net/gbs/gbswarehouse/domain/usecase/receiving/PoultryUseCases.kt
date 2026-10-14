package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveVendorPoultryMaterialsDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllPoultryMaterialsPurchaseOrdersByCarNumber()
}

typealias GetAllPoultryMaterialsPurchaseOrdersUseCase = GetAllPoultryMaterialsPurchaseOrdersByCarNumberUseCase

class GetPoultryMaterialsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(carNo: String) = repository.getPoultryMaterialsPurchaseOrderData(carNo)
}

class ReceivePoultryMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveVendorPoultryMaterialsDto) = repository.receivePoultryMaterials(request)
}
