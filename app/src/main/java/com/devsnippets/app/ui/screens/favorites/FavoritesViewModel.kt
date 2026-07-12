package com.devsnippets.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.repository.SnippetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: SnippetRepository
) : ViewModel() {

    val favorites: StateFlow<List<Snippet>> = repository.getFavoriteSnippets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(snippet: Snippet) {
        viewModelScope.launch { repository.toggleFavorite(snippet.id, !snippet.isFavorite) }
    }

    fun togglePinned(snippet: Snippet) {
        viewModelScope.launch { repository.togglePinned(snippet.id, !snippet.isPinned) }
    }
}
