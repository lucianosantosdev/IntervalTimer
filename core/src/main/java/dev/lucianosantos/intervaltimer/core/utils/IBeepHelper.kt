package dev.lucianosantos.intervaltimer.core.utils

interface IBeepHelper {
    suspend fun shortBeep()

    suspend fun longBeep()

    suspend fun doubleBeep()
}