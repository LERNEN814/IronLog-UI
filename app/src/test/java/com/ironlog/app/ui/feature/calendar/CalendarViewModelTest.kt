package com.ironlog.app.ui.feature.calendar

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.testutil.FakeWorkoutRepository
import com.ironlog.app.testutil.testSession
import java.time.LocalDate
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
class CalendarViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun header(id: String, localDate: String, regions: Set<Int>): SessionHeader {
        val session = testSession(
            id = id,
            status = SessionStatus.FINISHED,
            startedAt = localDate.hashCode().toLong(),
            endedAt = localDate.hashCode().toLong() + 3_600_000L,
        ).copy(localDate = localDate)
        return SessionHeader(
            session = session,
            exerciseNames = listOf("Bench"),
            bodyRegions = regions,
            summary = SessionSummary(3_600, 1, 3, 1_000_000, regions),
        )
    }

    @Test
    fun groupsSessionsByDateAndBuildsRegionDots() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        repository.sessionHeadersFlow.value = listOf(
            header("s1", "2026-01-05", setOf(0)),
            header("s2", "2026-01-06", setOf(4)),
            header("s3", "2026-01-06", setOf(6)),
        )
        val viewModel = CalendarViewModel(repository, FixedClock(1_767_268_800_000L))
        val state = viewModel.uiState.first { it.days.isNotEmpty() }
        // The clock is 2026-01-01 in Shanghai, so the January 2026 grid is shown.
        assertThat(state.year).isEqualTo(2026)
        assertThat(state.month).isEqualTo(1)

        val day5 = state.days.filterNotNull().first { it.date == LocalDate.of(2026, 1, 5) }
        assertThat(day5.dots).containsExactly(0)
        val day6 = state.days.filterNotNull().first { it.date == LocalDate.of(2026, 1, 6) }
        assertThat(day6.dots).containsExactly(4, 6).inOrder()

        viewModel.onDateSelected(LocalDate.of(2026, 1, 6))

        val selected = viewModel.uiState.value.selectedSessions
        assertThat(selected.map { it.session.id }).containsExactly("s2", "s3").inOrder()
        assertThat(selected.flatMap { it.bodyRegions }.toSet()).containsExactly(4, 6)
    }

    @Test
    fun monthNavigationMovesToThePreviousMonth() = runTest(dispatcher) {
        val repository = FakeWorkoutRepository()
        val viewModel = CalendarViewModel(repository, FixedClock(1_767_268_800_000L))
        viewModel.uiState.first { it.days.isNotEmpty() }

        viewModel.onPreviousMonth()

        assertThat(viewModel.uiState.value.year).isEqualTo(2025)
        assertThat(viewModel.uiState.value.month).isEqualTo(12)
    }
}
