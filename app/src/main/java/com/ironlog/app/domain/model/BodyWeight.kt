package com.ironlog.app.domain.model

data class BodyWeight(
    val id: String,
    val localDate: String,
    val weightGrams: Int,
    val recordedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
