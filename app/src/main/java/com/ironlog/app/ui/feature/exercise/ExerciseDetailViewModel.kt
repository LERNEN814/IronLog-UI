package com.ironlog.app.ui.feature.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseDetailUiState(
    val loading: Boolean = true,
    val exercise: Exercise? = null,
    val muscles: List<MuscleGroup> = emptyList(),
    val error: Boolean = false,
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val repository: ExerciseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    fun load(exerciseId: String) {
        if (exerciseId.isBlank() || !_uiState.value.loading) return
        viewModelScope.launch {
            val exercise = repository.getById(exerciseId)
            val mappings = if (exercise == null) emptyList() else repository.getMusclesFor(exerciseId)
            val byId = repository.observeMuscleGroups().first().associateBy { it.id }
            val muscles = mappings.sortedBy { it.role.ordinal }.mapNotNull { byId[it.muscleId] }
            _uiState.update { it.copy(loading = false, exercise = exercise, muscles = muscles, error = exercise == null) }
        }
    }
}
