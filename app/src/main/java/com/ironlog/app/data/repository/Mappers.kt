package com.ironlog.app.data.repository

import com.ironlog.app.data.db.converter.exerciseKindFrom
import com.ironlog.app.data.db.converter.muscleRoleFrom
import com.ironlog.app.data.db.converter.sessionStatusFrom
import com.ironlog.app.data.db.converter.setTypeFrom
import com.ironlog.app.data.db.converter.toDbValue
import com.ironlog.app.data.db.converter.weightUnitFrom
import com.ironlog.app.data.db.entity.BodyWeightEntity
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity
import com.ironlog.app.domain.model.BodyWeight
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet

/** Entity <-> domain mappings. Booleans and enums are stored as Int in the database. */

fun MuscleGroupEntity.toDomain(): MuscleGroup = MuscleGroup(
    id = id,
    displayNameEn = displayNameEn,
    displayNameZh = displayNameZh,
    bodyRegion = bodyRegion,
    recoveryHalfLifeHours = recoveryHalfLifeHours,
    sortOrder = sortOrder,
)

fun MuscleGroup.toEntity(): MuscleGroupEntity = MuscleGroupEntity(
    id = id,
    displayNameEn = displayNameEn,
    displayNameZh = displayNameZh,
    bodyRegion = bodyRegion,
    recoveryHalfLifeHours = recoveryHalfLifeHours,
    sortOrder = sortOrder,
)

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    nameZh = nameZh,
    nameEn = nameEn,
    kind = exerciseKindFrom(kind),
    equipment = equipment,
    primaryMuscleId = primaryMuscleId,
    isCustom = isCustom == 1,
    isArchived = isArchived == 1,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    nameZh = nameZh,
    nameEn = nameEn,
    kind = kind.toDbValue(),
    equipment = equipment,
    primaryMuscleId = primaryMuscleId,
    isCustom = if (isCustom) 1 else 0,
    isArchived = if (isArchived) 1 else 0,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ExerciseMuscleEntity.toDomain(): ExerciseMuscle = ExerciseMuscle(
    exerciseId = exerciseId,
    muscleId = muscleId,
    role = muscleRoleFrom(role),
    weight = weight,
)

fun ExerciseMuscle.toEntity(): ExerciseMuscleEntity = ExerciseMuscleEntity(
    exerciseId = exerciseId,
    muscleId = muscleId,
    role = role.toDbValue(),
    weight = weight,
)

fun WorkoutSessionEntity.toDomain(): WorkoutSession = WorkoutSession(
    id = id,
    status = sessionStatusFrom(status),
    startedAt = startedAt,
    endedAt = endedAt,
    localDate = localDate,
    rating = rating,
    note = note,
    restTargetAt = restTargetAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun WorkoutSession.toEntity(): WorkoutSessionEntity = WorkoutSessionEntity(
    id = id,
    status = status.toDbValue(),
    startedAt = startedAt,
    endedAt = endedAt,
    localDate = localDate,
    rating = rating,
    note = note,
    restTargetAt = restTargetAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SessionExerciseEntity.toDomain(): SessionExercise = SessionExercise(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    orderIndex = orderIndex,
    note = note,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SessionExercise.toEntity(): SessionExerciseEntity = SessionExerciseEntity(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    orderIndex = orderIndex,
    note = note,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun WorkoutSetEntity.toDomain(): WorkoutSet = WorkoutSet(
    id = id,
    sessionExerciseId = sessionExerciseId,
    orderIndex = orderIndex,
    setType = setTypeFrom(setType),
    parentSetId = parentSetId,
    weightGrams = weightG,
    inputUnit = weightUnitFrom(inputUnit),
    reps = reps,
    rir = rir,
    durationS = durationS,
    distanceM = distanceM,
    level = level,
    inclineX10 = inclineX10,
    speedX10 = speedX10,
    completedAt = completedAt,
    isCompleted = isCompleted == 1,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun WorkoutSet.toEntity(): WorkoutSetEntity = WorkoutSetEntity(
    id = id,
    sessionExerciseId = sessionExerciseId,
    orderIndex = orderIndex,
    setType = setType.toDbValue(),
    parentSetId = parentSetId,
    weightG = weightGrams,
    inputUnit = inputUnit.toDbValue(),
    reps = reps,
    rir = rir,
    durationS = durationS,
    distanceM = distanceM,
    level = level,
    inclineX10 = inclineX10,
    speedX10 = speedX10,
    completedAt = completedAt,
    isCompleted = if (isCompleted) 1 else 0,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun BodyWeightEntity.toDomain(): BodyWeight = BodyWeight(
    id = id,
    localDate = localDate,
    weightGrams = weightG,
    recordedAt = recordedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun BodyWeight.toEntity(): BodyWeightEntity = BodyWeightEntity(
    id = id,
    localDate = localDate,
    weightG = weightGrams,
    recordedAt = recordedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
