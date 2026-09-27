package com.ironlog.app.data.timer

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FakeElapsedClock
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.timer.TimerEvent
import com.ironlog.app.testutil.FakeForegroundState
import com.ironlog.app.testutil.FakeRestAlarmScheduler
import com.ironlog.app.testutil.FakeRestNotifier
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/** REST_TIMER section 9 vectors TR1-TR5 plus the foreground/background branches. */
class RestTimerRepositoryTest {

    private val nowWall = 1_700_000_000_000L
    private val nowElapsed = 500_000L

    private lateinit var workoutRepository: FakeWorkoutRepository
    private lateinit var scheduler: FakeRestAlarmScheduler
    private lateinit var notifier: FakeRestNotifier
    private lateinit var foreground: FakeForegroundState
    private lateinit var clock: FixedClock
    private lateinit var elapsed: FakeElapsedClock

    @Before
    fun setUp() {
        workoutRepository = FakeWorkoutRepository()
        scheduler = FakeRestAlarmScheduler()
        notifier = FakeRestNotifier()
        foreground = FakeForegroundState()
        clock = FixedClock(nowWall)
        elapsed = FakeElapsedClock(nowElapsed)
    }

    private fun createRepository(): RestTimerRepository = RestTimerRepository(
        workoutRepository = workoutRepository,
        scheduler = scheduler,
        notifier = notifier,
        foregroundState = foreground,
        clock = clock,
        elapsedClock = elapsed,
    )

    private fun seedInProgressSession(restTargetAt: Long? = null) {
        workoutRepository.sessions.value = listOf(
            testSession(
                id = "session-1",
                status = SessionStatus.IN_PROGRESS,
                startedAt = 0L,
                endedAt = null,
            ).copy(restTargetAt = restTargetAt),
        )
    }

    @Test
    fun tr1_startSchedulesAlarmAndPersistsWallTarget() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()

        repository.start("session-1", 90)

