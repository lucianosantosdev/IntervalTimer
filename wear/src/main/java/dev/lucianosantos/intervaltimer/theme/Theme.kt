
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import dev.lucianosantos.intervaltimer.theme.WearAppTypography
import dev.lucianosantos.intervaltimer.theme.wearAppColorPalette

@Composable
fun WearAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearAppColorPalette(),
        typography = WearAppTypography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}
