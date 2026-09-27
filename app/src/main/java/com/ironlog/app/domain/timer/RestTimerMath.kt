package com.ironlog.app.domain.timer

/** Timer state on the elapsedRealtime clock. [totalMillis] is the adjustable total. */
data class RestTimerState(
    val targetElapsedRealtime: Long,
    val totalMillis: Long,
)

/** DOMAIN_RULES.md section 7: pure rest-timer math. */
object RestTimerMath {

    fun startState(totalMillis: Long, nowElapsed: Long): RestTimerState = RestTimerState(
        targetElapsedRealtime = nowElapsed + totalMillis.coerceAtLeast(0L),
        totalMillis = totalMillis.coerceAtLeast(0L),
    )

    fun remainingMillis(state: RestTimerState, nowElapsed: Long): Long =
        (state.targetElapsedRealtime - nowElapsed).coerceAtLeast(0L)

    /** 1.0 at start, 0.0 when finished. */
    fun progress(state: RestTimerState, nowElapsed: Long): Float {
        if (state.totalMillis <= 0L) return 0f
        return (remainingMillis(state, nowElapsed).toFloat() / state.totalMillis.toFloat()).coerceIn(0f, 1f)
    }

    /** Moves the target by the actually applied delta, keeping the remaining time non-negative. */
    fun adjust(state: RestTimerState, deltaSeconds: Int, nowElapsed: Long): RestTimerState {
        val remaining = remainingMillis(state, nowElapsed)
        val newRemaining = (remaining + deltaSeconds * 1000L).coerceAtLeast(0L)
        val applied = newRemaining - remaining
        return RestTimerState(
            targetElapsedRealtime = state.targetElapsedRealtime + applied,
            totalMillis = (state.totalMillis + applied).coerceAtLeast(0L),
        )
    }
}
