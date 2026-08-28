package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class PackageType(
    @SerializedName("packTypeId"          ) var packTypeId          : Int?    = null,
    @SerializedName("packTypeCode"        ) var packTypeCode        : String? = null,
    @SerializedName("packTypeDescription" ) var packTypeDescription : String? = null
)
