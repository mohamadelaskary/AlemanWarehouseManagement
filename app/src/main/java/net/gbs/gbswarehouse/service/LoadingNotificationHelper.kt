package net.gbs.gbswarehouse.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import net.gbs.gbswarehouse.MainActivity
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.domain.model.LoadingOperation

/**
 * Three channels with deliberately different behaviour:
 *  - ACTIVE: silent, low importance, progress updates only
 *  - SUCCESS / FAILURE: different importance levels, silent, one-shot
 *
 * The result channels are silent on purpose: [net.gbs.gbswarehouse.audio.LoadingSoundPlayer]
 * owns the audible feedback and plays a distinct tone per transition, so the channels
 * must not add a second, identical system sound on top of it.
 */
object LoadingNotificationHelper {

    const val CHANNEL_ACTIVE = "loading_active"
    const val CHANNEL_SUCCESS = "loading_success"
    const val CHANNEL_FAILURE = "loading_failure"

    private const val NOTIFICATION_ACTIVE_ID = 1001
    private const val NOTIFICATION_RESULT_BASE_ID = 1100

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ACTIVE,
                context.getString(R.string.notif_channel_loading),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notif_channel_loading_desc)
                setSound(null, null)
                enableVibration(false)
                setShowBadge(false)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SUCCESS,
                context.getString(R.string.notif_channel_success),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_success_desc)
                setSound(null, null)
                enableVibration(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_FAILURE,
                context.getString(R.string.notif_channel_failure),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_failure_desc)
                setSound(null, null)
                enableVibration(true)
            }
        )
    }

    fun buildActiveNotification(context: Context, operation: LoadingOperation): Notification =
        NotificationCompat.Builder(context, CHANNEL_ACTIVE)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(
                context.getString(R.string.notif_title_loading, operation.itemName)
            )
            .setContentText(
                context.getString(
                    R.string.notif_text_loading,
                    operation.conveyorName,
                    operation.loadedQuantity,
                    operation.requiredQuantity
                )
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(operationPendingIntent(context, operation.operationId))
            .build()

    fun showSuccess(context: Context, operation: LoadingOperation) {
        showResult(
            context = context,
            operation = operation,
            channel = CHANNEL_SUCCESS,
            title = context.getString(R.string.notif_title_success),
            text = context.getString(
                R.string.notif_text_success,
                operation.itemName,
                operation.conveyorName
            )
        )
    }

    fun showFailure(context: Context, operation: LoadingOperation) {
        showResult(
            context = context,
            operation = operation,
            channel = CHANNEL_FAILURE,
            title = context.getString(R.string.notif_title_failure),
            text = context.getString(
                R.string.notif_text_failure,
                operation.itemName,
                operation.conveyorName
            )
        )
    }

    private fun showResult(
        context: Context,
        operation: LoadingOperation,
        channel: String,
        title: String,
        text: String
    ) {
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setPriority(
                if (channel == CHANNEL_FAILURE) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(operationPendingIntent(context, operation.operationId))
            .build()
        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIFICATION_RESULT_BASE_ID + operation.operationId.hashCode(), notification)
    }

    fun clearActive(context: Context) {
        context.getSystemService(NotificationManager::class.java)
            ?.cancel(NOTIFICATION_ACTIVE_ID)
    }

    /** The only navigation payload is the operationId. */
    private fun operationPendingIntent(context: Context, operationId: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(LoadingService.EXTRA_OPERATION_ID, operationId)
        }
        return PendingIntent.getActivity(
            context,
            operationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    const val ACTIVE_NOTIFICATION_ID = NOTIFICATION_ACTIVE_ID
}