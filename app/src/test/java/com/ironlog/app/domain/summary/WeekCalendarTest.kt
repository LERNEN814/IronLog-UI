package com.ironlog.app.domain.summary

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import java.time.ZoneId
import org.junit.Test

class WeekCalendarTest {

    @Test
    fun weekStartIsMondayMidnightInClockZone() {
        // 2026-01-01T12:00:00Z is a Thursday; the week starts 2025-12-29T00:00:00Z.
        val clock = FixedClock(1_767_268_800_000L, ZoneId.of("UTC"))

        assertThat(WeekCalendar.weekStartMillis(clock)).isEqualTo(1_766_966_400_000L)
    }

    @Test
    fun weekStartUsesTheLocalDateNotUtc() {
        // 2026-01-01T20:00:00Z is already 2026-01-02 04:00 in Shanghai (Friday in both zones here,
        // but the returned instant must be Monday 00:00 Shanghai = Sunday 16:00 UTC).
        val clock = FixedClock(1_767_297_600_000L, ZoneId.of("Asia/Shanghai"))

        val weekStart = WeekCalendar.weekStartMillis(clock)

        assertThat(clock.localDateOf(weekStart).toString()).isEqualTo("2025-12-29")
        assertThat(weekStart % 1_000L).isEqualTo(0L)
    }

    @Test
    fun durationTextFormatsMinutesAndSeconds() {
        assertThat(DurationText.mmss(0)).isEqualTo("00:00")
        assertThat(DurationText.mmss(65)).isEqualTo("01:05")
        assertThat(DurationText.mmss(3_670)).isEqualTo("61:10")
        assertThat(DurationText.mmss(-5)).isEqualTo("00:00")
    }
}
