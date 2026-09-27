package com.ironlog.app.domain.settings

import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>

    suspend fun setWeightUnit(unit: WeightUnit)
    suspend fun setDistanceUnit(unit: DistanceUnit)
    suspend fun setDefaultRestSeconds(seconds: Int)
    suspend fun setRestPresetsSeconds(seconds: String)
    suspend fun setKeepScreenOnDuringWorkout(enabled: Boolean)
    suspend fun setShowRirField(enabled: Boolean)
    suspend fun setThemeMode(mode: String)

    /** Marks that the exact-alarm explanation dialog has been shown. */
    suspend fun setExactAlarmPrompted(prompted: Boolean)
}
