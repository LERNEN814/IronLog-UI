package com.ironlog.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ironlog.app.ui.feature.calendar.CalendarTabRoute
import com.ironlog.app.ui.feature.exercise.ExerciseHistoryScreenRoute
import com.ironlog.app.ui.feature.exercise.ExerciseDetailScreen
import com.ironlog.app.ui.feature.exercise.ExercisePickerScreenRoute
import com.ironlog.app.ui.feature.history.SessionDetailScreenRoute
import com.ironlog.app.ui.feature.home.HomeScreenRoute
import com.ironlog.app.ui.feature.session.SessionScreenRoute
import com.ironlog.app.ui.feature.session.SessionViewModel
import com.ironlog.app.ui.feature.settings.SettingsScreenRoute
import com.ironlog.app.ui.feature.summary.SessionSummaryScreen

@Composable
fun IronLogNavHost(
    openSessionId: String? = null,
    onSessionOpened: () -> Unit = {},
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val tab = when {
        destination?.hasRoute(HomeRoute::class) == true -> TabTarget.HOME
        destination?.hasRoute(HistoryRoute::class) == true -> TabTarget.HISTORY
        destination?.hasRoute(ExercisePickerRoute::class) == true -> TabTarget.EXERCISES
        destination?.hasRoute(SettingsRoute::class) == true -> TabTarget.SETTINGS
        else -> TabTarget.OTHER
    }

    LaunchedEffect(openSessionId) {
        if (openSessionId != null) {
            navController.navigate(SessionRoute(openSessionId)) { launchSingleTop = true }
            onSessionOpened()
        }
    }

    Scaffold(
        bottomBar = {
            if (tab != TabTarget.OTHER) {
                IronLogBottomBar(navController = navController, currentTab = tab)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable<HomeRoute> {
                HomeScreenRoute(
                    onOpenSession = { sessionId -> navController.navigate(SessionRoute(sessionId)) },
                    onOpenHistory = { navController.navigate(HistoryRoute) },
                )
            }
            composable<HistoryRoute> {
                CalendarTabRoute(
                    onOpenDetail = { sessionId -> navController.navigate(SessionDetailRoute(sessionId)) },
                )
            }
            composable<ExercisePickerRoute> {
                ExercisePickerScreenRoute(
                    onExerciseSelected = { exerciseId ->
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set(SELECTED_EXERCISE_ID, exerciseId)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<SessionDetailRoute> { entry ->
                SessionDetailScreenRoute(
                    sessionId = entry.arguments?.getString("sessionId").orEmpty(),
                    onBack = { navController.popBackStack() },
                    onOpenSession = { sessionId ->
                        navController.navigate(SessionRoute(sessionId)) { launchSingleTop = true }
                    },
                    onOpenExerciseHistory = { exerciseId ->
                        navController.navigate(ExerciseHistoryRoute(exerciseId))
                    },
                )
            }
            composable<ExerciseHistoryRoute> { entry ->
                ExerciseHistoryScreenRoute(
                    exerciseId = entry.arguments?.getString("exerciseId").orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }
            composable<ExerciseDetailRoute> { entry ->
                ExerciseDetailScreen(
                    exerciseName = entry.arguments?.getString("exerciseId").orEmpty(),
                    equipment = "",
                    onBack = { navController.popBackStack() },
                )
            }
            composable<SettingsRoute> {
                SettingsScreenRoute()
            }
            composable<SessionRoute> { entry ->
                val viewModel: SessionViewModel = hiltViewModel()
                val selectedExerciseId by entry.savedStateHandle
                    .getStateFlow<String?>(SELECTED_EXERCISE_ID, null)
                    .collectAsStateWithLifecycle()
                LaunchedEffect(selectedExerciseId) {
                    val exerciseId = selectedExerciseId
                    if (exerciseId != null) {
                        viewModel.onExerciseSelected(exerciseId)
                        entry.savedStateHandle[SELECTED_EXERCISE_ID] = null
                    }
                }
                SessionScreenRoute(
                    onBack = { navController.popBackStack() },
                    onAddExercise = { navController.navigate(ExercisePickerRoute) },
                    onFinished = { navController.popBackStack(HomeRoute, inclusive = false) },
                    viewModel = viewModel,
                )
            }
            composable<SessionSummaryRoute> {
                SessionSummaryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
