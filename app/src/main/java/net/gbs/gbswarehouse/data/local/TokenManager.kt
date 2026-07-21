package net.gbs.gbswarehouse.data.local

interface TokenManager {

    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun saveTokens(
        accessToken: String,
        refreshToken: String
    )

    fun clear()
}