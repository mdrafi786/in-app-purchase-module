# **InAppPurchase**

InAppPurchase is a  Android Library to implement Google Billing Library integration easily in Android App.It is simple, lightweight and used for quick setup. 

## Prerequisite
* In order to implement an **`In-App purchase`** or a **`Subscription`**, you need an account in the **`Google play console`** and **`Payment profile`** setup with your application published in either beta/alpha/ or in Release mode.

## Installation
1. Add the following to the **`settings.gradle`** file.
   ```gradle
   dependencyResolutionManagement {
      repositories {
          maven { url 'https://jitpack.io' }
    }}
   ```
2. Add library dependency to the app level **`build.gradle`** file.
   ```gradle
    dependencies {
	   implementation 'com.github.mdrafi786:location-manager:$latest_stable_version'
	}
   ```
   
 3. Once you’ve added the library dependency, build a **`Release APK`** of your app and upload it to the **`Google Play Console`** in **`Internal Testing`** (Beta Testing).
 
 4. Once you’ve uploaded the APK, you can use the Google Play Console to start adding **`In-App Products`** or **`Subcriptions`** to sell in your app. Under **`Montetize >> Products`** in play console, you’ll see a section for **`In-App products`** and **`Subscriptions`**. This is where you can set up two types of items:

  *  **In App Products (or one-time purchases)**
  *  **Subscriptions**
  
  * When you create new **`In-App Products`** and **`Subcriptions`**, you are required to enter a **`Product ID`**, or **`SKU`**, for the item. This same **`Product ID`** is going to be used again later in your application code as we’ll see in the next step. Before creating a managed product, make sure to plan your Product IDs carefully. **`Product IDs`** need to be unique for your app, and they can’t be changed or reused after they’ve been created. 

## Usage
* Firstly, you need to declare variable of **`InAppPurchaseClient`** with **`lazy`** initailization  in your activity or fragment.

  ```kotlin
     private val billingClient: InAppPurchaseClient by lazy {
        InAppPurchaseClientImpl(this)
    }
  ```
* Set up billing connection by calling  **`startBillingConnection()`** with required parameter in **`onCreate()`** of your activity or fragment :
  > **Note:** Here **`this`** is **`InAppPurchaseUpdateListener`** that you have to implement in your activty or fragment.

  ```kotlin
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)
          billingClient.startBillingConnection(this)
    }
  ```
  
* Implement **`InAppPurchaseUpdateListener`** and override methods to get callback of purchase state .
   ```kotlin
    class MainActivity : AppCompatActivity(),InAppPurchaseUpdateListener {
     
       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)
      }

      override fun onBillingClientReady(billingResult: BillingResult) {
        // The BillingClient is ready. You can query purchases and product details here
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
     }

      override fun onQueryPurchasesSuccess(purchaseList: List<Purchase?>) {
        // handle existing purchase list here
     }

      override fun onError(error: String) {
      // Get any occur during setup or purchase
     }

      override fun onAcknowledgementResponse(isAcknowledged: Boolean) {
        // Get acknowledgement status for purchase
     }
  }
  ```
  
  #### Prouduct Type Enum Constants
  > **`BillingClient.ProductType.INAPP`** ( One time purchase)
  > **`BillingClient.ProductType.SUBS`** (Subscription)

   #### Query For Existing Purchase

    * You can Query Google Play Biliing for existing purchases with specific product Type

      ```kotlin
      billingClient.queryPurchases(BillingClient.ProductType.INAPP)
      ```
  
    #### Query for Prouducts Available to sell
    * You can Query Google Play Biliing for existing purchases with specific product Type

      ```kotlin
      val listOfProductQuery = listOf(
            ProductQuery(
                productId = "your_proudct_id",
                purchaseType = "your_prouduct_type"
            )
        )
      billingClient.queryProductDetails(listOfProductQuery)
      ```
 
     #### Launch the flow for specific product

    * Firstly get the ProductDetails Map from **`onQueryProductDetailSuccess()`** overriden method.
 
      ```koltin
        private var productDetailsMap: Map<String, ProductDetails>? = emptyMap()
        
        override fun onQueryProductDetailSuccess(productDetailsMap: Map<
        String, ProductDetails>) {
             // handle product details here
             // Here map key is productId and value is Product details
             this.productDetailsMap = productDetailsMap
      }
      ```
    * **`Launch the flow`** on button click
    
      ```kotlin
      val productDetails =
        productDetailsMap?.getOrDefault("your_prouduct_id", null)
         productDetails?.let {
            val billingParams = billingClient.billingFlowParamsBuilder(
                    productDetails = it
                )
                billingClient.launchBillingFlow(this, billingParams)
        }
      ```
    #### Termintate the Billing connection to avoid memory leaks
     * Close the connection of blilling library when activity has been destroyed to avoid memory leaks
        ```kotlin
         override fun onDestroy() {
           super.onDestroy()
           billingClient.terminateBillingConnection()
        }
        ```
        
        
## License
* [BigOhNotation](https://www.bigohtech.com/)