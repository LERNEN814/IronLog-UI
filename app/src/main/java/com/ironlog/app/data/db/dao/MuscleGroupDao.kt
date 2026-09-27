package com.ironlog.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleGroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(groups: List<MuscleGroupEntity>)

    @Query("SELECT COUNT(*) FROM muscle_group")
    suspend fun count(): Int

    @Query("SELECT * FROM muscle_group WHERE id = :id")
    suspend fun getById(id: String): MuscleGroupEntity?

    @Query("SELECT * FROM muscle_group ORDER BY sort_order")
    suspend fun getAll(): List<MuscleGroupEntity>

    @Query("SELECT * FROM muscle_group ORDER BY sort_order")
    fun observeAll(): Flow<List<MuscleGroupEntity>>
}
