package net.gbs.gbswarehouse.data.remote.dto.response

class PlantWarehousesResponseDto (
    override val responseStatus: ResponseStatus,
    val data: List<WarehouseDto>,
) : BaseResponse

data class WarehouseDto(
    val warehouseId: Long,
    val warehouseCode: String,
    val warehouseDescription: String,
    val plantCode: String,
)
