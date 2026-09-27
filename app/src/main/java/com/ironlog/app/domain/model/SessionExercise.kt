package com.ironlog.app.domain.model

data class SessionExercise(
    val id: String,
    val sessionId: String,
    val exerciseId: String,
    val orderIndex: Int,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
