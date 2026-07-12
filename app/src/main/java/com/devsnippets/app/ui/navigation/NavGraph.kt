package com.devsnippets.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsnippets.app.ui.screens.detail.SnippetDetailScreen
import com.devsnippets.app.ui.screens.editor.SnippetEditorScreen
import com.devsnippets.app.ui.screens.favorites.FavoritesScreen
import com.devsnippets.app.ui.screens.home.HomeScreen
import com.devsnippets.app.ui.screens.search.SearchScreen
import com.devsnippets.app.ui.screens.settings.SettingsScreen
import com.devsnippets.app.ui.screens.settings.SettingsViewModel
import com.devsnippets.app.ui.screens.statistics.StatisticsScreen

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Search, "Search", Icons.Filled.Search),
    BottomNavItem(Screen.Favorites, "Favorites", Icons.Filled.Star),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings)
)

/** Root composable: hosts the NavHost plus a bottom navigation bar and global FAB. */
@Composable
fun DevSnippetsNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBarAndFab = currentRoute in listOf(
        Screen.Home.route, Screen.Search.route, Screen.Favorites.route, Screen.Settings.route
    )

    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBarAndFab) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBarAndFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.SnippetEditor.createRoute()) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "New snippet")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(200)) { it / 8 } },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = { fadeOut(tween(150)) + slideOutHorizontally(tween(150)) { it / 8 } }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSnippetClick = { id -> navController.navigate(Screen.SnippetDetail.createRoute(id)) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onSnippetClick = { id -> navController.navigate(Screen.SnippetDetail.createRoute(id)) }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onSnippetClick = { id -> navController.navigate(Screen.SnippetDetail.createRoute(id)) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onOpenStatistics = { navController.navigate(Screen.Statistics.route) }
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.SnippetDetail.route,
                arguments = listOf(navArgument("snippetId") { type = NavType.StringType })
            ) {
                SnippetDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Screen.SnippetEditor.createRoute(id)) },
                    showLineNumbers = settingsState.showLineNumbers
                )
            }
            composable(
                route = Screen.SnippetEditor.route,
                arguments = listOf(
                    navArgument("snippetId") {
                        type = NavType.StringType
                        defaultValue = Screen.SnippetEditor.NEW_SNIPPET_ID.toString()
                    }
                )
            ) {
                SnippetEditorScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
