package com.ironlog.app.core.time

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * The only source of "now" in the app. Never call System.currentTimeMillis(),
 * Instant.now() or LocalDate.now() directly — inject Clock so tests can control time.
 */
interface Clock {
    fun nowMillis(): Long
    fun zone(): ZoneId

    fun today(): LocalDate = localDateOf(nowMillis())
    fun localDateOf(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone()).toLocalDate()
}

object SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
    override fun zone(): ZoneId = ZoneId.systemDefault()
}

/** Test double. */
class FixedClock(var millis: Long, private val zoneId: ZoneId = ZoneId.of("Asia/Shanghai")) : Clock {
    override fun nowMillis(): Long = millis
    override fun zone(): ZoneId = zoneId
    fun advanceSeconds(seconds: Long) {
        millis += seconds * 1000
    }
}
