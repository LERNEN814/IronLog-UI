package com.ironlog.app.domain.calendar

import com.ironlog.app.domain.model.WorkoutSession
import java.time.DayOfWeek
import java.time.LocalDate

/** PRD R-4.2 / R-4.3: pure calendar grid and body-region dots. */
object CalendarModel {

    /** Max dots shown under a day (PRD R-4.2). */
    const val MAX_DOTS = 4

    /**
     * Grid of [year]/[month] with leading/trailing nulls so the first row starts on
     * [firstDayOfWeek] and the last row is complete. One entry per cell.
     */
    fun monthGrid(year: Int, month: Int, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate?> {
        val first = LocalDate.of(year, month, 1)
        val leading = ((first.dayOfWeek.value - firstDayOfWeek.value) + 7) % 7
        val cells = ArrayList<LocalDate?>(leading + first.lengthOfMonth() + 6)
        repeat(leading) { cells += null }
        for (day in 1..first.lengthOfMonth()) {
            cells += LocalDate.of(year, month, day)
        }
        while (cells.size % 7 != 0) cells += null
        return cells
    }

    /**
     * Body regions trained on one day (union across sessions), sorted in PRD order
     * (chest/back/shoulders/arms/legs/core/cardio) and truncated to [MAX_DOTS].
     *
     * @param details sessionId -> PRIMARY body regions from SessionSummary.bodyRegions
     */
    fun regionDots(sessions: List<WorkoutSession>, details: Map<String, Set<Int>>): List<Int> =
        sessions
            .sortedBy { it.startedAt }
            .flatMap { details[it.id].orEmpty() }
            .distinct()
            .sorted()
            .take(MAX_DOTS)
}
