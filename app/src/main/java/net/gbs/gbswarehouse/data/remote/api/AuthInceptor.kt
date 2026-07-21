package net.gbs.gbswarehouse.data.remote.api

import net.gbs.gbswarehouse.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject



class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val accessToken = tokenManager.getAccessToken()

        val newRequest = originalRequest.newBuilder()

        if (!accessToken.isNullOrEmpty()) {
            newRequest.addHeader(
                "Authorization",
                "Bearer $accessToken"
            )
        }

        return chain.proceed(
            newRequest.build()
        )
    }
}