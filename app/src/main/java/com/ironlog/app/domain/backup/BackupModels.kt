package com.ironlog.app.domain.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackupFile(
    @SerialName("schema_version") val schemaVersion: Int,
    @SerialName("app_version") val appVersion: String,
    @SerialName("exported_at") val exportedAt: Long,
    val muscles: List<BackupMuscle> = emptyList(),
    val exercises: List<BackupExercise> = emptyList(),
    @SerialName("exercise_muscles") val exerciseMuscles: List<BackupExerciseMuscle> = emptyList(),
    val sessions: List<BackupSession> = emptyList(),
    @SerialName("session_exercises") val sessionExercises: List<BackupSessionExercise> = emptyList(),
    val sets: List<BackupSet> = emptyList(),
    @SerialName("body_weights") val bodyWeights: List<BackupBodyWeight> = emptyList(),
)

@Serializable data class BackupMuscle(val id: String, @SerialName("display_name_en") val displayNameEn: String, @SerialName("display_name_zh") val displayNameZh: String, @SerialName("body_region") val bodyRegion: Int, @SerialName("recovery_half_life_hours") val recoveryHalfLifeHours: Int, @SerialName("sort_order") val sortOrder: Int)
@Serializable data class BackupExercise(val id: String, @SerialName("name_zh") val nameZh: String, @SerialName("name_en") val nameEn: String, val kind: Int, val equipment: String, @SerialName("primary_muscle_id") val primaryMuscleId: String?, @SerialName("is_custom") val isCustom: Int, @SerialName("is_archived") val isArchived: Int, @SerialName("created_at") val createdAt: Long, @SerialName("updated_at") val updatedAt: Long)
@Serializable data class BackupExerciseMuscle(@SerialName("exercise_id") val exerciseId: String, @SerialName("muscle_id") val muscleId: String, val role: Int, val weight: Double)
@Serializable data class BackupSession(val id: String, val status: Int, @SerialName("started_at") val startedAt: Long, @SerialName("ended_at") val endedAt: Long?, @SerialName("local_date") val localDate: String, val rating: Int?, val note: String?, @SerialName("rest_target_at") val restTargetAt: Long?, @SerialName("created_at") val createdAt: Long, @SerialName("updated_at") val updatedAt: Long)
@Serializable data class BackupSessionExercise(val id: String, @SerialName("session_id") val sessionId: String, @SerialName("exercise_id") val exerciseId: String, @SerialName("order_index") val orderIndex: Int, val note: String?, @SerialName("created_at") val createdAt: Long, @SerialName("updated_at") val updatedAt: Long)
@Serializable data class BackupSet(val id: String, @SerialName("session_exercise_id") val sessionExerciseId: String, @SerialName("order_index") val orderIndex: Int, @SerialName("set_type") val setType: Int, @SerialName("parent_set_id") val parentSetId: String?, @SerialName("weight_g") val weightG: Int?, @SerialName("input_unit") val inputUnit: Int, val reps: Int?, val rir: Int?, @SerialName("duration_s") val durationS: Int?, @SerialName("distance_m") val distanceM: Int?, val level: Int?, @SerialName("incline_x10") val inclineX10: Int?, @SerialName("speed_x10") val speedX10: Int?, @SerialName("completed_at") val completedAt: Long?, @SerialName("is_completed") val isCompleted: Int, @SerialName("created_at") val createdAt: Long, @SerialName("updated_at") val updatedAt: Long)
@Serializable data class BackupBodyWeight(val id: String, @SerialName("local_date") val localDate: String, @SerialName("weight_g") val weightG: Int, @SerialName("recorded_at") val recordedAt: Long, @SerialName("created_at") val createdAt: Long, @SerialName("updated_at") val updatedAt: Long)
