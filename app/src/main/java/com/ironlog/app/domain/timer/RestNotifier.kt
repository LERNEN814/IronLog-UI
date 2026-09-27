package com.ironlog.app.domain.timer

/** Bridge to the Android notification system so the repository stays testable. */
interface RestNotifier {
    /** Ongoing countdown notification (channel rest_running, silent). */
    fun showRunning(sessionId: String, targetWallMillis: Long)

    fun cancelRunning()

    /** "Rest finished" notification (channel rest_done, sound + vibration). */
    fun showFinished(sessionId: String)
}
