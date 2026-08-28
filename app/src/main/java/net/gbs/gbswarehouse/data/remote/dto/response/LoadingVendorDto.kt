package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class LoadingVendorDto (
    @SerializedName("loadingVendorId"   ) var loadingVendorId   : Int?    = null,
    @SerializedName("loadingVendorCode" ) var loadingVendorCode : String? = null,
    @SerializedName("loadingVendorName" ) var loadingVendorName : String? = null
)
