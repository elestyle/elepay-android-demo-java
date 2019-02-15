## [日本語](https://github.com/elestyle/elepay-android-demo-java/blob/master/README.md)

# elepay android demo appliation

This is a demo application for the usage of elepay Android SDK using Java language.

## Requirement

* Android API 21
* Android Studio 3.2.1 +
* Kotlin 1.3 +
* Gradle 4.6 +

> Note that the version of the IDE and build tools may increase in the future.

* elepay API key (*test mode key* and *live mode key*)
> You should request those keys from https://admin.elepay.io

## Usage

You can either `git clone` or download the zip of the project.
Open the project in Android Studio and replace the *API key*s in
> app/src/main/java/jp/elestyle/elepaydemoandroidjava/ui/PaymentActivity.java

``` java
    private String testModePublicKey = PaymentManager.INVALID_KEY;
    private String liveModePublicKey = PaymentManager.INVALID_KEY;
    // The following keys are used to generate charge data.
    // You may consider create your charge data from your server for payment management.
    // So these keys may not live here.
    private String testModeSecretKey = PaymentManager.INVALID_KEY;
    private String liveModeSecretKey = PaymentManager.INVALID_KEY;
```

After running, there are 2 products to demo the payment processing. And each product can be payed by several payment methods.
However, not all of the payment method work for every product.

* The product with price ¥1 is available for Alipay and UnionPay.
* The product with price ¥100 is available for credit-card and Paypal testing.