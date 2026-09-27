package com.ironlog.app.ui.feature.exercise

/** Stable asset convention for exercise thumbnails and demonstrations. */
object ExerciseMedia {
    fun thumbnailAsset(exerciseId: String): String = "exercises/$exerciseId/thumbnail.webp"

    fun demoAsset(exerciseId: String): String = "exercises/$exerciseId/demo.webp"

    fun licenseAsset(exerciseId: String): String = "exercises/$exerciseId/license.txt"

    fun instructionAsset(exerciseId: String): String = "exercises/$exerciseId/instructions.json"
}
