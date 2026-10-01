package com.ironlog.app.ui.feature.home

enum class BodyView { FRONT, BACK }

data class HeatmapRenderModel(
    val view: BodyView,
    val scores: Map<String, Int>,
    val selectedMuscleId: String? = null,
)

data class PathRegion(
    val sourceId: String,
    val canonicalMuscleId: String,
    val side: RegionSide,
    val view: BodyView,
    val pathData: String,
    val zIndex: Int,
    val neutral: Boolean = false,
)

enum class RegionSide { LEFT, RIGHT, CENTER }
