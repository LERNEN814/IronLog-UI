package com.ironlog.app.domain.model

data class Exercise(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val kind: ExerciseKind,
    val equipment: String,
    val primaryMuscleId: String?,
    val isCustom: Boolean,
    val isArchived: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
