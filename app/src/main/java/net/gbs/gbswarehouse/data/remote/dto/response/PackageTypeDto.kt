package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PackageTypeDto (
    @SerializedName("packTypeId"          ) var packTypeId          : Int?    = null,
    @SerializedName("packTypeCode"        ) var packTypeCode        : String? = null,
    @SerializedName("packTypeDescription" ) var packTypeDescription : String? = null
)
