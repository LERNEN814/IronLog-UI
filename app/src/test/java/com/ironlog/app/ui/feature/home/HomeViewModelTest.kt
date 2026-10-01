package com.ironlog.app.ui.feature.home

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.testutil.FakeExerciseRepository
import com.ironlog.app.testutil.FakeFatigueRepository
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
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
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var fatigueRepository: FakeFatigueRepository
    private lateinit var exerciseRepository: FakeExerciseRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fatigueRepository = FakeFatigueRepository()
        exerciseRepository = FakeExerciseRepository()
    }

    private fun createViewModel(repository: FakeWorkoutRepository, clock: FixedClock): HomeViewModel =
        HomeViewModel(
            workoutRepository = repository,
            fatigueRepository = fatigueRepository,
            exerciseRepository = exerciseRepository,
            clock = clock,
        )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun showsStartActionWithoutInProgressSession() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()

        val viewModel = createViewModel(repository, FixedClock(100_000L))

        val state = viewModel.uiState.first { !it.loading }
        assertThat(state.primaryAction).isEqualTo(HomePrimaryAction.START)
        assertThat(state.inProgress).isNull()
        assertThat(state.elapsedSeconds).isEqualTo(0L)
    }

    @Test
    fun showsResumeActionAndElapsedTimeWithInProgressSession() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        repository.sessions.value = listOf(
            testSession(
                id = "session-1",
                status = SessionStatus.IN_PROGRESS,
                startedAt = 100_000L,
                endedAt = null,
            ),
        )

        val viewModel = createViewModel(repository, FixedClock(160_000L))

        val state = viewModel.uiState.first { it.primaryAction == HomePrimaryAction.RESUME }
        assertThat(state.inProgress?.id).isEqualTo("session-1")
        assertThat(state.elapsedSeconds).isEqualTo(60L)
    }

    @Test
    fun primaryActionOpensTheStartedSession() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        val viewModel = createViewModel(repository, FixedClock(0L))
        viewModel.uiState.first { !it.loading }

        viewModel.onPrimaryAction()

        val event = viewModel.events.first()
        assertThat(event).isInstanceOf(HomeEvent.OpenSession::class.java)
        assertThat((event as HomeEvent.OpenSession).sessionId).isEqualTo("session-1")
        assertThat(repository.sessions.value).hasSize(1)
    }

    @Test
    fun refreshElapsedRecomputesFromClock() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        repository.sessions.value = listOf(
            testSession(status = SessionStatus.IN_PROGRESS, startedAt = 0L, endedAt = null),
        )
        val clock = FixedClock(0L)
        val viewModel = createViewModel(repository, clock)
        viewModel.uiState.first { it.primaryAction == HomePrimaryAction.RESUME }

        clock.millis = 90_000L
        viewModel.refreshElapsed()

        assertThat(viewModel.uiState.value.elapsedSeconds).isEqualTo(90L)
    }

    @Test
    fun fatigueScoresComeFromTheRepository() = runTest(dispatcher) {
        fatigueRepository.result = mapOf("chest" to 50, "triceps" to 25)
        val repository = FakeWorkoutRepository()

        val viewModel = createViewModel(repository, FixedClock(123_456L))

        val state = viewModel.uiState.first { it.fatigueScores.isNotEmpty() }
        assertThat(state.fatigueScores["chest"]).isEqualTo(50)
        assertThat(fatigueRepository.requestedAt).containsExactly(123_456L)
    }

    @Test
    fun weekStatsAndRecentSessionsComeFromRepository() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        repository.weekStatsFlow.value = com.ironlog.app.domain.summary.WeekStats(4, 33)

        val viewModel = createViewModel(repository, FixedClock(0L))

        val state = viewModel.uiState.first { it.weekStats.sessionCount == 4 }
        assertThat(state.weekStats.workSetCount).isEqualTo(33)
    }

    @Test
    fun selectingAndChangingViewUpdatesOnlyReadyRenderModel() = runTest(dispatcher) {
        fatigueRepository.result = mapOf("chest" to 50, "upper_back" to 20)
        val viewModel = createViewModel(FakeWorkoutRepository(), FixedClock(0L))
        viewModel.uiState.first { it.heatmap is HeatmapState.Ready }

        viewModel.onMuscleSelected("chest")
        assertThat((viewModel.uiState.value.heatmap as HeatmapState.Ready).model.selectedMuscleId)
            .isEqualTo("chest")

        viewModel.onBodyViewSelected(BodyView.BACK)
        val model = (viewModel.uiState.value.heatmap as HeatmapState.Ready).model
        assertThat(model.view).isEqualTo(BodyView.BACK)
        assertThat(model.selectedMuscleId).isNull()
    }

    @Test
    fun invalidSelectionDoesNotEnterRenderModel() = runTest(dispatcher) {
        fatigueRepository.result = mapOf("chest" to 50)
        val viewModel = createViewModel(FakeWorkoutRepository(), FixedClock(0L))
        viewModel.uiState.first { it.heatmap is HeatmapState.Ready }

        viewModel.onMuscleSelected("lats")

        assertThat((viewModel.uiState.value.heatmap as HeatmapState.Ready).model.selectedMuscleId).isNull()
    }
}
