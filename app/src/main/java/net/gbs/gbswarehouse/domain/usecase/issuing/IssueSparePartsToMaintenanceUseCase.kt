package net.gbs.gbswarehouse.domain.usecase.issuing

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.PostSparePartsIssueToMaintenanceDto
import net.gbs.gbswarehouse.domain.model.PostSparePartsIssueToMaintenanceResult
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import javax.inject.Inject

class IssueSparePartsToMaintenanceUseCase @Inject constructor(
    private val repository: IssuingRepository
) {
    suspend operator fun invoke(request: PostSparePartsIssueToMaintenanceDto): NetworkResult<PostSparePartsIssueToMaintenanceResult> {
        return repository.issueSparePartsToMaintenance(request)
    }
}
