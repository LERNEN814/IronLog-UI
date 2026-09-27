@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.domain.keypad.KeypadKey
import com.ironlog.app.domain.keypad.KeypadPresets
import com.ironlog.app.domain.keypad.WeightStepper
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun KeypadSheet(
    keypad: KeypadUiState,
    displayUnit: WeightUnit,
    invalid: Boolean,
    onKey: (KeypadKey) -> Unit,
    onStep: (Int) -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = fieldLabel(keypad.field),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = keypad.keypadState.text.ifEmpty { "0" },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                textAlign = TextAlign.End,
            )
            if (invalid) {
                Text(
                    text = stringResource(R.string.keypad_invalid),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (keypad.field == SetField.WEIGHT) {
                val step = WeightStepper.defaultStepText(displayUnit)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onStep(-1) },
                        modifier = Modifier.weight(1f).height(Dimens.KeypadKeyHeight),
                    ) {
                        Text(stringResource(R.string.weight_step_minus, step))
                    }
                    OutlinedButton(
                        onClick = { onStep(+1) },
                        modifier = Modifier.weight(1f).height(Dimens.KeypadKeyHeight),
                    ) {
                        Text(stringResource(R.string.weight_step_plus, step))
                    }
                }
            }
            KeypadGrid(keypad = keypad, onKey = onKey, onDone = onDone)
        }
    }
}

@Composable
private fun KeypadGrid(keypad: KeypadUiState, onKey: (KeypadKey) -> Unit, onDone: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { rowDigits ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowDigits.forEach { digit ->
                    KeypadButton(
                        label = digit.toString(),
                        onClick = { onKey(KeypadKey.Digit(digit)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KeypadButton(
                label = ".",
                enabled = keypad.keypadState.allowDecimal,
                onClick = { onKey(KeypadKey.Dot) },
                modifier = Modifier.weight(1f),
            )
            KeypadButton(
                label = "0",
                onClick = { onKey(KeypadKey.Digit(0)) },
                modifier = Modifier.weight(1f),
            )
            KeypadButton(
                label = stringResource(R.string.keypad_backspace),
                onClick = { onKey(KeypadKey.Backspace) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { onKey(KeypadKey.Clear) },
                modifier = Modifier.weight(1f).height(Dimens.KeypadKeyHeight),
            ) {
                Text(stringResource(R.string.keypad_clear))
            }
            Button(
                onClick = onDone,
                modifier = Modifier.weight(2f).height(Dimens.KeypadKeyHeight),
            ) {
                Text(stringResource(R.string.keypad_done))
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(Dimens.KeypadKeyHeight),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun fieldLabel(field: SetField): String = when (field) {
    SetField.WEIGHT -> stringResource(R.string.field_weight)
    SetField.REPS -> stringResource(R.string.field_reps)
    SetField.RIR -> stringResource(R.string.field_rir)
    SetField.DURATION -> stringResource(R.string.field_duration_seconds)
    SetField.LEVEL -> stringResource(R.string.field_level)
    SetField.INCLINE -> stringResource(R.string.field_incline)
    SetField.SPEED -> stringResource(R.string.field_speed)
    SetField.DISTANCE -> stringResource(R.string.field_distance_m)
}

@Preview(showBackground = true)
@Composable
private fun KeypadSheetPreview() {
    IronLogTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            Text("120", modifier = Modifier.padding(8.dp))
            val sample = KeypadUiState(
                setId = "set-1",
                field = SetField.REPS,
                keypadState = KeypadPresets.REPS.copy(text = "120"),
            )
            Row(
                modifier = Modifier.padding(Dimens.ScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(sample.keypadState.text, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
