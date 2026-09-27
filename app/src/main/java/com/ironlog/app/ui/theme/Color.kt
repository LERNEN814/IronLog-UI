package com.ironlog.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ironlog.app.domain.summary.BodyRegionColors

/** Light palette. */
val IronRed = Color(0xFF7652D8)
val IronOnRed = Color(0xFFFFFFFF)
val IronRedContainer = Color(0xFFE8DEFF)
val IronOnRedContainer = Color(0xFF24104F)
val IronSurface = Color(0xFFFBF8FF)
val IronSurfaceVariant = Color(0xFFE9E1F2)
val IronOnSurface = Color(0xFF1C1A20)
val IronOnSurfaceVariant = Color(0xFF514A58)
val IronOutline = Color(0xFF817889)
val IronBackground = Color(0xFFFBF8FF)
val IronOnBackground = Color(0xFF1C1A20)

/** Dark palette. */
val IronRedDark = Color(0xFFD0BCFF)
val IronOnRedDark = Color(0xFF3B1C7A)
val IronRedContainerDark = Color(0xFF5B3FAE)
val IronOnRedContainerDark = Color(0xFFE8DEFF)
val IronSurfaceDark = Color(0xFF15131A)
val IronSurfaceVariantDark = Color(0xFF4A4352)
val IronOnSurfaceDark = Color(0xFFE8E0EA)
val IronOnSurfaceVariantDark = Color(0xFFCCC2D1)
val IronOutlineDark = Color(0xFF958C9D)
val IronBackgroundDark = Color(0xFF15131A)
val IronOnBackgroundDark = Color(0xFFE8E0EA)

/** PRD R-4.2 body region colors, sourced from the domain table. */
fun regionColor(bodyRegion: Int): Color = Color(BodyRegionColors.colorFor(bodyRegion))

/** Touch target / keypad metrics (PRD N-4). */
object Dimens {
    val TouchTarget: Dp = 48.dp
    val KeypadKeyHeight: Dp = 56.dp
    val ScreenPadding: Dp = 16.dp
    val CardSpacing: Dp = 12.dp
    val CardCorner: Dp = 16.dp
}
