package com.ironlog.app.domain.model

data class WorkoutSession(
    val id: String,
    val status: SessionStatus,
    val startedAt: Long,
    val endedAt: Long?,
    val localDate: String,
    val rating: Int?,
    val note: String?,
    val restTargetAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)
