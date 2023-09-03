package dev.lucianosantos.intervaltimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dev.lucianosantos.intervaltimer.core.TimerActivity
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import java.lang.ref.WeakReference

class MainActivity : TimerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntervalTimerTheme {
                if(isBound) {
                    MainApp(countDownTimerService)
                }
            }
        }
    }
}

@Composable
fun MainApp(countDownTimer: CountDownTimerService) {
    val navController = rememberNavController()

    MobileNavHost(
        countDownTimer = countDownTimer,
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MainAppPreview() {
    IntervalTimerTheme {
        MainApp(CountDownTimerService())
    }
}