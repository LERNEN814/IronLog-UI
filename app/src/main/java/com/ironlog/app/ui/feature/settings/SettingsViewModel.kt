package com.ironlog.app.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.settings.SettingsRepository
import com.ironlog.app.ui.theme.ThemeMode
import com.ironlog.app.ui.theme.toSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val loading: Boolean = true,
    val settings: UserSettings = UserSettings(),
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeSettings().collect { settings ->
                _uiState.update { it.copy(loading = false, settings = settings) }
            }
        }
    }

    fun onWeightUnitSelected(unit: WeightUnit) {
        viewModelScope.launch { repository.setWeightUnit(unit) }
    }

    fun onDistanceUnitSelected(unit: DistanceUnit) {
        viewModelScope.launch { repository.setDistanceUnit(unit) }
    }

    /** Ignores invalid values so the text field can stay in sync while typing. */
    fun onDefaultRestSecondsChanged(seconds: Int) {
        if (seconds in 5..3_600) {
            viewModelScope.launch { repository.setDefaultRestSeconds(seconds) }
        }
    }

    /** @param presets comma separated seconds, e.g. "30,90,120,150" */
    fun onRestPresetsChanged(presets: String) {
        val parsed = presets.split(',').mapNotNull { it.trim().toIntOrNull() }
        if (parsed.size == PRESET_COUNT && parsed.all { it in 5..3_600 }) {
            viewModelScope.launch { repository.setRestPresetsSeconds(parsed.joinToString(",")) }
        }
    }

    fun onKeepScreenOnChanged(enabled: Boolean) {
        viewModelScope.launch { repository.setKeepScreenOnDuringWorkout(enabled) }
    }

    fun onShowRirFieldChanged(enabled: Boolean) {
        viewModelScope.launch { repository.setShowRirField(enabled) }
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode.toSetting()) }
    }

    private companion object {
        const val PRESET_COUNT = 4
    }
}
