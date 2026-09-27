package com.ironlog.app.domain.timer

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** DOMAIN_RULES.md section 7 vectors T1-T6 (total = 90 s, target = 190_000, start at now = 100_000). */
class RestTimerMathTest {

    private val state = RestTimerState(targetElapsedRealtime = 190_000L, totalMillis = 90_000L)

    @Test
    fun t1_remainingAtStart() {
        assertThat(RestTimerMath.remainingMillis(state, 100_000L)).isEqualTo(90_000L)
    }

    @Test
    fun t2_remainingMidway() {
        assertThat(RestTimerMath.remainingMillis(state, 145_000L)).isEqualTo(45_000L)
    }

    @Test
    fun t3_remainingIsClampedAtZero() {
        assertThat(RestTimerMath.remainingMillis(state, 200_000L)).isEqualTo(0L)
    }

    @Test
    fun t4_progressIsHalfway() {
        assertThat(RestTimerMath.progress(state, 145_000L)).isWithin(1e-6f).of(0.5f)
    }

    @Test
    fun progressIsOneAtStartAndZeroAtEnd() {
        assertThat(RestTimerMath.progress(state, 100_000L)).isWithin(1e-6f).of(1f)
        assertThat(RestTimerMath.progress(state, 190_000L)).isWithin(1e-6f).of(0f)
    }

    @Test
    fun t5_adjustPlusFifteen() {
        val adjusted = RestTimerMath.adjust(state, deltaSeconds = 15, nowElapsed = 145_000L)

        assertThat(adjusted).isEqualTo(RestTimerState(targetElapsedRealtime = 205_000L, totalMillis = 105_000L))
    }

    @Test
    fun t6_adjustMinusFifteenFourTimesClampsAtZero() {
        var adjusted = state
        repeat(4) {
            adjusted = RestTimerMath.adjust(adjusted, deltaSeconds = -15, nowElapsed = 145_000L)
        }

        assertThat(adjusted).isEqualTo(RestTimerState(targetElapsedRealtime = 145_000L, totalMillis = 45_000L))
        assertThat(RestTimerMath.remainingMillis(adjusted, 145_000L)).isEqualTo(0L)
    }

    @Test
    fun adjustAfterExpiryDoesNotMoveTheTarget() {
        val expired = RestTimerState(targetElapsedRealtime = 100_000L, totalMillis = 90_000L)

        val adjusted = RestTimerMath.adjust(expired, deltaSeconds = -15, nowElapsed = 200_000L)

        assertThat(adjusted).isEqualTo(expired)
    }

    @Test
    fun startStateSetsTargetFromNow() {
        assertThat(RestTimerMath.startState(90_000L, 100_000L))
            .isEqualTo(RestTimerState(targetElapsedRealtime = 190_000L, totalMillis = 90_000L))
    }
}
