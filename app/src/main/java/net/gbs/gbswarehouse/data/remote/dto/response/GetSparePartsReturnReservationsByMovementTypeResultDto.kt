package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GetSparePartsReturnReservationsByMovementTypeResultDto (
    @SerializedName("reservationNumber"     ) var reservationNumber     : String? = null,
    @SerializedName("orderNumber"           ) var orderNumber           : String? = null,
    @SerializedName("orderDescription"      ) var orderDescription      : String? = null,
    @SerializedName("companyCode"           ) var companyCode           : String? = null,
    @SerializedName("plantCode"             ) var plantCode             : String? = null,
    @SerializedName("costCenterCode"        ) var costCenterCode        : String? = null,
    @SerializedName("costCenterDescription" ) var costCenterDescription : String? = null,
    @SerializedName("requirementDate"       ) var requirementDate       : String? = null,
    @SerializedName("movementType"          ) var movementType          : String? = null,
    @SerializedName("itemCount"             ) var itemCount             : Int?    = null

)