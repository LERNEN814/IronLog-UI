package com.ironlog.app.domain.strength

import java.math.BigDecimal
import java.math.RoundingMode

/** DOMAIN_RULES.md section 6: Epley estimated one rep max. */
object OneRepMax {

    /** @return estimated 1RM in grams, or null when reps are outside the reliable 1..12 range */
    fun estimate(weightGrams: Int, reps: Int): Int? {
        if (reps == 0 || reps > 12) return null
        if (reps == 1) return weightGrams
        return BigDecimal(weightGrams)
            .multiply(BigDecimal(30 + reps))
            .divide(BigDecimal(30), 0, RoundingMode.HALF_UP)
            .intValueExact()
    }
}
