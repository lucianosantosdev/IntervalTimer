package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.lucianosantos.intervaltimer.components.ActionButton

@Composable
fun TimerFinishedScreen(
    onStopClicked: () -> Unit,
    onRestartClicked: () -> Unit
) {
    Surface(
        color = colorResource(id = R.color.blue)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.state_finished_text))

            Row {
                ActionButton(
                    onClick = onStopClicked,
                    icon = R.drawable.ic_baseline_stop_24,
                    contentDescription = R.string.stop_content_description
                )
                ActionButton(
                    onClick = onRestartClicked,
                    icon = R.drawable.ic_baseline_refresh_24,
                    contentDescription = R.string.restart_content_description
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun TimerFinishedScreenPreview() {
    TimerFinishedScreen({}, {})
}