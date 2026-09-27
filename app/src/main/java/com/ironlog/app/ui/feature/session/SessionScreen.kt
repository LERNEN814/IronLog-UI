@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.session

import android.Manifest
import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.platform.timer.ExactAlarmPermission
import com.ironlog.app.ui.feature.timer.RestTimerBar
import com.ironlog.app.ui.feature.timer.playRestFinishedAlert
import com.ironlog.app.domain.model.CardioEquipment
import com.ironlog.app.domain.model.CardioLayout
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.SessionExercise
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.summary.DurationText
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme
import kotlinx.coroutines.delay

@Composable
fun SessionScreenRoute(
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onFinished: () -> Unit,
    viewModel: SessionViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    DisposableKeepScreenOn(enabled = state.keepScreenOn, activity = activity)

    LaunchedEffect(state.session?.id) {
        if (state.session != null) {
            while (true) {
                viewModel.refreshElapsed()
                delay(1000L)
            }
        }
    }

    LaunchedEffect(state.restTimer?.sessionId) {
        if (state.restTimer != null) {
            while (true) {
                viewModel.refreshRestTimer()
                delay(500L)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val prMessage = stringResource(R.string.session_pr_message)

    var shakeSetId by remember { mutableStateOf<String?>(null) }
    var keypadInvalid by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(shakeSetId) {
        if (shakeSetId != null) {
            delay(400L)
            shakeSetId = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SessionEvent.NeedsInput -> shakeSetId = event.setId
                is SessionEvent.InvalidInput -> keypadInvalid = true
                SessionEvent.ConfirmDiscard -> showDiscardDialog = true
                is SessionEvent.Finished -> onFinished()
                is SessionEvent.Discarded -> onFinished()
                is SessionEvent.RestFinished -> playRestFinishedAlert(context)
                is SessionEvent.NewPersonalRecord -> snackbarHostState.showSnackbar(prMessage)
            }
        }
    }

    SessionScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        shakeSetId = shakeSetId,
        onBack = onBack,
        onAddExercise = onAddExercise,
        onUnitToggle = viewModel::onUnitToggle,
        onFinishClicked = viewModel::onFinishClicked,
        onFieldClicked = viewModel::onFieldClicked,
        onSetChecked = viewModel::onSetChecked,
        onSetTypeChanged = viewModel::onSetTypeChanged,
        onDeleteSet = viewModel::onDeleteSet,
        onAddSet = viewModel::onAddSet,
        onDeleteEntry = viewModel::onDeleteEntry,
        onMoveEntry = viewModel::onMoveEntry,
        onRestAdjust = viewModel::onRestAdjust,
        onRestSkip = viewModel::onRestSkip,
        onInexactClick = { showExactAlarmDialog = true },
    )

    val keypad = state.keypad
    if (keypad != null) {
        KeypadSheet(
            keypad = keypad,
            displayUnit = state.displayUnit,
            invalid = keypadInvalid,
            onKey = { key ->
                keypadInvalid = false
                viewModel.onKeypadKey(key)
            },
            onStep = viewModel::onWeightStep,
            onDone = viewModel::onKeypadDone,
            onDismiss = viewModel::onKeypadDismiss,
        )
    }

    if (state.summaryVisible) {
        SummarySheet(
            summary = state.summary,
            displayUnit = state.displayUnit,
            rating = state.rating,
            note = state.note,
            onRatingSelected = viewModel::onRatingSelected,
            onNoteChanged = viewModel::onNoteChanged,
            onConfirm = viewModel::onConfirmFinish,
            onKeepTraining = viewModel::onKeepTraining,
        )
    }

    if (state.restPickerVisible) {
        RestPickerSheet(
            presets = state.restPresets,
            defaultSeconds = state.defaultRestSeconds,
            onSelect = { seconds ->
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !ExactAlarmPermission.canPostNotifications(context)
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (
                    !ExactAlarmPermission.canScheduleExactAlarms(context) &&
                    !state.exactAlarmPrompted
                ) {
                    showExactAlarmDialog = true
                }
                viewModel.onRestPresetSelected(seconds)
            },
            onSkip = viewModel::onRestSkipped,
            onDismiss = viewModel::onRestSkipped,
        )
    }

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmDialog = false },
            title = { Text(stringResource(R.string.rest_exact_alarm_title)) },
            text = { Text(stringResource(R.string.rest_exact_alarm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExactAlarmDialog = false
                        viewModel.onExactAlarmPromptedShown()
                        context.startActivity(ExactAlarmPermission.exactAlarmSettingsIntent(context))
                    },
                ) {
                    Text(stringResource(R.string.action_goto_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_title)) },
            text = { Text(stringResource(R.string.discard_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.onConfirmDiscard()
                    },
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun DisposableKeepScreenOn(enabled: Boolean, activity: Activity?) {
    androidx.compose.runtime.DisposableEffect(enabled, activity) {
        if (enabled) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun SessionScreen(
    state: SessionUiState,
    snackbarHostState: SnackbarHostState,
    shakeSetId: String?,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onUnitToggle: () -> Unit,
    onFinishClicked: () -> Unit,
    onFieldClicked: (String, SetField) -> Unit,
    onSetChecked: (String) -> Unit,
    onSetTypeChanged: (String, SetType) -> Unit,
    onDeleteSet: (String) -> Unit,
    onAddSet: (String) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onMoveEntry: (String, Int) -> Unit,
    onRestAdjust: (Int) -> Unit,
    onRestSkip: () -> Unit,
    onInexactClick: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DurationText.mmss(state.elapsedSeconds),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onUnitToggle) {
                        Text(
                            text = stringResource(
                                if (state.displayUnit == WeightUnit.KG) R.string.unit_kg else R.string.unit_lb,
                            ),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column {
                val restTimer = state.restTimer
                if (restTimer != null) {
                    RestTimerBar(
                        model = restTimer,
                        onAdjust = onRestAdjust,
                        onSkip = onRestSkip,
                        onInexactClick = onInexactClick,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.ScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
                ) {
                    OutlinedButton(onClick = onAddExercise, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.session_add_exercise))
                    }
                    Button(onClick = onFinishClicked, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.session_finish))
                    }
                }
            }
        },
    ) { padding ->
        if (state.entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.ScreenPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.session_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize().padding(padding)) {
                ExerciseRail(
                    entries = state.entries,
                    modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 12.dp),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
                ) {
                    items(state.entries, key = { it.entry.id }) { entry ->
                        EntryCard(
                            entry = entry,
                            showRirField = state.showRirField,
                            shakeSetId = shakeSetId,
                            onAddSet = { onAddSet(entry.entry.id) },
                            onDeleteEntry = { onDeleteEntry(entry.entry.id) },
                            onMoveUp = { onMoveEntry(entry.entry.id, -1) },
                            onMoveDown = { onMoveEntry(entry.entry.id, 1) },
                            onFieldClicked = onFieldClicked,
                            onSetChecked = onSetChecked,
                            onSetTypeChanged = onSetTypeChanged,
                            onDeleteSet = onDeleteSet,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseRail(entries: List<EntryUiState>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(72.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entries.forEachIndexed { index, entry ->
            val completed = entry.sets.isNotEmpty() && entry.sets.all { it.set.isCompleted }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (completed) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(16.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.FitnessCenter,
                    contentDescription = entry.exercise?.nameZh ?: entry.entry.exerciseId,
                    tint = if (completed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun EntryCard(
    entry: EntryUiState,
    showRirField: Boolean,
    shakeSetId: String?,
    onAddSet: () -> Unit,
    onDeleteEntry: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onFieldClicked: (String, SetField) -> Unit,
    onSetChecked: (String) -> Unit,
    onSetTypeChanged: (String, SetType) -> Unit,
    onDeleteSet: (String) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onLongPress = { menuOpen = true })
                },
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.exercise?.nameZh ?: entry.entry.exerciseId,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    val subtitle = buildString {
                        append(entry.exercise?.equipment.orEmpty())
                        entry.primaryBodyRegion?.let { region ->
                            if (isNotEmpty()) append(" · ")
                            append(regionLabel(region))
                        }
                    }
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    val lastText = lastSetsText(entry.lastSets)
                    if (lastText.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.session_last_sets, lastText),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.action_more))
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.entry_move_up)) },
                            onClick = { menuOpen = false; onMoveUp() },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.entry_move_down)) },
                            onClick = { menuOpen = false; onMoveDown() },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.entry_delete)) },
                            onClick = { menuOpen = false; onDeleteEntry() },
                        )
                    }
                }
            }

            val isCardio = entry.exercise?.kind == ExerciseKind.CARDIO
            val layout = CardioEquipment.layoutFor(entry.exercise?.equipment.orEmpty())
            LinearProgressIndicator(
                progress = { entry.sets.count { it.set.isCompleted }.toFloat() / entry.sets.size.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            entry.sets.forEach { row ->
                SetRowItem(
                    row = row,
                    isCardio = isCardio,
                    layout = layout,
                    showRirField = showRirField,
                    shaking = shakeSetId == row.set.id,
                    onFieldClicked = { field -> onFieldClicked(row.set.id, field) },
                    onSetChecked = { onSetChecked(row.set.id) },
                    onSetTypeChanged = { type -> onSetTypeChanged(row.set.id, type) },
                    onDelete = { onDeleteSet(row.set.id) },
                )
            }

            TextButton(onClick = onAddSet, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(stringResource(R.string.session_add_set))
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun SetRowItem(
    row: SetRowUiState,
    isCardio: Boolean,
    layout: CardioLayout,
    showRirField: Boolean,
    shaking: Boolean,
    onFieldClicked: (SetField) -> Unit,
    onSetChecked: () -> Unit,
    onSetTypeChanged: (SetType) -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
    )
    val background = if (row.set.isCompleted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = Dimens.ScreenPadding),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = shakeOffset(shaking))
                .background(background, RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SetTypeLabel(
                label = row.numberLabel,
                setType = row.set.setType,
                onSetTypeChanged = onSetTypeChanged,
            )
            if (isCardio) {
                FieldCell(
                    label = stringResource(R.string.field_duration),
                    value = row.durationText,
                    isPlaceholder = row.isPlaceholder,
                    onClick = { onFieldClicked(SetField.DURATION) },
                    modifier = Modifier.weight(1.2f),
                )
                when (layout) {
                    CardioLayout.STAIR_CLIMBER -> FieldCell(
                        label = stringResource(R.string.field_level),
                        value = row.levelText,
                        isPlaceholder = row.isPlaceholder,
                        onClick = { onFieldClicked(SetField.LEVEL) },
                        modifier = Modifier.weight(1f),
                    )

                    CardioLayout.TREADMILL -> {
                        FieldCell(
                            label = stringResource(R.string.field_speed),
                            value = row.speedText,
                            isPlaceholder = row.isPlaceholder,
                            onClick = { onFieldClicked(SetField.SPEED) },
                            modifier = Modifier.weight(1f),
                        )
                        FieldCell(
                            label = stringResource(R.string.field_incline),
                            value = row.inclineText,
                            isPlaceholder = row.isPlaceholder,
                            onClick = { onFieldClicked(SetField.INCLINE) },
                            modifier = Modifier.weight(1f),
                        )
                        FieldCell(
                            label = stringResource(R.string.field_distance_m),
                            value = row.distanceText,
                            isPlaceholder = row.isPlaceholder,
                            onClick = { onFieldClicked(SetField.DISTANCE) },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    CardioLayout.ELLIPTICAL_BIKE -> {
                        FieldCell(
                            label = stringResource(R.string.field_level),
                            value = row.levelText,
                            isPlaceholder = row.isPlaceholder,
                            onClick = { onFieldClicked(SetField.LEVEL) },
                            modifier = Modifier.weight(1f),
                        )
                        FieldCell(
                            label = stringResource(R.string.field_distance_m),
                            value = row.distanceText,
                            isPlaceholder = row.isPlaceholder,
                            onClick = { onFieldClicked(SetField.DISTANCE) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                FieldCell(
                    label = stringResource(R.string.field_weight),
                    value = row.weightText,
                    isPlaceholder = row.isPlaceholder,
                    onClick = { onFieldClicked(SetField.WEIGHT) },
                    modifier = Modifier.weight(1f),
                )
                FieldCell(
                    label = stringResource(R.string.field_reps),
                    value = row.repsText,
                    isPlaceholder = row.isPlaceholder,
                    onClick = { onFieldClicked(SetField.REPS) },
                    modifier = Modifier.weight(1f),
                )
                if (showRirField) {
                    FieldCell(
                        label = stringResource(R.string.field_rir),
                        value = row.set.rir?.toString().orEmpty(),
                        isPlaceholder = false,
                        onClick = { onFieldClicked(SetField.RIR) },
                        modifier = Modifier.weight(0.7f),
                    )
                }
            }
            if (row.isPr) {
                Text(
                    text = stringResource(R.string.session_pr_badge),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            IconButton(onClick = onSetChecked, modifier = Modifier.size(Dimens.TouchTarget)) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(
                        if (row.set.isCompleted) R.string.set_mark_incomplete else R.string.set_mark_complete,
                    ),
                    tint = if (row.set.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun SetTypeLabel(label: String, setType: SetType, onSetTypeChanged: (SetType) -> Unit) {
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
                    text = { Text(setTypeLabel(type)) },
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
private fun FieldCell(
    label: String,
    value: String,
    isPlaceholder: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(Dimens.TouchTarget)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyLarge,
            color = if (isPlaceholder) {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun shakeOffset(active: Boolean): Dp {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(active) {
        if (active) {
            offset.snapTo(0f)
            repeat(3) {
                offset.animateTo(8f, tween(durationMillis = 40))
                offset.animateTo(-8f, tween(durationMillis = 40))
            }
            offset.animateTo(0f, tween(durationMillis = 40))
        }
    }
    return offset.value.dp
}

@Composable
private fun lastSetsText(sets: List<WorkoutSet>): String {
    val separator = stringResource(R.string.list_separator)
    val format = stringResource(R.string.set_summary_format)
    return sets.take(4).joinToString(separator) { set ->
        val duration = set.durationS
        if (duration != null) {
            UnitConverter.formatDuration(duration)
        } else {
            val weight = set.weightGrams?.toString().orEmpty()
            val reps = set.reps?.toString().orEmpty()
            format.format(weight, reps)
        }
    }
}

@Composable
private fun setTypeLabel(type: SetType): String = when (type) {
    SetType.WORK -> stringResource(R.string.set_type_work)
    SetType.WARMUP -> stringResource(R.string.set_type_warmup)
    SetType.DROP -> stringResource(R.string.set_type_drop)
    SetType.FAILURE -> stringResource(R.string.set_type_failure)
}

@Composable
private fun regionLabel(region: Int): String = when (region) {
    0 -> stringResource(R.string.region_chest)
    1 -> stringResource(R.string.region_back)
    2 -> stringResource(R.string.region_shoulders)
    3 -> stringResource(R.string.region_arms)
    4 -> stringResource(R.string.region_legs)
    5 -> stringResource(R.string.region_core)
    else -> stringResource(R.string.region_cardio)
}

@Preview(showBackground = true)
@Composable
private fun SessionScreenPreview() {
    IronLogTheme {
        val entry = EntryUiState(
            entry = SessionExercise("entry-1", "session-1", "bench", 0, null, 0, 0),
            exercise = Exercise(
                id = "bench",
                nameZh = "Bench Press",
                nameEn = "Bench Press",
                kind = ExerciseKind.STRENGTH,
                equipment = "barbell",
                primaryMuscleId = "chest",
                isCustom = false,
                isArchived = false,
                createdAt = 0,
                updatedAt = 0,
            ),
            primaryBodyRegion = 0,
            lastSets = listOf(WorkoutSet("last", "entry-0", 0, SetType.WORK, null, 60_000, WeightUnit.KG, 8, null, null, null, null, null, null, null, true, 0, 0)),
            sets = listOf(
                SetRowUiState(
                    set = WorkoutSet("set-1", "entry-1", 0, SetType.WORK, null, null, WeightUnit.KG, null, null, null, null, null, null, null, null, false, 0, 0),
                    placeholder = null,
                    numberLabel = "1",
                    isPlaceholder = true,
                    weightText = "60",
                    repsText = "8",
                    durationText = "",
                    levelText = "",
                    speedText = "",
                    inclineText = "",
                    distanceText = "",
                ),
            ),
        )
        SessionScreen(
            snackbarHostState = remember { SnackbarHostState() },
            state = SessionUiState(
                loading = false,
                session = WorkoutSession("session-1", SessionStatus.IN_PROGRESS, 0, null, "2026-01-01", null, null, null, 0, 0),
                entries = listOf(entry),
                elapsedSeconds = 754,
            ),
            shakeSetId = null,
            onBack = {},
            onAddExercise = {},
            onUnitToggle = {},
            onFinishClicked = {},
            onFieldClicked = { _, _ -> },
            onSetChecked = {},
            onSetTypeChanged = { _, _ -> },
            onDeleteSet = {},
            onAddSet = {},
            onDeleteEntry = {},
            onMoveEntry = { _, _ -> },
            onRestAdjust = {},
            onRestSkip = {},
            onInexactClick = {},
        )
    }
}
