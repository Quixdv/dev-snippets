package com.devsnippets.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dev_snippets_settings")

/** App-wide color theme presets, selectable from Settings. */
enum class AppTheme {
    PURPLE_NEON, OCEAN_BLUE, FOREST_GREEN, SUNSET_ORANGE;

    companion object {
        fun fromName(name: String): AppTheme = entries.firstOrNull { it.name == name } ?: PURPLE_NEON
    }
}

/**
 * Thin wrapper around Jetpack DataStore for lightweight app settings that
 * don't belong in Room (theme choice, biometric lock, auto-save toggle...).
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val THEME = stringPreferencesKey("app_theme")
        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val LINE_NUMBERS = booleanPreferencesKey("line_numbers")
    }

    /** Defaults to true — the app is dark mode by default. */
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[Keys.DARK_MODE] ?: true }

    val appTheme: Flow<AppTheme> = dataStore.data.map {
        AppTheme.fromName(it[Keys.THEME] ?: AppTheme.PURPLE_NEON.name)
    }

    val isBiometricLockEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.BIOMETRIC_LOCK] ?: false }

    val isAutoSaveEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.AUTO_SAVE] ?: true }

    val showLineNumbers: Flow<Boolean> = dataStore.data.map { it[Keys.LINE_NUMBERS] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setAppTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        dataStore.edit { it[Keys.BIOMETRIC_LOCK] = enabled }
    }

    suspend fun setAutoSave(enabled: Boolean) {
        dataStore.edit { it[Keys.AUTO_SAVE] = enabled }
    }

    suspend fun setShowLineNumbers(enabled: Boolean) {
        dataStore.edit { it[Keys.LINE_NUMBERS] = enabled }
    }
}
