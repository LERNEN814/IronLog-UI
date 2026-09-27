package com.ironlog.app.domain.summary

import com.ironlog.app.domain.model.WorkoutSession

/** A finished session with everything the home/history lists need. */
data class SessionHeader(
    val session: WorkoutSession,
    val exerciseNames: List<String>,
    val bodyRegions: Set<Int>,
    val summary: SessionSummary,
)

/** Full read-only session detail for the history screen. */
data class SessionDetail(
    val header: SessionHeader,
    val entries: List<SessionExerciseWithSets>,
    val exerciseNames: Map<String, String>,
)

/** Home screen weekly counters. */
data class WeekStats(
    val sessionCount: Int,
    val workSetCount: Int,
)
