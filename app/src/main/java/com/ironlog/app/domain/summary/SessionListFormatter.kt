package com.ironlog.app.domain.summary

import com.ironlog.app.domain.model.WorkoutSession

/** Builds list text from raw data; UI adds localized separators and suffixes. */
object SessionListFormatter {
    const val MAX_NAMES = 3

    /** First three exercise names plus how many were hidden. */
    fun exerciseSummary(names: List<String>): ExerciseSummary =
        ExerciseSummary(
            shownNames = names.take(MAX_NAMES),
            hiddenCount = (names.size - MAX_NAMES).coerceAtLeast(0),
        )

    /** Newest session first. */
    fun sortByStartedAtDesc(sessions: List<WorkoutSession>): List<WorkoutSession> =
        sessions.sortedByDescending { it.startedAt }
}

data class ExerciseSummary(
    val shownNames: List<String>,
    val hiddenCount: Int,
)
