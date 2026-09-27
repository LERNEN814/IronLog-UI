package com.ironlog.app.domain.summary

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.testutil.testEntry
import com.ironlog.app.testutil.testSession
import com.ironlog.app.testutil.testSet
import org.junit.Test

/** DOMAIN_RULES.md section 4 vectors SM1-SM3 plus duration behaviour. */
class SessionSummaryTest {

    private val benchMeta = mapOf("barbell_bench_press" to ExerciseMeta(primaryBodyRegions = listOf(0)))
    private val cardioMeta = mapOf("treadmill_run" to ExerciseMeta(primaryBodyRegions = listOf(6)))

    @Test
    fun sm1_countsOnlyWorkSetsForVolume() {
        val entries = listOf(
            SessionExerciseWithSets(
                entry = testEntry(exerciseId = "barbell_bench_press"),
                sets = listOf(
                    testSet(type = SetType.WARMUP, weightGrams = 20_000, reps = 10),
                    testSet(type = SetType.WORK, weightGrams = 60_000, reps = 8),
                    testSet(type = SetType.WORK, weightGrams = 60_000, reps = 8),
                    testSet(type = SetType.WORK, weightGrams = 60_000, reps = 8),
                ),
            ),
        )

        val summary = summarize(testSession(), entries, benchMeta, nowMillis = 99_999)

        assertThat(summary.durationS).isEqualTo(3_600)
        assertThat(summary.exerciseCount).isEqualTo(1)
        assertThat(summary.workSetCount).isEqualTo(3)
        assertThat(summary.totalVolumeGrams).isEqualTo(1_440_000)
        assertThat(summary.bodyRegions).containsExactly(0)
    }

    @Test
    fun sm2_incompleteSetsAreIgnored() {
        val entries = listOf(
            SessionExerciseWithSets(
                entry = testEntry(exerciseId = "barbell_bench_press"),
                sets = listOf(
                    testSet(type = SetType.WORK, weightGrams = 60_000, reps = 8),
                    testSet(type = SetType.WORK, weightGrams = 100_000, reps = 5, isCompleted = false),
                ),
            ),
        )

        val summary = summarize(testSession(), entries, benchMeta, nowMillis = 99_999)

        assertThat(summary.workSetCount).isEqualTo(1)
        assertThat(summary.totalVolumeGrams).isEqualTo(480_000)
    }

    @Test
    fun sm3_cardioHasNoVolume() {
        val entries = listOf(
            SessionExerciseWithSets(
                entry = testEntry(exerciseId = "treadmill_run"),
                sets = listOf(
                    testSet(type = SetType.WORK, weightGrams = null, reps = null, durationS = 1_200),
                ),
            ),
        )

        val summary = summarize(testSession(), entries, cardioMeta, nowMillis = 99_999)

        assertThat(summary.exerciseCount).isEqualTo(1)
        assertThat(summary.workSetCount).isEqualTo(1)
        assertThat(summary.totalVolumeGrams).isEqualTo(0)
        assertThat(summary.bodyRegions).containsExactly(6)
    }

    @Test
    fun inProgressSessionUsesProvidedNow() {
        val session = testSession(
            status = SessionStatus.IN_PROGRESS,
            startedAt = 1_000_000,
            endedAt = null,
        )

        val summary = summarize(session, emptyList(), emptyMap(), nowMillis = 1_090_000)

        assertThat(summary.durationS).isEqualTo(90)
    }

    @Test
    fun sm4_onlyEntriesWithCompletedSetsCountAsTrained() {
        val entries = listOf(
            SessionExerciseWithSets(
                entry = testEntry(id = "e1", exerciseId = "barbell_bench_press"),
                sets = listOf(testSet(type = SetType.WORK, weightGrams = 60_000, reps = 8)),
            ),
            SessionExerciseWithSets(
                entry = testEntry(id = "e2", exerciseId = "back_squat"),
                sets = listOf(testSet(type = SetType.WORK, weightGrams = 100_000, reps = 5, isCompleted = false)),
            ),
        )
        val meta = benchMeta + ("back_squat" to ExerciseMeta(primaryBodyRegions = listOf(4)))

        val summary = summarize(testSession(), entries, meta, nowMillis = 99_999)

        assertThat(summary.exerciseCount).isEqualTo(1)
        assertThat(summary.bodyRegions).containsExactly(0)
    }

    @Test
    fun sm5_warmupOnlyEntryCountsAsTrainedButAddsNoWorkSets() {
        val entries = listOf(
            SessionExerciseWithSets(
                entry = testEntry(exerciseId = "barbell_bench_press"),
                sets = listOf(testSet(type = SetType.WARMUP, weightGrams = 20_000, reps = 10)),
            ),
        )

        val summary = summarize(testSession(), entries, benchMeta, nowMillis = 99_999)

        assertThat(summary.exerciseCount).isEqualTo(1)
        assertThat(summary.workSetCount).isEqualTo(0)
        assertThat(summary.totalVolumeGrams).isEqualTo(0)
        assertThat(summary.bodyRegions).containsExactly(0)
    }
}
