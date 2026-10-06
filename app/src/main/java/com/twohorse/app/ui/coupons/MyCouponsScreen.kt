package com.twohorse.app.ui.coupons

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.MyCoupon
import com.twohorse.app.domain.model.MyCouponLeg
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch

private sealed interface MyCouponsState {
    data object Loading : MyCouponsState
    data class Loaded(val coupons: List<MyCoupon>) : MyCouponsState
    data object Failed : MyCouponsState
    data object Locked : MyCouponsState
}

/*
 * "Kuponlarım": only the coupons this member saved from the coupon
 * screen, each with the legs it hit once the results are in.
 */
@Composable
fun MyCouponsScreen(
    onBack: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var state by
        remember {
            mutableStateOf<MyCouponsState>(MyCouponsState.Loading)
        }

    LaunchedEffect(Unit) {
        state =
            repository.myCoupons()
                .fold(
                    onSuccess = { MyCouponsState.Loaded(it) },
                    onFailure = {
                        if ((it as? ApiException)?.apiCode == "TIER_UPGRADE_REQUIRED")
                            MyCouponsState.Locked
                        else
                            MyCouponsState.Failed
                    }
                )
    }

    Scaffold(
        containerColor = Bg
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
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

                    Column {
                        Text(
                            text = strings.myCouponsTitle,
                            color = Ink,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = strings.myCouponsSubtitle,
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            when (val current = state) {
                MyCouponsState.Loading ->
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Green)
                        }
                    }

                MyCouponsState.Locked ->
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.myCouponsLocked,
                                color = Ink,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            Button(
                                onClick = onUpgradeClick,
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Green
                                    ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = strings.myCouponsUpgrade,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                MyCouponsState.Failed ->
                    item {
                        Text(
                            text = strings.myCouponsLoadFailed,
                            modifier = Modifier.padding(horizontal = 18.dp),
                            color = Red,
                            fontSize = 13.sp
                        )
                    }

                is MyCouponsState.Loaded ->
                    if (current.coupons.isEmpty()) {
                        item {
                            Text(
                                text = strings.myCouponsEmpty,
                                modifier = Modifier.padding(horizontal = 18.dp),
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        items(
                            current.coupons,
                            key = { it.id }
                        ) { coupon ->
                            MyCouponCard(
                                coupon = coupon,
                                onDelete = {
                                    scope.launch {
                                        repository.deleteMyCoupon(coupon.id)
                                            .onSuccess {
                                                state =
                                                    MyCouponsState.Loaded(
                                                        current.coupons.filter { it.id != coupon.id }
                                                    )
                                            }
                                    }
                                }
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun MyCouponCard(
    coupon: MyCoupon,
    onDelete: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        CouponCardHeader(
            title =
                "${coupon.city} · " +
                    strings.couponHistoryWindow(coupon.pool, coupon.windowNumber),
            subtitle =
                "${coupon.raceDate} · " +
                    strings.couponHistoryCost(
                        "%.0f".format(coupon.totalTl),
                        coupon.combinations.toInt()
                    ),
            brush =
                if (coupon.allLegsHit == true) CouponWinBrush
                else CouponHeaderBrush,
            trailing = {
                when {
                    !coupon.evaluated ->
                        CouponPill(
                            text = strings.couponHistoryPending,
                            background = Color.White.copy(alpha = 0.18f),
                            content = Color.White
                        )

                    coupon.allLegsHit == true ->
                        CouponPill(
                            text = strings.couponHistoryAllHit,
                            background = Color.White,
                            content = Color(0xFF6B4508)
                        )

                    else ->
                        CouponPill(
                            text = strings.couponHistoryLegsHit(coupon.hitLegs ?: 0, coupon.legCount)
                        )
                }
            }
        )

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            coupon.legs.forEachIndexed { index, leg ->
                MyCouponLegRow(index + 1, leg)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = strings.myCouponsDelete,
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyCouponLegRow(
    legIndex: Int,
    leg: MyCouponLeg
) {
    val strings = LocalStrings.current
    val hit = leg.hit

    val (accent, pale) =
        when (hit) {
            true -> Green to PaleGreen
            false -> Red to PaleRed
            null -> Muted to Bg
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier =
                Modifier
                    .width(62.dp)
                    .background(pale, RoundedCornerShape(11.dp))
                    .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = strings.couponLegRace(leg.raceNumber),
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                leg.horseNumbers.forEach { number ->
                    val isWinner = number == leg.winner

                    Box(
                        modifier =
                            Modifier
                                .size(24.dp)
                                .background(
                                    if (isWinner) Green else PaleGreen,
                                    CircleShape
                                ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            color = if (isWinner) Color.White else Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (leg.winner != null && hit == false) {
                Text(
                    text = strings.myCouponsWinner(leg.winner),
                    color = Red,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Icon(
            imageVector =
                when (hit) {
                    true -> Icons.Default.CheckCircle
                    false -> Icons.Default.Cancel
                    null -> Icons.Default.Schedule
                },
            contentDescription = null,
            tint = accent,
            modifier = Modifier.padding(end = 8.dp).size(20.dp)
        )
    }
}
