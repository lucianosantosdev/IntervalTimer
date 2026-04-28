package dev.lucianosantos.intervaltimer.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import dev.lucianosantos.intervaltimer.SettingsScreenContent
import dev.lucianosantos.intervaltimer.TimerRunningComponent
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private const val OUTPUT_DIR = "build/outputs/playstore-screenshots/mobile"

private val PrepareColor = Color(0xFFF9AA33)
private val TrainColor = Color(0xFF36AE7C)
private val RestColor = Color(0xFFEB5353)
private val FinishedColor = Color(0xFF187498)
private val NeutralColor = Color(0xFF1F2937)

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h914dp-xxhdpi", sdk = [35], application = StubApplication::class)
class PlayStoreScreenshotsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val testName = TestName()

    @Test
    fun settings() = capture(
        title = "Set up your workout",
        description = "Pick sections, train and rest times in seconds",
        background = NeutralColor
    ) {
        SettingsScreenContent(
            sections = 5,
            trainTimeSeconds = 45,
            restTimeSeconds = 15,
            onSectionsChange = {},
            onTrainTimeChange = {},
            onRestTimeChange = {},
            onStartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {},
            onAdvancedSettingsClicked = {}
        )
    }

    @Test
    fun running_train() = capture(
        title = "Push through every set",
        description = "Visual cues guide you while you train",
        background = NeutralColor
    ) {
        TimerRunningComponent(
            remainingSections = 4,
            currentTime = 38,
            timerState = TimerState.TRAIN,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            onStopClicked = {},
            onRestartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {}
        )
    }

    @Test
    fun running_rest() = capture(
        title = "Recover, then go again",
        description = "Rest periods keep you on pace",
        background = NeutralColor
    ) {
        TimerRunningComponent(
            remainingSections = 3,
            currentTime = 12,
            timerState = TimerState.REST,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            onStopClicked = {},
            onRestartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {}
        )
    }

    @Test
    fun running_prepare() = capture(
        title = "Get ready",
        description = "A short prepare phase before each round",
        background = NeutralColor
    ) {
        TimerRunningComponent(
            remainingSections = 8,
            currentTime = 5,
            timerState = TimerState.PREPARE,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            onStopClicked = {},
            onRestartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {}
        )
    }

    @Test
    fun running_finished() = capture(
        title = "Workout complete",
        description = "Track every session, anywhere",
        background = NeutralColor
    ) {
        TimerRunningComponent(
            remainingSections = 0,
            currentTime = 0,
            timerState = TimerState.FINISHED,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            onStopClicked = {},
            onRestartClicked = {},
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            onSoundModeChange = {},
            volume = 80,
            onVolumeChange = {}
        )
    }

    private fun capture(
        title: String,
        description: String,
        background: Color,
        content: @Composable () -> Unit
    ) {
        composeRule.setContent {
            PlayStoreFrame(
                title = title,
                description = description,
                backgroundColor = background
            ) {
                IntervalTimerTheme { content() }
            }
        }
        composeRule.onRoot().captureRoboImage("$OUTPUT_DIR/${testName.methodName}.png")
    }
}
