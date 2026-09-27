package com.ironlog.app.ui.feature.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.keypad.KeypadKey
import com.ironlog.app.domain.keypad.KeypadPresets
import com.ironlog.app.domain.keypad.KeypadState
import com.ironlog.app.domain.keypad.press
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.strength.OneRepMax
import com.ironlog.app.domain.summary.ShareFileName
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.settings.SettingsRepository
import com.ironlog.app.domain.workout.WorkoutRepository
import com.ironlog.app.ui.feature.session.KeypadUiState
import com.ironlog.app.ui.feature.session.SetField
import com.ironlog.app.ui.feature.summary.ShareCardData
import com.ironlog.app.ui.feature.summary.ShareExerciseBest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailSetUiState(
    val set: WorkoutSet,
    val numberLabel: String,
    val weightText: String,
    val repsText: String,
    val durationText: String,
    val levelText: String,
    val inclineText: String,
    val speedText: String,
    val distanceText: String,
)

data class DetailEntryUiState(
    val entryId: String,
    val exerciseId: String,
    val exerciseName: String,
    val kind: ExerciseKind,
    val equipment: String,
    val sets: List<DetailSetUiState>,
)

data class SessionDetailUiState(
    val loading: Boolean = true,
    val localDate: String = "",
    val durationText: String = "",
    val volumeGrams: Int = 0,
    val workSetCount: Int = 0,
    val rating: Int? = null,
    val note: String = "",
    val entries: List<DetailEntryUiState> = emptyList(),
    val displayUnit: WeightUnit = WeightUnit.KG,
    val displayDistanceUnit: DistanceUnit = DistanceUnit.KM,
    val keypad: KeypadUiState? = null,
    val confirmDeleteVisible: Boolean = false,
    val shareData: ShareCardData? = null,
    val shareFileName: String = "",
)

sealed interface SessionDetailEvent {
    data class TemplateStarted(val sessionId: String) : SessionDetailEvent
    data class AlreadyInProgress(val sessionId: String?) : SessionDetailEvent
    data object SessionDeleted : SessionDetailEvent
}

