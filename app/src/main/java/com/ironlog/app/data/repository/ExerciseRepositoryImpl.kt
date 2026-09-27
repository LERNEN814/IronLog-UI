package com.ironlog.app.data.repository

import com.ironlog.app.core.id.IdGenerator
import com.ironlog.app.core.time.Clock
import com.ironlog.app.data.db.converter.toDbValue
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : ExerciseRepository {

    override fun observeAll(): Flow<List<Exercise>> =
        exerciseDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String, bodyRegion: Int?): Flow<List<Exercise>> =
        exerciseDao.search(query, bodyRegion).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Exercise? = exerciseDao.getById(id)?.toDomain()

    override fun observeMuscleGroups(): Flow<List<MuscleGroup>> =
        muscleGroupDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLastUsed(): Flow<Map<String, Long>> =
        exerciseDao.observeLastUsed().map { rows -> rows.associate { it.exerciseId to it.lastUsedAt } }

    override suspend fun getMusclesFor(exerciseId: String): List<ExerciseMuscle> =
        exerciseDao.getMusclesFor(exerciseId).map { it.toDomain() }

    override suspend fun upsertCustom(
        id: String?,
        nameZh: String,
        nameEn: String,
        kind: ExerciseKind,
        equipment: String,
        primaryMuscleId: String?,
        muscles: List<ExerciseMuscle>,
    ): String {
        val now = clock.nowMillis()
        val exerciseId = id ?: idGenerator.newId()
        val existing = exerciseDao.getById(exerciseId)
        require(existing == null || existing.isCustom == 1) {
            "built-in exercise cannot be edited: $exerciseId"
        }

        val entity = if (existing == null) {
            ExerciseEntity(
                id = exerciseId,
                nameZh = nameZh,
                nameEn = nameEn,
                kind = kind.toDbValue(),
                equipment = equipment,
                primaryMuscleId = primaryMuscleId,
                isCustom = 1,
                isArchived = 0,
                createdAt = now,
                updatedAt = now,
            )
        } else {
            existing.copy(
                nameZh = nameZh,
                nameEn = nameEn,
                kind = kind.toDbValue(),
                equipment = equipment,
                primaryMuscleId = primaryMuscleId,
                updatedAt = now,
            )
        }
        exerciseDao.upsertCustomExercise(
            exercise = entity,
            muscles = muscles.map { it.copy(exerciseId = exerciseId).toEntity() },
        )
        return exerciseId
    }

    override suspend fun archive(id: String) {
        exerciseDao.archive(id, clock.nowMillis())
    }

    override suspend fun countBuiltIn(): Int = exerciseDao.countBuiltIn()
}
