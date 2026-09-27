package com.ironlog.app.domain.strength

import com.ironlog.app.domain.model.SetType

/** One completed set of an exercise together with the local date of its session. */
data class TrendSet(
    val localDate: String,
    /** Session the set belongs to; used to exclude the current session when judging a PR. */
    val sessionId: String = "",
    val setType: SetType,
    val weightGrams: Int?,
    val reps: Int?,
    val isCompleted: Boolean,
)

/** One point of the exercise history chart. */
data class TrendPoint(
    val date: String,
    val bestE1rmGrams: Int,
    val volumeGrams: Long,
)

/** M4-T4.4: groups sets by day, keeps the best Epley 1RM and the volume of the valid sets. */
object StrengthTrend {

    fun build(sets: List<TrendSet>): List<TrendPoint> =
        sets.mapNotNull { set ->
            if (!set.isCompleted) return@mapNotNull null
            if (set.setType == SetType.WARMUP) return@mapNotNull null
            val weight = set.weightGrams ?: return@mapNotNull null
            val reps = set.reps ?: return@mapNotNull null
            val e1rm = OneRepMax.estimate(weight, reps) ?: return@mapNotNull null
            DaySet(date = set.localDate, e1rm = e1rm, volume = weight.toLong() * reps)
        }
            .groupBy { it.date }
            .map { (date, daySets) ->
                TrendPoint(
                    date = date,
                    bestE1rmGrams = daySets.maxOf { it.e1rm },
                    volumeGrams = daySets.sumOf { it.volume },
                )
            }
            .sortedBy { it.date }

    /** PR days (DOMAIN_RULES §6 + D9): the first valid day is only a baseline. */
    fun personalRecords(sets: List<TrendSet>): List<TrendPoint> {
        var runningBest: Int? = null
        return build(sets).mapNotNull { point ->
            val isPr = PersonalRecord.isPersonalRecord(runningBest, point.bestE1rmGrams)
            runningBest = if (runningBest == null) point.bestE1rmGrams else maxOf(runningBest, point.bestE1rmGrams)
            if (isPr) point else null
        }
    }

    private data class DaySet(val date: String, val e1rm: Int, val volume: Long)
}
