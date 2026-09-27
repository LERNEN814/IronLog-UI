package com.ironlog.app.domain.strength

/**
 * DOMAIN_RULES §6 + D9: a PR needs a baseline.
 * The first valid set of an exercise is only a baseline; a PR must strictly beat an existing best.
 */
object PersonalRecord {

    /**
     * @param previousBestE1rmGrams best e1rm of earlier valid (completed, non-warmup) sets, or null
     * @param currentE1rmGrams e1rm of the set being judged (null = invalid set)
     */
    fun isPersonalRecord(previousBestE1rmGrams: Int?, currentE1rmGrams: Int?): Boolean {
        if (currentE1rmGrams == null) return false
        val baseline = previousBestE1rmGrams ?: return false
        return currentE1rmGrams > baseline
    }
}
