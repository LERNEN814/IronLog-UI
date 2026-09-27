package com.ironlog.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ironlog.app.data.db.entity.BodyWeightEntity

@Dao
interface BodyWeightDao {
    /** One entry per local date: re-recording the same day replaces it (DATA_MODEL section 3.7). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: BodyWeightEntity)

    @Query("SELECT * FROM body_weight WHERE local_date = :localDate")
    suspend fun getByDate(localDate: String): BodyWeightEntity?

    @Query("SELECT * FROM body_weight ORDER BY local_date DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<BodyWeightEntity>
}
