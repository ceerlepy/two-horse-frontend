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
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.components.CityChip
import com.twohorse.app.ui.components.RegionTabs
import com.twohorse.app.ui.components.TwoHorseHeader
import com.twohorse.app.ui.components.SixFoldEntryCard
import com.twohorse.app.ui.race.AskAiCardButton
import com.twohorse.app.ui.race.AskAiFab
import com.twohorse.app.ui.race.AskAiSheet
import com.twohorse.app.ui.race.WHOLE_MEETING
import com.twohorse.app.ui.race.InfoButton
import com.twohorse.app.ui.theme.*

private sealed interface ForeignLoadState {
    data object Loading : ForeignLoadState
    data class Loaded(val meetings: List<ForeignMeeting>) : ForeignLoadState
    data object Failed : ForeignLoadState
}

@Composable
fun ForeignScreen(
    onBack: () -> Unit,
    onOpenCoupons: (city: String, raceDate: String?) -> Unit = { _, _ -> },
    onHistoryClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    currentUser: MembershipUser? = null,
    onUpgradeClick: () -> Unit = {}
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

    /* The race whose "AI'ya sor" sheet is open, if any. */
    var askRaceNumber by
        remember {
            mutableStateOf<Int?>(null)
        }

    var refreshKey by
        remember {
            mutableStateOf(0)
        }

    var refreshing by
        remember {
            mutableStateOf(false)
        }

    LaunchedEffect(refreshKey) {
        refreshing = true
        state =
            repository
                .foreignMeetings()
                .fold(
                    onSuccess = { ForeignLoadState.Loaded(it) },
                    onFailure = { ForeignLoadState.Failed }
                )
        refreshing = false
    }

    /* The same meeting the list is showing, including its first-load default. */
    val shownMeeting =
        (state as? ForeignLoadState.Loaded)
            ?.meetings
            ?.let { meetings ->
                meetings.firstOrNull { it.city == selectedCity }
                    ?: meetings.firstOrNull()
            }

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        /* Room under the last card for the "AI'ya sor" button. */
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* Same top as home: the tabs, not a back arrow, switch screens. */
        item {
            TwoHorseHeader(
                refreshing = refreshing,
                onRefresh = { if (!refreshing) refreshKey++ },
                onHistory = onHistoryClick,
                onAccount = onAccountClick
            )
        }

        item {
            RegionTabs(
                foreignSelected = true,
                onSelect = { foreign -> if (!foreign) onBack() }
            )
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

                    /*
                     * Our coupon is built on the coupon screen at the
                     * member's own budget, entered from the same card as
                     * on home; nothing is pre-built here.
                     */
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp)
                        ) {
                            SixFoldEntryCard(
                                onClick = { onOpenCoupons(selected.city, selected.raceDate) }
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
                            ForeignRaceCard(
                                race = race,
                                onAskAi = { askRaceNumber = race.raceNumber }
                            )
                        }
                    }
                }
            }
        }
    }

    /*
     * "AI'ya sor" sits bottom-right like on a domestic race screen and
     * asks about the whole meeting; each race card has its own button.
     */
    if (shownMeeting?.races?.isNotEmpty() == true) {
        AskAiFab(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 18.dp, bottom = 18.dp),
            onClick = { askRaceNumber = WHOLE_MEETING }
        )
    }
    }

    val askMeeting = shownMeeting
    val askRace = askRaceNumber

    if (askMeeting != null && askRace != null) {
        AskAiSheet(
            city = askMeeting.city,
            raceNumber = askRace,
            raceDate = askMeeting.raceDate,
            isPremium = currentUser?.tier == "premium",
            repository = repository,
            onUpgradeClick = {
                askRaceNumber = null
                onUpgradeClick()
            },
            onDismiss = {
                askRaceNumber = null
            },
            foreign = true
        )
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
private fun ForeignRaceCard(
    race: ForeignRace,
    onAskAi: () -> Unit = {}
) {
    val strings = LocalStrings.current

    val ordered =
        race.runners.sortedWith(
            compareByDescending<ForeignRunner> {
                it.agfPercent ?: Double.NEGATIVE_INFINITY
            }.thenBy { it.number }
        )

    var expanded by rememberSaveable(race.raceNumber) { mutableStateOf(false) }

    /* Our corrected figure picks the favourite, not AGF. */
    val favourite =
        race.runners
            .filter { it.winProb != null }
            .maxByOrNull { it.winProb ?: 0.0 }

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

                /* This race only; the bottom-right button asks about the whole meeting. */
                AskAiCardButton(onClick = onAskAi)

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

            /*
             * The same favourite line the home cards carry, so a foreign
             * card answers "who does it like?" without being opened. Our
             * own figure picks it, not AGF, because that is the whole
             * point of correcting AGF. Gold and up only: without winProb
             * there is nothing of ours to show.
             */
            favourite?.let { pick ->
                HorizontalDivider(color = CardToneBorder)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = strings.homeModelFavorite,
                            color = Muted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "#${pick.number} ${pick.name}",
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        pick.agfPercent?.let {
                            Text(
                                text = "AGF %${"%.0f".format(it)}",
                                color = Muted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    pick.winProb?.let {
                        Surface(
                            color = PaleGold,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = strings.foreignWinProb(winProbLabel(it)),
                                modifier =
                                    Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                color = Gold,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            /* TJK's official top three, once the race is run. */
            if (race.result.isNotEmpty()) {
                HorizontalDivider(color = CardToneBorder)

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = strings.foreignResultTitle,
                        color = Muted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    race.result.forEach { placed ->
                        Text(
                            text = "${placed.position}. #${placed.number} ${placed.name}",
                            color = Ink,
                            fontSize = 13.sp,
                            fontWeight =
                                if (placed.position == 1) FontWeight.Black else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
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

    ForeignCouponCard(
        title = strings.foreignAiCouponTitle(coupon.altili),
        startTime = coupon.startTime,
        legs = coupon.legs.map { it.raceNumber to it.selection },
        combinations = coupon.combinations,
        amountTl = coupon.amountTl,
        coverageProbability = null,
        note = strings.foreignAltCouponNote,
        brush = ForeignAiBrush,
        titleColor = ForeignAiTitle,
        legLabelColor = Lavender,
        noteColor = Lavender,
        /* Ours is the one to read first, so this one opens on a tap. */
        collapsible = true
    )
}

/*
 * Our own altılı, built from the corrected win chances. Green rather
 * than the alternative coupon's lavender: the same brand colour the
 * rest of the app uses for our own figures, so the two cards are never
 * read as the same thing.
 */
@Composable
private fun ForeignCouponCard(
    title: String,
    startTime: String?,
    legs: List<Pair<Int, List<Int>>>,
    combinations: Int?,
    amountTl: Double?,
    coverageProbability: Double?,
    note: String,
    brush: Brush,
    titleColor: Color,
    legLabelColor: Color,
    noteColor: Color,
    collapsible: Boolean = false
) {
    val strings = LocalStrings.current

    var expanded by remember(title) { mutableStateOf(!collapsible) }

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
                    .background(brush)
                    .then(
                        if (collapsible) {
                            Modifier.clickable { expanded = !expanded }
                        } else {
                            Modifier
                        }
                    )
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                startTime?.let {
                    Text(
                        text = strings.foreignAiCouponStart(it),
                        color = legLabelColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (collapsible) {
                    Text(
                        text =
                            if (expanded) strings.foreignAltCouponCollapse
                            else strings.foreignAltCouponExpand,
                        modifier = Modifier.padding(start = 10.dp),
                        color = titleColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (expanded) {

            legs.forEach { (raceNumber, selection) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$raceNumber.K",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = legLabelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = selection.joinToString(" - "),
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (combinations != null && amountTl != null) {
                    Surface(
                        color = titleColor,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text =
                                strings.foreignAiCouponTotal(
                                    combinations,
                                    "%.0f".format(amountTl)
                                ),
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                            color = Color(0xFF2B1D03),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                coverageProbability?.let { coverage ->
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text =
                                strings.foreignCouponCoverage(
                                    "%.1f".format(coverage * 100)
                                ),
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Text(
                text = note,
                color = noteColor,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            }
        }
    }
}
