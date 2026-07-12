package com.devsnippets.app.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.data.local.UserPreferences
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.model.SnippetLanguage
import com.devsnippets.app.domain.repository.SnippetRepository
import com.devsnippets.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SnippetEditorUiState(
    val id: Long = 0L,
    val title: String = "",
    val code: String = "",
    val language: SnippetLanguage = SnippetLanguage.KOTLIN,
    val tagsInput: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val lastAutoSaveText: String? = null
) {
    val isValid: Boolean get() = title.isNotBlank() && code.isNotBlank()
}

@HiltViewModel
class SnippetEditorViewModel @Inject constructor(
    private val repository: SnippetRepository,
    private val preferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val snippetId: Long = savedStateHandle.get<String>("snippetId")?.toLongOrNull()
        ?: Screen.SnippetEditor.NEW_SNIPPET_ID

    private val _uiState = MutableStateFlow(SnippetEditorUiState(isEditing = snippetId != Screen.SnippetEditor.NEW_SNIPPET_ID))
    val uiState: StateFlow<SnippetEditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null
    private var autoSaveEnabled = true

    init {
        viewModelScope.launch {
            autoSaveEnabled = preferences.isAutoSaveEnabled.first()
        }
        if (snippetId != Screen.SnippetEditor.NEW_SNIPPET_ID) {
            loadSnippet(snippetId)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun loadSnippet(id: Long) {
        viewModelScope.launch {
            val snippet = repository.getSnippetById(id)
            if (snippet != null) {
                _uiState.value = SnippetEditorUiState(
                    id = snippet.id,
                    title = snippet.title,
                    code = snippet.code,
                    language = snippet.language,
                    tagsInput = snippet.tags.joinToString(", "),
                    notes = snippet.notes,
                    isFavorite = snippet.isFavorite,
                    isPinned = snippet.isPinned,
                    isEditing = true,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onTitleChange(value: String) = updateAndMaybeAutoSave { it.copy(title = value) }
    fun onCodeChange(value: String) = updateAndMaybeAutoSave { it.copy(code = value) }
    fun onLanguageChange(value: SnippetLanguage) = updateAndMaybeAutoSave { it.copy(language = value) }
    fun onTagsChange(value: String) = updateAndMaybeAutoSave { it.copy(tagsInput = value) }
    fun onNotesChange(value: String) = updateAndMaybeAutoSave { it.copy(notes = value) }

    fun toggleFavorite() = updateAndMaybeAutoSave { it.copy(isFavorite = !it.isFavorite) }
    fun togglePinned() = updateAndMaybeAutoSave { it.copy(isPinned = !it.isPinned) }

    private inline fun updateAndMaybeAutoSave(transform: (SnippetEditorUiState) -> SnippetEditorUiState) {
        _uiState.value = transform(_uiState.value)
        scheduleAutoSave()
    }

    /** Debounced auto-save: only triggers for existing (already-created) snippets. */
    private fun scheduleAutoSave() {
        if (!autoSaveEnabled || !_uiState.value.isEditing || !_uiState.value.isValid) return
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1200)
            persist(silent = true)
        }
    }

    /** Explicit Save button handler. Creates or updates depending on editing state. */
    fun saveSnippet(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val id = persist(silent = false)
            if (id != null) onSaved(id)
        }
    }

    private suspend fun persist(silent: Boolean): Long? {
        val state = _uiState.value
        if (!state.isValid) return null

        val tags = state.tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val now = System.currentTimeMillis()

        return if (state.isEditing) {
            val snippet = Snippet(
                id = state.id,
                title = state.title.trim(),
                code = state.code,
                language = state.language,
                tags = tags,
                notes = state.notes,
                isFavorite = state.isFavorite,
                isPinned = state.isPinned,
                updatedAt = now
            )
            repository.updateSnippet(snippet)
            if (silent) {
                _uiState.value = _uiState.value.copy(lastAutoSaveText = "Auto-saved")
            }
            state.id
        } else {
            val snippet = Snippet(
                title = state.title.trim(),
                code = state.code,
                language = state.language,
                tags = tags,
                notes = state.notes,
                isFavorite = state.isFavorite,
                isPinned = state.isPinned,
                createdAt = now,
                updatedAt = now
            )
            val newId = repository.insertSnippet(snippet)
            _uiState.value = _uiState.value.copy(id = newId, isEditing = true, isSaved = true)
            newId
        }
    }
}
