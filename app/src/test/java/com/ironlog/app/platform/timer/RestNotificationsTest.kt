package com.ironlog.app.platform.timer

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RestNotificationsTest {

    private lateinit var context: Context
    private lateinit var manager: NotificationManager

    @Before
    fun setUp() {
        val application: Application = ApplicationProvider.getApplicationContext()
        shadowOf(application).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        context = application
        manager = context.getSystemService(NotificationManager::class.java)
    }

    @Test
    fun createsBothChannelsWithSpecImportance() {
        RestNotifications(context)

        val running = manager.getNotificationChannel(RestNotifications.CHANNEL_RUNNING)
        val done = manager.getNotificationChannel(RestNotifications.CHANNEL_DONE)

        assertThat(running).isNotNull()
        assertThat(running?.importance).isEqualTo(NotificationManager.IMPORTANCE_LOW)
        assertThat(done).isNotNull()
        assertThat(done?.importance).isEqualTo(NotificationManager.IMPORTANCE_HIGH)
    }

    @Test
    fun runningNotificationUsesCountdownChronometerAndIsOngoing() {
        val notifications = RestNotifications(context)

        notifications.showRunning("session-1", 1_000_000L)

        val notification = shadowOf(manager).getNotification(RestNotifications.NOTIFICATION_RUNNING)
        assertThat(notification).isNotNull()
        val safe = requireNotNull(notification)
        assertThat(safe.`when`).isEqualTo(1_000_000L)
        assertThat(safe.flags and Notification.FLAG_ONGOING_EVENT).isNotEqualTo(0)
        assertThat(safe.extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER)).isTrue()
        assertThat(safe.extras.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN)).isTrue()
    }

    @Test
    fun finishedNotificationReplacesTheRunningOne() {
        val notifications = RestNotifications(context)
        notifications.showRunning("session-1", 1_000_000L)

        notifications.showFinished("session-1")

        assertThat(shadowOf(manager).getNotification(RestNotifications.NOTIFICATION_RUNNING)).isNull()
        val done = shadowOf(manager).getNotification(RestNotifications.NOTIFICATION_DONE)
        assertThat(done).isNotNull()
        assertThat(requireNotNull(done).flags and Notification.FLAG_AUTO_CANCEL).isNotEqualTo(0)
    }

    @Test
    fun cancelRunningRemovesOnlyTheRunningNotification() {
        val notifications = RestNotifications(context)
        notifications.showRunning("session-1", 1_000_000L)

        notifications.cancelRunning()

        assertThat(shadowOf(manager).getNotification(RestNotifications.NOTIFICATION_RUNNING)).isNull()
    }
}
