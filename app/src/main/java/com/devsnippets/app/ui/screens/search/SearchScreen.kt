package com.devsnippets.app.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.ui.components.EmptyState
import com.devsnippets.app.ui.components.SnippetCard
import com.devsnippets.app.ui.components.SnippetSearchBar

@Composable
fun SearchScreen(
    onSnippetClick: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        SnippetSearchBar(query = state.query, onQueryChange = viewModel::onQueryChange)

        Spacer(modifier = Modifier.height(12.dp))

        when {
            !state.hasSearched -> EmptyState(
                title = "Search your snippets",
                subtitle = "Instantly find anything by title, language, tags or code content."
            )
            state.results.isEmpty() -> EmptyState(
                title = "No results",
                subtitle = "Try a different keyword or tag."
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.results, key = { it.id }) { snippet ->
                    SnippetCard(
                        snippet = snippet,
                        onClick = { onSnippetClick(snippet.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(snippet) },
                        onPinClick = { }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}
