package com.ironlog.app.domain.timer

/** Lets the rest timer know whether the app is visible (drives in-app vs notification alerting). */
interface AppForegroundState {
    fun isInForeground(): Boolean
}
