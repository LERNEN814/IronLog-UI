package com.ironlog.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "exercise_muscle", primaryKeys = ["exercise_id", "muscle_id"])
data class ExerciseMuscleEntity(
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "muscle_id") val muscleId: String,
    @ColumnInfo(defaultValue = "0") val role: Int,
    val weight: Double,
)
