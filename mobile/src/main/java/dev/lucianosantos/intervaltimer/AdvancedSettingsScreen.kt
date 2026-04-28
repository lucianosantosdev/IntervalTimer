package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

@Composable
fun AdvancedSettingsScreen(
    onBack: () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepositoryImpl(LocalContext.current))
    )
    val uiState by settingsViewModel.uiState.collectAsState()

    AdvancedSettingsScreenContent(
        volume = uiState.timerSettings.volume,
        soundMode = uiState.timerSettings.soundMode,
        wakeScreenOnTransition = uiState.timerSettings.wakeScreenOnTransition,
        onVolumeChange = { settingsViewModel.setVolume(it) },
        onSoundModeChange = { settingsViewModel.setSoundMode(it) },
        onWakeScreenChange = { settingsViewModel.setWakeScreenOnTransition(it) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreenContent(
    volume: Int,
    soundMode: SoundMode,
    wakeScreenOnTransition: Boolean,
    onVolumeChange: (Int) -> Unit,
    onSoundModeChange: (SoundMode) -> Unit,
    onWakeScreenChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.advanced_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            SoundModeRow(soundMode = soundMode, onSoundModeChange = onSoundModeChange)
            VolumeRow(
                volume = volume,
                enabled = soundMode == SoundMode.SOUND_AND_VIBRATE,
                onVolumeChange = onVolumeChange
            )
            WakeScreenRow(
                checked = wakeScreenOnTransition,
                onCheckedChange = onWakeScreenChange
            )
        }
    }
}

@Composable
private fun SoundModeRow(
    soundMode: SoundMode,
    onSoundModeChange: (SoundMode) -> Unit
) {
    TextButton(
        onClick = { onSoundModeChange(soundMode.next()) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = soundMode.icon(), contentDescription = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_sound_mode),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = soundMode.label(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VolumeRow(
    volume: Int,
    enabled: Boolean,
    onVolumeChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Text(
            text = stringResource(R.string.settings_volume),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = volume.toFloat(),
                valueRange = 0f..100f,
                steps = 0,
                onValueChange = { onVolumeChange(it.toInt()) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text(text = volume.toString())
        }
    }
}

@Composable
private fun WakeScreenRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_wake_screen),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
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

@Preview(showSystemUi = true)
@Composable
fun AdvancedSettingsScreenPreview() {
    IntervalTimerTheme {
        AdvancedSettingsScreenContent(
            volume = 80,
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            wakeScreenOnTransition = true,
            onVolumeChange = {},
            onSoundModeChange = {},
            onWakeScreenChange = {},
            onBack = {}
        )
    }
}
