# **In App Purchase Module**

In App Purchase Module is a module, created on kotlin, which supports SDK level from 21 to 33.

## Features

* In App Purchase SDK Integration.

## Installation
1. Import `In App Purchase Module` module into your project.
   
2. Add module dependencies in your build.gradle(app level) file of your app module:
   ```groovy
    implementation project(path: ':InApppPurchaseModule')   or implementation(project(mapOf("path" to ":InApppPurchaseModule"))) (if you are      using build.gradle.kts)
   ```
3. implement this listener InAppPurchaseUpdateListener in the Activity or Fragment where payment flow is implemented.

4. Call init method from Activity or Fragment like this.

```groovy
     /***
     * @param context is refers to the context of the activity.
     * @param productsIdsList is the list of product ids which are registered on play console.
     ***/
    InAppPurchaseModule.initBillingClient(context = this, productsIdsList = skuList)
   ```

## License
* [BigOhNotation](https://www.bigohtech.com/)

