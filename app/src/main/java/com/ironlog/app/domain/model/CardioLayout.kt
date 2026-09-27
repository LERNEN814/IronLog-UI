package com.ironlog.app.domain.model

/** Which cardio fields a set row shows (PRD R-2.9). */
enum class CardioLayout { STAIR_CLIMBER, TREADMILL, ELLIPTICAL_BIKE }

/**
 * Maps an exercise equipment string to its cardio row layout.
 * rower and unknown equipment fall back to the elliptical/bike layout (duration + level + distance).
 */
object CardioEquipment {
    fun layoutFor(equipment: String): CardioLayout = when (equipment) {
        "stair_climber" -> CardioLayout.STAIR_CLIMBER
        "treadmill" -> CardioLayout.TREADMILL
        else -> CardioLayout.ELLIPTICAL_BIKE
    }
}
