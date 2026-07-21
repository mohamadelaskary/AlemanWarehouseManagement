package net.gbs.gbswarehouse.domain.usecase

import net.gbs.gbswarehouse.domain.repository.AuthenticationRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(
        userName: String,
        password: String
    ) = repository.login(userName, password)
}