package dev.lucianosantos.intervaltimer.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.wear.compose.material.Colors
import dev.lucianosantos.intervaltimer.R

val Red400 = Color(0xFFCF6679)

@Composable
fun wearAppColorPalette() = Colors(
    primary = colorResource(id = R.color.primary_500),
    primaryVariant = colorResource(id = R.color.primary_700),
    secondary = colorResource(id = R.color.secondary_800),
    secondaryVariant = colorResource(id = R.color.secondary_700),
    error = Red400,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onError = Color.Black,
    onBackground = Color.White,
)
