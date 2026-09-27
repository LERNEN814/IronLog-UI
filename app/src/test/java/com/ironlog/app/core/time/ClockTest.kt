package com.ironlog.app.core.time

import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Test

class ClockTest {
    @Test
    fun localDateUsesClockZone() {
        // 2026-09-24T16:30:00Z == 2026-09-25 00:30 in Asia/Shanghai
        val clock = FixedClock(1_790_267_400_000L)
        assertThat(clock.today()).isEqualTo(LocalDate.of(2026, 9, 25))
    }

    @Test
    fun advanceSecondsMovesTime() {
        val clock = FixedClock(0L)
        clock.advanceSeconds(90)
        assertThat(clock.nowMillis()).isEqualTo(90_000L)
    }
}
