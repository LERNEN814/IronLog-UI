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
import com.ironlog.app.`data`.db.entity.ExerciseEntity
import com.ironlog.app.`data`.db.entity.ExerciseMuscleEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
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
public class ExerciseDao_Impl(
  __db: RoomDatabase,
) : ExerciseDao() {
  private val __db: RoomDatabase

  private val __insertAdapterOfExerciseEntity: EntityInsertAdapter<ExerciseEntity>

  private val __insertAdapterOfExerciseMuscleEntity: EntityInsertAdapter<ExerciseMuscleEntity>

  private val __insertAdapterOfExerciseEntity_1: EntityInsertAdapter<ExerciseEntity>

  private val __updateAdapterOfExerciseEntity: EntityDeleteOrUpdateAdapter<ExerciseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfExerciseEntity = object : EntityInsertAdapter<ExerciseEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `exercise` (`id`,`name_zh`,`name_en`,`kind`,`equipment`,`primary_muscle_id`,`is_custom`,`is_archived`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExerciseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.nameZh)
        statement.bindText(3, entity.nameEn)
        statement.bindLong(4, entity.kind.toLong())
        statement.bindText(5, entity.equipment)
        val _tmpPrimaryMuscleId: String? = entity.primaryMuscleId
        if (_tmpPrimaryMuscleId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPrimaryMuscleId)
        }
        statement.bindLong(7, entity.isCustom.toLong())
        statement.bindLong(8, entity.isArchived.toLong())
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }
    this.__insertAdapterOfExerciseMuscleEntity = object : EntityInsertAdapter<ExerciseMuscleEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `exercise_muscle` (`exercise_id`,`muscle_id`,`role`,`weight`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExerciseMuscleEntity) {
        statement.bindText(1, entity.exerciseId)
        statement.bindText(2, entity.muscleId)
        statement.bindLong(3, entity.role.toLong())
        statement.bindDouble(4, entity.weight)
      }
    }
    this.__insertAdapterOfExerciseEntity_1 = object : EntityInsertAdapter<ExerciseEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `exercise` (`id`,`name_zh`,`name_en`,`kind`,`equipment`,`primary_muscle_id`,`is_custom`,`is_archived`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExerciseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.nameZh)
        statement.bindText(3, entity.nameEn)
        statement.bindLong(4, entity.kind.toLong())
        statement.bindText(5, entity.equipment)
        val _tmpPrimaryMuscleId: String? = entity.primaryMuscleId
        if (_tmpPrimaryMuscleId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPrimaryMuscleId)
        }
        statement.bindLong(7, entity.isCustom.toLong())
        statement.bindLong(8, entity.isArchived.toLong())
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }
    this.__updateAdapterOfExerciseEntity = object : EntityDeleteOrUpdateAdapter<ExerciseEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `exercise` SET `id` = ?,`name_zh` = ?,`name_en` = ?,`kind` = ?,`equipment` = ?,`primary_muscle_id` = ?,`is_custom` = ?,`is_archived` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ExerciseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.nameZh)
        statement.bindText(3, entity.nameEn)
        statement.bindLong(4, entity.kind.toLong())
        statement.bindText(5, entity.equipment)
        val _tmpPrimaryMuscleId: String? = entity.primaryMuscleId
        if (_tmpPrimaryMuscleId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPrimaryMuscleId)
        }
        statement.bindLong(7, entity.isCustom.toLong())
        statement.bindLong(8, entity.isArchived.toLong())
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindText(11, entity.id)
      }
    }
  }

  public override suspend fun insertAll(exercises: List<ExerciseEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfExerciseEntity.insert(_connection, exercises)
  }

  public override suspend fun insertMuscles(muscles: List<ExerciseMuscleEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfExerciseMuscleEntity.insert(_connection, muscles)
  }

  public override suspend fun insertExercise(exercise: ExerciseEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfExerciseEntity_1.insert(_connection, exercise)
  }

  public override suspend fun updateExercise(exercise: ExerciseEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfExerciseEntity.handle(_connection, exercise)
  }

  public override suspend fun upsertCustomExercise(exercise: ExerciseEntity, muscles: List<ExerciseMuscleEntity>): Unit = performInTransactionSuspending(__db) {
    super@ExerciseDao_Impl.upsertCustomExercise(exercise, muscles)
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM exercise"
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

  public override suspend fun countBuiltIn(): Int {
    val _sql: String = "SELECT COUNT(*) FROM exercise WHERE is_custom = 0"
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

  public override suspend fun getById(id: String): ExerciseEntity? {
    val _sql: String = "SELECT * FROM exercise WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNameZh: Int = getColumnIndexOrThrow(_stmt, "name_zh")
        val _columnIndexOfNameEn: Int = getColumnIndexOrThrow(_stmt, "name_en")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfEquipment: Int = getColumnIndexOrThrow(_stmt, "equipment")
        val _columnIndexOfPrimaryMuscleId: Int = getColumnIndexOrThrow(_stmt, "primary_muscle_id")
        val _columnIndexOfIsCustom: Int = getColumnIndexOrThrow(_stmt, "is_custom")
        val _columnIndexOfIsArchived: Int = getColumnIndexOrThrow(_stmt, "is_archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: ExerciseEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNameZh: String
          _tmpNameZh = _stmt.getText(_columnIndexOfNameZh)
          val _tmpNameEn: String
          _tmpNameEn = _stmt.getText(_columnIndexOfNameEn)
          val _tmpKind: Int
          _tmpKind = _stmt.getLong(_columnIndexOfKind).toInt()
          val _tmpEquipment: String
          _tmpEquipment = _stmt.getText(_columnIndexOfEquipment)
          val _tmpPrimaryMuscleId: String?
          if (_stmt.isNull(_columnIndexOfPrimaryMuscleId)) {
            _tmpPrimaryMuscleId = null
          } else {
            _tmpPrimaryMuscleId = _stmt.getText(_columnIndexOfPrimaryMuscleId)
          }
          val _tmpIsCustom: Int
          _tmpIsCustom = _stmt.getLong(_columnIndexOfIsCustom).toInt()
          val _tmpIsArchived: Int
          _tmpIsArchived = _stmt.getLong(_columnIndexOfIsArchived).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = ExerciseEntity(_tmpId,_tmpNameZh,_tmpNameEn,_tmpKind,_tmpEquipment,_tmpPrimaryMuscleId,_tmpIsCustom,_tmpIsArchived,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByIds(ids: List<String>): List<ExerciseEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM exercise WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNameZh: Int = getColumnIndexOrThrow(_stmt, "name_zh")
        val _columnIndexOfNameEn: Int = getColumnIndexOrThrow(_stmt, "name_en")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfEquipment: Int = getColumnIndexOrThrow(_stmt, "equipment")
        val _columnIndexOfPrimaryMuscleId: Int = getColumnIndexOrThrow(_stmt, "primary_muscle_id")
        val _columnIndexOfIsCustom: Int = getColumnIndexOrThrow(_stmt, "is_custom")
        val _columnIndexOfIsArchived: Int = getColumnIndexOrThrow(_stmt, "is_archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<ExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: ExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNameZh: String
          _tmpNameZh = _stmt.getText(_columnIndexOfNameZh)
          val _tmpNameEn: String
          _tmpNameEn = _stmt.getText(_columnIndexOfNameEn)
          val _tmpKind: Int
          _tmpKind = _stmt.getLong(_columnIndexOfKind).toInt()
          val _tmpEquipment: String
          _tmpEquipment = _stmt.getText(_columnIndexOfEquipment)
          val _tmpPrimaryMuscleId: String?
          if (_stmt.isNull(_columnIndexOfPrimaryMuscleId)) {
            _tmpPrimaryMuscleId = null
          } else {
            _tmpPrimaryMuscleId = _stmt.getText(_columnIndexOfPrimaryMuscleId)
          }
          val _tmpIsCustom: Int
          _tmpIsCustom = _stmt.getLong(_columnIndexOfIsCustom).toInt()
          val _tmpIsArchived: Int
          _tmpIsArchived = _stmt.getLong(_columnIndexOfIsArchived).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item_1 = ExerciseEntity(_tmpId,_tmpNameZh,_tmpNameEn,_tmpKind,_tmpEquipment,_tmpPrimaryMuscleId,_tmpIsCustom,_tmpIsArchived,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAll(): Flow<List<ExerciseEntity>> {
    val _sql: String = "SELECT * FROM exercise WHERE is_archived = 0 ORDER BY is_custom DESC, name_zh"
    return createFlow(__db, false, arrayOf("exercise")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNameZh: Int = getColumnIndexOrThrow(_stmt, "name_zh")
        val _columnIndexOfNameEn: Int = getColumnIndexOrThrow(_stmt, "name_en")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfEquipment: Int = getColumnIndexOrThrow(_stmt, "equipment")
        val _columnIndexOfPrimaryMuscleId: Int = getColumnIndexOrThrow(_stmt, "primary_muscle_id")
        val _columnIndexOfIsCustom: Int = getColumnIndexOrThrow(_stmt, "is_custom")
        val _columnIndexOfIsArchived: Int = getColumnIndexOrThrow(_stmt, "is_archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<ExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNameZh: String
          _tmpNameZh = _stmt.getText(_columnIndexOfNameZh)
          val _tmpNameEn: String
          _tmpNameEn = _stmt.getText(_columnIndexOfNameEn)
          val _tmpKind: Int
          _tmpKind = _stmt.getLong(_columnIndexOfKind).toInt()
          val _tmpEquipment: String
          _tmpEquipment = _stmt.getText(_columnIndexOfEquipment)
          val _tmpPrimaryMuscleId: String?
          if (_stmt.isNull(_columnIndexOfPrimaryMuscleId)) {
            _tmpPrimaryMuscleId = null
          } else {
            _tmpPrimaryMuscleId = _stmt.getText(_columnIndexOfPrimaryMuscleId)
          }
          val _tmpIsCustom: Int
          _tmpIsCustom = _stmt.getLong(_columnIndexOfIsCustom).toInt()
          val _tmpIsArchived: Int
          _tmpIsArchived = _stmt.getLong(_columnIndexOfIsArchived).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = ExerciseEntity(_tmpId,_tmpNameZh,_tmpNameEn,_tmpKind,_tmpEquipment,_tmpPrimaryMuscleId,_tmpIsCustom,_tmpIsArchived,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun search(query: String, bodyRegion: Int?): Flow<List<ExerciseEntity>> {
    val _sql: String = "SELECT e.* FROM exercise e LEFT JOIN muscle_group m ON e.primary_muscle_id = m.id WHERE e.is_archived = 0 AND (? IS NULL OR m.body_region = ?) AND (? = '' OR e.name_zh LIKE '%' || ? || '%' OR e.name_en LIKE '%' || ? || '%') ORDER BY e.is_custom DESC, e.name_zh"
    return createFlow(__db, false, arrayOf("exercise", "muscle_group")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (bodyRegion == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, bodyRegion.toLong())
        }
        _argIndex = 2
        if (bodyRegion == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, bodyRegion.toLong())
        }
        _argIndex = 3
        _stmt.bindText(_argIndex, query)
        _argIndex = 4
        _stmt.bindText(_argIndex, query)
        _argIndex = 5
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNameZh: Int = getColumnIndexOrThrow(_stmt, "name_zh")
        val _columnIndexOfNameEn: Int = getColumnIndexOrThrow(_stmt, "name_en")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfEquipment: Int = getColumnIndexOrThrow(_stmt, "equipment")
        val _columnIndexOfPrimaryMuscleId: Int = getColumnIndexOrThrow(_stmt, "primary_muscle_id")
        val _columnIndexOfIsCustom: Int = getColumnIndexOrThrow(_stmt, "is_custom")
        val _columnIndexOfIsArchived: Int = getColumnIndexOrThrow(_stmt, "is_archived")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<ExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNameZh: String
          _tmpNameZh = _stmt.getText(_columnIndexOfNameZh)
          val _tmpNameEn: String
          _tmpNameEn = _stmt.getText(_columnIndexOfNameEn)
          val _tmpKind: Int
          _tmpKind = _stmt.getLong(_columnIndexOfKind).toInt()
          val _tmpEquipment: String
          _tmpEquipment = _stmt.getText(_columnIndexOfEquipment)
          val _tmpPrimaryMuscleId: String?
          if (_stmt.isNull(_columnIndexOfPrimaryMuscleId)) {
            _tmpPrimaryMuscleId = null
          } else {
            _tmpPrimaryMuscleId = _stmt.getText(_columnIndexOfPrimaryMuscleId)
          }
          val _tmpIsCustom: Int
          _tmpIsCustom = _stmt.getLong(_columnIndexOfIsCustom).toInt()
          val _tmpIsArchived: Int
          _tmpIsArchived = _stmt.getLong(_columnIndexOfIsArchived).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = ExerciseEntity(_tmpId,_tmpNameZh,_tmpNameEn,_tmpKind,_tmpEquipment,_tmpPrimaryMuscleId,_tmpIsCustom,_tmpIsArchived,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLastUsed(): Flow<List<ExerciseLastUsed>> {
    val _sql: String = "SELECT se.exercise_id AS exercise_id, MAX(s.started_at) AS last_used_at FROM session_exercise se JOIN workout_session s ON se.session_id = s.id GROUP BY se.exercise_id"
    return createFlow(__db, false, arrayOf("session_exercise", "workout_session")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfExerciseId: Int = 0
        val _columnIndexOfLastUsedAt: Int = 1
        val _result: MutableList<ExerciseLastUsed> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseLastUsed
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpLastUsedAt: Long
          _tmpLastUsedAt = _stmt.getLong(_columnIndexOfLastUsedAt)
          _item = ExerciseLastUsed(_tmpExerciseId,_tmpLastUsedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMusclesFor(exerciseId: String): List<ExerciseMuscleEntity> {
    val _sql: String = "SELECT * FROM exercise_muscle WHERE exercise_id = ? ORDER BY role, weight DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, exerciseId)
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfMuscleId: Int = getColumnIndexOrThrow(_stmt, "muscle_id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _result: MutableList<ExerciseMuscleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseMuscleEntity
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpMuscleId: String
          _tmpMuscleId = _stmt.getText(_columnIndexOfMuscleId)
          val _tmpRole: Int
          _tmpRole = _stmt.getLong(_columnIndexOfRole).toInt()
          val _tmpWeight: Double
          _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          _item = ExerciseMuscleEntity(_tmpExerciseId,_tmpMuscleId,_tmpRole,_tmpWeight)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMusclesForExercises(exerciseIds: List<String>): List<ExerciseMuscleEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM exercise_muscle WHERE exercise_id IN (")
    val _inputSize: Int = exerciseIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ORDER BY exercise_id, role, weight DESC")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in exerciseIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exercise_id")
        val _columnIndexOfMuscleId: Int = getColumnIndexOrThrow(_stmt, "muscle_id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _result: MutableList<ExerciseMuscleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: ExerciseMuscleEntity
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpMuscleId: String
          _tmpMuscleId = _stmt.getText(_columnIndexOfMuscleId)
          val _tmpRole: Int
          _tmpRole = _stmt.getLong(_columnIndexOfRole).toInt()
          val _tmpWeight: Double
          _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          _item_1 = ExerciseMuscleEntity(_tmpExerciseId,_tmpMuscleId,_tmpRole,_tmpWeight)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countWithoutPrimaryMuscle(): Int {
    val _sql: String = "SELECT COUNT(*) FROM exercise e WHERE NOT EXISTS (SELECT 1 FROM exercise_muscle em WHERE em.exercise_id = e.id AND em.role = 0)"
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

  public override suspend fun archive(id: String, updatedAt: Long) {
    val _sql: String = "UPDATE exercise SET is_archived = 1, updated_at = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteMusclesFor(exerciseId: String) {
    val _sql: String = "DELETE FROM exercise_muscle WHERE exercise_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, exerciseId)
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
