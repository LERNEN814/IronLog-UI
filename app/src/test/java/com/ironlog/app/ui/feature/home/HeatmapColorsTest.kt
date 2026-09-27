package com.ironlog.app.ui.feature.home

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** M5-T5.2: the 9 bucket boundaries of DOMAIN_RULES §5.3. */
class HeatmapColorsTest {

    @Test
    fun mapsEveryBucketBoundary() {
        assertThat(HeatmapColors.colorFor(0)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFE0E0E0))
        assertThat(HeatmapColors.colorFor(1)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFF9D5D3))
        assertThat(HeatmapColors.colorFor(24)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFF9D5D3))
        assertThat(HeatmapColors.colorFor(25)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFF28B82))
        assertThat(HeatmapColors.colorFor(49)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFF28B82))
        assertThat(HeatmapColors.colorFor(50)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFD93025))
        assertThat(HeatmapColors.colorFor(74)).isEqualTo(androidx.compose.ui.graphics.Color(0xFFD93025))
        assertThat(HeatmapColors.colorFor(75)).isEqualTo(androidx.compose.ui.graphics.Color(0xFF8C1D18))
        assertThat(HeatmapColors.colorFor(100)).isEqualTo(androidx.compose.ui.graphics.Color(0xFF8C1D18))
    }
}
