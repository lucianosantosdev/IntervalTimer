package dev.lucianosantos.intervaltimer.subscription

import com.android.billingclient.api.ProductDetails
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionService {
    fun initialize()
    val isUserPremium: StateFlow<Boolean>
    val offerings: StateFlow<List<StoreProduct>>
}