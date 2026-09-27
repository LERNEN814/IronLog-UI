package com.ironlog.app.ui.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.core.time.Clock
import com.ironlog.app.domain.calendar.CalendarModel
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarDayUiState(
    val date: LocalDate,
    val isToday: Boolean,
    val dots: List<Int>,
)

data class CalendarUiState(
    val today: LocalDate,
    val year: Int,
    val month: Int,
    val days: List<CalendarDayUiState?> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedSessions: List<SessionHeader> = emptyList(),
    val listMode: Boolean = false,
)

/** M4-T4.2: month calendar built from the same session headers as the history list. */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        clock.today().let { today ->
            CalendarUiState(today = today, year = today.year, month = today.monthValue)
        },
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var headers: List<SessionHeader> = emptyList()

    init {
        viewModelScope.launch {
            workoutRepository.observeRecentSessionHeaders(CALENDAR_LIMIT).collect { loaded ->
                headers = loaded
                rebuild()
            }
        }
    }

    fun onPreviousMonth() {
        _uiState.update { state ->
            if (state.month == 1) {
                state.copy(year = state.year - 1, month = 12)
            } else {
                state.copy(month = state.month - 1)
            }
        }
        rebuild()
    }

    fun onNextMonth() {
        _uiState.update { state ->
            if (state.month == 12) {
                state.copy(year = state.year + 1, month = 1)
            } else {
                state.copy(month = state.month + 1)
            }
        }
        rebuild()
    }

    fun onToday() {
        _uiState.update { it.copy(year = it.today.year, month = it.today.monthValue) }
        rebuild()
    }

    fun onDateSelected(date: LocalDate?) {
        _uiState.update { it.copy(selectedDate = date) }
        rebuild()
    }

    fun onListModeToggled() {
        _uiState.update { it.copy(listMode = !it.listMode) }
    }

    private fun rebuild() {
        val state = _uiState.value
        val sessionsByDate = headers.groupBy { LocalDate.parse(it.session.localDate) }
        val details = headers.associate { it.session.id to it.bodyRegions }
        val days = CalendarModel.monthGrid(state.year, state.month).map { date ->
            date?.let {
                CalendarDayUiState(
                    date = it,
                    isToday = it == state.today,
                    dots = CalendarModel.regionDots(
                        sessions = sessionsByDate[it].orEmpty().map { header -> header.session },
                        details = details,
                    ),
                )
            }
        }
        _uiState.update {
            it.copy(
                days = days,
                selectedSessions = state.selectedDate?.let { date -> sessionsByDate[date].orEmpty() } ?: emptyList(),
            )
        }
    }

    private companion object {
        const val CALENDAR_LIMIT = 500
    }
}
