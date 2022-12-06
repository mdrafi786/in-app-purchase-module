package com.bigohtech.snippetmodule.listener

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface InAppPurchaseUpdateListener {
    fun onBillingSetupFinished(billingResult: BillingResult)
    fun onBillingServiceDisconnected()
    fun onPurchasesSuccess(purchaseList: List<Purchase?>)
    fun onPurchasesError()
    fun onPurchaseCancelled()
    fun onBillingInitialized(skuDetailsList: List<SkuDetails>)
}