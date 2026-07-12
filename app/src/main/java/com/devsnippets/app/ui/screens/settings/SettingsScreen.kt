package com.devsnippets.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.data.local.AppTheme
import com.devsnippets.app.ui.theme.ForestPrimary
import com.devsnippets.app.ui.theme.OceanPrimary
import com.devsnippets.app.ui.theme.PurplePrimary
import com.devsnippets.app.ui.theme.SunsetPrimary
import com.devsnippets.app.util.FileUtils
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onOpenStatistics: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportSnippets { json ->
                FileUtils.writeTextToUri(context, uri, json)
                scope.launch { snackbarHostState.showSnackbar("Snippets exported successfully") }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val text = FileUtils.readTextFromUri(context, uri)
            viewModel.importSnippets(text, replaceExisting = false) { result ->
                scope.launch {
                    result.fold(
                        onSuccess = { count -> snackbarHostState.showSnackbar("Imported $count snippets") },
                        onFailure = { snackbarHostState.showSnackbar("Import failed: invalid file") }
                    )
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { SectionLabel("Appearance") }
            item {
                SettingsSwitchRow(
                    title = "Dark mode",
                    subtitle = "Dev Snippets uses dark mode by default",
                    checked = state.isDarkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
            }
            item {
                ThemeSelector(selected = state.appTheme, onSelect = viewModel::setAppTheme)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                SettingsSwitchRow(
                    title = "Show line numbers",
                    subtitle = "Display line numbers in the code viewer",
                    checked = state.showLineNumbers,
                    onCheckedChange = viewModel::setShowLineNumbers
                )
            }

            item { SectionLabel("Editor") }
            item {
                SettingsSwitchRow(
                    title = "Auto-save",
                    subtitle = "Automatically save changes while editing",
                    checked = state.isAutoSaveEnabled,
                    onCheckedChange = viewModel::setAutoSave
                )
            }

            item { SectionLabel("Security") }
            item {
                SettingsSwitchRow(
                    title = "Biometric lock",
                    subtitle = "Require fingerprint or face unlock to open the app",
                    checked = state.isBiometricLockEnabled,
                    onCheckedChange = viewModel::setBiometricLock
                )
            }

            item { SectionLabel("Data") }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Upload,
                    title = "Export snippets as JSON",
                    subtitle = "Save all your snippets to a JSON backup file",
                    onClick = { exportLauncher.launch("dev_snippets_backup.json") }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Download,
                    title = "Import snippets from JSON",
                    subtitle = "Add snippets from a previously exported file",
                    onClick = { importLauncher.launch(arrayOf("application/json")) }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.BarChart,
                    title = "Statistics",
                    subtitle = "View snippet counts and language breakdown",
                    onClick = onOpenStatistics
                )
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ThemeSelector(selected: AppTheme, onSelect: (AppTheme) -> Unit) {
    val themes = listOf(
        Triple(AppTheme.PURPLE_NEON, "Purple Neon", PurplePrimary),
        Triple(AppTheme.OCEAN_BLUE, "Ocean Blue", OceanPrimary),
        Triple(AppTheme.FOREST_GREEN, "Forest Green", ForestPrimary),
        Triple(AppTheme.SUNSET_ORANGE, "Sunset Orange", SunsetPrimary)
    )
    Column {
        Text(text = "Theme", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            themes.forEach { (theme, label, color) ->
                ThemeSwatch(
                    color = color,
                    label = label,
                    isSelected = selected == theme,
                    onClick = { onSelect(theme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatch(color: Color, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color)
                .then(
                    if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(14.dp))
                    else Modifier
                )
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
