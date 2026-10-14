package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.LoadingOrderDto
import net.gbs.gbswarehouse.data.remote.dto.response.LoadingOrderLineDto
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem

/**
 * Mapping for `api/Conveyors/GetLoadingOrders`.
 *
 * suggestedBags -> requiredQuantity, loadedBags -> loadedQuantity,
 * materialDescription -> item name, carNo -> vehicle number.
 */
fun LoadingOrderDto.toDomain(): LoadingOrder = LoadingOrder(
    id = goodsReceiptId,
    orderNumber = receiptNo,
    vehicleNumber = carNo,
    items = lines.map { it.toDomain() },
    status = status,
    shippingPointCode = shippingPointCode,
    workerGroupCode = workerGroupCode,
    createdAt = createdAt,
    orderType = orderType
)

fun LoadingOrderLineDto.toDomain(): OrderItem = OrderItem(
    id = goodsReceiptLineId,
    name = materialDescription ?: materialCode.orEmpty(),
    requiredQuantity = suggestedBags,
    loadedQuantity = loadedBags,
    salesOrder = salesOrder,
    salesOrderItem = salesOrderItem,
    materialCode = materialCode,
    itemText = itemText,
    quantity = quantity,
    uom = uom,
    plantCode = plantCode,
    bagLengthMm = bagLengthMm,
    bagWeightKg = bagWeightKg,
    remainingSuggestedBags = remainingSuggestedBags
)
