package com.ironlog.app.ui.feature.summary

/** Data model of the offscreen share card (PRD R-5.4). */
data class ShareExerciseBest(
    val name: String,
    val bestSetText: String,
    val isPr: Boolean,
)

data class ShareCardData(
    val date: String,
    val durationText: String,
    val volumeText: String,
    val exercises: List<ShareExerciseBest>,
    val bodyRegions: List<Int>,
    val appName: String,
)
