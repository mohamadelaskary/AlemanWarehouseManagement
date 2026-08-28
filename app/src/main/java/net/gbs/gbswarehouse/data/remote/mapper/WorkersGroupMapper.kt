package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.WorkersGroupDto
import net.gbs.gbswarehouse.domain.model.WorkersGroup

fun WorkersGroupDto.toWorkersGroup(): WorkersGroup {
    return WorkersGroup(
        workerGroupId = workerGroupId,
        workerGroupCode = workerGroupCode,
        workerGroupDescription = workerGroupDescription,
    )
}

fun List<WorkersGroupDto>.toWorkersGroups(): List<WorkersGroup> {
    return map { it.toWorkersGroup() }
}
