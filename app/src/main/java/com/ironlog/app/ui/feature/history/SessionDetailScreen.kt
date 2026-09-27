@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.content.Intent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ironlog.app.platform.share.ShareEntryPoint
import com.ironlog.app.ui.feature.summary.ShareCardRenderer
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.CardioEquipment
import com.ironlog.app.domain.model.CardioLayout
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.ui.feature.session.KeypadSheet
import com.ironlog.app.ui.feature.session.SetField
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

private val RATINGS = (5..10).toList()

@Composable
fun SessionDetailScreenRoute(
    sessionId: String,
    onBack: () -> Unit,
    onOpenSession: (String) -> Unit,
    onOpenExerciseHistory: (String) -> Unit,
    viewModel: SessionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val alreadyInProgressMessage = stringResource(R.string.detail_already_in_progress)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val chooserTitle = stringResource(R.string.share_chooser_title)
    val shareExporter = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ShareEntryPoint::class.java,
        ).shareImageExporter()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SessionDetailEvent.TemplateStarted -> onOpenSession(event.sessionId)
                is SessionDetailEvent.AlreadyInProgress -> {
                    snackbarHostState.showSnackbar(alreadyInProgressMessage)
                    event.sessionId?.let(onOpenSession)
                }
                SessionDetailEvent.SessionDeleted -> onBack()
            }
        }
    }

    SessionDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRatingSelected = viewModel::onRatingSelected,
        onNoteChanged = viewModel::onNoteChanged,
        onFieldClicked = viewModel::onFieldClicked,
        onSetTypeChanged = viewModel::onSetTypeChanged,
        onAddSet = viewModel::onAddSet,
        onDeleteSet = viewModel::onDeleteSet,
        onDeleteEntry = viewModel::onDeleteEntry,
        onStartTemplate = viewModel::onStartTemplate,
        onDeleteClicked = viewModel::onDeleteClicked,
        onOpenExerciseHistory = onOpenExerciseHistory,
        onShare = {
            val shareData = state.shareData
            if (shareData != null) {
                val bitmap = ShareCardRenderer.render(context, shareData)
                val intent = shareExporter.shareIntent(bitmap, state.shareFileName)
                context.startActivity(Intent.createChooser(intent, chooserTitle))
            }
        },
    )

    val keypad = state.keypad
    if (keypad != null) {
        KeypadSheet(
            keypad = keypad,
            displayUnit = state.displayUnit,
            invalid = false,
            onKey = viewModel::onKeypadKey,
            onStep = {},
            onDone = viewModel::onKeypadDone,
            onDismiss = viewModel::onKeypadDismiss,
        )
    }

    if (state.confirmDeleteVisible) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissDelete,
            title = { Text(stringResource(R.string.detail_delete_title)) },
            text = { Text(stringResource(R.string.detail_delete_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDelete) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDelete) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun SessionDetailScreen(
    state: SessionDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRatingSelected: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onFieldClicked: (String, SetField) -> Unit,
    onSetTypeChanged: (String, SetType) -> Unit,
    onAddSet: (String) -> Unit,
    onDeleteSet: (String) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onStartTemplate: () -> Unit,
    onDeleteClicked: () -> Unit,
    onOpenExerciseHistory: (String) -> Unit,
    onShare: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(text = state.localDate, style = MaterialTheme.typography.titleMedium)
                    SummaryLine(stringResource(R.string.summary_duration), state.durationText)
                    SummaryLine(stringResource(R.string.summary_work_sets), state.workSetCount.toString())
                    SummaryLine(
                        label = stringResource(R.string.summary_volume),
                        value = UnitConverter.formatGrams(state.volumeGrams, state.displayUnit),
                    )
                    Text(text = stringResource(R.string.summary_rating), style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RATINGS.forEach { value ->
                            FilterChip(
                                selected = state.rating == value,
                                onClick = { onRatingSelected(value) },
                                label = { Text(value.toString()) },
                            )
                        }
                    }
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = onNoteChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.summary_note)) },
                    )
                }
            }

            state.entries.forEach { entry ->
                EntryCard(
                    entry = entry,
                    onFieldClicked = onFieldClicked,
                    onSetTypeChanged = onSetTypeChanged,
                    onAddSet = { onAddSet(entry.entryId) },
                    onDeleteSet = onDeleteSet,
                    onDeleteEntry = { onDeleteEntry(entry.entryId) },
                    onOpenExerciseHistory = { onOpenExerciseHistory(entry.exerciseId) },
                )
            }

            Button(onClick = onStartTemplate, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.detail_start_template))
            }
            Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.detail_share))
            }
            TextButton(onClick = onDeleteClicked, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.detail_delete_session),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Dimens.CardSpacing))
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EntryCard(
    entry: DetailEntryUiState,
    onFieldClicked: (String, SetField) -> Unit,
    onSetTypeChanged: (String, SetType) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (String) -> Unit,
    onDeleteEntry: () -> Unit,
    onOpenExerciseHistory: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenExerciseHistory),
                )
                IconButton(onClick = onDeleteEntry) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.entry_delete),
                    )
                }
            }
            entry.sets.forEach { row ->
                DetailSetRow(
                    row = row,
                    isCardio = entry.kind == ExerciseKind.CARDIO,
                    layout = CardioEquipment.layoutFor(entry.equipment),
                    onFieldClicked = { field -> onFieldClicked(row.set.id, field) },
                    onSetTypeChanged = { type -> onSetTypeChanged(row.set.id, type) },
                    onDelete = { onDeleteSet(row.set.id) },
                )
            }
            TextButton(onClick = onAddSet, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.session_add_set))
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun DetailSetRow(
    row: DetailSetUiState,
    isCardio: Boolean,
    layout: CardioLayout,
    onFieldClicked: (SetField) -> Unit,
    onSetTypeChanged: (SetType) -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SetTypeButton(row.numberLabel, onSetTypeChanged)
        if (isCardio) {
            ReadOnlyCell(stringResource(R.string.field_duration), row.durationText, Modifier.weight(1.2f))
            when (layout) {
                CardioLayout.STAIR_CLIMBER ->
                    EditableCell(
                        stringResource(R.string.field_level),
                        row.levelText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.LEVEL) },
                    )

                CardioLayout.TREADMILL -> {
                    EditableCell(
                        stringResource(R.string.field_speed),
                        row.speedText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.SPEED) },
                    )
                    EditableCell(
                        stringResource(R.string.field_incline),
                        row.inclineText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.INCLINE) },
                    )
                    EditableCell(
                        stringResource(R.string.field_distance_m),
                        row.distanceText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.DISTANCE) },
                    )
                }

                CardioLayout.ELLIPTICAL_BIKE -> {
                    EditableCell(
                        stringResource(R.string.field_level),
                        row.levelText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.LEVEL) },
                    )
                    EditableCell(
                        stringResource(R.string.field_distance_m),
                        row.distanceText,
                        Modifier.weight(1f),
                        onClick = { onFieldClicked(SetField.DISTANCE) },
                    )
                }
            }
        } else {
            EditableCell(
                stringResource(R.string.field_weight),
                row.weightText,
                Modifier.weight(1f),
                onClick = { onFieldClicked(SetField.WEIGHT) },
            )
            EditableCell(
                stringResource(R.string.field_reps),
                row.repsText,
                Modifier.weight(1f),
                onClick = { onFieldClicked(SetField.REPS) },
            )
            EditableCell(
                stringResource(R.string.field_rir),
                row.set.rir?.toString().orEmpty(),
                Modifier.weight(0.7f),
                onClick = { onFieldClicked(SetField.RIR) },
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(Dimens.TouchTarget)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.action_delete),
            )
        }
    }
}

