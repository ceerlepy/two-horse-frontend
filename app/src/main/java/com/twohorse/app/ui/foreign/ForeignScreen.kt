package com.twohorse.app.ui.foreign

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.twohorse.app.ui.race.InfoButton
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

                    /* winProb is a Gold+ signal; free members never see the note. */
                    if (selected.races.any { race -> race.runners.any { it.winProb != null } }) {
                        item {
                            Text(
                                text = strings.foreignWinProbNote,
                                modifier = Modifier.padding(horizontal = 18.dp),
                                color = Muted,
                                fontSize = 11.sp
                            )
                        }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ForeignRaceCard(race: ForeignRace) {
    val strings = LocalStrings.current

    val ordered =
        race.runners.sortedWith(
            compareByDescending<ForeignRunner> {
                it.agfPercent ?: Double.NEGATIVE_INFINITY
            }.thenBy { it.number }
        )

    var expanded by rememberSaveable(race.raceNumber) { mutableStateOf(false) }

    /* Same shell as the home and next-day race cards. */
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = CardTone),
        border = BorderStroke(1.dp, CardToneBorder),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = PaleGreen,
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = strings.homeCourseNumberCaps(race.raceNumber),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        color = Green,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                race.time?.let {
                    Spacer(Modifier.width(9.dp))

                    Text(
                        text = it,
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.weight(1f))

                InfoButton(
                    title = strings.foreignWinProbInfoTitle,
                    body = strings.foreignWinProbInfo
                )

                Icon(
                    imageVector =
                        if (expanded) Icons.Default.KeyboardArrowDown
                        else Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Muted
                )
            }

            Text(
                text =
                    listOfNotNull(
                        race.distanceMeters?.let { "$it m" },
                        race.track,
                        strings.homeNextDayRunnerCount(race.runners.size)
                    ).joinToString(" · "),
                color = Muted,
                fontSize = 11.sp
            )
        }

        if (!expanded) {
            return@Card
        }

        HorizontalDivider(color = CardToneBorder)

        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            race.aiPick?.let { pick ->
                val top = pick.ranked.firstOrNull()

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                val leading = index < 3

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(28.dp)
                                .background(
                                    if (leading) Green else PaleGreen,
                                    CircleShape
                                ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${runner.number}",
                            color = if (leading) Color.White else Green,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Column(
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    ) {
                        Text(
                            text = runner.name,
                            color = Ink,
                            fontSize = 13.sp,
                            fontWeight =
                                if (leading) FontWeight.ExtraBold else FontWeight.Medium,
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
                    runner.winProb?.let {
                        Surface(
                            color = PaleGold,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = strings.foreignWinProb(winProbLabel(it)),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                    }
                    runner.agfPercent?.let {
                        Surface(
                            color = if (leading) PaleGreen else Bg,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "%${"%.0f".format(it)}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = if (leading) Green else Muted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/* Whole percent; below 1% shows as "<1". */
private fun winProbLabel(prob: Double): String {
    val percent = prob * 100
    return if (percent < 1.0) "<1" else "%.0f".format(percent)
}

@Composable
private fun LavenderLabel(text: String) {
    Surface(
        color = LavenderSurface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* Gold title on deep lavender, white bold picks, gold total pill. */
private val ForeignAiTitle = Color(0xFFF5C451)

private val ForeignAiBrush =
    Brush.linearGradient(
        listOf(
            Color(0xFF2A2338),
            LavenderSurface,
            Color(0xFF4A3D66)
        )
    )

@Composable
private fun ForeignAiCouponCard(coupon: ForeignAiCoupon) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(ForeignAiBrush)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.foreignAiCouponTitle(coupon.altili),
                    modifier = Modifier.weight(1f),
                    color = ForeignAiTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                coupon.startTime?.let {
                    Text(
                        text = strings.foreignAiCouponStart(it),
                        color = Lavender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            coupon.legs.forEach { leg ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${leg.raceNumber}.K",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Lavender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = leg.selection.joinToString(" - "),
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (coupon.combinations != null && coupon.amountTl != null) {
                Surface(
                    color = ForeignAiTitle,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text =
                            strings.foreignAiCouponTotal(
                                coupon.combinations,
                                "%.0f".format(coupon.amountTl)
                            ),
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                        color = Color(0xFF2B1D03),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
