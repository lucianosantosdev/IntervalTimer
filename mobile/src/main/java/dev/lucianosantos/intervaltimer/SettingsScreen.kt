package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

@Composable
fun SettingsScreen(
    onStartClicked: () -> Unit = {}
) {
    val settingsViewModel : SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(TimerSettingsRepository(LocalContext.current))
    )

    val uiState by settingsViewModel.uiState.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,


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
                    formatter = { formatMinutesAndSeconds(it) },
                    onValueChange = {
                        settingsViewModel.setTrainTime(it)
                    }
                )

                LabelText(text = stringResource(R.string.label_rest_number_picker))
                NumberPicker(
                    value = uiState.timerSettings.restTimeSeconds,
                    formatter = { formatMinutesAndSeconds(it) },
                    onValueChange = {
                        settingsViewModel.setRestTime(it)
                    }
                )
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                onClick = onStartClicked
            ) {
                Text(text = stringResource(R.string.button_start))
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
fun NumberPicker(
    value: Int,
    formatter: (Int) -> String = { it.toString() },
    onValueChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            onValueChange(value - 1)
        }) {
            Text(text = "-")
        }

        Text(
            text = formatter(value),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace
            )
        )

        Button(onClick = {
            onValueChange(value + 1)
        }) {
            Text(text = "+")
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640, locale = "pt")
fun SettingsScreenPreview() {
    IntervalTimerTheme {
        SettingsScreen()
    }
}