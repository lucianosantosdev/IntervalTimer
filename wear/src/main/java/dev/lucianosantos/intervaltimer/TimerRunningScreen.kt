    package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.graphics.Typeface
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import java.util.Locale

@Composable
fun TimerRunningScreen(
    countDownTimerService: ICountDownTimerService,
    onRefreshClicked: () -> Unit,
    onStopClicked: () -> Unit
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
        currentTimeSeconds = currentTime,
        timerState = timerState,
        isPaused = isPaused,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onRefreshClicked = {
            countDownTimerService.restart()
            onRefreshClicked()
        }
    )

    LaunchedEffect(Unit){
        if (countDownTimerService.timerState!!.value == TimerState.NONE) {
            countDownTimerService.start()
        }
    }

    if (timerState == TimerState.STOPPED) {
        onStopClicked()
    }
}

@Composable
fun TimerRunningComponent(
    remainingSections : Int,
    currentTimeSeconds : Int,
    timerState : TimerState,
    isPaused: Boolean,
    onPlayClicked : () -> Unit,
    onPauseClicked : () -> Unit,
    onRefreshClicked: () -> Unit = {}
) {
    if (timerState == TimerState.NONE || timerState == TimerState.STOPPED) {
        return
    }
    val backgroundColor =
        when (timerState) {
            TimerState.PREPARE -> colorResource(id = R.color.prepare_color)
            TimerState.REST -> colorResource(id = R.color.rest_color)
            TimerState.TRAIN -> colorResource(id = R.color.train_color)
            TimerState.FINISHED -> colorResource(id = R.color.finished_color)
            else -> Color.Transparent
        }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val timerSize = 34.sp
        val labelSize = 14.sp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(0.8F)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            backgroundColor,
                            Color(0xFF000000)
                        ),
                    )
                )
        )
        TimeText(
            timeSource = TimeTextDefaults.timeSource(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "hh:mm")
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = 15.dp
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(.5F)
            )
            Text(
                text = if (timerState != TimerState.FINISHED) remainingSections.toString() else " ",
                style = MaterialTheme.typography.body1.copy(
                    fontSize = labelSize
                ),
                color = colorResource(id = R.color.white)
            )
            Text(
                text = if (timerState != TimerState.FINISHED) {
                    formatMinutesAndSeconds(currentTimeSeconds)
                } else {
                    stringResource(id = R.string.state_finished_text)
                },
                style = MaterialTheme.typography.title3.copy(
                    fontSize = timerSize,
                    fontFamily = FontFamily(Typeface.MONOSPACE)

                ),
                color = colorResource(id = R.color.white)
            )
            if(timerState != TimerState.FINISHED) {
                Text(
                    text = when(timerState) {
                        TimerState.PREPARE -> stringResource(id = R.string.state_prepare_text)
                        TimerState.REST -> stringResource(id = R.string.state_rest_text)
                        TimerState.TRAIN -> stringResource(id = R.string.state_train_text)
                        else -> ""
                    },
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = labelSize
                    ),
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(.2F)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if(timerState == TimerState.FINISHED) {
                    Button(
                        onClick = onRefreshClicked,
                        modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = stringResource(id = R.string.refresh_icon_content_description)
                        )
                    }
                } else {
                    if (!isPaused) {
                        Button(
                            onClick = onPauseClicked,
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Pause,
                                contentDescription = stringResource(id = R.string.pause_icon_content_description)
                            )
                        }
                    } else {
                        Button(
                            onClick = onPlayClicked,
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = stringResource(id = R.string.play_icon_content_description)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

    @Composable
    @Preview(
        device = Devices.WEAR_OS_LARGE_ROUND,
        backgroundColor = 0xff000000,
        showBackground = true,
        group = "Devices - Large Round",
        showSystemUi = true,
        fontScale = 1.6F
    )
    fun TimerRunningScreenBigPreview() {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 7,
                currentTimeSeconds = 3,
                timerState = TimerState.PREPARE,
                true,
                {},
                {},
                {}
            )
        }
    }


    @Composable
    @Preview(
        device = Devices.WEAR_OS_LARGE_ROUND,
        backgroundColor = 0xff000000,
        showBackground = true,
        group = "Devices - Large Round",
        showSystemUi = true
    )
    fun TimerRunningScreenPreview() {
        WearAppTheme {
            TimerRunningComponent(
                remainingSections = 7,
                currentTimeSeconds = 3,
                timerState = TimerState.PREPARE,
                true,
                {},
                {},
                {}
            )
        }
    }