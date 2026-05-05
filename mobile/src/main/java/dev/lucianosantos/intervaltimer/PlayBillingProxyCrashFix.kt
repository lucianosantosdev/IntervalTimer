package dev.lucianosantos.intervaltimer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.util.Log

// Play Billing 8.3.0's ProxyBillingActivity NPEs in onCreate when the system restores it after
// process death (the saved PendingIntent extras come back null and the library dereferences
// them without a null check). Until 8.4.0 ships, swap the framework's Instrumentation for one
// whose onException hook swallows that specific failure — ActivityThread calls onException on
// lifecycle exceptions and only rethrows when it returns false.
internal object PlayBillingProxyCrashFix {

    private const val TAG = "PlayBillingProxyCrashFix"
    private const val PROXY_BILLING_ACTIVITY = "com.android.billingclient.api.ProxyBillingActivity"

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun install() {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = activityThreadClass
                .getDeclaredMethod("currentActivityThread")
                .invoke(null) ?: return
            val instrumentationField = activityThreadClass
                .getDeclaredField("mInstrumentation")
                .apply { isAccessible = true }
            val original = instrumentationField.get(currentActivityThread) as? Instrumentation
                ?: return
            if (original is CrashSafeInstrumentation) return
            instrumentationField.set(currentActivityThread, CrashSafeInstrumentation(original))
        } catch (e: Throwable) {
            Log.w(TAG, "Could not install Play Billing crash workaround", e)
        }
    }

    private class CrashSafeInstrumentation(
        private val delegate: Instrumentation
    ) : Instrumentation() {

        override fun onException(obj: Any?, e: Throwable?): Boolean {
            if (obj is Activity &&
                obj::class.java.name == PROXY_BILLING_ACTIVITY &&
                containsNpe(e)
            ) {
                Log.w(TAG, "Suppressed Play Billing ProxyBillingActivity restore NPE", e)
                runCatching { obj.finish() }
                return true
            }
            return delegate.onException(obj, e)
        }

        private fun containsNpe(e: Throwable?): Boolean {
            var cur: Throwable? = e
            repeat(8) {
                if (cur is NullPointerException) return true
                cur = cur?.cause ?: return false
            }
            return false
        }
    }
}
