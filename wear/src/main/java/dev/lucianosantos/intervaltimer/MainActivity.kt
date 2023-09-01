package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import dev.lucianosantos.intervaltimer.core.TimerActivity
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper

class MainActivity : TimerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_App)

        setContent {
            if(isBound) {
                MainApp(countDownTimerService)
            }
        }
    }
}

@Composable
fun MainApp(countDownTimer: ICountDownTimerHelper) {
    val navController = rememberSwipeDismissableNavController()
    WearAppTheme {
        WearNavHost(
            countDownTimer = countDownTimer,
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@WearPreviewLargeRound
@Composable
fun MainAppPreview() {
    MainApp(CountDownTimerHelper())
}
