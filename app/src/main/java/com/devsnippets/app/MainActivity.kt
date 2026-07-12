package com.devsnippets.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.data.local.UserPreferences
import com.devsnippets.app.ui.navigation.DevSnippetsNavHost
import com.devsnippets.app.ui.theme.DevSnippetsTheme
import com.devsnippets.app.util.BiometricHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Single-activity host for the whole Compose app.
 * Uses FragmentActivity (not plain ComponentActivity) because the optional
 * biometric lock feature requires a FragmentActivity to show its prompt.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val rootViewModel: RootViewModel = hiltViewModel()
            val rootState by rootViewModel.state.collectAsState()

            DevSnippetsTheme(darkTheme = rootState.isDarkMode, appTheme = rootState.appTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var isUnlocked by remember { mutableStateOf(false) }

                    LaunchedEffect(rootState.isBiometricLockEnabled) {
                        if (rootState.isBiometricLockEnabled && !isUnlocked) {
                            if (BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                                BiometricHelper.showPrompt(
                                    activity = this@MainActivity,
                                    onSuccess = { isUnlocked = true },
                                    onError = { /* user can retry via Settings, keep locked */ }
                                )
                            } else {
                                // No biometric hardware/enrollment available; don't block the user out.
                                isUnlocked = true
                            }
                        }
                    }

                    if (!rootState.isBiometricLockEnabled || isUnlocked) {
                        DevSnippetsNavHost()
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Authenticate to continue", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}

data class RootUiState(
    val isDarkMode: Boolean = true,
    val appTheme: com.devsnippets.app.data.local.AppTheme = com.devsnippets.app.data.local.AppTheme.PURPLE_NEON,
    val isBiometricLockEnabled: Boolean = false
)

@HiltViewModel
class RootViewModel @Inject constructor(
    preferences: UserPreferences
) : ViewModel() {
    val state = combine(
        preferences.isDarkMode,
        preferences.appTheme,
        preferences.isBiometricLockEnabled
    ) { dark, theme, biometric ->
        RootUiState(dark, theme, biometric)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RootUiState())
}
