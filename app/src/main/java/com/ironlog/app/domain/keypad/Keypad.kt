package com.ironlog.app.domain.keypad

/** Pure state machine behind the numeric keypads. Keeps constraints so invalid keys are simply ignored. */
data class KeypadState(
    val text: String,
    val allowDecimal: Boolean,
    val maxDecimals: Int,
    val maxIntDigits: Int,
)

sealed interface KeypadKey {
    data class Digit(val d: Int) : KeypadKey
    data object Dot : KeypadKey
    data object Backspace : KeypadKey
    data object Clear : KeypadKey
}

object KeypadPresets {
    val WEIGHT = KeypadState(text = "", allowDecimal = true, maxDecimals = 2, maxIntDigits = 4)
    val REPS = KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 3)

    /** D3: distance in km/mi, up to 999.99. */
    val DISTANCE = KeypadState(text = "", allowDecimal = true, maxDecimals = 2, maxIntDigits = 3)

    /** D4: cardio duration is entered in whole minutes. */
    val MINUTES = KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 3)

    /** D4: timed exercises (planks) are entered in seconds. */
    val SECONDS = KeypadState(text = "", allowDecimal = false, maxDecimals = 0, maxIntDigits = 4)
}

fun KeypadState.press(key: KeypadKey): KeypadState = when (key) {
    is KeypadKey.Digit -> pressDigit(key.d)
    KeypadKey.Dot -> pressDot()
    KeypadKey.Backspace -> copy(text = text.dropLast(1))
    KeypadKey.Clear -> copy(text = "")
}

private fun KeypadState.pressDigit(d: Int): KeypadState {
    require(d in 0..9) { "digit out of range: $d" }
    // A lone leading zero is replaced instead of appended ("0" then 5 -> "5").
    if (text == "0") return copy(text = d.toString())

    val dotIndex = text.indexOf('.')
    if (dotIndex >= 0) {
        val decimals = text.length - dotIndex - 1
        if (decimals >= maxDecimals) return this
        return copy(text = text + d)
    }
    if (text.length >= maxIntDigits) return this
    return copy(text = text + d)
}

private fun KeypadState.pressDot(): KeypadState {
    if (!allowDecimal) return this
    if (text.contains('.')) return this
    if (text.isEmpty()) return copy(text = "0.")
    return copy(text = text + ".")
}
