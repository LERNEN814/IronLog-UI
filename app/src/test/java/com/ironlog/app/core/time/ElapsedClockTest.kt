package com.ironlog.app.core.time

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ElapsedClockTest {

    @Test
    fun fakeElapsedClockStartsAtZeroAndAdvances() {
        val clock = FakeElapsedClock()

        assertThat(clock.elapsedRealtime()).isEqualTo(0L)
        clock.advanceSeconds(90)
        assertThat(clock.elapsedRealtime()).isEqualTo(90_000L)
    }

    @Test
    fun fakeElapsedClockCanBeConstructedAtAnArbitraryBase() {
        val clock = FakeElapsedClock(1_000_000L)

        assertThat(clock.elapsedRealtime()).isEqualTo(1_000_000L)
    }
}
