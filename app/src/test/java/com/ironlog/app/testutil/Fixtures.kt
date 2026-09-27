package com.ironlog.app.testutil

import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet

fun testSet(
    id: String = "set-1",
    sessionExerciseId: String = "entry-1",
    orderIndex: Int = 0,
    type: SetType = SetType.WORK,
    weightGrams: Int? = null,
    reps: Int? = null,
    rir: Int? = null,
    durationS: Int? = null,
    distanceM: Int? = null,
    level: Int? = null,
    inclineX10: Int? = null,
    speedX10: Int? = null,
    isCompleted: Boolean = true,
    completedAt: Long? = null,
): WorkoutSet = WorkoutSet(
    id = id,
    sessionExerciseId = sessionExerciseId,
    orderIndex = orderIndex,
    setType = type,
    parentSetId = null,
    weightGrams = weightGrams,
    inputUnit = WeightUnit.KG,
    reps = reps,
    rir = rir,
    durationS = durationS,
    distanceM = distanceM,
    level = level,
    inclineX10 = inclineX10,
    speedX10 = speedX10,
    completedAt = completedAt,
    isCompleted = isCompleted,
    createdAt = 0,
    updatedAt = 0,
)

fun testEntry(
    id: String = "entry-1",
    sessionId: String = "session-1",
    exerciseId: String = "barbell_bench_press",
    orderIndex: Int = 0,
): SessionExercise = SessionExercise(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    orderIndex = orderIndex,
    note = null,
    createdAt = 0,
    updatedAt = 0,
)

fun testSession(
    id: String = "session-1",
    status: SessionStatus = SessionStatus.FINISHED,
    startedAt: Long = 0,
    endedAt: Long? = 3_600_000,
    localDate: String = "2026-01-01",
): WorkoutSession = WorkoutSession(
    id = id,
    status = status,
    startedAt = startedAt,
    endedAt = endedAt,
    localDate = localDate,
    rating = null,
    note = null,
    restTargetAt = null,
    createdAt = startedAt,
    updatedAt = endedAt ?: startedAt,
)
