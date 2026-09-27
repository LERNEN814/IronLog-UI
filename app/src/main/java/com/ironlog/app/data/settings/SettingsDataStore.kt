package com.ironlog.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.settings.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> = dataStore.data.map { it.toUserSettings() }

    override suspend fun setWeightUnit(unit: WeightUnit) {
        dataStore.edit { it[WEIGHT_UNIT] = unit.ordinal }
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        dataStore.edit { it[DISTANCE_UNIT] = unit.ordinal }
    }

    override suspend fun setDefaultRestSeconds(seconds: Int) {
        dataStore.edit { it[DEFAULT_REST_SECONDS] = seconds }
    }

    override suspend fun setRestPresetsSeconds(seconds: String) {
        dataStore.edit { it[REST_PRESETS_SECONDS] = seconds }
    }

    override suspend fun setKeepScreenOnDuringWorkout(enabled: Boolean) {
        dataStore.edit { it[KEEP_SCREEN_ON] = enabled }
    }

    override suspend fun setShowRirField(enabled: Boolean) {
        dataStore.edit { it[SHOW_RIR_FIELD] = enabled }
    }

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[THEME_MODE] = mode }
    }

    override suspend fun setExactAlarmPrompted(prompted: Boolean) {
        dataStore.edit { it[EXACT_ALARM_PROMPTED] = prompted }
    }

    private fun Preferences.toUserSettings(): UserSettings = UserSettings(
        weightUnit = get(WEIGHT_UNIT)?.let { WeightUnit.entries[it] } ?: WeightUnit.KG,
        distanceUnit = get(DISTANCE_UNIT)?.let { DistanceUnit.entries[it] } ?: DistanceUnit.KM,
        defaultRestSeconds = get(DEFAULT_REST_SECONDS) ?: 120,
        restPresetsSeconds = get(REST_PRESETS_SECONDS) ?: "30,90,120,150",
        keepScreenOnDuringWorkout = get(KEEP_SCREEN_ON) ?: true,
        showRirField = get(SHOW_RIR_FIELD) ?: false,
        themeMode = get(THEME_MODE) ?: "system",
        seedVersion = get(SEED_VERSION) ?: 0,
        exactAlarmPrompted = get(EXACT_ALARM_PROMPTED) ?: false,
    )

    private companion object {
        val WEIGHT_UNIT = intPreferencesKey("weight_unit")
        val DISTANCE_UNIT = intPreferencesKey("distance_unit")
        val DEFAULT_REST_SECONDS = intPreferencesKey("default_rest_seconds")
        val REST_PRESETS_SECONDS = stringPreferencesKey("rest_presets_seconds")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on_during_workout")
        val SHOW_RIR_FIELD = booleanPreferencesKey("show_rir_field")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SEED_VERSION = intPreferencesKey("seed_version")
        val EXACT_ALARM_PROMPTED = booleanPreferencesKey("exact_alarm_prompted")
    }
}
