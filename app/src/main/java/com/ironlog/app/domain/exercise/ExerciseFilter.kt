package com.ironlog.app.domain.exercise

import com.ironlog.app.domain.model.Exercise

/** Pure list shaping for the exercise picker. */
object ExerciseFilter {

    fun filter(
        exercises: List<Exercise>,
        bodyRegionByMuscleId: Map<String, Int>,
        query: String,
        bodyRegion: Int?,
    ): List<Exercise> {
        val needle = query.trim().lowercase()
        return exercises.filter { exercise ->
            val matchesQuery = needle.isEmpty() ||
                exercise.nameZh.lowercase().contains(needle) ||
                exercise.nameEn.lowercase().contains(needle)
            val matchesRegion = bodyRegion == null ||
                exercise.primaryMuscleId?.let { bodyRegionByMuscleId[it] } == bodyRegion
            matchesQuery && matchesRegion
        }
    }

    /** Custom exercises first, then most recently used, then by Chinese name. */
    fun sort(exercises: List<Exercise>, lastUsedAt: Map<String, Long>): List<Exercise> =
        exercises.sortedWith(
            compareByDescending<Exercise> { it.isCustom }
                .thenByDescending { lastUsedAt[it.id] ?: -1L }
                .thenBy { it.nameZh },
        )
}
