package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse (
    val success : Boolean,
    val accessToken: String,
    val refreshToken: String,
    @SerializedName("appVersion", alternate = ["AppVersion", "appversion", "Appversion"])
    val appVersion: AppVersionDto? = null
)
