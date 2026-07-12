package com.devsnippets.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.repository.SnippetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Snippet> = emptyList(),
    val hasSearched: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SnippetRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = queryFlow
        .debounce(150) // instant-feeling search, but avoids querying on every keystroke burst
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchUiState(query = query, results = emptyList(), hasSearched = false))
            } else {
                repository.searchSnippets(query.trim()).map { results ->
                    SearchUiState(query = query, results = results, hasSearched = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChange(newQuery: String) {
        queryFlow.value = newQuery
    }

    fun toggleFavorite(snippet: Snippet) {
        viewModelScope.launch { repository.toggleFavorite(snippet.id, !snippet.isFavorite) }
    }
}
