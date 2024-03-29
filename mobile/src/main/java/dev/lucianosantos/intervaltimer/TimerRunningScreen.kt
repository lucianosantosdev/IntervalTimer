package dev.lucianosantos.intervaltimer

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lucianosantos.intervaltimer.components.ActionButton
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

@Composable
fun TimerRunningScreen(
    countDownTimerService: ICountDownTimerService,
    onStop: () -> Unit
) {
    if (countDownTimerService.remainingSections == null) {
        return
    }

    val remainingSections by countDownTimerService.remainingSections!!.collectAsState()
    val currentTime by countDownTimerService.currentTimeSeconds!!.collectAsState()
    val timerState by countDownTimerService.timerState!!.collectAsState()
    val isPaused by countDownTimerService.isPaused!!.collectAsState()

    TimerRunningComponent(
        remainingSections = remainingSections,
        currentTime = currentTime,
        timerState = timerState,
        isPaused = isPaused,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onStopClicked = {
            countDownTimerService.stop()
        },
        onRestartClicked = {
            countDownTimerService.restart()
        }
    )

    LaunchedEffect(Unit){
        if (countDownTimerService.timerState!!.value == TimerState.NONE) {
            countDownTimerService.start()
        }
    }

    if (timerState == TimerState.STOPPED) {
        onStop()
    }
}

@Composable
fun TimerRunningComponent(
    remainingSections : Int,
    currentTime : Int,
    timerState : TimerState,
    isPaused: Boolean,
    onPlayClicked : () -> Unit,
    onPauseClicked : () -> Unit,
    onStopClicked : () -> Unit,
    onRestartClicked : () -> Unit
) {
    if (timerState == TimerState.NONE || timerState == TimerState.STOPPED) {
        return
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = when(timerState) {
            TimerState.NONE,
            TimerState.STOPPED -> Color.Transparent
            TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
            TimerState.REST -> colorResource(id = R.color.rest_color)
            TimerState.TRAIN -> colorResource(id = R.color.train_color)
            TimerState.FINISHED -> colorResource(id = R.color.finished_color)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (timerState != TimerState.FINISHED) remainingSections.toString() else " ",
                style = MaterialTheme.typography.headlineLarge,
                color = colorResource(id = R.color.white)
            )
            Text(
                text = if (timerState != TimerState.FINISHED) {
                    formatMinutesAndSeconds(currentTime)
                } else {
                    stringResource(id = R.string.state_finished_text)
                },
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 100.sp,
                    fontFamily = FontFamily(Typeface.MONOSPACE)

                ),
                color = colorResource(id = R.color.white)
            )
            Text(
                text = when(timerState) {
                    TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                    TimerState.REST -> stringResource(id = R.string.state_rest_text)
                    TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                    else -> ""
                },
                style = MaterialTheme.typography.headlineLarge,
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if(timerState == TimerState.FINISHED) {
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
                } else {
                    if (!isPaused) {
                        ActionButton(
                            onClick = onPauseClicked,
                            icon = R.drawable.ic_baseline_pause_24,
                            contentDescription = R.string.pause_content_description
                        )
                    } else {
                        ActionButton(
                            onClick = onStopClicked,
                            icon = R.drawable.ic_baseline_stop_24,
                            contentDescription = R.string.stop_content_description
                        )
                        ActionButton(
                            onClick = onPlayClicked,
                            icon = R.drawable.ic_baseline_play_arrow_24,
                            contentDescription = R.string.resume_content_description
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
fun TimerRunningScreenPreview() {
    IntervalTimerTheme {
        TimerRunningComponent(
            remainingSections = 1,
            currentTime = 1234,
            timerState = TimerState.PREPARE,
            true,
            {},
            {},
            {},
            {}
        )
    }
}