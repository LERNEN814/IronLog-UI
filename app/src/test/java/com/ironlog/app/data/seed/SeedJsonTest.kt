package com.ironlog.app.data.seed

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.ironlog.app.testutil.ProjectFiles
import kotlinx.serialization.json.Json
import org.junit.Test

/** Validates the bundled seed file without touching Android or Room. */
class SeedJsonTest {

    private val seed: SeedFile = Json.decodeFromString(
        SeedFile.serializer(),
        ProjectFiles.file("app/src/main/assets/seed/seed_v1.json").readText(),
    )

    @Test
    fun fileDeclaresVersionOne() {
        assertThat(seed.version).isEqualTo(1)
    }

    @Test
    fun hasSixteenMuscles() {
        assertThat(seed.muscles).hasSize(16)
    }

    @Test
    fun muscleIdsAreUnique() {
        assertThat(seed.muscles.map { it.id }).containsNoDuplicates()
    }

    @Test
    fun exerciseIdsAreUnique() {
        assertThat(seed.exercises.map { it.id }).containsNoDuplicates()
    }

    @Test
    fun hasAtLeastSixtyExercises() {
        assertThat(seed.exercises.size).isAtLeast(60)
    }

    @Test
    fun everyExerciseMuscleExists() {
        val muscleIds = seed.muscles.map { it.id }.toSet()
        seed.exercises.forEach { exercise ->
            exercise.muscles.forEach { mapping ->
                assertWithMessage("%s -> %s", exercise.id, mapping.muscleId)
                    .that(muscleIds).contains(mapping.muscleId)
            }
        }
    }

    @Test
    fun everyExerciseHasPrimaryMuscle() {
        seed.exercises.forEach { exercise ->
            assertWithMessage("%s primary role", exercise.id)
                .that(exercise.muscles.any { it.role == 0 }).isTrue()
        }
    }

    @Test
    fun primaryMuscleIdsExist() {
        val muscleIds = seed.muscles.map { it.id }.toSet()
        seed.exercises.forEach { exercise ->
            assertWithMessage("%s primary_muscle_id", exercise.id)
                .that(muscleIds).contains(exercise.primaryMuscleId)
        }
    }

    @Test
    fun exerciseMusclePairsAreUnique() {
        seed.exercises.forEach { exercise ->
            assertWithMessage("%s muscle pairs", exercise.id)
                .that(exercise.muscles.map { it.muscleId }).containsNoDuplicates()
        }
    }

    @Test
    fun muscleHalfLivesMatchSpec() {
        val halfLives = seed.muscles.associate { it.id to it.recoveryHalfLifeHours }
        assertThat(halfLives["chest"]).isEqualTo(48)
        assertThat(halfLives["triceps"]).isEqualTo(30)
        assertThat(halfLives["front_delts"]).isEqualTo(36)
        assertThat(halfLives["cardio"]).isEqualTo(12)
    }

    @Test
    fun includesEllipticalTrainer() {
        val elliptical = seed.exercises.firstOrNull { it.id == "elliptical_trainer" }
        assertThat(elliptical).isNotNull()
        assertThat(elliptical?.kind).isEqualTo(1)
        assertThat(elliptical?.equipment).isEqualTo("elliptical")
        assertThat(elliptical?.primaryMuscleId).isEqualTo("cardio")
        assertThat(elliptical?.muscles?.firstOrNull { it.role == 0 }?.muscleId).isEqualTo("cardio")
    }
}
