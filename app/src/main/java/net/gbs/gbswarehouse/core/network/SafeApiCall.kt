package net.gbs.gbswarehouse.core.network

import android.util.Log
import kotlinx.coroutines.CancellationException
import net.gbs.gbswarehouse.data.remote.dto.response.BaseResponse
import retrofit2.Response
import java.io.IOException

suspend inline fun <T : BaseResponse> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    val tag = "SafeApiCall"

    return try {

        val response = apiCall()

        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: response.message()
            Log.e(tag, "safeApiCallHttpError: code=${response.code()}, message=$errorMsg")
            return NetworkResult.HttpError(
                code = response.code(),
                message = errorMsg
            )
        }

        val body = response.body()
            ?: return NetworkResult.UnknownError("Response body is null")

        val status = body.responseStatus

        if (!status.isSuccess) {
            Log.e(tag, "safeApiCallBusinessError: ${status.errorMessage}")
            return NetworkResult.BusinessError(
                message = status.errorMessage.toString()
            )
        }

        NetworkResult.Success(
            data = body,
            message = status.statusMessage
        )

    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Log.e(tag, "safeApiCallIOException: ${e.message}", e)
        NetworkResult.NetworkError(
            e.message ?: "Network Error"
        )

    } catch (e: Exception) {
        Log.e(tag, "safeApiCallUnknownError: ${e.message}", e)
        NetworkResult.UnknownError(
            e.message ?: "Unknown Error"
        )
    }
}