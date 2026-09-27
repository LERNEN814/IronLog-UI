package com.ironlog.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.id.FixedIdGenerator
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.data.db.IronLogDatabase
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExerciseRepositoryImplTest {

    private lateinit var db: IronLogDatabase
    private lateinit var repository: ExerciseRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        repository = ExerciseRepositoryImpl(
            exerciseDao = db.exerciseDao(),
            muscleGroupDao = db.muscleGroupDao(),
            clock = FixedClock(1_000L),
            idGenerator = FixedIdGenerator(),
        )
        runBlocking {
            db.muscleGroupDao().insertAll(
                listOf(
                    muscleGroup("chest", 0),
                    muscleGroup("quads", 4),
                    muscleGroup("biceps", 3),
                    muscleGroup("triceps", 3),
                ),
            )
            db.exerciseDao().insertAll(
                listOf(builtInExercise("barbell_bench_press", "杠铃卧推", "Barbell Bench Press", "chest")),
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun muscleGroup(id: String, bodyRegion: Int) = MuscleGroupEntity(
        id = id,
        displayNameEn = id,
        displayNameZh = id,
        bodyRegion = bodyRegion,
        recoveryHalfLifeHours = 48,
        sortOrder = 0,
    )

    private fun builtInExercise(id: String, nameZh: String, nameEn: String, primary: String?) = ExerciseEntity(
        id = id,
        nameZh = nameZh,
        nameEn = nameEn,
        kind = 0,
        equipment = "barbell",
        primaryMuscleId = primary,
        isCustom = 0,
        isArchived = 0,
        createdAt = 0,
        updatedAt = 0,
    )

    @Test
    fun createsCustomExerciseWithMuscleMappings() = runBlocking {
        val id = repository.upsertCustom(
            id = null,
            nameZh = "哑铃弯举",
            nameEn = "Dumbbell Curl",
            kind = ExerciseKind.STRENGTH,
            equipment = "dumbbell",
            primaryMuscleId = "biceps",
            muscles = listOf(
                ExerciseMuscle("", "biceps", MuscleRole.PRIMARY, 1.0),
                ExerciseMuscle("", "forearms", MuscleRole.SECONDARY, 0.5),
            ),
        )

        val exercise = repository.getById(id)
        assertThat(exercise?.isCustom).isTrue()
        assertThat(exercise?.nameZh).isEqualTo("哑铃弯举")
        val muscles = repository.getMusclesFor(id)
        assertThat(muscles).hasSize(2)
        assertThat(muscles.first { it.role == MuscleRole.PRIMARY }.muscleId).isEqualTo("biceps")
        assertThat(muscles.first { it.muscleId == "forearms" }.exerciseId).isEqualTo(id)
    }

    @Test
    fun editingReplacesMuscleMappings() {
        runBlocking {
        val id = repository.upsertCustom(
            id = null,
            nameZh = "First",
            nameEn = "First",
            kind = ExerciseKind.STRENGTH,
            equipment = "cable",
            primaryMuscleId = "biceps",
            muscles = listOf(ExerciseMuscle("", "biceps", MuscleRole.PRIMARY, 1.0)),
        )

        repository.upsertCustom(
            id = id,
            nameZh = "Second",
            nameEn = "Second",
            kind = ExerciseKind.STRENGTH,
            equipment = "cable",
            primaryMuscleId = "triceps",
            muscles = listOf(ExerciseMuscle("", "triceps", MuscleRole.PRIMARY, 1.0)),
        )

        assertThat(repository.getById(id)?.nameZh).isEqualTo("Second")
        val muscles = repository.getMusclesFor(id)
        assertThat(muscles.map { it.muscleId }).containsExactly("triceps")
        }
    }

    @Test
    fun editingBuiltInExerciseThrows() {
        runBlocking {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.upsertCustom(
                    id = "barbell_bench_press",
                    nameZh = "changed",
                    nameEn = "changed",
                    kind = ExerciseKind.STRENGTH,
                    equipment = "barbell",
                    primaryMuscleId = "chest",
                    muscles = emptyList(),
                )
            }
        }
        }
    }

    @Test
    fun archiveHidesExerciseFromLists() = runBlocking {
        val id = repository.upsertCustom(
            id = null,
            nameZh = "Temp",
            nameEn = "Temp",
            kind = ExerciseKind.STRENGTH,
            equipment = "machine",
            primaryMuscleId = "quads",
            muscles = listOf(ExerciseMuscle("", "quads", MuscleRole.PRIMARY, 1.0)),
        )

        repository.archive(id)

        assertThat(repository.observeAll().first().map { it.id }).doesNotContain(id)
        assertThat(repository.search("", null).first().map { it.id }).doesNotContain(id)
        // Still reachable through history lookups.
        assertThat(repository.getById(id)?.isArchived).isTrue()
    }

    @Test
    fun searchWithoutRegionReturnsAllExercises() {
        runBlocking {
        repository.upsertCustom(
            id = null,
            nameZh = "腿举",
            nameEn = "Leg Press",
            kind = ExerciseKind.STRENGTH,
            equipment = "machine",
            primaryMuscleId = "quads",
            muscles = listOf(ExerciseMuscle("", "quads", MuscleRole.PRIMARY, 1.0)),
        )

        val all = repository.search("", null).first()

        assertThat(all.map { it.id }).containsExactly("barbell_bench_press", "fixed-1")
        }
    }

    @Test
    fun searchFiltersByRegionAndQuery() {
        runBlocking {
        repository.upsertCustom(
            id = null,
            nameZh = "腿举",
            nameEn = "Leg Press",
            kind = ExerciseKind.STRENGTH,
            equipment = "machine",
            primaryMuscleId = "quads",
            muscles = listOf(ExerciseMuscle("", "quads", MuscleRole.PRIMARY, 1.0)),
        )

        assertThat(repository.search("", 0).first().map { it.id })
            .containsExactly("barbell_bench_press")
        assertThat(repository.search("腿举", null).first().map { it.id })
            .containsExactly("fixed-1")
        assertThat(repository.search("BENCH", null).first().map { it.id })
            .containsExactly("barbell_bench_press")
        }
    }

    @Test
    fun lastUsedTracksSessions() = runBlocking {
        val sessionId = "session-1"
        db.workoutDao().insertSession(
            com.ironlog.app.data.db.entity.WorkoutSessionEntity(
                id = sessionId,
                status = 0,
                startedAt = 5_000L,
                endedAt = null,
                localDate = "2026-01-01",
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = 5_000L,
                updatedAt = 5_000L,
            ),
        )
        db.workoutDao().insertSessionExercise(
            com.ironlog.app.data.db.entity.SessionExerciseEntity(
                id = "entry-1",
                sessionId = sessionId,
                exerciseId = "barbell_bench_press",
                orderIndex = 0,
                note = null,
                createdAt = 5_000L,
                updatedAt = 5_000L,
            ),
        )

        val lastUsed = repository.observeLastUsed().first()

        assertThat(lastUsed["barbell_bench_press"]).isEqualTo(5_000L)
    }
}
