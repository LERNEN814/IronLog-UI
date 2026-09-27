package com.ironlog.app.testutil

import com.ironlog.app.domain.timer.RestTimer
import com.ironlog.app.domain.timer.RestTimerUiModel
import com.ironlog.app.domain.timer.TimerEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** In-memory RestTimer for ViewModel tests. */
class FakeRestTimer : RestTimer {

    private val _uiModel = MutableStateFlow<RestTimerUiModel?>(null)
    override val uiModel: StateFlow<RestTimerUiModel?> = _uiModel

    private val _events = MutableSharedFlow<TimerEvent>(extraBufferCapacity = 1)
    override val events: SharedFlow<TimerEvent> = _events

    val started = mutableListOf<Pair<String, Int>>()
    val adjusted = mutableListOf<Int>()
    val foregroundChanges = mutableListOf<Boolean>()
    var skipCount = 0
        private set
    var finishCount = 0
        private set
    var restoreCount = 0
        private set
    var refreshCount = 0
        private set

    suspend fun emitFinished(sessionId: String) {
        _events.emit(TimerEvent.Finished(sessionId))
    }

    override suspend fun start(sessionId: String, seconds: Int) {
        started += sessionId to seconds
        _uiModel.value = RestTimerUiModel(
            sessionId = sessionId,
            remainingMillis = seconds * 1000L,
            totalMillis = seconds * 1000L,
            isExact = true,
            isRunning = true,
        )
    }

    override suspend fun adjust(deltaSeconds: Int) {
        adjusted += deltaSeconds
        _uiModel.value = _uiModel.value?.let {
            it.copy(remainingMillis = it.remainingMillis + deltaSeconds * 1000L)
        }
    }

    override suspend fun skip() {
        skipCount += 1
        _uiModel.value = null
    }

    override suspend fun finish() {
        finishCount += 1
        _uiModel.value = null
    }

    override suspend fun restore() {
        restoreCount += 1
    }

    override fun refresh() {
        refreshCount += 1
    }

    override fun onForegroundChanged(inForeground: Boolean) {
        foregroundChanges += inForeground
    }
}
