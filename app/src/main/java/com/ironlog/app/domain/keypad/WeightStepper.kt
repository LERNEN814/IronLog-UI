package com.ironlog.app.domain.keypad

import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.WeightUnit
import java.math.BigDecimal

/** +/- weight steps. Arithmetic happens on the displayed value so the shown number moves by the step. */
object WeightStepper {
    const val DEFAULT_STEP_TEXT_KG = "2.5"
    const val DEFAULT_STEP_TEXT_LB = "5"

    fun defaultStepText(unit: WeightUnit): String =
        if (unit == WeightUnit.KG) DEFAULT_STEP_TEXT_KG else DEFAULT_STEP_TEXT_LB

    /**
     * @param direction positive steps up, negative steps down
     * @param stepText step size in the given unit (e.g. "2.5" kg, "5" lb)
     * @return new weight in grams, never below 0; the input is returned unchanged for an invalid step
     */
    fun stepWeight(grams: Int, unit: WeightUnit, direction: Int, stepText: String): Int {
        if (direction == 0) return grams
        val stepGrams = UnitConverter.parseToGrams(stepText, unit) ?: return grams
        if (stepGrams <= 0) return grams

        val current = BigDecimal(UnitConverter.formatGrams(grams, unit))
        val step = BigDecimal(UnitConverter.formatGrams(stepGrams, unit))
        val next = if (direction > 0) current.add(step) else current.subtract(step)
        if (next.signum() <= 0) return 0

        // Out-of-range results (above the parse limit) leave the value untouched.
        return UnitConverter.parseToGrams(next.toPlainString(), unit) ?: grams
    }
}
