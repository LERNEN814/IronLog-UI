package com.ironlog.app.domain.summary

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** PRD R-4.2 body region colors as ARGB. */
class BodyRegionColorsTest {

    @Test
    fun mapsEveryRegionToItsPrdColor() {
        assertThat(BodyRegionColors.colorFor(0)).isEqualTo(0xFFE53935.toInt())
        assertThat(BodyRegionColors.colorFor(1)).isEqualTo(0xFF1E88E5.toInt())
        assertThat(BodyRegionColors.colorFor(2)).isEqualTo(0xFFFB8C00.toInt())
        assertThat(BodyRegionColors.colorFor(3)).isEqualTo(0xFF8E24AA.toInt())
        assertThat(BodyRegionColors.colorFor(4)).isEqualTo(0xFF43A047.toInt())
        assertThat(BodyRegionColors.colorFor(5)).isEqualTo(0xFFFDD835.toInt())
        assertThat(BodyRegionColors.colorFor(6)).isEqualTo(0xFF00ACC1.toInt())
    }

    @Test
    fun unknownRegionFallsBackToGrey() {
        assertThat(BodyRegionColors.colorFor(99)).isEqualTo(0xFF9E9E9E.toInt())
    }
}
