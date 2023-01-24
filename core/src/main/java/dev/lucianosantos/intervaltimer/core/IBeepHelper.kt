package dev.lucianosantos.intervaltimer.core

interface IBeepHelper {
    suspend fun shortBeep()

    suspend fun longBeep()

    suspend fun doubleBeep()
}