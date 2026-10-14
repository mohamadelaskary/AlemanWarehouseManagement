package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class FinishedGoodsFeedOrderDto (
    @SerializedName("salesOrderNumber"    ) var salesOrderNumber    : String?          = null,
    @SerializedName("customerCode"        ) var customerCode        : String?          = null,
    @SerializedName("customerName"        ) var customerName        : String?          = null,
    @SerializedName("orderDescription"    ) var orderDescription    : String?          = null,
    @SerializedName("carNo"               ) var carNo               : String?          = null,
    @SerializedName("isFactoryCar"        ) var isFactoryCar        : Boolean?         = null,
    @SerializedName("factoryCarDescription") var factoryCarDescription : String?       = null,
    @SerializedName("itemCount"           ) var itemCount           : Int?             = null,
    @SerializedName("lines"               ) var lines               : ArrayList<FinishedGoodsFeedItemDto> = arrayListOf()
)

data class FinishedGoodsFeedItemDto (
    @SerializedName("customerCode"        ) var customerCode        : String? = null,
    @SerializedName("customerName"        ) var customerName        : String? = null,
    @SerializedName("orderType"           ) var orderType           : String? = null,
    @SerializedName("orderDescription"    ) var orderDescription    : String? = null,
    @SerializedName("shippingPointCode"   ) var shippingPointCode   : String? = null,
    @SerializedName("salesOrder"          ) var salesOrder          : String? = null,
    @SerializedName("itemNo"              ) var itemNo              : String? = null,
    @SerializedName("materialCode"        ) var materialCode        : String? = null,
    @SerializedName("materialDescription" ) var materialDescription : String? = null,
    @SerializedName("quantity"            ) var quantity            : Double? = null,
    @SerializedName("receivedQuantity"    ) var receivedQuantity    : Double? = null,
    @SerializedName("remainingQuantity"   ) var remainingQuantity   : Double? = null,
    @SerializedName("uom"                 ) var uom                 : String? = null,
    @SerializedName("plantCode"           ) var plantCode           : String? = null,
    @SerializedName("carNo"               ) var carNo               : String? = null,
    @SerializedName("factoryCarDescription") var factoryCarDescription : String? = null
)