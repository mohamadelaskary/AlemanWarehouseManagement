package net.gbs.gbswarehouse.data.remote.dto.response

data class ResponseStatus(
    val isSuccess: Boolean,
    val statusMessage: String,
    val errorMessage: Any?,
)
