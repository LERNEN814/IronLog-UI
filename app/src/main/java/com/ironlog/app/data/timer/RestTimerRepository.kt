package com.ironlog.app.data.timer

import com.ironlog.app.core.time.Clock
import com.ironlog.app.core.time.ElapsedClock
import com.ironlog.app.domain.timer.AppForegroundState
import com.ironlog.app.domain.timer.RestNotifier
import com.ironlog.app.domain.timer.RestTimer
import com.ironlog.app.domain.timer.RestTimerMath
import com.ironlog.app.domain.timer.RestTimerState
import com.ironlog.app.domain.timer.RestTimerUiModel
import com.ironlog.app.domain.timer.TimerEvent
import com.ironlog.app.domain.workout.WorkoutRepository
import com.ironlog.app.platform.timer.RestAlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * REST_TIMER.md sections 4/6/7 + 7.1 (D5). Singleton state machine; the database only stores
 * `workout_session.rest_target_at` for process recovery.
 *
 * All mutating operations run under [mutex] so a cold-process alarm and a foreground restore
 * can never interleave. There is deliberately no automatic restore in `init`: the receiver
 * must see the stored target before anything clears it.
 */
@Singleton
class RestTimerRepository @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val scheduler: RestAlarmScheduler,
    private val notifier: RestNotifier,
    private val foregroundState: AppForegroundState,
    private val clock: Clock,
    private val elapsedClock: ElapsedClock,
) : RestTimer {

    private val mutex = Mutex()

    private val _uiModel = MutableStateFlow<RestTimerUiModel?>(null)
    override val uiModel: StateFlow<RestTimerUiModel?> = _uiModel.asStateFlow()

    private val _events = MutableSharedFlow<TimerEvent>(extraBufferCapacity = 1)
    override val events: SharedFlow<TimerEvent> = _events.asSharedFlow()

    // Written from the receiver/Default thread, read from the main thread (onForegroundChanged).
    @Volatile
    private var sessionId: String? = null
    private var targetElapsed: Long = 0L
    @Volatile
    private var targetWall: Long = 0L
    private var totalMillis: Long = 0L

    /** Step 1-5 of REST_TIMER section 4. */
    override suspend fun start(sessionId: String, seconds: Int) {
        mutex.withLock {
            val nowWall = clock.nowMillis()
            val nowElapsed = elapsedClock.elapsedRealtime()
            val total = seconds * 1000L
            this.sessionId = sessionId
            this.targetElapsed = nowElapsed + total
            this.targetWall = nowWall + total
            this.totalMillis = total

            workoutRepository.setRestTargetAt(sessionId, targetWall)
            val exact = scheduler.schedule(targetElapsed)
            publish(isExact = exact)
            if (!foregroundState.isInForeground()) {
                notifier.showRunning(sessionId, targetWall)
            }
        }
    }

    override suspend fun adjust(deltaSeconds: Int) {
        mutex.withLock {
            val currentSessionId = sessionId ?: return@withLock
            val adjusted = RestTimerMath.adjust(
                state = RestTimerState(targetElapsedRealtime = targetElapsed, totalMillis = totalMillis),
                deltaSeconds = deltaSeconds,
                nowElapsed = elapsedClock.elapsedRealtime(),
            )
            val applied = adjusted.targetElapsedRealtime - targetElapsed
            targetElapsed = adjusted.targetElapsedRealtime
            totalMillis = adjusted.totalMillis
            targetWall += applied

            workoutRepository.setRestTargetAt(currentSessionId, targetWall)
            val exact = scheduler.schedule(targetElapsed)
            publish(isExact = exact)
            if (!foregroundState.isInForeground()) {
                notifier.showRunning(currentSessionId, targetWall)
            }
        }
    }

    override suspend fun skip() {
        finish()
    }

    /** Cancels everything without emitting a completion event (user skipped or session ended). */
    override suspend fun finish() {
        mutex.withLock {
            val currentSessionId = sessionId
            if (currentSessionId == null && _uiModel.value == null) return@withLock
            scheduler.cancel()
            notifier.cancelRunning()
            if (currentSessionId != null) {
                workoutRepository.setRestTargetAt(currentSessionId, null)
            }
            clearState()
        }
    }

    /**
     * Called by [com.ironlog.app.platform.timer.RestAlarmReceiver].
     * With a running in-memory timer this finishes it. In a cold process (D5) the stored
     * `rest_target_at` decides: expired -> notify, still in the future -> resume.
     */
    suspend fun onAlarmFired() {
        mutex.withLock {
            val inMemorySessionId = sessionId
            if (inMemorySessionId != null) {
                scheduler.cancel()
                notifier.cancelRunning()
                workoutRepository.setRestTargetAt(inMemorySessionId, null)
                clearState()
                notifyFinished(inMemorySessionId)
                return@withLock
            }

            val session = workoutRepository.observeInProgressSession().first()
            if (session == null) {
                notifier.cancelRunning()
                return@withLock
            }
            val storedTarget = session.restTargetAt
            if (storedTarget == null) {
                notifier.cancelRunning()
                return@withLock
            }

            val nowWall = clock.nowMillis()
            if (storedTarget <= nowWall + COLD_PROCESS_TOLERANCE_MILLIS) {
                workoutRepository.setRestTargetAt(session.id, null)
                notifier.cancelRunning()
                notifyFinished(session.id)
            } else {
                resumeFrom(session.id, storedTarget, nowWall)
            }
        }
    }

    /**
     * REST_TIMER section 7. Branches: no target (no-op), target in the future (resume),
     * target in the past (clear, no alert). Called from MainActivity/SessionViewModel.
     */
    override suspend fun restore() {
        mutex.withLock {
            if (_uiModel.value != null) {
                refresh()
                return@withLock
            }
            val session = workoutRepository.observeInProgressSession().first() ?: return@withLock
            val storedTarget = session.restTargetAt ?: return@withLock
            val nowWall = clock.nowMillis()
            if (storedTarget > nowWall) {
                resumeFrom(session.id, storedTarget, nowWall)
            } else {
                workoutRepository.setRestTargetAt(session.id, null)
                clearState()
            }
        }
    }

    /** Recomputes [RestTimerUiModel.remainingMillis] from the elapsed clock (UI ticker). */
    override fun refresh() {
        val model = _uiModel.value ?: return
        _uiModel.value = model.copy(remainingMillis = currentRemaining())
    }

    /** App moved between foreground and background while the timer runs. */
    override fun onForegroundChanged(inForeground: Boolean) {
        val currentSessionId = sessionId ?: return
        if (inForeground) {
            notifier.cancelRunning()
        } else {
            notifier.showRunning(currentSessionId, targetWall)
        }
    }

    private suspend fun notifyFinished(finishedSessionId: String) {
        if (foregroundState.isInForeground()) {
            _events.emit(TimerEvent.Finished(finishedSessionId))
        } else {
            notifier.showFinished(finishedSessionId)
        }
    }

    /** REST_TIMER section 7 + D6: the remaining time becomes the total (no extra column). */
    private fun resumeFrom(sessionId: String, storedTargetWall: Long, nowWall: Long) {
        val remaining = storedTargetWall - nowWall
        this.sessionId = sessionId
        targetWall = storedTargetWall
        totalMillis = remaining
        targetElapsed = elapsedClock.elapsedRealtime() + remaining
        val exact = scheduler.schedule(targetElapsed)
        publish(isExact = exact)
    }

    private fun publish(isExact: Boolean) {
        val id = sessionId ?: return
        _uiModel.value = RestTimerUiModel(
            sessionId = id,
            remainingMillis = currentRemaining(),
            totalMillis = totalMillis,
            isExact = isExact,
            isRunning = true,
        )
    }

    private fun currentRemaining(): Long = RestTimerMath.remainingMillis(
        state = RestTimerState(targetElapsedRealtime = targetElapsed, totalMillis = totalMillis),
        nowElapsed = elapsedClock.elapsedRealtime(),
    )

    private fun clearState() {
        sessionId = null
        targetElapsed = 0L
        targetWall = 0L
        totalMillis = 0L
        _uiModel.value = null
    }

    companion object {
        /** D5: absorbs wall-clock vs elapsedRealtime drift when judging a cold-process alarm. */
        const val COLD_PROCESS_TOLERANCE_MILLIS = 5_000L
    }
}
