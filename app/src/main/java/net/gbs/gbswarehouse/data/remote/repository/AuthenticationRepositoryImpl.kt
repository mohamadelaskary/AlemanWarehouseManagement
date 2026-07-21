package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.data.local.TokenManager
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.domain.repository.AuthenticationRepository
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : AuthenticationRepository  {
    override suspend fun login(
        userName: String,
        password: String
    ): NetworkResult<LoginResponse>
        {

        val result = safeApiCall {
            api.login(userName, password)
        }

        if (result is NetworkResult.Success) {

            tokenManager.saveTokens(
                accessToken = result.data.data.accessToken?:"",
                refreshToken = result.data.data.refreshToken?:""
            )
        }

        return result
    }
}