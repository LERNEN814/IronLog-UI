package com.ironlog.app.platform.timer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.ironlog.app.domain.timer.AppForegroundState
import javax.inject.Inject
import javax.inject.Singleton

/** Foreground state from ProcessLifecycleOwner (REST_TIMER sections 6/7). */
@Singleton
class ProcessForegroundState @Inject constructor() : AppForegroundState {
    override fun isInForeground(): Boolean =
        ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
}