@Composable
private fun SetTypeButton(label: String, onSetTypeChanged: (SetType) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(Dimens.TouchTarget)
                .clickable { menuOpen = true }
                .padding(top = 14.dp),
        )
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            SetType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(typeLabel(type)) },
                    onClick = {
                        menuOpen = false
                        onSetTypeChanged(type)
                    },
                )
            }
        }
    }
}

@Composable
private fun EditableCell(label: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .height(Dimens.TouchTarget)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value.ifEmpty { "-" }, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ReadOnlyCell(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value.ifEmpty { "-" }, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun typeLabel(type: SetType): String = when (type) {
    SetType.WORK -> stringResource(R.string.set_type_work)
    SetType.WARMUP -> stringResource(R.string.set_type_warmup)
    SetType.DROP -> stringResource(R.string.set_type_drop)
    SetType.FAILURE -> stringResource(R.string.set_type_failure)
}

@Preview(showBackground = true)
@Composable
private fun SessionDetailScreenPreview() {
    IronLogTheme {
        SessionDetailScreen(
            snackbarHostState = remember { SnackbarHostState() },
            state = SessionDetailUiState(
                loading = false,
                localDate = "2026-09-24",
                durationText = "60:00",
                volumeGrams = 1_440_000,
                workSetCount = 3,
                rating = 8,
                note = "good",
                entries = listOf(
                    DetailEntryUiState(
                        entryId = "e1",
                        exerciseId = "bench",
                        exerciseName = "Bench Press",
                        kind = ExerciseKind.STRENGTH,
                        equipment = "barbell",
                        sets = listOf(
                            DetailSetUiState(
                                set = WorkoutSet(
                                    "set-1", "e1", 0, SetType.WORK, null, 60_000, WeightUnit.KG,
                                    8, null, null, null, null, null, null, null, true, 0, 0,
                                ),
                                numberLabel = "1",
                                weightText = "60",
                                repsText = "8",
                                durationText = "",
                                levelText = "",
                                inclineText = "",
                                speedText = "",
                                distanceText = "",
                            ),
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRatingSelected = {},
            onNoteChanged = {},
            onFieldClicked = { _, _ -> },
            onSetTypeChanged = { _, _ -> },
            onAddSet = {},
            onDeleteSet = {},
            onDeleteEntry = {},
            onStartTemplate = {},
            onDeleteClicked = {},
            onOpenExerciseHistory = {},
            onShare = {},
        )
    }
}
