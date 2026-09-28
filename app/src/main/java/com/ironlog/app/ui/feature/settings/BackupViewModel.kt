package com.ironlog.app.ui.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.app.data.backup.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BackupUiState(val busy: Boolean = false, val message: String? = null, val importPending: Uri? = null)

@HiltViewModel
class BackupViewModel @Inject constructor(private val repository: BackupRepository) : ViewModel() {
    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()
    fun export(uri: Uri) = viewModelScope.launch { _state.update { it.copy(busy = true) }; repository.exportTo(uri).fold({ count -> _state.update { it.copy(busy = false, message = "Export complete: $count records") } }, { e -> _state.update { it.copy(busy = false, message = e.message ?: "Export failed") } }) }
    fun requestImport(uri: Uri) { _state.update { it.copy(importPending = uri) } }
    fun dismissImport() { _state.update { it.copy(importPending = null) } }
    fun confirmImport() = viewModelScope.launch { val uri = _state.value.importPending ?: return@launch; _state.update { it.copy(busy = true, importPending = null) }; repository.importFrom(uri).fold({ _state.update { it.copy(busy = false, message = "Import complete") } }, { e -> _state.update { it.copy(busy = false, message = e.message ?: "Import failed") } }) }
}
