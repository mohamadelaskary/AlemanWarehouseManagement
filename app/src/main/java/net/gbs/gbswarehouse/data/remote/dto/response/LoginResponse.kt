package net.gbs.gbswarehouse.data.remote.dto.response

data class LoginResponse (
    override val responseStatus: ResponseStatus,
    val data: UserDto,
) : BaseResponse

data class UserDto(
    val id: String,
    val userName: String,
    val email: String,
    val companyCodes: List<String>,
    val accessToken: String?,
    val refreshToken: String?,
)