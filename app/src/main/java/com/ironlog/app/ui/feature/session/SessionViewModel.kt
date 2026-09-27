package com.ironlog.app.ui.feature.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.core.time.Clock
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.keypad.KeypadKey
import com.ironlog.app.domain.keypad.KeypadPresets
import com.ironlog.app.domain.keypad.KeypadState
import com.ironlog.app.domain.keypad.WeightStepper
import com.ironlog.app.domain.keypad.press
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.MuscleRole
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.prefill.PrefillPlanner
import com.ironlog.app.domain.prefill.PrefillSet
import com.ironlog.app.domain.settings.SettingsRepository
import com.ironlog.app.domain.strength.OneRepMax
import com.ironlog.app.domain.strength.PersonalRecord
import com.ironlog.app.domain.timer.RestTimer
import com.ironlog.app.domain.timer.RestTimerUiModel
import com.ironlog.app.domain.summary.ExerciseMeta
import com.ironlog.app.domain.summary.SessionExerciseWithSets
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.domain.summary.summarize
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

enum class SetField { WEIGHT, REPS, RIR, DURATION, LEVEL, INCLINE, SPEED, DISTANCE }

data class SetRowUiState(
    val set: WorkoutSet,
    val placeholder: PrefillSet?,
    val numberLabel: String,
    val isPlaceholder: Boolean,
    val isPr: Boolean = false,
    val weightText: String,
    val repsText: String,
    val durationText: String,
    val levelText: String,
    val speedText: String,
    val inclineText: String,
    val distanceText: String,
)

data class EntryUiState(
    val entry: SessionExercise,
    val exercise: Exercise?,
    val primaryBodyRegion: Int?,
    val lastSets: List<WorkoutSet>,
    val sets: List<SetRowUiState>,
)

data class KeypadUiState(
    val setId: String,
    val field: SetField,
    val keypadState: KeypadState,
)

data class SessionUiState(
    val loading: Boolean = true,
    val session: WorkoutSession? = null,
    val entries: List<EntryUiState> = emptyList(),
    val displayUnit: WeightUnit = WeightUnit.KG,
    val displayDistanceUnit: DistanceUnit = DistanceUnit.KM,
    val showRirField: Boolean = false,
    val keepScreenOn: Boolean = true,
    val elapsedSeconds: Long = 0L,
    val summary: SessionSummary? = null,
    val keypad: KeypadUiState? = null,
    val summaryVisible: Boolean = false,
    val rating: Int? = null,
    val note: String = "",
    val restTimer: RestTimerUiModel? = null,
    val restPickerVisible: Boolean = false,
    val restPresets: List<Int> = listOf(30, 90, 120, 150),
    val defaultRestSeconds: Int = 120,
    val exactAlarmPrompted: Boolean = false,
)

