package com.ironlog.app.domain.strength

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** DOMAIN_RULES.md section 6 vectors E1-E6. */
class OneRepMaxTest {

    @Test
    fun e1() {
        assertThat(OneRepMax.estimate(100_000, 5)).isEqualTo(116_667)
    }

    @Test
    fun e2() {
        assertThat(OneRepMax.estimate(60_000, 10)).isEqualTo(80_000)
    }

    @Test
    fun e3() {
        assertThat(OneRepMax.estimate(102_058, 8)).isEqualTo(129_273)
    }

    @Test
    fun e4_singleRepReturnsWeight() {
        assertThat(OneRepMax.estimate(140_000, 1)).isEqualTo(140_000)
    }

    @Test
    fun e5_zeroRepsIsUnreliable() {
        assertThat(OneRepMax.estimate(50_000, 0)).isNull()
    }

    @Test
    fun e6_aboveTwelveRepsIsUnreliable() {
        assertThat(OneRepMax.estimate(50_000, 13)).isNull()
    }

    @Test
    fun twelveRepsIsStillAccepted() {
        assertThat(OneRepMax.estimate(100_000, 12)).isEqualTo(140_000)
    }
}
