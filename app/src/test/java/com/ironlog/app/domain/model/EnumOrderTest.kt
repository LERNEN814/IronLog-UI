package com.ironlog.app.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * DATA_MODEL.md section 2 freezes enum order and ordinals: they are stored in the database,
 * so inserting or reordering entries would silently corrupt existing rows.
 */
class EnumOrderTest {

    @Test
    fun exerciseKindOrder() {
        assertThat(ExerciseKind.entries.map { it.name })
            .containsExactly("STRENGTH", "CARDIO", "BODYWEIGHT", "TIMED").inOrder()
        assertThat(ExerciseKind.entries.map { it.ordinal }).containsExactly(0, 1, 2, 3).inOrder()
    }

    @Test
    fun muscleRoleOrder() {
        assertThat(MuscleRole.entries.map { it.name })
            .containsExactly("PRIMARY", "SECONDARY").inOrder()
        assertThat(MuscleRole.entries.map { it.ordinal }).containsExactly(0, 1).inOrder()
    }

    @Test
    fun setTypeOrder() {
        assertThat(SetType.entries.map { it.name })
            .containsExactly("WORK", "WARMUP", "DROP", "FAILURE").inOrder()
        assertThat(SetType.entries.map { it.ordinal }).containsExactly(0, 1, 2, 3).inOrder()
    }

    @Test
    fun sessionStatusOrder() {
        assertThat(SessionStatus.entries.map { it.name })
            .containsExactly("IN_PROGRESS", "FINISHED").inOrder()
        assertThat(SessionStatus.entries.map { it.ordinal }).containsExactly(0, 1).inOrder()
    }

    @Test
    fun weightUnitOrder() {
        assertThat(WeightUnit.entries.map { it.name }).containsExactly("KG", "LB").inOrder()
        assertThat(WeightUnit.entries.map { it.ordinal }).containsExactly(0, 1).inOrder()
    }

    @Test
    fun distanceUnitOrder() {
        assertThat(DistanceUnit.entries.map { it.name }).containsExactly("KM", "MI").inOrder()
        assertThat(DistanceUnit.entries.map { it.ordinal }).containsExactly(0, 1).inOrder()
    }
}
