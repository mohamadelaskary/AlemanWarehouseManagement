package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveFinishedGoodsFeedDto
import net.gbs.gbswarehouse.data.remote.dto.response.ReceiveFinishedGoodsPlasticDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetAllFinishedGoodsPlasticProductionOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllFinishedGoodsPlasticProductionOrders()
}

class GetFinishedGoodsPlasticProductionOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(orderNumber: String) = repository.getFinishedGoodsPlasticProductionOrderData(orderNumber)
}

class ReceiveFinishedGoodsPlasticUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveFinishedGoodsPlasticDto) = repository.receiveFinishedGoodsPlastic(request)
}

class GetAllFinishedGoodsFeedProductionOrdersUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getAllFinishedGoodsFeedProductionOrders()
}

class GetFinishedGoodsFeedProductionOrderDataUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(orderNumber: String) = repository.getFinishedGoodsFeedProductionOrderData(orderNumber)
}

class ReceiveFinishedGoodsFeedUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: ReceiveFinishedGoodsFeedDto) = repository.receiveFinishedGoodsFeed(request)
}
