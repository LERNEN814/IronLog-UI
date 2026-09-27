package com.ironlog.app.testutil

import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.summary.SessionDetail
import com.ironlog.app.domain.summary.SessionExerciseWithSets
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.domain.summary.WeekStats
import com.ironlog.app.domain.prefill.PrefillPlanner
import com.ironlog.app.domain.strength.TrendSet
import com.ironlog.app.domain.workout.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory WorkoutRepository for ViewModel tests. Behaviour mirrors the real implementation. */
class FakeWorkoutRepository : WorkoutRepository {

    val sessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val entries = MutableStateFlow<List<SessionExercise>>(emptyList())
    val sets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val weekStatsFlow = MutableStateFlow(WeekStats(0, 0))
    val sessionHeadersFlow = MutableStateFlow<List<SessionHeader>>(emptyList())
    val lastSetsByExercise = mutableMapOf<String, List<WorkoutSet>>()
    var createdAtMillis = 0L

    private var counter = 0

    private fun nextId(prefix: String): String {
        counter += 1
        return "$prefix-$counter"
    }

    override suspend fun startSession(): String {
        sessions.value.firstOrNull { it.status == SessionStatus.IN_PROGRESS }?.let { return it.id }
        val id = nextId("session")
        sessions.value = sessions.value + WorkoutSession(
            id = id,
            status = SessionStatus.IN_PROGRESS,
            startedAt = createdAtMillis,
            endedAt = null,
            localDate = "2026-01-01",
            rating = null,
            note = null,
            restTargetAt = null,
            createdAt = createdAtMillis,
            updatedAt = createdAtMillis,
        )
        return id
    }

    override suspend fun getSession(id: String): WorkoutSession? =
        sessions.value.firstOrNull { it.id == id }

    override fun observeInProgressSession(): Flow<WorkoutSession?> =
        sessions.map { list -> list.firstOrNull { it.status == SessionStatus.IN_PROGRESS } }

    override fun observeRecentSessions(limit: Int): Flow<List<WorkoutSession>> = sessions.map { list ->
        list.filter { it.status == SessionStatus.FINISHED }
            .sortedByDescending { it.startedAt }
            .take(limit)
    }

    override fun observeRecentSessionHeaders(limit: Int): Flow<List<SessionHeader>> =
        sessionHeadersFlow.map { it.take(limit) }

    override fun observeWeekStats(weekStartMillis: Long): Flow<WeekStats> = weekStatsFlow

    override suspend fun getSessionHeader(sessionId: String): SessionHeader? =
        sessionHeadersFlow.value.firstOrNull { it.session.id == sessionId }

    override suspend fun getSessionDetail(sessionId: String): SessionDetail? {
        val session = getSession(sessionId) ?: return null
        val sessionEntries = getEntriesFor(sessionId).map { entry ->
            SessionExerciseWithSets(entry = entry, sets = getSetsFor(entry.id))
        }
        val header = SessionHeader(
            session = session,
            exerciseNames = emptyList(),
            bodyRegions = emptySet(),
            summary = SessionSummary(0, 0, 0, 0, emptySet()),
        )
        return SessionDetail(header = header, entries = sessionEntries, exerciseNames = emptyMap())
    }

