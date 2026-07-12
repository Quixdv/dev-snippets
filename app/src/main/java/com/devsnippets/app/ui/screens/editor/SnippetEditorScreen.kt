package com.devsnippets.app.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.domain.model.SnippetLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetEditorScreen(
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: SnippetEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Snippet" else "New Snippet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveSnippet(onSaved) },
                        enabled = state.isValid
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = languageMenuExpanded,
                onExpandedChange = { languageMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.language.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language / Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageMenuExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    SnippetLanguage.entries.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language.displayName) },
                            onClick = {
                                viewModel.onLanguageChange(language)
                                languageMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.code,
                onValueChange = viewModel::onCodeChange,
                label = { Text("Code") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.tagsInput,
                onValueChange = viewModel::onTagsChange,
                label = { Text("Tags (comma separated)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Markdown notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = state.isFavorite,
                    onClick = viewModel::toggleFavorite,
                    label = { Text("Favorite") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = state.isPinned,
                    onClick = viewModel::togglePinned,
                    label = { Text("Pinned") }
                )
            }

            if (state.lastAutoSaveText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = state.lastAutoSaveText ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
