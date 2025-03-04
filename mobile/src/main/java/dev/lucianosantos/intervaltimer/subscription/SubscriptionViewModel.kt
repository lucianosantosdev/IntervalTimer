package dev.lucianosantos.intervaltimer.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val subscriptionService: SubscriptionService
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SubscriptionUiState(
            hasPremiumPermission = subscriptionService.isUserPremium.value,
            products = subscriptionService.offerings.value
        )
    )
    val uiState: StateFlow<SubscriptionUiState> = _uiState

    data class SubscriptionUiState(
        val hasPremiumPermission: Boolean,
        val products: List<StoreProduct> = emptyList(),
    )
}