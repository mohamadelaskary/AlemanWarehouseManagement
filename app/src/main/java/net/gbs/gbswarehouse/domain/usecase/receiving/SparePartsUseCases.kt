package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.request.GetSparePartsPurchaseOrderDataRequest
import net.gbs.gbswarehouse.data.remote.dto.request.PurchaseOrderRequestDto
import net.gbs.gbswarehouse.data.remote.dto.request.ReceiveVendorSparePartsRequestDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllSparePartsPurchaseOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getReceivingSparePartsFromVendorOrdersList()
}

class GetSparePartsPurchaseOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: GetSparePartsPurchaseOrderDataRequest) = 
        repository.getReceivingSparePartsFromVendorOrderData(request)
}

class ReceiveVendorSparePartsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveVendorSparePartsRequestDto) = repository.receiveVendorSpareParts(request)
}
