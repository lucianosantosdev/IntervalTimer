package dev.lucianosantos.intervaltimer

import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.ui.revenuecatui.PaywallFooter
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import dev.lucianosantos.intervaltimer.core.BaseActivity
import dev.lucianosantos.intervaltimer.core.service.ICountDownTimerService
import dev.lucianosantos.intervaltimer.core.service.NotificationHelper
import dev.lucianosantos.intervaltimer.subscription.SubscriptionService
import dev.lucianosantos.intervaltimer.subscription.SubscriptionViewModel
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : BaseActivity() {
    override val serviceName: Class<*>
        get() = CountDownTimerServiceMobile::class.java
    private lateinit var interstitialAdHelper: InterstitialAdHelper

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { it ->
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted()) {
            requestPermissions()
        }
        val fromNotification = intent.getBooleanExtra(NotificationHelper.EXTRA_LAUNCH_FROM_NOTIFICATION, false)

        MobileAds.initialize(this)
        interstitialAdHelper = InterstitialAdHelper(this)
        interstitialAdHelper.loadAd()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            IntervalTimerTheme {
                MainApp(
                    countDownTimerService = countDownTimerServiceProxy,
                    navController = navController
                )
                LaunchedEffect(Unit) {
                    if (fromNotification) {
                        navController.navigate(TimerRunning.route)
                    }
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainApp(
        navController: NavHostController,
        countDownTimerService: ICountDownTimerService
    ) {
        val subscriptionService = getKoin().get<SubscriptionService>()
        val isUserPremium by subscriptionService.isUserPremium.collectAsState()
        val products by subscriptionService.offerings.collectAsState()
        val context = LocalContext.current
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        LaunchedEffect(navBackStackEntry) {
            if (!isUserPremium && navBackStackEntry?.destination?.route == Settings.route) {
                // Call your method when returning to "home"
                interstitialAdHelper.showAd()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isUserPremium) {
                    Column {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            title = { },
                            actions = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Yellow,
                                        contentColor = Color.Black
                                    ),

                                    onClick = {
                                        Purchases.sharedInstance.purchaseWith(
                                            PurchaseParams.Builder(context as Activity, products[0]).build(),
                                            onError = { error, userCancelled ->
                                                // Handle error (log or show UI message)
                                            },
                                            onSuccess = { storeTransaction, customerInfo ->
                                                // Handle successful purchase
                                            }
                                        )
                                    }
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Diamond,
                                            contentDescription = stringResource(R.string.button_remove_ads)
                                        )
                                        Text(text = stringResource(R.string.button_remove_ads))
                                    }
                                }
                            }
                        )
                        BannerAd(
                            modifier = Modifier.fillMaxWidth(),
                            adId = "ca-app-pub-1325449258005309/4736998612"
                        )
                    }
                }
            }
        ) { innerPadding ->
            MobileNavHost(
                countDownTimerService = countDownTimerService,
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
fun BannerAd(modifier: Modifier, adId: String) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                    adUnitId = adId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
@Preview
fun BannerAdPreview() {
    BannerAd(modifier = Modifier.fillMaxSize(), adId = BuildConfig.GOOGLE_BANNER_ADS_ID)
}