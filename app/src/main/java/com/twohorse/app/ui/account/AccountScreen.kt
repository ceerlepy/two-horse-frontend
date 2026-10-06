package com.twohorse.app.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.Config
import com.twohorse.app.billing.BASE_PLAN_MONTHLY
import com.twohorse.app.billing.BASE_PLAN_YEARLY
import com.twohorse.app.billing.BillingManager
import com.twohorse.app.billing.offerFor
import com.twohorse.app.billing.priceFor
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
    data object SwitchScheduled : AccountMessage
    data object PurchaseVerifyFailed : AccountMessage
    data object PurchaseOtherAccount : AccountMessage
    data object DeleteFailed : AccountMessage
    data object RestoreNone : AccountMessage
}

internal fun formatIsoDate(
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

    var billingPeriod by
        remember { mutableStateOf(BASE_PLAN_MONTHLY) }

    var confirmSwitch by
        remember { mutableStateOf(false) }

    var confirmCancel by
        remember { mutableStateOf(false) }

    val lifecycleOwner =
        LocalLifecycleOwner.current

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
                    // A Premium -> Gold switch is only booked for the
                    // end of the paid period; nothing changes today.
                    message =
                        if (updated.subscriptionPendingTier != null)
                            AccountMessage.SwitchScheduled
                        else
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
        product: ProductDetails,
        basePlanId: String,
        deferred: Boolean = false
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
            basePlanId = basePlanId,
            oldPurchaseToken = oldToken,
            deferred = deferred
        )
    }

    // Cancelling, resuming or a pending plan change happen in Google
    // Play, so the plan is re-read from Play every time the member
    // comes back to this screen.
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(
            Lifecycle.State.RESUMED
        ) {
            repository.me(refreshSubscription = true)
                .onSuccess { fresh -> user = fresh; onUserUpdated(fresh) }
        }
    }

    LaunchedEffect(Unit) {
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

    fun restorePurchases() {
        if (purchaseInFlight) return

        scope.launch {
            message = null

            val owned =
                billingManager
                    .queryActiveSubscriptions()
                    .filter {
                        it.purchaseState ==
                            Purchase.PurchaseState.PURCHASED
                    }

            ownedSubscriptions = owned

            if (owned.isEmpty()) {
                message = AccountMessage.RestoreNone
                return@launch
            }

            owned.forEach {
                verifyAndApply(
                    it,
                    silent = false
                )
            }
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
                    val isError =
                        msg !is AccountMessage.PurchaseActivated &&
                            msg !is AccountMessage.SwitchScheduled

                    val messageText =
                        when (msg) {
                            is AccountMessage.PurchaseActivated ->
                                strings.accountPurchaseActivated(
                                    strings.accountTierTitle(msg.tier)
                                )

                            AccountMessage.SwitchScheduled ->
                                strings.accountSwitchScheduled(
                                    formatIsoDate(
                                        activeUser?.subscriptionExpiresAt,
                                        currentLanguage()
                                    ) ?: ""
                                )

                            AccountMessage.PurchaseVerifyFailed ->
                                strings.accountPurchaseVerifyFailed

                            AccountMessage.PurchaseOtherAccount ->
                                strings.accountPurchaseOtherAccount

                            AccountMessage.DeleteFailed ->
                                strings.accountDeleteFailed

                            AccountMessage.RestoreNone ->
                                strings.accountRestoreNone
                        }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    if (isError) PaleRed else PaleGreen
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = messageText,
                            modifier = Modifier.padding(12.dp),
                            color = if (isError) Red else Green,
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

                val paidTier =
                    if (onTrial) "free" else activeUser?.tier ?: "free"

                if (paidTier != "premium") {
                    Text(
                        text = strings.accountChoosePlan,
                        color = Ink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PlanComparisonTable(
                        currentTier = activeUser?.tier ?: "free"
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Yearly plans may not be switched on in Play Console yet;
                // the toggle only appears once at least one product has one.
                val yearlyAvailable =
                    listOf(goldProduct, premiumProduct).any {
                        it?.subscriptionOfferDetails.orEmpty().any { offer ->
                            offer.basePlanId == BASE_PLAN_YEARLY
                        }
                    }

                LaunchedEffect(yearlyAvailable) {
                    if (!yearlyAvailable) billingPeriod = BASE_PLAN_MONTHLY
                }

                if (paidTier != "premium" && yearlyAvailable) {
                    BillingPeriodToggle(
                        selected = billingPeriod,
                        onSelect = { billingPeriod = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (paidTier != "gold" && paidTier != "premium") {
                    PlanCard(
                        title = strings.accountTierTitle("gold"),
                        description = strings.accountGoldDescription,
                        price = goldProduct.priceFor(billingPeriod),
                        yearly = billingPeriod == BASE_PLAN_YEARLY,
                        highlighted = true,
                        badge = null,
                        accent = Gold,
                        enabled =
                            goldProduct?.offerFor(billingPeriod) != null &&
                                !purchaseInFlight,
                        onClick = {
                            goldProduct?.let { launchPurchase(it, billingPeriod) }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (paidTier != "premium") {
                    PlanCard(
                        title = strings.accountTierTitle("premium"),
                        description = strings.accountPremiumDescription,
                        price = premiumProduct.priceFor(billingPeriod),
                        yearly = billingPeriod == BASE_PLAN_YEARLY,
                        highlighted = false,
                        badge = null,
                        accent = Green,
                        enabled =
                            premiumProduct?.offerFor(billingPeriod) != null &&
                                !purchaseInFlight,
                        onClick = {
                            premiumProduct?.let { launchPurchase(it, billingPeriod) }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Play policy: subscription terms must be clear before purchase.
                    Text(
                        text = strings.accountRenewalTerms,
                        color = Muted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    TextButton(
                        onClick = { restorePurchases() },
                        enabled = !purchaseInFlight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.accountRestorePurchases,
                            color = Green,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    AlreadyPremiumCard()

                    Spacer(modifier = Modifier.height(12.dp))

                    val onPlay =
                        activeUser?.tierSource == "play_subscription"

                    val switchPending =
                        activeUser?.subscriptionPendingTier != null

                    val canceled =
                        activeUser?.subscriptionAutoRenew == false

                    // Downgrade waits for the paid month to end (Play
                    // DEFERRED replacement), like other subscription apps.
                    if (onPlay && !switchPending && !canceled) {
                        Text(
                            text = strings.accountChangePlan,
                            color = Ink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        PlanCard(
                            title = strings.accountTierTitle("gold"),
                            description = strings.accountGoldDescription,
                            price = goldProduct.priceFor(BASE_PLAN_MONTHLY),
                            yearly = false,
                            highlighted = false,
                            badge = null,
                            accent = Gold,
                            enabled =
                                goldProduct?.offerFor(BASE_PLAN_MONTHLY) != null &&
                                    !purchaseInFlight,
                            buttonText = strings.accountSwitchToGold,
                            note = strings.accountGoldDowngradeNote,
                            onClick = { confirmSwitch = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (activeUser?.tierSource == "play_subscription") {
                    val endDate =
                        formatIsoDate(
                            activeUser.subscriptionExpiresAt,
                            currentLanguage()
                        ) ?: ""

                    val tierTitle =
                        strings.accountTierTitle(activeUser.tier)

                    if (activeUser.subscriptionAutoRenew == false) {
                        NoteCard(
                            text = strings.accountCanceledNote(endDate, tierTitle),
                            warning = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                openPlaySubscriptions(
                                    context,
                                    activeUser.subscriptionProductId()
                                )
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Green
                                )
                        ) {
                            Text(
                                text = strings.accountResume,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        if (activeUser.subscriptionPendingTier != null) {
                            NoteCard(
                                text = strings.accountSwitchScheduled(endDate),
                                warning = false
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedButton(
                            onClick = { confirmCancel = true },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Text(
                                text = strings.accountCancelButton,
                                color = Ink,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = {
                        repository.logout()
                        onLoggedOut()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
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

                    Text(
                        text = strings.accountLogout,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Required by Google Play for apps that let users create accounts.
                if (activeUser?.tierSource != "manual") {
                    TextButton(
                        onClick = { confirmDelete = true },
                        enabled = !deleting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.accountDeleteButton,
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    val dialogUser = user

    if (confirmSwitch && dialogUser != null) {
        AlertDialog(
            onDismissRequest = { confirmSwitch = false },
            title = { Text(strings.accountSwitchTitle) },
            text = {
                Text(
                    strings.accountSwitchBody(
                        formatIsoDate(
                            dialogUser.subscriptionExpiresAt,
                            currentLanguage()
                        ) ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmSwitch = false

                        goldProduct?.let {
                            launchPurchase(
                                it,
                                BASE_PLAN_MONTHLY,
                                deferred = true
                            )
                        }
                    }
                ) {
                    Text(
                        text = strings.accountSwitchToGold,
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSwitch = false }) {
                    Text(strings.accountDeleteCancel)
                }
            }
        )
    }

    if (confirmCancel && dialogUser != null) {
        AlertDialog(
            onDismissRequest = { confirmCancel = false },
            title = { Text(strings.accountCancelButton) },
            text = {
                Text(
                    strings.accountCancelBody(
                        formatIsoDate(
                            dialogUser.subscriptionExpiresAt,
                            currentLanguage()
                        ) ?: "",
                        strings.accountTierTitle(dialogUser.tier)
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmCancel = false

                        openPlaySubscriptions(
                            context,
                            dialogUser.subscriptionProductId()
                        )
                    }
                ) {
                    Text(
                        text = strings.accountGoToPlay,
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmCancel = false }) {
                    Text(strings.accountDeleteCancel)
                }
            }
        )
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

private fun openPlaySubscriptions(
    context: android.content.Context,
    productId: String?
) {
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
}

@Composable
private fun AlreadyPremiumCard() {
    val strings = LocalStrings.current

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
}

@Composable
private fun NoteCard(
    text: String,
    warning: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = if (warning) PaleGold else PaleGreen
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            color = Ink,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold
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
private fun BillingPeriodToggle(
    selected: String,
    onSelect: (String) -> Unit
) {
    val strings = LocalStrings.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Surface)
                .padding(4.dp)
    ) {
        listOf(
            BASE_PLAN_MONTHLY to strings.accountPeriodMonthly,
            BASE_PLAN_YEARLY to strings.accountPeriodYearly
        ).forEach { (period, label) ->
            val active = period == selected

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) Ink else Color.Transparent)
                        .clickable { onSelect(period) }
                        .padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (period == BASE_PLAN_YEARLY) {
                    Text(
                        text = strings.accountYearlySaving,
                        color = if (active) Gold else Green,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

internal fun daysLeft(
    isoEnd: String?
): Int? {
    val end =
        isoEnd
            ?.let { runCatching { Instant.parse(it) }.getOrNull() }
            ?: return null

    val millis =
        end.toEpochMilli() - System.currentTimeMillis()

    return if (millis <= 0) 0
    else ((millis + 86_399_999L) / 86_400_000L).toInt()
}

@Composable
private fun CurrentTierCard(
    user: MembershipUser
) {
    val strings = LocalStrings.current
    val language = currentLanguage()

    // An ended trial reads as a plain Free plan, not "0 days left".
    val onTrial =
        user.tierSource == "trial" && !user.isFree

    val gradient =
        when (user.tier) {
            "premium" ->
                listOf(Ink, Green)

            "gold" ->
                listOf(Color(0xFF8A5A12), Gold)

            else ->
                listOf(Color(0xFF3B4641), Muted)
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(gradient)
                )
                .padding(18.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = strings.accountTierTitle(user.tier),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text =
                            if (onTrial) strings.accountTrialBadge
                            else strings.accountCurrentPlan,
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.displayName ?: user.email,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )

            val trialEnds =
                formatIsoDate(user.trialEndsAt, language)

            val subscriptionEnds =
                formatIsoDate(user.subscriptionExpiresAt, language)

            Spacer(modifier = Modifier.height(12.dp))

            when {
                onTrial && trialEnds != null -> {
                    val left =
                        daysLeft(user.trialEndsAt) ?: 0

                    Text(
                        text = strings.accountDaysLeft(left),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = {
                            (left / 7f).coerceIn(0f, 1f)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.accountTrialEndsAt(trialEnds),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }

                user.tierSource == "play_subscription" && subscriptionEnds != null ->
                    Text(
                        text =
                            when {
                                user.subscriptionPendingTier != null ->
                                    strings.accountSwitchScheduledCard(subscriptionEnds)

                                user.subscriptionAutoRenew == false ->
                                    strings.accountCanceledCard(subscriptionEnds)

                                else ->
                                    strings.accountSubscriptionRenewsAt(subscriptionEnds)
                            },
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )

                user.tierSource == "manual" ->
                    Text(
                        text = strings.accountUnlimited,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )

                else -> {}
            }
        }
    }
}

private sealed interface PlanCell {
    data object Yes : PlanCell
    data object No : PlanCell
    data class Label(val text: String) : PlanCell
}

@Composable
internal fun PlanComparisonTable(
    currentTier: String
) {
    val strings = LocalStrings.current

    val rows: List<Pair<String, List<PlanCell>>> =
        listOf(
            strings.accountFeatureProgram to
                listOf(PlanCell.Yes, PlanCell.Yes, PlanCell.Yes),
            strings.accountFeatureTraining to
                listOf(PlanCell.Yes, PlanCell.Yes, PlanCell.Yes),
            strings.accountFeatureSignals to
                listOf(PlanCell.No, PlanCell.Yes, PlanCell.Yes),
            strings.accountFeatureCoupons to
                listOf(
                    PlanCell.No,
                    PlanCell.Label(strings.accountCouponLimitGold),
                    PlanCell.Label(strings.accountCouponUnlimited)
                ),
            strings.accountFeatureDailyCoupons to
                listOf(
                    PlanCell.No,
                    PlanCell.Label(strings.accountDailyCouponsGold),
                    PlanCell.Label(strings.accountCouponUnlimited)
                ),
            strings.accountFeatureMyCoupons to
                listOf(PlanCell.No, PlanCell.Yes, PlanCell.Yes),
            strings.accountFeatureValueModel to
                listOf(PlanCell.No, PlanCell.No, PlanCell.Yes),
            strings.accountFeatureAskAi to
                listOf(
                    PlanCell.No,
                    PlanCell.No,
                    PlanCell.Label(strings.accountAskAiPremium)
                ),
            strings.accountFeatureVideos to
                listOf(PlanCell.Yes, PlanCell.Yes, PlanCell.Yes)
        )

    val tiers =
        listOf("free", "gold", "premium")

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
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1.6f))

                tiers.forEach { tier ->
                    Text(
                        text = strings.accountTierTitle(tier),
                        modifier = Modifier.weight(1f),
                        color =
                            when (tier) {
                                "gold" -> Gold
                                "premium" -> Green
                                else -> Muted
                            },
                        fontSize = 12.sp,
                        fontWeight =
                            if (tier == currentTier) FontWeight.Black
                            else FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            rows.forEachIndexed { index, (feature, cells) ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        color = Border
                    )
                }

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature,
                        modifier = Modifier.weight(1.6f),
                        color = Ink,
                        fontSize = 12.sp
                    )

                    cells.forEach { cell ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (cell) {
                                PlanCell.Yes ->
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Green,
                                        modifier = Modifier.size(18.dp)
                                    )

                                PlanCell.No ->
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Border,
                                        modifier = Modifier.size(18.dp)
                                    )

                                is PlanCell.Label ->
                                    Text(
                                        text = cell.text,
                                        color = Ink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    description: String,
    price: String?,
    yearly: Boolean,
    highlighted: Boolean,
    badge: String?,
    accent: Color,
    enabled: Boolean,
    buttonText: String? = null,
    note: String? = null,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (highlighted) PaleGold else Surface
            ),
        border =
            BorderStroke(
                if (highlighted) 2.dp else 1.dp,
                if (highlighted) accent else Border
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = accent,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = badge,
                            modifier =
                                Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 3.dp
                                ),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (price != null) {
                    Text(
                        text = price,
                        color = Ink,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )

                    Text(
                        text =
                            if (yearly) strings.accountPerYear
                            else strings.accountPerMonth,
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            if (note != null) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = note,
                    color = Ink,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (highlighted) Ink else Green
                    )
            ) {
                Text(
                    text =
                        if (enabled)
                            buttonText ?: strings.accountUpgradeTo(title)
                        else
                            strings.accountLoadingEllipsis,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
