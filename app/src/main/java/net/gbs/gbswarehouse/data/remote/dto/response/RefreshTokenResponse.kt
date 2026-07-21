package net.gbs.gbswarehouse.data.remote.dto.response

data class RefreshTokenResponse (
    override val responseStatus: ResponseStatus,
    val data: RefreshTokenDto,
) : BaseResponse

data class RefreshTokenDto (
    val accessToken: String,
    val refreshToken: String,
)

