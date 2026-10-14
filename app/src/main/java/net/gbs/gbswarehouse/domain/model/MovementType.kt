package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class MovementType(
    @SerializedName("movementType" ) var movementType : String? = null,
    @SerializedName("returnReason" ) var returnReason : String? = null,
    @SerializedName("count"        ) var count        : Int?    = null,
    @SerializedName("value"        ) var value        : String?    = null,
    )
