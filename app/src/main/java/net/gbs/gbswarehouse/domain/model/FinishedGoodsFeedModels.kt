package net.gbs.gbswarehouse.domain.model

data class FinishedGoodsFeedOrder(
    val salesOrderNumber: String?,
    val customerCode: String?,
    val customerName: String?,
    val orderDescription: String?,
    val carNo: String?,
    val isFactoryCar: Boolean = true,
    val factoryCarDescription: String?,
    val itemCount: Int?,
    val lines: List<FinishedGoodsFeedItem> = emptyList()
) {
    val salesOrder: String?
        get() = carNo ?: salesOrderNumber
}

data class FinishedGoodsFeedItem(
    val customerCode: String?,
    val customerName: String?,
    val orderType: String?,
    val orderDescription: String?,
    val shippingPointCode: String?,
    val salesOrder: String?,
    val itemNo: String?,
    val materialCode: String?,
    val materialDescription: String?,
    val quantity: Double?,
    val receivedQuantity: Double?,
    val remainingQuantity: Double?,
    val uom: String?,
    val plantCode: String?,
    val carNo: String?,
    val factoryCarDescription: String?
)

data class IssueFinishedGoodsFeedResult(
    val goodsReceiptId: Int?,
    val receiptNo: String?,
    val receiptStatus: String?,
    val deliveryNo: String?,
    val readyForPutAway: Boolean?,
    val needsAction: Boolean?,
    val hasOrphanedLines: Boolean?,
    val lines: List<IssueFinishedGoodsFeedResultLine> = emptyList()
)

data class IssueFinishedGoodsFeedResultLine(
    val goodsReceiptLineId: Int?,
    val material: String?,
    val materialDescription: String?,
    val receivedQuantity: Int?,
    val sapOutboxMessageId: Int?,
    val sapStatus: String?,
    val sapMaterialDocument: String?,
    val sapMaterialDocumentYear: String?,
    val attemptCount: Int?,
    val maxAttempts: Int?,
    val lastErrorMessage: String?,
    val salesOrder: String?,
    val salesOrderItem: String?,
    val billingDocumentNo: String?,
    val billingAmount: String?
)