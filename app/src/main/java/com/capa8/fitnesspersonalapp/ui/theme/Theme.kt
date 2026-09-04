package com.capa8.fitnesspersonalapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

private val DarkColorScheme = darkColorScheme(
    primary = FitnessBlue,
    secondary = FitnessBlueLight,
    tertiary = FitnessBlueDark,
    background = ContentBackground,
    surface = SideMenuBackground
)

private val LightColorScheme = lightColorScheme(
    primary = FitnessBlue,
    secondary = FitnessBlueLight,
    tertiary = FitnessBlueDark,
    background = ContentBackground,
    surface = SideMenuBackground,
    onPrimary = ContentBackground,
    onSecondary = ContentBackground,
    onBackground = ContentTextColor,
    onSurface = SideMenuText
)

@Composable
fun FitnessPersonalAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // statusBarColor is deprecated from API 35; use WindowInsetsControllerCompat instead
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                @Suppress("DEPRECATION")
                window.statusBarColor = FitnessBlue.toArgb()
            }
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
