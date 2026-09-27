@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.exercise

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.MuscleGroup
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun ExercisePickerScreenRoute(
    onExerciseSelected: (String) -> Unit,
    onExerciseDetail: (String) -> Unit = {},
    onBack: () -> Unit,
    viewModel: ExercisePickerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ExercisePickerEvent.ExerciseSelected -> onExerciseSelected(event.exerciseId)
            }
        }
    }

    ExercisePickerScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onRegionSelected = viewModel::onRegionSelected,
        onExerciseClicked = viewModel::onExerciseClicked,
        onCreateClicked = viewModel::onShowCreateForm,
        onBack = onBack,
        onExerciseDetail = onExerciseDetail,
    )

    if (state.formVisible) {
        CustomExerciseSheet(
            muscleGroups = state.muscleGroups,
            onDismiss = viewModel::onDismissCreateForm,
            onSave = viewModel::onSaveCustom,
        )
    }
}

@Composable
fun ExercisePickerScreen(
    state: ExercisePickerUiState,
    onQueryChange: (String) -> Unit,
    onRegionSelected: (Int?) -> Unit,
    onExerciseClicked: (String) -> Unit,
    onCreateClicked: () -> Unit,
    onBack: () -> Unit,
    onExerciseDetail: (String) -> Unit = {},
    onExerciseDetail: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exercise_picker_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCreateClicked) {
                        Text(stringResource(R.string.exercise_new))
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.ScreenPadding),
                label = { Text(stringResource(R.string.exercise_search_hint)) },
                singleLine = true,
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Dimens.ScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = state.bodyRegion == null,
                        onClick = { onRegionSelected(null) },
                        label = { Text(stringResource(R.string.region_all)) },
                    )
                }
                items((0..6).toList()) { region ->
                    FilterChip(
                        selected = state.bodyRegion == region,
                        onClick = { onRegionSelected(region) },
                        label = { Text(regionLabel(region)) },
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = Dimens.ScreenPadding,
                    end = Dimens.ScreenPadding,
                    bottom = Dimens.ScreenPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.exercises, key = { it.id }) { exercise ->
                    ExerciseRow(
                        exercise = exercise,
                        primaryMuscleName = exercise.primaryMuscleId?.let { id ->
                            state.muscleGroups.firstOrNull { it.id == id }?.displayNameZh
                        },
                        isRecent = state.lastUsed.containsKey(exercise.id),
                        onClick = { onExerciseClicked(exercise.id) },
                        onDetail = { onExerciseDetail(exercise.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: Exercise,
    primaryMuscleName: String?,
    isRecent: Boolean,
    onClick: () -> Unit,
    onDetail: () -> Unit = {},
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                ExerciseThumbnail(exercise.id)
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = exercise.nameZh, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = exercise.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDetail) { Text(stringResource(R.string.exercise_details)) }
                if (isRecent) {
                    Tag(text = stringResource(R.string.exercise_recent))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tag(text = exercise.equipment)
                if (primaryMuscleName != null) Tag(text = primaryMuscleName)
            }
        }
    }
}

@Composable
private fun ExerciseThumbnail(exerciseId: String) {
    val context = LocalContext.current
    val assetExists = remember(exerciseId) {
        runCatching { context.assets.open(ExerciseMedia.thumbnailAsset(exerciseId)).close(); true }
            .getOrDefault(false)
    }
    if (assetExists) {
        Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    } else {
        Text(
            text = exerciseId.take(2).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Tag(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
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

private val EQUIPMENT_OPTIONS = listOf(
    "barbell", "dumbbell", "machine", "cable", "bodyweight",
    "treadmill", "stair_climber", "elliptical", "bike", "rower",
)

@Composable
fun CustomExerciseSheet(
    muscleGroups: List<MuscleGroup>,
    onDismiss: () -> Unit,
    onSave: (String, String, ExerciseKind, String, String?, List<String>) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var nameZh by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(ExerciseKind.STRENGTH) }
    var equipment by remember { mutableStateOf(EQUIPMENT_OPTIONS.first()) }
    var primaryMuscleId by remember { mutableStateOf<String?>(null) }
    var secondaryIds by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Text(
                text = stringResource(R.string.custom_exercise_title),
                style = MaterialTheme.typography.titleMedium,
            )
            OutlinedTextField(
                value = nameZh,
                onValueChange = { nameZh = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_name_zh)) },
                singleLine = true,
            )
            OutlinedTextField(
                value = nameEn,
                onValueChange = { nameEn = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_name_en)) },
                singleLine = true,
            )
            Text(text = stringResource(R.string.field_kind), style = MaterialTheme.typography.bodyMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExerciseKind.entries.toList()) { option ->
                    FilterChip(
                        selected = kind == option,
                        onClick = { kind = option },
                        label = { Text(kindLabel(option)) },
                    )
                }
            }
            DropdownField(
                label = stringResource(R.string.field_equipment),
                value = equipment,
                options = EQUIPMENT_OPTIONS,
            ) { equipment = it }
            DropdownField(
                label = stringResource(R.string.field_primary_muscle),
                value = muscleGroups.firstOrNull { it.id == primaryMuscleId }?.displayNameZh
                    ?: stringResource(R.string.field_not_selected),
                options = muscleGroups.map { it.id },
                optionLabel = { id -> muscleGroups.first { it.id == id }.displayNameZh },
            ) {
                primaryMuscleId = it
                secondaryIds = secondaryIds - it
            }
            Text(text = stringResource(R.string.field_secondary_muscles), style = MaterialTheme.typography.bodyMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(muscleGroups.filter { it.id != primaryMuscleId }) { muscle ->
                    FilterChip(
                        selected = muscle.id in secondaryIds,
                        onClick = {
                            secondaryIds = if (muscle.id in secondaryIds) {
                                secondaryIds - muscle.id
                            } else {
                                secondaryIds + muscle.id
                            }
                        },
                        label = { Text(muscle.displayNameZh) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.CardSpacing)) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
                Button(
                    onClick = { onSave(nameZh, nameEn, kind, equipment, primaryMuscleId, secondaryIds.toList()) },
                    enabled = nameZh.isNotBlank() && primaryMuscleId != null,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
            Spacer(Modifier.height(Dimens.CardSpacing))
        }
    }
}

