package net.gbs.gbswarehouse.domain.model

import com.google.gson.annotations.SerializedName

data class NaolonDescription(
    @SerializedName("naolonDescriptionId" ) var naolonDescriptionId : Int?    = null,
    @SerializedName("naolonDescription"   ) var naolonDescription   : String? = null
)
