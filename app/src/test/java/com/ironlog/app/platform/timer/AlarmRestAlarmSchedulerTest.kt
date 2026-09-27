package com.ironlog.app.platform.timer

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

/**
 * REST_TIMER section 9. The exact/inexact choice is observable through the return value of
 * [AlarmRestAlarmScheduler.schedule]: it returns false exactly when the inexact fallback was used.
 * Robolectric's ScheduledAlarm records both branches as ELAPSED_REALTIME_WAKEUP + allowWhileIdle.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AlarmRestAlarmSchedulerTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(AlarmManager::class.java)
        ShadowAlarmManager.reset()
    }

    private fun scheduledAlarms() = shadowOf(alarmManager).scheduledAlarms

    @Test
    fun schedulesExactAlarmWhenPermissionIsGranted() {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
        val scheduler = AlarmRestAlarmScheduler(context)

        val exact = scheduler.schedule(190_000L)

        assertThat(exact).isTrue()
        val alarms = scheduledAlarms()
        assertThat(alarms).hasSize(1)
        val alarm = alarms.single()
        assertThat(alarm.triggerAtTime).isEqualTo(190_000L)
        assertThat(alarm.type).isEqualTo(AlarmManager.ELAPSED_REALTIME_WAKEUP)
        assertThat(alarm.allowWhileIdle).isTrue()
    }

    @Test
    fun fallsBackToInexactAlarmWhenPermissionIsDenied() {
        ShadowAlarmManager.setCanScheduleExactAlarms(false)
        val scheduler = AlarmRestAlarmScheduler(context)

        val exact = scheduler.schedule(190_000L)

        // false == the inexact branch (setAndAllowWhileIdle) was taken.
        assertThat(exact).isFalse()
        val alarms = scheduledAlarms()
        assertThat(alarms).hasSize(1)
        val alarm = alarms.single()
        assertThat(alarm.triggerAtTime).isEqualTo(190_000L)
        assertThat(alarm.type).isEqualTo(AlarmManager.ELAPSED_REALTIME_WAKEUP)
        assertThat(alarm.allowWhileIdle).isTrue()
    }

    @Test
    fun cancelRemovesTheScheduledAlarm() {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
        val scheduler = AlarmRestAlarmScheduler(context)
        scheduler.schedule(190_000L)

        scheduler.cancel()

        assertThat(scheduledAlarms()).isEmpty()
    }
}
