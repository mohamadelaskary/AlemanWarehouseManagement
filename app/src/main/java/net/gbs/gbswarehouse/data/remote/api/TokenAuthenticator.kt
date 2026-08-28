package net.gbs.gbswarehouse.data.remote.api

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.data.local.TokenManager
import net.gbs.gbswarehouse.data.remote.dto.request.RefreshTokenBody
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val refreshTokenApi : AuthenticationApiService
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        // لو الـ Refresh API نفسها رجعت 401
        // يبقى متعملش Refresh تاني
        if (response.request.url.encodedPath.contains("refreshToken")) {

            tokenManager.clear()

            // TODO Notify Session Expired

            return null
        }

        val refreshToken =
            tokenManager.getRefreshToken()
                ?: return null

        return runBlocking {

            mutex.withLock {

                val currentAccessToken =
                    tokenManager.getAccessToken()

                val requestAccessToken =
                    response.request.header("Authorization")

                // لو حد عمل Refresh قبلي
                if (requestAccessToken != "Bearer $currentAccessToken") {

                    return@withLock response.request
                        .newBuilder()
                        .header(
                            "Authorization",
                            "Bearer $currentAccessToken"
                        )
                        .build()
                }

                val refreshResponse =
                    refreshTokenApi.refreshToken(
                        RefreshTokenBody(refreshToken)
                    )

                if (!refreshResponse.isSuccessful) {

                    tokenManager.clear()

                    sessionManager.sessionExpired()

                    return@withLock null
                }

                val body =
                    refreshResponse.body()
                        ?: return@withLock null

                tokenManager.saveTokens(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken
                )

                response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${body.accessToken}"
                    )
                    .build()
            }
        }
    }
}