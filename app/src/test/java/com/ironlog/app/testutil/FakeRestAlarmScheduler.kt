package com.ironlog.app.testutil

import com.ironlog.app.platform.timer.RestAlarmScheduler

/** Records scheduler calls for repository tests. */
class FakeRestAlarmScheduler : RestAlarmScheduler {

    val scheduledTargets = mutableListOf<Long>()
    var cancelCount = 0
        private set
    var exactResult = true

    override fun schedule(targetElapsed: Long): Boolean {
        scheduledTargets += targetElapsed
        return exactResult
    }

    override fun cancel() {
        cancelCount += 1
    }
}
