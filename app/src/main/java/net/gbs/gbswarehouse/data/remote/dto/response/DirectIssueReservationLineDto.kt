package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class DirectIssueReservationLineDto (
    @SerializedName("reservationNumber"     ) var reservationNumber     : String?  = null,
    @SerializedName("reservationItem"       ) var reservationItem       : String?  = null,
    @SerializedName("materialCode"          ) var materialCode          : String?  = null,
    @SerializedName("materialDescription"   ) var materialDescription   : String?  = null,
    @SerializedName("uom"                   ) var uom                   : String?  = null,
    @SerializedName("plantCode"             ) var plantCode             : String?  = null,
    @SerializedName("storageLocationCode"   ) var storageLocationCode   : String?  = null,
    @SerializedName("movementType"          ) var movementType          : String?  = null,
    @SerializedName("batch"                 ) var batch                 : String?  = null,
    @SerializedName("quantity"              ) var quantity              : Double?  = null,
    @SerializedName("receivedQuantity"      ) var receivedQuantity      : Double?  = null,
    @SerializedName("remainingQuantity"     ) var remainingQuantity     : Double?  = null,
    @SerializedName("costCenterCode"        ) var costCenterCode        : String?  = null,
    @SerializedName("costCenterDescription" ) var costCenterDescription : String?  = null,
    @SerializedName("isBatchManaged"        ) var isBatchManaged        : Boolean? = null
)