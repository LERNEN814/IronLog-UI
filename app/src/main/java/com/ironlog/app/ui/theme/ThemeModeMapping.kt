package com.ironlog.app.ui.theme

/** Settings value "theme_mode": system / light / dark. */
fun String.toThemeMode(): ThemeMode = when (this) {
    "light" -> ThemeMode.LIGHT
    "dark" -> ThemeMode.DARK
    else -> ThemeMode.SYSTEM
}

fun ThemeMode.toSetting(): String = when (this) {
    ThemeMode.SYSTEM -> "system"
    ThemeMode.LIGHT -> "light"
    ThemeMode.DARK -> "dark"
}
