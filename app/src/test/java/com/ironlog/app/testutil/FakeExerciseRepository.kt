package com.ironlog.app.testutil

import com.ironlog.app.domain.exercise.ExerciseFilter
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.ExerciseMuscle
import com.ironlog.app.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/** In-memory ExerciseRepository for ViewModel tests. */
class FakeExerciseRepository : ExerciseRepository {

    val exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val muscleGroups = MutableStateFlow<List<MuscleGroup>>(emptyList())
    val lastUsed = MutableStateFlow<Map<String, Long>>(emptyMap())
    val muscleLinks = mutableMapOf<String, List<ExerciseMuscle>>()

    private var counter = 0

    override fun observeAll(): Flow<List<Exercise>> =
        exercises.map { list -> list.filterNot { it.isArchived } }

    override fun search(query: String, bodyRegion: Int?): Flow<List<Exercise>> =
        combine(observeAll(), muscleGroups) { list, groups ->
            ExerciseFilter.filter(
                exercises = list,
                bodyRegionByMuscleId = groups.associate { it.id to it.bodyRegion },
                query = query,
                bodyRegion = bodyRegion,
            )
        }

    override suspend fun getById(id: String): Exercise? =
        exercises.value.firstOrNull { it.id == id }

    override fun observeMuscleGroups(): Flow<List<MuscleGroup>> = muscleGroups

    override fun observeLastUsed(): Flow<Map<String, Long>> = lastUsed

    override suspend fun getMusclesFor(exerciseId: String): List<ExerciseMuscle> =
        muscleLinks[exerciseId] ?: emptyList()

    override suspend fun upsertCustom(
        id: String?,
        nameZh: String,
        nameEn: String,
        kind: ExerciseKind,
        equipment: String,
        primaryMuscleId: String?,
        muscles: List<ExerciseMuscle>,
    ): String {
        val exerciseId = id ?: "custom-${++counter}"
        val existing = exercises.value.firstOrNull { it.id == exerciseId }
        require(existing == null || existing.isCustom) { "built-in exercise cannot be edited: $exerciseId" }
        val exercise = Exercise(
            id = exerciseId,
            nameZh = nameZh,
            nameEn = nameEn,
            kind = kind,
            equipment = equipment,
            primaryMuscleId = primaryMuscleId,
            isCustom = true,
            isArchived = existing?.isArchived ?: false,
            createdAt = existing?.createdAt ?: 0L,
            updatedAt = 0L,
        )
        exercises.value = if (existing == null) {
            exercises.value + exercise
        } else {
            exercises.value.map { if (it.id == exerciseId) exercise else it }
        }
        muscleLinks[exerciseId] = muscles.map { it.copy(exerciseId = exerciseId) }
        return exerciseId
    }

    override suspend fun archive(id: String) {
        exercises.value = exercises.value.map { if (it.id == id) it.copy(isArchived = true) else it }
    }

    override suspend fun countBuiltIn(): Int = exercises.value.count { !it.isCustom }
}
