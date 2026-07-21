package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.WarehouseDto
import net.gbs.gbswarehouse.domain.model.Warehouse

fun WarehouseDto.toWarehouse(): Warehouse {
    return Warehouse(
        warehouseId = warehouseId,
        warehouseCode = warehouseCode,
        warehouseDescription = warehouseDescription,
        plantCode = plantCode,
    )
}

fun List<WarehouseDto>.toWarehouses():List<Warehouse>{
    return map { it.toWarehouse() }
}

fun Warehouse.toWarehouseDto(): WarehouseDto {
    return WarehouseDto(
        warehouseId = warehouseId,
        warehouseCode = warehouseCode,
        warehouseDescription = warehouseDescription,
        plantCode = plantCode,
    )
}

fun List<Warehouse>.toWarehousesDto():List<WarehouseDto>{
    return map { it.toWarehouseDto() }
}