package dev.lucianosantos.intervaltimer.subscription

import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreProduct
import dev.lucianosantos.intervaltimer.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SubscriptionServiceImpl(
    private val context: Context
) : SubscriptionService {
    private val _isUserPremium = MutableStateFlow(false)
    private val _offerings = MutableStateFlow<List<StoreProduct>>(emptyList())
    override val isUserPremium: StateFlow<Boolean> = _isUserPremium
    override val offerings: StateFlow<List<StoreProduct>> = _offerings

    override fun initialize() {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(context, BuildConfig.REVENUECAT_API).build()
        )
        Purchases.sharedInstance.getCustomerInfo(
            callback = object : ReceiveCustomerInfoCallback {
                override fun onError(error: PurchasesError) {
                    // TODO("Not yet implemented")
                }

                override fun onReceived(customerInfo: CustomerInfo) {
                    customerInfo.entitlements[ENTITLEMENT_PREMIUM]?.isActive?.let {
                        _isUserPremium.value = it
                    }
                }
            }
        )
        Purchases.sharedInstance.updatedCustomerInfoListener =
            UpdatedCustomerInfoListener { customerInfo ->
                customerInfo.entitlements[ENTITLEMENT_PREMIUM]?.isActive?.let {
                    _isUserPremium.value = it
                }
            }
        // Fetch available offerings
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onError(error: PurchasesError) {
                // Handle error (e.g., log it)
            }

            override fun onReceived(offerings: Offerings) {
                val availableProducts = offerings.current?.availablePackages
                    ?.map { it.product }
                    ?: emptyList()

                _offerings.value = availableProducts
            }
        })
    }

    companion object {
        const val ENTITLEMENT_PREMIUM = "pro"
    }
}