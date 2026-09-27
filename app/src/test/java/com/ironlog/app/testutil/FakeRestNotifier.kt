package com.ironlog.app.testutil

import com.ironlog.app.domain.timer.AppForegroundState
import com.ironlog.app.domain.timer.RestNotifier

class FakeRestNotifier : RestNotifier {

    val runningCalls = mutableListOf<Pair<String, Long>>()
    val finishedCalls = mutableListOf<String>()
    var cancelCount = 0
        private set

    override fun showRunning(sessionId: String, targetWallMillis: Long) {
        runningCalls += sessionId to targetWallMillis
    }

    override fun cancelRunning() {
        cancelCount += 1
    }

    override fun showFinished(sessionId: String) {
        finishedCalls += sessionId
    }
}

class FakeForegroundState(var inForeground: Boolean = true) : AppForegroundState {
    override fun isInForeground(): Boolean = inForeground
}
