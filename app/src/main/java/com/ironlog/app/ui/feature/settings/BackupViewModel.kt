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

enum class BackupMessageKind {
    EXPORT_SUCCESS,
    EXPORT_FAILURE,
    IMPORT_SUCCESS,
    IMPORT_FAILURE,
}

data class BackupMessage(val kind: BackupMessageKind, val count: Int = 0)

data class BackupUiState(val busy: Boolean = false, val message: BackupMessage? = null, val importPending: Uri? = null)

@HiltViewModel
class BackupViewModel @Inject constructor(private val repository: BackupRepository) : ViewModel() {
    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()
    fun export(uri: Uri) = viewModelScope.launch {
        _state.update { it.copy(busy = true) }
        repository.exportTo(uri).fold(
            { count -> _state.update { it.copy(busy = false, message = BackupMessage(BackupMessageKind.EXPORT_SUCCESS, count)) } },
            { _ -> _state.update { it.copy(busy = false, message = BackupMessage(BackupMessageKind.EXPORT_FAILURE)) } },
        )
    }
    fun requestImport(uri: Uri) { _state.update { it.copy(importPending = uri) } }
    fun dismissImport() { _state.update { it.copy(importPending = null) } }
    fun confirmImport() = viewModelScope.launch {
        val uri = _state.value.importPending ?: return@launch
        _state.update { it.copy(busy = true, importPending = null) }
        repository.importFrom(uri).fold(
            { _state.update { it.copy(busy = false, message = BackupMessage(BackupMessageKind.IMPORT_SUCCESS)) } },
            { _ -> _state.update { it.copy(busy = false, message = BackupMessage(BackupMessageKind.IMPORT_FAILURE)) } },
        )
    }
}
