package com.ironlog.app.domain.summary

/** PRD R-4.2 body region colors (ARGB). Regions match muscle_group.body_region. */
object BodyRegionColors {
    const val CHEST = 0
    const val BACK = 1
    const val SHOULDERS = 2
    const val ARMS = 3
    const val LEGS = 4
    const val CORE = 5
    const val CARDIO = 6

    fun colorFor(bodyRegion: Int): Int = when (bodyRegion) {
        CHEST -> 0xFFE53935.toInt()
        BACK -> 0xFF1E88E5.toInt()
        SHOULDERS -> 0xFFFB8C00.toInt()
        ARMS -> 0xFF8E24AA.toInt()
        LEGS -> 0xFF43A047.toInt()
        CORE -> 0xFFFDD835.toInt()
        CARDIO -> 0xFF00ACC1.toInt()
        else -> 0xFF9E9E9E.toInt()
    }
}
