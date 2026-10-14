package net.gbs.gbswarehouse.core.loading

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single hand-off point between a notification tap and the Compose NavController.
 * The operationId is set by MainActivity and consumed exactly once by WarehouseApp.
 */
object LoadingNavigation {

    private val pendingOperationId = MutableStateFlow<String?>(null)
    val target: StateFlow<String?> = pendingOperationId.asStateFlow()

    fun request(operationId: String) {
        if (operationId.isNotBlank()) {
            pendingOperationId.value = operationId
        }
    }

    fun consume() {
        pendingOperationId.value = null
    }
}