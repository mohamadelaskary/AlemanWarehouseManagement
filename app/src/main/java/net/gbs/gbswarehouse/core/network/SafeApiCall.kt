package net.gbs.gbswarehouse.core.network

import net.gbs.gbswarehouse.data.remote.dto.response.BaseResponse
import net.gbs.gbswarehouse.data.remote.dto.response.ResponseStatus
import retrofit2.Response

suspend inline fun <T: BaseResponse> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>
): NetworkResult<T> {

    return try {

        val response = apiCall()

        if (response.isSuccessful) {

            response.body()?.let {
                    val responseStatus = it.responseStatus
                if (responseStatus.isSuccess) {
                    NetworkResult.Success(it, responseStatus.statusMessage)

                } else
                    NetworkResult.Error(responseStatus.statusMessage)
            } ?: NetworkResult.Error("Empty Body")

        } else {

            NetworkResult.Error(
                response.errorBody()?.string()
                    ?: response.message()
            )

        }

    } catch (e: Exception) {

        NetworkResult.Error(
            e.message ?: "Unknown Error"
        )
    }
}