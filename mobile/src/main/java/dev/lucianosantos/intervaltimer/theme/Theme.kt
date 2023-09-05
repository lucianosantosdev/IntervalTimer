package dev.lucianosantos.intervaltimer.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.SettingsScreen

private val DarkColorScheme = darkColorScheme(
    surface = Blue,
    onSurface = Navy,
    primary = Navy,
    onPrimary = Chartreuse
)

@Composable
private fun customColorScheme() = darkColorScheme(
    primary =  colorResource(id = R.color.primary_500),
    onPrimary = colorResource(id = R.color.white),
    secondary = colorResource(id = R.color.secondary_800),
    onSecondary = colorResource(id = R.color.black),
    surface = colorResource(id = R.color.white),
    onSurface = colorResource(id = R.color.primary_400)
)

@Composable
fun IntervalTimerTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> customColorScheme()
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

@Preview(locale = "pt", showBackground = true)
@Composable
fun themePreview() {
    IntervalTimerTheme {
        SettingsScreen()
    }
}