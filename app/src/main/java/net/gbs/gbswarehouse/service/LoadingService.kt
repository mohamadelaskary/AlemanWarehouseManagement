package net.gbs.gbswarehouse.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.audio.LoadingSoundPlayer
import net.gbs.gbswarehouse.domain.model.LoadingEvent
import net.gbs.gbswarehouse.domain.model.LoadingOperation
import net.gbs.gbswarehouse.domain.repository.LoadingRepository
import javax.inject.Inject

/**
 * Owns the running loading operation so progress, sound and the ongoing
 * notification keep working while the UI is closed or the app is backgrounded.
 * Audio is driven by PLC events only, never by recomposition.
 */
@AndroidEntryPoint
class LoadingService : Service() {

    @Inject
    lateinit var repository: LoadingRepository

    @Inject
    lateinit var soundPlayer: LoadingSoundPlayer

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var eventsJob: Job? = null
    private var progressJob: Job? = null

    private var resultShown = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        LoadingNotificationHelper.createChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val operationId = intent?.getStringExtra(EXTRA_OPERATION_ID)
        if (operationId.isNullOrBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }
        resultShown = false
        startForeground(operationId)
        observeEvents(operationId)
        observeProgress(operationId)
        return START_STICKY
    }

    override fun onDestroy() {
        eventsJob?.cancel()
        progressJob?.cancel()
        LoadingNotificationHelper.clearActive(this)
        soundPlayer.release()
        scope.cancel()
        super.onDestroy()
    }

    private fun observeEvents(operationId: String) {
        eventsJob?.cancel()
        eventsJob = repository.events
            .filter { event ->
                when (event) {
                    is LoadingEvent.BagCounted -> event.operationId == operationId
                    is LoadingEvent.Completed -> event.operation.operationId == operationId
                    is LoadingEvent.Failed -> event.operation.operationId == operationId
                    is LoadingEvent.ConveyorStatusChanged -> false
                }
            }
            .onEach(::onEvent)
            .launchIn(scope)
    }

    private fun observeProgress(operationId: String) {
        progressJob?.cancel()
        progressJob = repository.operations
            .map { operations -> operations.firstOrNull { it.operationId == operationId } }
            .onEach { operation ->
                if (operation == null || operation.isFinished) return@onEach
                getSystemService(NotificationManager::class.java)?.notify(
                    LoadingNotificationHelper.ACTIVE_NOTIFICATION_ID,
                    LoadingNotificationHelper.buildActiveNotification(this, operation)
                )
            }
            .launchIn(scope)
    }

    private fun onEvent(event: LoadingEvent) {
        when (event) {
            is LoadingEvent.BagCounted -> soundPlayer.playBagCounted()

            is LoadingEvent.Completed -> {
                if (resultShown) return
                resultShown = true
                soundPlayer.playSuccess()
                LoadingNotificationHelper.showSuccess(this, event.operation)
                stopTracking()
            }

            is LoadingEvent.Failed -> {
                if (resultShown) return
                resultShown = true
                soundPlayer.playFailure()
                LoadingNotificationHelper.showFailure(this, event.operation)
                stopTracking()
            }

            is LoadingEvent.ConveyorStatusChanged -> Unit
        }
    }

    private fun stopTracking() {
        LoadingNotificationHelper.clearActive(this)
        scope.launch {
            delay(RESULT_NOTIFICATION_GRACE_MS)
            ServiceCompat.stopForeground(this@LoadingService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun startForeground(operationId: String) {
        val notification = repository.getOperation(operationId)
            ?.let { LoadingNotificationHelper.buildActiveNotification(this, it) }
            ?: startingNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                LoadingNotificationHelper.ACTIVE_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(LoadingNotificationHelper.ACTIVE_NOTIFICATION_ID, notification)
        }
    }

    private fun startingNotification() =
        NotificationCompat.Builder(this, LoadingNotificationHelper.CHANNEL_ACTIVE)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(getString(R.string.loading_active))
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    companion object {
        const val EXTRA_OPERATION_ID = "extra_operation_id"
        private const val RESULT_NOTIFICATION_GRACE_MS = 2_000L

        fun start(context: Context, operationId: String) {
            val intent = Intent(context, LoadingService::class.java).apply {
                putExtra(EXTRA_OPERATION_ID, operationId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LoadingService::class.java))
        }
    }
}