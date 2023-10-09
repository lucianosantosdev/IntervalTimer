package dev.lucianosantos.intervaltimer.core.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.lucianosantos.intervaltimer.core.R
import dev.lucianosantos.intervaltimer.core.data.DefaultTimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerSettings
import dev.lucianosantos.intervaltimer.core.data.TimerState
import dev.lucianosantos.intervaltimer.core.utils.AlertUserHelper
import dev.lucianosantos.intervaltimer.core.utils.CountDownTimerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface ICountDownTimerService {

    val timerState : StateFlow<TimerState>?

    val remainingSections : StateFlow<Int>?

    val currentTimeSeconds : StateFlow<Int>?

    val isPaused : StateFlow<Boolean>?

    fun setTimerSettings(newTimerSettings: TimerSettings)

    fun start()

    fun pause()

    fun resume()

    fun stop()

    fun reset()
}