package com.ironlog.app.data.db.converter

import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.MuscleRole
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit

/** Enums are stored as their ordinal. Order is frozen by EnumOrderTest. */
fun ExerciseKind.toDbValue(): Int = ordinal
fun exerciseKindFrom(value: Int): ExerciseKind = ExerciseKind.entries[value]

fun MuscleRole.toDbValue(): Int = ordinal
fun muscleRoleFrom(value: Int): MuscleRole = MuscleRole.entries[value]

fun SetType.toDbValue(): Int = ordinal
fun setTypeFrom(value: Int): SetType = SetType.entries[value]

fun SessionStatus.toDbValue(): Int = ordinal
fun sessionStatusFrom(value: Int): SessionStatus = SessionStatus.entries[value]

fun WeightUnit.toDbValue(): Int = ordinal
fun weightUnitFrom(value: Int): WeightUnit = WeightUnit.entries[value]

fun DistanceUnit.toDbValue(): Int = ordinal
fun distanceUnitFrom(value: Int): DistanceUnit = DistanceUnit.entries[value]
