package dev.lucianosantos.intervaltimer.core

import android.media.ToneGenerator

interface IBeepHelper {
    suspend fun shortBeep()

    suspend fun longBeep()

    suspend fun doubleBeep()
}