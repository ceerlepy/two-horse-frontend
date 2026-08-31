package com.twohorse.app.ui.account

import android.app.Activity
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
        remember { mutableStateOf<String?>(null) }

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
        }
    }

    LaunchedEffect(Unit) {
        billingManager.purchases.collect { purchase ->
            val productId =
                purchase.products.firstOrNull()
                    ?: return@collect

            purchaseInFlight = true
            message = null

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

                    message =
                        strings.accountPurchaseActivated(
                            strings.accountTierTitle(updated.tier)
                        )
                }
                .onFailure {
                    message =
                        strings.accountPurchaseVerifyFailed
                }

            purchaseInFlight = false
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

                message?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = PaleGreen
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(12.dp),
                            color = Green,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (activeUser?.tier != "gold" && activeUser?.tier != "premium") {
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
                            val product = goldProduct
                            if (product != null) {
                                billingManager.launchPurchaseFlow(
                                    context as Activity,
                                    product
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (activeUser?.tier != "premium") {
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
                            val product = premiumProduct
                            if (product != null) {
                                billingManager.launchPurchaseFlow(
                                    context as Activity,
                                    product
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (activeUser?.tier == "premium") {
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
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
