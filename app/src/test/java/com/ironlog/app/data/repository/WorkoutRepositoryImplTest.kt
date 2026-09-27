package com.ironlog.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.id.FixedIdGenerator
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.data.db.IronLogDatabase
import com.ironlog.app.data.db.converter.toDbValue
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.workout.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WorkoutRepositoryImplTest {

    private lateinit var db: IronLogDatabase
    private lateinit var repository: WorkoutRepository
    private lateinit var clock: FixedClock

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        clock = FixedClock(1_700_000_000_000L)
        repository = newRepository()
        runBlocking { seedExercise() }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun newRepository(): WorkoutRepository = WorkoutRepositoryImpl(
        workoutDao = db.workoutDao(),
        exerciseDao = db.exerciseDao(),
        muscleGroupDao = db.muscleGroupDao(),
        clock = clock,
        idGenerator = FixedIdGenerator(),
    )

    private suspend fun seedExercise() {
        db.exerciseDao().insertAll(
            listOf(
                ExerciseEntity(
                    id = "barbell_bench_press",
                    nameZh = "Bench",
                    nameEn = "Barbell Bench Press",
                    kind = 0,
                    equipment = "barbell",
                    primaryMuscleId = "chest",
                    isCustom = 0,
                    isArchived = 0,
                    createdAt = 0,
                    updatedAt = 0,
                ),
            ),
        )
    }

    @Test
    fun startSessionCreatesExactlyOneInProgressSession() = runBlocking {
        val first = repository.startSession()
        val second = repository.startSession()

        assertThat(second).isEqualTo(first)
        assertThat(db.workoutDao().countInProgressSessions()).isEqualTo(1)
        val session = repository.getSession(first)
        assertThat(session?.status).isEqualTo(SessionStatus.IN_PROGRESS)
        assertThat(session?.startedAt).isEqualTo(clock.nowMillis())
    }

    @Test
    fun startSessionIsAtomicUnderConcurrentCalls() = runBlocking {
        val ids = coroutineScope {
            (1..10).map { async(Dispatchers.IO) { repository.startSession() } }.awaitAll()
        }

        assertThat(ids.toSet()).hasSize(1)
        assertThat(db.workoutDao().countInProgressSessions()).isEqualTo(1)
    }

    @Test
    fun completingASetIsPersisted() = runBlocking {
        val sessionId = repository.startSession()
        val entryId = repository.addEntry(sessionId, "barbell_bench_press", orderIndex = 0)
        val setId = repository.addSet(entryId, orderIndex = 0)

        val placeholder = repository.getSetsFor(entryId).single()
        assertThat(placeholder.id).isEqualTo(setId)
        assertThat(placeholder.isCompleted).isFalse()

        repository.updateSet(
            placeholder.copy(
                weightGrams = 60_000,
                reps = 8,
                isCompleted = true,
                completedAt = clock.nowMillis(),
            ),
        )

        val reloaded = repository.getSetsFor(entryId).single()
        assertThat(reloaded.isCompleted).isTrue()
        assertThat(reloaded.weightGrams).isEqualTo(60_000)
        assertThat(reloaded.reps).isEqualTo(8)
    }

    @Test
    fun dataWrittenIsVisibleToAFreshRepositoryInstance() = runBlocking {
        val sessionId = repository.startSession()
        val entryId = repository.addEntry(sessionId, "barbell_bench_press", orderIndex = 0)
        repository.addSet(entryId, orderIndex = 0)

        // Simulates a new process: same database, new repository instance.
        val freshRepository = newRepository()

        assertThat(freshRepository.getSession(sessionId)?.status).isEqualTo(SessionStatus.IN_PROGRESS)
        assertThat(freshRepository.getEntriesFor(sessionId)).hasSize(1)
        assertThat(freshRepository.getSetsFor(entryId)).hasSize(1)
        assertThat(freshRepository.observeInProgressSession().first()?.id).isEqualTo(sessionId)
    }

    @Test
    fun finishSessionMarksItFinishedAndStoresRating() = runBlocking {
        val sessionId = repository.startSession()

        repository.finishSession(sessionId, clock.nowMillis() + 3_600_000, rating = 8, note = "good")

        val session = repository.getSession(sessionId)
        assertThat(session?.status).isEqualTo(SessionStatus.FINISHED)
        assertThat(session?.rating).isEqualTo(8)
        assertThat(session?.note).isEqualTo("good")
        assertThat(session?.endedAt).isEqualTo(clock.nowMillis() + 3_600_000)
        assertThat(repository.observeInProgressSession().first()).isNull()
        assertThat(db.workoutDao().countInProgressSessions()).isEqualTo(0)
    }

    @Test
    fun lastSetsRoundTripThroughTheRepository() = runBlocking {
        val sessionId = repository.startSession()
        val entryId = repository.addEntry(sessionId, "barbell_bench_press", orderIndex = 0)
        val setId = repository.addSet(entryId, orderIndex = 0, setType = SetType.WORK)
        val set = repository.getSetsFor(entryId).single()
        repository.updateSet(
            set.copy(weightGrams = 60_000, reps = 8, isCompleted = true, completedAt = clock.nowMillis()),
        )
        repository.finishSession(sessionId, clock.nowMillis() + 1000, rating = null, note = null)

        val lastSets = repository.getLastSetsForExercise("barbell_bench_press")

        assertThat(lastSets.map { it.id }).containsExactly(setId)
        assertThat(lastSets.single().weightGrams).isEqualTo(60_000)
    }

    @Test
    fun entryAndSetCanBeUpdatedAndDeleted() = runBlocking {
        val sessionId = repository.startSession()
        val entryId = repository.addEntry(sessionId, "barbell_bench_press", orderIndex = 0)
        val setId = repository.addSet(entryId, orderIndex = 0)

        val entry = repository.getEntry(entryId)
        assertThat(entry).isNotNull()
        repository.updateEntry(requireNotNull(entry).copy(note = "felt heavy"))
        assertThat(repository.getEntry(entryId)?.note).isEqualTo("felt heavy")

        repository.deleteSet(setId)
        assertThat(repository.getSetsFor(entryId)).isEmpty()

        repository.deleteEntry(entryId)
        assertThat(repository.getEntry(entryId)).isNull()
    }

    @Test
    fun recentSessionHeadersAreBuiltInBatchForThreeSessions() = runBlocking {
        db.muscleGroupDao().insertAll(
            listOf(
                MuscleGroupEntity("chest", "Chest", "Chest", 0, 48, 0),
                MuscleGroupEntity("quads", "Quads", "Quads", 4, 54, 1),
            ),
        )
        db.exerciseDao().insertAll(
            listOf(
                ExerciseEntity("back_squat", "Squat", "Back Squat", 0, "barbell", "quads", 0, 0, 0, 0),
            ),
        )
        db.exerciseDao().insertMuscles(
            listOf(
                ExerciseMuscleEntity("barbell_bench_press", "chest", 0, 1.0),
                ExerciseMuscleEntity("back_squat", "quads", 0, 1.0),
            ),
        )

        // s1: bench warmup 20x10 + work 60x8 completed
        clock.millis = 1_000L
        val s1 = repository.startSession()
        val e1 = repository.addEntry(s1, "barbell_bench_press", 0)
        repository.addSet(e1, 0)
        repository.updateSet(
            repository.getSetsFor(e1).single().copy(
                setType = SetType.WARMUP, weightGrams = 20_000, reps = 10, isCompleted = true, completedAt = 1_000L,
            ),
        )
        val work = repository.addSet(e1, 1)
        repository.updateSet(
            repository.getSetsFor(e1).first { it.id == work }.copy(
                weightGrams = 60_000, reps = 8, isCompleted = true, completedAt = 1_000L,
            ),
        )
        repository.finishSession(s1, 1_100L, rating = null, note = null)

        // s2: squat 100x5 completed
        clock.millis = 2_000L
        val s2 = repository.startSession()
        val e2 = repository.addEntry(s2, "back_squat", 0)
        repository.addSet(e2, 0)
        repository.updateSet(
            repository.getSetsFor(e2).single().copy(
                weightGrams = 100_000, reps = 5, isCompleted = true, completedAt = 2_000L,
            ),
        )
        repository.finishSession(s2, 2_100L, rating = 8, note = null)

        // s3: bench not completed only -> no trained exercise
        clock.millis = 3_000L
        val s3 = repository.startSession()
        val e3 = repository.addEntry(s3, "barbell_bench_press", 0)
        repository.addSet(e3, 0)
        repository.finishSession(s3, 3_100L, rating = null, note = null)

        val headers = repository.observeRecentSessionHeaders(10).first()

        assertThat(headers.map { it.session.id }).containsExactly(s3, s2, s1).inOrder()
        val header1 = headers.first { it.session.id == s1 }
        assertThat(header1.exerciseNames).containsExactly("Bench")
        assertThat(header1.bodyRegions).containsExactly(0)
        assertThat(header1.summary.workSetCount).isEqualTo(1)
        assertThat(header1.summary.totalVolumeGrams).isEqualTo(480_000)
        val header2 = headers.first { it.session.id == s2 }
        assertThat(header2.exerciseNames).containsExactly("Squat")
        assertThat(header2.bodyRegions).containsExactly(4)
        assertThat(header2.summary.totalVolumeGrams).isEqualTo(500_000)
        val header3 = headers.first { it.session.id == s3 }
        assertThat(header3.exerciseNames).isEmpty()
        assertThat(header3.bodyRegions).isEmpty()
        assertThat(header3.summary.workSetCount).isEqualTo(0)
    }

    @Test
    fun startSessionFromTemplateRefusesWhenASessionIsInProgress() {
        runBlocking {
        seedTemplateSource()
        val inProgressId = repository.startSession()

        val result = repository.startSessionFromTemplate("source")

        assertThat(result).isNull()
        assertThat(db.workoutDao().getEntriesFor(inProgressId)).isEmpty()
        assertThat(repository.getSession(inProgressId)?.status).isEqualTo(SessionStatus.IN_PROGRESS)
        assertThat(repository.observeRecentSessions(10).first().map { it.id }).containsExactly("source")
        }
    }

    @Test
    fun startSessionFromTemplateCreatesEntriesAndPlaceholderSets() = runBlocking {
        seedTemplateSource()

        val newSessionId = repository.startSessionFromTemplate("source")

        assertThat(newSessionId).isNotNull()
        val newEntries = repository.getEntriesFor(requireNotNull(newSessionId))
        assertThat(newEntries.map { it.exerciseId }).containsExactly("barbell_bench_press", "back_squat").inOrder()
        assertThat(newEntries.map { it.orderIndex }).containsExactly(0, 1).inOrder()
        val benchEntryId = newEntries.first { it.exerciseId == "barbell_bench_press" }.id
        val benchSets = repository.getSetsFor(benchEntryId)
        // Prefill types come from the source (its own completed sets are the "last time" source).
        assertThat(benchSets.map { it.setType }).containsExactly(SetType.WARMUP, SetType.WORK).inOrder()
        assertThat(benchSets.all { it.weightGrams == null && it.reps == null }).isTrue()
        val squatEntryId = newEntries.first { it.exerciseId == "back_squat" }.id
        assertThat(repository.getSetsFor(squatEntryId)).hasSize(1)
    }

    @Test
    fun batchQueriesHandleMoreThanNineHundredEntries() {
        runBlocking {
        db.workoutDao().insertSession(
            com.ironlog.app.data.db.entity.WorkoutSessionEntity(
                id = "big-session",
                status = 1,
                startedAt = 1_000L,
                endedAt = 2_000L,
                localDate = "2026-01-01",
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
        val entries = (1..1_005).map { index ->
            com.ironlog.app.data.db.entity.SessionExerciseEntity(
                id = "big-entry-$index",
                sessionId = "big-session",
                exerciseId = "barbell_bench_press",
                orderIndex = index,
                note = null,
                createdAt = 0,
                updatedAt = 0,
            )
        }
        db.workoutDao().insertSessionExercises(entries)
        val sets = (1..1_005).map { index ->
            com.ironlog.app.data.db.entity.WorkoutSetEntity(
                id = "big-set-$index",
                sessionExerciseId = "big-entry-$index",
                orderIndex = 0,
                setType = 0,
                parentSetId = null,
                weightG = 60_000,
                inputUnit = 0,
                reps = 8,
                rir = null,
                durationS = null,
                distanceM = null,
                level = null,
                inclineX10 = null,
                speedX10 = null,
                completedAt = 1_000L,
                isCompleted = 1,
                createdAt = 0,
                updatedAt = 0,
            )
        }
        db.workoutDao().insertSets(sets)

        val headers = repository.observeRecentSessionHeaders(5).first()

        assertThat(headers).hasSize(1)
        assertThat(headers.single().summary.workSetCount).isEqualTo(1_005)
        assertThat(headers.single().exerciseNames).containsExactly("Bench")
        }
    }

    private suspend fun seedTemplateSource() {
        db.exerciseDao().insertAll(
            listOf(
                ExerciseEntity("back_squat", "Squat", "Back Squat", 0, "barbell", null, 0, 0, 0, 0),
            ),
        )
        db.workoutDao().insertSession(
            com.ironlog.app.data.db.entity.WorkoutSessionEntity(
                id = "source",
                status = 1,
                startedAt = 0L,
                endedAt = 3_600_000L,
                localDate = "2026-01-01",
                rating = 8,
                note = null,
                restTargetAt = null,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
        db.workoutDao().insertSessionExercises(
            listOf(
                com.ironlog.app.data.db.entity.SessionExerciseEntity(
                    "source-entry-1", "source", "barbell_bench_press", 0, null, 0, 0,
                ),
                com.ironlog.app.data.db.entity.SessionExerciseEntity(
                    "source-entry-2", "source", "back_squat", 1, null, 0, 0,
                ),
            ),
        )
        db.workoutDao().insertSets(
            listOf(
                com.ironlog.app.data.db.entity.WorkoutSetEntity(
                    "source-set-1", "source-entry-1", 0, SetType.WARMUP.toDbValue(), null, 20_000, 0, 10,
                    null, null, null, null, null, null, 0L, 1, 0, 0,
                ),
                com.ironlog.app.data.db.entity.WorkoutSetEntity(
                    "source-set-2", "source-entry-1", 1, SetType.WORK.toDbValue(), null, 60_000, 0, 8,
                    null, null, null, null, null, null, 0L, 1, 0, 0,
                ),
                com.ironlog.app.data.db.entity.WorkoutSetEntity(
                    "source-set-3", "source-entry-2", 0, SetType.WORK.toDbValue(), null, 100_000, 0, 5,
                    null, null, null, null, null, null, 0L, 1, 0, 0,
                ),
            ),
        )
    }

    @Test
    fun deletingSessionCascadesToEntriesAndSets() = runBlocking {
        val sessionId = repository.startSession()
        val entryId = repository.addEntry(sessionId, "barbell_bench_press", orderIndex = 0)
        repository.addSet(entryId, orderIndex = 0)
        repository.addSet(entryId, orderIndex = 1)

        repository.deleteSession(sessionId)

        assertThat(repository.getSession(sessionId)).isNull()
        assertThat(db.workoutDao().getEntriesFor(sessionId)).isEmpty()
        assertThat(db.workoutDao().getSetsFor(entryId)).isEmpty()
    }
}
