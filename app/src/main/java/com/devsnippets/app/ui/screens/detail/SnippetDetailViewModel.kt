package com.devsnippets.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.repository.SnippetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SnippetDetailUiState(
    val snippet: Snippet? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

@HiltViewModel
class SnippetDetailViewModel @Inject constructor(
    private val repository: SnippetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val snippetId: Long = checkNotNull(savedStateHandle.get<String>("snippetId")).toLong()

    private val _uiState = MutableStateFlow(SnippetDetailUiState())
    val uiState: StateFlow<SnippetDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val snippet = repository.getSnippetById(snippetId)
            _uiState.value = SnippetDetailUiState(snippet = snippet, isLoading = false)
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value.snippet ?: return
        viewModelScope.launch {
            repository.toggleFavorite(current.id, !current.isFavorite)
            _uiState.value = _uiState.value.copy(snippet = current.copy(isFavorite = !current.isFavorite))
        }
    }

    fun togglePinned() {
        val current = _uiState.value.snippet ?: return
        viewModelScope.launch {
            repository.togglePinned(current.id, !current.isPinned)
            _uiState.value = _uiState.value.copy(snippet = current.copy(isPinned = !current.isPinned))
        }
    }

    fun deleteSnippet() {
        val current = _uiState.value.snippet ?: return
        viewModelScope.launch {
            repository.deleteSnippet(current)
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
