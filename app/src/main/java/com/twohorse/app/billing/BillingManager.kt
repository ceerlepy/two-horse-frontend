package com.twohorse.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.BillingFlowParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/*
 * Thin wrapper around Play Billing Library for the two subscription
 * products (gold_monthly / premium_monthly), each with a "monthly"
 * and a "yearly" base plan. Product IDs must match Play Console
 * exactly and the backend's PRODUCT_TIER_MAP (src/membership/tier.ts);
 * the base plan is not the backend's concern, since both grant the
 * same tier.
 *
 * This class never decides a purchase is valid on its own -- it
 * only surfaces raw purchases via [purchases]; the caller is
 * responsible for sending the purchase token to the backend's
 * /api/billing/verify-purchase and only then treating the tier as
 * upgraded.
 */
class BillingManager(
    context: Context
) {
    private val appContext =
        context.applicationContext

    private var billingClient: BillingClient? = null

    private val _purchases =
        MutableSharedFlow<Purchase>(
            extraBufferCapacity = 4
        )

    val purchases: SharedFlow<Purchase> =
        _purchases

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (
                billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK &&
                purchases != null
            ) {
                // PENDING purchases (e.g. cash payment not made yet)
                // arrive again as PURCHASED once paid.
                purchases
                    .filter {
                        it.purchaseState ==
                            Purchase.PurchaseState.PURCHASED
                    }
                    .forEach {
                        _purchases.tryEmit(it)
                    }
            }
        }

    suspend fun connect(): Boolean =
        suspendCancellableCoroutine { cont ->
            val client =
                BillingClient.newBuilder(appContext)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                            .enableOneTimeProducts()
                            .build()
                    )
                    .enableAutoServiceReconnection()
                    .build()

            billingClient = client

            client.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(
                        result: BillingResult
                    ) {
                        if (cont.isActive) {
                            cont.resume(
                                result.responseCode ==
                                    BillingClient.BillingResponseCode.OK
                            )
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        // Reconnect is attempted lazily on next connect() call.
                    }
                }
            )
        }

    suspend fun queryProductDetails(
        productIds: List<String>
    ): List<ProductDetails> {
        val client =
            billingClient
                ?: return emptyList()

        val products =
            productIds.map { id ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(
                        BillingClient.ProductType.SUBS
                    )
                    .build()
            }

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build()

        return suspendCancellableCoroutine { cont ->
            client.queryProductDetailsAsync(params) { _, result ->
                if (cont.isActive) {
                    cont.resume(
                        result.productDetailsList
                    )
                }
            }
        }
    }

    /*
     * Subscriptions the signed-in Play account currently owns. Used
     * on screen open to re-send purchases whose verification never
     * reached the backend (app killed, no network) and to find the
     * old token when switching Gold <-> Premium.
     */
    suspend fun queryActiveSubscriptions(): List<Purchase> {
        val client =
            billingClient
                ?: return emptyList()

        val params =
            QueryPurchasesParams.newBuilder()
                .setProductType(
                    BillingClient.ProductType.SUBS
                )
                .build()

        return suspendCancellableCoroutine { cont ->
            client.queryPurchasesAsync(params) { result, purchases ->
                if (cont.isActive) {
                    cont.resume(
                        if (
                            result.responseCode ==
                            BillingClient.BillingResponseCode.OK
                        )
                            purchases
                        else
                            emptyList()
                    )
                }
            }
        }
    }

    /*
     * [accountId] is our backend user id; Play stores it on the
     * purchase (obfuscatedExternalAccountId) and the backend refuses
     * to credit the purchase to any other account. [oldPurchaseToken]
     * turns the purchase into a plan switch instead of a second,
     * parallel subscription.
     */
    fun launchPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        accountId: String?,
        basePlanId: String,
        oldPurchaseToken: String? = null
    ): Boolean {
        val client =
            billingClient
                ?: return false

        val offerToken =
            productDetails
                .offerFor(basePlanId)
                ?.offerToken
                ?: return false

        val flowBuilder =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams
                            .newBuilder()
                            .setProductDetails(
                                productDetails
                            )
                            .setOfferToken(
                                offerToken
                            )
                            .build()
                    )
                )

        if (!accountId.isNullOrBlank()) {
            flowBuilder.setObfuscatedAccountId(
                accountId
            )
        }

        if (!oldPurchaseToken.isNullOrBlank()) {
            flowBuilder.setSubscriptionUpdateParams(
                BillingFlowParams.SubscriptionUpdateParams
                    .newBuilder()
                    .setOldPurchaseToken(
                        oldPurchaseToken
                    )
                    .setSubscriptionReplacementMode(
                        BillingFlowParams.SubscriptionUpdateParams
                            .ReplacementMode
                            .WITH_TIME_PRORATION
                    )
                    .build()
            )
        }

        val flowParams =
            flowBuilder.build()

        val result =
            client.launchBillingFlow(
                activity,
                flowParams
            )

        return result.responseCode ==
            BillingClient.BillingResponseCode.OK
    }

    suspend fun acknowledge(
        purchaseToken: String
    ) {
        val client =
            billingClient
                ?: return

        suspendCancellableCoroutine<Unit> { cont ->
            client.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(
                        purchaseToken
                    )
                    .build()
            ) {
                if (cont.isActive) {
                    cont.resume(Unit)
                }
            }
        }
    }

    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
    }
}

/*
 * The offer to buy for one base plan: the plain base-plan offer
 * (no offerId) when there is one, otherwise any offer on that plan.
 * A product set up before base plan IDs were fixed may only have a
 * single base plan under another ID; monthly then falls back to it
 * so existing setups keep working.
 */
fun ProductDetails.offerFor(
    basePlanId: String
): ProductDetails.SubscriptionOfferDetails? {
    val offers =
        subscriptionOfferDetails
            .orEmpty()

    val onPlan =
        offers.filter { it.basePlanId == basePlanId }

    return onPlan.firstOrNull { it.offerId == null }
        ?: onPlan.firstOrNull()
        ?: offers
            .takeIf { basePlanId == BASE_PLAN_MONTHLY && offers.none { it.basePlanId == BASE_PLAN_YEARLY } }
            ?.firstOrNull()
}

/* Recurring price of one base plan, e.g. "₺349,00". */
fun ProductDetails?.priceFor(
    basePlanId: String
): String? =
    this
        ?.offerFor(basePlanId)
        ?.pricingPhases
        ?.pricingPhaseList
        ?.lastOrNull()
        ?.formattedPrice

const val BASE_PLAN_MONTHLY = "monthly"
const val BASE_PLAN_YEARLY = "yearly"
