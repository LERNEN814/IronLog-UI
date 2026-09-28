package com.ironlog.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WorkoutDao {

    @Query("SELECT * FROM workout_session ORDER BY id")
    abstract suspend fun getAllSessions(): List<WorkoutSessionEntity>

    @Query("SELECT * FROM session_exercise ORDER BY id")
    abstract suspend fun getAllSessionExercises(): List<SessionExerciseEntity>

    @Query("SELECT * FROM workout_set ORDER BY id")
    abstract suspend fun getAllSets(): List<WorkoutSetEntity>

    @Query("DELETE FROM workout_set")
    abstract suspend fun deleteAllSets()

    @Query("DELETE FROM session_exercise")
    abstract suspend fun deleteAllSessionExercises()

    @Query("DELETE FROM workout_session")
    abstract suspend fun deleteAllSessions()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSessions(sessions: List<WorkoutSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSessionExercise(entry: SessionExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSessionExercises(entries: List<SessionExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSet(set: WorkoutSetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Update
    abstract suspend fun updateSession(session: WorkoutSessionEntity)

    @Update
    abstract suspend fun updateSessionExercise(entry: SessionExerciseEntity)

    @Update
    abstract suspend fun updateSet(set: WorkoutSetEntity)

    @Query("DELETE FROM workout_session WHERE id = :id")
    abstract suspend fun deleteSession(id: String)

    @Query("DELETE FROM session_exercise WHERE id = :id")
    abstract suspend fun deleteSessionExercise(id: String)

    @Query("DELETE FROM workout_set WHERE id = :id")
    abstract suspend fun deleteSet(id: String)

    @Query("SELECT * FROM workout_session WHERE id = :id")
    abstract suspend fun getSession(id: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_session WHERE status = 0 ORDER BY started_at DESC LIMIT 1")
    abstract suspend fun getInProgressSession(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_session WHERE status = 0 ORDER BY started_at DESC LIMIT 1")
    abstract fun observeInProgressSession(): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_session WHERE status = 1 ORDER BY started_at DESC LIMIT :limit")
    abstract fun observeRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT COUNT(*) FROM workout_session WHERE status = 0")
    abstract suspend fun countInProgressSessions(): Int

    @Query("SELECT COUNT(*) FROM workout_session WHERE status = 1 AND started_at >= :sinceMillis")
    abstract fun observeFinishedSessionCountSince(sinceMillis: Long): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM workout_set ws " +
            "JOIN session_exercise se ON ws.session_exercise_id = se.id " +
            "JOIN workout_session s ON se.session_id = s.id " +
            "WHERE s.status = 1 AND s.started_at >= :sinceMillis " +
            "AND ws.is_completed = 1 AND ws.set_type != 1",
    )
    abstract fun observeCompletedWorkSetCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT * FROM session_exercise WHERE session_id = :sessionId ORDER BY order_index")
    abstract suspend fun getEntriesFor(sessionId: String): List<SessionExerciseEntity>

    /** Batch variant used by the calendar/history lists (one query for N sessions). */
    @Query("SELECT * FROM session_exercise WHERE session_id IN (:sessionIds) ORDER BY session_id, order_index")
    abstract suspend fun getEntriesForSessions(sessionIds: List<String>): List<SessionExerciseEntity>

    @Query("SELECT * FROM session_exercise WHERE id = :id")
    abstract suspend fun getEntry(id: String): SessionExerciseEntity?

    @Query("SELECT * FROM workout_set WHERE session_exercise_id = :entryId ORDER BY order_index")
    abstract suspend fun getSetsFor(entryId: String): List<WorkoutSetEntity>

    /** Batch variant used by the calendar/history lists (one query for N entries). */
    @Query("SELECT * FROM workout_set WHERE session_exercise_id IN (:entryIds) ORDER BY session_exercise_id, order_index")
    abstract suspend fun getSetsForEntries(entryIds: List<String>): List<WorkoutSetEntity>

    /** DOMAIN_RULES §5: completed sets of finished sessions inside the fatigue window. */
    @Query(
        "SELECT ws.*, s.started_at AS session_started_at, se.exercise_id AS exercise_id, e.kind AS exercise_kind " +
            "FROM workout_set ws " +
            "JOIN session_exercise se ON ws.session_exercise_id = se.id " +
            "JOIN workout_session s ON se.session_id = s.id " +
            "JOIN exercise e ON se.exercise_id = e.id " +
            "WHERE ws.is_completed = 1 " +
            "AND COALESCE(ws.completed_at, s.started_at) >= :sinceMillis",
    )
    abstract suspend fun getFatigueSetsSince(sinceMillis: Long): List<FatigueSetRow>

    /** All sets of an exercise across finished sessions (exercise history chart). */
    @Query(
        "SELECT ws.*, s.local_date AS local_date, s.started_at AS started_at, s.id AS session_id FROM workout_set ws " +
            "JOIN session_exercise se ON ws.session_exercise_id = se.id " +
            "JOIN workout_session s ON se.session_id = s.id " +
            "WHERE se.exercise_id = :exerciseId AND s.status = 1 " +
            "ORDER BY s.started_at ASC, ws.order_index ASC",
    )
    abstract suspend fun getExerciseHistorySets(exerciseId: String): List<ExerciseSetWithDate>

    /** Atomically creates the session only when no other IN_PROGRESS session exists. */
    @Transaction
    open suspend fun insertSessionIfNone(session: WorkoutSessionEntity): Boolean {
        if (getInProgressSession() != null) return false
        insertSession(session)
        return true
    }

    /**
     * D7: creates a template session (session + entries + placeholder sets) atomically.
     * Returns false without writing anything when another session is already in progress.
     */
    @Transaction
    open suspend fun insertTemplateSession(
        session: WorkoutSessionEntity,
        entries: List<SessionExerciseEntity>,
        sets: List<WorkoutSetEntity>,
    ): Boolean {
        if (getInProgressSession() != null) return false
        insertSession(session)
        if (entries.isNotEmpty()) insertSessionExercises(entries)
        if (sets.isNotEmpty()) insertSets(sets)
        return true
    }

    /**
     * DOMAIN_RULES.md section 3.1: completed sets of the most recent finished session entry
     * that holds this exercise and has at least one completed set.
     */
    @Query(
        "SELECT ws.* FROM workout_set ws " +
            "JOIN session_exercise se ON ws.session_exercise_id = se.id " +
            "WHERE se.id = (" +
            "  SELECT se2.id FROM session_exercise se2 " +
            "  JOIN workout_session s ON se2.session_id = s.id " +
            "  WHERE se2.exercise_id = :exerciseId AND s.status = 1 " +
            "    AND EXISTS (SELECT 1 FROM workout_set x WHERE x.session_exercise_id = se2.id AND x.is_completed = 1) " +
            "  ORDER BY s.started_at DESC, se2.order_index DESC " +
            "  LIMIT 1" +
            ") " +
            "AND ws.is_completed = 1 " +
            "ORDER BY ws.order_index ASC",
    )
    abstract suspend fun getLastSetsForExercise(exerciseId: String): List<WorkoutSetEntity>
}
