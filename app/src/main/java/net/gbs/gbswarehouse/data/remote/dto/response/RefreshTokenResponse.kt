package net.gbs.gbswarehouse.data.remote.dto.response

data class RefreshTokenResponse (
    val success : Boolean,
    val accessToken: String,
    val refreshToken: String,
)


