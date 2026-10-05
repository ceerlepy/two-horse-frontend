package com.twohorse.app.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.Config
import com.twohorse.app.billing.BillingManager
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.i18n.Language
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.auth.LanguageToggle
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private sealed interface AccountMessage {
    data class PurchaseActivated(val tier: String) : AccountMessage
    data object PurchaseVerifyFailed : AccountMessage
    data object PurchaseOtherAccount : AccountMessage
    data object DeleteFailed : AccountMessage
}

private fun formatIsoDate(
    value: String?,
    language: Language
): String? {
    val raw =
        value?.trim()?.takeIf { it.isNotEmpty() }
            ?: return null

    return runCatching {
        Instant.parse(raw)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofPattern(
                    "d MMMM yyyy",
                    Locale(language.code)
                )
            )
    }.getOrDefault(raw)
}

@Composable
fun AccountScreen(
    repository: TwoHorseRepository,
    initialUser: MembershipUser?,
    onUserUpdated: (MembershipUser) -> Unit,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current

    val billingManager =
        remember {
            BillingManager(context)
        }

    var user by
        remember {
            mutableStateOf(initialUser)
        }

    var goldProduct by
        remember { mutableStateOf<ProductDetails?>(null) }

    var premiumProduct by
        remember { mutableStateOf<ProductDetails?>(null) }

    var purchaseInFlight by
        remember { mutableStateOf(false) }

    var message by
        remember { mutableStateOf<AccountMessage?>(null) }

    var ownedSubscriptions by
        remember { mutableStateOf<List<Purchase>>(emptyList()) }

    var confirmDelete by
        remember { mutableStateOf(false) }

    var deleting by
        remember { mutableStateOf(false) }

    /*
     * Sends a Play purchase to the backend, the only place that can
     * grant a tier. [silent] is used for purchases found again on
     * screen open, where a failure shouldn't show an error banner.
     */
    suspend fun verifyAndApply(
        purchase: Purchase,
        silent: Boolean
    ) {
        val productId =
            purchase.products.firstOrNull()
                ?: return

        purchaseInFlight = true

        if (!silent) {
            message = null
        }

        repository
            .verifyPurchase(
                productId,
                purchase.purchaseToken
            )
            .onSuccess { updated ->
                user = updated
                onUserUpdated(updated)

                if (!purchase.isAcknowledged) {
                    billingManager.acknowledge(
                        purchase.purchaseToken
                    )
                }

                if (!silent) {
                    message =
                        AccountMessage.PurchaseActivated(updated.tier)
                }
            }
            .onFailure { throwable ->
                if (!silent) {
                    message =
                        if (
                            (throwable as? ApiException)?.apiCode ==
                            "PURCHASE_BELONGS_TO_ANOTHER_ACCOUNT"
                        )
                            AccountMessage.PurchaseOtherAccount
                        else
                            AccountMessage.PurchaseVerifyFailed
                }
            }

        purchaseInFlight = false
    }

    /*
     * Gold <-> Premium is a plan switch on the existing subscription,
     * not a second subscription running in parallel.
     */
    fun launchPurchase(
        product: ProductDetails
    ) {
        val oldToken =
            ownedSubscriptions
                .firstOrNull { owned ->
                    owned.products.none { it == product.productId }
                }
                ?.purchaseToken

        billingManager.launchPurchaseFlow(
            context as Activity,
            product,
            accountId = user?.id,
            oldPurchaseToken = oldToken
        )
    }

    LaunchedEffect(Unit) {
        repository.me()
            .onSuccess { fresh -> user = fresh; onUserUpdated(fresh) }

        val connected =
            billingManager.connect()

        if (connected) {
            val products =
                billingManager.queryProductDetails(
                    listOf(
                        Config.PRODUCT_ID_GOLD_MONTHLY,
                        Config.PRODUCT_ID_PREMIUM_MONTHLY
                    )
                )

            goldProduct =
                products.firstOrNull {
                    it.productId == Config.PRODUCT_ID_GOLD_MONTHLY
                }

            premiumProduct =
                products.firstOrNull {
                    it.productId == Config.PRODUCT_ID_PREMIUM_MONTHLY
                }

            ownedSubscriptions =
                billingManager
                    .queryActiveSubscriptions()
                    .filter {
                        it.purchaseState ==
                            Purchase.PurchaseState.PURCHASED
                    }

            // A purchase whose verification never reached the backend
            // (app killed, no network) must still be verified and
            // acknowledged, or Google refunds it after three days.
            ownedSubscriptions
                .filter {
                    !it.isAcknowledged ||
                        user?.tierSource != "play_subscription"
                }
                .forEach {
                    verifyAndApply(
                        it,
                        silent = true
                    )
                }
        }
    }

    LaunchedEffect(Unit) {
        billingManager.purchases.collect { purchase ->
            verifyAndApply(
                purchase,
                silent = false
            )

            ownedSubscriptions =
                billingManager
                    .queryActiveSubscriptions()
                    .filter {
                        it.purchaseState ==
                            Purchase.PurchaseState.PURCHASED
                    }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            billingManager.disconnect()
        }
    }

    Scaffold(
        containerColor = Bg
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = strings.back,
                        tint = Ink
                    )
                }

                Text(
                    text = strings.accountTitle,
                    modifier = Modifier.weight(1f),
                    color = Ink,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                LanguageToggle()
            }

            Column(
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                val activeUser = user

                if (activeUser != null) {
                    CurrentTierCard(activeUser)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                message?.let { msg ->
                    val messageText =
                        when (msg) {
                            is AccountMessage.PurchaseActivated ->
                                strings.accountPurchaseActivated(
                                    strings.accountTierTitle(msg.tier)
                                )

                            AccountMessage.PurchaseVerifyFailed ->
                                strings.accountPurchaseVerifyFailed

                            AccountMessage.PurchaseOtherAccount ->
                                strings.accountPurchaseOtherAccount

                            AccountMessage.DeleteFailed ->
                                strings.accountDeleteFailed
                        }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = PaleGreen
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = messageText,
                            modifier = Modifier.padding(12.dp),
                            color = Green,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Trial users already have Gold features but still need
                // to be able to buy before the trial runs out.
                val onTrial =
                    activeUser?.tierSource == "trial"

                if (onTrial || (activeUser?.tier != "gold" && activeUser?.tier != "premium")) {
                    UpgradeCard(
                        title = strings.accountTierTitle("gold"),
                        description = strings.accountGoldDescription,
                        price =
                            goldProduct
                                ?.subscriptionOfferDetails
                                ?.firstOrNull()
                                ?.pricingPhases
                                ?.pricingPhaseList
                                ?.firstOrNull()
                                ?.formattedPrice,
                        enabled = goldProduct != null && !purchaseInFlight,
                        onClick = {
                            goldProduct?.let { launchPurchase(it) }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (onTrial || activeUser?.tier != "premium") {
                    UpgradeCard(
                        title = strings.accountTierTitle("premium"),
                        description = strings.accountPremiumDescription,
                        price =
                            premiumProduct
                                ?.subscriptionOfferDetails
                                ?.firstOrNull()
                                ?.pricingPhases
                                ?.pricingPhaseList
                                ?.firstOrNull()
                                ?.formattedPrice,
                        enabled = premiumProduct != null && !purchaseInFlight,
                        onClick = {
                            premiumProduct?.let { launchPurchase(it) }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (activeUser?.tier == "premium" && !onTrial) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = PaleGreen
                            ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Green
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = strings.accountAlreadyPremium,
                                color = Green,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (activeUser?.tierSource == "play_subscription") {
                    OutlinedButton(
                        onClick = {
                            val productId =
                                activeUser.subscriptionProductId()

                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "https://play.google.com/store/account/subscriptions" +
                                            "?package=${context.packageName}" +
                                            (productId?.let { "&sku=$it" } ?: "")
                                    )
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.accountManageSubscription)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        repository.logout()
                        onLoggedOut()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Red
                        ),
                    border = BorderStroke(1.dp, PaleRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(strings.accountLogout)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Required by Google Play for apps that let users create accounts.
                if (activeUser?.tierSource != "manual") {
                    TextButton(
                        onClick = { confirmDelete = true },
                        enabled = !deleting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.accountDeleteButton,
                            color = Red,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = {
                if (!deleting) confirmDelete = false
            },
            title = { Text(strings.accountDeleteTitle) },
            text = { Text(strings.accountDeleteMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleting = true

                        scope.launch {
                            repository
                                .deleteAccount()
                                .onSuccess {
                                    deleting = false
                                    confirmDelete = false
                                    onLoggedOut()
                                }
                                .onFailure {
                                    deleting = false
                                    confirmDelete = false
                                    message = AccountMessage.DeleteFailed
                                }
                        }
                    },
                    enabled = !deleting
                ) {
                    Text(
                        text = strings.accountDeleteConfirm,
                        color = Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDelete = false },
                    enabled = !deleting
                ) {
                    Text(strings.accountDeleteCancel)
                }
            }
        )
    }
}

private fun MembershipUser.subscriptionProductId(): String? =
    when (tier) {
        "gold" -> Config.PRODUCT_ID_GOLD_MONTHLY
        "premium" -> Config.PRODUCT_ID_PREMIUM_MONTHLY
        else -> null
    }

@Composable
private fun CurrentTierCard(
    user: MembershipUser
) {
    val strings = LocalStrings.current
    val language = currentLanguage()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = PaleGold
            ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Gold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = strings.accountTierTitle(user.tier),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = user.email,
                color = Muted,
                fontSize = 12.sp
            )

            val trialEnds =
                formatIsoDate(user.trialEndsAt, language)

            val subscriptionEnds =
                formatIsoDate(user.subscriptionExpiresAt, language)

            when {
                user.tierSource == "trial" && trialEnds != null ->
                    Text(
                        text = strings.accountTrialEndsAt(trialEnds),
                        color = Muted,
                        fontSize = 11.sp
                    )

                user.tierSource == "play_subscription" && subscriptionEnds != null ->
                    Text(
                        text = strings.accountSubscriptionRenewsAt(subscriptionEnds),
                        color = Muted,
                        fontSize = 11.sp
                    )

                user.tierSource == "manual" ->
                    Text(
                        text = strings.accountUnlimited,
                        color = Muted,
                        fontSize = 11.sp
                    )

                else -> {}
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    title: String,
    description: String,
    price: String?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = description,
                        color = Muted,
                        fontSize = 11.sp
                    )
                }

                if (price != null) {
                    Text(
                        text = price,
                        color = Green,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
            ) {
                Text(
                    text =
                        if (enabled)
                            strings.accountUpgradeTo(title)
                        else
                            strings.accountLoadingEllipsis,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
