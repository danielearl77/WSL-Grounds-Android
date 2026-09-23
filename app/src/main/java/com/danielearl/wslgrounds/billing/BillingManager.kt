package com.danielearl.wslgrounds.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams

/**
 * Android Play Billing port of iOS's IAPManager. Sold as a consumable "tip" product
 * (Play Console product id below), consumed immediately after purchase so it can be
 * bought again, matching the repeat-purchase behaviour of SupportViewController's tipLimit.
 *
 * NOTE: the product id "wsl_support_tip" must be created as a one-time managed product
 * in Google Play Console before this will return real product data.
 */
class BillingManager(context: Context) : PurchasesUpdatedListener {

    companion object {
        const val TIP_PRODUCT_ID = "wsl_support_tip"
    }

    sealed interface BillingManagerError {
        data object NoProductsFound : BillingManagerError
        data object PurchaseCancelled : BillingManagerError
        data class Other(val message: String) : BillingManagerError
    }

    private var onBuyProductHandler: ((Result<Boolean>) -> Unit)? = null

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            com.android.billingclient.api.PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    fun startConnection(onReady: () -> Unit, onError: (String) -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                } else {
                    onError(billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                // The next call will retry the connection automatically via startConnection.
            }
        })
    }

    fun getTipProduct(onResult: (Result<ProductDetails>) -> Unit) {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(TIP_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                productDetailsList.isNotEmpty()
            ) {
                onResult(Result.success(productDetailsList.first()))
            } else {
                onResult(Result.failure(Exception("No In-App Purchases were found.")))
            }
        }
    }

    fun canMakePayments(): Boolean =
        billingClient.isReady

    fun buy(activity: Activity, productDetails: ProductDetails, onResult: (Result<Boolean>) -> Unit) {
        onBuyProductHandler = onResult
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase -> consumeAndNotify(purchase) }
                if (purchases.isNullOrEmpty()) {
                    onBuyProductHandler?.invoke(Result.failure(Exception("Purchase completed with no items.")))
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                onBuyProductHandler?.invoke(Result.failure(Exception("In-App Purchase process was cancelled.")))
            }
            else -> {
                onBuyProductHandler?.invoke(Result.failure(Exception(billingResult.debugMessage)))
            }
        }
    }

    private fun consumeAndNotify(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
            if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onBuyProductHandler?.invoke(Result.success(true))
            } else {
                onBuyProductHandler?.invoke(Result.failure(Exception(consumeResult.debugMessage)))
            }
        }
    }

    fun formattedPrice(productDetails: ProductDetails): String? =
        productDetails.oneTimePurchaseOfferDetails?.formattedPrice
}
