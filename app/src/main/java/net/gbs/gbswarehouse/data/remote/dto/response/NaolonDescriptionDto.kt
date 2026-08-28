package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class NaolonDescriptionDto (
    @SerializedName("naolonDescriptionId" ) var naolonDescriptionId : Int?    = null,
    @SerializedName("naolonDescription"   ) var naolonDescription   : String? = null
)
