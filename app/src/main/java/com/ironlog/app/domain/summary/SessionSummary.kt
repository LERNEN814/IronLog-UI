package com.ironlog.app.domain.summary

import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet

data class SessionExerciseWithSets(
    val entry: SessionExercise,
    val sets: List<WorkoutSet>,
)

/** Muscle metadata needed by the summary. */
data class ExerciseMeta(val primaryBodyRegions: List<Int>)

data class SessionSummary(
    val durationS: Long,
    val exerciseCount: Int,
    val workSetCount: Int,
    val totalVolumeGrams: Long,
    val bodyRegions: Set<Int>,
)

private val WORK_SET_TYPES = setOf(SetType.WORK, SetType.DROP, SetType.FAILURE)

/**
 * DOMAIN_RULES.md section 4. Only completed sets are counted.
 *
 * @param nowMillis current time, used as the end of a session that is still in progress
 */
fun summarize(
    session: WorkoutSession,
    entries: List<SessionExerciseWithSets>,
    exerciseMeta: Map<String, ExerciseMeta>,
    nowMillis: Long,
): SessionSummary {
    // D2 (M1 audit): only entries with at least one completed set count as trained.
    val trainedEntries = entries.filter { entry -> entry.sets.any { it.isCompleted } }
    val completed = trainedEntries.flatMap { it.sets }.filter { it.isCompleted }
    val workSets = completed.filter { it.setType in WORK_SET_TYPES }
    val volume = workSets.sumOf { (it.weightGrams ?: 0).toLong() * (it.reps ?: 0) }
    val durationS = ((session.endedAt ?: nowMillis) - session.startedAt) / 1000
    val exerciseCount = trainedEntries.size
    val bodyRegions = trainedEntries
        .mapNotNull { exerciseMeta[it.entry.exerciseId] }
        .flatMap { it.primaryBodyRegions }
        .toSet()
    return SessionSummary(
        durationS = durationS,
        exerciseCount = exerciseCount,
        workSetCount = workSets.size,
        totalVolumeGrams = volume,
        bodyRegions = bodyRegions,
    )
}
