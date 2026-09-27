package com.ironlog.app.domain.fatigue

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import org.junit.Test

/** DOMAIN_RULES.md section 5.2 vectors F1-F12. Raw values are checked with 1e-3 tolerance. */
class FatigueModelTest {

    private val now = 1_000_000_000_000L
    private val hour = 3_600_000L

    private val benchMuscles = listOf("chest" to 1.0, "triceps" to 0.5, "front_delts" to 0.5)
    private val squatMuscles = listOf("quads" to 1.0, "glutes" to 1.0, "hamstrings" to 0.5)
    private val cardioMuscles = listOf("cardio" to 1.0)

    private val halfLives = mapOf(
        "chest" to 48,
        "triceps" to 30,
        "front_delts" to 36,
        "quads" to 54,
        "glutes" to 54,
        "hamstrings" to 54,
        "cardio" to 12,
    )

    private fun benchSet(
        completedAt: Long = now,
        type: SetType = SetType.WORK,
        rir: Int? = null,
    ) = FatigueInputSet(completedAt, ExerciseKind.STRENGTH, type, rir, null, benchMuscles)

    private fun squatSet(completedAt: Long = now) =
        FatigueInputSet(completedAt, ExerciseKind.STRENGTH, SetType.WORK, null, null, squatMuscles)

    private fun cardioSet(durationS: Int, completedAt: Long = now) =
        FatigueInputSet(completedAt, ExerciseKind.CARDIO, SetType.WORK, null, durationS, cardioMuscles)

    private fun rawOf(map: Map<String, Double>, key: String): Double =
        requireNotNull(map[key]) { "missing $key in $map" }

    @Test
    fun f1_fiveWorkSetsToday() {
        val sets = List(5) { benchSet() }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 50, "triceps", 25, "front_delts", 25)
        val raw = FatigueModel.computeRaw(sets, now, halfLives)
        assertThat(rawOf(raw, "chest")).isWithin(1e-3).of(5.0)
        assertThat(rawOf(raw, "triceps")).isWithin(1e-3).of(2.5)
        assertThat(rawOf(raw, "front_delts")).isWithin(1e-3).of(2.5)
    }

    @Test
    fun f2_fiveWorkSetsTwoDaysAgo() {
        val sets = List(5) { benchSet(completedAt = now - 48 * hour) }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 25, "triceps", 8, "front_delts", 10)
        val raw = FatigueModel.computeRaw(sets, now, halfLives)
        assertThat(rawOf(raw, "chest")).isWithin(1e-3).of(2.5)
        assertThat(rawOf(raw, "triceps")).isWithin(1e-3).of(0.8247)
        assertThat(rawOf(raw, "front_delts")).isWithin(1e-3).of(0.9921)
    }

    @Test
    fun f3_warmupSetsAddNoStimulus() {
        val sets = List(3) { benchSet(type = SetType.WARMUP) } + List(3) { benchSet() }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 30, "triceps", 15, "front_delts", 15)
    }

    @Test
    fun f4_fourSetsThreeDaysAgoPlusThreeToday() {
        val sets = List(4) { benchSet(completedAt = now - 72 * hour) } + List(3) { benchSet() }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 44, "triceps", 19, "front_delts", 20)
        val raw = FatigueModel.computeRaw(sets, now, halfLives)
        assertThat(rawOf(raw, "chest")).isWithin(1e-3).of(4.4142)
        assertThat(rawOf(raw, "triceps")).isWithin(1e-3).of(1.8789)
        assertThat(rawOf(raw, "front_delts")).isWithin(1e-3).of(2.0)
    }

    @Test
    fun f5_rirScalesStimulus() {
        val sets = listOf(
            benchSet(rir = 1),
            benchSet(rir = 1),
            benchSet(rir = 3),
            benchSet(rir = 3),
            benchSet(rir = 5),
        )

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 42, "triceps", 21, "front_delts", 21)
    }

    @Test
    fun f6_dropSetsCountHalf() {
        val sets = List(2) { benchSet(type = SetType.DROP) }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 10, "triceps", 5, "front_delts", 5)
    }

    @Test
    fun f7_fiveSquatSetsAt167Hours() {
        val sets = List(5) { squatSet(completedAt = now - 167 * hour) }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("quads", 6, "glutes", 6, "hamstrings", 3)
        val raw = FatigueModel.computeRaw(sets, now, halfLives)
        assertThat(rawOf(raw, "quads")).isWithin(1e-3).of(0.5861)
        assertThat(rawOf(raw, "glutes")).isWithin(1e-3).of(0.5861)
        assertThat(rawOf(raw, "hamstrings")).isWithin(1e-3).of(0.2931)
    }

    @Test
    fun f8_setsOlderThanWindowAreIgnored() {
        val sets = List(5) { squatSet(completedAt = now - 169 * hour) }

        assertThat(FatigueModel.computeRaw(sets, now, halfLives)).isEmpty()
        assertThat(FatigueModel.compute(sets, now, halfLives)).isEmpty()
    }

    @Test
    fun f9_twelveWorkSetsCapTheScoreAtHundred() {
        val sets = List(12) { benchSet() }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 100, "triceps", 60, "front_delts", 60)
        assertThat(rawOf(FatigueModel.computeRaw(sets, now, halfLives), "chest")).isWithin(1e-3).of(12.0)
    }

    @Test
    fun f10_cardioTwentyMinutesThreeHoursAgo() {
        val sets = listOf(cardioSet(durationS = 1_200, completedAt = now - 3 * hour))

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("cardio", 17)
        assertThat(rawOf(FatigueModel.computeRaw(sets, now, halfLives), "cardio")).isWithin(1e-3).of(1.6818)
    }

    @Test
    fun f11_cardioStimulusIsCappedAtThree() {
        val sets = listOf(cardioSet(durationS = 3_600))

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("cardio", 30)
        assertThat(rawOf(FatigueModel.computeRaw(sets, now, halfLives), "cardio")).isWithin(1e-3).of(3.0)
    }

    @Test
    fun f12_futureCompletedAtIsClampedToAgeZero() {
        val sets = List(2) { benchSet(completedAt = now + hour) }

        val scores = FatigueModel.compute(sets, now, halfLives)

        assertThat(scores).containsExactly("chest", 20, "triceps", 10, "front_delts", 10)
    }
}
