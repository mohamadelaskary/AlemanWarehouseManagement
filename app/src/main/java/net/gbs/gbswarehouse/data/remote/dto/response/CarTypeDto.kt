package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CarTypeDto (
    @SerializedName("vehicleTypeId"   ) var vehicleTypeId   : Int?     = null,
    @SerializedName("vehicleTypeName" ) var vehicleTypeName : String?  = null,
    @SerializedName("description"     ) var description     : String?  = null,
    @SerializedName("isActive"        ) var isActive        : Boolean? = null,
    @SerializedName("createdBy"       ) var createdBy       : String?  = null,
    @SerializedName("createdAt"       ) var createdAt       : String?  = null
)
