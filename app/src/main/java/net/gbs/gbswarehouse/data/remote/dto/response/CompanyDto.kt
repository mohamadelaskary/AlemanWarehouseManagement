package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CompanyDto (
    @SerializedName("companyCode"        ) var companyCode        : String? = null,
    @SerializedName("companyDescription" ) var companyDescription : String? = null
)
