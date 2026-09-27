package com.ironlog.app.domain.fatigue

import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import kotlin.math.pow
import kotlin.math.roundToInt

/** One completed set as seen by the fatigue model. */
data class FatigueInputSet(
    val completedAtMillis: Long,
    val kind: ExerciseKind,
    val setType: SetType,
    val rir: Int?,
    val durationS: Int?,
    val muscles: List<Pair<String, Double>>,
)

/** DOMAIN_RULES.md section 5: seven day muscle fatigue. */
object FatigueModel {
    const val WINDOW_HOURS = 168.0
    const val FULL_SCALE = 10.0
    private const val MILLIS_PER_HOUR = 3_600_000.0

    /** @return score per muscle id, 10 equivalent all-out work sets = 100 */
    fun compute(
        sets: List<FatigueInputSet>,
        nowMillis: Long,
        halfLifeHours: Map<String, Int>,
    ): Map<String, Int> = computeRaw(sets, nowMillis, halfLifeHours)
        .mapValues { (_, raw) -> scoreOf(raw) }

    /** Raw, unrounded fatigue values, for tests and debugging. */
    fun computeRaw(
        sets: List<FatigueInputSet>,
        nowMillis: Long,
        halfLifeHours: Map<String, Int>,
    ): Map<String, Double> {
        val fatigue = mutableMapOf<String, Double>()
        sets.forEach setLoop@{ set ->
            val ageHours = maxOf(0.0, (nowMillis - set.completedAtMillis) / MILLIS_PER_HOUR)
            if (ageHours > WINDOW_HOURS) return@setLoop
            val stimulus = stimulusOf(set)
            if (stimulus <= 0.0) return@setLoop
            set.muscles.forEach muscleLoop@{ (muscleId, weight) ->
                val halfLife = halfLifeHours[muscleId] ?: return@muscleLoop
                fatigue[muscleId] = (fatigue[muscleId] ?: 0.0) +
                    stimulus * weight * 0.5.pow(ageHours / halfLife)
            }
        }
        return fatigue.filterValues { it > 0.0 }
    }

    private fun stimulusOf(set: FatigueInputSet): Double = when (set.kind) {
        ExerciseKind.CARDIO -> minOf((set.durationS ?: 0) / 600.0, 3.0)
        else -> typeFactor(set.setType) * rirFactor(set.rir)
    }

    private fun typeFactor(setType: SetType): Double = when (setType) {
        SetType.WORK, SetType.FAILURE -> 1.0
        SetType.DROP -> 0.5
        SetType.WARMUP -> 0.0
    }

    private fun rirFactor(rir: Int?): Double = when {
        rir == null || rir <= 1 -> 1.0
        rir == 2 -> 0.9
        rir == 3 -> 0.8
        else -> 0.6
    }

    private fun scoreOf(raw: Double): Int = minOf(100, (raw / FULL_SCALE * 100).roundToInt())
}
