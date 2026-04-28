package dev.lucianosantos.intervaltimer.heartrate

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HeartRateMonitor(context: Context) {

    private val measureClient = HealthServices.getClient(context).measureClient

    fun heartRateBpm(): Flow<HeartRateSample> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                Log.d(TAG, "availability=$availability")
                if (availability is DataTypeAvailability && availability != DataTypeAvailability.AVAILABLE) {
                    trySend(HeartRateSample.Unavailable(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val points = data.getData(DataType.HEART_RATE_BPM)
                val latest = points.lastOrNull() ?: return
                val bpm = latest.value.toInt()
                Log.d(TAG, "data bpm=$bpm")
                if (bpm > 0) {
                    trySend(HeartRateSample.Reading(bpm))
                }
            }
        }

        try {
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
            Log.d(TAG, "registered HEART_RATE_BPM callback")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to register heart rate callback", t)
            trySend(HeartRateSample.Unavailable(null))
        }

        awaitClose {
            try {
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                Log.d(TAG, "unregistered HEART_RATE_BPM callback")
            } catch (t: Throwable) {
                Log.w(TAG, "Failed to unregister heart rate callback", t)
            }
        }
    }

    companion object {
        private const val TAG = "HeartRateMonitor"
    }
}

sealed interface HeartRateSample {
    data class Reading(val bpm: Int) : HeartRateSample
    data class Unavailable(val availability: DataTypeAvailability?) : HeartRateSample
}
