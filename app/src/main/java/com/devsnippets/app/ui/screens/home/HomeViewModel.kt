package com.devsnippets.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.model.SnippetLanguage
import com.devsnippets.app.domain.repository.SnippetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val allSnippets: List<Snippet> = emptyList(),
    val pinnedSnippets: List<Snippet> = emptyList(),
    val recentSnippets: List<Snippet> = emptyList(),
    val selectedCategory: SnippetLanguage? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SnippetRepository
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<SnippetLanguage?>(null)

    private val allSnippetsFlow = repository.getAllSnippets()
    private val pinnedFlow = repository.getPinnedSnippets()
    private val recentFlow = repository.getRecentSnippets(8)

    val uiState: StateFlow<HomeUiState> = combine(
        allSnippetsFlow, pinnedFlow, recentFlow, selectedCategory
    ) { all, pinned, recent, category ->
        val filtered = if (category == null) all else all.filter { it.language == category }
        HomeUiState(
            allSnippets = filtered,
            pinnedSnippets = pinned,
            recentSnippets = recent,
            selectedCategory = category,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun selectCategory(category: SnippetLanguage?) {
        selectedCategory.value = category
    }

    fun toggleFavorite(snippet: Snippet) {
        viewModelScope.launch {
            repository.toggleFavorite(snippet.id, !snippet.isFavorite)
        }
    }

    fun togglePinned(snippet: Snippet) {
        viewModelScope.launch {
            repository.togglePinned(snippet.id, !snippet.isPinned)
        }
    }

    fun deleteSnippet(snippet: Snippet) {
        viewModelScope.launch {
            repository.deleteSnippet(snippet)
        }
    }
}
