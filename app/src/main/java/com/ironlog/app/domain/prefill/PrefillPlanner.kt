package com.ironlog.app.domain.prefill

import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WorkoutSet

/** A placeholder set for a fresh session entry. Placeholders start unfinished; the UI renders them grey. */
data class PrefillSet(
    val setType: SetType,
    val weightGrams: Int?,
    val reps: Int?,
    val durationS: Int?,
    val level: Int?,
    val inclineX10: Int?,
    val speedX10: Int?,
    val distanceM: Int?,
)

/** DOMAIN_RULES.md section 3.2: plan the placeholder sets for a newly added exercise. */
object PrefillPlanner {

    fun plan(lastSets: List<WorkoutSet>, kind: ExerciseKind): List<PrefillSet> {
        if (lastSets.isEmpty()) return listOf(PrefillSet(SetType.WORK, null, null, null, null, null, null, null))
        // rir and parent_set_id are intentionally not copied: they are per-set details of the old session.
        return lastSets.map {
            PrefillSet(
                setType = it.setType,
                weightGrams = it.weightGrams,
                reps = it.reps,
                durationS = it.durationS,
                level = it.level,
                inclineX10 = it.inclineX10,
                speedX10 = it.speedX10,
                distanceM = it.distanceM,
            )
        }
    }
}
