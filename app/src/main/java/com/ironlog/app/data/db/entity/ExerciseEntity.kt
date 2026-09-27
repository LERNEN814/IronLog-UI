package com.ironlog.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_zh") val nameZh: String,
    @ColumnInfo(name = "name_en") val nameEn: String,
    @ColumnInfo(defaultValue = "0") val kind: Int,
    val equipment: String,
    @ColumnInfo(name = "primary_muscle_id") val primaryMuscleId: String?,
    @ColumnInfo(name = "is_custom", defaultValue = "0") val isCustom: Int,
    @ColumnInfo(name = "is_archived", defaultValue = "0") val isArchived: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
