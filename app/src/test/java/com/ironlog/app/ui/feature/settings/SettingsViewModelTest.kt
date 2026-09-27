package com.ironlog.app.ui.feature.settings

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.testutil.FakeSettingsRepository
import com.ironlog.app.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun stateFollowsStoredSettings() = runTest(dispatcher) {
        val repository = FakeSettingsRepository(UserSettings(showRirField = true, themeMode = "dark"))

        val viewModel = SettingsViewModel(repository)

        val state = viewModel.uiState.first { !it.loading }
        assertThat(state.settings.showRirField).isTrue()
        assertThat(state.settings.themeMode).isEqualTo("dark")
    }

    @Test
    fun changingWeightUnitUpdatesStoreAndState() = runTest(dispatcher) {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onWeightUnitSelected(WeightUnit.LB)

        assertThat(repository.settings.value.weightUnit).isEqualTo(WeightUnit.LB)
        val state = viewModel.uiState.first { it.settings.weightUnit == WeightUnit.LB }
        assertThat(state.settings.weightUnit).isEqualTo(WeightUnit.LB)
    }

    @Test
    fun togglesAndThemeArePersisted() = runTest(dispatcher) {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onDistanceUnitSelected(DistanceUnit.MI)
        viewModel.onKeepScreenOnChanged(false)
        viewModel.onShowRirFieldChanged(true)
        viewModel.onThemeModeChanged(ThemeMode.DARK)

        val stored = repository.settings.value
        assertThat(stored.distanceUnit).isEqualTo(DistanceUnit.MI)
        assertThat(stored.keepScreenOnDuringWorkout).isFalse()
        assertThat(stored.showRirField).isTrue()
        assertThat(stored.themeMode).isEqualTo("dark")
    }

    @Test
    fun restSecondsAndPresetsValidateInput() = runTest(dispatcher) {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onDefaultRestSecondsChanged(90)
        assertThat(repository.settings.value.defaultRestSeconds).isEqualTo(90)

        // Invalid values are ignored (text field may contain partial input).
        viewModel.onDefaultRestSecondsChanged(0)
        assertThat(repository.settings.value.defaultRestSeconds).isEqualTo(90)

        viewModel.onRestPresetsChanged("45,60,90,120")
        assertThat(repository.settings.value.restPresetsSeconds).isEqualTo("45,60,90,120")

        viewModel.onRestPresetsChanged("45,60")
        assertThat(repository.settings.value.restPresetsSeconds).isEqualTo("45,60,90,120")
    }
}