sealed interface SessionEvent {
    data class NeedsInput(val setId: String) : SessionEvent
    data class InvalidInput(val field: SetField) : SessionEvent
    data object ConfirmDiscard : SessionEvent
    data class Finished(val sessionId: String) : SessionEvent
    data class Discarded(val sessionId: String) : SessionEvent
    data class RestFinished(val sessionId: String) : SessionEvent
    data class NewPersonalRecord(val setId: String) : SessionEvent
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val settingsRepository: SettingsRepository,
    private val restTimer: RestTimer,
    private val clock: Clock,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle.get<String>(SESSION_ID_KEY)) {
        "sessionId missing from SavedStateHandle"
    }

    /** Prefill values that are shown in placeholder style until the user adopts them. */
    private val placeholders = mutableMapOf<String, PrefillSet>()
    private var muscleRegionById: Map<String, Int> = emptyMap()
    private var unitOverridden = false

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val _events = Channel<SessionEvent>(Channel.BUFFERED)
    val events: Flow<SessionEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch { loadSession() }
        viewModelScope.launch { restTimer.restore() }
        viewModelScope.launch {
            restTimer.uiModel.collect { model ->
                _uiState.update { it.copy(restTimer = model) }
            }
        }
        viewModelScope.launch {
            restTimer.events.collect { event ->
                when (event) {
                    is com.ironlog.app.domain.timer.TimerEvent.Finished ->
                        _events.send(SessionEvent.RestFinished(event.sessionId))
                }
            }
        }
        viewModelScope.launch {
            exerciseRepository.observeMuscleGroups().collect { groups ->
                muscleRegionById = groups.associate { it.id to it.bodyRegion }
                renderEntries()
            }
        }
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        displayUnit = if (unitOverridden) it.displayUnit else settings.weightUnit,
                        displayDistanceUnit = settings.distanceUnit,
                        showRirField = settings.showRirField,
                        keepScreenOn = settings.keepScreenOnDuringWorkout,
                        restPresets = settings.restPresetsSeconds
                            .split(',')
                            .mapNotNull { value -> value.trim().toIntOrNull() },
                        defaultRestSeconds = settings.defaultRestSeconds,
                        exactAlarmPrompted = settings.exactAlarmPrompted,
                    )
                }
                renderEntries()
            }
        }
    }

    /** Called once per second by the UI so elapsed time tracks [Clock]. */
    fun refreshElapsed() {
        val session = _uiState.value.session ?: return
        _uiState.update { it.copy(elapsedSeconds = elapsedSeconds(session)) }
    }

    fun onExerciseSelected(exerciseId: String) {
        viewModelScope.launch {
            val entryCount = workoutRepository.getEntriesFor(sessionId).size
            val entryId = workoutRepository.addEntry(sessionId, exerciseId, orderIndex = entryCount)
            val exercise = exerciseRepository.getById(exerciseId)
            val kind = exercise?.kind ?: ExerciseKind.STRENGTH
            val plan = PrefillPlanner.plan(workoutRepository.getLastSetsForExercise(exerciseId), kind)
            plan.forEachIndexed { index, prefill ->
                val setId = workoutRepository.addSet(
                    entryId = entryId,
                    orderIndex = index,
                    setType = prefill.setType,
                    inputUnit = _uiState.value.displayUnit,
                )
                if (prefill.hasAnyValue()) placeholders[setId] = prefill
            }
            loadSession()
        }
    }

    fun onAddSet(entryId: String) {
        viewModelScope.launch {
            val sets = workoutRepository.getSetsFor(entryId)
            val previous = sets.lastOrNull()
            val index = sets.size
            val setId = workoutRepository.addSet(
                entryId = entryId,
                orderIndex = index,
                setType = previous?.setType ?: SetType.WORK,
                inputUnit = _uiState.value.displayUnit,
            )
            if (previous != null) {
                val resolved = resolve(previous, placeholders[previous.id])
                if (resolved.hasAnyValue()) placeholders[setId] = resolved.toPrefill()
            }
            loadSession()
        }
    }

    fun onDeleteSet(setId: String) {
        viewModelScope.launch {
            workoutRepository.deleteSet(setId)
            placeholders.remove(setId)
            loadSession()
        }
    }

    fun onDeleteEntry(entryId: String) {
        viewModelScope.launch {
            workoutRepository.deleteEntry(entryId)
            loadSession()
        }
    }

    fun onMoveEntry(entryId: String, delta: Int) {
        viewModelScope.launch {
            val entries = workoutRepository.getEntriesFor(sessionId).sortedBy { it.orderIndex }
            val index = entries.indexOfFirst { it.id == entryId }
            val target = index + delta
            if (index < 0 || target !in entries.indices) return@launch
            val reordered = entries.toMutableList().apply { add(target, removeAt(index)) }
            reordered.forEachIndexed { orderIndex, entry ->
                workoutRepository.updateEntry(entry.copy(orderIndex = orderIndex))
            }
            loadSession()
        }
    }

    fun onSetTypeChanged(setId: String, setType: SetType) {
        viewModelScope.launch {
            val set = findRow(setId)?.row?.set ?: return@launch
            workoutRepository.updateSet(set.copy(setType = setType))
            loadSession()
        }
    }

    fun onSetChecked(setId: String) {
        viewModelScope.launch {
            val entryRow = findRow(setId) ?: return@launch
            val set = entryRow.row.set
            if (set.isCompleted) {
                workoutRepository.updateSet(set.copy(isCompleted = false, completedAt = null))
                loadSession()
                return@launch
            }
            val resolved = resolve(set, entryRow.row.placeholder)
            if (needsMoreInput(resolved, entryRow.entry.exercise?.kind)) {
                _events.send(SessionEvent.NeedsInput(setId))
                return@launch
            }
            // Adopted placeholder values are recorded in the unit the user is looking at.
            val adoptedWeight = set.weightGrams == null && resolved.weightGrams != null
            val completed = resolved.copy(
                isCompleted = true,
                completedAt = clock.nowMillis(),
                inputUnit = if (adoptedWeight) _uiState.value.displayUnit else resolved.inputUnit,
            )
            workoutRepository.updateSet(completed)
            onSetCompleted(setId)
            loadSession()
            if (findRow(setId)?.row?.isPr == true) {
                _events.send(SessionEvent.NewPersonalRecord(setId))
            }
        }
    }

    /** M3: the rest picker is the single hook after a completed set. */
    private fun onSetCompleted(setId: String) {
        _uiState.update { it.copy(restPickerVisible = true) }
    }

    fun onRestPresetSelected(seconds: Int) {
        _uiState.update { it.copy(restPickerVisible = false) }
        viewModelScope.launch { restTimer.start(sessionId, seconds) }
    }

    fun onRestSkipped() {
        _uiState.update { it.copy(restPickerVisible = false) }
    }

    fun onRestAdjust(deltaSeconds: Int) {
        viewModelScope.launch { restTimer.adjust(deltaSeconds) }
    }

    fun onRestSkip() {
        viewModelScope.launch { restTimer.skip() }
    }

    fun refreshRestTimer() {
        restTimer.refresh()
    }

    fun onExactAlarmPromptedShown() {
        viewModelScope.launch { settingsRepository.setExactAlarmPrompted(true) }
    }

    fun onFieldClicked(setId: String, field: SetField) {
        val entryRow = findRow(setId) ?: return
        val row = entryRow.row
        val kind = entryRow.entry.exercise?.kind
        val currentText = when (field) {
            SetField.WEIGHT -> row.weightText
            SetField.REPS -> row.repsText
            SetField.RIR -> row.set.rir?.toString().orEmpty()
            SetField.DURATION -> durationInputText(row, kind)
            SetField.LEVEL -> row.levelText
            SetField.INCLINE -> row.inclineText
            SetField.SPEED -> row.speedText
            SetField.DISTANCE -> row.distanceText
        }
        val preset = when (field) {
            SetField.WEIGHT -> KeypadPresets.WEIGHT
            SetField.DISTANCE -> KeypadPresets.DISTANCE
            SetField.RIR -> KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 1)
            SetField.DURATION -> if (kind == ExerciseKind.CARDIO) {
                KeypadPresets.MINUTES
            } else {
                KeypadPresets.SECONDS
            }

            else -> KeypadPresets.REPS
        }
        _uiState.update {
            it.copy(keypad = KeypadUiState(setId = setId, field = field, keypadState = preset.copy(text = currentText)))
        }
    }

    /** D4: cardio edits whole minutes, timed exercises edit seconds. */
    private fun durationInputText(row: SetRowUiState, kind: ExerciseKind?): String {
        val durationS = row.set.durationS ?: row.placeholder?.durationS ?: return ""
        return if (kind == ExerciseKind.CARDIO) (durationS / 60).toString() else durationS.toString()
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
        val row = findRow(keypad.setId) ?: return
        val updated = applyFieldValue(
            set = row.row.set,
            field = keypad.field,
            text = keypad.keypadState.text,
            kind = row.entry.exercise?.kind,
        )
        if (updated == null) {
            viewModelScope.launch { _events.send(SessionEvent.InvalidInput(keypad.field)) }
            return
        }
        viewModelScope.launch {
            workoutRepository.updateSet(updated)
            _uiState.update { it.copy(keypad = null) }
            loadSession()
        }
    }

    fun onWeightStep(direction: Int) {
        _uiState.update { state ->
            val keypad = state.keypad ?: return@update state
            if (keypad.field != SetField.WEIGHT) return@update state
            val current = UnitConverter.parseToGrams(keypad.keypadState.text, state.displayUnit) ?: 0
            val next = WeightStepper.stepWeight(
                grams = current,
                unit = state.displayUnit,
                direction = direction,
                stepText = WeightStepper.defaultStepText(state.displayUnit),
            )
            state.copy(
                keypad = keypad.copy(
                    keypadState = keypad.keypadState.copy(
                        text = UnitConverter.formatGrams(next, state.displayUnit),
                    ),
                ),
            )
        }
    }

    fun onUnitToggle() {
        unitOverridden = true
        _uiState.update {
            it.copy(displayUnit = if (it.displayUnit == WeightUnit.KG) WeightUnit.LB else WeightUnit.KG)
        }
        viewModelScope.launch { loadSession() }
    }

    fun onFinishClicked() {
        val hasCompleted = _uiState.value.entries.any { entry -> entry.sets.any { it.set.isCompleted } }
        if (hasCompleted) {
            _uiState.update { it.copy(summaryVisible = true) }
        } else {
            viewModelScope.launch { _events.send(SessionEvent.ConfirmDiscard) }
        }
    }

    fun onRatingSelected(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onKeepTraining() {
        _uiState.update { it.copy(summaryVisible = false) }
    }

    fun onConfirmFinish() {
        viewModelScope.launch {
            restTimer.finish()
            val state = _uiState.value
            workoutRepository.finishSession(
                sessionId = sessionId,
                endedAtMillis = clock.nowMillis(),
                rating = state.rating,
                note = state.note.ifBlank { null },
            )
            _uiState.update { it.copy(summaryVisible = false) }
            _events.send(SessionEvent.Finished(sessionId))
        }
    }

    fun onConfirmDiscard() {
        viewModelScope.launch {
            restTimer.finish()
            workoutRepository.deleteSession(sessionId)
            _events.send(SessionEvent.Discarded(sessionId))
        }
    }

    private suspend fun loadSession() {
        val session = workoutRepository.getSession(sessionId)
        if (session == null) {
            _uiState.update { it.copy(loading = false, session = null, entries = emptyList()) }
            return
        }
        val entries = workoutRepository.getEntriesFor(sessionId).sortedBy { it.orderIndex }
        val exerciseCache = mutableMapOf<String, Exercise?>()
        val withSets = entries.map { entry ->
            SessionExerciseWithSets(entry = entry, sets = workoutRepository.getSetsFor(entry.id))
        }
        val meta = entries.associate { entry ->
            val exercise = exerciseCache.getOrPut(entry.exerciseId) { exerciseRepository.getById(entry.exerciseId) }
            entry.exerciseId to ExerciseMeta(
                primaryBodyRegions = listOfNotNull(exercise?.primaryMuscleId?.let { muscleRegionById[it] }),
            )
        }
        val summary = summarize(session, withSets, meta, clock.nowMillis())
        var workNumber = 0
        val uiEntries = entries.map { entry ->
            val exercise = exerciseCache.getOrPut(entry.exerciseId) { exerciseRepository.getById(entry.exerciseId) }
            val sets = withSets.first { it.entry.id == entry.id }.sets
            val plan = PrefillPlanner.plan(
                lastSets = workoutRepository.getLastSetsForExercise(entry.exerciseId),
                kind = exercise?.kind ?: ExerciseKind.STRENGTH,
            )
            var runningBest = historyMaxE1rm(entry.exerciseId)
            val rows = sets.mapIndexed { index, set ->
                val placeholder = placeholders[set.id] ?: plan.getOrNull(index)?.takeIf { isUntouched(set) }
                if (placeholder != null) placeholders[set.id] = placeholder
                val label = when (set.setType) {
                    SetType.WARMUP -> "W"
                    SetType.DROP -> "D"
                    SetType.FAILURE -> "F"
                    SetType.WORK -> {
                        workNumber += 1
                        workNumber.toString()
                    }
                }
                val pr = isPrSet(set, runningBest)
                if (pr) runningBest = e1rmOf(set) ?: runningBest
                buildRow(set, placeholder, label, isPr = pr)
            }
            EntryUiState(
                entry = entry,
                exercise = exercise,
                primaryBodyRegion = exercise?.primaryMuscleId?.let { muscleRegionById[it] },
                lastSets = workoutRepository.getLastSetsForExercise(entry.exerciseId),
                sets = rows,
            )
        }
        _uiState.update {
            it.copy(
                loading = false,
                session = session,
                entries = uiEntries,
                summary = summary,
                elapsedSeconds = elapsedSeconds(session),
            )
        }
    }

    /** Re-renders placeholder texts (used when the display unit changes). */
    private suspend fun renderEntries() {
        val state = _uiState.value
        val session = state.session ?: return
        val summary = state.summary
        val uiEntries = state.entries.map { entry ->
            var workNumber = 0
            val rows = entry.sets.map { row ->
                val label = when (row.set.setType) {
                    SetType.WARMUP -> "W"
                    SetType.DROP -> "D"
                    SetType.FAILURE -> "F"
                    SetType.WORK -> {
                        workNumber += 1
                        workNumber.toString()
                    }
                }
                buildRow(row.set, row.placeholder, label, row.isPr)
            }
            entry.copy(sets = rows)
        }
        if (summary == null) return
        _uiState.update { it.copy(entries = uiEntries, elapsedSeconds = elapsedSeconds(session)) }
    }

    private fun buildRow(
        set: WorkoutSet,
        placeholder: PrefillSet?,
        numberLabel: String,
        isPr: Boolean = false,
    ): SetRowUiState {
        val weightGrams = set.weightGrams ?: placeholder?.weightGrams
        val durationS = set.durationS ?: placeholder?.durationS
        return SetRowUiState(
            set = set,
            placeholder = placeholder,
            numberLabel = numberLabel,
            isPlaceholder = isUntouched(set) && placeholder?.hasAnyValue() == true,
            isPr = isPr,
            weightText = weightGrams?.let { UnitConverter.formatGrams(it, _uiState.value.displayUnit) }.orEmpty(),
            repsText = (set.reps ?: placeholder?.reps)?.toString().orEmpty(),
            durationText = durationS?.let { UnitConverter.formatDuration(it) }.orEmpty(),
            levelText = (set.level ?: placeholder?.level)?.toString().orEmpty(),
            speedText = (set.speedX10 ?: placeholder?.speedX10)?.let(UnitConverter::formatTenths).orEmpty(),
            inclineText = (set.inclineX10 ?: placeholder?.inclineX10)?.let(UnitConverter::formatTenths).orEmpty(),
            distanceText = (set.distanceM ?: placeholder?.distanceM)
                ?.let { UnitConverter.formatMeters(it, _uiState.value.displayDistanceUnit) }.orEmpty(),
        )
    }

    private fun findRow(setId: String): EntryRow? =
        _uiState.value.entries.firstNotNullOfOrNull { entry ->
            entry.sets.firstOrNull { it.set.id == setId }?.let { EntryRow(entry, it) }
        }

    private fun resolve(set: WorkoutSet, placeholder: PrefillSet?): WorkoutSet = set.copy(
        weightGrams = set.weightGrams ?: placeholder?.weightGrams,
        reps = set.reps ?: placeholder?.reps,
        durationS = set.durationS ?: placeholder?.durationS,
        level = set.level ?: placeholder?.level,
        inclineX10 = set.inclineX10 ?: placeholder?.inclineX10,
        speedX10 = set.speedX10 ?: placeholder?.speedX10,
        distanceM = set.distanceM ?: placeholder?.distanceM,
    )

    private fun needsMoreInput(set: WorkoutSet, kind: ExerciseKind?): Boolean = when (kind) {
        ExerciseKind.CARDIO, ExerciseKind.TIMED -> set.durationS == null
        else -> set.reps == null
    }

    private fun applyFieldValue(set: WorkoutSet, field: SetField, text: String, kind: ExerciseKind?): WorkoutSet? =
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

    /** DOMAIN_RULES §6: baseline = best earlier completed non-warmup set (history only; today handled by the row scan). */
    private suspend fun historyMaxE1rm(exerciseId: String): Int? =
        workoutRepository.getExerciseTrendSets(exerciseId)
            .filter { it.isCompleted && it.setType != SetType.WARMUP }
            .mapNotNull { set ->
                val weight = set.weightGrams ?: return@mapNotNull null
                val reps = set.reps ?: return@mapNotNull null
                OneRepMax.estimate(weight, reps)
            }
            .maxOrNull()

    private fun e1rmOf(set: WorkoutSet): Int? {
        val weight = set.weightGrams ?: return null
        val reps = set.reps ?: return null
        return OneRepMax.estimate(weight, reps)
    }

    private fun isPrSet(set: WorkoutSet, runningBest: Int?): Boolean {
        if (!set.isCompleted || set.setType == SetType.WARMUP) return false
        return PersonalRecord.isPersonalRecord(runningBest, e1rmOf(set))
    }

    private fun elapsedSeconds(session: WorkoutSession): Long =
        ((clock.nowMillis() - session.startedAt) / 1000).coerceAtLeast(0L)

    private fun isUntouched(set: WorkoutSet): Boolean =
        !set.isCompleted &&
            set.weightGrams == null &&
            set.reps == null &&
            set.rir == null &&
            set.durationS == null &&
            set.distanceM == null &&
            set.level == null &&
            set.inclineX10 == null &&
            set.speedX10 == null

    private fun PrefillSet.hasAnyValue(): Boolean =
        weightGrams != null || reps != null || durationS != null || level != null ||
            inclineX10 != null || speedX10 != null || distanceM != null

    private fun WorkoutSet.hasAnyValue(): Boolean =
        weightGrams != null || reps != null || durationS != null || level != null ||
            inclineX10 != null || speedX10 != null || distanceM != null

    private fun WorkoutSet.toPrefill(): PrefillSet = PrefillSet(
        setType = setType,
        weightGrams = weightGrams,
        reps = reps,
        durationS = durationS,
        level = level,
        inclineX10 = inclineX10,
        speedX10 = speedX10,
        distanceM = distanceM,
    )

    private data class EntryRow(val entry: EntryUiState, val row: SetRowUiState)

    companion object {
        const val SESSION_ID_KEY = "sessionId"
    }
}
