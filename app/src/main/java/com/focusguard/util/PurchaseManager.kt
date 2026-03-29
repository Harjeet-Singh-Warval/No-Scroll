package com.focusguard.util

import android.content.Context

/**
 * Monetization layer stub — ready for RevenueCat integration.
 *
 * Free tier:   Instagram + YouTube blocking only
 * Premium ($2.99 / ₹199 one-time): All 5 apps + PIN protection + Stats export
 *
 * To integrate RevenueCat:
 *   1. Add implementation("com.revenuecat.purchases:purchases:7.x.x") to build.gradle.kts
 *   2. Call Purchases.configure(PurchasesConfiguration.Builder(context, "your_api_key").build())
 *      from FocusGuardApp.onCreate()
 *   3. Replace isPremiumSync() with an async RevenueCat entitlement check
 */
object PurchaseManager {

    const val PRODUCT_ID = "focusguard_premium"
    const val PRICE_USD  = "$2.99"
    const val PRICE_INR  = "₹199"

    /**
     * Checks the local premium flag.
     * In production, verify with RevenueCat instead.
     */
    fun isPremiumSync(context: Context): Boolean {
        // Placeholder — actual check will validate RevenueCat entitlements
        val prefs = context.getSharedPreferences("purchase_cache", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_premium", false)
    }

    /** Call after a successful RevenueCat purchase to cache locally */
    fun setPremiumLocally(context: Context, premium: Boolean) {
        context.getSharedPreferences("purchase_cache", Context.MODE_PRIVATE)
            .edit().putBoolean("is_premium", premium).apply()
    }
}
