package dev.lucianosantos.intervaltimer.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.CountDownTimerServiceProxy

abstract class BaseActivity : ComponentActivity() {
    abstract val serviceName: Class<*>
    var serviceBound by mutableStateOf(false)
    val countDownTimerServiceProxy = CountDownTimerServiceProxy()

    private val countDownTimerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as CountDownTimerService.CountDownTimerBinder
            countDownTimerServiceProxy.countDownTimerService = binder.getService()
            serviceBound = true
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            countDownTimerServiceProxy.countDownTimerService  = null
            serviceBound = false
            Log.d(TAG, "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart!")
        val serviceIntent = Intent(this, serviceName)
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
        const val TAG = "BaseActivity"
    }
}
