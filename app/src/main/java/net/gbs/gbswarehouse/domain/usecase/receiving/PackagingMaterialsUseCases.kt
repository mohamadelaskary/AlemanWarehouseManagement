package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceivePackagingMaterialsDto
import net.gbs.gbswarehouse.data.remote.dto.response.ReceivePackagingMaterialsFromDiffPOsDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllPackagingMaterialsPurchaseOrders()
}

typealias GetAllPackagingMaterialsPurchaseOrdersUseCase = GetAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase
typealias GetAllPackagingMaterialsPurchaseOrderByCarNumberUseCase = GetAllPackagingMaterialsPurchaseOrdersByCarNumberUseCase

class GetPackagingMaterialsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(carNo: String) = repository.getPackagingMaterialsPurchaseOrderData(carNo)
}

class ReceivePackagingMaterialsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceivePackagingMaterialsDto) = repository.receivePackagingMaterials(request)
}

class ReceivePackagingMaterialsFromDiffPOsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceivePackagingMaterialsFromDiffPOsDto) = repository.receivePackagingMaterialsFromDiffPOs(request)
}
