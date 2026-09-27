package com.ironlog.app.ui.feature.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironlog.app.R

@Composable
fun ExerciseDetailScreen(
    exerciseName: String,
    equipment: String,
    primaryMuscle: String = "",
    onAddToWorkout: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.padding(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.exercise_demo_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(stringResource(R.string.field_equipment), style = MaterialTheme.typography.titleMedium)
            Text(equipment.ifBlank { stringResource(R.string.exercise_equipment_pending) }, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.exercise_instruction_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.exercise_instruction_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.exercise_detail_primary_muscle), style = MaterialTheme.typography.labelLarge)
                Text(primaryMuscle.ifBlank { stringResource(R.string.exercise_detail_muscle_pending) }, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onAddToWorkout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) {
                Text(stringResource(R.string.exercise_add_to_workout))
            }
        }
    }
}
