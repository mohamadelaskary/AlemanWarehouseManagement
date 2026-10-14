package net.gbs.gbswarehouse.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.LoadingEvent
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.model.StreamStatus

/**
 * PLC abstraction. Live conveyor statuses arrive through the
 * `api/Conveyors/Stream` push channel (SignalR / SSE) and are cached here.
 */
interface PlcDataSource {

    val conveyors: StateFlow<List<Conveyor>>
    val operations: StateFlow<List<LoadingOperation>>
    val events: SharedFlow<LoadingEvent>

    /** Connection state of the live conveyor stream. */
    val streamStatus: StateFlow<StreamStatus>

    suspend fun getConveyors(): NetworkResult<List<Conveyor>>

    suspend fun startLoading(
        order: LoadingOrder,
        item: OrderItem,
        conveyor: Conveyor,
        qty: Int
    ): NetworkResult<LoadingOperation>

    suspend fun pauseLoading(operationId: String): NetworkResult<LoadingOperation>
    suspend fun resumeLoading(operationId: String): NetworkResult<LoadingOperation>
    suspend fun manualStart(conveyorId: Int): NetworkResult<Conveyor>
    suspend fun manualStop(conveyorId: Int): NetworkResult<Conveyor>
    suspend fun addOnePack(operationId: String): NetworkResult<LoadingOperation>
    suspend fun reportFailure(operationId: String, reason: String): NetworkResult<LoadingOperation>

    fun getOperation(operationId: String): LoadingOperation?
    fun getActiveOperation(): LoadingOperation?
}
