package com.ironlog.app.domain.strength

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.SetType
import org.junit.Test

/** M4-T4.4: E-vector 1RM values participate, invalid days are skipped, output is date-ascending. */
class StrengthTrendTest {

    private fun set(
        date: String,
        weightGrams: Int?,
        reps: Int?,
        setType: SetType = SetType.WORK,
        isCompleted: Boolean = true,
    ) = TrendSet(
        localDate = date,
        setType = setType,
        weightGrams = weightGrams,
        reps = reps,
        isCompleted = isCompleted,
    )

    @Test
    fun e1ToE3ValuesParticipate() {
        val trend = StrengthTrend.build(
            listOf(
                set("2026-01-01", 100_000, 5),   // E1 -> 116667
                set("2026-01-02", 60_000, 10),   // E2 -> 80000
                set("2026-01-03", 102_058, 8),   // E3 -> 129273
            ),
        )

        assertThat(trend.map { it.bestE1rmGrams }).containsExactly(116_667, 80_000, 129_273).inOrder()
        assertThat(trend.map { it.date }).containsExactly("2026-01-01", "2026-01-02", "2026-01-03").inOrder()
        assertThat(trend[0].volumeGrams).isEqualTo(500_000)
        assertThat(trend[1].volumeGrams).isEqualTo(600_000)
        assertThat(trend[2].volumeGrams).isEqualTo(816_464)
    }

    @Test
    fun sameDayKeepsTheBestAndSumsTheVolume() {
        val trend = StrengthTrend.build(
            listOf(
                set("2026-01-01", 60_000, 8),
                set("2026-01-01", 100_000, 5),
            ),
        )

        assertThat(trend).hasSize(1)
        assertThat(trend.single().bestE1rmGrams).isEqualTo(116_667)
        assertThat(trend.single().volumeGrams).isEqualTo(980_000)
    }

    @Test
    fun warmupAndIncompleteSetsAreIgnored() {
        val trend = StrengthTrend.build(
            listOf(
                set("2026-01-01", 100_000, 5, setType = SetType.WARMUP),
                set("2026-01-01", 100_000, 5, isCompleted = false),
                set("2026-01-01", 60_000, 8),
            ),
        )

        assertThat(trend.single().bestE1rmGrams).isEqualTo(76_000)
        assertThat(trend.single().volumeGrams).isEqualTo(480_000)
    }

    @Test
    fun daysWithoutAValidSetAreSkipped() {
        val trend = StrengthTrend.build(
            listOf(
                set("2026-01-01", 50_000, 0),    // E5 -> null
                set("2026-01-01", 50_000, 13),   // E6 -> null
                set("2026-01-02", null, 8),      // bodyweight/cardio
                set("2026-01-03", 60_000, 8),
            ),
        )

        assertThat(trend.map { it.date }).containsExactly("2026-01-03")
    }

    @Test
    fun outputIsSortedByDateAscendingEvenWhenInputIsShuffled() {
        val trend = StrengthTrend.build(
            listOf(
                set("2026-03-01", 60_000, 8),
                set("2026-01-01", 60_000, 8),
                set("2026-02-01", 60_000, 8),
            ),
        )

        assertThat(trend.map { it.date })
            .containsExactly("2026-01-01", "2026-02-01", "2026-03-01").inOrder()
    }

    @Test
    fun personalRecordsAreStrictlyGreaterAndNeedABaseline() {
        val sets = listOf(
            set("2026-01-01", 100_000, 5),   // 116667 -> first valid day, only a baseline (D9)
            set("2026-01-02", 100_000, 5),   // equal -> not a PR
            set("2026-01-03", 102_058, 8),   // 129273 -> PR
            set("2026-01-04", 90_000, 5),    // lower -> not a PR
        )

        val prs = StrengthTrend.personalRecords(sets)

        assertThat(prs.map { it.date }).containsExactly("2026-01-03")
    }

    @Test
    fun aSingleValidDayHasNoPr() {
        val sets = listOf(set("2026-01-01", 100_000, 5))

        assertThat(StrengthTrend.personalRecords(sets)).isEmpty()
    }
}
