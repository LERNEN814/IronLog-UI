package com.ironlog.app.testutil

import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** In-memory SettingsRepository for ViewModel tests. */
class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {

    val settings = MutableStateFlow(initial)

    override fun observeSettings(): Flow<UserSettings> = settings

    override suspend fun setWeightUnit(unit: WeightUnit) {
        settings.update { it.copy(weightUnit = unit) }
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        settings.update { it.copy(distanceUnit = unit) }
    }

    override suspend fun setDefaultRestSeconds(seconds: Int) {
        settings.update { it.copy(defaultRestSeconds = seconds) }
    }

    override suspend fun setRestPresetsSeconds(seconds: String) {
        settings.update { it.copy(restPresetsSeconds = seconds) }
    }

    override suspend fun setKeepScreenOnDuringWorkout(enabled: Boolean) {
        settings.update { it.copy(keepScreenOnDuringWorkout = enabled) }
    }

    override suspend fun setShowRirField(enabled: Boolean) {
        settings.update { it.copy(showRirField = enabled) }
    }

    override suspend fun setThemeMode(mode: String) {
        settings.update { it.copy(themeMode = mode) }
    }

    override suspend fun setExactAlarmPrompted(prompted: Boolean) {
        settings.update { it.copy(exactAlarmPrompted = prompted) }
    }
}
