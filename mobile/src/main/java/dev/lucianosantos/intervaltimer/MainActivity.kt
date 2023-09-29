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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val countDownTimerService = rememberBoundLocalService<CountDownTimerService, CountDownTimerService.CountDownTimerBinder> { getService() }
            if (countDownTimerService !== null) {
                IntervalTimerTheme {
                    MainApp(countDownTimerService)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun MainApp(countDownTimerService: CountDownTimerService) {
    val navController = rememberNavController()
    MobileNavHost(
        countDownTimerService = countDownTimerService,
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}
@Composable
inline fun <reified BoundService : Service, reified BoundServiceBinder : Binder> rememberBoundLocalService(
    crossinline getService: @DisallowComposableCalls BoundServiceBinder.() -> BoundService,
): BoundService? {
    val context: Context = LocalContext.current
    var boundService: BoundService? by remember(context) { mutableStateOf(null) }
    val serviceConnection: ServiceConnection = remember(context) {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                boundService = (service as BoundServiceBinder).getService()
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                boundService = null
            }
        }
    }
    DisposableEffect(context, serviceConnection) {
        context.bindService(Intent(context, BoundService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(serviceConnection) }
    }
    return boundService
}