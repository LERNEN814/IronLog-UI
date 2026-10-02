package com.ironlog.app.ui.theme

import android.provider.Settings
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared visual effect tokens. Effects are reserved for context and state layers. */
object IronEffects {
    const val GlassAlpha = 0.82f
    const val FallbackGlassAlpha = 1f
    const val RaisedShadowAlpha = 0.16f
    const val OverlayShadowAlpha = 0.22f

    val RaisedElevation: Dp = 2.dp
    val OverlayElevation: Dp = 6.dp
    val EffectBorder: Dp = 1.dp
    val PrimaryCorner = 16.dp
    val OverlayCorner = 24.dp

    /** Glass treatment is intentionally scoped to Android 12+; older devices use a stable opaque surface. */
    fun glassColor(base: Color): Color = base.copy(
        alpha = base.alpha * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlassAlpha else FallbackGlassAlpha,
    )
}

/** Android-native equivalent of a reduced-motion preference. */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val resolver = context.contentResolver
        val duration = Settings.Global.getFloat(
            resolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
        val transition = Settings.Global.getFloat(
            resolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            1f,
        )
        duration <= 0f || transition <= 0f
    }
}

@Composable
fun IronGlassSurface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(IronEffects.OverlayCorner),
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = IronEffects.OverlayElevation,
    content: @Composable () -> Unit,
) {
    val glassColor = IronEffects.glassColor(color)
    Surface(
        modifier = modifier,
        shape = shape,
        color = glassColor,
        contentColor = contentColor,
        border = BorderStroke(
            IronEffects.EffectBorder,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        ),
        shadowElevation = shadowElevation,
        content = content,
    )
}
