package com.devsnippets.app.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.ui.components.LanguageBadge
import com.devsnippets.app.ui.components.TagChip
import com.devsnippets.app.ui.components.CodeViewer
import com.devsnippets.app.util.copySnippetToClipboard
import com.devsnippets.app.util.shareSnippetAsText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    showLineNumbers: Boolean,
    viewModel: SnippetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.snippet?.title ?: "Snippet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    state.snippet?.let { snippet ->
                        IconButton(onClick = { onEdit(snippet.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val snippet = state.snippet
        if (snippet == null) {
            if (!state.isLoading) {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Snippet not found")
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LanguageBadge(languageName = snippet.language.displayName)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.togglePinned() }) {
                    Icon(
                        imageVector = if (snippet.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (snippet.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        imageVector = if (snippet.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (snippet.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (snippet.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    snippet.tags.forEach { TagChip(it) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            CodeViewer(
                code = snippet.code,
                language = snippet.language,
                showLineNumbers = showLineNumbers,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = {
                    copySnippetToClipboard(context, snippet)
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy")
                }
                FilledTonalButton(onClick = {
                    shareSnippetAsText(context, snippet)
                }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            }

            if (snippet.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = snippet.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete snippet?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSnippet()
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
