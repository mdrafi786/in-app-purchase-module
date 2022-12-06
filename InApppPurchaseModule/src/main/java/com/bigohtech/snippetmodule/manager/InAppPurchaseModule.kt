package com.bigohtech.snippetmodule.manager

import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.bigohtech.snippetmodule.listener.InAppPurchaseUpdateListener

object InAppPurchaseModule {

    // Declare billingClient variable
    private lateinit var billingClient: BillingClient

    private lateinit var listener: InAppPurchaseUpdateListener

    private const val TAG = "InAppPurchaseModule"


    /***
     * @param context is refers to the context of the activity.
     * @param productsIdsList is the list of product ids which are registered on play console.
     ***/
    fun initBillingClient(context: Context, productsIdsList: ArrayList<String>) {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()

        startBillingConnection(productsIdsList = productsIdsList)
    }


    /***
     * @param productsIdsList is the list of product ids which are registered on play console.
     ***/
    private fun startBillingConnection(productsIdsList: ArrayList<String>) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                listener.onBillingSetupFinished(billingResult = billingResult)
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(productsIdsList).setType(BillingClient.SkuType.SUBS)
                    billingClient.querySkuDetailsAsync(
                        params.build()
                    ) { billingResult, skuDetailsList ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "onBillingSetupFinished: $billingResult")
                            onBillingInitialized(skuDetailsList)
                        } else {
                            Log.d(TAG, "onBillingSetupFinished: $billingResult Failed ")
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingSetupFinished:  Failed ")

            }
        })
    }

    private fun onBillingInitialized(skuDetailsList: List<SkuDetails>?) {
        skuDetailsList?.let { listener.onBillingInitialized(skuDetailsList = it) }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                listener.onPurchasesSuccess(purchaseList = purchases)
                Log.d(TAG, "onBillingSuccess: $purchases")
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.d(TAG, "onBillingError:  Cancelled")
                listener.onPurchaseCancelled()
            } else {
                listener.onPurchasesError()
                Log.d(TAG, "onBillingError:  Error")
            }
        }

}