package com.ironlog.app.domain.model

data class MuscleGroup(
    val id: String,
    val displayNameEn: String,
    val displayNameZh: String,
    val bodyRegion: Int,
    val recoveryHalfLifeHours: Int,
    val sortOrder: Int,
)
