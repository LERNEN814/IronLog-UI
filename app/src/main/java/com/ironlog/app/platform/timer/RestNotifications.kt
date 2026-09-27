package com.ironlog.app.platform.timer

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ironlog.app.MainActivity
import com.ironlog.app.R
import com.ironlog.app.domain.timer.RestNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** REST_TIMER.md section 6: two channels, ids 2001 (running) and 2002 (done). */
class RestNotifications @Inject constructor(
    @ApplicationContext private val context: Context,
) : RestNotifier {

    init {
        createChannels()
    }

    fun createChannels() {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_RUNNING,
                context.getString(R.string.rest_channel_running),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_DONE,
                context.getString(R.string.rest_channel_done),
                NotificationManager.IMPORTANCE_HIGH,
            ),
        )
    }

    override fun showRunning(sessionId: String, targetWallMillis: Long) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_RUNNING)
            .setSmallIcon(R.drawable.ic_stat_rest)
            .setContentTitle(context.getString(R.string.rest_notification_running_title))
            .setContentText(context.getString(R.string.rest_notification_running_text))
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setWhen(targetWallMillis)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent(sessionId))
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_RUNNING, notification)
    }

    override fun cancelRunning() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_RUNNING)
    }

    override fun showFinished(sessionId: String) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_DONE)
            .setSmallIcon(R.drawable.ic_stat_rest)
            .setContentTitle(context.getString(R.string.rest_notification_done_title))
            .setContentText(context.getString(R.string.rest_notification_done_text))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(contentIntent(sessionId))
            .build()
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_RUNNING)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_DONE, notification)
    }

    /** Builds the running notification without posting it (preview/tests). */
    fun buildRunning(sessionId: String, targetWallMillis: Long): Notification =
        NotificationCompat.Builder(context, CHANNEL_RUNNING)
            .setSmallIcon(R.drawable.ic_stat_rest)
            .setContentTitle(context.getString(R.string.rest_notification_running_title))
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setWhen(targetWallMillis)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent(sessionId))
            .build()

    private fun contentIntent(sessionId: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .putExtra(EXTRA_SESSION_ID, sessionId)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            REQUEST_CONTENT,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    companion object {
        const val CHANNEL_RUNNING = "rest_running"
        const val CHANNEL_DONE = "rest_done"
        const val NOTIFICATION_RUNNING = 2001
        const val NOTIFICATION_DONE = 2002
        const val EXTRA_SESSION_ID = "sessionId"
        private const val REQUEST_CONTENT = 1002
    }
}
