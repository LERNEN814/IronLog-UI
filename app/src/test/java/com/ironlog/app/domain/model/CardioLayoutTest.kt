package com.ironlog.app.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** Audit follow-up: row layout per cardio equipment (PRD R-2.9). */
class CardioLayoutTest {

    @Test
    fun stairClimberHasItsOwnLayout() {
        assertThat(CardioEquipment.layoutFor("stair_climber")).isEqualTo(CardioLayout.STAIR_CLIMBER)
    }

    @Test
    fun treadmillHasItsOwnLayout() {
        assertThat(CardioEquipment.layoutFor("treadmill")).isEqualTo(CardioLayout.TREADMILL)
    }

    @Test
    fun ellipticalAndBikeShareTheirLayout() {
        assertThat(CardioEquipment.layoutFor("elliptical")).isEqualTo(CardioLayout.ELLIPTICAL_BIKE)
        assertThat(CardioEquipment.layoutFor("bike")).isEqualTo(CardioLayout.ELLIPTICAL_BIKE)
    }

    @Test
    fun rowerAndUnknownEquipmentFallBackToEllipticalBike() {
        assertThat(CardioEquipment.layoutFor("rower")).isEqualTo(CardioLayout.ELLIPTICAL_BIKE)
        assertThat(CardioEquipment.layoutFor("unknown_machine")).isEqualTo(CardioLayout.ELLIPTICAL_BIKE)
    }
}
