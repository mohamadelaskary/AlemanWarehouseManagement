package net.gbs.gbswarehouse.domain.usecase.receiving

import net.gbs.gbswarehouse.data.remote.dto.response.PostSparePartsReturnDto
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Inject

class GetSparePartsReturnMovementTypeCountsUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke() = repository.getSparePartsReturnMovementTypeCounts()
}

class GetSparePartsReturnReservationsByMovementTypeUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(movementType: String) = repository.getSparePartsReturnReservationsByMovementType(movementType)
}

class GetSparePartsReturnReservationLinesUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(reservationNumber: String) = repository.getSparePartsReturnReservationLines(reservationNumber)
}

class PostSparePartsReturnUseCase @Inject constructor(private val repository: ReceivingRepository) {
    suspend operator fun invoke(request: PostSparePartsReturnDto) = repository.postSparePartsReturn(request)
}
