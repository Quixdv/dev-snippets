package com.devsnippets.app.ui.navigation

/** Centralized navigation destinations for the whole app. */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")
    data object Statistics : Screen("statistics")

    data object SnippetDetail : Screen("snippet_detail/{snippetId}") {
        fun createRoute(snippetId: Long) = "snippet_detail/$snippetId"
    }

    data object SnippetEditor : Screen("snippet_editor?snippetId={snippetId}") {
        const val NEW_SNIPPET_ID = -1L
        fun createRoute(snippetId: Long = NEW_SNIPPET_ID) = "snippet_editor?snippetId=$snippetId"
    }
}

/** Bottom navigation bar destinations (subset of all screens). */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
