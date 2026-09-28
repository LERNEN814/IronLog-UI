package com.ironlog.app.ui.feature.bodyweight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.core.time.Clock
import com.ironlog.app.domain.bodyweight.BodyWeightRepository
import com.ironlog.app.domain.model.BodyWeight
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BodyWeightUiState(
    val loading: Boolean = true,
    val entries: List<BodyWeight> = emptyList(),
    val unit: WeightUnit = WeightUnit.KG,
    val today: String = "",
    val error: String? = null,
)

@HiltViewModel
class BodyWeightViewModel @Inject constructor(
    private val repository: BodyWeightRepository,
    private val settings: SettingsRepository,
    private val clock: Clock,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BodyWeightUiState(today = clock.today().toString()))
    val uiState: StateFlow<BodyWeightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { settings.observeSettings().collect { value -> _uiState.update { it.copy(unit = value.weightUnit) } } }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            runCatching { repository.recent() }
                .onSuccess { entries -> _uiState.update { it.copy(loading = false, entries = entries, error = null) } }
                .onFailure { error -> _uiState.update { it.copy(loading = false, error = error.message) } }
        }
    }

    fun save(weightGrams: Int) {
        if (weightGrams <= 0) return
        viewModelScope.launch { repository.upsert(_uiState.value.today, weightGrams); refresh() }
    }

    fun delete(localDate: String) {
        viewModelScope.launch { repository.delete(localDate); refresh() }
    }
}
