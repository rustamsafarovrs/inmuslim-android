package tj.rsdevteam.inmuslim.core.router.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun InmuslimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = InmuslimTypo,
        content = content
    )
}

val LightColors = lightColorScheme(
    primary = Color(0xFF01B6D5),
    onPrimary = Color(0xFF00C6EA),
    primaryContainer = Color(0xFFE6F7FF),
    onPrimaryContainer = Color(0xFF0091B6),
    secondary = Color(0xFF53D9F9),
    onSecondary = Color(0xFFE0FFFB),
    secondaryContainer = Color(0xFFD8F2FF),
    onSecondaryContainer = Color(0xFFB3E5FC),
    tertiary = Color(0xFFD8F2FF),
    onTertiary = Color(0xFFE0FFFB),
    tertiaryContainer = Color(0xFFD3F7FF),
    onTertiaryContainer = Color(0xFFB3E5FC),
)
