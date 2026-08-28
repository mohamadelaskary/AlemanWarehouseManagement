package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class CarNumber(
    @SerializedName("carNumberId"    ) var carNumberId    : Int?    = null,
    @SerializedName("carNumber"      ) var carNumber      : String? = null,
    @SerializedName("carDescription" ) var carDescription : String? = null
)
