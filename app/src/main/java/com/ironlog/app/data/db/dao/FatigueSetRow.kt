package com.ironlog.app.data.db.dao

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ironlog.app.data.db.entity.WorkoutSetEntity

/** One completed set with the data needed to build a [com.ironlog.app.domain.fatigue.FatigueInputSet]. */
data class FatigueSetRow(
    @Embedded val set: WorkoutSetEntity,
    @ColumnInfo(name = "session_started_at") val sessionStartedAt: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "exercise_kind") val exerciseKind: Int,
)
