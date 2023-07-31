package dev.lucianosantos.intervaltimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.lucianosantos.intervaltimer.components.ActionButton
import dev.lucianosantos.intervaltimer.core.data.TimerState

@Composable
fun TimerRunningScreen(
    remainingSections : Int,
    currentTime : String,
    timerState : TimerState,
    onPlayPauseClicked : () -> Unit,
    onStopClicked: () -> Unit
) {
    Surface(
        color = when(timerState) {
            TimerState.PREPARE -> colorResource(id = R.color.blue)
            TimerState.REST -> colorResource(id = R.color.green)
            TimerState.TRAIN -> colorResource(id = R.color.orange)
            TimerState.FINISHED -> colorResource(id = R.color.red)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = remainingSections.toString())
            Text(text = currentTime)
            Text(text = when(timerState) {
                TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                TimerState.REST -> stringResource(id = R.string.state_rest_text)
                TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                TimerState.FINISHED -> stringResource(id = R.string.state_finished_text)
            })
            Row {
                ActionButton(
                    onClick = onStopClicked,
                    icon = R.drawable.ic_baseline_stop_24,
                    contentDescription = R.string.stop_content_description
                )
                ActionButton(
                    onClick = onPlayPauseClicked,
                    icon = R.drawable.ic_baseline_play_arrow_24,
                    contentDescription = R.string.resume_content_description
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun TimerRunningScreenPreview() {
    TimerRunningScreen(
        remainingSections = 1,
        currentTime = "12:34",
        timerState = TimerState.PREPARE,
        {},
        {}
    )
}