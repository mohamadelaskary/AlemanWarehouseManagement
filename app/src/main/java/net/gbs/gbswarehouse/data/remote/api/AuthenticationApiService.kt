package net.gbs.gbswarehouse.data.remote.api

import net.gbs.gbswarehouse.data.remote.dto.request.RefreshTokenBody
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.data.remote.dto.response.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticationApiService {
    @POST("/api/AccountHH/RefreshToken")
    suspend fun refreshToken(
        @Body refreshToken: RefreshTokenBody,
    ): Response<RefreshTokenResponse>
    @POST("/api/AccountHH/Login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<LoginResponse>
}