package com.bigohtech.snippetmodule.listener

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

interface InAppPurchaseUpdateListener {
    fun onBillingClientReady(billingResult: BillingResult)

    fun onBillingServiceDisconnected() {
        // this is optional
    }

    fun onPurchasesSuccess(purchaseList: List<Purchase?>)
    fun onQueryPurchasesSuccess(purchaseList: List<Purchase?>) {
        // this is optional
    }

    fun onQueryProductDetailSuccess(productDetailsMap: Map<String, ProductDetails>) {
        // this is optional
    }

    fun onPurchaseCancelled() {
        // this is optional
    }

    fun onError(error: String)

}