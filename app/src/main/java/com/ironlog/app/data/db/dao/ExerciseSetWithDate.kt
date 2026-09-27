package com.ironlog.app.data.db.dao

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ironlog.app.data.db.entity.WorkoutSetEntity

/** A set plus the local date of the (finished) session it belongs to. */
data class ExerciseSetWithDate(
    @Embedded val set: WorkoutSetEntity,
    @ColumnInfo(name = "local_date") val localDate: String,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "session_id") val sessionId: String,
)
