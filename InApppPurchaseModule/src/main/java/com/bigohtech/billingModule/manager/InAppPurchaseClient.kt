package com.bigohtech.billingModule.manager

import android.app.Activity
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.bigohtech.billingModule.listener.InAppPurchaseUpdateListener
import com.bigohtech.billingModule.model.ProductQuery

interface InAppPurchaseClient {
    fun startBillingConnection(purchaseListener: InAppPurchaseUpdateListener)

    fun queryPurchases(productType: String)

    fun queryProductDetails(productQueryList: List<ProductQuery>)

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams)

    fun billingFlowParamsBuilder(
        productDetails: ProductDetails,
    ): BillingFlowParams

    fun terminateBillingConnection()

}
