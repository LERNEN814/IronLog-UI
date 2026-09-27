package com.ironlog.app.core.units

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.WeightUnit
import org.junit.Test

/**
 * DOMAIN_RULES.md section 1.3 vectors U1-U22 plus the lb round-trip property.
 * Every table row has its own test; format assertions use the parsed grams from the same row.
 */
class UnitConverterTest {

    /** Row with parse + both format columns. */
    private fun checkRow(id: String, input: String, unit: WeightUnit, grams: Int, kg: String, lb: String) {
        assertWithMessage("%s parse", id).that(UnitConverter.parseToGrams(input, unit)).isEqualTo(grams)
        assertWithMessage("%s format KG", id).that(UnitConverter.formatGrams(grams, WeightUnit.KG)).isEqualTo(kg)
        assertWithMessage("%s format LB", id).that(UnitConverter.formatGrams(grams, WeightUnit.LB)).isEqualTo(lb)
    }

    private fun checkRoundTrip(id: String, lbText: String) {
        val grams = UnitConverter.parseToGrams(lbText, WeightUnit.LB)
        assertWithMessage("%s parse", id).that(grams).isNotNull()
        assertWithMessage("%s round trip", id)
            .that(UnitConverter.formatGrams(checkNotNull(grams), WeightUnit.LB))
            .isEqualTo(lbText)
    }

    @Test
    fun u1() = checkRow("U1", "60", WeightUnit.KG, 60_000, "60", "132.28")

    @Test
    fun u2() = checkRow("U2", "62.5", WeightUnit.KG, 62_500, "62.5", "137.79")

    @Test
    fun u3() = checkRow("U3", "0.25", WeightUnit.KG, 250, "0.25", "0.55")

    @Test
    fun u4() = checkRow("U4", "1.25", WeightUnit.KG, 1_250, "1.25", "2.76")

    @Test
    fun u5() = checkRow("U5", "20.4", WeightUnit.KG, 20_400, "20.4", "44.97")

    @Test
    fun u6() = checkRow("U6", "45", WeightUnit.LB, 20_412, "20.41", "45")

    @Test
    fun u7() = checkRow("U7", "2.5", WeightUnit.LB, 1_134, "1.13", "2.5")

    @Test
    fun u8() = checkRow("U8", "135", WeightUnit.LB, 61_235, "61.24", "135")

    @Test
    fun u9() = checkRow("U9", "100", WeightUnit.LB, 45_359, "45.36", "100")

    @Test
    fun u10() = checkRow("U10", "225", WeightUnit.LB, 102_058, "102.06", "225")

    @Test
    fun u11() = checkRow("U11", "0", WeightUnit.KG, 0, "0", "0")

    @Test
    fun u12() {
        assertThat(UnitConverter.parseToGrams(" 62,5 ", WeightUnit.KG)).isEqualTo(62_500)
    }

    @Test
    fun u13() {
        assertThat(UnitConverter.parseToGrams("", WeightUnit.KG)).isNull()
    }

    @Test
    fun u14() {
        assertThat(UnitConverter.parseToGrams(".", WeightUnit.KG)).isNull()
    }

    @Test
    fun u15() {
        assertThat(UnitConverter.parseToGrams("-5", WeightUnit.KG)).isNull()
    }

    @Test
    fun u16() {
        assertThat(UnitConverter.parseToGrams("62.555", WeightUnit.KG)).isNull()
    }

    @Test
    fun u17() {
        assertThat(UnitConverter.parseToGrams("1000.01", WeightUnit.KG)).isNull()
    }

    @Test
    fun u18() {
        assertThat(UnitConverter.parseToGrams("abc", WeightUnit.LB)).isNull()
    }

    @Test
    fun u19() {
        assertThat(UnitConverter.parseToGrams("2200", WeightUnit.LB)).isEqualTo(997_903)
    }

    @Test
    fun u20() {
        assertThat(UnitConverter.parseToGrams("2200.5", WeightUnit.LB)).isNull()
    }

    @Test
    fun u21() {
        assertThat(UnitConverter.formatGrams(1_000, WeightUnit.KG)).isEqualTo("1")
        assertThat(UnitConverter.formatGrams(1_000, WeightUnit.LB)).isEqualTo("2.2")
    }

    @Test
    fun u22() {
        assertThat(UnitConverter.formatGrams(100_000, WeightUnit.KG)).isEqualTo("100")
        assertThat(UnitConverter.formatGrams(100_000, WeightUnit.LB)).isEqualTo("220.46")
    }

    @Test
    fun kgUpperBoundIsInclusive() {
        assertThat(UnitConverter.parseToGrams("1000", WeightUnit.KG)).isEqualTo(1_000_000)
    }

    @Test
    fun u23_trailingDotIsAccepted() {
        assertThat(UnitConverter.parseToGrams("62.", WeightUnit.KG)).isEqualTo(62_000)
    }

    @Test
    fun u24_zeroWithTrailingDotIsZero() {
        assertThat(UnitConverter.parseToGrams("0.", WeightUnit.KG)).isEqualTo(0)
    }

    @Test
    fun u25_leadingDotIsRejected() {
        assertThat(UnitConverter.parseToGrams(".5", WeightUnit.KG)).isNull()
    }

