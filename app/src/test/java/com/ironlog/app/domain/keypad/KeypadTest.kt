package com.ironlog.app.domain.keypad

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.WeightUnit
import org.junit.Test

/** DOMAIN_RULES.md section 2 vectors K1-K12. */
class KeypadTest {

    private fun KeypadState.pressAll(vararg keys: KeypadKey): KeypadState =
        keys.fold(this) { state, key -> state.press(key) }

    @Test
    fun k1_weightEmptySixZero() {
        val result = KeypadPresets.WEIGHT.pressAll(KeypadKey.Digit(6), KeypadKey.Digit(0))
        assertThat(result.text).isEqualTo("60")
    }

    @Test
    fun k2_weightEmptyDotFive() {
        val result = KeypadPresets.WEIGHT.pressAll(KeypadKey.Dot, KeypadKey.Digit(5))
        assertThat(result.text).isEqualTo("0.5")
    }

    @Test
    fun k3_weightZeroThenFiveFoldsLeadingZero() {
        val result = KeypadPresets.WEIGHT.copy(text = "0").pressAll(KeypadKey.Digit(5))
        assertThat(result.text).isEqualTo("5")
    }

    @Test
    fun k4_weightTrailingDotSecondDotIgnored() {
        val result = KeypadPresets.WEIGHT.copy(text = "62.").pressAll(KeypadKey.Dot)
        assertThat(result.text).isEqualTo("62.")
    }

    @Test
    fun k5_weightSecondDecimalAllowedThirdIgnored() {
        val result = KeypadPresets.WEIGHT.copy(text = "62.5")
            .pressAll(KeypadKey.Digit(5), KeypadKey.Digit(5))
        assertThat(result.text).isEqualTo("62.55")
    }

    @Test
    fun k6_weightFifthIntegerDigitIgnored() {
        val result = KeypadPresets.WEIGHT.copy(text = "1000").pressAll(KeypadKey.Digit(0))
        assertThat(result.text).isEqualTo("1000")
    }

    @Test
    fun k7_weightBackspaceEmptiesAndStopsAtEmpty() {
        val result = KeypadPresets.WEIGHT.copy(text = "60")
            .pressAll(KeypadKey.Backspace, KeypadKey.Backspace, KeypadKey.Backspace)
        assertThat(result.text).isEqualTo("")
    }

    @Test
    fun k8_repsEmptyOneTwo() {
        val result = KeypadPresets.REPS.pressAll(KeypadKey.Digit(1), KeypadKey.Digit(2))
        assertThat(result.text).isEqualTo("12")
    }

    @Test
    fun k9_repsDotIgnored() {
        val result = KeypadPresets.REPS.copy(text = "12").pressAll(KeypadKey.Dot)
        assertThat(result.text).isEqualTo("12")
    }

    @Test
    fun k10_repsFourthIntegerDigitIgnored() {
        val result = KeypadPresets.REPS.copy(text = "100").pressAll(KeypadKey.Digit(1))
        assertThat(result.text).isEqualTo("100")
    }

    @Test
    fun k11_repsClear() {
        val result = KeypadPresets.REPS.copy(text = "8").pressAll(KeypadKey.Clear)
        assertThat(result.text).isEqualTo("")
    }

    @Test
    fun k12_repsZeroThenZeroStaysZero() {
        val result = KeypadPresets.REPS.copy(text = "0").pressAll(KeypadKey.Digit(0))
        assertThat(result.text).isEqualTo("0")
    }

    @Test
    fun presetsMatchSpecLimits() {
        assertThat(KeypadPresets.WEIGHT).isEqualTo(
            KeypadState(text = "", allowDecimal = true, maxDecimals = 2, maxIntDigits = 4),
        )
        assertThat(KeypadPresets.REPS).isEqualTo(
            KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 3),
        )
    }

    /** Weight keypad output must feed UnitConverter directly: "62.5" kg -> 62500 g. */
    @Test
    fun weightTextParsesToGrams() {
        val state = KeypadPresets.WEIGHT.pressAll(
            KeypadKey.Digit(6),
            KeypadKey.Digit(2),
            KeypadKey.Dot,
            KeypadKey.Digit(5),
        )
        assertThat(state.text).isEqualTo("62.5")
        assertThat(UnitConverter.parseToGrams(state.text, WeightUnit.KG)).isEqualTo(62_500)
    }
}
