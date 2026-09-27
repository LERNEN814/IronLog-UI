@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

/** R-3.1: presets + "其他" + "跳过" after a completed set. */
@Composable
fun RestPickerSheet(
    presets: List<Int>,
    defaultSeconds: Int,
    onSelect: (Int) -> Unit,
    onSkip: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var customDialogVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Text(
                text = stringResource(R.string.rest_picker_title),
                style = MaterialTheme.typography.titleMedium,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(presets) { seconds ->
                    FilterChip(
                        selected = seconds == defaultSeconds,
                        onClick = { onSelect(seconds) },
                        label = { Text(stringResource(R.string.rest_preset_format, seconds)) },
                    )
                }
                item {
                    TextButton(onClick = { customDialogVisible = true }) {
                        Text(stringResource(R.string.rest_other))
                    }
                }
            }
            TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.rest_skip))
            }
        }
    }

    if (customDialogVisible) {
        CustomRestDialog(
            onDismiss = { customDialogVisible = false },
            onConfirm = { seconds ->
                customDialogVisible = false
                onSelect(seconds)
            },
        )
    }
}

@Composable
private fun CustomRestDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var text by remember { mutableStateOf("") }
    val seconds = text.toIntOrNull()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rest_picker_custom_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { seconds?.let(onConfirm) },
                enabled = seconds != null && seconds in 5..3_600,
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun RestPickerSheetPreview() {
    IronLogTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(30, 90, 120, 150).forEach { seconds ->
                FilterChip(
                    selected = seconds == 90,
                    onClick = {},
                    label = { Text(stringResource(R.string.rest_preset_format, seconds)) },
                )
            }
        }
    }
}
