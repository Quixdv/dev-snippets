package com.devsnippets.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.data.local.AppTheme
import com.devsnippets.app.data.local.UserPreferences
import com.devsnippets.app.domain.repository.SnippetRepository
import com.devsnippets.app.util.SnippetJsonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = true,
    val appTheme: AppTheme = AppTheme.PURPLE_NEON,
    val isBiometricLockEnabled: Boolean = false,
    val isAutoSaveEnabled: Boolean = true,
    val showLineNumbers: Boolean = true
)

sealed class SettingsEvent {
    data class ExportSuccess(val json: String) : SettingsEvent()
    data class ImportSuccess(val count: Int) : SettingsEvent()
    data class Error(val message: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences,
    private val repository: SnippetRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.isDarkMode,
        preferences.appTheme,
        preferences.isBiometricLockEnabled,
        preferences.isAutoSaveEnabled,
        preferences.showLineNumbers
    ) { dark, theme, biometric, autoSave, lineNumbers ->
        SettingsUiState(dark, theme, biometric, autoSave, lineNumbers)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { preferences.setDarkMode(enabled) }

    fun setAppTheme(theme: AppTheme) = viewModelScope.launch { preferences.setAppTheme(theme) }

    fun setBiometricLock(enabled: Boolean) = viewModelScope.launch { preferences.setBiometricLock(enabled) }

    fun setAutoSave(enabled: Boolean) = viewModelScope.launch { preferences.setAutoSave(enabled) }

    fun setShowLineNumbers(enabled: Boolean) = viewModelScope.launch { preferences.setShowLineNumbers(enabled) }

    /** Serializes all snippets to a JSON string; the caller writes it to a Uri. */
    fun exportSnippets(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val snippets = repository.getAllSnippetsOnce()
            onResult(SnippetJsonUtils.export(snippets))
        }
    }

    /** Parses a JSON string and inserts the contained snippets (used for import & restore). */
    fun importSnippets(jsonText: String, replaceExisting: Boolean, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            try {
                val snippets = SnippetJsonUtils.import(jsonText)
                if (replaceExisting) repository.deleteAll()
                repository.insertAll(snippets)
                onResult(Result.success(snippets.size))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}
