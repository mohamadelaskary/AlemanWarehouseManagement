package net.gbs.gbswarehouse.data.remote.dto.response

data class WorkerGroupsResponseDto (
    override val responseStatus: ResponseStatus,
    val data: List<WorkersGroupDto>,
) : BaseResponse
data class WorkersGroupDto(
    val workerGroupId: Long,
    val workerGroupCode: String,
    val workerGroupDescription: String,
)