    override suspend fun finishSession(sessionId: String, endedAtMillis: Long, rating: Int?, note: String?) {
        sessions.value = sessions.value.map {
            if (it.id == sessionId) {
                it.copy(
                    status = SessionStatus.FINISHED,
                    endedAt = endedAtMillis,
                    rating = rating,
                    note = note,
                    updatedAt = endedAtMillis,
                )
            } else {
                it
            }
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        val entryIds = entries.value.filter { it.sessionId == sessionId }.map { it.id }.toSet()
        sessions.value = sessions.value.filterNot { it.id == sessionId }
        entries.value = entries.value.filterNot { it.sessionId == sessionId }
        sets.value = sets.value.filterNot { it.sessionExerciseId in entryIds }
    }

    override suspend fun startSessionFromTemplate(sourceSessionId: String): String? {
        if (sessions.value.any { it.status == SessionStatus.IN_PROGRESS }) return null
        val sourceEntries = entries.value.filter { it.sessionId == sourceSessionId }.sortedBy { it.orderIndex }
        val newSessionId = nextId("session")
        sessions.value = sessions.value + WorkoutSession(
            id = newSessionId,
            status = SessionStatus.IN_PROGRESS,
            startedAt = createdAtMillis,
            endedAt = null,
            localDate = "2026-01-01",
            rating = null,
            note = null,
            restTargetAt = null,
            createdAt = createdAtMillis,
            updatedAt = createdAtMillis,
        )
        sourceEntries.forEachIndexed { index, source ->
            val newEntryId = nextId("entry")
            entries.value = entries.value + SessionExercise(
                id = newEntryId,
                sessionId = newSessionId,
                exerciseId = source.exerciseId,
                orderIndex = index,
                note = null,
                createdAt = createdAtMillis,
                updatedAt = createdAtMillis,
            )
            val plan = PrefillPlanner.plan(
                lastSets = lastSetsByExercise[source.exerciseId] ?: emptyList(),
                kind = ExerciseKind.STRENGTH,
            )
            plan.forEachIndexed { setIndex, prefill ->
                sets.value = sets.value + WorkoutSet(
                    id = nextId("set"),
                    sessionExerciseId = newEntryId,
                    orderIndex = setIndex,
                    setType = prefill.setType,
                    parentSetId = null,
                    weightGrams = null,
                    inputUnit = WeightUnit.KG,
                    reps = null,
                    rir = null,
                    durationS = null,
                    distanceM = null,
                    level = null,
                    inclineX10 = null,
                    speedX10 = null,
                    completedAt = null,
                    isCompleted = false,
                    createdAt = createdAtMillis,
                    updatedAt = createdAtMillis,
                )
            }
        }
        return newSessionId
    }

    override suspend fun setRestTargetAt(sessionId: String, targetAtMillis: Long?) {
        sessions.value = sessions.value.map {
            if (it.id == sessionId) it.copy(restTargetAt = targetAtMillis) else it
        }
    }

    override suspend fun updateSessionRatingAndNote(sessionId: String, rating: Int?, note: String?) {
        sessions.value = sessions.value.map {
            if (it.id == sessionId) it.copy(rating = rating, note = note) else it
        }
    }

    override suspend fun addEntry(sessionId: String, exerciseId: String, orderIndex: Int, note: String?): String {
        val id = nextId("entry")
        entries.value = entries.value + SessionExercise(
            id = id,
            sessionId = sessionId,
            exerciseId = exerciseId,
            orderIndex = orderIndex,
            note = note,
            createdAt = createdAtMillis,
            updatedAt = createdAtMillis,
        )
        return id
    }

    override suspend fun getEntry(id: String): SessionExercise? =
        entries.value.firstOrNull { it.id == id }

    override suspend fun getEntriesFor(sessionId: String): List<SessionExercise> =
        entries.value.filter { it.sessionId == sessionId }.sortedBy { it.orderIndex }

    override suspend fun updateEntry(entry: SessionExercise) {
        entries.value = entries.value.map { if (it.id == entry.id) entry else it }
    }

    override suspend fun deleteEntry(id: String) {
        entries.value = entries.value.filterNot { it.id == id }
        sets.value = sets.value.filterNot { it.sessionExerciseId == id }
    }

    override suspend fun addSet(
        entryId: String,
        orderIndex: Int,
        setType: SetType,
        inputUnit: WeightUnit,
    ): String {
        val id = nextId("set")
        sets.value = sets.value + WorkoutSet(
            id = id,
            sessionExerciseId = entryId,
            orderIndex = orderIndex,
            setType = setType,
            parentSetId = null,
            weightGrams = null,
            inputUnit = inputUnit,
            reps = null,
            rir = null,
            durationS = null,
            distanceM = null,
            level = null,
            inclineX10 = null,
            speedX10 = null,
            completedAt = null,
            isCompleted = false,
            createdAt = createdAtMillis,
            updatedAt = createdAtMillis,
        )
        return id
    }

    override suspend fun getSetsFor(entryId: String): List<WorkoutSet> =
        sets.value.filter { it.sessionExerciseId == entryId }.sortedBy { it.orderIndex }

    override suspend fun updateSet(set: WorkoutSet) {
        sets.value = sets.value.map { if (it.id == set.id) set else it }
    }

    override suspend fun deleteSet(id: String) {
        sets.value = sets.value.filterNot { it.id == id }
    }

    override suspend fun getLastSetsForExercise(exerciseId: String): List<WorkoutSet> =
        lastSetsByExercise[exerciseId] ?: emptyList()

    override suspend fun getExerciseTrendSets(exerciseId: String): List<TrendSet> {
        val sessionsById = sessions.value.associateBy { it.id }
        return entries.value
            .filter { it.exerciseId == exerciseId }
            .flatMap { entry ->
                val session = sessionsById[entry.sessionId]
                if (session == null || session.status != SessionStatus.FINISHED) {
                    emptyList()
                } else {
                    sets.value
                        .filter { it.sessionExerciseId == entry.id }
                        .map { set ->
                            TrendSet(
                                localDate = session.localDate,
                                sessionId = session.id,
                                setType = set.setType,
                                weightGrams = set.weightGrams,
                                reps = set.reps,
                                isCompleted = set.isCompleted,
                            )
                        }
                }
            }
    }
}
