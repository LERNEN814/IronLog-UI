package com.ironlog.app.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.ironlog.app.`data`.db.entity.MuscleGroupEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MuscleGroupDao_Impl(
  __db: RoomDatabase,
) : MuscleGroupDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMuscleGroupEntity: EntityInsertAdapter<MuscleGroupEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMuscleGroupEntity = object : EntityInsertAdapter<MuscleGroupEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `muscle_group` (`id`,`display_name_en`,`display_name_zh`,`body_region`,`recovery_half_life_hours`,`sort_order`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MuscleGroupEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.displayNameEn)
        statement.bindText(3, entity.displayNameZh)
        statement.bindLong(4, entity.bodyRegion.toLong())
        statement.bindLong(5, entity.recoveryHalfLifeHours.toLong())
        statement.bindLong(6, entity.sortOrder.toLong())
      }
    }
  }

  public override suspend fun insertAll(groups: List<MuscleGroupEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMuscleGroupEntity.insert(_connection, groups)
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM muscle_group"
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

  public override suspend fun getById(id: String): MuscleGroupEntity? {
    val _sql: String = "SELECT * FROM muscle_group WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayNameEn: Int = getColumnIndexOrThrow(_stmt, "display_name_en")
        val _columnIndexOfDisplayNameZh: Int = getColumnIndexOrThrow(_stmt, "display_name_zh")
        val _columnIndexOfBodyRegion: Int = getColumnIndexOrThrow(_stmt, "body_region")
        val _columnIndexOfRecoveryHalfLifeHours: Int = getColumnIndexOrThrow(_stmt, "recovery_half_life_hours")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sort_order")
        val _result: MuscleGroupEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDisplayNameEn: String
          _tmpDisplayNameEn = _stmt.getText(_columnIndexOfDisplayNameEn)
          val _tmpDisplayNameZh: String
          _tmpDisplayNameZh = _stmt.getText(_columnIndexOfDisplayNameZh)
          val _tmpBodyRegion: Int
          _tmpBodyRegion = _stmt.getLong(_columnIndexOfBodyRegion).toInt()
          val _tmpRecoveryHalfLifeHours: Int
          _tmpRecoveryHalfLifeHours = _stmt.getLong(_columnIndexOfRecoveryHalfLifeHours).toInt()
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _result = MuscleGroupEntity(_tmpId,_tmpDisplayNameEn,_tmpDisplayNameZh,_tmpBodyRegion,_tmpRecoveryHalfLifeHours,_tmpSortOrder)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<MuscleGroupEntity> {
    val _sql: String = "SELECT * FROM muscle_group ORDER BY sort_order"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayNameEn: Int = getColumnIndexOrThrow(_stmt, "display_name_en")
        val _columnIndexOfDisplayNameZh: Int = getColumnIndexOrThrow(_stmt, "display_name_zh")
        val _columnIndexOfBodyRegion: Int = getColumnIndexOrThrow(_stmt, "body_region")
        val _columnIndexOfRecoveryHalfLifeHours: Int = getColumnIndexOrThrow(_stmt, "recovery_half_life_hours")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sort_order")
        val _result: MutableList<MuscleGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MuscleGroupEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDisplayNameEn: String
          _tmpDisplayNameEn = _stmt.getText(_columnIndexOfDisplayNameEn)
          val _tmpDisplayNameZh: String
          _tmpDisplayNameZh = _stmt.getText(_columnIndexOfDisplayNameZh)
          val _tmpBodyRegion: Int
          _tmpBodyRegion = _stmt.getLong(_columnIndexOfBodyRegion).toInt()
          val _tmpRecoveryHalfLifeHours: Int
          _tmpRecoveryHalfLifeHours = _stmt.getLong(_columnIndexOfRecoveryHalfLifeHours).toInt()
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item = MuscleGroupEntity(_tmpId,_tmpDisplayNameEn,_tmpDisplayNameZh,_tmpBodyRegion,_tmpRecoveryHalfLifeHours,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAll(): Flow<List<MuscleGroupEntity>> {
    val _sql: String = "SELECT * FROM muscle_group ORDER BY sort_order"
    return createFlow(__db, false, arrayOf("muscle_group")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayNameEn: Int = getColumnIndexOrThrow(_stmt, "display_name_en")
        val _columnIndexOfDisplayNameZh: Int = getColumnIndexOrThrow(_stmt, "display_name_zh")
        val _columnIndexOfBodyRegion: Int = getColumnIndexOrThrow(_stmt, "body_region")
        val _columnIndexOfRecoveryHalfLifeHours: Int = getColumnIndexOrThrow(_stmt, "recovery_half_life_hours")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sort_order")
        val _result: MutableList<MuscleGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MuscleGroupEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDisplayNameEn: String
          _tmpDisplayNameEn = _stmt.getText(_columnIndexOfDisplayNameEn)
          val _tmpDisplayNameZh: String
          _tmpDisplayNameZh = _stmt.getText(_columnIndexOfDisplayNameZh)
          val _tmpBodyRegion: Int
          _tmpBodyRegion = _stmt.getLong(_columnIndexOfBodyRegion).toInt()
          val _tmpRecoveryHalfLifeHours: Int
          _tmpRecoveryHalfLifeHours = _stmt.getLong(_columnIndexOfRecoveryHalfLifeHours).toInt()
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item = MuscleGroupEntity(_tmpId,_tmpDisplayNameEn,_tmpDisplayNameZh,_tmpBodyRegion,_tmpRecoveryHalfLifeHours,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
