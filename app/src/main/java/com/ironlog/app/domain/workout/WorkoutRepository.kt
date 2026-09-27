package com.ironlog.app.domain.workout

import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.summary.SessionDetail
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.WeekStats
import com.ironlog.app.domain.strength.TrendSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    /** @return the id of the single in-progress session, creating one when none exists */
    suspend fun startSession(): String

    suspend fun getSession(id: String): WorkoutSession?
    fun observeInProgressSession(): Flow<WorkoutSession?>
    fun observeRecentSessions(limit: Int): Flow<List<WorkoutSession>>
    fun observeRecentSessionHeaders(limit: Int): Flow<List<SessionHeader>>
    fun observeWeekStats(weekStartMillis: Long): Flow<WeekStats>
    suspend fun getSessionHeader(sessionId: String): SessionHeader?
    suspend fun getSessionDetail(sessionId: String): SessionDetail?
    suspend fun finishSession(sessionId: String, endedAtMillis: Long, rating: Int?, note: String?)

    /**
     * D7: starts a new session copied from [sourceSessionId] (entries + last-time placeholder sets)
     * in one transaction. Returns null without writing anything when a session is already in progress.
     */
    suspend fun startSessionFromTemplate(sourceSessionId: String): String?

    /** History editing: changes only rating/note, never status or ended_at. */
    suspend fun updateSessionRatingAndNote(sessionId: String, rating: Int?, note: String?)

    suspend fun deleteSession(sessionId: String)

    /** REST_TIMER: wall-clock target of the running rest timer, or null when none. */
    suspend fun setRestTargetAt(sessionId: String, targetAtMillis: Long?)

    suspend fun addEntry(sessionId: String, exerciseId: String, orderIndex: Int, note: String? = null): String
    suspend fun getEntry(id: String): SessionExercise?
    suspend fun getEntriesFor(sessionId: String): List<SessionExercise>
    suspend fun updateEntry(entry: SessionExercise)
    suspend fun deleteEntry(id: String)

    /** Adds an unfinished placeholder set. [inputUnit] is the unit shown when the set was created. */
    suspend fun addSet(
        entryId: String,
        orderIndex: Int,
        setType: SetType = SetType.WORK,
        inputUnit: WeightUnit = WeightUnit.KG,
    ): String

    suspend fun getSetsFor(entryId: String): List<WorkoutSet>
    suspend fun updateSet(set: WorkoutSet)
    suspend fun deleteSet(id: String)

    /** "Last time" sets for prefill, see DOMAIN_RULES.md section 3.1. */
    suspend fun getLastSetsForExercise(exerciseId: String): List<WorkoutSet>

    /** Completed sets of an exercise across finished sessions, with their local dates (M4-T4.4). */
    suspend fun getExerciseTrendSets(exerciseId: String): List<TrendSet>
}
