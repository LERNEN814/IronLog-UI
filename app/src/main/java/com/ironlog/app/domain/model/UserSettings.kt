package com.ironlog.app.domain.model

data class UserSettings(
    val weightUnit: WeightUnit = WeightUnit.KG,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val defaultRestSeconds: Int = 120,
    val restPresetsSeconds: String = "30,90,120,150",
    val keepScreenOnDuringWorkout: Boolean = true,
    val showRirField: Boolean = false,
    val themeMode: String = "system",
    val seedVersion: Int = 0,
    /** REST_TIMER section 8: true once the exact-alarm dialog was shown. */
    val exactAlarmPrompted: Boolean = false,
)
