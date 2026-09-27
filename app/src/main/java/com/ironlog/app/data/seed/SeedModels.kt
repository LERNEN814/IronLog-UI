package com.ironlog.app.data.seed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Shape of assets/seed/seed_v1.json (DATA_MODEL.md section 5). */
@Serializable
data class SeedFile(
    val version: Int,
    val muscles: List<SeedMuscle>,
    val exercises: List<SeedExercise>,
)

@Serializable
data class SeedMuscle(
    val id: String,
    @SerialName("display_name_en") val displayNameEn: String,
    @SerialName("display_name_zh") val displayNameZh: String,
    @SerialName("body_region") val bodyRegion: Int,
    @SerialName("recovery_half_life_hours") val recoveryHalfLifeHours: Int,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class SeedExercise(
    val id: String,
    @SerialName("name_zh") val nameZh: String,
    @SerialName("name_en") val nameEn: String,
    val kind: Int,
    val equipment: String,
    @SerialName("primary_muscle_id") val primaryMuscleId: String?,
    val muscles: List<SeedExerciseMuscle> = emptyList(),
)

@Serializable
data class SeedExerciseMuscle(
    @SerialName("muscle_id") val muscleId: String,
    val role: Int,
    val weight: Double,
)
