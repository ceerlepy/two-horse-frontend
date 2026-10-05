package com.twohorse.app.ui.coupons

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.CouponHistoryEntry
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.theme.*

private sealed interface CouponHistoryState {
    data object Loading : CouponHistoryState
    data class Loaded(val entries: List<CouponHistoryEntry>) : CouponHistoryState
    data class Failed(val error: CouponError) : CouponHistoryState
}

/*
 * Premium: the coupons the system froze before each Altılı / Beşli
 * window over the last 30 days, and how many legs each one hit.
 */
@Composable
fun CouponHistoryScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val strings = LocalStrings.current

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var state by
        remember {
            mutableStateOf<CouponHistoryState>(CouponHistoryState.Loading)
        }

    /*
     * The model saves one coupon per budget tier for every window;
     * showing all of them makes the list long, so the member picks
     * one budget and sees that tier's coupon for each window.
     */
    var selectedBudget by
        remember {
            mutableStateOf<Double?>(null)
        }

    LaunchedEffect(Unit) {
        state =
            repository.couponHistory()
                .fold(
                    onSuccess = { CouponHistoryState.Loaded(it) },
                    onFailure = { CouponHistoryState.Failed(couponErrorFromThrowable(it)) }
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = strings.couponHistoryTitle,
                            color = Ink,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = strings.couponHistorySubtitle,
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            when (val current = state) {
                CouponHistoryState.Loading ->
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

                is CouponHistoryState.Failed ->
                    item {
                        Text(
                            text = couponErrorText(current.error, strings),
                            modifier = Modifier.padding(horizontal = 18.dp),
                            color = Red,
                            fontSize = 13.sp
                        )
                    }

                is CouponHistoryState.Loaded ->
                    if (current.entries.isEmpty()) {
                        item {
                            Text(
                                text = strings.couponHistoryEmpty,
                                modifier = Modifier.padding(horizontal = 18.dp),
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        val budgets =
                            current.entries
                                .map { it.budgetTl }
                                .distinct()
                                .sorted()

                        val budget =
                            selectedBudget
                                ?.takeIf { it in budgets }
                                ?: budgets.first()

                        if (budgets.size > 1) {
                            item {
                                Column(
                                    modifier = Modifier.padding(horizontal = 18.dp)
                                ) {
                                    Text(
                                        text = strings.couponHistoryBudget,
                                        color = Muted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(budgets) { value ->
                                            SelectChip(
                                                text = "${value.toInt()} TL",
                                                selected = value == budget,
                                                onClick = { selectedBudget = value }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        items(current.entries.filter { it.budgetTl == budget }) { entry ->
                            CouponHistoryCard(entry)
                        }
                    }
            }
        }
    }
}

@Composable
private fun CouponHistoryCard(
    entry: CouponHistoryEntry
) {
    val strings = LocalStrings.current

    val (badgeText, badgeColor, badgeBg) =
        when {
            !entry.evaluated ->
                Triple(strings.couponHistoryPending, Muted, Bg)

            entry.allLegsHit == true ->
                Triple(strings.couponHistoryAllHit, Color.White, Green)

            else ->
                Triple(
                    strings.couponHistoryLegsHit(entry.hitLegs ?: 0, entry.legCount),
                    Ink,
                    PaleGold
                )
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            "${entry.city} · " +
                                strings.couponHistoryWindow(entry.pool, entry.windowNumber),
                        color = Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "${entry.raceDate} · " +
                                strings.couponHistoryCost(
                                    "%.0f".format(entry.totalTl),
                                    entry.combinations
                                ),
                        color = Muted,
                        fontSize = 11.sp
                    )
                }

                androidx.compose.material3.Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            entry.legs.forEachIndexed { index, leg ->
                Text(
                    text =
                        strings.couponHistoryLeg(
                            index + 1,
                            leg.raceNumber,
                            leg.horseNumbers.joinToString(", ")
                        ),
                    color = Ink,
                    fontSize = 12.sp
                )
            }
        }
    }
}
