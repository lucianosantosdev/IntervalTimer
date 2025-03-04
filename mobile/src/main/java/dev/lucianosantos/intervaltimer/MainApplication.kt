package dev.lucianosantos.intervaltimer

import android.app.Application
import dev.lucianosantos.intervaltimer.subscription.SubscriptionService
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
        val subscriptionService: SubscriptionService = get<SubscriptionService>()
        subscriptionService.initialize()
    }
}