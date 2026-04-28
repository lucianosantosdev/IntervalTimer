package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lucianosantos.intervaltimer.components.NumberPicker
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onStartClicked: () -> Unit = {},
    onAdvancedSettingsClicked: () -> Unit = {}
) {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val uiState by settingsViewModel.uiState.collectAsState()

    SettingsScreenContent(
        sections = uiState.timerSettings.sections,
        trainTimeSeconds = uiState.timerSettings.trainTimeSeconds,
        restTimeSeconds = uiState.timerSettings.restTimeSeconds,
        onSectionsChange = { settingsViewModel.setSections(it) },
        onTrainTimeChange = { settingsViewModel.setTrainTime(it) },
        onRestTimeChange = { settingsViewModel.setRestTime(it) },
        onStartClicked = onStartClicked,
        onAdvancedSettingsClicked = onAdvancedSettingsClicked,
        soundMode = uiState.timerSettings.soundMode,
        onSoundModeChange = { settingsViewModel.setSoundMode(it) },
        volume = uiState.timerSettings.volume,
        onVolumeChange = { settingsViewModel.setVolume(it) }
    )
}

@Composable
fun SettingsScreenContent(
    sections: Int,
    trainTimeSeconds: Int,
    restTimeSeconds: Int,
    onSectionsChange: (Int) -> Unit,
    onTrainTimeChange: (Int) -> Unit,
    onRestTimeChange: (Int) -> Unit,
    onStartClicked: () -> Unit,
    soundMode: SoundMode,
    onSoundModeChange: (SoundMode) -> Unit,
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    onAdvancedSettingsClicked: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VolumeControl(
                    volume = volume,
                    onVolumeChange = onVolumeChange,
                    soundMode = soundMode,
                    onSoundModeChange = onSoundModeChange
                )
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LabeledNumberPicker(
                        label = stringResource(R.string.label_sections),
                        value = sections,
                        type = PickerType.NUMBER,
                        onValueChange = {
                            onSectionsChange(it)
                        }
                    )
                    LabeledNumberPicker(
                        label = stringResource(R.string.label_train_number_picker),
                        value = trainTimeSeconds,
                        type = PickerType.TIME,
                        onValueChange = {
                            onTrainTimeChange(it)
                        }
                    )
                    LabeledNumberPicker(
                        label = stringResource(R.string.label_rest_number_picker),
                        value = restTimeSeconds,
                        type = PickerType.TIME,
                        onValueChange = {
                            onRestTimeChange(it)
                        }
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClick = onStartClicked,
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Text(text = stringResource(R.string.button_start))
                }
            }
        }
    }
}


@Composable
fun VolumeControl(
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    soundMode: SoundMode,
    onSoundModeChange: (SoundMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = {
            val newMode = when (soundMode) {
                SoundMode.SOUND_AND_VIBRATE -> SoundMode.MUTE
                SoundMode.MUTE -> SoundMode.VIBRATE_ONLY
                SoundMode.VIBRATE_ONLY -> SoundMode.SOUND_AND_VIBRATE
            }
            onSoundModeChange(newMode)
        }) {
            Icon(
                imageVector = when (soundMode) {
                    SoundMode.SOUND_AND_VIBRATE -> Icons.Default.VolumeUp
                    SoundMode.MUTE -> Icons.Default.VolumeOff
                    SoundMode.VIBRATE_ONLY -> Icons.Default.Vibration
                },
                contentDescription = null
            )
        }
        Slider(
            value = volume.toFloat(),
            valueRange = 0f..100f,
            steps = 0,
            onValueChange = { onVolumeChange(it.toInt()) },
            enabled = soundMode == SoundMode.SOUND_AND_VIBRATE,
            modifier = Modifier.weight(1f)
        )
        Text(text = volume.toString())
    }
}
@Composable
fun LabeledNumberPicker(
    label: String,
    type: PickerType,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LabelText(text = label)
        NumberPicker(
            value = value,
            type = type,
            onValueChange = {
                onValueChange(it)
            }
        )
    }
}
@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall
    )
}


@Composable
@Preview(showSystemUi = true)
fun SettingsScreenPreview() {
    IntervalTimerTheme {
        SettingsScreenContent(
            sections = 5,
            trainTimeSeconds = 30,
            restTimeSeconds = 15,
            onSectionsChange = {},
            onTrainTimeChange = {},
            onRestTimeChange = {},
            onStartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {}
        )
    }
}