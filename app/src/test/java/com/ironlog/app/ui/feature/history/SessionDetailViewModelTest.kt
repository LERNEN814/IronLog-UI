package com.ironlog.app.ui.feature.history

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.keypad.KeypadKey
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.testutil.FakeExerciseRepository
import com.ironlog.app.testutil.FakeSettingsRepository
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
import com.ironlog.app.testutil.testSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionDetailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var workoutRepository: FakeWorkoutRepository
    private lateinit var exerciseRepository: FakeExerciseRepository
    private lateinit var settingsRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        workoutRepository = FakeWorkoutRepository()
        exerciseRepository = FakeExerciseRepository()
        exerciseRepository.exercises.value = listOf(
            exercise("bench", ExerciseKind.STRENGTH, "barbell"),
            exercise("treadmill", ExerciseKind.CARDIO, "treadmill"),
        )
        settingsRepository = FakeSettingsRepository()
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

    private fun seedFinishedSession(vararg entries: SessionExercise, sessionId: String = "session-1") {
        workoutRepository.sessions.value = listOf(
            testSession(id = sessionId, status = SessionStatus.FINISHED, startedAt = 0L, endedAt = 3_600_000L),
        )
        workoutRepository.entries.value = entries.toList()
    }

    private fun createViewModel(sessionId: String = "session-1"): SessionDetailViewModel = SessionDetailViewModel(
        workoutRepository = workoutRepository,
        exerciseRepository = exerciseRepository,
        settingsRepository = settingsRepository,
        clock = FixedClock(100_000L),
        savedStateHandle = SavedStateHandle(mapOf("sessionId" to sessionId)),
    )

    @Test
    fun editingWeightPersists() = runTest(dispatcher) {
        seedFinishedSession(SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0))
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 50_000, reps = 5, isCompleted = true),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onFieldClicked("set-1", com.ironlog.app.ui.feature.session.SetField.WEIGHT)
        viewModel.onKeypadKey(KeypadKey.Clear)
        listOf(6, 0).forEach { digit -> viewModel.onKeypadKey(KeypadKey.Digit(digit)) }
        viewModel.onKeypadDone()

        assertThat(workoutRepository.sets.value.single().weightGrams).isEqualTo(60_000)
    }

    @Test
    fun editingRepsAndTypePersists() = runTest(dispatcher) {
        seedFinishedSession(SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0))
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 5, isCompleted = true),
        )
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onFieldClicked("set-1", com.ironlog.app.ui.feature.session.SetField.REPS)
        viewModel.onKeypadKey(KeypadKey.Clear)
        listOf(8).forEach { digit -> viewModel.onKeypadKey(KeypadKey.Digit(digit)) }
        viewModel.onKeypadDone()
        viewModel.onSetTypeChanged("set-1", SetType.WARMUP)

        val stored = workoutRepository.sets.value.single()
        assertThat(stored.reps).isEqualTo(8)
        assertThat(stored.setType).isEqualTo(SetType.WARMUP)
    }

    @Test
    fun ratingAndNoteArePersisted() = runTest(dispatcher) {
        seedFinishedSession(SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0))
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onRatingSelected(9)
        viewModel.onNoteChanged("good session")

        val session = workoutRepository.sessions.value.single()
        assertThat(session.rating).isEqualTo(9)
        assertThat(session.note).isEqualTo("good session")
    }

    @Test
    fun deletingTheSessionRemovesItAndEmitsAnEvent() = runTest(dispatcher) {
        seedFinishedSession(SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0))
        workoutRepository.sets.value = listOf(testSet(id = "set-1", sessionExerciseId = "entry-1"))
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onConfirmDelete()

        assertThat(workoutRepository.sessions.value).isEmpty()
        assertThat(workoutRepository.entries.value).isEmpty()
        assertThat(workoutRepository.sets.value).isEmpty()
        assertThat(viewModel.events.first()).isEqualTo(SessionDetailEvent.SessionDeleted)
    }

    @Test
    fun addAndDeleteSetArePersisted() = runTest(dispatcher) {
        seedFinishedSession(SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0))
        workoutRepository.sets.value = listOf(testSet(id = "set-1", sessionExerciseId = "entry-1"))
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onAddSet("entry-1")
        assertThat(workoutRepository.sets.value).hasSize(2)

        viewModel.onDeleteSet("set-1")
        assertThat(workoutRepository.sets.value.map { it.id }).doesNotContain("set-1")
    }

    @Test
    fun templateRefusesWhenASessionIsInProgress() = runTest(dispatcher) {
        seedFinishedSession(
            SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8, isCompleted = true),
        )
        workoutRepository.sessions.value = workoutRepository.sessions.value + testSession(
            id = "in-progress",
            status = SessionStatus.IN_PROGRESS,
            startedAt = 5_000L,
            endedAt = null,
        )
        val entriesBefore = workoutRepository.entries.value.size
        val setsBefore = workoutRepository.sets.value.size
        val viewModel = createViewModel()
        viewModel.uiState.first { !it.loading }

        viewModel.onStartTemplate()

        val event = viewModel.events.first()
        assertThat(event).isEqualTo(SessionDetailEvent.AlreadyInProgress("in-progress"))
        assertThat(workoutRepository.entries.value).hasSize(entriesBefore)
        assertThat(workoutRepository.sets.value).hasSize(setsBefore)
        assertThat(workoutRepository.getEntriesFor("in-progress")).isEmpty()
    }

    @Test
    fun templateCopiesEntriesAndUsesLastTimePrefill() = runTest(dispatcher) {
        seedFinishedSession(
            SessionExercise("entry-1", "session-old", "bench", 0, null, 0, 0),
            SessionExercise("entry-2", "session-old", "treadmill", 1, null, 0, 0),
            sessionId = "session-old",
        )
        workoutRepository.sets.value = listOf(
            testSet(id = "set-1", sessionExerciseId = "entry-1", weightGrams = 60_000, reps = 8, isCompleted = true),
        )
        workoutRepository.lastSetsByExercise["bench"] = listOf(
            testSet(id = "last-1", type = SetType.WARMUP, weightGrams = 20_000, reps = 10),
            testSet(id = "last-2", type = SetType.WORK, weightGrams = 60_000, reps = 8),
            testSet(id = "last-3", type = SetType.WORK, weightGrams = 60_000, reps = 7),
        )
        val viewModel = createViewModel(sessionId = "session-old")
        viewModel.uiState.first { !it.loading }

        viewModel.onStartTemplate()

        val event = viewModel.events.first()
        assertThat(event).isInstanceOf(SessionDetailEvent.TemplateStarted::class.java)
        val newSessionId = (event as SessionDetailEvent.TemplateStarted).sessionId
        val newEntries = workoutRepository.entries.value.filter { it.sessionId == newSessionId }
        assertThat(newEntries.map { it.exerciseId }).containsExactly("bench", "treadmill").inOrder()
        val benchEntryId = newEntries.first { it.exerciseId == "bench" }.id
        val benchSets = workoutRepository.sets.value.filter { it.sessionExerciseId == benchEntryId }
        assertThat(benchSets.map { it.setType }).containsExactly(SetType.WARMUP, SetType.WORK, SetType.WORK).inOrder()
        // Placeholder rows: values are filled by SessionViewModel from "last time", not copied here.
        assertThat(benchSets.all { it.weightGrams == null && it.reps == null }).isTrue()
        val treadmillEntryId = newEntries.first { it.exerciseId == "treadmill" }.id
        assertThat(workoutRepository.sets.value.count { it.sessionExerciseId == treadmillEntryId }).isEqualTo(1)
    }
}
