package dev.lucianosantos.intervaltimer

import android.app.Application
import dev.lucianosantos.intervaltimer.core.data.ITimerSettingsRepository
import dev.lucianosantos.intervaltimer.core.data.TimerSettingsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                module {
                    single<ITimerSettingsRepository> { TimerSettingsRepositoryImpl(get()) }
                }
            )
        }
    }
}
