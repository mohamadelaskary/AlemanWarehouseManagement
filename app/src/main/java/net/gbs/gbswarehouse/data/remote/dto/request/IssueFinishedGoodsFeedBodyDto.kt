package net.gbs.gbswarehouse.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class IssueFinishedGoodsFeedLineDto (
    @SerializedName("salesOrder"     ) var salesOrder     : String? = null,
    @SerializedName("itemNo"         ) var itemNo         : String? = null,
    @SerializedName("materialCode"   ) var materialCode   : String? = null,
    @SerializedName("quantity"       ) var quantity       : Double?    = null,
    @SerializedName("uom"            ) var uom            : String? = null,
    @SerializedName("plantCode"      ) var plantCode      : String? = null,
    @SerializedName("warehouseCode"  ) var warehouseCode  : String? = null,
    @SerializedName("storageBinCode" ) var storageBinCode : String? = null,
    @SerializedName("batch"          ) var batch          : String? = null,
    @SerializedName("valuationType"  ) var valuationType  : String? = null
)

data class IssueFinishedGoodsFeedBodyDto (
    @SerializedName("shippingPointCode" ) var shippingPointCode : String?          = null,
    @SerializedName("carNo"             ) var carNo             : String?          = null,
    @SerializedName("workerGroupCode"   ) var workerGroupCode   : String?          = null,
    @SerializedName("lines"             ) var lines             : ArrayList<IssueFinishedGoodsFeedLineDto> = arrayListOf()
)
