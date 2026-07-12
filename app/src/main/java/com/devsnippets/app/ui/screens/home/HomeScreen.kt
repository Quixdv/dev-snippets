package com.devsnippets.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.ui.components.CategoryChipRow
import com.devsnippets.app.ui.components.EmptyState
import com.devsnippets.app.ui.components.SnippetCard

@Composable
fun HomeScreen(
    onSnippetClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Dev Snippets",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your personal code library",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            CategoryChipRow(
                selected = state.selectedCategory,
                onSelect = viewModel::selectCategory,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (state.pinnedSnippets.isNotEmpty() && state.selectedCategory == null) {
            item { SectionHeader("Pinned") }
            items(state.pinnedSnippets, key = { "pinned_${it.id}" }) { snippet ->
                SnippetCard(
                    snippet = snippet,
                    onClick = { onSnippetClick(snippet.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(snippet) },
                    onPinClick = { viewModel.togglePinned(snippet) }
                )
            }
        }

        if (state.recentSnippets.isNotEmpty() && state.selectedCategory == null) {
            item { SectionHeader("Recent") }
        }

        val listToShow: List<Snippet> = if (state.selectedCategory == null) {
            state.recentSnippets
        } else {
            state.allSnippets
        }

        if (state.selectedCategory != null) {
            item { SectionHeader(state.selectedCategory!!.displayName) }
        }

        if (!state.isLoading && listToShow.isEmpty()) {
            item {
                EmptyState(
                    title = "No snippets yet",
                    subtitle = "Tap the + button to create your first code snippet."
                )
            }
        } else {
            items(listToShow, key = { it.id }) { snippet ->
                SnippetCard(
                    snippet = snippet,
                    onClick = { onSnippetClick(snippet.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(snippet) },
                    onPinClick = { viewModel.togglePinned(snippet) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}