    @Test
    fun tenthsRoundTrip() {
        assertThat(UnitConverter.parseTenths("6.5")).isEqualTo(65)
        assertThat(UnitConverter.parseTenths("8")).isEqualTo(80)
        assertThat(UnitConverter.parseTenths("12,25")).isNull()
        assertThat(UnitConverter.parseTenths("abc")).isNull()
        assertThat(UnitConverter.formatTenths(65)).isEqualTo("6.5")
        assertThat(UnitConverter.formatTenths(80)).isEqualTo("8")
        assertThat(UnitConverter.formatTenths(0)).isEqualTo("0")
    }

    // Property: a lb value the user can enter must survive a parse/format round trip unchanged.
    @Test
    fun roundTripLbU6() = checkRoundTrip("U6", "45")

    @Test
    fun roundTripLbU7() = checkRoundTrip("U7", "2.5")

    @Test
    fun roundTripLbU8() = checkRoundTrip("U8", "135")

    @Test
    fun roundTripLbU9() = checkRoundTrip("U9", "100")

    @Test
    fun roundTripLbU10() = checkRoundTrip("U10", "225")

    @Test
    fun di1_distanceFiveKm() {
        val meters = UnitConverter.parseDistanceToMeters("5", DistanceUnit.KM)
        assertThat(meters).isEqualTo(5_000)
        assertThat(UnitConverter.formatMeters(5_000, DistanceUnit.KM)).isEqualTo("5")
        assertThat(UnitConverter.formatMeters(5_000, DistanceUnit.MI)).isEqualTo("3.11")
    }

    @Test
    fun di2_distanceFivePointTwoFiveKm() {
        val meters = UnitConverter.parseDistanceToMeters("5.25", DistanceUnit.KM)
        assertThat(meters).isEqualTo(5_250)
        assertThat(UnitConverter.formatMeters(5_250, DistanceUnit.KM)).isEqualTo("5.25")
        assertThat(UnitConverter.formatMeters(5_250, DistanceUnit.MI)).isEqualTo("3.26")
    }

    @Test
    fun di3_distanceZeroPointFourKm() {
        val meters = UnitConverter.parseDistanceToMeters("0.4", DistanceUnit.KM)
        assertThat(meters).isEqualTo(400)
        assertThat(UnitConverter.formatMeters(400, DistanceUnit.KM)).isEqualTo("0.4")
        assertThat(UnitConverter.formatMeters(400, DistanceUnit.MI)).isEqualTo("0.25")
    }

    @Test
    fun di4_distanceThreePointOneMiles() {
        val meters = UnitConverter.parseDistanceToMeters("3.1", DistanceUnit.MI)
        assertThat(meters).isEqualTo(4_989)
        assertThat(UnitConverter.formatMeters(4_989, DistanceUnit.KM)).isEqualTo("4.99")
        assertThat(UnitConverter.formatMeters(4_989, DistanceUnit.MI)).isEqualTo("3.1")
    }

    @Test
    fun di5_halfMile() {
        val meters = UnitConverter.parseDistanceToMeters("0.5", DistanceUnit.MI)
        assertThat(meters).isEqualTo(805)
        assertThat(UnitConverter.formatMeters(805, DistanceUnit.KM)).isEqualTo("0.81")
        assertThat(UnitConverter.formatMeters(805, DistanceUnit.MI)).isEqualTo("0.5")
    }

    @Test
    fun di6_marathonMiles() {
        val meters = UnitConverter.parseDistanceToMeters("26.2", DistanceUnit.MI)
        assertThat(meters).isEqualTo(42_165)
        assertThat(UnitConverter.formatMeters(42_165, DistanceUnit.KM)).isEqualTo("42.17")
        assertThat(UnitConverter.formatMeters(42_165, DistanceUnit.MI)).isEqualTo("26.2")
    }

    @Test
    fun di7_zeroDistanceIsInvalid() {
        assertThat(UnitConverter.parseDistanceToMeters("0", DistanceUnit.KM)).isNull()
    }

    @Test
    fun di8_distanceOverNineHundredNinetyNineIsInvalid() {
        assertThat(UnitConverter.parseDistanceToMeters("1000", DistanceUnit.KM)).isNull()
    }

    @Test
    fun distanceGrammarMatchesWeights() {
        assertThat(UnitConverter.parseDistanceToMeters("5,", DistanceUnit.KM)).isEqualTo(5_000)
        assertThat(UnitConverter.parseDistanceToMeters(".5", DistanceUnit.KM)).isNull()
        assertThat(UnitConverter.parseDistanceToMeters("5.255", DistanceUnit.KM)).isNull()
        assertThat(UnitConverter.parseDistanceToMeters("999.99", DistanceUnit.KM)).isEqualTo(999_990)
    }

    @Test
    fun d4_formatDurationExamples() {
        assertThat(UnitConverter.formatDuration(1_200)).isEqualTo("20:00")
        assertThat(UnitConverter.formatDuration(45)).isEqualTo("0:45")
        assertThat(UnitConverter.formatDuration(5_400)).isEqualTo("90:00")
        assertThat(UnitConverter.formatDuration(61)).isEqualTo("1:01")
    }
}
