package com.bigohtech.snippetmodule.manager


import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.bigohtech.snippetmodule.listener.InAppPurchaseUpdateListener
import com.bigohtech.snippetmodule.model.ProductQuery

/**
 * The [BillingClientManager] isolates the Google Play Billing's [BillingClient] methods needed
 * to have a simple implementation
 */
class BillingClientManager(
    context: Context,
) : PurchasesUpdatedListener, ProductDetailsResponseListener {

    // Initialize the BillingClient.
    private val billingClient =
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

    lateinit var listener: InAppPurchaseUpdateListener

    private var _isPurchaseAcknowledged = false
    private val isPurchaseAcknowledged get() = _isPurchaseAcknowledged

    // Establish a connection to Google Play.
    fun startBillingConnection(
        purchaseListener: InAppPurchaseUpdateListener,
    ) {
        listener = purchaseListener
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing response OK")
                    listener.onBillingClientReady(billingResult)
                } else {
                    Log.e(TAG, billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "Billing connection disconnected")
                startBillingConnection(purchaseListener)
                listener.onBillingServiceDisconnected()
            }
        })
    }

    // Query Google Play Billing for existing purchases.
    // New purchases will be provided to PurchasesUpdatedListener.onPurchasesUpdated().
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
            listener.onError("queryPurchases: BillingClient is not ready")
        }
        // Query for existing subscription products that have been purchased.
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchaseList.isNotEmpty()) {
                    listener.onQueryPurchasesSuccess(purchaseList)
                } else {
                    listener.onQueryPurchasesSuccess(emptyList())
                }

            } else {
                Log.e(TAG, billingResult.debugMessage)
                listener.onError(billingResult.debugMessage)
            }
        }
    }

    // Query Google Play Billing for products available to sell and present them in the UI
    fun queryProductDetails(productQueryList: List<ProductQuery>) {
        val params = QueryProductDetailsParams.newBuilder()
        val productList = mutableListOf<QueryProductDetailsParams.Product>()
        for (product in productQueryList) {
            productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(product.productId).setProductType(product.purchaseType).build())

            params.setProductList(productList).let { productDetailsParams ->
                Log.i(TAG, "queryProductDetailsAsync")
                billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
            }
        }
    }

    // [ProductDetailsResponseListener] implementation
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>,
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                var newMap = emptyMap<String, ProductDetails>()
                if (productDetailsList.isEmpty()) {
                    val errorString =
                        "onProductDetailsResponse: " + "Found null or empty ProductDetails. " + "Check to see if the Products you requested are correctly " + "published in the Google Play Console."
                    Log.e(TAG, errorString)
                    listener.onError(errorString)
                } else {
                    newMap = productDetailsList.associateBy {
                        it.productId
                    }
                }
                listener.onQueryProductDetailSuccess(newMap)
            }
            else -> {
                listener.onError(debugMessage)
                Log.i(TAG, "onProductDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    // Launch Purchase flow
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        if (!billingClient.isReady) {
            listener.onError("launchBillingFlow: BillingClient is not ready")
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        billingClient.launchBillingFlow(activity, params)
    }

    // PurchasesUpdatedListener that helps handle new purchases returned from the API
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?,
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
            // Post new purchase List to _purchases
            listener.onPurchasesSuccess(purchases)

            // Then, handle the purchases
            for (purchase in purchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e(TAG, "User has cancelled")
            listener.onPurchaseCancelled()
            listener.onError("User has cancelled")
        } else {
            // Handle any other error codes.
            listener.onError("Purchase error")
        }
    }

    // Perform new subscription purchases' acknowledgement client side.
    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                val params =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(it.purchaseToken)
                        .build()

                billingClient.acknowledgePurchase(params) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        _isPurchaseAcknowledged = true
                    }
                }
            }
        }
    }

    // Build billing flow params for launch billing flow with specifi id
    fun billingFlowParamsBuilder(
        productDetails: ProductDetails,
    ): BillingFlowParams {
        return BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails).build())).build()
    }

    // End Billing connection.
    fun terminateBillingConnection() {
        Log.i(TAG, "Terminating connection")
        billingClient.endConnection()
    }

    companion object {
        private const val TAG = "BillingClient"
    }
}