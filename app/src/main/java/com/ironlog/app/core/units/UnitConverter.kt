package com.ironlog.app.core.units

import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.WeightUnit
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Converts between the text a user types and integer grams stored in the database.
 * Uses BigDecimal on purpose: Double arithmetic would break lb round-trips (45 lb -> 44.99 lb).
 */
object UnitConverter {
    private val GRAMS_PER_KG = BigDecimal(1000)
    private val GRAMS_PER_LB = BigDecimal("453.59237")

    private val MAX_KG = BigDecimal(1000)
    private val MAX_LB = BigDecimal(2200)
    private val METERS_PER_KM = BigDecimal(1000)
    private val METERS_PER_MI = BigDecimal("1609.344")
    private val MAX_DISTANCE = BigDecimal("999.99")

    /** Digits, optional dot with at most 2 decimals. A trailing dot is allowed (U23: "62."). */
    private val WEIGHT_INPUT = Regex("""^\d+(\.(\d{1,2})?)?$""")

    /** Digits with at most one decimal, for x10 fixed point fields. */
    private val TENTHS_INPUT = Regex("""^\d+(\.\d?)?$""")

    /**
     * Parses user text (e.g. "62.5", "62,5" or "62.") into grams, rounded half-up.
     * Returns null for invalid input: empty, no digits, negative, >2 decimals, >1000 kg / >2200 lb.
     */
    fun parseToGrams(input: String, unit: WeightUnit): Int? {
        val normalized = input.trim().replace(',', '.')
        if (!WEIGHT_INPUT.matches(normalized)) return null

        val value = BigDecimal(normalized.removeSuffix("."))
        val maxValue = maxValueOf(unit)
        if (value > maxValue) return null

        return value.multiply(gramsPerUnit(unit)).setScale(0, RoundingMode.HALF_UP).intValueExact()
    }

    /** Parses a x10 fixed point value (incline 6.5% -> 65, speed 8 km/h -> 80). */
    fun parseTenths(input: String): Int? {
        val normalized = input.trim().replace(',', '.')
        if (!TENTHS_INPUT.matches(normalized)) return null
        return BigDecimal(normalized.removeSuffix("."))
            .multiply(BigDecimal(10))
            .setScale(0, RoundingMode.HALF_UP)
            .intValueExact()
    }

    /** Formats a x10 fixed point value, dropping trailing zeros. */
    fun formatTenths(valueX10: Int): String {
        val display = BigDecimal(valueX10).divide(BigDecimal(10), 1, RoundingMode.HALF_UP)
        if (display.compareTo(BigDecimal.ZERO) == 0) return "0"
        return display.stripTrailingZeros().toPlainString()
    }

    /**
     * DOMAIN_RULES section 1.4 (D3): parses distance text in the user's distance unit into meters.
     * Same grammar as weights; 0 and values above 999.99 are invalid.
     */
    fun parseDistanceToMeters(input: String, unit: DistanceUnit): Int? {
        val normalized = input.trim().replace(',', '.')
        if (!WEIGHT_INPUT.matches(normalized)) return null
        val value = BigDecimal(normalized.removeSuffix("."))
        if (value.signum() == 0 || value > MAX_DISTANCE) return null
        return value.multiply(metersPerUnit(unit)).setScale(0, RoundingMode.HALF_UP).intValueExact()
    }

    /** DOMAIN_RULES section 1.4 (D3): meters -> user distance unit, at most 2 decimals. */
    fun formatMeters(meters: Int, unit: DistanceUnit): String {
        val display = BigDecimal(meters).divide(metersPerUnit(unit), 2, RoundingMode.HALF_UP)
        if (display.compareTo(BigDecimal.ZERO) == 0) return "0"
        return display.stripTrailingZeros().toPlainString()
    }

    /** DOMAIN_RULES section 1.5 (D4): seconds -> m:ss, minutes may exceed 59. */
    fun formatDuration(seconds: Int): String {
        val safe = seconds.coerceAtLeast(0)
        val minutes = safe / 60
        val secs = safe % 60
        return minutes.toString() + ":" + secs.toString().padStart(2, '0')
    }

    private fun metersPerUnit(unit: DistanceUnit): BigDecimal =
        if (unit == DistanceUnit.KM) METERS_PER_KM else METERS_PER_MI

    /** Formats grams for display: at most 2 decimals, trailing zeros and the dot removed. */
    fun formatGrams(grams: Int, unit: WeightUnit): String {
        val display = BigDecimal(grams).divide(gramsPerUnit(unit), 2, RoundingMode.HALF_UP)
        if (display.compareTo(BigDecimal.ZERO) == 0) return "0"
        return display.stripTrailingZeros().toPlainString()
    }

    private fun gramsPerUnit(unit: WeightUnit): BigDecimal =
        if (unit == WeightUnit.KG) GRAMS_PER_KG else GRAMS_PER_LB

    private fun maxValueOf(unit: WeightUnit): BigDecimal =
        if (unit == WeightUnit.KG) MAX_KG else MAX_LB
}
