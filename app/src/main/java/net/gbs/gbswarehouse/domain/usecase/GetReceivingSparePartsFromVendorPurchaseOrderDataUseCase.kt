package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.data.remote.dto.request.GetSparePartsPurchaseOrderDataRequest
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetReceivingSparePartsFromVendorPurchaseOrderDataUseCase @Inject constructor(
    val repository: ReceivingRepository
){
    suspend operator fun invoke (
        poNumber: String
    ) = repository.getReceivingSparePartsFromVendorOrderData(
            GetSparePartsPurchaseOrderDataRequest(
                poNumber
            )
        )
}