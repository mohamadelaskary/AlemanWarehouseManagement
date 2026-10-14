package net.gbs.gbswarehouse.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.LoadingRunDto
import net.gbs.gbswarehouse.domain.model.BagType
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.ConveyorSettings
import net.gbs.gbswarehouse.domain.model.LoadingEvent
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.model.LoadingOrder
import net.gbs.gbswarehouse.domain.model.OrderItem
import net.gbs.gbswarehouse.domain.model.StreamStatus

/**
 * Single source of truth for the whole loading feature.
 *
 * Orders are real, from `api/Conveyors/GetLoadingOrders`.
 * Conveyor and operation state is streamed live from `api/Conveyors/Stream`
 * (with `api/Conveyors/GetConveyors` as the initial/fallback pull).
 */
interface LoadingRepository {

    val conveyors: StateFlow<List<Conveyor>>
    val operations: StateFlow<List<LoadingOperation>>
    val events: SharedFlow<LoadingEvent>

    /** Connection state of the live conveyor stream. */
    val streamStatus: StateFlow<StreamStatus>

    /** Fetches the real order list and caches it for [getOrderDetails]. */
    suspend fun getOrders(): NetworkResult<List<LoadingOrder>>

    fun getCachedOrders(): List<LoadingOrder>?

    suspend fun getOrderDetails(orderId: Int): NetworkResult<LoadingOrder>

    suspend fun startLoading(order: LoadingOrder, item: OrderItem, conveyor: Conveyor, qty: Int): NetworkResult<LoadingOperation>
    suspend fun pauseLoading(operationId: String): NetworkResult<LoadingOperation>
    suspend fun resumeLoading(operationId: String): NetworkResult<LoadingOperation>
    suspend fun manualStart(conveyorId: Int): NetworkResult<Conveyor>
    suspend fun manualStop(conveyorId: Int): NetworkResult<Conveyor>
    suspend fun addOnePack(operationId: String): NetworkResult<LoadingOperation>
    suspend fun reportFailure(operationId: String, reason: String): NetworkResult<LoadingOperation>

    // Real API integrations from Conveyors-Mobile-API.md
    suspend fun getConveyors(): NetworkResult<List<Conveyor>>
    suspend fun getConveyor(conveyorId: String): NetworkResult<Conveyor>
    suspend fun resetConveyorFault(conveyorId: String): NetworkResult<Unit>
    suspend fun getConveyorSettings(conveyorId: String): NetworkResult<ConveyorSettings>
    suspend fun updateConveyorSettings(conveyorId: String, settings: ConveyorSettings): NetworkResult<ConveyorSettings>
    suspend fun getBagTypes(): NetworkResult<List<BagType>>
    suspend fun getLoadingRunsReport(): NetworkResult<List<LoadingRunDto>>

    fun getOperation(operationId: String): LoadingOperation?
    fun getActiveOperation(): LoadingOperation?
}
