package net.gbs.gbswarehouse.core.network

sealed class NetworkResult<out T> {

    data class Success<T>(
        val data: T,
        val message: String? = null
    ) : NetworkResult<T>()

    data class Error(
        val message: String
    ) : NetworkResult<Nothing>()

    object Loading : NetworkResult<Nothing>()
}