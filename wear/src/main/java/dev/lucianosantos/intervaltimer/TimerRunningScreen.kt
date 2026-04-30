    package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.graphics.Typeface
import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
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
import androidx.wear.compose.material.dialog.Dialog
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.formatMinutesAndSeconds
import dev.lucianosantos.intervaltimer.heartrate.CalorieMonitor
import dev.lucianosantos.intervaltimer.heartrate.HeartRateMonitor
import dev.lucianosantos.intervaltimer.heartrate.HeartRateSample
import java.util.Locale

private const val BUTTONS_AUTO_HIDE_MS = 5_000L

@Composable
fun TimerRunningScreen(
    countDownTimerService: ICountDownTimerService,
    onRefreshClicked: () -> Unit,
    onStopClicked: () -> Unit,
    onSettingsClick: (() -> Unit)? = null
) {
    if (countDownTimerService.remainingSections == null) {
        return
    }
    val remainingSections by countDownTimerService.remainingSections!!.collectAsState()
    val currentTime by countDownTimerService.currentTimeSeconds!!.collectAsState()
    val timerState by countDownTimerService.timerState!!.collectAsState()
    val isPaused by countDownTimerService.isPaused!!.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    val canExit = timerState == TimerState.PREPARE ||
        timerState == TimerState.TRAIN ||
        timerState == TimerState.REST
    BackHandler(enabled = canExit) {
        showExitDialog = true
    }

    val context = LocalContext.current
    val monitor = remember(context) { HeartRateMonitor(context.applicationContext) }
    val heartRate by produceState<Int?>(initialValue = null, monitor) {
        monitor.heartRateBpm().collect { sample ->
            value = when (sample) {
                is HeartRateSample.Reading -> sample.bpm
                is HeartRateSample.Unavailable -> null
            }
        }
    }
    val calorieMonitor = remember(context) { CalorieMonitor(context.applicationContext) }
    val calories by produceState(initialValue = 0, calorieMonitor) {
        calorieMonitor.caloriesKcal().collect { kcal ->
            if (kcal > value) value = kcal
        }
    }

    TimerRunningComponent(
        remainingSections = remainingSections,
        currentTimeSeconds = currentTime,
        timerState = timerState,
        isPaused = isPaused,
        heartRate = heartRate,
        calories = calories,
        onPlayClicked = {
            countDownTimerService.resume()
        },
        onPauseClicked = {
            countDownTimerService.pause()
        },
        onRefreshClicked = {
            countDownTimerService.restart()
            onRefreshClicked()
        },
        onSkipNextClicked = { countDownTimerService.skipNext() },
        onSkipPreviousClicked = { countDownTimerService.skipPrevious() },
        onSettingsClick = onSettingsClick
    )

    Dialog(
        showDialog = showExitDialog,
        onDismissRequest = { showExitDialog = false }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.exit_workout_title),
                style = MaterialTheme.typography.title3,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.exit_workout_message),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.secondaryButtonColors(),
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(id = R.string.exit_workout_cancel)
                    )
                }
                Button(
                    onClick = {
                        showExitDialog = false
                        countDownTimerService.stop()
                    },
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = stringResource(id = R.string.exit_workout_confirm)
                    )
                }
            }
        }
    }

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
    onRefreshClicked: () -> Unit = {},
    onSkipNextClicked: () -> Unit = {},
    onSkipPreviousClicked: () -> Unit = {},
    onSettingsClick: (() -> Unit)? = null,
    heartRate: Int? = null,
    calories: Int = 0
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
    val isAmbient = LocalIsAmbient.current
    var showButtons by remember { mutableStateOf(false) }
    var interactionTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(interactionTick, isAmbient) {
        if (isAmbient) return@LaunchedEffect
        delay(BUTTONS_AUTO_HIDE_MS)
        showButtons = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    showButtons = true
                    interactionTick++
                }
            }
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

        AnimatedVisibility(
            visible = !isAmbient && showButtons && onSettingsClick != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 26.dp)
        ) {
            Button(
                onClick = { onSettingsClick?.invoke() },
                colors = ButtonDefaults.secondaryButtonColors(),
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(id = R.string.settings_icon_content_description),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

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
                color = colorResource(id = R.color.white),
                modifier = Modifier.padding(top = 16.dp)
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
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HeartRateBadge(heartRate = heartRate, labelSize = labelSize)
                    CaloriesBadge(calories = calories, labelSize = labelSize)
                }
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(.5F)
            )
            Box(
                modifier = Modifier.height(ButtonDefaults.SmallButtonSize),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isAmbient && showButtons,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f)
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        Button(
                            onClick = onSkipPreviousClicked,
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.size(ButtonDefaults.ExtraSmallButtonSize),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = stringResource(id = R.string.skip_previous_icon_content_description),
                                modifier = Modifier.size(ButtonDefaults.SmallIconSize)
                            )
                        }
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
                        Button(
                            onClick = onSkipNextClicked,
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.size(ButtonDefaults.ExtraSmallButtonSize),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = stringResource(id = R.string.skip_next_icon_content_description),
                                modifier = Modifier.size(ButtonDefaults.SmallIconSize)
                            )
                        }
                    }
                }
            }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeartRateBadge(heartRate: Int?, labelSize: androidx.compose.ui.unit.TextUnit) {
    MetricChip(
        icon = Icons.Filled.Favorite,
        iconContentDescription = stringResource(id = R.string.heart_rate_icon_content_description),
        text = heartRate?.toString() ?: stringResource(id = R.string.heart_rate_unavailable),
        active = heartRate != null,
        labelSize = labelSize
    )
}

@Composable
private fun CaloriesBadge(calories: Int, labelSize: androidx.compose.ui.unit.TextUnit) {
    MetricChip(
        icon = Icons.Filled.LocalFireDepartment,
        iconContentDescription = stringResource(id = R.string.calories_icon_content_description),
        text = calories.toString(),
        active = true,
        labelSize = labelSize
    )
}

@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconContentDescription: String,
    text: String,
    active: Boolean,
    labelSize: androidx.compose.ui.unit.TextUnit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = if (active) {
                colorResource(id = R.color.white)
            } else {
                colorResource(id = R.color.white).copy(alpha = 0.5f)
            },
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1.copy(
                fontSize = labelSize,
                fontFamily = FontFamily(Typeface.MONOSPACE)
            ),
            color = colorResource(id = R.color.white)
        )
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