        assertThat(scheduler.scheduledTargets).containsExactly(nowElapsed + 90_000L)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isEqualTo(nowWall + 90_000L)
        val model = repository.uiModel.value
        assertThat(model?.sessionId).isEqualTo("session-1")
        assertThat(model?.remainingMillis).isEqualTo(90_000L)
        assertThat(model?.totalMillis).isEqualTo(90_000L)
        assertThat(model?.isExact).isTrue()
        assertThat(model?.isRunning).isTrue()
    }

    @Test
    fun tr2_adjustReschedulesAndMovesBothTargets() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)

        repository.adjust(+15)

        assertThat(scheduler.scheduledTargets).containsExactly(nowElapsed + 90_000L, nowElapsed + 105_000L)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isEqualTo(nowWall + 105_000L)
        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(105_000L)
        assertThat(repository.uiModel.value?.totalMillis).isEqualTo(105_000L)
    }

    @Test
    fun tr3_skipCancelsAlarmAndClearsDatabase() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)

        repository.skip()

        assertThat(scheduler.cancelCount).isEqualTo(1)
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun tr4_restoreFutureTargetResumesAndSchedules() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall + 30_000L)

        val repository = createRepository()
        repository.restore()

        assertThat(scheduler.scheduledTargets).containsExactly(nowElapsed + 30_000L)
        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(30_000L)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isEqualTo(nowWall + 30_000L)
    }

    @Test
    fun tr5_restoreExpiredTargetClearsFieldWithoutScheduling() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall - 1L)

        val repository = createRepository()
        repository.restore()

        assertThat(repository.uiModel.value).isNull()
        assertThat(scheduler.scheduledTargets).isEmpty()
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
    }

    @Test
    fun restoreWithoutInProgressSessionIsANoOp() = runBlocking {
        val repository = createRepository()
        repository.restore()

        assertThat(repository.uiModel.value).isNull()
        assertThat(scheduler.scheduledTargets).isEmpty()
    }

    @Test
    fun backgroundTimerShowsOngoingNotification() {
        runBlocking {
        seedInProgressSession()
        foreground.inForeground = false
        val repository = createRepository()

        repository.start("session-1", 90)

        assertThat(notifier.runningCalls).containsExactly("session-1" to nowWall + 90_000L)
        }
    }

    @Test
    fun foregroundTimerDoesNotShowNotificationAndReactsToAppSwitch() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)
        assertThat(notifier.runningCalls).isEmpty()

        repository.onForegroundChanged(inForeground = false)
        assertThat(notifier.runningCalls).hasSize(1)

        repository.onForegroundChanged(inForeground = true)
        assertThat(notifier.cancelCount).isEqualTo(1)
    }

    @Test
    fun foregroundAlarmEmitsInAppEventInsteadOfNotification() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)
        val event = async(start = CoroutineStart.UNDISPATCHED) { repository.events.first() }

        repository.onAlarmFired()

        assertThat(event.await()).isEqualTo(TimerEvent.Finished("session-1"))
        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun backgroundAlarmShowsFinishedNotification() = runBlocking {
        seedInProgressSession()
        foreground.inForeground = false
        val repository = createRepository()
        repository.start("session-1", 90)

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).containsExactly("session-1")
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
    }

    @Test
    fun startingASecondTimerOverwritesTheFirst() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()

        repository.start("session-1", 90)
        repository.start("session-1", 150)

        assertThat(repository.uiModel.value?.totalMillis).isEqualTo(150_000L)
        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(150_000L)
        assertThat(scheduler.scheduledTargets.last()).isEqualTo(nowElapsed + 150_000L)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isEqualTo(nowWall + 150_000L)
    }

    @Test
    fun restoreIsANoOpWhileATimerRunsLocally() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.restore()
        repository.start("session-1", 90)

        repository.restore()

        assertThat(scheduler.scheduledTargets).hasSize(1)
        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(90_000L)
    }

    @Test
    fun finishWhileRunningClearsTimerAndDatabase() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)

        repository.finish()

        assertThat(scheduler.cancelCount).isEqualTo(1)
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun restoreAfterAlarmFiredDoesNotResume() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall + 30_000L)
        val repository = createRepository()
        repository.restore()
        val scheduledAfterRestore = scheduler.scheduledTargets.size

        repository.onAlarmFired()
        repository.restore()

        assertThat(scheduler.scheduledTargets).hasSize(scheduledAfterRestore)
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun adjustAndSkipWithoutARunningTimerAreNoOps() = runBlocking {
        val repository = createRepository()

        repository.adjust(+15)
        repository.skip()
        repository.onForegroundChanged(inForeground = false)

        assertThat(scheduler.scheduledTargets).isEmpty()
        assertThat(scheduler.cancelCount).isEqualTo(0)
        assertThat(notifier.runningCalls).isEmpty()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun alarmFiredWithoutARunningTimerIsANoOp() = runBlocking {
        val repository = createRepository()

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun tr6_coldProcessAlarmWithExpiredTargetShowsFinishedNotification() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall - 1_000L)
        foreground.inForeground = false
        val repository = createRepository()

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).containsExactly("session-1")
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun tr6b_coldProcessAlarmInForegroundEmitsFinishedEvent() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall - 1_000L)
        val repository = createRepository()
        val event = async(start = CoroutineStart.UNDISPATCHED) { repository.events.first() }

        repository.onAlarmFired()

        assertThat(event.await()).isEqualTo(TimerEvent.Finished("session-1"))
        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(workoutRepository.sessions.value.single().restTargetAt).isNull()
    }

    @Test
    fun tr7_coldProcessAlarmWithoutStoredTargetIsSilent() = runBlocking {
        seedInProgressSession(restTargetAt = null)
        foreground.inForeground = false
        val repository = createRepository()

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun coldProcessAlarmWithoutInProgressSessionIsSilent() = runBlocking {
        val repository = createRepository()

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(notifier.cancelCount).isEqualTo(1)
        assertThat(repository.uiModel.value).isNull()
    }

    @Test
    fun coldProcessAlarmWithFutureTargetResumesWithoutNotifying() = runBlocking {
        seedInProgressSession(restTargetAt = nowWall + 30_000L)
        val repository = createRepository()

        repository.onAlarmFired()

        assertThat(notifier.finishedCalls).isEmpty()
        assertThat(scheduler.scheduledTargets).containsExactly(nowElapsed + 30_000L)
        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(30_000L)
    }

    @Test
    fun refreshDecreasesRemainingFromElapsedClock() = runBlocking {
        seedInProgressSession()
        val repository = createRepository()
        repository.start("session-1", 90)

        elapsed.advanceSeconds(30)
        repository.refresh()

        assertThat(repository.uiModel.value?.remainingMillis).isEqualTo(60_000L)
    }
}