/** M4-T4.3: history detail with editing. Never touches the rest timer (that belongs to sessions). */
@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: com.ironlog.app.core.time.Clock,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle.get<String>(SESSION_ID_KEY)) {
        "sessionId missing from SavedStateHandle"
    }

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<SessionDetailEvent>(Channel.BUFFERED)
    val events: Flow<SessionDetailEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch { load() }
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                val unitChanged = _uiState.value.displayUnit != settings.weightUnit ||
                    _uiState.value.displayDistanceUnit != settings.distanceUnit
                _uiState.update {
                    it.copy(
                        displayUnit = settings.weightUnit,
                        displayDistanceUnit = settings.distanceUnit,
                    )
                }
                if (unitChanged) renderRows()
            }
        }
    }

    fun onRatingSelected(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
        viewModelScope.launch {
            workoutRepository.updateSessionRatingAndNote(sessionId, rating, _uiState.value.note.ifBlank { null })
        }
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(note = note) }
        viewModelScope.launch {
            workoutRepository.updateSessionRatingAndNote(sessionId, _uiState.value.rating, note.ifBlank { null })
        }
    }

    fun onDeleteClicked() {
        _uiState.update { it.copy(confirmDeleteVisible = true) }
    }

    fun onDismissDelete() {
        _uiState.update { it.copy(confirmDeleteVisible = false) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            workoutRepository.deleteSession(sessionId)
            _events.send(SessionDetailEvent.SessionDeleted)
        }
    }

    /** D7: the repository either creates the whole template session atomically or refuses. */
    fun onStartTemplate() {
        viewModelScope.launch {
            val newSessionId = workoutRepository.startSessionFromTemplate(sessionId)
            if (newSessionId != null) {
                _events.send(SessionDetailEvent.TemplateStarted(newSessionId))
            } else {
                val inProgressId = workoutRepository.observeInProgressSession().first()?.id
                _events.send(SessionDetailEvent.AlreadyInProgress(inProgressId))
            }
        }
    }

    fun onSetTypeChanged(setId: String, setType: SetType) {
        viewModelScope.launch {
            val set = findSet(setId) ?: return@launch
            workoutRepository.updateSet(set.copy(setType = setType))
            load()
        }
    }

    fun onAddSet(entryId: String) {
        viewModelScope.launch {
            val sets = workoutRepository.getSetsFor(entryId)
            val previous = sets.lastOrNull()
            workoutRepository.addSet(
                entryId = entryId,
                orderIndex = sets.size,
                setType = previous?.setType ?: SetType.WORK,
                inputUnit = _uiState.value.displayUnit,
            )
            load()
        }
    }

    fun onDeleteSet(setId: String) {
        viewModelScope.launch {
            workoutRepository.deleteSet(setId)
            load()
        }
    }

    fun onDeleteEntry(entryId: String) {
        viewModelScope.launch {
            workoutRepository.deleteEntry(entryId)
            load()
        }
    }

    fun onFieldClicked(setId: String, field: SetField) {
        val entry = entryOfSet(setId) ?: return
        val row = entry.sets.firstOrNull { it.set.id == setId } ?: return
        val currentText = when (field) {
            SetField.WEIGHT -> row.weightText
            SetField.REPS -> row.repsText
            SetField.RIR -> row.set.rir?.toString().orEmpty()
            SetField.DURATION -> durationInputText(row.set, entry.kind)
            SetField.LEVEL -> row.levelText
            SetField.INCLINE -> row.inclineText
            SetField.SPEED -> row.speedText
            SetField.DISTANCE -> row.distanceText
        }
        val preset = when (field) {
            SetField.WEIGHT -> KeypadPresets.WEIGHT
            SetField.DISTANCE -> KeypadPresets.DISTANCE
            SetField.RIR -> KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 1)
            SetField.DURATION -> if (entry.kind == ExerciseKind.CARDIO) KeypadPresets.MINUTES else KeypadPresets.SECONDS
            else -> KeypadPresets.REPS
        }
        _uiState.update {
            it.copy(keypad = KeypadUiState(setId = setId, field = field, keypadState = preset.copy(text = currentText)))
        }
    }

    fun onKeypadKey(key: KeypadKey) {
        _uiState.update { state ->
            val keypad = state.keypad ?: return@update state
            state.copy(keypad = keypad.copy(keypadState = keypad.keypadState.press(key)))
        }
    }

    fun onKeypadDismiss() {
        _uiState.update { it.copy(keypad = null) }
    }

    fun onKeypadDone() {
        val keypad = _uiState.value.keypad ?: return
        val entry = entryOfSet(keypad.setId) ?: return
        val set = findSet(keypad.setId) ?: return
        val updated = applyFieldValue(set, keypad.field, keypad.keypadState.text, entry.kind) ?: return
        viewModelScope.launch {
            workoutRepository.updateSet(updated)
            _uiState.update { it.copy(keypad = null) }
            load()
        }
    }

    private suspend fun load() {
        val detail = workoutRepository.getSessionDetail(sessionId)
        if (detail == null) {
            _uiState.update { it.copy(loading = false) }
            return
        }
        val entries = detail.entries.map { entry ->
            val exercise = exerciseRepository.getById(entry.entry.exerciseId)
            buildEntry(
                entryId = entry.entry.id,
                exerciseId = entry.entry.exerciseId,
                exerciseName = exercise?.nameZh ?: entry.entry.exerciseId,
                kind = exercise?.kind ?: ExerciseKind.STRENGTH,
                equipment = exercise?.equipment.orEmpty(),
                sets = entry.sets,
            )
        }
        _uiState.update {
            it.copy(
                loading = false,
                localDate = detail.header.session.localDate,
                durationText = UnitConverter.formatDuration(detail.header.summary.durationS.toInt()),
                volumeGrams = detail.header.summary.totalVolumeGrams.toInt(),
                workSetCount = detail.header.summary.workSetCount,
                rating = detail.header.session.rating,
                note = detail.header.session.note.orEmpty(),
                entries = entries,
                shareData = buildShareCardData(detail, it.displayUnit),
                shareFileName = ShareFileName.format(clock.nowMillis(), clock.zone()),
            )
        }
    }

    private suspend fun buildShareCardData(
        detail: com.ironlog.app.domain.summary.SessionDetail,
        displayUnit: WeightUnit,
    ): ShareCardData {
        val exercises = detail.entries.map { entry ->
            val exercise = exerciseRepository.getById(entry.entry.exerciseId)
            val best = entry.sets
                .filter { it.isCompleted && it.setType != SetType.WARMUP }
                .mapNotNull { set ->
                    val weight = set.weightGrams ?: return@mapNotNull null
                    val reps = set.reps ?: return@mapNotNull null
                    val e1rm = OneRepMax.estimate(weight, reps) ?: return@mapNotNull null
                    BestSet(e1rm = e1rm, weightGrams = weight, reps = reps)
                }
                .maxByOrNull { it.e1rm }
            val historyMax = workoutRepository.getExerciseTrendSets(entry.entry.exerciseId)
                .filter { it.sessionId != sessionId && it.isCompleted && it.setType != SetType.WARMUP }
                .mapNotNull { set ->
                    val weight = set.weightGrams ?: return@mapNotNull null
                    val reps = set.reps ?: return@mapNotNull null
                    OneRepMax.estimate(weight, reps)
                }
                .maxOrNull() ?: 0
            ShareExerciseBest(
                name = exercise?.nameZh ?: entry.entry.exerciseId,
                bestSetText = best?.let { "${it.weightGrams} x ${it.reps}" }.orEmpty(),
                isPr = best != null && best.e1rm > historyMax,
            )
        }
        return ShareCardData(
            date = detail.header.session.localDate,
            durationText = UnitConverter.formatDuration(detail.header.summary.durationS.toInt()),
            volumeText = UnitConverter.formatGrams(detail.header.summary.totalVolumeGrams.toInt(), displayUnit) +
                if (displayUnit == WeightUnit.KG) "kg" else "lb",
            exercises = exercises,
            bodyRegions = detail.header.bodyRegions.sorted(),
            appName = "IronLog",
        )
    }

    private data class BestSet(val e1rm: Int, val weightGrams: Int, val reps: Int)

    /** Re-renders value texts after a display-unit change without hitting the database again. */
    private fun renderRows() {
        _uiState.update { current ->
            current.copy(
                entries = current.entries.map { entry ->
                    entry.copy(sets = rebuildRows(entry.sets))
                },
            )
        }
    }

    private fun rebuildRows(sets: List<DetailSetUiState>): List<DetailSetUiState> =
        sets.map { row -> buildRow(row.set, row.numberLabel) }

    private fun buildEntry(
        entryId: String,
        exerciseId: String,
        exerciseName: String,
        kind: ExerciseKind,
        equipment: String,
        sets: List<WorkoutSet>,
    ): DetailEntryUiState {
        var workNumber = 0
        val rows = sets.map { set ->
            val label = when (set.setType) {
                SetType.WARMUP -> "W"
                SetType.DROP -> "D"
                SetType.FAILURE -> "F"
                SetType.WORK -> {
                    workNumber += 1
                    workNumber.toString()
                }
            }
            buildRow(set, label)
        }
        return DetailEntryUiState(
            entryId = entryId,
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            kind = kind,
            equipment = equipment,
            sets = rows,
        )
    }

    private fun buildRow(set: WorkoutSet, numberLabel: String): DetailSetUiState = DetailSetUiState(
        set = set,
        numberLabel = numberLabel,
        weightText = set.weightGrams?.let { UnitConverter.formatGrams(it, _uiState.value.displayUnit) }.orEmpty(),
        repsText = set.reps?.toString().orEmpty(),
        durationText = set.durationS?.let { UnitConverter.formatDuration(it) }.orEmpty(),
        levelText = set.level?.toString().orEmpty(),
        inclineText = set.inclineX10?.let { UnitConverter.formatTenths(it) }.orEmpty(),
        speedText = set.speedX10?.let { UnitConverter.formatTenths(it) }.orEmpty(),
        distanceText = set.distanceM?.let { UnitConverter.formatMeters(it, _uiState.value.displayDistanceUnit) }.orEmpty(),
    )

    private fun durationInputText(set: WorkoutSet, kind: ExerciseKind): String {
        val durationS = set.durationS ?: return ""
        return if (kind == ExerciseKind.CARDIO) (durationS / 60).toString() else durationS.toString()
    }

    private fun applyFieldValue(set: WorkoutSet, field: SetField, text: String, kind: ExerciseKind): WorkoutSet? =
        when (field) {
            SetField.WEIGHT -> UnitConverter.parseToGrams(text, _uiState.value.displayUnit)
                ?.let { set.copy(weightGrams = it, inputUnit = _uiState.value.displayUnit) }

            SetField.REPS -> text.toIntOrNull()?.takeIf { it in 0..999 }?.let { set.copy(reps = it) }
            SetField.RIR -> text.toIntOrNull()?.takeIf { it in 0..5 }?.let { set.copy(rir = it) }
            SetField.DURATION -> text.toIntOrNull()
                ?.takeIf { it > 0 }
                ?.times(if (kind == ExerciseKind.CARDIO) 60 else 1)
                ?.let { set.copy(durationS = it) }

            SetField.LEVEL -> text.toIntOrNull()?.takeIf { it > 0 }?.let { set.copy(level = it) }
            SetField.INCLINE -> UnitConverter.parseTenths(text)?.let { set.copy(inclineX10 = it) }
            SetField.SPEED -> UnitConverter.parseTenths(text)?.let { set.copy(speedX10 = it) }
            SetField.DISTANCE -> UnitConverter.parseDistanceToMeters(text, _uiState.value.displayDistanceUnit)
                ?.let { set.copy(distanceM = it) }
        }

    private fun findSet(setId: String): WorkoutSet? =
        _uiState.value.entries.asSequence()
            .flatMap { it.sets.asSequence() }
            .firstOrNull { it.set.id == setId }
            ?.set

    private fun entryOfSet(setId: String): DetailEntryUiState? =
        _uiState.value.entries.firstOrNull { entry -> entry.sets.any { it.set.id == setId } }

    private companion object {
        const val SESSION_ID_KEY = "sessionId"
    }
}
