package dev.lucianosantos.intervaltimer.screenshots

import WearAppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import dev.lucianosantos.intervaltimer.TimerRunningComponent
import dev.lucianosantos.intervaltimer.WearSettingsScreen
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.viewmodels.SoundMode
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private const val OUTPUT_DIR = "build/outputs/playstore-screenshots/wear"

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w227dp-h227dp-round-xxhdpi", sdk = [35], application = StubApplication::class)
class PlayStoreScreenshotsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val testName = TestName()

    @Test
    fun running_train() = capture {
        TimerRunningComponent(
            remainingSections = 5,
            currentTimeSeconds = 42,
            timerState = TimerState.TRAIN,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            heartRate = 132,
            calories = 18,
            onSettingsClick = {}
        )
    }

    @Test
    fun running_rest() = capture {
        TimerRunningComponent(
            remainingSections = 3,
            currentTimeSeconds = 12,
            timerState = TimerState.REST,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            heartRate = 108,
            calories = 47,
            onSettingsClick = {}
        )
    }

    @Test
    fun running_prepare() = capture {
        TimerRunningComponent(
            remainingSections = 8,
            currentTimeSeconds = 5,
            timerState = TimerState.PREPARE,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            heartRate = 82,
            calories = 0,
            onSettingsClick = {}
        )
    }

    @Test
    fun running_finished() = capture {
        TimerRunningComponent(
            remainingSections = 0,
            currentTimeSeconds = 0,
            timerState = TimerState.FINISHED,
            isPaused = false,
            onPlayClicked = {},
            onPauseClicked = {},
            heartRate = 95,
            calories = 152,
            onSettingsClick = {}
        )
    }

    @Test
    fun settings() = capture {
        WearSettingsScreen(
            volume = 70,
            soundMode = SoundMode.SOUND_AND_VIBRATE,
            wakeScreenOnTransition = true,
            onVolumeChange = {},
            onSoundModeChange = {},
            onWakeScreenChange = {}
        )
    }

    private fun capture(content: @Composable () -> Unit) {
        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(227.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            ) {
                WearAppTheme { content() }
            }
        }
        composeRule.waitForIdle()
        composeRule.onRoot().performTouchInput { click() }
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage("$OUTPUT_DIR/${testName.methodName}.png")
    }
}
