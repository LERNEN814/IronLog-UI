package com.ironlog.app.data.repository

import com.ironlog.app.core.id.IdGenerator
import com.ironlog.app.core.time.Clock
import com.ironlog.app.data.db.chunkedQuery
import com.ironlog.app.data.db.converter.exerciseKindFrom
import com.ironlog.app.data.db.converter.setTypeFrom
import com.ironlog.app.data.db.converter.toDbValue
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.dao.WorkoutDao
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.prefill.PrefillPlanner
import com.ironlog.app.domain.summary.ExerciseMeta
import com.ironlog.app.domain.summary.SessionDetail
import com.ironlog.app.domain.summary.SessionExerciseWithSets
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.WeekStats
import com.ironlog.app.domain.summary.summarize
import com.ironlog.app.domain.strength.TrendSet
import com.ironlog.app.domain.workout.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val muscleGroupDao: MuscleGroupDao,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : WorkoutRepository {

    override suspend fun startSession(): String {
        workoutDao.getInProgressSession()?.let { return it.id }

        val now = clock.nowMillis()
        val id = idGenerator.newId()
        val inserted = workoutDao.insertSessionIfNone(
            WorkoutSessionEntity(
                id = id,
                status = SessionStatus.IN_PROGRESS.toDbValue(),
                startedAt = now,
                endedAt = null,
                localDate = clock.localDateOf(now).toString(),
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = now,
                updatedAt = now,
            ),
        )
        if (inserted) return id
        // Another caller won the transaction; use its session.
        return checkNotNull(workoutDao.getInProgressSession()) { "no in-progress session after insert conflict" }.id
    }

    override suspend fun getSession(id: String): WorkoutSession? =
        workoutDao.getSession(id)?.toDomain()

    override fun observeInProgressSession(): Flow<WorkoutSession?> =
        workoutDao.observeInProgressSession().map { it?.toDomain() }

    override fun observeRecentSessions(limit: Int): Flow<List<WorkoutSession>> =
        workoutDao.observeRecentSessions(limit).map { list -> list.map { it.toDomain() } }

    override fun observeRecentSessionHeaders(limit: Int): Flow<List<SessionHeader>> =
        workoutDao.observeRecentSessions(limit).map { sessions -> buildHeaders(sessions).map { it.header } }

    override fun observeWeekStats(weekStartMillis: Long): Flow<WeekStats> = combine(
        workoutDao.observeFinishedSessionCountSince(weekStartMillis),
        workoutDao.observeCompletedWorkSetCountSince(weekStartMillis),
    ) { sessions, workSets -> WeekStats(sessionCount = sessions, workSetCount = workSets) }

    override suspend fun getSessionHeader(sessionId: String): SessionHeader? {
        val session = workoutDao.getSession(sessionId) ?: return null
        return buildHeaders(listOf(session)).firstOrNull()?.header
    }

    override suspend fun getSessionDetail(sessionId: String): SessionDetail? {
        val session = workoutDao.getSession(sessionId) ?: return null
        val entriesBySession = loadEntriesBySession(listOf(sessionId))
        val data = buildHeaders(listOf(session)).first()
        return SessionDetail(
            header = data.header,
            entries = entriesBySession[sessionId].orEmpty(),
            exerciseNames = data.namesByExercise,
        )
    }

    override suspend fun finishSession(sessionId: String, endedAtMillis: Long, rating: Int?, note: String?) {
        val session = workoutDao.getSession(sessionId) ?: return
        workoutDao.updateSession(
            session.copy(
                status = SessionStatus.FINISHED.toDbValue(),
                endedAt = endedAtMillis,
                rating = rating,
                note = note,
                updatedAt = endedAtMillis,
            ),
        )
    }

    override suspend fun deleteSession(sessionId: String) {
        workoutDao.deleteSession(sessionId)
    }

    override suspend fun startSessionFromTemplate(sourceSessionId: String): String? {
        val sourceEntries = workoutDao.getEntriesFor(sourceSessionId).sortedBy { it.orderIndex }
        val now = clock.nowMillis()
        val newSessionId = idGenerator.newId()
        val entries = mutableListOf<SessionExerciseEntity>()
        val sets = mutableListOf<WorkoutSetEntity>()
        sourceEntries.forEachIndexed { index, source ->
            val newEntryId = idGenerator.newId()
            entries += SessionExerciseEntity(
                id = newEntryId,
                sessionId = newSessionId,
                exerciseId = source.exerciseId,
                orderIndex = index,
                note = null,
                createdAt = now,
                updatedAt = now,
            )
            val exercise = exerciseDao.getById(source.exerciseId)
            val plan = PrefillPlanner.plan(
                lastSets = workoutDao.getLastSetsForExercise(source.exerciseId).map { it.toDomain() },
                kind = exercise?.kind?.let(::exerciseKindFrom) ?: ExerciseKind.STRENGTH,
            )
            plan.forEachIndexed { setIndex, prefill ->
                sets += WorkoutSetEntity(
                    id = idGenerator.newId(),
                    sessionExerciseId = newEntryId,
                    orderIndex = setIndex,
                    setType = prefill.setType.toDbValue(),
                    parentSetId = null,
                    weightG = null,
                    inputUnit = 0,
                    reps = null,
                    rir = null,
                    durationS = null,
                    distanceM = null,
                    level = null,
                    inclineX10 = null,
                    speedX10 = null,
                    completedAt = null,
                    isCompleted = 0,
                    createdAt = now,
                    updatedAt = now,
                )
            }
        }
        val inserted = workoutDao.insertTemplateSession(
            session = WorkoutSessionEntity(
                id = newSessionId,
                status = SessionStatus.IN_PROGRESS.toDbValue(),
                startedAt = now,
                endedAt = null,
                localDate = clock.localDateOf(now).toString(),
                rating = null,
                note = null,
                restTargetAt = null,
                createdAt = now,
                updatedAt = now,
            ),
            entries = entries,
            sets = sets,
        )
        return if (inserted) newSessionId else null
    }

    override suspend fun updateSessionRatingAndNote(sessionId: String, rating: Int?, note: String?) {
        val session = workoutDao.getSession(sessionId) ?: return
        workoutDao.updateSession(session.copy(rating = rating, note = note, updatedAt = clock.nowMillis()))
    }

    override suspend fun setRestTargetAt(sessionId: String, targetAtMillis: Long?) {
        val session = workoutDao.getSession(sessionId) ?: return
        workoutDao.updateSession(
            session.copy(restTargetAt = targetAtMillis, updatedAt = clock.nowMillis()),
        )
    }

    override suspend fun addEntry(
        sessionId: String,
        exerciseId: String,
        orderIndex: Int,
        note: String?,
    ): String {
        val now = clock.nowMillis()
        val id = idGenerator.newId()
        workoutDao.insertSessionExercise(
            SessionExerciseEntity(
                id = id,
                sessionId = sessionId,
                exerciseId = exerciseId,
                orderIndex = orderIndex,
                note = note,
                createdAt = now,
                updatedAt = now,
            ),
        )
        return id
    }

    override suspend fun getEntry(id: String): SessionExercise? =
        workoutDao.getEntry(id)?.toDomain()

    override suspend fun getEntriesFor(sessionId: String): List<SessionExercise> =
        workoutDao.getEntriesFor(sessionId).map { it.toDomain() }

    override suspend fun updateEntry(entry: SessionExercise) {
        workoutDao.updateSessionExercise(entry.copy(updatedAt = clock.nowMillis()).toEntity())
    }

    override suspend fun deleteEntry(id: String) {
        workoutDao.deleteSessionExercise(id)
    }

    override suspend fun addSet(
        entryId: String,
        orderIndex: Int,
        setType: SetType,
        inputUnit: WeightUnit,
    ): String {
        val now = clock.nowMillis()
        val id = idGenerator.newId()
        workoutDao.insertSet(
            WorkoutSetEntity(
                id = id,
                sessionExerciseId = entryId,
                orderIndex = orderIndex,
                setType = setType.toDbValue(),
                parentSetId = null,
                weightG = null,
                inputUnit = inputUnit.toDbValue(),
                reps = null,
                rir = null,
                durationS = null,
                distanceM = null,
                level = null,
                inclineX10 = null,
                speedX10 = null,
                completedAt = null,
                isCompleted = 0,
                createdAt = now,
                updatedAt = now,
            ),
        )
        return id
    }

    override suspend fun getSetsFor(entryId: String): List<WorkoutSet> =
        workoutDao.getSetsFor(entryId).map { it.toDomain() }

    override suspend fun updateSet(set: WorkoutSet) {
        workoutDao.updateSet(set.copy(updatedAt = clock.nowMillis()).toEntity())
    }

    override suspend fun deleteSet(id: String) {
        workoutDao.deleteSet(id)
    }

    override suspend fun getLastSetsForExercise(exerciseId: String): List<WorkoutSet> =
        workoutDao.getLastSetsForExercise(exerciseId).map { it.toDomain() }

    override suspend fun getExerciseTrendSets(exerciseId: String): List<TrendSet> =
        workoutDao.getExerciseHistorySets(exerciseId).map { row ->
            TrendSet(
                localDate = row.localDate,
                sessionId = row.sessionId,
                setType = setTypeFrom(row.set.setType),
                weightGrams = row.set.weightG,
                reps = row.set.reps,
                isCompleted = row.set.isCompleted == 1,
            )
        }

    /**
     * Batch load for N sessions: 3 queries total (entries, sets, exercises + muscle links)
     * instead of 4 per session. Used by the calendar and history lists.
     */
    private suspend fun loadEntriesBySession(sessionIds: List<String>): Map<String, List<SessionExerciseWithSets>> {
        if (sessionIds.isEmpty()) return emptyMap()
        val entryEntities = chunkedQuery(sessionIds) { chunk -> workoutDao.getEntriesForSessions(chunk) }
        val setsByEntry = if (entryEntities.isEmpty()) {
            emptyMap()
        } else {
            chunkedQuery(entryEntities.map { it.id }) { chunk -> workoutDao.getSetsForEntries(chunk) }
                .groupBy { it.sessionExerciseId }
                .mapValues { (_, sets) -> sets.sortedBy { it.orderIndex } }
        }
        return entryEntities
            .groupBy { it.sessionId }
            .mapValues { (_, entries) ->
                entries.sortedBy { it.orderIndex }.map { entry ->
                    SessionExerciseWithSets(
                        entry = entry.toDomain(),
                        sets = setsByEntry[entry.id].orEmpty().map { it.toDomain() },
                    )
                }
            }
    }

    /** Builds list/detail headers for several sessions with a constant number of queries. */
    private suspend fun buildHeaders(entities: List<WorkoutSessionEntity>): List<HeaderData> {
        if (entities.isEmpty()) return emptyList()
        val entriesBySession = loadEntriesBySession(entities.map { it.id })
        val allEntries = entities.flatMap { entriesBySession[it.id].orEmpty() }
        val exerciseIds = allEntries.map { it.entry.exerciseId }.distinct()
        val exercises = if (exerciseIds.isEmpty()) {
            emptyList()
        } else {
            chunkedQuery(exerciseIds) { chunk -> exerciseDao.getByIds(chunk) }
        }
        val muscleLinks = if (exerciseIds.isEmpty()) {
            emptyList()
        } else {
            chunkedQuery(exerciseIds) { chunk -> exerciseDao.getMusclesForExercises(chunk) }
        }
        val muscleGroups = muscleGroupDao.getAll().associateBy { it.id }
        val namesById = exercises.associate { it.id to it.nameZh }
        val metaById = exerciseIds.associateWith { id ->
            ExerciseMeta(
                primaryBodyRegions = muscleLinks
                    .filter { it.exerciseId == id && it.role == 0 }
                    .mapNotNull { muscleGroups[it.muscleId]?.bodyRegion }
                    .distinct(),
            )
        }
        return entities.map { entity ->
            val session = entity.toDomain()
            val entries = entriesBySession[entity.id].orEmpty()
            val trainedEntries = entries.filter { entry -> entry.sets.any { it.isCompleted } }
            val names = trainedEntries.map { it.entry.exerciseId }.distinct().mapNotNull { namesById[it] }
            val summary = summarize(session, entries, metaById, clock.nowMillis())
            HeaderData(
                header = SessionHeader(
                    session = session,
                    exerciseNames = names,
                    bodyRegions = summary.bodyRegions,
                    summary = summary,
                ),
                namesByExercise = namesById,
            )
        }
    }

    private data class HeaderData(
        val header: SessionHeader,
        val namesByExercise: Map<String, String>,
    )
}
