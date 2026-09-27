package com.ironlog.app.platform.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

/** Schedules the wake-up for the rest timer; swappable in tests. */
interface RestAlarmScheduler {
    /** @return true when the alarm is exact (false = may be delayed by the system) */
    fun schedule(targetElapsed: Long): Boolean

    fun cancel()
}

class AlarmRestAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : RestAlarmScheduler {

    override fun schedule(targetElapsed: Long): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        val exact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        val pendingIntent = pendingIntent()
        if (exact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                targetElapsed,
                pendingIntent,
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                targetElapsed,
                pendingIntent,
            )
        }
        return exact
    }

    override fun cancel() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, RestAlarmReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    companion object {
        /** Only one rest timer can run at a time. */
        const val REQUEST_CODE = 1001
    }
}
