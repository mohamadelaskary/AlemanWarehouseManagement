package net.gbs.gbswarehouse.core.network

import android.content.ContentValues.TAG
import android.util.Log
import net.gbs.gbswarehouse.data.remote.dto.response.BaseResponse
import net.gbs.gbswarehouse.data.remote.dto.response.ResponseStatus
import retrofit2.Response
import java.io.IOException

suspend inline fun <T : BaseResponse> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>
): NetworkResult<T> {

    return try {

        val response = apiCall()

        if (!response.isSuccessful) {
            Log.e(TAG, "safeApiCallNetworkException: ")
            return NetworkResult.HttpError(
                code = response.code(),
                message = response.errorBody()?.string()
                    ?: response.message()
            )
        }

        val body = response.body()
            ?: return NetworkResult.UnknownError("Response body is null")

        val status = body.responseStatus

        if (!status.isSuccess) {
            return NetworkResult.BusinessError(
                message = status.errorMessage.toString(),

            )
            Log.e(TAG, "safeApiCallNetworkException: ")
        }

        NetworkResult.Success(
            data = body,
            message = status.statusMessage
        )

    } catch (e: IOException) {
        Log.e(TAG, "safeApiCallIOException: ", e)
        NetworkResult.NetworkError(
            e.message ?: "Network Error"
        )

    } catch (e: Exception) {
        Log.e(TAG, "safeApiCallUnknownError: ", e)
        NetworkResult.UnknownError(
            e.message ?: "Unknown Error"
        )
    }
}