package com.ironlog.app.data.db.dao

import androidx.room.ColumnInfo

/** Projection for the "recently used" ordering of the exercise picker. */
data class ExerciseLastUsed(
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "last_used_at") val lastUsedAt: Long,
)
