package com.ironlog.app.domain.keypad

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.WeightUnit
import org.junit.Test

/** DOMAIN_RULES.md section 2 stepper vectors S1-S4. */
class WeightStepperTest {

    @Test
    fun s1_kgPlus() {
        assertThat(WeightStepper.stepWeight(60_000, WeightUnit.KG, +1, "2.5")).isEqualTo(62_500)
    }

    @Test
    fun s2_kgMinusClampsAtZero() {
        assertThat(WeightStepper.stepWeight(1_000, WeightUnit.KG, -1, "2.5")).isEqualTo(0)
    }

    @Test
    fun s3_lbPlus() {
        assertThat(WeightStepper.stepWeight(20_412, WeightUnit.LB, +1, "5")).isEqualTo(22_680)
    }

    @Test
    fun s4_lbMinus() {
        assertThat(WeightStepper.stepWeight(61_235, WeightUnit.LB, -1, "5")).isEqualTo(58_967)
    }

    @Test
    fun defaultStepsMatchSpec() {
        assertThat(WeightStepper.defaultStepText(WeightUnit.KG)).isEqualTo("2.5")
        assertThat(WeightStepper.defaultStepText(WeightUnit.LB)).isEqualTo("5")
    }

    @Test
    fun zeroStepKeepsValue() {
        assertThat(WeightStepper.stepWeight(60_000, WeightUnit.KG, +1, "0")).isEqualTo(60_000)
    }

    @Test
    fun invalidStepKeepsValue() {
        assertThat(WeightStepper.stepWeight(60_000, WeightUnit.KG, +1, "abc")).isEqualTo(60_000)
    }

    /** Stepping uses the displayed (user-unit) value, not raw grams: 44.97 lb + 5 lb = 49.97 lb. */
    @Test
    fun stepsOnDisplayedValue() {
        // 20400 g displays as 44.97 lb; 44.97 + 5 = 49.97 lb -> 22666 g.
        assertThat(WeightStepper.stepWeight(20_400, WeightUnit.LB, +1, "5")).isEqualTo(22_666)
    }
}
