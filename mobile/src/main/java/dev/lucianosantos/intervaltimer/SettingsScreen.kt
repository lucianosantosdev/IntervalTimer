package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    onStartClicked: () -> Unit = {}
) {
    Surface() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LabelText(text = stringResource(R.string.label_sections))
            NumberPicker()
            LabelText(text = stringResource(R.string.label_train_number_picker))
            NumberPicker()
            LabelText(text = stringResource(R.string.label_rest_number_picker))
            NumberPicker()
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
fun NumberPicker() {
    Text(text = "Number Picker")
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun SettingsScreenPreview() {
    SettingsScreen()
}