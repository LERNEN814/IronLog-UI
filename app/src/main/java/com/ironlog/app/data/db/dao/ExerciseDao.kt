package com.ironlog.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMuscles(muscles: List<ExerciseMuscleEntity>)

    @Insert
    abstract suspend fun insertExercise(exercise: ExerciseEntity)

    @Update
    abstract suspend fun updateExercise(exercise: ExerciseEntity)

    @Query("SELECT COUNT(*) FROM exercise")
    abstract suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM exercise WHERE is_custom = 0")
    abstract suspend fun countBuiltIn(): Int

    @Query("SELECT * FROM exercise WHERE id = :id")
    abstract suspend fun getById(id: String): ExerciseEntity?

    /** Batch variant used by the calendar/history lists. */
    @Query("SELECT * FROM exercise WHERE id IN (:ids)")
    abstract suspend fun getByIds(ids: List<String>): List<ExerciseEntity>

    @Query("SELECT * FROM exercise WHERE is_archived = 0 ORDER BY is_custom DESC, name_zh")
    abstract fun observeAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise ORDER BY id")
    abstract suspend fun getAll(): List<ExerciseEntity>

    @Query("SELECT * FROM exercise_muscle ORDER BY exercise_id, muscle_id")
    abstract suspend fun getAllMuscles(): List<ExerciseMuscleEntity>

    @Query("DELETE FROM exercise_muscle")
    abstract suspend fun deleteAllMuscles()

    @Query("DELETE FROM exercise")
    abstract suspend fun deleteAll()

    /** @param bodyRegion muscle_group.body_region, or null for no region filtering */
    @Query(
        "SELECT e.* FROM exercise e " +
            "LEFT JOIN muscle_group m ON e.primary_muscle_id = m.id " +
            "WHERE e.is_archived = 0 " +
            "AND (:bodyRegion IS NULL OR m.body_region = :bodyRegion) " +
            "AND (:query = '' OR e.name_zh LIKE '%' || :query || '%' OR e.name_en LIKE '%' || :query || '%') " +
            "ORDER BY e.is_custom DESC, e.name_zh",
    )
    abstract fun search(query: String, bodyRegion: Int?): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercise SET is_archived = 1, updated_at = :updatedAt WHERE id = :id")
    abstract suspend fun archive(id: String, updatedAt: Long)

    @Query("DELETE FROM exercise_muscle WHERE exercise_id = :exerciseId")
    abstract suspend fun deleteMusclesFor(exerciseId: String)

    @Query(
        "SELECT se.exercise_id AS exercise_id, MAX(s.started_at) AS last_used_at " +
            "FROM session_exercise se " +
            "JOIN workout_session s ON se.session_id = s.id " +
            "GROUP BY se.exercise_id",
    )
    abstract fun observeLastUsed(): Flow<List<ExerciseLastUsed>>

    @Query("SELECT * FROM exercise_muscle WHERE exercise_id = :exerciseId ORDER BY role, weight DESC")
    abstract suspend fun getMusclesFor(exerciseId: String): List<ExerciseMuscleEntity>

    /** Batch variant used by the calendar/history lists. */
    @Query("SELECT * FROM exercise_muscle WHERE exercise_id IN (:exerciseIds) ORDER BY exercise_id, role, weight DESC")
    abstract suspend fun getMusclesForExercises(exerciseIds: List<String>): List<ExerciseMuscleEntity>

    @Query(
        "SELECT COUNT(*) FROM exercise e WHERE NOT EXISTS " +
            "(SELECT 1 FROM exercise_muscle em WHERE em.exercise_id = e.id AND em.role = 0)",
    )
    abstract suspend fun countWithoutPrimaryMuscle(): Int

    /** Exercise row plus its muscle mappings, applied atomically. */
    @Transaction
    open suspend fun upsertCustomExercise(exercise: ExerciseEntity, muscles: List<ExerciseMuscleEntity>) {
        if (getById(exercise.id) == null) insertExercise(exercise) else updateExercise(exercise)
        deleteMusclesFor(exercise.id)
        insertMuscles(muscles)
    }
}
