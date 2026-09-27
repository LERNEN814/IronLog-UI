package com.ironlog.app.domain.exercise

import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeAll(): Flow<List<Exercise>>

    /** @param bodyRegion muscle_group.body_region filter, or null for all regions */
    fun search(query: String, bodyRegion: Int?): Flow<List<Exercise>>

    suspend fun getById(id: String): Exercise?

    fun observeMuscleGroups(): Flow<List<MuscleGroup>>

    /** exerciseId -> startedAt of the most recent session that used it. */
    fun observeLastUsed(): Flow<Map<String, Long>>

    suspend fun getMusclesFor(exerciseId: String): List<ExerciseMuscle>

    /**
     * Creates (id == null) or edits a user-created exercise. Built-in exercises are rejected.
     * @return the exercise id
     */
    suspend fun upsertCustom(
        id: String?,
        nameZh: String,
        nameEn: String,
        kind: ExerciseKind,
        equipment: String,
        primaryMuscleId: String?,
        muscles: List<ExerciseMuscle>,
    ): String

    /** Hides the exercise from pickers without deleting history. */
    suspend fun archive(id: String)

    suspend fun countBuiltIn(): Int
}
