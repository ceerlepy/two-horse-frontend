package com.twohorse.app.ui.race

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.*
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.components.*
import com.twohorse.app.ui.theme.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun RaceDetailScreen(
    race: Race,
    currentUser: MembershipUser?,
    onBack: () -> Unit,
    onOpenCoupons: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val strings = LocalStrings.current

    val canViewVideos =
        currentUser?.tier == "premium"

    val screenContext =
        LocalContext.current

    val repository =
        remember {
            TwoHorseRepository(
                screenContext
            )
        }

    var currentRace by
        remember(
            race.city,
            race.number
        ) {
            mutableStateOf(race)
        }

    var refreshing by
        remember {
            mutableStateOf(false)
        }

    var error by
        remember {
            mutableStateOf<String?>(null)
        }

    var refreshKey by
        remember {
            mutableIntStateOf(0)
        }

    var deepExpanded by
        remember {
            mutableStateOf(false)
        }

    LaunchedEffect(
        refreshKey,
        race.city,
        race.number
    ) {
        if (refreshing) {
            return@LaunchedEffect
        }

        refreshing = true
        error = null

        repository
            .today()
            .onSuccess { today ->
                val fresh =
                    today.meetings
                        .asSequence()
                        .flatMap {
                            it.races.asSequence()
                        }
                        .firstOrNull {
                            it.city ==
                                race.city &&
                            it.number ==
                                race.number
                        }

                if (fresh != null) {
                    currentRace = fresh
                } else {
                    error =
                        strings.raceNotFoundInProgram
                }
            }
            .onFailure {
                error =
                    strings.raceRefreshFailed
            }

        refreshing = false
    }

    val horses =
        currentRace.horses
            .sortedWith(
                compareByDescending<Horse> {
                    it.score
                        ?: Double.NEGATIVE_INFINITY
                }
                    .thenByDescending {
                        it.agfPercent
                            ?: Double.NEGATIVE_INFINITY
                    }
                    .thenBy {
                        it.number
                    }
            )

    val favorite =
        horses.firstOrNull()

    val rival =
        horses.getOrNull(1)

    val surprise =
        horses.getOrNull(2)

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                bottom = 34.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        item {
            RaceHeader(
                race = currentRace,
                refreshing = refreshing,
                onRefresh = {
                    if (!refreshing) {
                        refreshKey++
                    }
                },
                onBack = onBack
            )
        }

        error?.let {
            item {
                Notice(
                    text = it
                )
            }
        }

        favorite?.let {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    ResultHero(
                        favorite = it,
                        rival = rival,
                        surprise = surprise
                    )
                }
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                RaceRiskCard(
                    race = currentRace
                )
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                Button(
                    onClick = {
                        onOpenCoupons(
                            currentRace.city
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 50.dp
                            ),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Green
                        ),
                    shape =
                        RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text =
                            strings.raceCouponButton,
                        fontWeight =
                            FontWeight.Black
                    )
                }
            }
        }

        if (
            horses.isNotEmpty()
        ) {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    SectionHeader(
                        title =
                            strings.raceAllHorsesTitle,
                        subtitle =
                            strings.raceHorseCount(horses.size)
                    )
                }
            }

            itemsIndexed(
                items = horses,
                key = {
                    _,
                    horse ->
                    horse.number
                }
            ) {
                index,
                horse ->

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    HorseCard(
                        horse = horse,
                        rank = index + 1,
                        raceDate = currentRace.raceDate,
                        city = currentRace.city,
                        raceNumber = currentRace.number,
                        canViewVideos = canViewVideos
                    )
                }
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                ExpandableAnalysisHeader(
                    expanded =
                        deepExpanded,
                    onClick = {
                        deepExpanded =
                            !deepExpanded
                    }
                )
            }
        }

        if (
            deepExpanded
        ) {
            favorite?.let {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        DeepAnalysisCard(
                            horse = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RaceHeader(
    race: Race,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    val compact =
        isCompactScreen()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier =
                Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = strings.back,
                tint = Ink
            )
        }

        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text =
                    strings.raceCityAndNumber(race.city, race.number),
                color = Ink,
                fontSize =
                    if (compact)
                        18.sp
                    else
                        21.sp,
                fontWeight =
                    FontWeight.Black,
                maxLines = 2
            )

            Text(
                text =
                    raceMeta(race),
                color = Muted,
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !refreshing,
            modifier =
                Modifier.size(48.dp)
        ) {
            if (refreshing) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Green
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription =
                        strings.raceRefresh,
                    tint = Ink
                )
            }
        }
    }
}

