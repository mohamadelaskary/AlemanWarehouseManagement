package net.gbs.gbswarehouse

import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock

/**
 * Batch / valuation type that a saved issuing line was created with. Kept aside while the
 * stock list for the edited bin is re-fetched, because the reload resets the selection.
 */
data class StockSelectionRestore(
    val batch: String?,
    val valuationType: String?,
    val qty: String
)

/** What [resolveStockSelection] decided should be shown for a freshly loaded stock list. */
data class ResolvedStockSelection(
    val selected: MaterialOnHandStock?,
    val availableQty: Double,
    val maxQty: Double,
    val qty: String,
    val searchText: String
)

/**
 * Works out the stock selection to display after a stock list is loaded.
 *
 * When [pending] is null this is the normal "user just scanned a bin" flow: batch and
 * valuation managed materials stay unselected so the user must pick one. When [pending] is
 * supplied the line is being edited, so the saved batch / valuation type and quantity are
 * re-applied instead of being thrown away.
 */
fun resolveStockSelection(
    stockList: List<MaterialOnHandStock>,
    remainingQty: Double,
    pending: StockSelectionRestore?
): ResolvedStockSelection {
    if (stockList.isEmpty()) {
        return ResolvedStockSelection(null, 0.0, 0.0, "0", "")
    }

    val isBatchManaged = stockList.any { it.isBatchManaged == true }
    val hasValuationType = !isBatchManaged && stockList.any { !it.valuationType.isNullOrEmpty() }

    if (isBatchManaged || hasValuationType) {
        if (pending == null) {
            return ResolvedStockSelection(null, 0.0, 0.0, "0", "")
        }
        val matched = stockList.find {
            if (isBatchManaged) it.batch == pending.batch else it.valuationType == pending.valuationType
        } ?: return ResolvedStockSelection(null, 0.0, 0.0, "0", "")
        val availQty = matched.availableQty ?: 0.0
        val maxQty = minOf(remainingQty, availQty)
        return ResolvedStockSelection(
            selected = matched,
            availableQty = availQty,
            maxQty = maxQty,
            qty = pending.qty,
            searchText = matched.stockSpinnerLabel()
        )
    }

    val availQty = stockList.sumOf { it.availableQty ?: 0.0 }
    val maxQty = minOf(remainingQty, availQty)
    return ResolvedStockSelection(
        selected = stockList.firstOrNull(),
        availableQty = availQty,
        maxQty = maxQty,
        qty = pending?.qty ?: if (maxQty > 0) formatQty(maxQty) else "0",
        searchText = ""
    )
}