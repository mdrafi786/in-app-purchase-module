package com.bigohtech.inapppurchaaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.bigohtech.snippetmodule.listener.InAppPurchaseUpdateListener
import com.bigohtech.snippetmodule.manager.InAppPurchaseModule

class MainActivity : AppCompatActivity() , InAppPurchaseUpdateListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val skuList: ArrayList<String> = ArrayList()

        skuList.add("productId_12345")
        skuList.add("productId_09876")
        InAppPurchaseModule.initBillingClient(context = this, productsIdsList = skuList)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        TODO("Not yet implemented")
    }

    override fun onBillingServiceDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onPurchasesSuccess(purchaseList: List<Purchase?>) {
        TODO("Not yet implemented")
    }

    override fun onPurchasesError() {
        TODO("Not yet implemented")
    }

    override fun onPurchaseCancelled() {
        TODO("Not yet implemented")
    }

    override fun onBillingInitialized(skuDetailsList: List<SkuDetails>) {
        TODO("Not yet implemented")
    }
}