package com.ironlog.app.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** DOMAIN_RULES.md section 3.1 integration vectors R1-R4. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LastSetsQueryTest {

    private lateinit var db: IronLogDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        runBlocking { seedExercise("bench") }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun seedExercise(id: String) {
        db.exerciseDao().insertAll(
            listOf(
                ExerciseEntity(
                    id = id,
                    nameZh = id,
                    nameEn = id,
                    kind = 0,
                    equipment = "barbell",
                    primaryMuscleId = null,
                    isCustom = 0,
                    isArchived = 0,
                    createdAt = 0,
                    updatedAt = 0,
                ),
            ),
        )
    }

    private suspend fun addSession(id: String, status: Int, startedAt: Long) {
        db.workoutDao().insertSession(
            WorkoutSessionEntity(
                id = id,
                status = status,
                startedAt = startedAt,
                endedAt = if (status == 1) startedAt + 3_600_000 else null,
                localDate = "2026-01-01",
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = startedAt,
                updatedAt = startedAt,
            ),
        )
    }

    private suspend fun addEntry(id: String, sessionId: String, exerciseId: String, orderIndex: Int) {
        db.workoutDao().insertSessionExercise(
            SessionExerciseEntity(
                id = id,
                sessionId = sessionId,
                exerciseId = exerciseId,
                orderIndex = orderIndex,
                note = null,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
    }

    private suspend fun addSet(id: String, entryId: String, orderIndex: Int, completed: Boolean, weightG: Int) {
        db.workoutDao().insertSet(
            WorkoutSetEntity(
                id = id,
                sessionExerciseId = entryId,
                orderIndex = orderIndex,
                setType = 0,
                parentSetId = null,
                weightG = weightG,
                inputUnit = 0,
                reps = 5,
                rir = null,
                durationS = null,
                distanceM = null,
                level = null,
                inclineX10 = null,
                speedX10 = null,
                completedAt = if (completed) 1 else null,
                isCompleted = if (completed) 1 else 0,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
    }

    @Test
    fun r1_returnsSetsOfTheMostRecentFinishedSession() {
        runBlocking {
        addSession("s1", status = 1, startedAt = 1_000)
        addEntry("e1", "s1", "bench", 0)
        addSet("set-1", "e1", 0, completed = true, weightG = 60_000)
        addSession("s2", status = 1, startedAt = 2_000)
        addEntry("e2", "s2", "bench", 0)
        addSet("set-2", "e2", 0, completed = true, weightG = 70_000)
        addSession("s3", status = 1, startedAt = 3_000)
        addEntry("e3", "s3", "bench", 0)
        addSet("set-3", "e3", 0, completed = true, weightG = 80_000)

        val sets = db.workoutDao().getLastSetsForExercise("bench")

        assertThat(sets.map { it.id }).containsExactly("set-3")
        }
    }

    @Test
    fun r2_fallsBackWhenLatestEntryHasNoCompletedSets() {
        runBlocking {
        addSession("s1", status = 1, startedAt = 1_000)
        addEntry("e1", "s1", "bench", 0)
        addSet("set-1", "e1", 0, completed = true, weightG = 60_000)
        addSession("s2", status = 1, startedAt = 2_000)
        addEntry("e2", "s2", "bench", 0)
        addSet("set-2", "e2", 0, completed = false, weightG = 90_000)

        val sets = db.workoutDao().getLastSetsForExercise("bench")

        assertThat(sets.map { it.id }).containsExactly("set-1")
        }
    }

    @Test
    fun r3_inProgressSessionIsIgnored() {
        runBlocking {
        addSession("s1", status = 1, startedAt = 1_000)
        addEntry("e1", "s1", "bench", 0)
        addSet("set-1", "e1", 0, completed = true, weightG = 60_000)
        addSession("s2", status = 0, startedAt = 2_000)
        addEntry("e2", "s2", "bench", 0)
        addSet("set-2", "e2", 0, completed = true, weightG = 90_000)

        val sets = db.workoutDao().getLastSetsForExercise("bench")

        assertThat(sets.map { it.id }).containsExactly("set-1")
        }
    }

    @Test
    fun r4_sameSessionUsesHigherOrderEntry() {
        runBlocking {
        addSession("s1", status = 1, startedAt = 1_000)
        addEntry("e1", "s1", "bench", 0)
        addSet("set-1", "e1", 0, completed = true, weightG = 60_000)
        addEntry("e2", "s1", "bench", 1)
        addSet("set-2", "e2", 0, completed = true, weightG = 70_000)

        val sets = db.workoutDao().getLastSetsForExercise("bench")

        assertThat(sets.map { it.id }).containsExactly("set-2")
        }
    }

    @Test
    fun returnsSetsOfTheEntryInOrderAndOnlyCompletedOnes() {
        runBlocking {
        addSession("s1", status = 1, startedAt = 1_000)
        addEntry("e1", "s1", "bench", 0)
        addSet("set-2", "e1", 2, completed = true, weightG = 60_000)
        addSet("set-0", "e1", 0, completed = true, weightG = 40_000)
        addSet("set-1", "e1", 1, completed = false, weightG = 50_000)

        val sets = db.workoutDao().getLastSetsForExercise("bench")

        assertThat(sets.map { it.id }).containsExactly("set-0", "set-2").inOrder()
        }
    }
}
