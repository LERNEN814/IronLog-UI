package com.ironlog.app.data.backup

import android.content.ContentResolver
import android.net.Uri
import com.ironlog.app.data.db.IronLogDatabase
import com.ironlog.app.data.db.entity.BodyWeightEntity
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import com.ironlog.app.data.seed.SeedImporter
import com.ironlog.app.domain.backup.*
import com.ironlog.app.core.time.Clock
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val database: IronLogDatabase,
    private val resolver: ContentResolver,
    private val clock: Clock,
    private val seedImporter: SeedImporter,
) {
    private val json = Json { ignoreUnknownKeys = false; prettyPrint = true }

    suspend fun exportTo(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val file = database.snapshot()
            val text = json.encodeToString(BackupFile.serializer(), file)
            resolver.openOutputStream(uri)?.use { it.write(text.toByteArray(Charsets.UTF_8)) }
                ?: error("Unable to open backup destination")
            file.totalCount()
        }
    }

    suspend fun importFrom(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val input = resolver.openInputStream(uri) ?: error("Unable to open backup source")
            val file = input.use { json.decodeFromString(BackupFile.serializer(), it.readBytes().toString(Charsets.UTF_8)) }
            require(file.schemaVersion == 1) { "Unsupported backup schema: ${file.schemaVersion}" }
            validate(file)
            database.withTransaction {
                database.clearUserDataBlocking()
                database.muscleGroupDao().insertAll(file.muscles.map { it.toEntity() })
                database.exerciseDao().insertAll(file.exercises.map { it.toEntity() })
                database.exerciseDao().insertMuscles(file.exerciseMuscles.map { it.toEntity() })
                file.sessions.forEach { database.workoutDao().insertSession(it.toEntity()) }
                file.sessionExercises.forEach { database.workoutDao().insertSessionExercise(it.toEntity()) }
                database.workoutDao().insertSets(file.sets.map { it.toEntity() })
                file.bodyWeights.forEach { database.bodyWeightDao().upsert(it.toEntity()) }
            }
            seedImporter.importFromAssets()
        }
    }

    private fun validate(file: BackupFile) {
        val muscleIds = file.muscles.map { it.id }.toSet()
        val exerciseIds = file.exercises.map { it.id }.toSet()
        val sessionIds = file.sessions.map { it.id }.toSet()
        val entryIds = file.sessionExercises.map { it.id }.toSet()
        require(muscleIds.size == file.muscles.size) { "Duplicate muscle IDs" }
        require(exerciseIds.size == file.exercises.size) { "Duplicate exercise IDs" }
        require(sessionIds.size == file.sessions.size) { "Duplicate session IDs" }
        require(entryIds.size == file.sessionExercises.size) { "Duplicate session exercise IDs" }
        require(file.exerciseMuscles.all { it.exerciseId in exerciseIds && it.muscleId in muscleIds }) { "Backup has invalid exercise muscle references" }
        require(file.sessions.all { it.status in 0..1 }) { "Backup has invalid session status" }
        require(file.sessionExercises.all { it.sessionId in sessionIds && it.exerciseId in exerciseIds }) { "Backup has invalid session exercise references" }
        require(file.sets.all { it.sessionExerciseId in entryIds }) { "Backup has invalid set references" }
        require(file.bodyWeights.all { it.weightG > 0 && it.localDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }) { "Backup has invalid body weight data" }
    }

    private suspend fun IronLogDatabase.snapshot(): BackupFile = BackupFile(
        schemaVersion = 1, appVersion = "0.1.0", exportedAt = clock.nowMillis(),
        muscles = muscleGroupDao().getAll().map { it.toBackup() }, exercises = exerciseDao().getAll().map { it.toBackup() },
        exerciseMuscles = exerciseDao().getAllMuscles().map { it.toBackup() }, sessions = workoutDao().getAllSessions().map { it.toBackup() },
        sessionExercises = workoutDao().getAllSessionExercises().map { it.toBackup() }, sets = workoutDao().getAllSets().map { it.toBackup() },
        bodyWeights = bodyWeightDao().getAll().map { it.toBackup() },
    )

    private fun BackupFile.totalCount() = muscles.size + exercises.size + exerciseMuscles.size + sessions.size + sessionExercises.size + sets.size + bodyWeights.size
    private fun BackupMuscle.toEntity() = MuscleGroupEntity(id, displayNameEn, displayNameZh, bodyRegion, recoveryHalfLifeHours, sortOrder)
    private fun BackupExercise.toEntity() = ExerciseEntity(id, nameZh, nameEn, kind, equipment, primaryMuscleId, isCustom, isArchived, createdAt, updatedAt)
    private fun BackupExerciseMuscle.toEntity() = ExerciseMuscleEntity(exerciseId, muscleId, role, weight)
    private fun BackupSession.toEntity() = WorkoutSessionEntity(id, status, startedAt, endedAt, localDate, rating, note, restTargetAt, createdAt, updatedAt)
    private fun BackupSessionExercise.toEntity() = SessionExerciseEntity(id, sessionId, exerciseId, orderIndex, note, createdAt, updatedAt)
    private fun BackupSet.toEntity() = WorkoutSetEntity(id, sessionExerciseId, orderIndex, setType, parentSetId, weightG, inputUnit, reps, rir, durationS, distanceM, level, inclineX10, speedX10, completedAt, isCompleted, createdAt, updatedAt)
    private fun BackupBodyWeight.toEntity() = BodyWeightEntity(id, localDate, weightG, recordedAt, createdAt, updatedAt)
    private fun MuscleGroupEntity.toBackup() = BackupMuscle(id, displayNameEn, displayNameZh, bodyRegion, recoveryHalfLifeHours, sortOrder)
    private fun ExerciseEntity.toBackup() = BackupExercise(id, nameZh, nameEn, kind, equipment, primaryMuscleId, isCustom, isArchived, createdAt, updatedAt)
    private fun ExerciseMuscleEntity.toBackup() = BackupExerciseMuscle(exerciseId, muscleId, role, weight)
    private fun WorkoutSessionEntity.toBackup() = BackupSession(id, status, startedAt, endedAt, localDate, rating, note, restTargetAt, createdAt, updatedAt)
    private fun SessionExerciseEntity.toBackup() = BackupSessionExercise(id, sessionId, exerciseId, orderIndex, note, createdAt, updatedAt)
    private fun WorkoutSetEntity.toBackup() = BackupSet(id, sessionExerciseId, orderIndex, setType, parentSetId, weightG, inputUnit, reps, rir, durationS, distanceM, level, inclineX10, speedX10, completedAt, isCompleted, createdAt, updatedAt)
    private fun BodyWeightEntity.toBackup() = BackupBodyWeight(id, localDate, weightG, recordedAt, createdAt, updatedAt)
}
