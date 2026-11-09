package net.gbs.gbswarehouse.data.remote.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.network.safeApiCall
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.core.update.AppUpdateManager
import net.gbs.gbswarehouse.data.local.LoginManager
import net.gbs.gbswarehouse.data.local.TokenManager
import net.gbs.gbswarehouse.data.remote.api.AuthenticationApiService
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.data.remote.mapper.toPermissions
import net.gbs.gbswarehouse.domain.model.Permissions
import net.gbs.gbswarehouse.domain.repository.AuthenticationRepository
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val api: AuthenticationApiService,
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val loginManager: LoginManager,
    private val appUpdateManager: AppUpdateManager
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
            sessionManager.authenticated()
            loginManager.saveUserData(
                userId = result.data.data.id ?: "",
                userPlantCodes = result.data.data.plantCodes ?: result.data.data.companyCodes ?: emptyList(),
                permissions = result.data.data.permissions?.toPermissions() ?: Permissions.default()
            )

            try {
                android.util.Log.d("AuthenticationRepository", "Processing appVersion from login: ${result.data.data.appVersion}")
                appUpdateManager.processVersionDto(result.data.data.appVersion)
            } catch (e: Exception) {
                android.util.Log.e("AuthenticationRepository", "Error processing appVersion: ${e.message}", e)
            }
        }

        return result
    }
}
