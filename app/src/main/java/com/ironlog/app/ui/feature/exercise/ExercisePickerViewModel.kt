package com.ironlog.app.ui.feature.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.domain.exercise.ExerciseFilter
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import com.ironlog.app.domain.model.MuscleRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExercisePickerUiState(
    val loading: Boolean = true,
    val query: String = "",
    val bodyRegion: Int? = null,
    val exercises: List<Exercise> = emptyList(),
    val muscleGroups: List<MuscleGroup> = emptyList(),
    val lastUsed: Map<String, Long> = emptyMap(),
    val formVisible: Boolean = false,
)

sealed interface ExercisePickerEvent {
    data class ExerciseSelected(val exerciseId: String) : ExercisePickerEvent
}

@HiltViewModel
class ExercisePickerViewModel @Inject constructor(
    private val repository: ExerciseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExercisePickerUiState())
    val uiState: StateFlow<ExercisePickerUiState> = _uiState.asStateFlow()

    private val _events = Channel<ExercisePickerEvent>(Channel.BUFFERED)
    val events: Flow<ExercisePickerEvent> = _events.receiveAsFlow()

    private val query = MutableStateFlow("")
    private val bodyRegion = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            combine(
                repository.observeAll(),
                repository.observeMuscleGroups(),
                repository.observeLastUsed(),
                query,
                bodyRegion,
            ) { exercises, groups, lastUsed, q, region -> PickerData(exercises, groups, lastUsed, q, region) }
                .collect { data ->
                    val regionByMuscle = data.muscleGroups.associate { it.id to it.bodyRegion }
                    val filtered = ExerciseFilter.filter(
                        exercises = data.exercises,
                        bodyRegionByMuscleId = regionByMuscle,
                        query = data.query,
                        bodyRegion = data.bodyRegion,
                    )
                    _uiState.update {
                        it.copy(
                            loading = false,
                            query = data.query,
                            bodyRegion = data.bodyRegion,
                            muscleGroups = data.muscleGroups,
                            lastUsed = data.lastUsed,
                            exercises = ExerciseFilter.sort(filtered, data.lastUsed),
                        )
                    }
                }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onRegionSelected(region: Int?) {
        bodyRegion.value = region
    }

    fun onShowCreateForm() {
        _uiState.update { it.copy(formVisible = true) }
    }

    fun onDismissCreateForm() {
        _uiState.update { it.copy(formVisible = false) }
    }

    fun onExerciseClicked(exerciseId: String) {
        viewModelScope.launch { _events.send(ExercisePickerEvent.ExerciseSelected(exerciseId)) }
    }

    fun onSaveCustom(
        nameZh: String,
        nameEn: String,
        kind: ExerciseKind,
        equipment: String,
        primaryMuscleId: String?,
        secondaryMuscleIds: List<String>,
    ) {
        viewModelScope.launch {
            val muscles = buildList {
                if (primaryMuscleId != null) {
                    add(ExerciseMuscle("", primaryMuscleId, MuscleRole.PRIMARY, 1.0))
                }
                secondaryMuscleIds
                    .filter { it != primaryMuscleId }
                    .forEach { add(ExerciseMuscle("", it, MuscleRole.SECONDARY, 0.5)) }
            }
            val id = repository.upsertCustom(
                id = null,
                nameZh = nameZh.trim(),
                nameEn = nameEn.trim(),
                kind = kind,
                equipment = equipment,
                primaryMuscleId = primaryMuscleId,
                muscles = muscles,
            )
            _uiState.update { it.copy(formVisible = false) }
            _events.send(ExercisePickerEvent.ExerciseSelected(id))
        }
    }
}

private data class PickerData(
    val exercises: List<Exercise>,
    val muscleGroups: List<MuscleGroup>,
    val lastUsed: Map<String, Long>,
    val query: String,
    val bodyRegion: Int?,
)
