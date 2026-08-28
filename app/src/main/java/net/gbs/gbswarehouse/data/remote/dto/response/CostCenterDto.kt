package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CostCenterDto (
    @SerializedName("costCenterId"          ) var costCenterId          : Int?    = null,
    @SerializedName("costCenterCode"        ) var costCenterCode        : String? = null,
    @SerializedName("costCenterDescription" ) var costCenterDescription : String? = null,
    @SerializedName("companyCode"           ) var companyCode           : String? = null,
    @SerializedName("companyDescription"    ) var companyDescription    : String? = null
)