package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class MovementTypeDto (
    @SerializedName("movementType" ) var movementType : String? = null,
    @SerializedName("returnReason" ) var returnReason : String? = null,
    @SerializedName("count"        ) var count        : Int?    = null
)