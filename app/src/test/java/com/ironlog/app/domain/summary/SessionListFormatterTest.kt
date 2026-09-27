package com.ironlog.app.domain.summary

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.WorkoutSession
import org.junit.Test

/** M2-T2.5: history list sorting and exercise summary text data. */
class SessionListFormatterTest {

    private fun session(id: String, startedAt: Long) = WorkoutSession(
        id = id,
        status = SessionStatus.FINISHED,
        startedAt = startedAt,
        endedAt = startedAt + 3_600_000,
        localDate = "2026-01-01",
        rating = null,
        note = null,
        restTargetAt = null,
        createdAt = startedAt,
        updatedAt = startedAt,
    )

    @Test
    fun sortsNewestFirst() {
        val sessions = listOf(session("old", 1_000), session("new", 3_000), session("middle", 2_000))

        val sorted = SessionListFormatter.sortByStartedAtDesc(sessions)

        assertThat(sorted.map { it.id }).containsExactly("new", "middle", "old").inOrder()
    }

    @Test
    fun keepsAtMostThreeNames() {
        val summary = SessionListFormatter.exerciseSummary(listOf("A", "B", "C", "D", "E"))

        assertThat(summary.shownNames).containsExactly("A", "B", "C").inOrder()
        assertThat(summary.hiddenCount).isEqualTo(2)
    }

    @Test
    fun keepsShortListUnchanged() {
        val summary = SessionListFormatter.exerciseSummary(listOf("Bench", "Squat"))

        assertThat(summary.shownNames).containsExactly("Bench", "Squat").inOrder()
        assertThat(summary.hiddenCount).isEqualTo(0)
    }

    @Test
    fun handlesEmptyAndNullNameLists() {
        val empty = SessionListFormatter.exerciseSummary(emptyList())

        assertThat(empty.shownNames).isEmpty()
        assertThat(empty.hiddenCount).isEqualTo(0)
    }
}