@Composable
private fun ResultHero(
    favorite: Horse,
    rival: Horse?,
    surprise: Horse?
) {
    val strings = LocalStrings.current

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Ink
            ),
        shape =
            RoundedCornerShape(26.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {
            Text(
                text =
                    strings.raceLikelyWinner,
                color = Gold,
                fontWeight =
                    FontWeight.ExtraBold,
                fontSize = 10.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            "#${favorite.number} ${favorite.name}",
                        color = Color.White,
                        fontWeight =
                            FontWeight.Black,
                        fontSize = 24.sp,
                        maxLines = 2,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Text(
                        text =
                            strings.raceConfidenceScore(
                                favorite.score
                                    ?.let {
                                        "%.1f".format(it)
                                    }
                                    ?: "—"
                            ),
                        color =
                            Color.White.copy(
                                alpha = 0.72f
                            ),
                        fontSize = 13.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Surface(
                    color =
                        Color.White.copy(
                            alpha = 0.10f
                        ),
                    shape = CircleShape
                ) {
                    Text(
                        text =
                            favorite.score
                                ?.roundToInt()
                                ?.toString()
                                ?: "—",
                        modifier =
                            Modifier.padding(18.dp),
                        color = Gold,
                        fontWeight =
                            FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )

            DarkMetric(
                strings.raceAgf,
                favorite.agfPercent
                    ?.let {
                        "%${"%.1f".format(it)}"
                    }
                    ?: strings.noData
            )

            DarkMetric(
                strings.raceHp,
                favorite.hp
                    ?.let {
                        strings.raceHpPoints(it)
                    }
                    ?: strings.noData
            )

            DarkMetric(
                strings.raceExpertSupport,
                expertSummary(
                    favorite
                )
            )

            DarkMetric(
                strings.raceField,
                fieldSummary(
                    favorite
                )
            )

            DarkMetric(
                strings.raceMarket,
                marketSummary(
                    favorite
                )
            )

            DarkMetric(
                strings.raceForm,
                favorite.recentForm
                    .ifBlank {
                        strings.noData
                    }
            )

            favorite.learningAdjustment
                ?.let {
                    DarkMetric(
                        strings.raceLearning,
                        strings.raceLearningDelta(
                            "${
                                if (it >= 0)
                                    "+"
                                else
                                    ""
                            }${"%.1f".format(it)}"
                        )
                    )
                }

            if (
                rival != null ||
                surprise != null
            ) {
                Spacer(
                    Modifier.height(13.dp)
                )

                HorizontalDivider(
                    color =
                        Color.White.copy(
                            alpha = 0.12f
                        )
                )

                Spacer(
                    Modifier.height(11.dp)
                )

                rival?.let {
                    Text(
                        text =
                            strings.raceTopRival(
                                it.number,
                                it.name,
                                it.score?.roundToInt() ?: 0
                            ),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                surprise?.let {
                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            strings.raceSurprise(
                                it.number,
                                it.name,
                                it.score?.roundToInt() ?: 0
                            ),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DarkMetric(
    label: String,
    value: String
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            modifier =
                Modifier.weight(1f),
            color =
                Color.White.copy(
                    alpha = 0.58f
                ),
            fontSize = 11.sp
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun RaceRiskCard(
    race: Race
) {
    val strings = LocalStrings.current

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(15.dp)
        ) {
            Text(
                text =
                    strings.raceRiskMapTitle,
                color = Ink,
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Black
            )

            RaceInsightSummary(
                race = race
            )

            race.uncertainty?.let {
                Spacer(
                    Modifier.height(9.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {
                    MiniMetric(
                        Modifier.weight(1f),
                        strings.raceUncertaintyMetric,
                        uncertaintyText(
                            it.level
                        )
                    )

                    MiniMetric(
                        Modifier.weight(1f),
                        strings.raceLeaderMarginMetric,
                        "%.1f".format(
                            it.topMargin
                        )
                    )

                    MiniMetric(
                        Modifier.weight(1f),
                        strings.raceExpansionMetric,
                        "%.1f".format(
                            it.expansionPressure
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Bottom
    ) {
        Text(
            text = title,
            modifier =
                Modifier.weight(1f),
            color = Ink,
            fontSize = 19.sp,
            fontWeight =
                FontWeight.Black
        )

        Text(
            text = subtitle,
            color = Muted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ExpandableAnalysisHeader(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            CardDefaults
                .outlinedCardBorder(),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier =
                Modifier.padding(15.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text =
                        strings.raceDeepAnalysisTitle,
                    color = Ink,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        strings.raceDeepAnalysisSubtitle,
                    color = Muted,
                    fontSize = 10.sp
                )
            }

            Icon(
                if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription =
                    if (expanded)
                        strings.raceCloseDeepAnalysis
                    else
                        strings.raceOpenDeepAnalysis
            )
        }
    }
}

@Composable
private fun Notice(
    text: String
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp
                ),
        color = PaleGold,
        shape =
            RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(12.dp),
            color = Muted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun raceMeta(
    race: Race
): String {
    val strings = LocalStrings.current

    return listOf(
        race.distance,
        race.surface
    )
        .filter {
            it.isNotBlank()
        }
        .joinToString(" · ")
        .ifBlank {
            strings.raceInfoFallback
        }
}

@Composable
private fun expertSummary(
    horse: Horse
): String {
    val strings = LocalStrings.current

    val e =
        horse.expertConsensus
            ?: return strings.raceExpertSourceMissing

    return buildString {
        append(
            strings.raceExpertSourcesCount(e.sourceCount)
        )

        if (e.favoriteCount > 0) {
            append(
                " · ${strings.raceExpertFavoriteCount(e.favoriteCount)}"
            )
        }

        if (e.bankoCount > 0) {
            append(
                " · ${strings.raceExpertBankoCount(e.bankoCount)}"
            )
        }

        if (e.strongCount > 0) {
            append(
                " · ${strings.raceExpertStrongCount(e.strongCount)}"
            )
        }
    }
}

@Composable
private fun marketSummary(
    horse: Horse
): String {
    val strings = LocalStrings.current

    val m =
        horse.marketMovement
            ?: return strings.noData

    return buildString {
        append(
            "${marketArrow(m.direction)} ${
                marketText(m.direction)
            }"
        )

        m.absoluteDelta?.let {
            append(
                " · AGF ${
                    if (it >= 0)
                        "+"
                    else
                        ""
                }${"%.1f".format(it)}"
            )
        }
    }
}

@Composable
private fun fieldSummary(
    horse: Horse
): String {
    val strings = LocalStrings.current

    return horse.fieldSignal
        ?.score
        ?.let {
            strings.raceFieldCombined("%.1f".format(it))
        }
        ?: strings.noData
}

@Composable
private fun HorseCard(
    horse: Horse,
    rank: Int,
    raceDate: String?,
    city: String,
    raceNumber: Int,
    canViewVideos: Boolean
) {
    val strings = LocalStrings.current

    var expanded by
        remember(
            horse.number
        ) {
            mutableStateOf(
                rank == 1
            )
        }

    var videoExpanded by
        remember(horse.number) { mutableStateOf(false) }

    var videoLoading by
        remember(horse.number) { mutableStateOf(false) }

    var videoFetched by
        remember(horse.number) { mutableStateOf(false) }

    var videos by
        remember(horse.number) {
            mutableStateOf<List<HorseVideo>>(emptyList())
        }

    val context = LocalContext.current

    val videoRepository =
        remember { TwoHorseRepository(context) }

    val scope = rememberCoroutineScope()

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                1.dp,
                if (rank == 1)
                    Green.copy(
                        alpha = 0.35f
                    )
                else
                    Border
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color =
                        if (rank == 1)
                            PaleGreen
                        else
                            Color(0xFFF3F5F4),
                    shape =
                        RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text =
                            horse.number
                                .toString(),
                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 9.dp
                            ),
                        color =
                            if (rank == 1)
                                Green
                            else
                                Ink,
                        fontWeight =
                            FontWeight.Black
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                ) {
                    Text(
                        text = horse.name,
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Black,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    if (
                        horse.jockey
                            .isNotBlank()
                    ) {
                        Text(
                            text =
                                "${horse.jockey}${
                                    horse.weight?.let {
                                        " · ${"%.1f".format(it)} kg"
                                    }.orEmpty()
                                }",
                            color = Muted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {
                    Text(
                        text =
                            horse.score
                                ?.let {
                                    "%.1f".format(it)
                                }
                                ?: "—",
                        color = Green,
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Black
                    )

                    Text(
                        text =
                            "#$rank model",
                        color = Muted,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(
                Modifier.height(11.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                MiniMetric(
                    Modifier.weight(1f),
                    strings.raceGuven,
                    horse.confidence
                        ?.let {
                            "${(it * 100).roundToInt()}%"
                        }
                        ?: "—",
                    accent =
                        rank == 1
                )

                MiniMetric(
                    Modifier.weight(1f),
                    strings.raceAgf,
                    horse.agfPercent
                        ?.let {
                            "%${"%.1f".format(it)}"
                        }
                        ?: "—"
                )

                MiniMetric(
                    Modifier.weight(1f),
                    strings.raceHp,
                    horse.hp
                        ?.toString()
                        ?: "—"
                )
            }

            ExpertConsensusSection(
                horse.expertConsensus
            )

            MarketSection(
                horse.marketMovement
            )

            FieldSection(
                horse.fieldSignal
            )

            if (
                horse.recentForm
                    .isNotBlank()
            ) {
                Spacer(
                    Modifier.height(11.dp)
                )

                Text(
                    text = strings.raceForm,
                    color = Ink,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        horse.recentForm,
                    color = Muted,
                    fontSize = 10.sp
                )
            }

            if (raceDate != null) {
                Spacer(
                    Modifier.height(10.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            videoExpanded =
                                !videoExpanded

                            if (
                                videoExpanded &&
                                canViewVideos &&
                                !videoFetched &&
                                !videoLoading
                            ) {
                                videoLoading = true

                                scope.launch {
                                    videoRepository
                                        .horseVideos(
                                            raceDate =
                                                raceDate,
                                            city = city,
                                            raceNumber = raceNumber,
                                            horseNumber = horse.number
                                        )
                                        .onSuccess {
                                            videos = it
                                        }
                                        .onFailure {
                                            videos = emptyList()
                                        }

                                    videoFetched = true
                                    videoLoading = false
                                }
                            }
                        },
                        contentPadding =
                            PaddingValues(0.dp)
                    ) {
                        Text(
                            text =
                                if (canViewVideos)
                                    strings.raceVideoLabel
                                else
                                    strings.raceVideoLabelLocked,
                            color =
                                if (canViewVideos)
                                    Green
                                else
                                    Muted,
                            fontSize = 10.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    if (videoLoading) {
                        Spacer(Modifier.width(6.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            strokeWidth = 1.5.dp,
                            color = Green
                        )
                    }
                }

                if (videoExpanded && !videoLoading) {
                    if (!canViewVideos) {
                        Text(
                            text =
                                strings.raceVideoLockedBody,
                            color = Muted,
                            fontSize = 9.sp
                        )
                    } else if (videoFetched && videos.isEmpty()) {
                        Text(
                            text =
                                strings.raceVideoNotFound,
                            color = Muted,
                            fontSize = 9.sp
                        )
                    } else {
                        videos.forEachIndexed {
                            index,
                            video ->

                            TextButton(
                                onClick = {
                                    runCatching {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(video.url)
                                            )
                                        )
                                    }
                                },
                                contentPadding =
                                    PaddingValues(vertical = 2.dp)
                            ) {
                                Text(
                                    text =
                                        "${index + 1}. ${video.label.ifBlank { strings.raceVideoFallbackLabel }}",
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Ink
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            TextButton(
                onClick = {
                    expanded =
                        !expanded
                },
                contentPadding =
                    PaddingValues(0.dp)
            ) {
                Text(
                    text =
                        if (expanded)
                            strings.raceCloseModelDetail
                        else
                            strings.raceOpenModelDetail,
                    color = Green,
                    fontSize = 10.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            if (expanded) {
                LearningSection(
                    horse
                )

                if (
                    horse.scoreComponents
                        .isNotEmpty()
                ) {
                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            strings.raceScoreComponents,
                        color = Ink,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Black
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    horse.scoreComponents
                        .forEach {
                            ScoreProgress(
                                title =
                                    componentTitle(
                                        it.key
                                    ),
                                score =
                                    it.score,
                                subtitle =
                                    strings.raceWeightBoth(
                                        "%.1f".format(it.effectiveWeight),
                                        "%.1f".format(it.configuredWeight)
                                    )
                            )

                            Spacer(
                                Modifier.height(8.dp)
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun ExpertConsensusSection(
    value: ExpertConsensusSummary?
) {
    val strings = LocalStrings.current

    if (
        value == null ||
        value.sourceCount <= 0
    ) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        text =
            strings.raceExpertConsensusTitle,
        color = Ink,
        fontSize = 11.sp,
        fontWeight =
            FontWeight.Black
    )

    Spacer(
        Modifier.height(6.dp)
    )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(5.dp)
    ) {
        Row(
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            AnalyticsChip(
                strings.raceExpertSourcesCount(value.sourceCount)
            )

            if (
                value.favoriteCount > 0
            ) {
                AnalyticsChip(
                    "${strings.raceExpertFavoriteCount(value.favoriteCount)} (%${value.favoriteScore.roundToInt()})",
                    strong = true
                )
            }

            if (
                value.bankoCount > 0
            ) {
                AnalyticsChip(
                    "${strings.raceExpertBankoCount(value.bankoCount)} (%${value.bankoScore.roundToInt()})",
                    strong = true
                )
            }
        }

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            if (
                value.strongCount > 0
            ) {
                AnalyticsChip(
                    strings.raceExpertStrongCount(value.strongCount) +
                        " (%${value.strongScore.roundToInt()})"
                )
            }

            if (
                value.starCount > 0
            ) {
                AnalyticsChip(
                    strings.raceStarTag(value.starCount, value.starScore.roundToInt())
                )
            }

            if (
                value.rivalCount > 0
            ) {
                AnalyticsChip(
                    strings.raceRivalTag(value.rivalCount, value.rivalScore.roundToInt())
                )
            }

            if (
                value.surpriseCount > 0
            ) {
                AnalyticsChip(
                    strings.raceSurpriseTag(value.surpriseCount, value.surpriseScore.roundToInt())
                )
            }

            if (
                value.avoidCount > 0
            ) {
                AnalyticsChip(
                    strings.raceAvoidTag(value.avoidCount, value.avoidScore.roundToInt()),
                    danger = true
                )
            }
        }
    }

    if (
        value.summary.isNotBlank()
    ) {
        Spacer(
            Modifier.height(6.dp)
        )

        Text(
            text = value.summary,
            color = Muted,
            fontSize = 11.sp
        )
    }

    value.expertScore?.let {
        Spacer(
            Modifier.height(6.dp)
        )

        ScoreProgress(
            title = strings.raceExpertScoreTitle,
            score = it,
            subtitle =
                value.supportConfidence
                    ?.let { confidence ->
                        strings.raceSupportConfidence(
                            "%.1f".format(confidence * 100)
                        )
                    }
        )
    }
}

@Composable
private fun MarketSection(
    value: MarketMovement?
) {
    val strings = LocalStrings.current

    if (value == null) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Surface(
            color =
                when (
                    value.direction
                ) {
                    "strong-up",
                    "up" ->
                        PaleGreen

                    "strong-down",
                    "down" ->
                        PaleRed

                    else ->
                        Color(0xFFF2F4F3)
                },
            shape =
                RoundedCornerShape(10.dp)
        ) {
            Text(
                text =
                    marketArrow(
                        value.direction
                    ),
                modifier =
                    Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                color =
                    when (
                        value.direction
                    ) {
                        "strong-up",
                        "up" ->
                            Green

                        "strong-down",
                        "down" ->
                            Red

                        else ->
                            Muted
                    },
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Black
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 9.dp)
        ) {
            Text(
                text =
                    strings.raceMarketMoveTitle,
                color = Ink,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.Black
            )

            Text(
                text =
                    buildString {
                        append(
                            marketText(
                                value.direction
                            )
                        )

                        value.firstAgf?.let {
                            append(
                                " · ${strings.raceMarketFirst("%.1f".format(it))}"
                            )
                        }

                        value.latestAgf?.let {
                            append(
                                " ${strings.raceMarketTo("%.1f".format(it))}"
                            )
                        }

                        if (
                            value.sampleSize > 0
                        ) {
                            append(
                                " · ${strings.raceMarketSamples(value.sampleSize)}"
                            )
                        }
                    },
                color = Muted,
                fontSize = 9.sp,
                maxLines = 2
            )
        }
    }

    value.score?.let {
        Spacer(
            Modifier.height(5.dp)
        )

        ScoreProgress(
            title =
                strings.raceMarketScoreTitle,
            score = it
        )
    }
}

@Composable
private fun FieldSection(
    value: FieldSignal?
) {
    val strings = LocalStrings.current

    if (value == null) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        text = strings.raceFieldSignalTitle,
        color = Ink,
        fontSize = 11.sp,
        fontWeight =
            FontWeight.Black
    )

    value.score?.let {
        Spacer(
            Modifier.height(5.dp)
        )

        ScoreProgress(
            title =
                strings.raceFieldCombinedTitle,
            score = it,
            subtitle =
                buildString {
                    value.tjkScore?.let {
                        append(
                            strings.raceFieldTjk("%.1f".format(it))
                        )
                    }

                    value.expertScore?.let {
                        if (isNotEmpty()) {
                            append(" · ")
                        }

                        append(
                            strings.raceFieldExpert("%.1f".format(it))
                        )
                    }

                    if (
                        value.tjkSampleSize > 0
                    ) {
                        append(
                            " · ${strings.raceFieldSamples(value.tjkSampleSize)}"
                        )
                    }
                }
        )
    }
}

@Composable
private fun LearningSection(
    horse: Horse
) {
    val strings = LocalStrings.current

    val base =
        horse.baseScore

    val adjustment =
        horse.learningAdjustment

    if (
        base == null &&
        adjustment == null
    ) {
        return
    }

    Spacer(
        Modifier.height(8.dp)
    )

    Surface(
        color = PaleGreen,
        shape =
            RoundedCornerShape(12.dp)
    ) {
        Column(
            Modifier.padding(10.dp)
        ) {
            Text(
                text = strings.raceLearningEffectTitle,
                color = Green,
                fontSize = 10.sp,
                fontWeight =
                    FontWeight.Black
            )

            Text(
                text =
                    buildString {
                        base?.let {
                            append(
                                strings.raceLearningBase("%.1f".format(it))
                            )
                        }

                        horse.score?.let {
                            if (isNotEmpty()) {
                                append(" → ")
                            }

                            append(
                                strings.raceLearningFinal("%.1f".format(it))
                            )
                        }

                        adjustment?.let {
                            append(
                                " (${
                                    if (it >= 0)
                                        "+"
                                    else
                                        ""
                                }${"%.1f".format(it)})"
                            )
                        }
                    },
                color = Ink,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeepAnalysisCard(
    horse: Horse
) {
    val strings = LocalStrings.current

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            CardDefaults
                .outlinedCardBorder(),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(15.dp)
        ) {
            Text(
                text =
                    strings.raceDeepViewTitle,
                color = Ink,
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Black
            )

            Text(
                text =
                    "#${horse.number} ${horse.name}",
                color = Muted,
                fontSize = 10.sp
            )

            Spacer(
                Modifier.height(11.dp)
            )

            horse.scoreComponents
                .forEach {
                    ScoreProgress(
                        title =
                            componentTitle(
                                it.key
                            ),
                        score =
                            it.score,
                        subtitle =
                            strings.raceWeightEffectiveOnly(
                                "%.1f".format(it.effectiveWeight)
                            )
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )
                }
        }
    }
}
