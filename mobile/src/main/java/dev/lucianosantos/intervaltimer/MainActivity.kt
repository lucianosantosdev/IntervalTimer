package dev.lucianosantos.intervaltimer

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

class MainActivity : BaseActivity() {
    override val serviceName: Class<*>
        get() = CountDownTimerServiceMobile::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (serviceBound) {
                IntervalTimerTheme {
                    MainApp(
                        countDownTimerService = countDownTimerService!!,
                        startDestination = Settings
                    )
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun MainApp(
    countDownTimerService: ICountDownTimerService,
    startDestination: IntervalTimerDestination
) {
    val navController = rememberNavController()
    MobileNavHost(
        countDownTimerService = countDownTimerService,
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = startDestination
    )
}