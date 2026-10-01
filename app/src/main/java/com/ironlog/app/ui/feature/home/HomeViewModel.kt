package com.ironlog.app.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.core.time.Clock
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.fatigue.FatigueRepository
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.WeekCalendar
import com.ironlog.app.domain.summary.WeekStats
import com.ironlog.app.domain.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomePrimaryAction { START, RESUME }

data class HomeUiState(
    val loading: Boolean = true,
    val inProgress: WorkoutSession? = null,
    val primaryAction: HomePrimaryAction = HomePrimaryAction.START,
    val elapsedSeconds: Long = 0L,
    val weekStats: WeekStats = WeekStats(sessionCount = 0, workSetCount = 0),
    val recentSessions: List<SessionHeader> = emptyList(),
    val fatigueScores: Map<String, Int> = emptyMap(),
    val muscleNames: Map<String, String> = emptyMap(),
    val heatmap: HeatmapState = HeatmapState.Loading,
)

sealed interface HomeEvent {
    data class OpenSession(val sessionId: String) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val fatigueRepository: FatigueRepository,
    private val exerciseRepository: ExerciseRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            workoutRepository.observeInProgressSession().collect { session ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        inProgress = session,
                        primaryAction = if (session == null) {
                            HomePrimaryAction.START
                        } else {
                            HomePrimaryAction.RESUME
                        },
                        elapsedSeconds = session?.let(::elapsedSeconds) ?: 0L,
                    )
                }
            }
        }
        viewModelScope.launch {
            val weekStart = WeekCalendar.weekStartMillis(clock)
            workoutRepository.observeWeekStats(weekStart).collect { stats ->
                _uiState.update { it.copy(weekStats = stats) }
            }
        }
        viewModelScope.launch {
            workoutRepository.observeRecentSessionHeaders(RECENT_LIMIT).collect { headers ->
                _uiState.update { it.copy(recentSessions = headers) }
            }
        }
        viewModelScope.launch {
            exerciseRepository.observeMuscleGroups().collect { groups ->
                _uiState.update { it.copy(muscleNames = groups.associate { group -> group.id to group.displayNameZh }) }
            }
        }
        viewModelScope.launch { loadFatigue() }
    }

    /** DOMAIN_RULES §5; called again whenever the home screen becomes visible. */
    fun refreshFatigue() {
        viewModelScope.launch { loadFatigue() }
    }

    private suspend fun loadFatigue() {
        try {
            val scores = fatigueRepository.scores(clock.nowMillis())
            val cardio = scores["cardio"] ?: 0
            val anatomical = (scores - "cardio").filterKeys { it in HeatmapCanonical.ids }
            _uiState.update {
                it.copy(
                    fatigueScores = anatomical,
                    heatmap = if (anatomical.isEmpty() && cardio == 0) HeatmapState.Empty
                    else HeatmapState.Ready(
                        HeatmapRenderModel(
                            view = (it.heatmap as? HeatmapState.Ready)?.model?.view ?: BodyView.FRONT,
                            scores = anatomical,
                            selectedMuscleId = (it.heatmap as? HeatmapState.Ready)?.model?.selectedMuscleId
                                ?.takeIf { id -> id in HeatmapCanonical.ids },
                        ),
                        cardio,
                    ),
                )
            }
        } catch (error: Exception) {
            _uiState.update { it.copy(fatigueScores = emptyMap(), heatmap = HeatmapState.Error(error.message)) }
        }
    }

    fun onMuscleSelected(muscleId: String?) {
        _uiState.update { state ->
            val selected = muscleId?.takeIf { id -> id in HeatmapCanonical.ids }
            val heatmap = state.heatmap
            if (heatmap is HeatmapState.Ready) {
                state.copy(heatmap = heatmap.copy(model = heatmap.model.copy(selectedMuscleId = selected)))
            } else {
                state
            }
        }
    }

    fun onBodyViewSelected(view: BodyView) {
        _uiState.update { state ->
            val heatmap = state.heatmap
            if (heatmap is HeatmapState.Ready) {
                val selected = heatmap.model.selectedMuscleId?.takeIf { id ->
                    MusclePathTable.forView(view).any { it.interactive && it.canonicalMuscleId == id }
                }
                state.copy(heatmap = heatmap.copy(model = heatmap.model.copy(view = view, selectedMuscleId = selected)))
            } else state
        }
    }

    fun onPrimaryAction() {
        viewModelScope.launch {
            val sessionId = workoutRepository.startSession()
            _events.send(HomeEvent.OpenSession(sessionId))
        }
    }

    /** Called once per second by the UI so elapsed time stays in sync with [Clock]. */
    fun refreshElapsed() {
        val session = _uiState.value.inProgress ?: return
        _uiState.update { it.copy(elapsedSeconds = elapsedSeconds(session)) }
    }

    private fun elapsedSeconds(session: WorkoutSession): Long =
        ((clock.nowMillis() - session.startedAt) / 1000).coerceAtLeast(0L)

    private companion object {
        const val RECENT_LIMIT = 3
    }
}
