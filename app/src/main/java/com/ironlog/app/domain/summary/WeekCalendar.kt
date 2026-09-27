package com.ironlog.app.domain.summary

import com.ironlog.app.core.time.Clock
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

/** Local calendar helpers. */
object WeekCalendar {

    /** Monday 00:00 of the current week (clock zone) as UTC millis. */
    fun weekStartMillis(clock: Clock): Long {
        val today = clock.today()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.atStartOfDay(clock.zone()).toInstant().toEpochMilli()
    }
}

/** Duration rendering shared by home/session/history. */
object DurationText {

    /** 65 -> "01:05"; hours keep counting in minutes (e.g. 3670 -> "61:10"). */
    fun mmss(totalSeconds: Long): String {
        val safe = totalSeconds.coerceAtLeast(0)
        val minutes = safe / 60
        val seconds = safe % 60
        return minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
    }
}
