package net.gbs.gbswarehouse.domain.repository

import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.data.remote.dto.response.PlantWarehousesResponseDto
import net.gbs.gbswarehouse.data.remote.dto.response.UserDto

interface AuthenticationRepository {
    suspend fun login(userName: String, password: String):
            NetworkResult<LoginResponse>
}