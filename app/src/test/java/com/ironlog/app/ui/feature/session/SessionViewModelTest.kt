package com.ironlog.app.ui.feature.session

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.keypad.KeypadKey
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.testutil.FakeExerciseRepository
import com.ironlog.app.testutil.FakeRestTimer
import com.ironlog.app.testutil.FakeSettingsRepository
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
import com.ironlog.app.testutil.testSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val clock = FixedClock(100_000L)

    private lateinit var workoutRepository: FakeWorkoutRepository
    private lateinit var exerciseRepository: FakeExerciseRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var restTimer: FakeRestTimer

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        workoutRepository = FakeWorkoutRepository()
        workoutRepository.sessions.value = listOf(
            testSession(
                id = "session-1",
                status = SessionStatus.IN_PROGRESS,
                startedAt = 90_000L,
                endedAt = null,
            ),
        )
        exerciseRepository = FakeExerciseRepository()
        exerciseRepository.exercises.value = listOf(
            exercise("bench", ExerciseKind.STRENGTH, "barbell"),
            exercise("treadmill", ExerciseKind.CARDIO, "treadmill"),
        )
        settingsRepository = FakeSettingsRepository()
        restTimer = FakeRestTimer()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun exercise(id: String, kind: ExerciseKind, equipment: String) = Exercise(
        id = id,
        nameZh = id,
        nameEn = id,
        kind = kind,
        equipment = equipment,
        primaryMuscleId = null,
        isCustom = false,
        isArchived = false,
        createdAt = 0,
        updatedAt = 0,
    )

    private fun createViewModel(): SessionViewModel = SessionViewModel(
        workoutRepository = workoutRepository,
        exerciseRepository = exerciseRepository,
        settingsRepository = settingsRepository,
        restTimer = restTimer,
        clock = clock,
        savedStateHandle = SavedStateHandle(mapOf(SessionViewModel.SESSION_ID_KEY to "session-1")),
    )

    @Test
    fun addingExerciseCreatesPrefillSetsFromLastTime() = runTest(dispatcher) {
        workoutRepository.lastSetsByExercise["bench"] = listOf(
            testSet(id = "last-1", type = SetType.WARMUP, weightGrams = 20_000, reps = 10),
            testSet(id = "last-2", type = SetType.WORK, weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onExerciseSelected("bench")

        val state = viewModel.uiState.first { it.entries.isNotEmpty() && it.entries[0].sets.size == 2 }
        val rows = state.entries[0].sets
        assertThat(rows.map { it.set.setType }).containsExactly(SetType.WARMUP, SetType.WORK).inOrder()
        assertThat(rows.map { it.numberLabel }).containsExactly("W", "1").inOrder()
        assertThat(rows[1].isPlaceholder).isTrue()
        assertThat(rows[1].weightText).isEqualTo("60")
        assertThat(rows[1].repsText).isEqualTo("8")
        // Placeholder values are not written to the database before completion.
        assertThat(workoutRepository.sets.value[1].weightGrams).isNull()
    }

    @Test
    fun checkingUntouchedPrefillAdoptsPlaceholderValues() = runTest(dispatcher) {
        workoutRepository.lastSetsByExercise["bench"] = listOf(
            testSet(id = "last-1", type = SetType.WORK, weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onExerciseSelected("bench")
        val row = viewModel.uiState.first { it.entries.isNotEmpty() }.entries[0].sets.single()

        viewModel.onSetChecked(row.set.id)

        val stored = workoutRepository.sets.value.single()
        assertThat(stored.isCompleted).isTrue()
        assertThat(stored.weightGrams).isEqualTo(60_000)
        assertThat(stored.reps).isEqualTo(8)
        assertThat(stored.completedAt).isEqualTo(clock.nowMillis())
    }

    @Test
    fun checkingEmptySetEmitsNeedsInputAndDoesNotWrite() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onExerciseSelected("bench")
        val row = viewModel.uiState.first { it.entries.isNotEmpty() }.entries[0].sets.single()
        assertThat(row.isPlaceholder).isFalse()

        viewModel.onSetChecked(row.set.id)

        val event = viewModel.events.first()
        assertThat(event).isEqualTo(SessionEvent.NeedsInput(row.set.id))
        assertThat(workoutRepository.sets.value.single().isCompleted).isFalse()
    }

    @Test
    fun unitToggleChangesTextOnly() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        val initialState = viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }
        assertThat(initialState.displayUnit).isEqualTo(WeightUnit.KG)
        assertThat(initialState.entries[0].sets[0].weightText).isEqualTo("60")

        viewModel.onUnitToggle()

        val lbState = viewModel.uiState.first { it.displayUnit == WeightUnit.LB }
        assertThat(lbState.entries[0].sets[0].weightText).isEqualTo("132.28")
        assertThat(workoutRepository.sets.value.single().weightGrams).isEqualTo(60_000)
    }

    @Test
    fun finishingWritesRatingNoteAndEndedAt() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(
                id = "set-1",
                sessionExerciseId = "entry-1",
                weightGrams = 60_000,
                reps = 8,
                isCompleted = true,
                completedAt = 95_000L,
            ),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onFinishClicked()
        assertThat(viewModel.uiState.first { it.summaryVisible }.summaryVisible).isTrue()
        viewModel.onRatingSelected(8)
        viewModel.onNoteChanged("good session")
        viewModel.onConfirmFinish()

        val event = viewModel.events.first()
        assertThat(event).isEqualTo(SessionEvent.Finished("session-1"))
        val session = workoutRepository.sessions.value.single()
        assertThat(session.status).isEqualTo(SessionStatus.FINISHED)
        assertThat(session.rating).isEqualTo(8)
        assertThat(session.note).isEqualTo("good session")
        assertThat(session.endedAt).isEqualTo(clock.nowMillis())
    }

    @Test
    fun keepTrainingLeavesSessionInProgress() = runTest(dispatcher) {
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", weightGrams = 60_000, reps = 8, isCompleted = true),
        )
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onFinishClicked()
        viewModel.onKeepTraining()

        assertThat(viewModel.uiState.value.summaryVisible).isFalse()
        assertThat(workoutRepository.sessions.value.single().status).isEqualTo(SessionStatus.IN_PROGRESS)
    }

    @Test
    fun finishingWithoutCompletedSetsConfirmsDiscardThenDeletesSession() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", isCompleted = false),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onFinishClicked()
        assertThat(viewModel.events.first()).isEqualTo(SessionEvent.ConfirmDiscard)

        viewModel.onConfirmDiscard()

        assertThat(viewModel.events.first()).isEqualTo(SessionEvent.Discarded("session-1"))
        assertThat(workoutRepository.sessions.value).isEmpty()
        assertThat(workoutRepository.entries.value).isEmpty()
        assertThat(workoutRepository.sets.value).isEmpty()
    }

    @Test
    fun cardioDurationIsEnteredInMinutes() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onExerciseSelected("treadmill")
        val row = viewModel.uiState.first { it.entries.isNotEmpty() }.entries[0].sets.single()

        viewModel.onSetChecked(row.set.id)
        assertThat(viewModel.events.first()).isEqualTo(SessionEvent.NeedsInput(row.set.id))

        viewModel.onFieldClicked(row.set.id, SetField.DURATION)
        listOf(2, 0).forEach { digit -> viewModel.onKeypadKey(KeypadKey.Digit(digit)) }
        viewModel.onKeypadDone()

        // D4: cardio input is whole minutes: "20" -> 1200 s.
        assertThat(workoutRepository.sets.value.single().durationS).isEqualTo(1_200)

        viewModel.onSetChecked(row.set.id)
        assertThat(workoutRepository.sets.value.single().isCompleted).isTrue()
    }

    @Test
    fun cardioDistanceUsesTheConfiguredDistanceUnit() = runTest(dispatcher) {
        settingsRepository.settings.value = UserSettings(distanceUnit = DistanceUnit.KM)
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onExerciseSelected("treadmill")
        val row = viewModel.uiState.first { it.entries.isNotEmpty() }.entries[0].sets.single()

        viewModel.onFieldClicked(row.set.id, SetField.DISTANCE)
        viewModel.onKeypadKey(KeypadKey.Digit(5))
        viewModel.onKeypadKey(KeypadKey.Dot)
        viewModel.onKeypadKey(KeypadKey.Digit(2))
        viewModel.onKeypadKey(KeypadKey.Digit(5))
        viewModel.onKeypadDone()

        // D3: distance input is in the configured distance unit: 5.25 km -> 5250 m.
        assertThat(workoutRepository.sets.value.single().distanceM).isEqualTo(5_250)
        viewModel.onFieldClicked(row.set.id, SetField.DISTANCE)
        assertThat(viewModel.uiState.value.keypad?.keypadState?.text).isEqualTo("5.25")
    }

    @Test
    fun weightEditRecordsInputUnit() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", isCompleted = false),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onUnitToggle()
        viewModel.uiState.first { it.displayUnit == WeightUnit.LB }
        viewModel.onFieldClicked("set-1", SetField.WEIGHT)
        listOf(4, 5).forEach { digit -> viewModel.onKeypadKey(KeypadKey.Digit(digit)) }
        viewModel.onKeypadDone()

        val stored = workoutRepository.sets.value.single()
        assertThat(stored.weightGrams).isEqualTo(20_412)
        assertThat(stored.inputUnit).isEqualTo(WeightUnit.LB)
    }



    @Test
    fun completingASetShowsTheRestPicker() = runTest(dispatcher) {
        workoutRepository.lastSetsByExercise["bench"] = listOf(
            testSet(id = "last-1", type = SetType.WORK, weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onExerciseSelected("bench")
        val row = viewModel.uiState.first { it.entries.isNotEmpty() }.entries[0].sets.single()

        viewModel.onSetChecked(row.set.id)

        assertThat(viewModel.uiState.value.restPickerVisible).isTrue()
    }

    @Test
    fun selectingARestPresetStartsTheTimerAndClosesThePicker() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onRestPresetSelected(90)

        assertThat(restTimer.started).containsExactly("session-1" to 90)
        assertThat(viewModel.uiState.value.restPickerVisible).isFalse()
        assertThat(viewModel.uiState.value.restTimer?.remainingMillis).isEqualTo(90_000L)
    }

    @Test
    fun skippingTheRestPickerDoesNotStartTheTimer() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onRestSkipped()

        assertThat(restTimer.started).isEmpty()
        assertThat(viewModel.uiState.value.restPickerVisible).isFalse()
    }

    @Test
    fun restFinishedEventIsForwardedToTheUi() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        restTimer.emitFinished("session-1")

        assertThat(viewModel.events.first()).isEqualTo(SessionEvent.RestFinished("session-1"))
    }

    @Test
    fun deletingTheRestingSetDoesNotCancelTheTimer() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }
        viewModel.onRestPresetSelected(90)

        viewModel.onDeleteSet("set-1")

        assertThat(restTimer.skipCount).isEqualTo(0)
        assertThat(restTimer.finishCount).isEqualTo(0)
    }

    @Test
    fun finishingTheSessionStopsTheRestTimer() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8, isCompleted = true),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }
        viewModel.onRestPresetSelected(90)

        viewModel.onFinishClicked()
        viewModel.onConfirmFinish()

        assertThat(restTimer.finishCount).isEqualTo(1)
    }

    @Test
    fun discardingTheSessionStopsTheRestTimer() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }
        viewModel.onRestPresetSelected(90)

        viewModel.onConfirmDiscard()

        assertThat(restTimer.finishCount).isEqualTo(1)
    }

    @Test
    fun restTimerIsRestoredWhenTheViewModelIsCreated() = runTest(dispatcher) {
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        assertThat(restTimer.restoreCount).isAtLeast(1)
    }

    @Test
    fun completingASetAboveHistoryEmitsPersonalRecord() = runTest(dispatcher) {
        seedBenchHistory(weightGrams = 60_000, reps = 10) // e1rm 80000
        workoutRepository.sessions.value = workoutRepository.sessions.value + testSession(
            id = "session-1",
            status = SessionStatus.IN_PROGRESS,
            startedAt = 90_000L,
            endedAt = null,
        )
        workoutRepository.entries.value = workoutRepository.entries.value + SessionExercise(
            "entry-1", "session-1", "bench", 0, null, 0, 0,
        )
        workoutRepository.sets.value = workoutRepository.sets.value + testSet(
            id = "set-1",
            sessionExerciseId = "entry-1",
            weightGrams = 100_000,
            reps = 5,
            isCompleted = false,
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onSetChecked("set-1")

        assertThat(viewModel.events.first()).isEqualTo(SessionEvent.NewPersonalRecord("set-1"))
        assertThat(viewModel.uiState.value.entries.first().sets.single().isPr).isTrue()
    }

    @Test
    fun firstEverValidSetIsOnlyABaseline() = runTest(dispatcher) {
        // No history at all: D9 says the first valid set is a baseline, not a PR.
        workoutRepository.sessions.value = listOf(
            testSession(id = "session-1", status = SessionStatus.IN_PROGRESS, startedAt = 90_000L, endedAt = null),
        )
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(
                id = "set-1",
                sessionExerciseId = "entry-1",
                weightGrams = 100_000,
                reps = 5,
                isCompleted = false,
            ),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onSetChecked("set-1")

        assertThat(workoutRepository.sets.value.single().isCompleted).isTrue()
        assertThat(viewModel.uiState.value.entries.first().sets.single().isPr).isFalse()
        assertThat(withTimeoutOrNull(200) { viewModel.events.first() }).isNull()
    }

    @Test
    fun completingASetEqualToOneFromHistoryDoesNotEmitPersonalRecord() = runTest(dispatcher) {
        seedBenchHistory(weightGrams = 60_000, reps = 10) // e1rm 80000
        workoutRepository.sessions.value = workoutRepository.sessions.value + testSession(
            id = "session-1",
            status = SessionStatus.IN_PROGRESS,
            startedAt = 90_000L,
            endedAt = null,
        )
        workoutRepository.entries.value = workoutRepository.entries.value + SessionExercise(
            "entry-1", "session-1", "bench", 0, null, 0, 0,
        )
        workoutRepository.sets.value = workoutRepository.sets.value + testSet(
            id = "set-1",
            sessionExerciseId = "entry-1",
            weightGrams = 60_000,
            reps = 10,
            isCompleted = false,
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onSetChecked("set-1")

        assertThat(workoutRepository.sets.value.first { it.id == "set-1" }.isCompleted).isTrue()
        assertThat(viewModel.uiState.value.entries.first().sets.single().isPr).isFalse()
        assertThat(withTimeoutOrNull(200) { viewModel.events.first() }).isNull()
    }

    private fun seedBenchHistory(weightGrams: Int, reps: Int) {
        workoutRepository.sessions.value = listOf(
            testSession(id = "history", status = SessionStatus.FINISHED, startedAt = 0L, endedAt = 3_600_000L),
        )
        workoutRepository.entries.value = listOf(
            SessionExercise("history-entry", "history", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(
                id = "history-set",
                sessionExerciseId = "history-entry",
                weightGrams = weightGrams,
                reps = reps,
                isCompleted = true,
            ),
        )
    }

    @Test
    fun changingSetTypeAndDeletingSetArePersisted() = runTest(dispatcher) {
        workoutRepository.entries.value = listOf(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading && it.entries.isNotEmpty() }

        viewModel.onSetTypeChanged("set-1", SetType.WARMUP)
        assertThat(workoutRepository.sets.value.single().setType).isEqualTo(SetType.WARMUP)

        viewModel.onDeleteSet("set-1")
        assertThat(workoutRepository.sets.value).isEmpty()
    }
}
