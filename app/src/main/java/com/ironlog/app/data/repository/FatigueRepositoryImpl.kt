package com.ironlog.app.data.repository

import com.ironlog.app.data.db.chunkedQuery
import com.ironlog.app.data.db.converter.exerciseKindFrom
import com.ironlog.app.data.db.converter.setTypeFrom
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.dao.WorkoutDao
import com.ironlog.app.domain.fatigue.FatigueInputSet
import com.ironlog.app.domain.fatigue.FatigueModel
import com.ironlog.app.domain.fatigue.FatigueRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** M5-T5.1: assembles FatigueInputSet rows and delegates the math to FatigueModel. */
class FatigueRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
) : FatigueRepository {

    override suspend fun scores(nowMillis: Long): Map<String, Int> {
        val sinceMillis = nowMillis - (FatigueModel.WINDOW_HOURS * 3_600_000.0).toLong()
        val rows = workoutDao.getFatigueSetsSince(sinceMillis)
        if (rows.isEmpty()) return emptyMap()

        val halfLives = muscleGroupDao.getAll().associate { it.id to it.recoveryHalfLifeHours }
        val exerciseIds = rows.map { it.exerciseId }.distinct()
        val links = chunkedQuery(exerciseIds) { chunk -> exerciseDao.getMusclesForExercises(chunk) }
        val linksByExercise = links.groupBy { it.exerciseId }

        val inputs = rows.map { row ->
            FatigueInputSet(
                completedAtMillis = row.set.completedAt ?: row.sessionStartedAt,
                kind = exerciseKindFrom(row.exerciseKind),
                setType = setTypeFrom(row.set.setType),
                rir = row.set.rir,
                durationS = row.set.durationS,
                muscles = linksByExercise[row.exerciseId].orEmpty()
                    .map { muscle -> muscle.muscleId to muscle.weight },
            )
        }
        return withContext(Dispatchers.Default) {
            FatigueModel.compute(inputs, nowMillis, halfLives)
        }
    }
}
