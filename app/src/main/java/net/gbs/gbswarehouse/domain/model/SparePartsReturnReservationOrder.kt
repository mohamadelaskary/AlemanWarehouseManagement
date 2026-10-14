package net.gbs.gbswarehouse.domain.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class SparePartsReturnReservationOrder(
    @SerializedName("reservationNumber"     ) var reservationNumber     : String? = null,
    @SerializedName("orderNumber"           ) var orderNumber           : String? = null,
    @SerializedName("orderDescription"      ) var orderDescription      : String? = null,
    @SerializedName("companyCode"           ) var companyCode           : String? = null,
    @SerializedName("plantCode"             ) var plantCode             : String? = null,
    @SerializedName("costCenterCode"        ) var costCenterCode        : String? = null,
    @SerializedName("costCenterDescription" ) var costCenterDescription : String? = null,
    @SerializedName("requirementDate"       ) var requirementDate       : String? = null,
    @SerializedName("movementType"          ) var movementType          : String? = null,
    @SerializedName("itemCount"             ) var itemCount             : Int?    = null,
    @SerializedName("lines"                 ) var lines                : List<SparePartsReturnReservationLine> = emptyList()


){
    fun toJson(): String{
        return Gson().toJson(this)
    }
    companion object {
        fun fromJson(json: String): SparePartsReturnReservationOrder {
            return Gson().fromJson(json, SparePartsReturnReservationOrder::class.java)
        }
    }
}
