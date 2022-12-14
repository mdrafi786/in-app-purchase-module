package com.bigohtech.inapppurchaaseexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.bigohtech.billingModule.listener.InAppPurchaseUpdateListener
import com.bigohtech.billingModule.manager.InAppPurchaseClient
import com.bigohtech.billingModule.manager.InAppPurchaseClientImpl
import com.bigohtech.billingModule.model.ProductQuery
import com.bigohtech.inapppurchaaseexample.constants.Constants

class MainActivity : AppCompatActivity(), InAppPurchaseUpdateListener {

    private val billingClient: InAppPurchaseClient by lazy {
        InAppPurchaseClientImpl(this)
    }

    private var productDetailsMap: Map<String, ProductDetails>? = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        billingClient.startBillingConnection(this)
        findViewById<AppCompatButton>(R.id.launchBillingBt).setOnClickListener {
            // this will launch flow for basic plan id
            val productDetails =
                productDetailsMap?.getOrDefault(Constants.Subscription.BASIC_SUB.name, null)
            productDetails?.let {
                val billingParams = billingClient.billingFlowParamsBuilder(productDetails = it)
                billingClient.launchBillingFlow(this, billingParams)
            }

        }
    }

    override fun onBillingClientReady(billingResult: BillingResult) {

        // The BillingClient is ready. You can query purchases and product details here
        val listOfProductQuery =
            listOf(ProductQuery(productId = Constants.Subscription.BASIC_SUB.name,
                purchaseType = BillingClient.ProductType.INAPP),

                ProductQuery(productId = Constants.Subscription.PREMIUM_SUB.name,
                    purchaseType = BillingClient.ProductType.SUBS))

        // Query Google Play Billing for existing purchases.
        billingClient.queryPurchases(BillingClient.ProductType.INAPP)

        // Query Google Play Billing for products available to sell and present them in the UI
        billingClient.queryProductDetails(listOfProductQuery)
    }

    override fun onPurchasesSuccess(purchaseList: List<Purchase?>) {
        // handle new purchase success here and also check state of purchase


    }

    override fun onPurchaseCancelled() {
        // handle  purchase cancelled by user here
    }

    override fun onQueryProductDetailSuccess(productDetailsMap: Map<String, ProductDetails>) {
        // handle product details here
        // Here map key is productId and value is Product details
        this.productDetailsMap = productDetailsMap
    }

    override fun onQueryPurchasesSuccess(purchaseList: List<Purchase?>) {
        // handle existing purchase list here
    }

    override fun onError(error: String) {
        Log.d("MainActivity::", error)
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }


    override fun onAcknowledgementResponse(isAcknowledged: Boolean) {
        // check purchase acknowledgement
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient.terminateBillingConnection()
    }

}