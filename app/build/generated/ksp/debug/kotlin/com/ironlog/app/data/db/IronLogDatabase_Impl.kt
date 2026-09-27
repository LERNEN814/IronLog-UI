package com.ironlog.app.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.ironlog.app.`data`.db.dao.BodyWeightDao
import com.ironlog.app.`data`.db.dao.BodyWeightDao_Impl
import com.ironlog.app.`data`.db.dao.ExerciseDao
import com.ironlog.app.`data`.db.dao.ExerciseDao_Impl
import com.ironlog.app.`data`.db.dao.MuscleGroupDao
import com.ironlog.app.`data`.db.dao.MuscleGroupDao_Impl
import com.ironlog.app.`data`.db.dao.WorkoutDao
import com.ironlog.app.`data`.db.dao.WorkoutDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class IronLogDatabase_Impl : IronLogDatabase() {
  private val _muscleGroupDao: Lazy<MuscleGroupDao> = lazy {
    MuscleGroupDao_Impl(this)
  }

  private val _exerciseDao: Lazy<ExerciseDao> = lazy {
    ExerciseDao_Impl(this)
  }

  private val _workoutDao: Lazy<WorkoutDao> = lazy {
    WorkoutDao_Impl(this)
  }

  private val _bodyWeightDao: Lazy<BodyWeightDao> = lazy {
    BodyWeightDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "a743051ae98d4c0aa23481caacd6d821", "5c6c9104040c1ccff0a65a5527407813") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `muscle_group` (`id` TEXT NOT NULL, `display_name_en` TEXT NOT NULL, `display_name_zh` TEXT NOT NULL, `body_region` INTEGER NOT NULL, `recovery_half_life_hours` INTEGER NOT NULL, `sort_order` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `exercise` (`id` TEXT NOT NULL, `name_zh` TEXT NOT NULL, `name_en` TEXT NOT NULL, `kind` INTEGER NOT NULL DEFAULT 0, `equipment` TEXT NOT NULL, `primary_muscle_id` TEXT, `is_custom` INTEGER NOT NULL DEFAULT 0, `is_archived` INTEGER NOT NULL DEFAULT 0, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `exercise_muscle` (`exercise_id` TEXT NOT NULL, `muscle_id` TEXT NOT NULL, `role` INTEGER NOT NULL DEFAULT 0, `weight` REAL NOT NULL, PRIMARY KEY(`exercise_id`, `muscle_id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workout_session` (`id` TEXT NOT NULL, `status` INTEGER NOT NULL DEFAULT 0, `started_at` INTEGER NOT NULL, `ended_at` INTEGER, `local_date` TEXT NOT NULL, `rating` INTEGER, `note` TEXT, `rest_target_at` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `session_exercise` (`id` TEXT NOT NULL, `session_id` TEXT NOT NULL, `exercise_id` TEXT NOT NULL, `order_index` INTEGER NOT NULL, `note` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`session_id`) REFERENCES `workout_session`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exercise_id`) REFERENCES `exercise`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercise_session_id` ON `session_exercise` (`session_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercise_exercise_id` ON `session_exercise` (`exercise_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workout_set` (`id` TEXT NOT NULL, `session_exercise_id` TEXT NOT NULL, `order_index` INTEGER NOT NULL, `set_type` INTEGER NOT NULL DEFAULT 0, `parent_set_id` TEXT, `weight_g` INTEGER, `input_unit` INTEGER NOT NULL DEFAULT 0, `reps` INTEGER, `rir` INTEGER, `duration_s` INTEGER, `distance_m` INTEGER, `level` INTEGER, `incline_x10` INTEGER, `speed_x10` INTEGER, `completed_at` INTEGER, `is_completed` INTEGER NOT NULL DEFAULT 0, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`session_exercise_id`) REFERENCES `session_exercise`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_set_session_exercise_id` ON `workout_set` (`session_exercise_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `body_weight` (`id` TEXT NOT NULL, `local_date` TEXT NOT NULL, `weight_g` INTEGER NOT NULL, `recorded_at` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_body_weight_local_date` ON `body_weight` (`local_date`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a743051ae98d4c0aa23481caacd6d821')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `muscle_group`")
        connection.execSQL("DROP TABLE IF EXISTS `exercise`")
        connection.execSQL("DROP TABLE IF EXISTS `exercise_muscle`")
        connection.execSQL("DROP TABLE IF EXISTS `workout_session`")
        connection.execSQL("DROP TABLE IF EXISTS `session_exercise`")
        connection.execSQL("DROP TABLE IF EXISTS `workout_set`")
        connection.execSQL("DROP TABLE IF EXISTS `body_weight`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsMuscleGroup: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMuscleGroup.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroup.put("display_name_en", TableInfo.Column("display_name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroup.put("display_name_zh", TableInfo.Column("display_name_zh", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroup.put("body_region", TableInfo.Column("body_region", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroup.put("recovery_half_life_hours", TableInfo.Column("recovery_half_life_hours", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroup.put("sort_order", TableInfo.Column("sort_order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMuscleGroup: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMuscleGroup: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMuscleGroup: TableInfo = TableInfo("muscle_group", _columnsMuscleGroup, _foreignKeysMuscleGroup, _indicesMuscleGroup)
        val _existingMuscleGroup: TableInfo = read(connection, "muscle_group")
        if (!_infoMuscleGroup.equals(_existingMuscleGroup)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |muscle_group(com.ironlog.app.data.db.entity.MuscleGroupEntity).
              | Expected:
              |""".trimMargin() + _infoMuscleGroup + """
              |
              | Found:
              |""".trimMargin() + _existingMuscleGroup)
        }
        val _columnsExercise: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExercise.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("name_zh", TableInfo.Column("name_zh", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("name_en", TableInfo.Column("name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("kind", TableInfo.Column("kind", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("equipment", TableInfo.Column("equipment", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("primary_muscle_id", TableInfo.Column("primary_muscle_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("is_custom", TableInfo.Column("is_custom", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("is_archived", TableInfo.Column("is_archived", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercise.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExercise: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExercise: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExercise: TableInfo = TableInfo("exercise", _columnsExercise, _foreignKeysExercise, _indicesExercise)
        val _existingExercise: TableInfo = read(connection, "exercise")
        if (!_infoExercise.equals(_existingExercise)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |exercise(com.ironlog.app.data.db.entity.ExerciseEntity).
              | Expected:
              |""".trimMargin() + _infoExercise + """
              |
              | Found:
              |""".trimMargin() + _existingExercise)
        }
        val _columnsExerciseMuscle: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExerciseMuscle.put("exercise_id", TableInfo.Column("exercise_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExerciseMuscle.put("muscle_id", TableInfo.Column("muscle_id", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExerciseMuscle.put("role", TableInfo.Column("role", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsExerciseMuscle.put("weight", TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExerciseMuscle: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExerciseMuscle: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExerciseMuscle: TableInfo = TableInfo("exercise_muscle", _columnsExerciseMuscle, _foreignKeysExerciseMuscle, _indicesExerciseMuscle)
        val _existingExerciseMuscle: TableInfo = read(connection, "exercise_muscle")
        if (!_infoExerciseMuscle.equals(_existingExerciseMuscle)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |exercise_muscle(com.ironlog.app.data.db.entity.ExerciseMuscleEntity).
              | Expected:
              |""".trimMargin() + _infoExerciseMuscle + """
              |
              | Found:
              |""".trimMargin() + _existingExerciseMuscle)
        }
        val _columnsWorkoutSession: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkoutSession.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("status", TableInfo.Column("status", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("started_at", TableInfo.Column("started_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("ended_at", TableInfo.Column("ended_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("local_date", TableInfo.Column("local_date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("rating", TableInfo.Column("rating", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("note", TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("rest_target_at", TableInfo.Column("rest_target_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSession.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkoutSession: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWorkoutSession: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWorkoutSession: TableInfo = TableInfo("workout_session", _columnsWorkoutSession, _foreignKeysWorkoutSession, _indicesWorkoutSession)
        val _existingWorkoutSession: TableInfo = read(connection, "workout_session")
        if (!_infoWorkoutSession.equals(_existingWorkoutSession)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workout_session(com.ironlog.app.data.db.entity.WorkoutSessionEntity).
              | Expected:
              |""".trimMargin() + _infoWorkoutSession + """
              |
              | Found:
              |""".trimMargin() + _existingWorkoutSession)
        }
        val _columnsSessionExercise: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSessionExercise.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("session_id", TableInfo.Column("session_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("exercise_id", TableInfo.Column("exercise_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("order_index", TableInfo.Column("order_index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("note", TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessionExercise.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSessionExercise: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysSessionExercise.add(TableInfo.ForeignKey("workout_session", "CASCADE", "NO ACTION", listOf("session_id"), listOf("id")))
        _foreignKeysSessionExercise.add(TableInfo.ForeignKey("exercise", "NO ACTION", "NO ACTION", listOf("exercise_id"), listOf("id")))
        val _indicesSessionExercise: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSessionExercise.add(TableInfo.Index("index_session_exercise_session_id", false, listOf("session_id"), listOf("ASC")))
        _indicesSessionExercise.add(TableInfo.Index("index_session_exercise_exercise_id", false, listOf("exercise_id"), listOf("ASC")))
        val _infoSessionExercise: TableInfo = TableInfo("session_exercise", _columnsSessionExercise, _foreignKeysSessionExercise, _indicesSessionExercise)
        val _existingSessionExercise: TableInfo = read(connection, "session_exercise")
        if (!_infoSessionExercise.equals(_existingSessionExercise)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |session_exercise(com.ironlog.app.data.db.entity.SessionExerciseEntity).
              | Expected:
              |""".trimMargin() + _infoSessionExercise + """
              |
              | Found:
              |""".trimMargin() + _existingSessionExercise)
        }
        val _columnsWorkoutSet: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkoutSet.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("session_exercise_id", TableInfo.Column("session_exercise_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("order_index", TableInfo.Column("order_index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("set_type", TableInfo.Column("set_type", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("parent_set_id", TableInfo.Column("parent_set_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("weight_g", TableInfo.Column("weight_g", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("input_unit", TableInfo.Column("input_unit", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("reps", TableInfo.Column("reps", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("rir", TableInfo.Column("rir", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("duration_s", TableInfo.Column("duration_s", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("distance_m", TableInfo.Column("distance_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("level", TableInfo.Column("level", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("incline_x10", TableInfo.Column("incline_x10", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("speed_x10", TableInfo.Column("speed_x10", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("completed_at", TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("is_completed", TableInfo.Column("is_completed", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSet.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkoutSet: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysWorkoutSet.add(TableInfo.ForeignKey("session_exercise", "CASCADE", "NO ACTION", listOf("session_exercise_id"), listOf("id")))
        val _indicesWorkoutSet: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWorkoutSet.add(TableInfo.Index("index_workout_set_session_exercise_id", false, listOf("session_exercise_id"), listOf("ASC")))
        val _infoWorkoutSet: TableInfo = TableInfo("workout_set", _columnsWorkoutSet, _foreignKeysWorkoutSet, _indicesWorkoutSet)
        val _existingWorkoutSet: TableInfo = read(connection, "workout_set")
        if (!_infoWorkoutSet.equals(_existingWorkoutSet)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workout_set(com.ironlog.app.data.db.entity.WorkoutSetEntity).
              | Expected:
              |""".trimMargin() + _infoWorkoutSet + """
              |
              | Found:
              |""".trimMargin() + _existingWorkoutSet)
        }
        val _columnsBodyWeight: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBodyWeight.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyWeight.put("local_date", TableInfo.Column("local_date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyWeight.put("weight_g", TableInfo.Column("weight_g", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyWeight.put("recorded_at", TableInfo.Column("recorded_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyWeight.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyWeight.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBodyWeight: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBodyWeight: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBodyWeight.add(TableInfo.Index("index_body_weight_local_date", true, listOf("local_date"), listOf("ASC")))
        val _infoBodyWeight: TableInfo = TableInfo("body_weight", _columnsBodyWeight, _foreignKeysBodyWeight, _indicesBodyWeight)
        val _existingBodyWeight: TableInfo = read(connection, "body_weight")
        if (!_infoBodyWeight.equals(_existingBodyWeight)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |body_weight(com.ironlog.app.data.db.entity.BodyWeightEntity).
              | Expected:
              |""".trimMargin() + _infoBodyWeight + """
              |
              | Found:
              |""".trimMargin() + _existingBodyWeight)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "muscle_group", "exercise", "exercise_muscle", "workout_session", "session_exercise", "workout_set", "body_weight")
  }

  public override fun clearAllTables() {
    super.performClear(true, "muscle_group", "exercise", "exercise_muscle", "workout_session", "session_exercise", "workout_set", "body_weight")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(MuscleGroupDao::class, MuscleGroupDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExerciseDao::class, ExerciseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WorkoutDao::class, WorkoutDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BodyWeightDao::class, BodyWeightDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun muscleGroupDao(): MuscleGroupDao = _muscleGroupDao.value

  public override fun exerciseDao(): ExerciseDao = _exerciseDao.value

  public override fun workoutDao(): WorkoutDao = _workoutDao.value

  public override fun bodyWeightDao(): BodyWeightDao = _bodyWeightDao.value
}
