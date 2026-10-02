package com.ironlog.app.ui.navigation

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ironlog.app.R
import com.ironlog.app.ui.theme.IronEffects

private enum class Tab(val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME(R.string.nav_home, Icons.Filled.Home),
    HISTORY(R.string.nav_history, Icons.Filled.DateRange),
    EXERCISES(R.string.nav_exercises, Icons.Filled.FitnessCenter),
    SETTINGS(R.string.nav_settings, Icons.Filled.Settings),
}

@Composable
fun IronLogBottomBar(navController: NavHostController, currentTab: TabTarget) {
    val containerColor = MaterialTheme.colorScheme.surface.copy(
        alpha = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            IronEffects.GlassAlpha
        } else {
            IronEffects.FallbackGlassAlpha
        },
    )
    NavigationBar(
        modifier = Modifier.border(
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        ),
        containerColor = containerColor,
        tonalElevation = 0.dp,
    ) {
        Tab.entries.forEach { tab ->
            val selected = when (tab) {
                Tab.HOME -> currentTab == TabTarget.HOME
                Tab.HISTORY -> currentTab == TabTarget.HISTORY
                Tab.EXERCISES -> currentTab == TabTarget.EXERCISES
                Tab.SETTINGS -> currentTab == TabTarget.SETTINGS
            }
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) return@NavigationBarItem
                    val route = when (tab) {
                        Tab.HOME -> HomeRoute
                        Tab.HISTORY -> HistoryRoute
                        Tab.EXERCISES -> ExercisePickerRoute
                        Tab.SETTINGS -> SettingsRoute
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.labelRes)) },
            )
        }
    }
}

enum class TabTarget { HOME, HISTORY, EXERCISES, SETTINGS, OTHER }
