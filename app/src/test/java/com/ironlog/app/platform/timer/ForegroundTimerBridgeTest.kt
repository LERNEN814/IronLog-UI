package com.ironlog.app.platform.timer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FakeElapsedClock
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.data.timer.RestTimerRepository
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.testutil.FakeForegroundState
import com.ironlog.app.testutil.FakeRestAlarmScheduler
import com.ironlog.app.testutil.FakeRestNotifier
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** REST_TIMER section 7.1 (D5) test TR8: the bridge drives the ongoing notification. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ForegroundTimerBridgeTest {

    private val nowWall = 1_700_000_000_000L
    private val nowElapsed = 500_000L

    private class TestLifecycleOwner : LifecycleOwner {
        override val lifecycle: Lifecycle = LifecycleRegistry(this)
    }

    @Test
    fun tr8_onStopShowsRunningNotificationAndOnStartCancelsIt() = runBlocking {
        val workoutRepository = FakeWorkoutRepository()
        workoutRepository.sessions.value = listOf(
            testSession(
                id = "session-1",
                status = SessionStatus.IN_PROGRESS,
                startedAt = 0L,
                endedAt = null,
            ),
        )
        val notifier = FakeRestNotifier()
        val repository = RestTimerRepository(
            workoutRepository = workoutRepository,
            scheduler = FakeRestAlarmScheduler(),
            notifier = notifier,
            foregroundState = FakeForegroundState(inForeground = true),
            clock = FixedClock(nowWall),
            elapsedClock = FakeElapsedClock(nowElapsed),
        )
        repository.start("session-1", 90)
        assertThat(notifier.runningCalls).isEmpty()

        val bridge = ForegroundTimerBridge(repository)
        val owner = TestLifecycleOwner()

        bridge.onStop(owner)
        assertThat(notifier.runningCalls).containsExactly("session-1" to nowWall + 90_000L)

        bridge.onStart(owner)
        assertThat(notifier.cancelCount).isEqualTo(1)
    }
}
