package com.ironlog.app.ui.feature.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.settings.SettingsRepository
import com.ironlog.app.domain.strength.StrengthTrend
import com.ironlog.app.domain.strength.TrendPoint
import com.ironlog.app.domain.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseHistoryUiState(
    val loading: Boolean = true,
    val exerciseName: String = "",
    val trend: List<TrendPoint> = emptyList(),
    val prDates: Set<String> = emptySet(),
    val displayUnit: WeightUnit = WeightUnit.KG,
)

/** M4-T4.4: best Epley 1RM per training day with PR markers. */
@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val exerciseId: String = checkNotNull(savedStateHandle.get<String>(EXERCISE_ID_KEY)) {
        "exerciseId missing from SavedStateHandle"
    }

    private val _uiState = MutableStateFlow(ExerciseHistoryUiState())
    val uiState: StateFlow<ExerciseHistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                _uiState.update { it.copy(displayUnit = settings.weightUnit) }
            }
        }
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        val exercise = exerciseRepository.getById(exerciseId)
        val sets = workoutRepository.getExerciseTrendSets(exerciseId)
        val trend = StrengthTrend.build(sets)
        val prDates = StrengthTrend.personalRecords(sets).map { it.date }.toSet()
        _uiState.update {
            it.copy(
                loading = false,
                exerciseName = exercise?.nameZh ?: exerciseId,
                trend = trend,
                prDates = prDates,
            )
        }
    }

    private companion object {
        const val EXERCISE_ID_KEY = "exerciseId"
    }
}
