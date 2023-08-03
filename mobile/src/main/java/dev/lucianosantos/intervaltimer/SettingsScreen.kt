package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.viewmodels.SettingsViewModel

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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LabelText(text = stringResource(R.string.label_sections))
            NumberPicker(
                uiState.timerSettings.sections
            ) {
                settingsViewModel.setSections(it)
            }

            LabelText(text = stringResource(R.string.label_train_number_picker))
            NumberPicker(
                uiState.timerSettings.trainTimeSeconds
            ) {
                settingsViewModel.setTrainTime(it)
            }

            LabelText(text = stringResource(R.string.label_rest_number_picker))
            NumberPicker(
                uiState.timerSettings.restTimeSeconds
            ) {
                settingsViewModel.setRestTime(it)
            }

            Button(onClick = onStartClicked) {
                Text(text = stringResource(R.string.button_start))
            }
        }
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit) {
    Button(onClick = {
        onValueChange(value - 1)
    }) {
        Text(text = "-")
    }

    Text(text = "Number Picker: $value")
    Button(onClick = {
        onValueChange(value + 1)
    }) {
        Text(text = "+")
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun SettingsScreenPreview() {
    SettingsScreen()
}