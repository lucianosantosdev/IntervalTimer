package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper


class MainActivity : BaseActivity() {

    override val serviceName = CountDownTimerServiceWear::class.java

    private lateinit var appUpdateManager: AppUpdateManager

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Log.e("UpdateFlow", "Update failed or was canceled by the user")
        }
    }

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored — chip just stays hidden if denied */ }

    private val requestBodySensorsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored — heart rate badge shows -- if denied */ }

    private val requestHeartRatePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored — heart rate badge shows -- if denied */ }

    private val requestActivityRecognitionPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored — calories badge shows -- if denied */ }

    private var isAmbient by mutableStateOf(false)

    private val ambientObserver by lazy {
        AmbientLifecycleObserver(
            this,
            object : AmbientLifecycleObserver.AmbientLifecycleCallback {
                override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                    isAmbient = true
                }

                override fun onExitAmbient() {
                    isAmbient = false
                }

                override fun onUpdateAmbient() {}
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate!")
        setTheme(R.style.Theme_App)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForAppUpdate()

        lifecycle.addObserver(ambientObserver)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestBodySensorsPermission.launch(Manifest.permission.BODY_SENSORS)
        }

        if (ContextCompat.checkSelfPermission(this, HEALTH_READ_HEART_RATE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestHeartRatePermission.launch(HEALTH_READ_HEART_RATE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestActivityRecognitionPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager?.canUseFullScreenIntent() == false) {
                runCatching {
                    startActivity(
                        Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                            data = Uri.parse("package:$packageName")
                        }
                    )
                }
            }
        }

        val fromNotification = intent.getBooleanExtra(NotificationHelper.EXTRA_LAUNCH_FROM_NOTIFICATION, false)

        setContent {
            val navController = rememberSwipeDismissableNavController()
            CompositionLocalProvider(LocalIsAmbient provides isAmbient) {
                MainApp(
                    countDownTimerService = countDownTimerServiceProxy,
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

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_STEM_1 -> {
                Log.d(TAG, "KEYCODE_STEM_1")
                true
            }
            KeyEvent.KEYCODE_STEM_2 -> {
                Log.d(TAG, "KEYCODE_STEM_2")
                true
            }
            KeyEvent.KEYCODE_STEM_3 -> {
                Log.d(TAG, "KEYCODE_STEM_3")
                true
            }
            KeyEvent.KEYCODE_STEM_PRIMARY -> {
                Log.d(TAG, "KEYCODE_STEM_PRIMARY")
                true
            }
            else -> {
                super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            else -> super.onKeyUp(keyCode, event)
        }
    }

    companion object {
        const val TAG = "MAIN ACTIVITY!"
        private const val HEALTH_READ_HEART_RATE = "android.permission.health.READ_HEART_RATE"
    }
}

@Composable
fun MainApp(
    countDownTimerService: ICountDownTimerService,
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