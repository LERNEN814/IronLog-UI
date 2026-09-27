package com.ironlog.app.domain.fatigue

/** Supplies the seven day muscle fatigue scores (DOMAIN_RULES §5). */
interface FatigueRepository {
    /** muscleId -> score (0..100) computed by [FatigueModel.compute] for [nowMillis]. */
    suspend fun scores(nowMillis: Long): Map<String, Int>
}
