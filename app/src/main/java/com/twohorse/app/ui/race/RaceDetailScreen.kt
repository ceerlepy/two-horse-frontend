@file:OptIn(ExperimentalLayoutApi::class)

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.Config
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.*
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.components.*
import com.twohorse.app.ui.home.raceTimeMillis
import com.twohorse.app.ui.theme.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private sealed interface RaceDetailError {
    data object NotFoundInProgram : RaceDetailError
    data object RefreshFailed : RaceDetailError
}

@Composable
fun RaceDetailScreen(
    race: Race,
    currentUser: MembershipUser?,
    onBack: () -> Unit,
    onOpenCoupons: (String) -> Unit,
    onUpgradeClick: () -> Unit
) {
    BackHandler(onBack = onBack)

    val strings = LocalStrings.current

    // Videos are links to TJK's own public pages, open to every plan.
    val canViewVideos = true

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
            mutableStateOf<RaceDetailError?>(null)
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
                        RaceDetailError.NotFoundInProgram
                }
            }
            .onFailure {
                error =
                    RaceDetailError.RefreshFailed
            }

        refreshing = false
    }

    AutoRefreshEffect(
        nextStartMillis =
            raceTimeMillis(currentRace)
    ) {
        if (!refreshing) {
            refreshKey++
        }
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

    var askOpen by
        rememberSaveable(
            race.city,
            race.number
        ) {
            mutableStateOf(false)
        }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {
    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                // Room for the round "AI'ya sor" button.
                bottom = 96.dp
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

        error?.let { raceError ->
            item {
                Notice(
                    text =
                        when (raceError) {
                            RaceDetailError.NotFoundInProgram ->
                                strings.raceNotFoundInProgram

                            RaceDetailError.RefreshFailed ->
                                strings.raceRefreshFailed
                        }
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
                        surprise = surprise,
                        raceExpertSources = raceExpertTotal(currentRace)
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

            val raceExpertSources =
                raceExpertTotal(currentRace)

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
                        raceExpertSources = raceExpertSources,
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
                RaceTrainingSection(
                    raceDate = currentRace.raceDate,
                    city = currentRace.city,
                    raceNumber = currentRace.number,
                    repository = repository
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
                RaceFormSection(
                    raceDate = currentRace.raceDate,
                    city = currentRace.city,
                    raceNumber = currentRace.number,
                    repository = repository
                )
            }
        }

        if (Config.SHOW_MODEL_DIAGNOSTICS) item {
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
            Config.SHOW_MODEL_DIAGNOSTICS &&
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

        AskAiFab(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(
                        end = 18.dp,
                        bottom = 18.dp
                    ),
            onClick = {
                askOpen = true
            }
        )
    }

    if (askOpen) {
        AskAiSheet(
            race = currentRace,
            isPremium = currentUser?.tier == "premium",
            repository = repository,
            onUpgradeClick = {
                askOpen = false
                onUpgradeClick()
            },
            onDismiss = {
                askOpen = false
            }
        )
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
    surprise: Horse?,
    raceExpertSources: Int
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

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
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

                        InfoButton(
                            title = strings.infoModelScoreTitle,
                            body = strings.infoModelScore,
                            tint =
                                Color.White.copy(
                                    alpha = 0.72f
                                )
                        )
                    }
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

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {
                DarkTile(
                    Modifier.weight(1f),
                    strings.raceAgf,
                    favorite.agfPercent
                        ?.let {
                            "%${"%.1f".format(it)}"
                        }
                        ?: "—"
                )

                DarkTile(
                    Modifier.weight(1f),
                    strings.raceHp,
                    favorite.hp
                        ?.toString()
                        ?: "—"
                )

                DarkTile(
                    Modifier.weight(1f),
                    strings.raceGuven,
                    favorite.confidence
                        ?.let {
                            "%${(it * 100).roundToInt()}"
                        }
                        ?: "—"
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Surface(
                color =
                    Color.White.copy(
                        alpha = 0.06f
                    ),
                shape =
                    RoundedCornerShape(14.dp)
            ) {
                Column(
                    Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
                ) {
                    DarkMetric(
                        strings.raceExpertSupport,
                        expertSummary(
                            favorite,
                            raceExpertSources
                        )
                    )

                    DarkRule()

                    DarkMetric(
                        strings.raceMarket,
                        marketSummary(
                            favorite
                        )
                    )

                    DarkRule()

                    DarkMetric(
                        strings.raceForm,
                        recentFormText(
                            favorite.recentForm,
                            strings.raceFormTenPlus
                        )
                            .ifBlank {
                                strings.noData
                            }
                    )

                    if (Config.SHOW_MODEL_DIAGNOSTICS) {
                        favorite.learningAdjustment
                            ?.let {
                                DarkRule()

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
                    }
                }
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
                        color = Lavender,
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
                        color = Lavender,
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
                .padding(vertical = 8.dp),
        verticalAlignment =
            Alignment.Top
    ) {
        Text(
            text = label,
            modifier =
                Modifier.width(92.dp),
            color =
                Color.White.copy(
                    alpha = 0.58f
                ),
            fontSize = 11.sp
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(1f),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.SemiBold,
            textAlign =
                TextAlign.End
        )
    }
}

@Composable
private fun DarkRule() {
    HorizontalDivider(
        color =
            Color.White.copy(
                alpha = 0.08f
            )
    )
}

@Composable
private fun DarkTile(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color =
            Color.White.copy(
                alpha = 0.06f
            ),
        shape =
            RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(
                    vertical = 9.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color =
                    Color.White.copy(
                        alpha = 0.58f
                    ),
                fontSize = 9.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight =
                    FontWeight.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RaceRiskCard(
    race: Race
) {
    val strings = LocalStrings.current

    ToneCard {
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

            val uncertainty =
                race.uncertainty

            val strategy =
                race.couponStrategy

            if (
                uncertainty == null &&
                strategy == null
            ) {
                return@Column
            }

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {
                LevelMetric(
                    Modifier.weight(1f),
                    label = strings.raceUncertaintyMetric,
                    level =
                        when (uncertainty?.level?.lowercase()) {
                            "low" -> 1
                            "medium" -> 2
                            "high" -> 3
                            "very-high" -> 4
                            else -> 0
                        },
                    levelText =
                        uncertainty
                            ?.let {
                                uncertaintyText(it.level)
                            }
                            ?: "—",
                    info = strings.infoUncertainty
                )

                InsetMetric(
                    Modifier.weight(1f),
                    strings.raceLeaderMarginMetric,
                    uncertainty
                        ?.let {
                            strings.raceLeaderMarginValue(
                                "%.1f".format(it.topMargin)
                            )
                        }
                        ?: "—",
                    info = strings.infoLeaderMargin
                )

                InsetMetric(
                    Modifier.weight(1f),
                    strings.raceExpansionMetric,
                    strategy
                        ?.let {
                            when (it.mode.lowercase()) {
                                "single" -> strings.strategyShortSingle
                                "compact", "narrow" -> strings.strategyShortCompact
                                "spread", "wide", "broad" -> strings.strategyShortSpread
                                else -> strings.strategyShortBalanced
                            }
                        }
                        ?: "—",
                    info = strings.infoCouponAdvice
                )
            }

            val summary =
                listOfNotNull(
                    uncertainty?.let { uncertaintyExplanation(it) },
                    strategy?.let {
                        when {
                            it.horseNumbers.size == 1 -> strings.strategyOneCandidate
                            it.horseNumbers.isNotEmpty() -> strings.strategyCandidates(it.horseNumbers.size)
                            else -> null
                        }
                    }
                ).joinToString(" · ")

            if (summary.isNotBlank()) {
                Spacer(
                    Modifier.height(9.dp)
                )

                Text(
                    text = summary,
                    color = Ink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                text = strings.raceRiskHint,
                color = Muted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
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

    ToneCard {
        ExpandableToneHeader(
            title = strings.raceDeepAnalysisTitle,
            subtitle = strings.raceDeepAnalysisSubtitle,
            expanded = expanded,
            openDescription = strings.raceOpenDeepAnalysis,
            closeDescription = strings.raceCloseDeepAnalysis,
            onClick = onClick
        )
    }
}

private sealed interface TrainingLoadState {
    data object Idle : TrainingLoadState
    data object Loading : TrainingLoadState
    data class Loaded(val training: RaceTraining) : TrainingLoadState
    data object Failed : TrainingLoadState
}

/* "2026-10-02" -> "02.10" */
private fun shortTrainingDate(value: String?): String? {
    val parts = value?.split("-") ?: return null
    return if (parts.size == 3) "${parts[2]}.${parts[1]}" else value
}

@Composable
private fun RaceTrainingSection(
    raceDate: String?,
    city: String,
    raceNumber: Int,
    repository: TwoHorseRepository
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by
        remember(city, raceNumber) {
            mutableStateOf(false)
        }

    var state by
        remember(city, raceNumber) {
            mutableStateOf<TrainingLoadState>(TrainingLoadState.Idle)
        }

    fun load() {
        state = TrainingLoadState.Loading
        scope.launch {
            state =
                repository
                    .raceTraining(
                        raceDate = raceDate ?: "",
                        city = city,
                        raceNumber = raceNumber
                    )
                    .fold(
                        onSuccess = {
                            if (it.status == "unavailable")
                                TrainingLoadState.Failed
                            else
                                TrainingLoadState.Loaded(it)
                        },
                        onFailure = {
                            TrainingLoadState.Failed
                        }
                    )
        }
    }

    ToneCard {
        ExpandableToneHeader(
            title = strings.raceTrainingTitle,
            subtitle = strings.raceTrainingSubtitle,
            expanded = expanded,
            openDescription = strings.raceTrainingOpen,
            closeDescription = strings.raceTrainingClose,
            onClick = {
                expanded = !expanded
                if (
                    expanded &&
                    state == TrainingLoadState.Idle
                ) {
                    load()
                }
            }
        )

        if (expanded) {
            Column(
                modifier =
                    Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                when (val current = state) {
                    TrainingLoadState.Idle,
                    TrainingLoadState.Loading ->
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Green
                        )

                    TrainingLoadState.Failed -> {
                        Text(
                            text = strings.raceTrainingUnavailable,
                            color = Muted,
                            fontSize = 12.sp
                        )
                        TextButton(
                            onClick = { load() }
                        ) {
                            Text(strings.raceTrainingRetry)
                        }
                    }

                    is TrainingLoadState.Loaded -> {
                        if (current.training.horses.isEmpty()) {
                            Text(
                                text = strings.raceTrainingEmpty,
                                color = Muted,
                                fontSize = 12.sp
                            )
                        }

                        current.training.horses.forEach { horse ->
                            TrainingRow(
                                horse = horse,
                                onOpenVideo = { url ->
                                    runCatching {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(url)
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingRow(
    horse: HorseTraining,
    onOpenVideo: (String) -> Unit
) {
    val strings = LocalStrings.current

    InsetBox {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberBadge(
                number = horse.horseNumber,
                size = 28.dp
            )

            Text(
                text = horse.horseName,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 9.dp),
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            shortTrainingDate(horse.trainingDate)?.let {
                FactTag(text = it)
            }
        }

        val meta =
            listOfNotNull(
                listOfNotNull(horse.track, horse.trackCondition)
                    .joinToString(" ")
                    .takeIf { it.isNotBlank() },
                horse.trainingType,
                horse.hippodrome
            ).joinToString(" · ")

        if (meta.isNotBlank()) {
            Text(
                text = meta,
                color = Muted,
                fontSize = 11.sp
            )
        }

        if (horse.splits.isNotEmpty()) {
            ToneDivider()

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                horse.splits.forEach {
                    FactTag(
                        text = "${it.distanceMeters}m  ${it.time}",
                        strong = true
                    )
                }
            }
        }

        if (horse.jockey != null || horse.videoUrl != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        horse.jockey
                            ?.let { strings.raceTrainingJockey(it) }
                            .orEmpty(),
                    modifier = Modifier.weight(1f),
                    color = Muted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                horse.videoUrl?.let { url ->
                    TextButton(
                        onClick = { onOpenVideo(url) },
                        contentPadding =
                            PaddingValues(
                                horizontal = 8.dp,
                                vertical = 0.dp
                            )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Green
                        )

                        Text(
                            text = strings.raceTrainingVideo,
                            color = Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private sealed interface FormLoadState {
    data object Idle : FormLoadState
    data object Loading : FormLoadState
    data class Loaded(val form: RaceForm) : FormLoadState
    data object Failed : FormLoadState
}

/* 59.5 -> "59,5", 62.0 -> "62" */
private fun compactNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString()
    else value.toString().replace('.', ',')

@Composable
private fun RaceFormSection(
    raceDate: String?,
    city: String,
    raceNumber: Int,
    repository: TwoHorseRepository
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    var expanded by
        remember(city, raceNumber) {
            mutableStateOf(false)
        }

    var state by
        remember(city, raceNumber) {
            mutableStateOf<FormLoadState>(FormLoadState.Idle)
        }

    fun load() {
        state = FormLoadState.Loading
        scope.launch {
            state =
                repository
                    .raceForm(
                        raceDate = raceDate ?: "",
                        city = city,
                        raceNumber = raceNumber
                    )
                    .fold(
                        onSuccess = { FormLoadState.Loaded(it) },
                        onFailure = { FormLoadState.Failed }
                    )
        }
    }

    ToneCard {
        ExpandableToneHeader(
            title = strings.raceFormTitle,
            subtitle = strings.raceFormSubtitle,
            expanded = expanded,
            openDescription = strings.raceFormOpen,
            closeDescription = strings.raceFormClose,
            onClick = {
                expanded = !expanded
                if (
                    expanded &&
                    state == FormLoadState.Idle
                ) {
                    load()
                }
            }
        )

        if (expanded) {
            Column(
                modifier =
                    Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                when (val current = state) {
                    FormLoadState.Idle,
                    FormLoadState.Loading ->
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Green
                        )

                    FormLoadState.Failed -> {
                        Text(
                            text = strings.raceTrainingUnavailable,
                            color = Muted,
                            fontSize = 12.sp
                        )
                        TextButton(
                            onClick = { load() }
                        ) {
                            Text(strings.raceTrainingRetry)
                        }
                    }

                    is FormLoadState.Loaded -> {
                        if (current.form.horses.all { it.runs.isEmpty() }) {
                            Text(
                                text = strings.raceFormEmpty,
                                color = Muted,
                                fontSize = 12.sp
                            )
                        } else {
                            current.form.horses.forEach { horse ->
                                HorseFormRow(horse = horse)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HorseFormRow(
    horse: HorseForm
) {
    val strings = LocalStrings.current

    InsetBox {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberBadge(
                number = horse.horseNumber,
                size = 28.dp
            )

            Text(
                text = horse.horseName,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 9.dp),
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Finishing positions at a glance, in the same order as the list below.
            if (horse.runs.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    horse.runs.take(4).forEach {
                        PositionDot(position = it.finishPosition)
                    }
                }
            }
        }

        if (horse.runs.isEmpty()) {
            Text(
                text = strings.raceFormNoRuns,
                color = Muted,
                fontSize = 11.sp
            )
        }

        horse.runs.forEach { run ->
            ToneDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PositionDot(position = run.finishPosition)

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 9.dp)
                ) {
                    Text(
                        text =
                            listOfNotNull(
                                run.city,
                                run.distanceMeters?.let { "${it}m" },
                                run.track
                            ).joinToString(" · "),
                        color = Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val details =
                        listOfNotNull(
                            run.finishTime,
                            run.jockey,
                            run.weight?.let { "${compactNumber(it)} kg" },
                            run.odds?.let { strings.raceFormOdds(compactNumber(it)) }
                        ).joinToString(" · ")

                    if (details.isNotBlank()) {
                        Text(
                            text = details,
                            color = Muted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                shortTrainingDate(run.raceDate)?.let {
                    FactTag(text = it)
                }
            }
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

/* Experts with any pick in this race; older servers lack the field,
 * so fall back to the widest per-horse count. */
private fun raceExpertTotal(race: Race): Int =
    race.expertSourceCount
        ?: race.horses.maxOfOrNull { it.expertConsensus?.sourceCount ?: 0 }
        ?: 0

@Composable
private fun expertSummary(
    horse: Horse,
    raceExpertSources: Int
): String {
    val strings = LocalStrings.current

    val e =
        horse.expertConsensus
            ?: return strings.raceExpertSourceMissing

    val total =
        maxOf(raceExpertSources, e.sourceCount)

    val primary =
        e.primaryCount
            ?: maxOf(e.bankoCount, e.favoriteCount, e.starCount)

    val strongOnly =
        e.strongOnlyCount ?: e.strongCount

    val secondary =
        e.secondaryCount ?: e.rivalCount

    return buildString {
        append(strings.raceExpertHeroFirst(primary, total))

        if (strongOnly > 0) {
            append(" · ${strings.raceExpertHeroStrong(strongOnly)}")
        }

        if (secondary > 0) {
            append(" · ${strings.raceExpertHeroSecond(secondary)}")
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

    val first = m.firstAgf
    val latest = m.latestAgf
    val word = marketText(m.direction)

    // Plain "AGF %33,0 → %34,0 (Yatay)" instead of arrows and deltas.
    return if (first != null && latest != null)
        "%${"%.1f".format(first)} → %${"%.1f".format(latest)} ($word)"
    else
        word
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
    raceExpertSources: Int,
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

    ToneCard(
        borderColor =
            if (rank == 1)
                Green.copy(
                    alpha = 0.35f
                )
            else
                CardToneBorder
    ) {
        Column(
            modifier =
                Modifier.padding(13.dp),
            verticalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                NumberBadge(
                    number = horse.number,
                    highlighted = rank == 1
                )

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
                                        " · ${compactNumber(it)} kg"
                                    }.orEmpty()
                                }",
                            color = Muted,
                            fontSize = 11.sp,
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
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Black
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                                strings.raceRankLabel(rank),
                            color = Muted,
                            fontSize = 9.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        InfoButton(
                            title = strings.infoModelScoreTitle,
                            body = strings.infoModelScore
                        )
                    }
                }
            }

            ToneDivider()

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                InsetMetric(
                    Modifier.weight(1f),
                    strings.raceGuven,
                    horse.confidence
                        ?.let {
                            "%${(it * 100).roundToInt()}"
                        }
                        ?: "—",
                    accent =
                        rank == 1,
                    info = strings.infoGuven
                )

                InsetMetric(
                    Modifier.weight(1f),
                    strings.raceAgf,
                    horse.agfPercent
                        ?.let {
                            "%${"%.1f".format(it)}"
                        }
                        ?: "—",
                    info = strings.infoAgf
                )

                InsetMetric(
                    Modifier.weight(1f),
                    strings.raceHp,
                    horse.hp
                        ?.toString()
                        ?: "—",
                    info = strings.infoHp
                )
            }

            ValueModelSection(
                horse.valueModel,
                horse.agfPercent,
                isModelFavourite = rank == 1
            )

            ExpertConsensusSection(
                horse.expertConsensus,
                raceExpertSources
            )

            MarketSection(
                horse.marketMovement
            )

            FieldSection(
                horse.fieldSignal
            )

            InsetBox(
                title = strings.raceForm,
                info = strings.infoForm
            ) {
                if (parseRecentForm(horse.recentForm).isEmpty()) {
                    Text(
                        text = strings.noData,
                        color = Muted,
                        fontSize = 11.sp
                    )
                } else {
                    RecentFormDots(
                        raw = horse.recentForm,
                        tenPlusLabel = strings.raceFormTenPlus
                    )

                    Text(
                        text = strings.raceFormHint,
                        color = Muted,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }

            if (raceDate != null) {
                ToneDivider()

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
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
                            }
                            .padding(
                                horizontal = 4.dp,
                                vertical = 8.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint =
                            if (canViewVideos)
                                Green
                            else
                                Muted
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text =
                            if (canViewVideos)
                                strings.raceVideoLabel
                            else
                                strings.raceVideoLabelLocked,
                        modifier = Modifier.weight(1f),
                        color =
                            if (canViewVideos)
                                Green
                            else
                                Muted,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    if (videoLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 1.5.dp,
                            color = Green
                        )
                    } else {
                        Icon(
                            if (videoExpanded)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription =
                                if (videoExpanded)
                                    strings.raceCloseVideos
                                else
                                    strings.raceOpenVideos,
                            tint = Muted
                        )
                    }
                }

                if (videoExpanded && !videoLoading) {
                    if (!canViewVideos) {
                        Text(
                            text =
                                strings.raceVideoLockedBody,
                            color = Muted,
                            fontSize = 11.sp
                        )
                    } else if (videoFetched && videos.isEmpty()) {
                        Text(
                            text =
                                strings.raceVideoNotFound,
                            color = Muted,
                            fontSize = 11.sp
                        )
                    } else if (videos.isNotEmpty()) {
                        InsetBox(
                            padding = 4.dp
                        ) {
                            videos.forEachIndexed {
                                index,
                                video ->

                                if (index > 0) {
                                    ToneDivider()
                                }

                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                runCatching {
                                                    context.startActivity(
                                                        Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(video.url)
                                                        )
                                                    )
                                                }
                                            }
                                            .padding(
                                                horizontal = 8.dp,
                                                vertical = 8.dp
                                            ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        modifier = Modifier.width(20.dp),
                                        color = Muted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )

                                    Text(
                                        text =
                                            video.label.ifBlank { strings.raceVideoFallbackLabel },
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Ink
                                    )

                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Green
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (Config.SHOW_MODEL_DIAGNOSTICS) {
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
                        Text(
                            text =
                                strings.raceScoreComponents,
                            color = Ink,
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Black
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
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpertConsensusSection(
    value: ExpertConsensusSummary?,
    raceExpertSources: Int
) {
    val strings = LocalStrings.current

    InsetBox(
        title = strings.raceExpertConsensusTitle,
        info = strings.infoExpert,
        trailing = {
            if (value != null && value.sourceCount > 0) {
                Text(
                    text = strings.raceExpertSourcesCount(maxOf(raceExpertSources, value.sourceCount)),
                    color = Ink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        if (
            value == null ||
            value.sourceCount <= 0
        ) {
            Text(
                text = strings.raceExpertNone,
                color = Muted,
                fontSize = 11.sp
            )

            ScoreProgress(
                title = strings.raceExpertScoreTitle,
                score = null
            )

            return@InsetBox
        }

        /*
         * Each expert counted once, by the strongest role they gave the
         * horse, over every expert covering the race: "İlk tercih 2/4".
         * Older servers without the role counts fall back to the largest
         * single main-pick label.
         */
        val total =
            maxOf(raceExpertSources, value.sourceCount, 1)

        val primary =
            value.primaryCount
                ?: maxOf(value.bankoCount, value.favoriteCount, value.starCount)

        val strongOnly =
            value.strongOnlyCount ?: value.strongCount

        val secondary =
            value.secondaryCount ?: value.rivalCount

        val surpriseOnly =
            value.surpriseOnlyCount ?: value.surpriseCount

        val firstChoiceChips =
            listOf(
                strings.expertChipFavorite to value.favoriteCount,
                strings.expertChipBanko to value.bankoCount,
                strings.expertChipStar to value.starCount
            )
                .filter { it.second > 0 }

        // The four roles always show, so "0/4" says as much as "3/4";
        // negative only when an expert actually said it.
        CategoryBar(
            label = strings.raceExpertFirstChoice,
            detail = strings.raceCategoryCountOf(primary, total),
            percent = primary * 100.0 / total,
            color = Green,
            info = strings.expertFirstChoiceInfo,
            chips = firstChoiceChips
        )

        CategoryBar(
            label = strings.expertRowStrong,
            detail = strings.raceCategoryCountOf(strongOnly, total),
            percent = strongOnly * 100.0 / total,
            color = Green.copy(alpha = 0.7f)
        )

        CategoryBar(
            label = strings.raceExpertSecondChoice,
            detail = strings.raceCategoryCountOf(secondary, total),
            percent = secondary * 100.0 / total,
            color = Gold
        )

        CategoryBar(
            label = strings.expertRowSurprise,
            detail = strings.raceCategoryCountOf(surpriseOnly, total),
            percent = surpriseOnly * 100.0 / total,
            color = Lavender
        )

        if (value.avoidCount > 0) {
            CategoryBar(
                label = strings.expertRowAvoid,
                detail = strings.raceCategoryCountOf(value.avoidCount, total),
                percent = value.avoidCount * 100.0 / total,
                color = Red
            )
        }

        ToneDivider()

        // Same damping the model applies: few experts pull the score
        // toward 50, so one or two picks never read as near-certain.
        ScoreProgress(
            title = strings.raceExpertScoreTitle,
            score =
                value.expertScore?.let { raw ->
                    50 + (raw - 50) * (value.supportConfidence ?: 1.0)
                },
            subtitle = strings.raceExpertScoreHint
        )
    }
}

@Composable
private fun MarketSection(
    value: MarketMovement?
) {
    val strings = LocalStrings.current

    val score =
        value?.score

    InsetBox(
        title = strings.raceMarketMoveTitle,
        info = strings.infoMarket
    ) {
        if (value != null && score != null) {
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
                                Color.White.copy(
                                    alpha = 0.7f
                                )
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
                                vertical = 5.dp
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
                            .padding(start = 10.dp)
                ) {
                    Text(
                        text =
                            marketText(
                                value.direction
                            ),
                        color = Ink,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    val detail =
                        buildString {
                            value.firstAgf?.let {
                                append(
                                    strings.raceMarketFirst("%.1f".format(it))
                                )
                            }

                            value.latestAgf?.let {
                                if (isNotEmpty()) {
                                    append(" ")
                                }

                                append(
                                    strings.raceMarketTo("%.1f".format(it))
                                )
                            }
                        }

                    if (detail.isNotBlank()) {
                        Text(
                            text = detail,
                            color = Muted,
                            fontSize = 10.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        ScoreProgress(
            title =
                strings.raceMarketScoreTitle,
            score = score,
            subtitle =
                if (score != null)
                    strings.raceMarketScoreHint
                else
                    strings.raceMarketNoDataHint
        )
    }
}

@Composable
private fun ValueModelSection(
    value: ValueModelOpinion?,
    agfPercent: Double?,
    isModelFavourite: Boolean
) {
    val strings = LocalStrings.current

    if (value == null) {
        return
    }

    val pct = { p: Double -> "%.1f".format(p * 100) }

    InsetBox(
        title = strings.raceValueModelTitle,
        info = strings.infoValueModel,
        trailing = {
            /*
             * Two chips do not fit beside the title on a narrow phone,
             * so they wrap rather than ellipsize.
             */
            FlowRow(
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                when (value.label) {
                    "underrated" ->
                        AnalyticsChip(
                            strings.raceValueUnderrated,
                            accent = true
                        )

                    "overrated" -> {
                        AnalyticsChip(
                            strings.raceValueOverrated,
                            danger = true
                        )

                        /*
                         * "Too heavily backed" and "still the one most
                         * likely to win" are both true of the same
                         * horse, and seeing only the first next to our
                         * own top pick reads as the app contradicting
                         * itself.
                         */
                        if (isModelFavourite) {
                            AnalyticsChip(
                                strings.raceValueStillFavourite,
                                warn = true
                            )
                        }
                    }
                }
            }
        }
    ) {
        Text(
            text =
                // Same AGF figure as the tile above, so the numbers match.
                (agfPercent?.div(100) ?: value.agfProbability)
                    ?.let {
                        strings.raceValueVsAgf(pct(it), pct(value.probability))
                    }
                    ?: value.ganyanProbability
                        ?.let {
                            strings.raceValueVsGanyan(pct(it), pct(value.probability))
                        }
                    ?: "${strings.raceValueModelTitle}: %${pct(value.probability)}",
            color = Ink,
            fontSize = 12.sp,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = strings.raceValueNote,
            color = Muted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun FieldSection(
    value: FieldSignal?
) {
    val strings = LocalStrings.current

    /*
     * The model only uses the field score when at least half the race
     * has it (the server nulls `score` otherwise), but the horse's own
     * TJK surface figure is still worth showing on screen.
     */
    val score =
        value?.score
            ?: value?.tjkScore

    InsetBox(
        title = strings.raceFieldSignalTitle,
        info = strings.infoField
    ) {
        ScoreProgress(
            title =
                strings.raceFieldCombinedTitle,
            score = score,
            subtitle =
                listOfNotNull(
                    strings.raceFieldHint,
                    value
                        ?.tjkSampleSize
                        ?.takeIf { score != null && it > 0 }
                        ?.let { strings.raceFieldSamples(it) }
                ).joinToString(" · ")
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


/*
 * The consensus sentence in the app's language, built from the same
 * counts and rules as the backend's Turkish summary (experts/aggregator.ts
 * buildSummary), so English users don't see Turkish server text.
 */
@Composable
private fun consensusSummary(
    value: ExpertConsensusSummary
): String {
    val strings = LocalStrings.current

    if (value.sourceCount <= 0) {
        return value.summary
    }

    val positive =
        listOf(
            Triple(value.bankoCount, value.bankoScore, strings.raceCategoryBanko),
            Triple(value.favoriteCount, value.favoriteScore, strings.raceCategoryFavorite),
            Triple(value.strongCount, value.strongScore, strings.raceCategoryStrong),
            Triple(value.starCount, value.starScore, strings.raceCategoryStar),
            Triple(value.surpriseCount, value.surpriseScore, strings.raceCategorySurprise),
            Triple(value.rivalCount, value.rivalScore, strings.raceCategoryRival)
        )
            .filter { it.first > 0 }
            .sortedByDescending { it.first }
            .firstOrNull()

    return when {
        value.avoidCount > 0 && value.avoidCount >= (positive?.first ?: 0) ->
            strings.raceConsensusAvoid(
                value.sourceCount,
                value.avoidCount,
                value.avoidScore.roundToInt()
            )

        positive != null && value.avoidCount > 0 ->
            strings.raceConsensusPositiveWithAvoid(
                value.sourceCount,
                positive.first,
                positive.third,
                positive.second.roundToInt(),
                value.avoidCount
            )

        positive != null ->
            strings.raceConsensusPositive(
                value.sourceCount,
                positive.first,
                positive.third,
                positive.second.roundToInt()
            )

        else ->
            strings.raceConsensusNoDirection(value.sourceCount)
    }
}
