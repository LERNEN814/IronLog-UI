package com.ironlog.app.ui.feature.home

import androidx.compose.ui.graphics.Color

/** DOMAIN_RULES §5.3 fatigue colour buckets (single-hue ramp, red-green colourblind safe). */
object HeatmapColors {

    fun colorFor(score: Int): Color = when {
        score <= 0 -> Color(0xFFE0E0E0)
        score <= 24 -> Color(0xFFF9D5D3)
        score <= 49 -> Color(0xFFF28B82)
        score <= 74 -> Color(0xFFD93025)
        else -> Color(0xFF8C1D18)
    }
}
