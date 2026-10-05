package com.twohorse.app.ui.foreign

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.ForeignAiCoupon
import com.twohorse.app.domain.model.ForeignMeeting
import com.twohorse.app.domain.model.ForeignRace
import com.twohorse.app.domain.model.ForeignRunner
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.components.CityChip
import com.twohorse.app.ui.theme.*

private sealed interface ForeignLoadState {
    data object Loading : ForeignLoadState
    data class Loaded(val meetings: List<ForeignMeeting>) : ForeignLoadState
    data object Failed : ForeignLoadState
}

@Composable
fun ForeignScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val strings = LocalStrings.current
    val context = LocalContext.current

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var state by
        remember {
            mutableStateOf<ForeignLoadState>(ForeignLoadState.Loading)
        }

    var selectedCity by
        remember {
            mutableStateOf<String?>(null)
        }

    LaunchedEffect(Unit) {
        state =
            repository
                .foreignMeetings()
                .fold(
                    onSuccess = { ForeignLoadState.Loaded(it) },
                    onFailure = { ForeignLoadState.Failed }
                )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 34.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = strings.foreignBack
                    )
                }

                Column {
                    Text(
                        text = strings.foreignTitle,
                        color = Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = strings.foreignSubtitle,
                        color = Muted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        when (val current = state) {
            ForeignLoadState.Loading ->
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green)
                    }
                }

            ForeignLoadState.Failed ->
                item {
                    ForeignNotice(strings.foreignLoadFailed)
                }

            is ForeignLoadState.Loaded -> {
                val meetings = current.meetings

                if (meetings.isEmpty()) {
                    item {
                        ForeignNotice(strings.foreignEmpty)
                    }
                } else {
                    val selected =
                        meetings.firstOrNull { it.city == selectedCity }
                            ?: meetings.first()

                    item {
                        LazyRow(
                            modifier = Modifier.padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(meetings) { meeting ->
                                CityChip(
                                    city = meeting.city,
                                    selected = meeting.city == selected.city,
                                    onClick = { selectedCity = meeting.city }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = strings.foreignAgfNote,
                            modifier = Modifier.padding(horizontal = 18.dp),
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }

                    items(
                        selected.aiCoupons,
                        key = { "${selected.city}-ai-${it.altili}" }
                    ) { coupon ->
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp)
                        ) {
                            ForeignAiCouponCard(coupon)
                        }
                    }

                    items(
                        selected.races,
                        key = { "${selected.city}-${it.raceNumber}" }
                    ) { race ->
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp)
                        ) {
                            ForeignRaceCard(race)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForeignNotice(text: String) {
    Text(
        text = text,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 24.dp),
        color = Muted,
        fontSize = 13.sp
    )
}

@Composable
private fun ForeignRaceCard(race: ForeignRace) {
    val strings = LocalStrings.current

    val ordered =
        race.runners.sortedWith(
            compareByDescending<ForeignRunner> {
                it.agfPercent ?: Double.NEGATIVE_INFINITY
            }.thenBy { it.number }
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.foreignRaceTitle(race.raceNumber.toString()),
                    modifier = Modifier.weight(1f),
                    color = Ink,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text =
                        listOfNotNull(
                            race.time,
                            race.distanceMeters?.let { "${it}m" },
                            race.track
                        ).joinToString(" · "),
                    color = Muted,
                    fontSize = 11.sp
                )
            }

            race.aiPick?.let { pick ->
                val top = pick.ranked.firstOrNull()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    top?.let {
                        LavenderLabel(strings.foreignAiTop("${it.number}-${it.name}"))
                    }

                    if (pick.selection.isNotEmpty()) {
                        LavenderLabel(strings.foreignAiSelection(pick.selection.joinToString("-")))
                    }
                }

            }

            ordered.forEachIndexed { index, runner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${runner.number}",
                        modifier = Modifier.width(26.dp),
                        color = if (index < 3) Green else Muted,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = runner.name,
                            color = Ink,
                            fontSize = 13.sp,
                            fontWeight =
                                if (index < 3) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val meta =
                            listOfNotNull(
                                runner.jockey,
                                runner.weight?.let { "${"%.1f".format(it).removeSuffix(",0").removeSuffix(".0")} kg" },
                                runner.recentForm
                            ).joinToString(" · ")
                        if (meta.isNotBlank()) {
                            Text(
                                text = meta,
                                color = Muted,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    runner.agfPercent?.let {
                        Text(
                            text = "%${"%.0f".format(it)}",
                            color = if (index < 3) Green else Muted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LavenderLabel(text: String) {
    Surface(
        color = LavenderSurface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            color = Lavender,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ForeignAiCouponCard(coupon: ForeignAiCoupon) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LavenderSurface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.foreignAiCouponTitle(coupon.altili),
                    modifier = Modifier.weight(1f),
                    color = Lavender,
                    fontWeight = FontWeight.Black
                )
                coupon.startTime?.let {
                    Text(
                        text = strings.foreignAiCouponStart(it),
                        color = Lavender,
                        fontSize = 11.sp
                    )
                }
            }

            coupon.legs.forEach { leg ->
                Text(
                    text = strings.foreignAiLeg(leg.raceNumber, leg.selection.joinToString("-")),
                    color = Lavender,
                    fontSize = 12.sp
                )
            }

            if (coupon.combinations != null && coupon.amountTl != null) {
                Text(
                    text =
                        strings.foreignAiCouponTotal(
                            coupon.combinations,
                            "%.0f".format(coupon.amountTl)
                        ),
                    color = Lavender,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
