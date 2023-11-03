package edu.app.productivity.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MossGreen,
    onPrimary = CornSilk,
    secondary = Buff,
    onSecondary = CornSilk,
//    background = DarkGray,
    background = CafeNoir,
    onBackground = CornSilk,
//    surface = DarkGray,
    surface = CafeNoir,
    onSurface = CornSilk,
    surfaceVariant = Russet,
    onSurfaceVariant = CornSilk,
    outline = Buff
)

private val LightColorScheme = lightColorScheme(
    primary = MossGreen,
    onPrimary = CornSilk,
    secondary = Buff,
    onSecondary = CornSilk,
    background = PapayaWhip,
    onBackground = Russet,
    surface = CornSilk,
    onSurface = Russet,
    surfaceVariant = Beige,
    onSurfaceVariant = Russet,
    outline = Buff,
)

@Composable
fun ProductivityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
