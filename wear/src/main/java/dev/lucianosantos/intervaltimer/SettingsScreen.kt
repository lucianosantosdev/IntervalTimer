package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode

@Composable
fun WearSettingsScreen(
    volume: Int,
    soundMode: SoundMode,
    wakeScreenOnTransition: Boolean,
    onVolumeChange: (Int) -> Unit,
    onSoundModeChange: (SoundMode) -> Unit,
    onWakeScreenChange: (Boolean) -> Unit,
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.title3,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }
        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                onClick = {
                    onSoundModeChange(soundMode.next())
                },
                icon = {
                    Icon(
                        imageVector = soundMode.icon(),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(R.string.settings_sound_mode))
                },
                secondaryLabel = {
                    Text(text = soundMode.label())
                }
            )
        }
        item {
            VolumeSliderRow(
                volume = volume,
                enabled = soundMode == SoundMode.SOUND_AND_VIBRATE,
                onVolumeChange = onVolumeChange
            )
        }
        item {
            ToggleChip(
                modifier = Modifier.fillMaxWidth(),
                checked = wakeScreenOnTransition,
                onCheckedChange = onWakeScreenChange,
                label = {
                    Text(
                        text = stringResource(R.string.settings_wake_screen),
                        maxLines = 2
                    )
                },
                toggleControl = {
                    Icon(
                        imageVector = ToggleChipDefaults.switchIcon(checked = wakeScreenOnTransition),
                        contentDescription = null
                    )
                }
            )
        }
        item { Spacer(Modifier.height(4.dp)) }
    }
}

@Composable
private fun VolumeSliderRow(
    volume: Int,
    enabled: Boolean,
    onVolumeChange: (Int) -> Unit
) {
    InlineSlider(
        value = volume,
        onValueChange = onVolumeChange,
        valueProgression = 0..100 step 10,
        decreaseIcon = {
            Icon(
                imageVector = InlineSliderDefaults.Decrease,
                contentDescription = null
            )
        },
        increaseIcon = {
            Icon(
                imageVector = InlineSliderDefaults.Increase,
                contentDescription = null
            )
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SoundMode.icon(): ImageVector = when (this) {
    SoundMode.SOUND_AND_VIBRATE -> Icons.Default.VolumeUp
    SoundMode.MUTE -> Icons.Default.VolumeOff
    SoundMode.VIBRATE_ONLY -> Icons.Default.Vibration
}

@Composable
private fun SoundMode.label(): String = when (this) {
    SoundMode.SOUND_AND_VIBRATE -> stringResource(R.string.settings_sound_mode_sound)
    SoundMode.MUTE -> stringResource(R.string.settings_sound_mode_mute)
    SoundMode.VIBRATE_ONLY -> stringResource(R.string.settings_sound_mode_vibrate)
}

private fun SoundMode.next(): SoundMode = when (this) {
    SoundMode.SOUND_AND_VIBRATE -> SoundMode.VIBRATE_ONLY
    SoundMode.VIBRATE_ONLY -> SoundMode.MUTE
    SoundMode.MUTE -> SoundMode.SOUND_AND_VIBRATE
}
