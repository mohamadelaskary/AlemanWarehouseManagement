package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class CarType(
    @SerializedName("vehicleTypeId"   ) var vehicleTypeId   : Int?     = null,
    @SerializedName("vehicleTypeName" ) var vehicleTypeName : String?  = null,
    @SerializedName("description"     ) var description     : String?  = null,
    @SerializedName("isActive"        ) var isActive        : Boolean? = null,
    @SerializedName("createdBy"       ) var createdBy       : String?  = null,
    @SerializedName("createdAt"       ) var createdAt       : String?  = null
)
