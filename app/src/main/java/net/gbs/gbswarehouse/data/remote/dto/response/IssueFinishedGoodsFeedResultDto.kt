package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class IssueFinishedGoodsFeedResultDto (
    @SerializedName("goodsReceiptId"   ) var goodsReceiptId   : Int?             = null,
    @SerializedName("receiptNo"        ) var receiptNo        : String?          = null,
    @SerializedName("receiptStatus"    ) var receiptStatus    : String?          = null,
    @SerializedName("deliveryNo"       ) var deliveryNo       : String?          = null,
    @SerializedName("readyForPutAway"  ) var readyForPutAway  : Boolean?         = null,
    @SerializedName("needsAction"      ) var needsAction      : Boolean?         = null,
    @SerializedName("hasOrphanedLines" ) var hasOrphanedLines : Boolean?         = null,
    @SerializedName("lines"            ) var lines            : ArrayList<IssueFinishedGoodsFeedResultLineDto> = arrayListOf()
)

data class IssueFinishedGoodsFeedResultLineDto (
    @SerializedName("goodsReceiptLineId"      ) var goodsReceiptLineId      : Int?    = null,
    @SerializedName("material"                ) var material                : String? = null,
    @SerializedName("materialDescription"     ) var materialDescription     : String? = null,
    @SerializedName("receivedQuantity"        ) var receivedQuantity        : Int?    = null,
    @SerializedName("sapOutboxMessageId"      ) var sapOutboxMessageId      : Int?    = null,
    @SerializedName("sapStatus"               ) var sapStatus               : String? = null,
    @SerializedName("sapMaterialDocument"     ) var sapMaterialDocument     : String? = null,
    @SerializedName("sapMaterialDocumentYear" ) var sapMaterialDocumentYear : String? = null,
    @SerializedName("attemptCount"            ) var attemptCount            : Int?    = null,
    @SerializedName("maxAttempts"             ) var maxAttempts             : Int?    = null,
    @SerializedName("lastErrorMessage"        ) var lastErrorMessage        : String? = null,
    @SerializedName("salesOrder"              ) var salesOrder              : String? = null,
    @SerializedName("salesOrderItem"          ) var salesOrderItem          : String? = null,
    @SerializedName("billingDocumentNo"       ) var billingDocumentNo       : String? = null,
    @SerializedName("billingAmount"           ) var billingAmount           : String? = null
)
