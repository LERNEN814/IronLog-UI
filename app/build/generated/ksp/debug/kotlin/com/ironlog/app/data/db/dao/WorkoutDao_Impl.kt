package com.ironlog.app.`data`.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.ironlog.app.`data`.db.entity.SessionExerciseEntity
import com.ironlog.app.`data`.db.entity.WorkoutSessionEntity
import com.ironlog.app.`data`.db.entity.WorkoutSetEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WorkoutDao_Impl(
  __db: RoomDatabase,
) : WorkoutDao() {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkoutSessionEntity: EntityInsertAdapter<WorkoutSessionEntity>

  private val __insertAdapterOfSessionExerciseEntity: EntityInsertAdapter<SessionExerciseEntity>

  private val __insertAdapterOfWorkoutSetEntity: EntityInsertAdapter<WorkoutSetEntity>

  private val __updateAdapterOfWorkoutSessionEntity:
      EntityDeleteOrUpdateAdapter<WorkoutSessionEntity>

  private val __updateAdapterOfSessionExerciseEntity:
      EntityDeleteOrUpdateAdapter<SessionExerciseEntity>

  private val __updateAdapterOfWorkoutSetEntity: EntityDeleteOrUpdateAdapter<WorkoutSetEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkoutSessionEntity = object : EntityInsertAdapter<WorkoutSessionEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `workout_session` (`id`,`status`,`started_at`,`ended_at`,`local_date`,`rating`,`note`,`rest_target_at`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.status.toLong())
        statement.bindLong(3, entity.startedAt)
        val _tmpEndedAt: Long? = entity.endedAt
        if (_tmpEndedAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpEndedAt)
        }
        statement.bindText(5, entity.localDate)
        val _tmpRating: Int? = entity.rating
        if (_tmpRating == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRating.toLong())
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNote)
        }
        val _tmpRestTargetAt: Long? = entity.restTargetAt
        if (_tmpRestTargetAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpRestTargetAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }
    this.__insertAdapterOfSessionExerciseEntity = object : EntityInsertAdapter<SessionExerciseEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `session_exercise` (`id`,`session_id`,`exercise_id`,`order_index`,`note`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SessionExerciseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionId)
        statement.bindText(3, entity.exerciseId)
        statement.bindLong(4, entity.orderIndex.toLong())
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNote)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
      }
    }
    this.__insertAdapterOfWorkoutSetEntity = object : EntityInsertAdapter<WorkoutSetEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `workout_set` (`id`,`session_exercise_id`,`order_index`,`set_type`,`parent_set_id`,`weight_g`,`input_unit`,`reps`,`rir`,`duration_s`,`distance_m`,`level`,`incline_x10`,`speed_x10`,`completed_at`,`is_completed`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutSetEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionExerciseId)
        statement.bindLong(3, entity.orderIndex.toLong())
        statement.bindLong(4, entity.setType.toLong())
        val _tmpParentSetId: String? = entity.parentSetId
        if (_tmpParentSetId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpParentSetId)
        }
        val _tmpWeightG: Int? = entity.weightG
        if (_tmpWeightG == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpWeightG.toLong())
        }
        statement.bindLong(7, entity.inputUnit.toLong())
        val _tmpReps: Int? = entity.reps
        if (_tmpReps == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReps.toLong())
        }
        val _tmpRir: Int? = entity.rir
        if (_tmpRir == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpRir.toLong())
        }
        val _tmpDurationS: Int? = entity.durationS
        if (_tmpDurationS == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDurationS.toLong())
        }
        val _tmpDistanceM: Int? = entity.distanceM
        if (_tmpDistanceM == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpDistanceM.toLong())
        }
        val _tmpLevel: Int? = entity.level
        if (_tmpLevel == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLevel.toLong())
        }
        val _tmpInclineX10: Int? = entity.inclineX10
        if (_tmpInclineX10 == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpInclineX10.toLong())
        }
        val _tmpSpeedX10: Int? = entity.speedX10
        if (_tmpSpeedX10 == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpSpeedX10.toLong())
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpCompletedAt)
        }
        statement.bindLong(16, entity.isCompleted.toLong())
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
      }
    }
    this.__updateAdapterOfWorkoutSessionEntity = object : EntityDeleteOrUpdateAdapter<WorkoutSessionEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `workout_session` SET `id` = ?,`status` = ?,`started_at` = ?,`ended_at` = ?,`local_date` = ?,`rating` = ?,`note` = ?,`rest_target_at` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.status.toLong())
        statement.bindLong(3, entity.startedAt)
        val _tmpEndedAt: Long? = entity.endedAt
        if (_tmpEndedAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpEndedAt)
        }
        statement.bindText(5, entity.localDate)
        val _tmpRating: Int? = entity.rating
        if (_tmpRating == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRating.toLong())
        }
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNote)
        }
        val _tmpRestTargetAt: Long? = entity.restTargetAt
        if (_tmpRestTargetAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpRestTargetAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindText(11, entity.id)
      }
    }
    this.__updateAdapterOfSessionExerciseEntity = object : EntityDeleteOrUpdateAdapter<SessionExerciseEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `session_exercise` SET `id` = ?,`session_id` = ?,`exercise_id` = ?,`order_index` = ?,`note` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SessionExerciseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionId)
        statement.bindText(3, entity.exerciseId)
        statement.bindLong(4, entity.orderIndex.toLong())
        val _tmpNote: String? = entity.note
        if (_tmpNote == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNote)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        statement.bindText(8, entity.id)
      }
    }
    this.__updateAdapterOfWorkoutSetEntity = object : EntityDeleteOrUpdateAdapter<WorkoutSetEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `workout_set` SET `id` = ?,`session_exercise_id` = ?,`order_index` = ?,`set_type` = ?,`parent_set_id` = ?,`weight_g` = ?,`input_unit` = ?,`reps` = ?,`rir` = ?,`duration_s` = ?,`distance_m` = ?,`level` = ?,`incline_x10` = ?,`speed_x10` = ?,`completed_at` = ?,`is_completed` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutSetEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionExerciseId)
        statement.bindLong(3, entity.orderIndex.toLong())
        statement.bindLong(4, entity.setType.toLong())
        val _tmpParentSetId: String? = entity.parentSetId
        if (_tmpParentSetId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpParentSetId)
        }
        val _tmpWeightG: Int? = entity.weightG
        if (_tmpWeightG == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpWeightG.toLong())
        }
        statement.bindLong(7, entity.inputUnit.toLong())
        val _tmpReps: Int? = entity.reps
        if (_tmpReps == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReps.toLong())
        }
        val _tmpRir: Int? = entity.rir
        if (_tmpRir == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpRir.toLong())
        }
        val _tmpDurationS: Int? = entity.durationS
        if (_tmpDurationS == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDurationS.toLong())
        }
        val _tmpDistanceM: Int? = entity.distanceM
        if (_tmpDistanceM == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpDistanceM.toLong())
        }
        val _tmpLevel: Int? = entity.level
        if (_tmpLevel == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLevel.toLong())
        }
        val _tmpInclineX10: Int? = entity.inclineX10
        if (_tmpInclineX10 == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpInclineX10.toLong())
        }
        val _tmpSpeedX10: Int? = entity.speedX10
        if (_tmpSpeedX10 == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpSpeedX10.toLong())
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpCompletedAt)
        }
        statement.bindLong(16, entity.isCompleted.toLong())
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        statement.bindText(19, entity.id)
      }
    }
  }

  public override suspend fun insertSession(session: WorkoutSessionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWorkoutSessionEntity.insert(_connection, session)
  }

  public override suspend fun insertSessionExercise(entry: SessionExerciseEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSessionExerciseEntity.insert(_connection, entry)
  }

  public override suspend fun insertSessionExercises(entries: List<SessionExerciseEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSessionExerciseEntity.insert(_connection, entries)
  }

  public override suspend fun insertSet(`set`: WorkoutSetEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWorkoutSetEntity.insert(_connection, set)
  }

  public override suspend fun insertSets(sets: List<WorkoutSetEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWorkoutSetEntity.insert(_connection, sets)
  }

  public override suspend fun updateSession(session: WorkoutSessionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfWorkoutSessionEntity.handle(_connection, session)
  }

  public override suspend fun updateSessionExercise(entry: SessionExerciseEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfSessionExerciseEntity.handle(_connection, entry)
  }

  public override suspend fun updateSet(`set`: WorkoutSetEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfWorkoutSetEntity.handle(_connection, set)
  }

  public override suspend fun insertSessionIfNone(session: WorkoutSessionEntity): Boolean = performInTransactionSuspending(__db) {
    super@WorkoutDao_Impl.insertSessionIfNone(session)
  }

  public override suspend fun insertTemplateSession(
    session: WorkoutSessionEntity,
    entries: List<SessionExerciseEntity>,
    sets: List<WorkoutSetEntity>,
  ): Boolean = performInTransactionSuspending(__db) {
    super@WorkoutDao_Impl.insertTemplateSession(session, entries, sets)
  }

  public override suspend fun getSession(id: String): WorkoutSessionEntity? {
    val _sql: String = "SELECT * FROM workout_session WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "started_at")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "ended_at")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfRestTargetAt: Int = getColumnIndexOrThrow(_stmt, "rest_target_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: WorkoutSessionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpRestTargetAt: Long?
          if (_stmt.isNull(_columnIndexOfRestTargetAt)) {
            _tmpRestTargetAt = null
          } else {
            _tmpRestTargetAt = _stmt.getLong(_columnIndexOfRestTargetAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = WorkoutSessionEntity(_tmpId,_tmpStatus,_tmpStartedAt,_tmpEndedAt,_tmpLocalDate,_tmpRating,_tmpNote,_tmpRestTargetAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getInProgressSession(): WorkoutSessionEntity? {
    val _sql: String = "SELECT * FROM workout_session WHERE status = 0 ORDER BY started_at DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "started_at")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "ended_at")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfRestTargetAt: Int = getColumnIndexOrThrow(_stmt, "rest_target_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: WorkoutSessionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpRestTargetAt: Long?
          if (_stmt.isNull(_columnIndexOfRestTargetAt)) {
            _tmpRestTargetAt = null
          } else {
            _tmpRestTargetAt = _stmt.getLong(_columnIndexOfRestTargetAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = WorkoutSessionEntity(_tmpId,_tmpStatus,_tmpStartedAt,_tmpEndedAt,_tmpLocalDate,_tmpRating,_tmpNote,_tmpRestTargetAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeInProgressSession(): Flow<WorkoutSessionEntity?> {
    val _sql: String = "SELECT * FROM workout_session WHERE status = 0 ORDER BY started_at DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("workout_session")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "started_at")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "ended_at")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfRestTargetAt: Int = getColumnIndexOrThrow(_stmt, "rest_target_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: WorkoutSessionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpRestTargetAt: Long?
          if (_stmt.isNull(_columnIndexOfRestTargetAt)) {
            _tmpRestTargetAt = null
          } else {
            _tmpRestTargetAt = _stmt.getLong(_columnIndexOfRestTargetAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = WorkoutSessionEntity(_tmpId,_tmpStatus,_tmpStartedAt,_tmpEndedAt,_tmpLocalDate,_tmpRating,_tmpNote,_tmpRestTargetAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>> {
    val _sql: String = "SELECT * FROM workout_session WHERE status = 1 ORDER BY started_at DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("workout_session")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "started_at")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "ended_at")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfRestTargetAt: Int = getColumnIndexOrThrow(_stmt, "rest_target_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<WorkoutSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpRating: Int?
          if (_stmt.isNull(_columnIndexOfRating)) {
            _tmpRating = null
          } else {
            _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          }
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpRestTargetAt: Long?
          if (_stmt.isNull(_columnIndexOfRestTargetAt)) {
            _tmpRestTargetAt = null
          } else {
            _tmpRestTargetAt = _stmt.getLong(_columnIndexOfRestTargetAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = WorkoutSessionEntity(_tmpId,_tmpStatus,_tmpStartedAt,_tmpEndedAt,_tmpLocalDate,_tmpRating,_tmpNote,_tmpRestTargetAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countInProgressSessions(): Int {
    val _sql: String = "SELECT COUNT(*) FROM workout_session WHERE status = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeFinishedSessionCountSince(sinceMillis: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM workout_session WHERE status = 1 AND started_at >= ?"
    return createFlow(__db, false, arrayOf("workout_session")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceMillis)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCompletedWorkSetCountSince(sinceMillis: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM workout_set ws JOIN session_exercise se ON ws.session_exercise_id = se.id JOIN workout_session s ON se.session_id = s.id WHERE s.status = 1 AND s.started_at >= ? AND ws.is_completed = 1 AND ws.set_type != 1"
    return createFlow(__db, false, arrayOf("workout_set", "session_exercise", "workout_session")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceMillis)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEntriesFor(sessionId: String): List<SessionExerciseEntity> {
    val _sql: String = "SELECT * FROM session_exercise WHERE session_id = ? ORDER BY order_index"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<SessionExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SessionExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SessionExerciseEntity(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpOrderIndex,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEntriesForSessions(sessionIds: List<String>): List<SessionExerciseEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM session_exercise WHERE session_id IN (")
    val _inputSize: Int = sessionIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ORDER BY session_id, order_index")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in sessionIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<SessionExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: SessionExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item_1 = SessionExerciseEntity(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpOrderIndex,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEntry(id: String): SessionExerciseEntity? {
    val _sql: String = "SELECT * FROM session_exercise WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: SessionExerciseEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpNote: String?
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SessionExerciseEntity(_tmpId,_tmpSessionId,_tmpExerciseId,_tmpOrderIndex,_tmpNote,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSetsFor(entryId: String): List<WorkoutSetEntity> {
    val _sql: String = "SELECT * FROM workout_set WHERE session_exercise_id = ? ORDER BY order_index"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, entryId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionExerciseId: Int = getColumnIndexOrThrow(_stmt, "session_exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfSetType: Int = getColumnIndexOrThrow(_stmt, "set_type")
        val _columnIndexOfParentSetId: Int = getColumnIndexOrThrow(_stmt, "parent_set_id")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfInputUnit: Int = getColumnIndexOrThrow(_stmt, "input_unit")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfRir: Int = getColumnIndexOrThrow(_stmt, "rir")
        val _columnIndexOfDurationS: Int = getColumnIndexOrThrow(_stmt, "duration_s")
        val _columnIndexOfDistanceM: Int = getColumnIndexOrThrow(_stmt, "distance_m")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfInclineX10: Int = getColumnIndexOrThrow(_stmt, "incline_x10")
        val _columnIndexOfSpeedX10: Int = getColumnIndexOrThrow(_stmt, "speed_x10")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completed_at")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "is_completed")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<WorkoutSetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutSetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionExerciseId: String
          _tmpSessionExerciseId = _stmt.getText(_columnIndexOfSessionExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpSetType: Int
          _tmpSetType = _stmt.getLong(_columnIndexOfSetType).toInt()
          val _tmpParentSetId: String?
          if (_stmt.isNull(_columnIndexOfParentSetId)) {
            _tmpParentSetId = null
          } else {
            _tmpParentSetId = _stmt.getText(_columnIndexOfParentSetId)
          }
          val _tmpWeightG: Int?
          if (_stmt.isNull(_columnIndexOfWeightG)) {
            _tmpWeightG = null
          } else {
            _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          }
          val _tmpInputUnit: Int
          _tmpInputUnit = _stmt.getLong(_columnIndexOfInputUnit).toInt()
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpRir: Int?
          if (_stmt.isNull(_columnIndexOfRir)) {
            _tmpRir = null
          } else {
            _tmpRir = _stmt.getLong(_columnIndexOfRir).toInt()
          }
          val _tmpDurationS: Int?
          if (_stmt.isNull(_columnIndexOfDurationS)) {
            _tmpDurationS = null
          } else {
            _tmpDurationS = _stmt.getLong(_columnIndexOfDurationS).toInt()
          }
          val _tmpDistanceM: Int?
          if (_stmt.isNull(_columnIndexOfDistanceM)) {
            _tmpDistanceM = null
          } else {
            _tmpDistanceM = _stmt.getLong(_columnIndexOfDistanceM).toInt()
          }
          val _tmpLevel: Int?
          if (_stmt.isNull(_columnIndexOfLevel)) {
            _tmpLevel = null
          } else {
            _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          }
          val _tmpInclineX10: Int?
          if (_stmt.isNull(_columnIndexOfInclineX10)) {
            _tmpInclineX10 = null
          } else {
            _tmpInclineX10 = _stmt.getLong(_columnIndexOfInclineX10).toInt()
          }
          val _tmpSpeedX10: Int?
          if (_stmt.isNull(_columnIndexOfSpeedX10)) {
            _tmpSpeedX10 = null
          } else {
            _tmpSpeedX10 = _stmt.getLong(_columnIndexOfSpeedX10).toInt()
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpIsCompleted: Int
          _tmpIsCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = WorkoutSetEntity(_tmpId,_tmpSessionExerciseId,_tmpOrderIndex,_tmpSetType,_tmpParentSetId,_tmpWeightG,_tmpInputUnit,_tmpReps,_tmpRir,_tmpDurationS,_tmpDistanceM,_tmpLevel,_tmpInclineX10,_tmpSpeedX10,_tmpCompletedAt,_tmpIsCompleted,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSetsForEntries(entryIds: List<String>): List<WorkoutSetEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM workout_set WHERE session_exercise_id IN (")
    val _inputSize: Int = entryIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ORDER BY session_exercise_id, order_index")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in entryIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionExerciseId: Int = getColumnIndexOrThrow(_stmt, "session_exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfSetType: Int = getColumnIndexOrThrow(_stmt, "set_type")
        val _columnIndexOfParentSetId: Int = getColumnIndexOrThrow(_stmt, "parent_set_id")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfInputUnit: Int = getColumnIndexOrThrow(_stmt, "input_unit")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfRir: Int = getColumnIndexOrThrow(_stmt, "rir")
        val _columnIndexOfDurationS: Int = getColumnIndexOrThrow(_stmt, "duration_s")
        val _columnIndexOfDistanceM: Int = getColumnIndexOrThrow(_stmt, "distance_m")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfInclineX10: Int = getColumnIndexOrThrow(_stmt, "incline_x10")
        val _columnIndexOfSpeedX10: Int = getColumnIndexOrThrow(_stmt, "speed_x10")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completed_at")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "is_completed")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<WorkoutSetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: WorkoutSetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionExerciseId: String
          _tmpSessionExerciseId = _stmt.getText(_columnIndexOfSessionExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpSetType: Int
          _tmpSetType = _stmt.getLong(_columnIndexOfSetType).toInt()
          val _tmpParentSetId: String?
          if (_stmt.isNull(_columnIndexOfParentSetId)) {
            _tmpParentSetId = null
          } else {
            _tmpParentSetId = _stmt.getText(_columnIndexOfParentSetId)
          }
          val _tmpWeightG: Int?
          if (_stmt.isNull(_columnIndexOfWeightG)) {
            _tmpWeightG = null
          } else {
            _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          }
          val _tmpInputUnit: Int
          _tmpInputUnit = _stmt.getLong(_columnIndexOfInputUnit).toInt()
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpRir: Int?
          if (_stmt.isNull(_columnIndexOfRir)) {
            _tmpRir = null
          } else {
            _tmpRir = _stmt.getLong(_columnIndexOfRir).toInt()
          }
          val _tmpDurationS: Int?
          if (_stmt.isNull(_columnIndexOfDurationS)) {
            _tmpDurationS = null
          } else {
            _tmpDurationS = _stmt.getLong(_columnIndexOfDurationS).toInt()
          }
          val _tmpDistanceM: Int?
          if (_stmt.isNull(_columnIndexOfDistanceM)) {
            _tmpDistanceM = null
          } else {
            _tmpDistanceM = _stmt.getLong(_columnIndexOfDistanceM).toInt()
          }
          val _tmpLevel: Int?
          if (_stmt.isNull(_columnIndexOfLevel)) {
            _tmpLevel = null
          } else {
            _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          }
          val _tmpInclineX10: Int?
          if (_stmt.isNull(_columnIndexOfInclineX10)) {
            _tmpInclineX10 = null
          } else {
            _tmpInclineX10 = _stmt.getLong(_columnIndexOfInclineX10).toInt()
          }
          val _tmpSpeedX10: Int?
          if (_stmt.isNull(_columnIndexOfSpeedX10)) {
            _tmpSpeedX10 = null
          } else {
            _tmpSpeedX10 = _stmt.getLong(_columnIndexOfSpeedX10).toInt()
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpIsCompleted: Int
          _tmpIsCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item_1 = WorkoutSetEntity(_tmpId,_tmpSessionExerciseId,_tmpOrderIndex,_tmpSetType,_tmpParentSetId,_tmpWeightG,_tmpInputUnit,_tmpReps,_tmpRir,_tmpDurationS,_tmpDistanceM,_tmpLevel,_tmpInclineX10,_tmpSpeedX10,_tmpCompletedAt,_tmpIsCompleted,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFatigueSetsSince(sinceMillis: Long): List<FatigueSetRow> {
    val _sql: String = "SELECT ws.*, s.started_at AS session_started_at, se.exercise_id AS exercise_id, e.kind AS exercise_kind FROM workout_set ws JOIN session_exercise se ON ws.session_exercise_id = se.id JOIN workout_session s ON se.session_id = s.id JOIN exercise e ON se.exercise_id = e.id WHERE ws.is_completed = 1 AND COALESCE(ws.completed_at, s.started_at) >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceMillis)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionExerciseId: Int = getColumnIndexOrThrow(_stmt, "session_exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfSetType: Int = getColumnIndexOrThrow(_stmt, "set_type")
        val _columnIndexOfParentSetId: Int = getColumnIndexOrThrow(_stmt, "parent_set_id")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfInputUnit: Int = getColumnIndexOrThrow(_stmt, "input_unit")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfRir: Int = getColumnIndexOrThrow(_stmt, "rir")
        val _columnIndexOfDurationS: Int = getColumnIndexOrThrow(_stmt, "duration_s")
        val _columnIndexOfDistanceM: Int = getColumnIndexOrThrow(_stmt, "distance_m")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfInclineX10: Int = getColumnIndexOrThrow(_stmt, "incline_x10")
        val _columnIndexOfSpeedX10: Int = getColumnIndexOrThrow(_stmt, "speed_x10")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completed_at")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "is_completed")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfSessionStartedAt: Int = getColumnIndexOrThrow(_stmt, "session_started_at")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfExerciseKind: Int = getColumnIndexOrThrow(_stmt, "exercise_kind")
        val _result: MutableList<FatigueSetRow> = mutableListOf()
        while (_stmt.step()) {
          val _item: FatigueSetRow
          val _tmpSessionStartedAt: Long
          _tmpSessionStartedAt = _stmt.getLong(_columnIndexOfSessionStartedAt)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpExerciseKind: Int
          _tmpExerciseKind = _stmt.getLong(_columnIndexOfExerciseKind).toInt()
          val _tmpSet: WorkoutSetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionExerciseId: String
          _tmpSessionExerciseId = _stmt.getText(_columnIndexOfSessionExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpSetType: Int
          _tmpSetType = _stmt.getLong(_columnIndexOfSetType).toInt()
          val _tmpParentSetId: String?
          if (_stmt.isNull(_columnIndexOfParentSetId)) {
            _tmpParentSetId = null
          } else {
            _tmpParentSetId = _stmt.getText(_columnIndexOfParentSetId)
          }
          val _tmpWeightG: Int?
          if (_stmt.isNull(_columnIndexOfWeightG)) {
            _tmpWeightG = null
          } else {
            _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          }
          val _tmpInputUnit: Int
          _tmpInputUnit = _stmt.getLong(_columnIndexOfInputUnit).toInt()
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpRir: Int?
          if (_stmt.isNull(_columnIndexOfRir)) {
            _tmpRir = null
          } else {
            _tmpRir = _stmt.getLong(_columnIndexOfRir).toInt()
          }
          val _tmpDurationS: Int?
          if (_stmt.isNull(_columnIndexOfDurationS)) {
            _tmpDurationS = null
          } else {
            _tmpDurationS = _stmt.getLong(_columnIndexOfDurationS).toInt()
          }
          val _tmpDistanceM: Int?
          if (_stmt.isNull(_columnIndexOfDistanceM)) {
            _tmpDistanceM = null
          } else {
            _tmpDistanceM = _stmt.getLong(_columnIndexOfDistanceM).toInt()
          }
          val _tmpLevel: Int?
          if (_stmt.isNull(_columnIndexOfLevel)) {
            _tmpLevel = null
          } else {
            _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          }
          val _tmpInclineX10: Int?
          if (_stmt.isNull(_columnIndexOfInclineX10)) {
            _tmpInclineX10 = null
          } else {
            _tmpInclineX10 = _stmt.getLong(_columnIndexOfInclineX10).toInt()
          }
          val _tmpSpeedX10: Int?
          if (_stmt.isNull(_columnIndexOfSpeedX10)) {
            _tmpSpeedX10 = null
          } else {
            _tmpSpeedX10 = _stmt.getLong(_columnIndexOfSpeedX10).toInt()
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpIsCompleted: Int
          _tmpIsCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _tmpSet = WorkoutSetEntity(_tmpId,_tmpSessionExerciseId,_tmpOrderIndex,_tmpSetType,_tmpParentSetId,_tmpWeightG,_tmpInputUnit,_tmpReps,_tmpRir,_tmpDurationS,_tmpDistanceM,_tmpLevel,_tmpInclineX10,_tmpSpeedX10,_tmpCompletedAt,_tmpIsCompleted,_tmpCreatedAt,_tmpUpdatedAt)
          _item = FatigueSetRow(_tmpSet,_tmpSessionStartedAt,_tmpExerciseId,_tmpExerciseKind)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getExerciseHistorySets(exerciseId: String): List<ExerciseSetWithDate> {
    val _sql: String = "SELECT ws.*, s.local_date AS local_date, s.started_at AS started_at, s.id AS session_id FROM workout_set ws JOIN session_exercise se ON ws.session_exercise_id = se.id JOIN workout_session s ON se.session_id = s.id WHERE se.exercise_id = ? AND s.status = 1 ORDER BY s.started_at ASC, ws.order_index ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, exerciseId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionExerciseId: Int = getColumnIndexOrThrow(_stmt, "session_exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfSetType: Int = getColumnIndexOrThrow(_stmt, "set_type")
        val _columnIndexOfParentSetId: Int = getColumnIndexOrThrow(_stmt, "parent_set_id")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfInputUnit: Int = getColumnIndexOrThrow(_stmt, "input_unit")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfRir: Int = getColumnIndexOrThrow(_stmt, "rir")
        val _columnIndexOfDurationS: Int = getColumnIndexOrThrow(_stmt, "duration_s")
        val _columnIndexOfDistanceM: Int = getColumnIndexOrThrow(_stmt, "distance_m")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfInclineX10: Int = getColumnIndexOrThrow(_stmt, "incline_x10")
        val _columnIndexOfSpeedX10: Int = getColumnIndexOrThrow(_stmt, "speed_x10")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completed_at")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "is_completed")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "started_at")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "session_id")
        val _result: MutableList<ExerciseSetWithDate> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseSetWithDate
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpSet: WorkoutSetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionExerciseId: String
          _tmpSessionExerciseId = _stmt.getText(_columnIndexOfSessionExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpSetType: Int
          _tmpSetType = _stmt.getLong(_columnIndexOfSetType).toInt()
          val _tmpParentSetId: String?
          if (_stmt.isNull(_columnIndexOfParentSetId)) {
            _tmpParentSetId = null
          } else {
            _tmpParentSetId = _stmt.getText(_columnIndexOfParentSetId)
          }
          val _tmpWeightG: Int?
          if (_stmt.isNull(_columnIndexOfWeightG)) {
            _tmpWeightG = null
          } else {
            _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          }
          val _tmpInputUnit: Int
          _tmpInputUnit = _stmt.getLong(_columnIndexOfInputUnit).toInt()
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpRir: Int?
          if (_stmt.isNull(_columnIndexOfRir)) {
            _tmpRir = null
          } else {
            _tmpRir = _stmt.getLong(_columnIndexOfRir).toInt()
          }
          val _tmpDurationS: Int?
          if (_stmt.isNull(_columnIndexOfDurationS)) {
            _tmpDurationS = null
          } else {
            _tmpDurationS = _stmt.getLong(_columnIndexOfDurationS).toInt()
          }
          val _tmpDistanceM: Int?
          if (_stmt.isNull(_columnIndexOfDistanceM)) {
            _tmpDistanceM = null
          } else {
            _tmpDistanceM = _stmt.getLong(_columnIndexOfDistanceM).toInt()
          }
          val _tmpLevel: Int?
          if (_stmt.isNull(_columnIndexOfLevel)) {
            _tmpLevel = null
          } else {
            _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          }
          val _tmpInclineX10: Int?
          if (_stmt.isNull(_columnIndexOfInclineX10)) {
            _tmpInclineX10 = null
          } else {
            _tmpInclineX10 = _stmt.getLong(_columnIndexOfInclineX10).toInt()
          }
          val _tmpSpeedX10: Int?
          if (_stmt.isNull(_columnIndexOfSpeedX10)) {
            _tmpSpeedX10 = null
          } else {
            _tmpSpeedX10 = _stmt.getLong(_columnIndexOfSpeedX10).toInt()
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpIsCompleted: Int
          _tmpIsCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _tmpSet = WorkoutSetEntity(_tmpId,_tmpSessionExerciseId,_tmpOrderIndex,_tmpSetType,_tmpParentSetId,_tmpWeightG,_tmpInputUnit,_tmpReps,_tmpRir,_tmpDurationS,_tmpDistanceM,_tmpLevel,_tmpInclineX10,_tmpSpeedX10,_tmpCompletedAt,_tmpIsCompleted,_tmpCreatedAt,_tmpUpdatedAt)
          _item = ExerciseSetWithDate(_tmpSet,_tmpLocalDate,_tmpStartedAt,_tmpSessionId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLastSetsForExercise(exerciseId: String): List<WorkoutSetEntity> {
    val _sql: String = "SELECT ws.* FROM workout_set ws JOIN session_exercise se ON ws.session_exercise_id = se.id WHERE se.id = (  SELECT se2.id FROM session_exercise se2   JOIN workout_session s ON se2.session_id = s.id   WHERE se2.exercise_id = ? AND s.status = 1     AND EXISTS (SELECT 1 FROM workout_set x WHERE x.session_exercise_id = se2.id AND x.is_completed = 1)   ORDER BY s.started_at DESC, se2.order_index DESC   LIMIT 1) AND ws.is_completed = 1 ORDER BY ws.order_index ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, exerciseId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionExerciseId: Int = getColumnIndexOrThrow(_stmt, "session_exercise_id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "order_index")
        val _columnIndexOfSetType: Int = getColumnIndexOrThrow(_stmt, "set_type")
        val _columnIndexOfParentSetId: Int = getColumnIndexOrThrow(_stmt, "parent_set_id")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfInputUnit: Int = getColumnIndexOrThrow(_stmt, "input_unit")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfRir: Int = getColumnIndexOrThrow(_stmt, "rir")
        val _columnIndexOfDurationS: Int = getColumnIndexOrThrow(_stmt, "duration_s")
        val _columnIndexOfDistanceM: Int = getColumnIndexOrThrow(_stmt, "distance_m")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfInclineX10: Int = getColumnIndexOrThrow(_stmt, "incline_x10")
        val _columnIndexOfSpeedX10: Int = getColumnIndexOrThrow(_stmt, "speed_x10")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completed_at")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "is_completed")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<WorkoutSetEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutSetEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionExerciseId: String
          _tmpSessionExerciseId = _stmt.getText(_columnIndexOfSessionExerciseId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpSetType: Int
          _tmpSetType = _stmt.getLong(_columnIndexOfSetType).toInt()
          val _tmpParentSetId: String?
          if (_stmt.isNull(_columnIndexOfParentSetId)) {
            _tmpParentSetId = null
          } else {
            _tmpParentSetId = _stmt.getText(_columnIndexOfParentSetId)
          }
          val _tmpWeightG: Int?
          if (_stmt.isNull(_columnIndexOfWeightG)) {
            _tmpWeightG = null
          } else {
            _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          }
          val _tmpInputUnit: Int
          _tmpInputUnit = _stmt.getLong(_columnIndexOfInputUnit).toInt()
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpRir: Int?
          if (_stmt.isNull(_columnIndexOfRir)) {
            _tmpRir = null
          } else {
            _tmpRir = _stmt.getLong(_columnIndexOfRir).toInt()
          }
          val _tmpDurationS: Int?
          if (_stmt.isNull(_columnIndexOfDurationS)) {
            _tmpDurationS = null
          } else {
            _tmpDurationS = _stmt.getLong(_columnIndexOfDurationS).toInt()
          }
          val _tmpDistanceM: Int?
          if (_stmt.isNull(_columnIndexOfDistanceM)) {
            _tmpDistanceM = null
          } else {
            _tmpDistanceM = _stmt.getLong(_columnIndexOfDistanceM).toInt()
          }
          val _tmpLevel: Int?
          if (_stmt.isNull(_columnIndexOfLevel)) {
            _tmpLevel = null
          } else {
            _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          }
          val _tmpInclineX10: Int?
          if (_stmt.isNull(_columnIndexOfInclineX10)) {
            _tmpInclineX10 = null
          } else {
            _tmpInclineX10 = _stmt.getLong(_columnIndexOfInclineX10).toInt()
          }
          val _tmpSpeedX10: Int?
          if (_stmt.isNull(_columnIndexOfSpeedX10)) {
            _tmpSpeedX10 = null
          } else {
            _tmpSpeedX10 = _stmt.getLong(_columnIndexOfSpeedX10).toInt()
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpIsCompleted: Int
          _tmpIsCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = WorkoutSetEntity(_tmpId,_tmpSessionExerciseId,_tmpOrderIndex,_tmpSetType,_tmpParentSetId,_tmpWeightG,_tmpInputUnit,_tmpReps,_tmpRir,_tmpDurationS,_tmpDistanceM,_tmpLevel,_tmpInclineX10,_tmpSpeedX10,_tmpCompletedAt,_tmpIsCompleted,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSession(id: String) {
    val _sql: String = "DELETE FROM workout_session WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSessionExercise(id: String) {
    val _sql: String = "DELETE FROM session_exercise WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSet(id: String) {
    val _sql: String = "DELETE FROM workout_set WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
