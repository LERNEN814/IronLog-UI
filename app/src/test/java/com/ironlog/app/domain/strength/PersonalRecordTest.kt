package com.ironlog.app.domain.strength

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** DOMAIN_RULES §6 vectors PR1-PR4 (D9). */
class PersonalRecordTest {

    private fun e1rm(weightGrams: Int, reps: Int): Int =
        checkNotNull(OneRepMax.estimate(weightGrams, reps)) { "invalid vector" }

    @Test
    fun pr1_withoutAnyValidBaselineItIsNotAPr() {
        val current = e1rm(100_000, 5)

        assertThat(PersonalRecord.isPersonalRecord(previousBestE1rmGrams = null, currentE1rmGrams = current))
            .isFalse()
    }

    @Test
    fun pr2_onlyWarmupsAreNotABaseline() {
        // Warmup sets never reach the PR computation, so the previous best stays null.
        val current = e1rm(100_000, 5)

        assertThat(PersonalRecord.isPersonalRecord(previousBestE1rmGrams = null, currentE1rmGrams = current))
            .isFalse()
    }

    @Test
    fun pr3_equalE1rmIsNotAPr() {
        val previous = e1rm(100_000, 5)
        val current = e1rm(100_000, 5)

        assertThat(previous).isEqualTo(116_667)
        assertThat(PersonalRecord.isPersonalRecord(previous, current)).isFalse()
    }

    @Test
    fun pr4_strictlyHigherE1rmIsAPr() {
        val previous = 116_667
        val current = e1rm(102_500, 5)

        assertThat(current).isEqualTo(119_583)
        assertThat(PersonalRecord.isPersonalRecord(previous, current)).isTrue()
    }

    @Test
    fun invalidCurrentSetIsNeverAPr() {
        assertThat(PersonalRecord.isPersonalRecord(previousBestE1rmGrams = 100_000, currentE1rmGrams = null))
            .isFalse()
    }
}
