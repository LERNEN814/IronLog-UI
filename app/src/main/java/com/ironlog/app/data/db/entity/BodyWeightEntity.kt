package com.ironlog.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "body_weight", indices = [Index(value = ["local_date"], unique = true)])
data class BodyWeightEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "local_date") val localDate: String,
    @ColumnInfo(name = "weight_g") val weightG: Int,
    @ColumnInfo(name = "recorded_at") val recordedAt: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
