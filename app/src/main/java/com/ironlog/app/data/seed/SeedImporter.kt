package com.ironlog.app.data.seed

import android.content.Context
import com.ironlog.app.core.time.Clock
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.serialization.json.Json

/**
 * Imports the bundled muscle/exercise seed. Every insert ignores existing ids, so running at every
 * app start is idempotent and never overwrites user edits (DATA_MODEL.md section 5).
 */
class SeedImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val muscleGroupDao: MuscleGroupDao,
    private val exerciseDao: ExerciseDao,
    private val clock: Clock,
) {
    suspend fun importFromAssets() {
        val json = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
        import(json)
    }

    suspend fun import(json: String) {
        val seed = JSON.decodeFromString(SeedFile.serializer(), json)
        val now = clock.nowMillis()

        muscleGroupDao.insertAll(seed.muscles.map { it.toEntity() })
        exerciseDao.insertAll(seed.exercises.map { it.toEntity(now) })
        exerciseDao.insertMuscles(seed.exercises.flatMap { it.toMuscleEntities() })
    }

    private fun SeedMuscle.toEntity() = MuscleGroupEntity(
        id = id,
        displayNameEn = displayNameEn,
        displayNameZh = displayNameZh,
        bodyRegion = bodyRegion,
        recoveryHalfLifeHours = recoveryHalfLifeHours,
        sortOrder = sortOrder,
    )

    private fun SeedExercise.toEntity(now: Long) = ExerciseEntity(
        id = id,
        nameZh = nameZh,
        nameEn = nameEn,
        kind = kind,
        equipment = equipment,
        primaryMuscleId = primaryMuscleId,
        isCustom = 0,
        isArchived = 0,
        createdAt = now,
        updatedAt = now,
    )

    private fun SeedExercise.toMuscleEntities() = muscles.map {
        ExerciseMuscleEntity(
            exerciseId = id,
            muscleId = it.muscleId,
            role = it.role,
            weight = it.weight,
        )
    }

    private companion object {
        const val ASSET_PATH = "seed/seed_v1.json"
        val JSON = Json
    }
}
