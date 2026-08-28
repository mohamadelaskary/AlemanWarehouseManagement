package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsLineDto
import net.gbs.gbswarehouse.data.remote.dto.response.SparePartsReturnLineDto
import net.gbs.gbswarehouse.domain.model.SparePartsReturnLine

fun SparePartsReturnLineDto.toModel() = SparePartsReturnLine(
    materialCode = materialCode,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    uom = uom,
    receivedQuantity = receivedQuantity,
    reservationNumber = reservationNumber,
    reservationItem = reservationItem,
    returnReason = returnReason,
    valuationType = valuationType,
    itemText = itemText,
    recipientName = goodsRecipient,
)

fun SparePartsReturnLine.toDto() = SparePartsReturnLineDto(
    materialCode = materialCode,
    plantCode = plantCode,
    warehouseCode = warehouseCode,
    uom = uom,
    receivedQuantity = receivedQuantity,
    reservationNumber = reservationNumber,
    reservationItem = reservationItem,
    returnReason = returnReason,
    valuationType = valuationType,
    itemText = itemText,
    goodsRecipient = recipientName,
)

fun List<SparePartsReturnLineDto>.toModel() = map { it.toModel() }
fun List<SparePartsReturnLine>.toDto() = map { it.toDto() }

