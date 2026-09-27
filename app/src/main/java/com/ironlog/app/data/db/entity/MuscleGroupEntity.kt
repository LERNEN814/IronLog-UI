package com.ironlog.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muscle_group")
data class MuscleGroupEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "display_name_en") val displayNameEn: String,
    @ColumnInfo(name = "display_name_zh") val displayNameZh: String,
    @ColumnInfo(name = "body_region") val bodyRegion: Int,
    @ColumnInfo(name = "recovery_half_life_hours") val recoveryHalfLifeHours: Int,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
)