@Composable
private fun <T> DropdownField(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String = { it.toString() },
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label: $value")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun kindLabel(kind: ExerciseKind): String = when (kind) {
    ExerciseKind.STRENGTH -> stringResource(R.string.kind_strength)
    ExerciseKind.CARDIO -> stringResource(R.string.kind_cardio)
    ExerciseKind.BODYWEIGHT -> stringResource(R.string.kind_bodyweight)
    ExerciseKind.TIMED -> stringResource(R.string.kind_timed)
}

@Preview(showBackground = true)
@Composable
private fun ExercisePickerScreenPreview() {
    IronLogTheme {
        ExercisePickerScreen(
            state = ExercisePickerUiState(
                loading = false,
                exercises = listOf(
                    Exercise(
                        id = "barbell_bench_press",
                        nameZh = "Bench Press",
                        nameEn = "Barbell Bench Press",
                        kind = ExerciseKind.STRENGTH,
                        equipment = "barbell",
                        primaryMuscleId = "chest",
                        isCustom = false,
                        isArchived = false,
                        createdAt = 0,
                        updatedAt = 0,
                    ),
                ),
                muscleGroups = listOf(
                    MuscleGroup("chest", "Chest", "Chest", 0, 48, 0),
                ),
                lastUsed = mapOf("barbell_bench_press" to 1L),
            ),
            onQueryChange = {},
            onRegionSelected = {},
            onExerciseClicked = {},
            onCreateClicked = {},
            onBack = {},
        )
    }
}
