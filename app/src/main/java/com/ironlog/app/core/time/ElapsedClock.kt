package com.ironlog.app.core.time

import android.os.SystemClock

/**
 * Monotonic clock for countdowns. This is the second allowed system time source (after Clock);
 * `SystemClock.elapsedRealtime()` is immune to wall-clock changes.
 */
interface ElapsedClock {
    fun elapsedRealtime(): Long
}

object SystemElapsedClock : ElapsedClock {
    override fun elapsedRealtime(): Long = SystemClock.elapsedRealtime()
}

/** Test double. */
class FakeElapsedClock(var millis: Long = 0L) : ElapsedClock {
    override fun elapsedRealtime(): Long = millis

    fun advanceSeconds(seconds: Long) {
        millis += seconds * 1000
    }
}
