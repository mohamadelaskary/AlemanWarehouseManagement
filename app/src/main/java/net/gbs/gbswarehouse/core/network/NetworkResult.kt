package net.gbs.gbswarehouse.core.network

sealed class NetworkResult<out T> {

    data class Success<T>(
        val data: T,
        val message: String? = null
    ) : NetworkResult<T>()

    data class BusinessError(
        val message: String
    ) : NetworkResult<Nothing>()

    data class HttpError(
        val code: Int,
        val message: String
    ) : NetworkResult<Nothing>()

    data class NetworkError(
        val message: String
    ) : NetworkResult<Nothing>()
    data class UnknownError(
        val message: String
    ) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    fun <R> map(mapper: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(mapper(data), message)
            is BusinessError -> BusinessError(message)
            is HttpError -> HttpError(code, message)
            is NetworkError -> NetworkError(message)
            is UnknownError -> UnknownError(message)
            is Loading -> Loading
        }
    }
}
