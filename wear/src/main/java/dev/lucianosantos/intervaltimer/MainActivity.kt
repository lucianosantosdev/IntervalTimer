package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.firebase.components.Component
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import dev.lucianosantos.intervaltimer.core.utils.ICountDownTimerHelper

class MainActivity : ComponentActivity() {

    private var serviceBound by mutableStateOf(false)
    private var countDownTimerService: CountDownTimerServiceWear? = null
    private val countDownTimerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as CountDownTimerServiceWear.CountDownTimerBinder
            countDownTimerService = binder.getService()
            serviceBound = true
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            countDownTimerService = null
            serviceBound = false
            Log.d(TAG, "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate!")
        setTheme(R.style.Theme_App)

        val fromNotification = intent.getBooleanExtra(EXTRA_LAUNCH_FROM_NOTIFICATION, false)

        setContent {
            val navController = rememberSwipeDismissableNavController()

            if (serviceBound) {
                Log.d(TAG, "countDownTimerService != null")
                MainApp(
                    countDownTimerService = countDownTimerService!!,
                    navHostController = navController,
                    startDestination = if (fromNotification) {
                        TimerRunning
                    } else {
                        SetSections
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart!")
        val serviceIntent = Intent(this, CountDownTimerServiceWear::class.java)
        bindService(serviceIntent, countDownTimerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.d(TAG, "OnStop!")
        if (serviceBound) {
            Log.d(TAG, "Unbinding!")
            unbindService(countDownTimerServiceConnection)
            serviceBound = false
        }
        super.onStop()
    }

    companion object {
        const val TAG = "MAIN ACTIVITY!"

        const val EXTRA_LAUNCH_FROM_NOTIFICATION = "EXTRA_LAUNCH_FROM_NOTIFICATION"
    }
}

@Composable
fun MainApp(
    countDownTimerService: CountDownTimerServiceWear,
    navHostController: NavHostController,
    startDestination: IntervalTimerDestination
) {
    WearAppTheme {
        WearNavHost(
            countDownTimerService = countDownTimerService,
            navController = navHostController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@WearPreviewLargeRound
@Composable
fun MainAppPreview() {
    val navController = rememberSwipeDismissableNavController()
    MainApp(CountDownTimerServiceWear(), navController, SetSections)
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

    LaunchedEffect(context, serviceConnection) {
        Log.d("DisposableEffect", "Will bind service!")
        context.bindService(Intent(context, BoundService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    return boundService
}