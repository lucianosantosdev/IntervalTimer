package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onStartClicked: () -> Unit = {}
) {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val uiState by settingsViewModel.uiState.collectAsState()


        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LabelText(text = stringResource(R.string.label_sections))
                    NumberPicker(
                        value = uiState.timerSettings.sections,
                        onValueChange = {
                            settingsViewModel.setSections(it)
                        }
                    )
                    LabelText(text = stringResource(R.string.label_train_number_picker))
                    NumberPicker(
                        uiState.timerSettings.trainTimeSeconds,
                        type = PickerType.TIME,
                        onValueChange = {
                            settingsViewModel.setTrainTime(it)
                        }
                    )
                    LabelText(text = stringResource(R.string.label_rest_number_picker))
                    NumberPicker(
                        value = uiState.timerSettings.restTimeSeconds,
                        type = PickerType.TIME,
                        onValueChange = {
                            settingsViewModel.setRestTime(it)
                        }
                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        IconButton(onClick = {
//                            val newMode = when (uiState.soundMode) {
//                                SoundMode.SOUND -> SoundMode.MUTE
//                                SoundMode.MUTE -> SoundMode.VIBRATE
//                                SoundMode.VIBRATE -> SoundMode.SOUND
//                            }
//                            settingsViewModel.setSoundMode(newMode)
//                        }) {
//                            Icon(
//                                imageVector = when (uiState.soundMode) {
//                                    SoundMode.SOUND -> Icons.Default.VolumeUp
//                                    SoundMode.MUTE -> Icons.Default.VolumeOff
//                                    SoundMode.VIBRATE -> Icons.Default.Vibration
//                                },
//                                contentDescription = null
//                            )
//                        }
//                        Slider(
//                            value = uiState.volume.toFloat(),
//                            valueRange = 0f..100f,
//                            steps = 0,
//                            onValueChange = { settingsViewModel.setVolume(it) },
//                            enabled = uiState.soundMode == SoundMode.SOUND,
//                            modifier = Modifier.weight(1f)
//                        )
//                        Text(text = uiState.volume.toString())
//                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        onClick = onStartClicked
                    ) {
                        Text(text = stringResource(R.string.button_start))
                    }
                }
            }
        }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium
    )
}


@Composable
@Preview(widthDp = 320, heightDp = 640, locale = "pt")
fun SettingsScreenPreview() {
    IntervalTimerTheme {
        SettingsScreen()
    }
}