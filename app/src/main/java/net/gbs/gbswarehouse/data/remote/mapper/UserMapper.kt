package net.gbs.gbswarehouse.data.remote.mapper

import net.gbs.gbswarehouse.data.remote.dto.response.UserDto
import net.gbs.gbswarehouse.domain.model.User

fun UserDto.toUser(): User {
    return User(
        id = id ?: "",
        userName = userName ?: "",
        email = email ?: "",
        companyCodes = companyCodes ?: emptyList(),
    )
}