package com.ironlog.app.platform.timer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ironlog.app.domain.timer.RestTimer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST_TIMER section 7.1 (D5): process foreground changes drive the ongoing notification.
 * Registered on ProcessLifecycleOwner in [com.ironlog.app.IronLogApp].
 */
@Singleton
class ForegroundTimerBridge @Inject constructor(
    private val restTimer: RestTimer,
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        restTimer.onForegroundChanged(inForeground = true)
    }

    override fun onStop(owner: LifecycleOwner) {
        restTimer.onForegroundChanged(inForeground = false)
    }
}
