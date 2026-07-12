package com.devsnippets.app.ui.screens.favorites

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

@Composable
fun FavoritesScreen(
    onSnippetClick: (Long) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (favorites.isEmpty()) {
            EmptyState(
                title = "No favorites yet",
                subtitle = "Tap the star on a snippet to pin it here for quick access."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites, key = { it.id }) { snippet ->
                    SnippetCard(
                        snippet = snippet,
                        onClick = { onSnippetClick(snippet.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(snippet) },
                        onPinClick = { viewModel.togglePinned(snippet) }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}
