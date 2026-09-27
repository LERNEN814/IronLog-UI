package com.ironlog.app.ui.feature.home

import android.graphics.RectF

/**
 * A simplified body region drawn as a rounded rectangle / ellipse.
 * Coordinates are normalised to 0..1 and multiplied by the canvas size when drawing.
 * Symmetric muscles are two shapes sharing the same [muscleId].
 */
data class RegionShape(
    val muscleId: String,
    val body: Int,
    val rect: RectF,
    val cornerRadius: Float,
)

object RegionGeometry {
    const val BODY_FRONT = 0
    const val BODY_BACK = 1

    private fun shape(
        muscleId: String,
        body: Int,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        cornerRadius: Float = 0.02f,
    ) = RegionShape(muscleId, body, RectF(left, top, right, bottom), cornerRadius)

    /** PRD R-5.3: front view — chest, front delts, biceps, forearms, abs, quads, calves. */
    val front: List<RegionShape> = listOf(
        shape("chest", BODY_FRONT, 0.30f, 0.17f, 0.47f, 0.26f),
        shape("chest", BODY_FRONT, 0.53f, 0.17f, 0.70f, 0.26f),
        shape("front_delts", BODY_FRONT, 0.22f, 0.13f, 0.33f, 0.20f),
        shape("front_delts", BODY_FRONT, 0.67f, 0.13f, 0.78f, 0.20f),
        shape("biceps", BODY_FRONT, 0.18f, 0.25f, 0.27f, 0.38f),
        shape("biceps", BODY_FRONT, 0.73f, 0.25f, 0.82f, 0.38f),
        shape("forearms", BODY_FRONT, 0.14f, 0.40f, 0.23f, 0.54f),
        shape("forearms", BODY_FRONT, 0.77f, 0.40f, 0.86f, 0.54f),
        shape("abs", BODY_FRONT, 0.40f, 0.28f, 0.60f, 0.46f),
        shape("quads", BODY_FRONT, 0.36f, 0.50f, 0.48f, 0.74f),
        shape("quads", BODY_FRONT, 0.52f, 0.50f, 0.64f, 0.74f),
        shape("calves", BODY_FRONT, 0.38f, 0.78f, 0.46f, 0.96f),
        shape("calves", BODY_FRONT, 0.54f, 0.78f, 0.62f, 0.96f),
    )

    /** PRD R-5.3: back view — upper back, lats, lower back, rear delts, triceps, glutes, hamstrings, calves. */
    val back: List<RegionShape> = listOf(
        shape("upper_back", BODY_BACK, 0.36f, 0.16f, 0.64f, 0.29f),
        shape("lats", BODY_BACK, 0.27f, 0.29f, 0.43f, 0.44f),
        shape("lats", BODY_BACK, 0.57f, 0.29f, 0.73f, 0.44f),
        shape("lower_back", BODY_BACK, 0.42f, 0.44f, 0.58f, 0.52f),
        shape("rear_delts", BODY_BACK, 0.22f, 0.12f, 0.33f, 0.19f),
        shape("rear_delts", BODY_BACK, 0.67f, 0.12f, 0.78f, 0.19f),
        shape("triceps", BODY_BACK, 0.18f, 0.24f, 0.27f, 0.40f),
        shape("triceps", BODY_BACK, 0.73f, 0.24f, 0.82f, 0.40f),
        shape("glutes", BODY_BACK, 0.37f, 0.52f, 0.49f, 0.62f),
        shape("glutes", BODY_BACK, 0.51f, 0.52f, 0.63f, 0.62f),
        shape("hamstrings", BODY_BACK, 0.36f, 0.63f, 0.47f, 0.78f),
        shape("hamstrings", BODY_BACK, 0.53f, 0.63f, 0.64f, 0.78f),
        shape("calves", BODY_BACK, 0.38f, 0.80f, 0.46f, 0.96f),
        shape("calves", BODY_BACK, 0.54f, 0.80f, 0.62f, 0.96f),
    )

    fun forBody(body: Int): List<RegionShape> = if (body == BODY_BACK) back else front
}
