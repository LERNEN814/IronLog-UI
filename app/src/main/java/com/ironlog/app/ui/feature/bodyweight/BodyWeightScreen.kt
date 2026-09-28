@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.bodyweight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.WeightUnit

@Composable
fun BodyWeightScreenRoute(onBack: () -> Unit, viewModel: BodyWeightViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BodyWeightScreen(state, viewModel::save, viewModel::delete, onBack)
}

@Composable
fun BodyWeightScreen(state: BodyWeightUiState, onSave: (Int) -> Unit, onDelete: (String) -> Unit, onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.body_weight_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.body_weight_today, state.today), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(input, { input = it }, Modifier.weight(1f), label = { Text(stringResource(R.string.body_weight_input)) }, singleLine = true)
                Button(onClick = { UnitConverter.parseToGrams(input, state.unit)?.let(onSave); input = "" }) { Text(stringResource(R.string.action_save)) }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.entries, key = { it.id }) { entry ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.localDate)
                        Text(UnitConverter.formatGrams(entry.weightGrams, state.unit))
                        IconButton(onClick = { onDelete(entry.localDate) }) { Icon(Icons.Filled.Delete, stringResource(R.string.action_delete)) }
                    }
                }
            }
        }
    }
}
