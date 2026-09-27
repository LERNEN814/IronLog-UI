package com.ironlog.app.domain.calendar

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.WorkoutSession
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Test

/** M4-T4.1: month grid boundaries and body-region dots (PRD R-4.2). */
class CalendarModelTest {

    private fun session(id: String, startedAt: Long, localDate: String = "2026-01-01") = WorkoutSession(
        id = id,
        status = SessionStatus.FINISHED,
        startedAt = startedAt,
        endedAt = startedAt + 3_600_000,
        localDate = localDate,
        rating = null,
        note = null,
        restTargetAt = null,
        createdAt = startedAt,
        updatedAt = startedAt,
    )

    @Test
    fun monthGridPadsTheStartOfTheMonth() {
        // 2026-01-01 is a Thursday; Monday-first grid starts with 3 empty cells.
        val grid = CalendarModel.monthGrid(2026, 1)

        assertThat(grid.take(3)).containsExactly(null, null, null)
        assertThat(grid[3]).isEqualTo(LocalDate.of(2026, 1, 1))
        assertThat(grid.size % 7).isEqualTo(0)
    }

    @Test
    fun monthGridPadsTheEndOfTheMonth() {
        // January 2026 has 31 days -> 3 + 31 = 34 cells -> 2 trailing nulls.
        val grid = CalendarModel.monthGrid(2026, 1)

        assertThat(grid.size).isEqualTo(35)
        assertThat(grid[34]).isNull()
        assertThat(grid.filterNotNull().last()).isEqualTo(LocalDate.of(2026, 1, 31))
    }

    @Test
    fun monthGridHandlesLeapFebruary() {
        val grid = CalendarModel.monthGrid(2024, 2)

        assertThat(grid.filterNotNull()).hasSize(29)
        assertThat(grid.filterNotNull().last()).isEqualTo(LocalDate.of(2024, 2, 29))
        assertThat(grid.size % 7).isEqualTo(0)
    }

    @Test
    fun monthGridHandlesNonLeapFebruary() {
        val grid = CalendarModel.monthGrid(2023, 2)

        assertThat(grid.filterNotNull()).hasSize(28)
        assertThat(grid.filterNotNull().last()).isEqualTo(LocalDate.of(2023, 2, 28))
    }

    @Test
    fun monthGridSupportsSundayFirst() {
        // 2024-02-01 is a Thursday: Sunday-first leaves 4 leading nulls.
        val grid = CalendarModel.monthGrid(2024, 2, DayOfWeek.SUNDAY)

        assertThat(grid.take(4)).containsExactly(null, null, null, null)
        assertThat(grid[4]).isEqualTo(LocalDate.of(2024, 2, 1))
    }

    @Test
    fun monthGridMondayStartPutsMondayFirst() {
        // 2026-06-01 is a Monday: no leading nulls.
        val grid = CalendarModel.monthGrid(2026, 6)

        assertThat(grid.first()).isEqualTo(LocalDate.of(2026, 6, 1))
    }

    @Test
    fun regionDotsMergeTwoSessionsOnTheSameDay() {
        val sessions = listOf(session("s1", 1_000), session("s2", 2_000))
        val details = mapOf("s1" to setOf(0), "s2" to setOf(6))

        assertThat(CalendarModel.regionDots(sessions, details)).containsExactly(0, 6).inOrder()
    }

    @Test
    fun regionDotsTruncateToFourInPrdOrder() {
        val sessions = listOf(session("s1", 1_000))
        val details = mapOf("s1" to setOf(6, 3, 1, 5, 0, 4, 2))

        assertThat(CalendarModel.regionDots(sessions, details)).containsExactly(0, 1, 2, 3).inOrder()
    }

    @Test
    fun regionDotsAreEmptyWithoutDetails() {
        assertThat(CalendarModel.regionDots(listOf(session("s1", 1_000)), emptyMap())).isEmpty()
        assertThat(CalendarModel.regionDots(emptyList(), emptyMap())).isEmpty()
    }
}
