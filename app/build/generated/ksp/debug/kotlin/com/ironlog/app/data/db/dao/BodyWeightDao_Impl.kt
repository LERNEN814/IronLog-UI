package com.ironlog.app.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.ironlog.app.`data`.db.entity.BodyWeightEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BodyWeightDao_Impl(
  __db: RoomDatabase,
) : BodyWeightDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBodyWeightEntity: EntityInsertAdapter<BodyWeightEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBodyWeightEntity = object : EntityInsertAdapter<BodyWeightEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `body_weight` (`id`,`local_date`,`weight_g`,`recorded_at`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BodyWeightEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.localDate)
        statement.bindLong(3, entity.weightG.toLong())
        statement.bindLong(4, entity.recordedAt)
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(entry: BodyWeightEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBodyWeightEntity.insert(_connection, entry)
  }

  public override suspend fun getByDate(localDate: String): BodyWeightEntity? {
    val _sql: String = "SELECT * FROM body_weight WHERE local_date = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, localDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recorded_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: BodyWeightEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpWeightG: Int
          _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = BodyWeightEntity(_tmpId,_tmpLocalDate,_tmpWeightG,_tmpRecordedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecent(limit: Int): List<BodyWeightEntity> {
    val _sql: String = "SELECT * FROM body_weight ORDER BY local_date DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLocalDate: Int = getColumnIndexOrThrow(_stmt, "local_date")
        val _columnIndexOfWeightG: Int = getColumnIndexOrThrow(_stmt, "weight_g")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recorded_at")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _result: MutableList<BodyWeightEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BodyWeightEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLocalDate: String
          _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate)
          val _tmpWeightG: Int
          _tmpWeightG = _stmt.getLong(_columnIndexOfWeightG).toInt()
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = BodyWeightEntity(_tmpId,_tmpLocalDate,_tmpWeightG,_tmpRecordedAt,_tmpCreatedAt,_tmpUpdatedAt)
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
