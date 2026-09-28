package com.ironlog.app.ui.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigation routes. */
@Serializable
data object HomeRoute

@Serializable
data object HistoryRoute

@Serializable
data object SettingsRoute

@Serializable
data object BodyWeightRoute

@Serializable
data object ExercisePickerRoute

@Serializable
data class SessionRoute(val sessionId: String)

@Serializable
data class SessionSummaryRoute(val sessionId: String)

@Serializable
data class SessionDetailRoute(val sessionId: String)

@Serializable
data class ExerciseHistoryRoute(val exerciseId: String)

@Serializable
data class ExerciseDetailRoute(val exerciseId: String)

/** Key used in savedStateHandle to return a picker selection to the caller. */
const val SELECTED_EXERCISE_ID = "selected_exercise_id"
