package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CarNumberDto (
    @SerializedName("carNumberId"    ) var carNumberId    : Int?    = null,
    @SerializedName("carNumber"      ) var carNumber      : String? = null,
    @SerializedName("carDescription" ) var carDescription : String? = null
)