@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.platform.timer.ExactAlarmPermission
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme
import com.ironlog.app.ui.theme.ThemeMode
import com.ironlog.app.ui.theme.toThemeMode

@Composable
fun SettingsScreenRoute(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onWeightUnitSelected = viewModel::onWeightUnitSelected,
        onDistanceUnitSelected = viewModel::onDistanceUnitSelected,
        onDefaultRestSecondsChanged = viewModel::onDefaultRestSecondsChanged,
        onRestPresetsChanged = viewModel::onRestPresetsChanged,
        onKeepScreenOnChanged = viewModel::onKeepScreenOnChanged,
        onShowRirFieldChanged = viewModel::onShowRirFieldChanged,
        onThemeModeChanged = viewModel::onThemeModeChanged,
    )
}

@Composable
@Suppress("LongParameterList")
fun SettingsScreen(
    state: SettingsUiState,
    onWeightUnitSelected: (WeightUnit) -> Unit,
    onDistanceUnitSelected: (DistanceUnit) -> Unit,
    onDefaultRestSecondsChanged: (Int) -> Unit,
    onRestPresetsChanged: (String) -> Unit,
    onKeepScreenOnChanged: (Boolean) -> Unit,
    onShowRirFieldChanged: (Boolean) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
) {
    val settings = state.settings
    var restText by remember(state.loading) { mutableStateOf(settings.defaultRestSeconds.toString()) }
    var presetsText by remember(state.loading) { mutableStateOf(settings.restPresetsSeconds) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
    ) {
        Text(text = stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge)

        Text(text = stringResource(R.string.settings_unit), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = settings.weightUnit == WeightUnit.KG,
                onClick = { onWeightUnitSelected(WeightUnit.KG) },
                label = { Text(stringResource(R.string.unit_kg)) },
            )
            FilterChip(
                selected = settings.weightUnit == WeightUnit.LB,
                onClick = { onWeightUnitSelected(WeightUnit.LB) },
                label = { Text(stringResource(R.string.unit_lb)) },
            )
        }

        Text(text = stringResource(R.string.settings_distance_unit), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = settings.distanceUnit == DistanceUnit.KM,
                onClick = { onDistanceUnitSelected(DistanceUnit.KM) },
                label = { Text(stringResource(R.string.settings_km)) },
            )
            FilterChip(
                selected = settings.distanceUnit == DistanceUnit.MI,
                onClick = { onDistanceUnitSelected(DistanceUnit.MI) },
                label = { Text(stringResource(R.string.settings_mi)) },
            )
        }

        OutlinedTextField(
            value = restText,
            onValueChange = { value ->
                restText = value
                value.toIntOrNull()?.let(onDefaultRestSecondsChanged)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.settings_default_rest)) },
            singleLine = true,
        )

        OutlinedTextField(
            value = presetsText,
            onValueChange = { value ->
                presetsText = value
                onRestPresetsChanged(value)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.settings_rest_presets)) },
            singleLine = true,
        )

        SwitchRow(
            label = stringResource(R.string.settings_keep_screen_on),
            checked = settings.keepScreenOnDuringWorkout,
            onCheckedChange = onKeepScreenOnChanged,
        )
        SwitchRow(
            label = stringResource(R.string.settings_show_rir),
            checked = settings.showRirField,
            onCheckedChange = onShowRirFieldChanged,
        )

        Text(text = stringResource(R.string.settings_theme), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = settings.themeMode.toThemeMode() == mode,
                    onClick = { onThemeModeChanged(mode) },
                    label = { Text(themeLabel(mode)) },
                )
            }
        }

        val context = LocalContext.current
        var timerHelpVisible by remember { mutableStateOf(false) }
        TextButton(onClick = { timerHelpVisible = true }) {
            Text(stringResource(R.string.settings_timer_help))
        }
        if (timerHelpVisible) {
            AlertDialog(
                onDismissRequest = { timerHelpVisible = false },
                title = { Text(stringResource(R.string.settings_timer_help)) },
                text = { Text(stringResource(R.string.settings_timer_help_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            timerHelpVisible = false
                            context.startActivity(ExactAlarmPermission.batteryOptimizationSettingsIntent())
                        },
                    ) {
                        Text(stringResource(R.string.action_open_system_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { timerHelpVisible = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                },
            )
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
    ThemeMode.DARK -> stringResource(R.string.theme_dark)
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    IronLogTheme {
        SettingsScreen(
            state = SettingsUiState(loading = false, settings = UserSettings()),
            onWeightUnitSelected = {},
            onDistanceUnitSelected = {},
            onDefaultRestSecondsChanged = {},
            onRestPresetsChanged = {},
            onKeepScreenOnChanged = {},
            onShowRirFieldChanged = {},
            onThemeModeChanged = {},
        )
    }
}
