package com.twohorse.app

/*
 * Values that only exist once the matching Google Cloud / Play
 * Console setup is done -- see the membership setup notes. The app
 * still builds and runs with the placeholders; Google Sign-In and
 * real purchases simply fail with a clear error until these are
 * filled in with the real values.
 */
object Config {
    /*
     * The OAuth 2.0 "Web application" client ID from Google Cloud
     * Console (NOT the Android client ID) -- Google Sign-In on
     * Android requests an ID token audienced to the Web client, and
     * the backend's GOOGLE_CLIENT_ID secret must match this exactly.
     * Set at build time from the GOOGLE_WEB_CLIENT_ID GitHub variable.
     */
    val GOOGLE_WEB_CLIENT_ID: String =
        BuildConfig.GOOGLE_WEB_CLIENT_ID

    /*
     * Play Console subscription product IDs. Must match the backend's
     * PRODUCT_TIER_MAP (src/membership/tier.ts) exactly.
     */
    const val PRODUCT_ID_GOLD_MONTHLY = "gold_monthly"
    const val PRODUCT_ID_PREMIUM_MONTHLY = "premium_monthly"
}
