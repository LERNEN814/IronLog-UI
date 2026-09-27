package com.ironlog.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.data.db.IronLogDatabase
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import com.ironlog.app.domain.fatigue.FatigueInputSet
import com.ironlog.app.domain.fatigue.FatigueModel
import com.ironlog.app.domain.fatigue.FatigueRepository
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** M5-T5.1: repository output matches FatigueModel on the same inputs; window/filters are applied. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FatigueRepositoryImplTest {

    private val now = 1_700_000_000_000L
    private val hour = 3_600_000L

    private lateinit var db: IronLogDatabase
    private lateinit var repository: FatigueRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        repository = FatigueRepositoryImpl(
            workoutDao = db.workoutDao(),
            exerciseDao = db.exerciseDao(),
            muscleGroupDao = db.muscleGroupDao(),
        )
        runBlocking {
            db.muscleGroupDao().insertAll(
                listOf(
                    MuscleGroupEntity("chest", "Chest", "Chest", 0, 48, 0),
                    MuscleGroupEntity("triceps", "Triceps", "Triceps", 3, 30, 1),
                    MuscleGroupEntity("cardio", "Cardio", "Cardio", 6, 12, 2),
                ),
            )
            db.exerciseDao().insertAll(
                listOf(
                    ExerciseEntity("bench", "Bench", "Bench", 0, "barbell", "chest", 0, 0, 0, 0),
                    ExerciseEntity("treadmill", "Run", "Run", 1, "treadmill", "cardio", 0, 0, 0, 0),
                ),
            )
            db.exerciseDao().insertMuscles(
                listOf(
                    ExerciseMuscleEntity("bench", "chest", 0, 1.0),
                    ExerciseMuscleEntity("bench", "triceps", 1, 0.5),
                    ExerciseMuscleEntity("treadmill", "cardio", 0, 1.0),
                ),
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun insertSession(
        id: String,
        startedAt: Long,
        entryId: String,
        exerciseId: String,
        status: Int = 1,
    ) {
        db.workoutDao().insertSession(
            WorkoutSessionEntity(
                id = id,
                status = status,
                startedAt = startedAt,
                endedAt = startedAt + hour,
                localDate = "2026-01-01",
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = startedAt,
                updatedAt = startedAt,
            ),
        )
        db.workoutDao().insertSessionExercise(
            SessionExerciseEntity(entryId, id, exerciseId, 0, null, startedAt, startedAt),
        )
    }

    private suspend fun insertSet(
        id: String,
        entryId: String,
        setType: Int = 0,
        completedAt: Long?,
        isCompleted: Int = 1,
        weightG: Int? = 60_000,
        reps: Int? = 8,
        durationS: Int? = null,
    ) {
        db.workoutDao().insertSet(
            WorkoutSetEntity(
                id = id,
                sessionExerciseId = entryId,
                orderIndex = 0,
                setType = setType,
                parentSetId = null,
                weightG = weightG,
                inputUnit = 0,
                reps = reps,
                rir = null,
                durationS = durationS,
                distanceM = null,
                level = null,
                inclineX10 = null,
                speedX10 = null,
                completedAt = completedAt,
                isCompleted = isCompleted,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
    }

    @Test
    fun matchesDirectFatigueModelComputation() {
        runBlocking {
        insertSession("recent", now - 3 * hour, "e1", "bench")
        insertSet("s1", "e1", completedAt = now - 2 * hour)
        insertSet("s2", "e1", completedAt = null) // falls back to session start (now - 3h)
        insertSet("s3", "e1", completedAt = now - hour, isCompleted = 0, weightG = 100_000, reps = 5)
        insertSession("old", now - 200 * hour, "e2", "bench")
        insertSet("s4", "e2", completedAt = now - 200 * hour)

        val expected = FatigueModel.compute(
            sets = listOf(
                FatigueInputSet(
                    completedAtMillis = now - 2 * hour,
                    kind = ExerciseKind.STRENGTH,
                    setType = SetType.WORK,
                    rir = null,
                    durationS = null,
                    muscles = listOf("chest" to 1.0, "triceps" to 0.5),
                ),
                FatigueInputSet(
                    completedAtMillis = now - 3 * hour,
                    kind = ExerciseKind.STRENGTH,
                    setType = SetType.WORK,
                    rir = null,
                    durationS = null,
                    muscles = listOf("chest" to 1.0, "triceps" to 0.5),
                ),
            ),
            nowMillis = now,
            halfLifeHours = mapOf("chest" to 48, "triceps" to 30, "cardio" to 12),
        )

        val scores = repository.scores(now)

        assertThat(scores).containsExactlyEntriesIn(expected)
        }
    }

    @Test
    fun setsOutsideTheWindowAreExcluded() = runBlocking {
        insertSession("old", now - 200 * hour, "e1", "bench")
        insertSet("s1", "e1", completedAt = now - 200 * hour)

        assertThat(repository.scores(now)).isEmpty()
    }

    @Test
    fun incompleteSetsAreExcluded() = runBlocking {
        insertSession("recent", now - hour, "e1", "bench")
        insertSet("s1", "e1", completedAt = now - hour, isCompleted = 0)

        assertThat(repository.scores(now)).isEmpty()
    }

    @Test
    fun completedAtNullFallsBackToTheSessionStart() = runBlocking {
        insertSession("recent", now - 3 * hour, "e1", "bench")
        insertSet("s1", "e1", completedAt = null)

        val scores = repository.scores(now)
        val expected = FatigueModel.compute(
            sets = listOf(
                FatigueInputSet(
                    completedAtMillis = now - 3 * hour,
                    kind = ExerciseKind.STRENGTH,
                    setType = SetType.WORK,
                    rir = null,
                    durationS = null,
                    muscles = listOf("chest" to 1.0, "triceps" to 0.5),
                ),
            ),
            nowMillis = now,
            halfLifeHours = mapOf("chest" to 48, "triceps" to 30, "cardio" to 12),
        )

        assertThat(scores).containsExactlyEntriesIn(expected)
        assertThat(scores).containsKey("chest")
    }

    @Test
    fun inProgressSessionCompletedSetsAreCounted() = runBlocking {
        insertSession("current", now - 3 * hour, "e1", "bench", status = 0)
        insertSet("s1", "e1", completedAt = now - 2 * hour)

        val scores = repository.scores(now)

        assertThat(scores).containsKey("chest")
        assertThat(scores["chest"]).isAtLeast(1)
    }

    @Test
    fun cardioUsesDurationStimulus() = runBlocking {
        insertSession("recent", now - 3 * hour, "e1", "treadmill")
        insertSet("s1", "e1", completedAt = now - 3 * hour, weightG = null, reps = null, durationS = 1_200)

        val scores = repository.scores(now)

        assertThat(scores).containsKey("cardio")
        // F10 vector: 20 min, 3 h old -> 17.
        assertThat(scores["cardio"]).isEqualTo(17)
    }
}
