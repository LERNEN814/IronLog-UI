package com.ironlog.app.ui.feature.home

enum class BodyView { FRONT, BACK }

data class HeatmapRenderModel(
    val view: BodyView,
    val scores: Map<String, Int>,
    val selectedMuscleId: String? = null,
)

object HeatmapCanonical {
    val ids = setOf("chest", "upper_back", "lower_back", "front_delts", "biceps", "triceps", "forearms", "quads", "hamstrings", "glutes", "calves", "abs")
}

sealed interface HeatmapState {
    data object Loading : HeatmapState
    data object Empty : HeatmapState
    data class Ready(val model: HeatmapRenderModel, val cardioScore: Int = 0) : HeatmapState
    data class Error(val message: String? = null) : HeatmapState
}

data class PathRegion(
    val sourceId: String,
    val canonicalMuscleId: String,
    val side: RegionSide,
    val view: BodyView,
    val pathData: String,
    val zIndex: Int,
    val neutral: Boolean = false,
    val interactive: Boolean = true,
)

enum class RegionSide { LEFT, RIGHT, CENTER }
