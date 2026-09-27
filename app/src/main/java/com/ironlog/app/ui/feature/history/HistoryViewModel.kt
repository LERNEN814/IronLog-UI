package com.ironlog.app.ui.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val loading: Boolean = true,
    val sessions: List<SessionHeader> = emptyList(),
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepository.observeRecentSessionHeaders(HISTORY_LIMIT).collect { headers ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        sessions = headers.sortedByDescending { header -> header.session.startedAt },
                    )
                }
            }
        }
    }

    private companion object {
        const val HISTORY_LIMIT = 50
    }
}
