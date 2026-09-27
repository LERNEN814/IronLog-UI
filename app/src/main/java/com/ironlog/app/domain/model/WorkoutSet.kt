package com.ironlog.app.domain.model

data class WorkoutSet(
    val id: String,
    val sessionExerciseId: String,
    val orderIndex: Int,
    val setType: SetType,
    val parentSetId: String?,
    val weightGrams: Int?,
    val inputUnit: WeightUnit,
    val reps: Int?,
    val rir: Int?,
    val durationS: Int?,
    val distanceM: Int?,
    val level: Int?,
    val inclineX10: Int?,
    val speedX10: Int?,
    val completedAt: Long?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
