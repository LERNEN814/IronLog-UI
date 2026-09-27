package com.ironlog.app.domain.timer

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** Everything the timer UI needs. [remainingMillis] is refreshed by [refresh] and on every write. */
data class RestTimerUiModel(
    val sessionId: String,
    val remainingMillis: Long,
    val totalMillis: Long,
    val isExact: Boolean,
    val isRunning: Boolean,
)

sealed interface TimerEvent {
    data class Finished(val sessionId: String) : TimerEvent
}

/** Rest-timer contract consumed by the UI layer; implemented by data/timer/RestTimerRepository. */
interface RestTimer {
    val uiModel: StateFlow<RestTimerUiModel?>
    val events: SharedFlow<TimerEvent>

    suspend fun start(sessionId: String, seconds: Int)
    suspend fun adjust(deltaSeconds: Int)
    suspend fun skip()

    /** Cancels without emitting a completion event (session finished/discarded, user skip). */
    suspend fun finish()

    /** REST_TIMER section 7; also called when the app returns to the foreground. */
    suspend fun restore()

    /** Recomputes remainingMillis from the elapsed clock (UI ticker). */
    fun refresh()

    fun onForegroundChanged(inForeground: Boolean)
}
