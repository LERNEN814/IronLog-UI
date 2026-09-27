package com.ironlog.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = SessionExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("session_exercise_id")],
)
data class WorkoutSetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_exercise_id") val sessionExerciseId: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "set_type", defaultValue = "0") val setType: Int,
    @ColumnInfo(name = "parent_set_id") val parentSetId: String?,
    @ColumnInfo(name = "weight_g") val weightG: Int?,
    @ColumnInfo(name = "input_unit", defaultValue = "0") val inputUnit: Int,
    val reps: Int?,
    val rir: Int?,
    @ColumnInfo(name = "duration_s") val durationS: Int?,
    @ColumnInfo(name = "distance_m") val distanceM: Int?,
    val level: Int?,
    @ColumnInfo(name = "incline_x10") val inclineX10: Int?,
    @ColumnInfo(name = "speed_x10") val speedX10: Int?,
    @ColumnInfo(name = "completed_at") val completedAt: Long?,
    @ColumnInfo(name = "is_completed", defaultValue = "0") val isCompleted: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
