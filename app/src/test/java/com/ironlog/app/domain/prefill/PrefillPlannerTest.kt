package com.ironlog.app.domain.prefill

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.testutil.testSet
import org.junit.Test

/** DOMAIN_RULES.md section 3.2 vectors P1-P4. */
class PrefillPlannerTest {

    @Test
    fun p1_emptyHistoryGivesOneEmptyWorkPlaceholder() {
        val plan = PrefillPlanner.plan(emptyList(), ExerciseKind.STRENGTH)

        assertThat(plan).containsExactly(
            PrefillSet(SetType.WORK, null, null, null, null, null, null, null),
        )
    }

    @Test
    fun p2_copiesAllSetsKeepingOrderAndTypes() {
        val lastSets = listOf(
            testSet(id = "1", type = SetType.WARMUP, weightGrams = 20_000, reps = 10),
            testSet(id = "2", type = SetType.WORK, weightGrams = 60_000, reps = 8),
            testSet(id = "3", type = SetType.WORK, weightGrams = 60_000, reps = 8),
            testSet(id = "4", type = SetType.WORK, weightGrams = 60_000, reps = 7),
        )

        val plan = PrefillPlanner.plan(lastSets, ExerciseKind.STRENGTH)

        assertThat(plan).containsExactly(
            PrefillSet(SetType.WARMUP, 20_000, 10, null, null, null, null, null),
            PrefillSet(SetType.WORK, 60_000, 8, null, null, null, null, null),
            PrefillSet(SetType.WORK, 60_000, 8, null, null, null, null, null),
            PrefillSet(SetType.WORK, 60_000, 7, null, null, null, null, null),
        ).inOrder()
    }

    @Test
    fun p3_dropSetIsCopiedWithoutRirOrParent() {
        val lastSets = listOf(
            testSet(id = "1", type = SetType.WORK, weightGrams = 100_000, reps = 5, rir = 2),
            testSet(id = "2", type = SetType.DROP, weightGrams = 80_000, reps = 8),
        )

        val plan = PrefillPlanner.plan(lastSets, ExerciseKind.STRENGTH)

        assertThat(plan).containsExactly(
            PrefillSet(SetType.WORK, 100_000, 5, null, null, null, null, null),
            PrefillSet(SetType.DROP, 80_000, 8, null, null, null, null, null),
        ).inOrder()
    }

    @Test
    fun p4_cardioFieldsAreCopiedWithNullWeightAndReps() {
        val lastSets = listOf(
            testSet(
                type = SetType.WORK,
                weightGrams = null,
                reps = null,
                durationS = 1_200,
                level = 8,
            ),
        )

        val plan = PrefillPlanner.plan(lastSets, ExerciseKind.CARDIO)

        assertThat(plan).containsExactly(
            PrefillSet(SetType.WORK, null, null, 1_200, 8, null, null, null),
        )
    }
}
