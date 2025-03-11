package dev.lucianosantos.intervaltimer

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdHelper(private val activity: Activity) {
    private var interstitialAd: InterstitialAd? = null

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, BuildConfig.GOOGLE_BANNER_INTERESTIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdMob", "Ad Loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdMob", "Ad failed to load: ${error.message}")
                }
            })
    }

    fun showAd(onAdClosed: () -> Unit = {}) {
        interstitialAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMob", "Ad dismissed")
                    interstitialAd = null
                    loadAd() // Load next ad
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.e("AdMob", "Ad failed to show: ${error.message}")
                    interstitialAd = null
                    loadAd()
                }
            }
            it.show(activity)
        } ?: run {
            Log.d("AdMob", "Ad not loaded yet")
            onAdClosed()
        }
    }
}
