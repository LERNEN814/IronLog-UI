package com.ironlog.app.domain.model

data class ExerciseMuscle(
    val exerciseId: String,
    val muscleId: String,
    val role: MuscleRole,
    val weight: Double,
)
