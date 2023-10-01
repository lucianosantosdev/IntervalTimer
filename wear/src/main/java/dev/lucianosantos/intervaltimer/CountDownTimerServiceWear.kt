package dev.lucianosantos.intervaltimer

import dev.lucianosantos.intervaltimer.core.service.CountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.OngoingActivityWrapper
import dev.lucianosantos.intervaltimer.di.OngoingActivityWrapperImpl

class CountDownTimerServiceWear : CountDownTimerService(CountDownTimerServiceWear::class.java) {
    override val ongoingActivityWrapper: OngoingActivityWrapper
        get() = OngoingActivityWrapperImpl()
    override val mainActivity: Class<*>
        get() = MainActivity::class.java
}